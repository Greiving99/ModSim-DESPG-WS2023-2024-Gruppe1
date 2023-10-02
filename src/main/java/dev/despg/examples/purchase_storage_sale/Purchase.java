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
    private double purchaseQuantity;

    public Purchase(Database database, Travelcosts travelcosts)
    {
        this.database = database;
        this.travelcosts = travelcosts;
        SimulationObjects.getInstance().add(this);
    }

    public final boolean simulate(long timeStep)
    {
        performPurchase(purchaseQuantity);
        return false;
    }

    @SuppressWarnings("deprecation")
	private void updateFillLevel(Session session, int storageID, BigDecimal fillLevel)
    {
        StorageEntity storage = session.get(StorageEntity.class, storageID);
        storage.setFillLevel(fillLevel);
        session.update(storage);
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

                List<StorageEntity> storages = session.createQuery("FROM StorageEntity", StorageEntity.class).list();
                List<SupplierEntity> suppliers = session.createQuery("FROM SupplierEntity WHERE pricePerTon IS NOT NULL", SupplierEntity.class).list();

                int selectedStorageID = -1;
                int selectedSupplierID = -1;
                BigDecimal selectedCapacity = BigDecimal.ZERO;
                BigDecimal selectedPricePerTon = BigDecimal.valueOf(Double.MAX_VALUE);
                double minDistance = Double.MAX_VALUE;

                for (StorageEntity storage : storages)
                {
                    if (storage.getFillLevel().doubleValue() < criticalCapacity * storage.getCapacity().doubleValue())
                    {
                        for (SupplierEntity supplier : suppliers)
                        {
                            if (supplier.getPricePerTon().compareTo(selectedPricePerTon) < 0)
                            {
                                double distance = travelcosts.getDistanceToSupplier(storage.getStorageID(), supplier.getSupplierID());
                                if (distance < minDistance)
                                {
                                    selectedStorageID = storage.getStorageID();
                                    selectedSupplierID = supplier.getSupplierID();
                                    selectedCapacity = storage.getCapacity();
                                    selectedPricePerTon = supplier.getPricePerTon();
                                    minDistance = distance;
                                }
                            }
                        }
                    }
                }

                if (selectedStorageID != -1)
                {
                    BigDecimal refill = selectedCapacity.subtract(session.get(StorageEntity.class, selectedStorageID).getFillLevel());
                    double quantity = Math.min(refill.doubleValue(), purchaseQuantity);
                    BigDecimal gravelCosts = BigDecimal.valueOf(quantity * selectedPricePerTon.doubleValue());
                    double supplierCosts = quantity / 40 * travelcosts.calculateTravelcosts(selectedStorageID, selectedSupplierID);
                    BigDecimal totalCosts = gravelCosts.add(BigDecimal.valueOf(supplierCosts));

                    insertPurchase(session, selectedSupplierID, selectedStorageID,
                    		quantity, selectedPricePerTon, totalCosts, gravelCosts, BigDecimal.valueOf(supplierCosts));

                    BigDecimal fillLevelNew = session.get(StorageEntity.class,
                    		selectedStorageID).getFillLevel().add(BigDecimal.valueOf(quantity / selectedCapacity.doubleValue()));
                    updateFillLevel(session, selectedStorageID, fillLevelNew);
                } else
                {
                    LOGGER.log(Level.WARNING, "No storage found for the purchase.");
                }

                transaction.commit();

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

    @SuppressWarnings("deprecation")
	private void insertPurchase(Session session, int supplierID, int storageID, double quantity,
    		BigDecimal pricePerTon, BigDecimal totalCosts, BigDecimal gravelCosts, BigDecimal deliveryCosts)
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
    }

    public static void main(String[] args)
    {
        Database database = new Database();
        Travelcosts travelcosts = new Travelcosts(37.0, database);
        Purchase purchase = new Purchase(database, travelcosts);
        purchase.performPurchase(generateRandomPurchaseQuantity(1000, 10000));
    }
}
