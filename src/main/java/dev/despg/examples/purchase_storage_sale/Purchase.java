package dev.despg.examples.purchase_storage_sale;

import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.math.BigDecimal;
import java.util.List;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.despg.examples.util.Database;

public class Purchase extends SimulationObject
{
    private static final Logger LOGGER = Logger.getLogger(Purchase.class.getName());
    private static Logger logger = Logger.getLogger("GravelShipping");
    private Database database;
    private Travelcosts travelcosts;
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
/**
 *
 * @param purchasequantity
 */
    public void performPurchase(double purchasequantity)
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

            purchasequantity = generateRandomPurchasequantity(minPurchaseQuantity, maxPurchaseQuantity);

            try (Session session = database.getSession())
            {

                String storageQueryString = "FROM StorageEntity";
                Query<StorageEntity> storageQuery = session.createQuery(storageQueryString, StorageEntity.class);
                List<StorageEntity> storages = storageQuery.list();

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
                                double quantity = Math.min(refill.doubleValue(), purchasequantity);
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
                    BigDecimal refill = selectedCapacity.subtract(getFillLevel(selectedStorageID));
                    double quantity = Math.min(refill.doubleValue(), purchasequantity);
                    BigDecimal gravelcosts = BigDecimal.valueOf(quantity * selectedPricePerTon.doubleValue());
                    double suppliercosts = quantity / 40 * travelcosts.calculateTravelcosts(selectedStorageID, selectedSupplierID);
                    BigDecimal totalcosts = gravelcosts.add(BigDecimal.valueOf(suppliercosts));

                    insertPurchase(session, selectedSupplierID, selectedStorageID, quantity,
                    		selectedPricePerTon, totalcosts, gravelcosts, BigDecimal.valueOf(suppliercosts));
                    BigDecimal fillLevelNew = getFillLevel(selectedStorageID).add(BigDecimal.valueOf(quantity / selectedCapacity.doubleValue()));
                    updateFillLevel(session, selectedStorageID, fillLevelNew);

                    LOGGER.log(Level.INFO, "Purchase performed in storage " + selectedStorageID + ".");
                } else
                {
                    System.out.println("No storage found for the purchase.");
                }

                logger.log(Level.INFO, "Purchase process completed.");
                logger.log(Level.INFO, "Purchase data:");
                logger.log(Level.INFO, "Purchase quantity: " + purchasequantity);
            } catch (Exception e)
            {
                LOGGER.log(Level.SEVERE, "Error performing the purchase: ", e);
            }

        } catch (IOException e)
         {
            LOGGER.log(Level.SEVERE, "Error reading config file: ", e);
        }
    }

    private int findCheapestSupplier()
    {
        int cheapestSupplierID = -1;
        BigDecimal minPrice = BigDecimal.valueOf(Double.MAX_VALUE);

        try (Session session = database.getSession())
        {
            String queryStr = "FROM SupplierEntity WHERE pricePerTon IS NOT NULL";
            List<SupplierEntity> suppliers = session.createQuery(queryStr, SupplierEntity.class).list();
            for (SupplierEntity supplier : suppliers)
            {
                if (supplier.getPricePerTon().compareTo(minPrice) < 0)
                {
                    minPrice = supplier.getPricePerTon();
                    cheapestSupplierID = supplier.getSupplierID();
                }
            }
        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Error finding the cheapest supplier: ", e);
        }
        return cheapestSupplierID;
    }

    private BigDecimal getPricePerTon(int supplierID)
    {
        try (Session session = database.getSession())
        {
            SupplierEntity supplier = session.get(SupplierEntity.class, supplierID);
            return supplier.getPricePerTon();
        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Error while retrieving the price per ton: ", e);
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getFillLevel(int storageID)
    {
        try (Session session = database.getSession())
        {
            StorageEntity storage = session.get(StorageEntity.class, storageID);
            return storage.getFillLevel();
        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Error while retrieving the fill level: ", e);
            return BigDecimal.ZERO;
        }
    }

    private void updateFillLevel(Session session, int storageID, BigDecimal fillLevel)
    {
        Transaction tx = null;
        try
        {
            tx = session.beginTransaction();
            StorageEntity storage = session.get(StorageEntity.class, storageID);
            storage.setFillLevel(fillLevel);
            session.update(storage);
            tx.commit();
            System.out.println("Fill level for storage " + storageID + " has been updated.");
        } catch (Exception e)
        {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Error updating the fill level: ", e);
        }
    }
/**
 *
 * @param session
 * @param supplierID
 * @param storageID
 * @param quantity
 * @param pricePerTon
 * @param totalCosts
 * @param gravelCosts
 * @param deliveryCosts
 */
    private void insertPurchase(Session session, int supplierID, int storageID,
    		double quantity, BigDecimal pricePerTon, BigDecimal totalCosts, BigDecimal gravelCosts, BigDecimal deliveryCosts)
    {
        Transaction tx = null;
        try
        {
            tx = session.beginTransaction();
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setSupplierId(supplierID);
            purchase.setStorageId(storageID);
            purchase.setQuantity(quantity);
            purchase.setPricePerTon(pricePerTon.doubleValue());
            purchase.setTotalCosts(totalCosts.doubleValue());
            purchase.setGravelCosts(gravelCosts.doubleValue());
            purchase.setDeliveryCosts(deliveryCosts.doubleValue());
            session.save(purchase);
            tx.commit();
            LOGGER.log(Level.INFO, "Purchase in storage " + storageID + " successfully performed.");
        } catch (Exception e)
        {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Error inserting the purchase: ", e);
        }
    }

    protected static double generateRandomPurchasequantity(double min, double max)
    {
        Random random = new Random();
        double randomPurchasequantity = min + (max - min) * random.nextDouble();
        return Math.round(randomPurchasequantity * 100.0) / 100.0;
    }

    public static void main(String[] args)
    {
        Database database = new Database();
        Travelcosts travelcosts = new Travelcosts(37.0, database);
        Purchase purchase = new Purchase(database, travelcosts);
        double purchasequantity = generateRandomPurchasequantity(1000, 10000);
        purchase.performPurchase(purchasequantity);
    }
}
