package dev.despg.examples.purchase_storage_sale;

import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import dev.despg.examples.util.Database;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Purchase extends SimulationObject
{
    private static final Logger LOGGER = Logger.getLogger(Purchase.class.getName());
    private final Database database;
    private final Travelcosts travelcosts;
    private final PurchaseRepository purchaseRepository;
    private double purchaseQuantity;

    public Purchase(Database database, Travelcosts travelcosts)
    {
        this.database = database;
        this.travelcosts = travelcosts;
        this.purchaseRepository = new PurchaseRepository(database.getSession());
        SimulationObjects.getInstance().add(this);
    }

    public final boolean simulate(long timeStep)
    {
        performPurchase(purchaseQuantity);
        return false;
    }

    private void updateFillLevel(Session session, int storageID, BigDecimal fillLevel)
    {
        try
        {
            StorageEntity storage = session.get(StorageEntity.class, storageID);
            storage.setFillLevel(fillLevel);
            session.update(storage);
            System.out.println("Fill level for storage " + storageID + " has been updated.");
        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Error updating the fill level: ", e);
        }
    }
    /**
     *
     * @param purchaseQuantity
     */
    public void performPurchase(double purchaseQuantity)
    {
        Properties purchaseProbs = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties"))
        {
            if (is == null)
            {
                throw new IOException("config.properties not found in the classpath");
            }
            purchaseProbs.load(is);

            double minPurchaseQuantity = Double.parseDouble(purchaseProbs.getProperty("purchaseQuantityMin"));
            double maxPurchaseQuantity = Double.parseDouble(purchaseProbs.getProperty("purchaseQuantityMax"));
            double criticalCapacity = Double.parseDouble(purchaseProbs.getProperty("criticalCapacity"));

            purchaseQuantity = generateRandomPurchaseQuantity(minPurchaseQuantity, maxPurchaseQuantity);

            try (Session session = database.getSession())
            {
                Transaction transaction = session.beginTransaction();

                // Use repository for database operations
                List<StorageEntity> storages = purchaseRepository.getAllStorages();
                int selectedStorageID = -1;
                int selectedSupplierID = -1;
                BigDecimal selectedCapacity = BigDecimal.ZERO;
                BigDecimal selectedPricePerTon = BigDecimal.valueOf(Double.MAX_VALUE);
                double minDistance = Double.MAX_VALUE;

                for (StorageEntity storage : storages)
                {
                    BigDecimal filllevel = storage.getFillLevel();
                    BigDecimal capacity = storage.getCapacity();

                    if (filllevel.doubleValue() < criticalCapacity * capacity.doubleValue())
                    {
                        int supplierID = findCheapestSupplier();

                        BigDecimal pricePerTon = getPricePerTon(supplierID);
                        if (pricePerTon.doubleValue() < selectedPricePerTon.doubleValue())
                        {
                            double distance = travelcosts.getDistanceToSupplier(storage.getStorageID(), supplierID);
                            if (distance < minDistance)
                            {
                                BigDecimal refill = capacity.subtract(filllevel);
                                double quantity = Math.min(refill.doubleValue(), purchaseQuantity);
                                BigDecimal filllevelnew = filllevel.add(BigDecimal.valueOf(quantity / capacity.doubleValue()));
                                if (filllevelnew.doubleValue() <= 1.0)
                                {
                                    selectedStorageID = storage.getStorageID();
                                    selectedSupplierID = supplierID;
                                    selectedCapacity = capacity;
                                    selectedPricePerTon = pricePerTon;
                                    minDistance = distance;
                                }
                            }
                        }
                    }
                }

                if (selectedStorageID != -1)
                {
                    BigDecimal refill = selectedCapacity.subtract(purchaseRepository.getStorageById(selectedStorageID).getFillLevel());
                    double quantity = Math.min(refill.doubleValue(), purchaseQuantity);
                    BigDecimal gravelcosts = BigDecimal.valueOf(quantity * selectedPricePerTon.doubleValue());
                    double suppliercosts = quantity / 40 * travelcosts.calculateTravelcosts(selectedStorageID, selectedSupplierID);
                    BigDecimal totalcosts = gravelcosts.add(BigDecimal.valueOf(suppliercosts));

                    insertPurchase(session, selectedSupplierID, selectedStorageID, quantity,
                            selectedPricePerTon, totalcosts, gravelcosts, BigDecimal.valueOf(suppliercosts));
                    BigDecimal fillLevelNew = purchaseRepository.getStorageById(selectedStorageID).getFillLevel().add(
                    		BigDecimal.valueOf(quantity / selectedCapacity.doubleValue()));
                    updateFillLevel(session, selectedStorageID, fillLevelNew);

                    LOGGER.log(Level.INFO, "Purchase performed in storage " + selectedStorageID + ".");
                } else
                {
                    System.out.println("No storage found for the purchase.");
                }

                transaction.commit();

                LOGGER.log(Level.INFO, "Purchase process completed.");
                LOGGER.log(Level.INFO, "Purchase data:");
                LOGGER.log(Level.INFO, "Purchase quantity: " + purchaseQuantity);
            } catch (Exception e)
            {
                LOGGER.log(Level.SEVERE, "Error performing the purchase: ", e);
            }

        } catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Error reading config file: ", e);
        }
    }

    protected static double generateRandomPurchaseQuantity(double min, double max)
    {
        Random random = new Random();
        double randomPurchaseQuantity = min + (max - min) * random.nextDouble();
        return Math.round(randomPurchaseQuantity * 100.0) / 100.0;
    }

    private int findCheapestSupplier()
    {
        int cheapestSupplierID = -1;
        BigDecimal minPrice = BigDecimal.valueOf(Double.MAX_VALUE);

        List<SupplierEntity> suppliers = purchaseRepository.getAllSuppliers();
        for (SupplierEntity supplier : suppliers)
        {
            if (supplier.getPricePerTon().compareTo(minPrice) < 0)
            {
                minPrice = supplier.getPricePerTon();
                cheapestSupplierID = supplier.getSupplierID();
            }
        }
        return cheapestSupplierID;
    }

    private BigDecimal getPricePerTon(int supplierID)
    {
        return purchaseRepository.getSupplierById(supplierID).getPricePerTon();
    }

    private void insertPurchase(Session session, int supplierID, int storageID,
            double quantity, BigDecimal pricePerTon, BigDecimal totalCosts,
            BigDecimal gravelCosts, BigDecimal deliveryCosts)
    {
        try
        {
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setSupplierId(supplierID);
            purchase.setStorageId(storageID);
            purchase.setQuantity(quantity);
            purchase.setPricePerTon(pricePerTon.doubleValue());
            purchase.setTotalCosts(totalCosts.doubleValue());
            purchase.setGravelCosts(gravelCosts.doubleValue());
            purchase.setDeliveryCosts(deliveryCosts.doubleValue());
            session.save(purchase);
            LOGGER.log(Level.INFO, "Purchase in storage " + storageID + " successfully performed.");
        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Error inserting the purchase: ", e);
        }
    }
    public static void main(String[] args)
    {
        Database database = new Database();
        Travelcosts travelcosts = new Travelcosts(37.0, database);
        Purchase purchase = new Purchase(database, travelcosts);
        double purchasequantity = generateRandomPurchaseQuantity(1000, 10000);
        purchase.performPurchase(purchasequantity);
    }
}
