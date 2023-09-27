package dev.despg.examples.purchase_storage_sale;

import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import dev.despg.examples.util.Database;


public final class Sale extends SimulationObject
{
    private static final Logger LOGGER = Logger.getLogger(Sale.class.getName());
    private final Database database;
    private final Travelcosts travelcosts;

    public Sale(Database database, Travelcosts travelcosts)
    {
        this.database = database;
        this.travelcosts = travelcosts;
        SimulationObjects.getInstance().add(this);
    }

    public boolean simulate(long timeStep)
    {
        performSales(generateRandomSalesQuantity(1000, 10000));
        return false;
    }

    public void updateCustomerTotalSales()
    {
        try (Session session = database.getSession())
        {
            List<SaleEntity> sales = session.createQuery("FROM SaleEntity", SaleEntity.class).list();

            Map<Integer, Double> customerSalesMap = sales.stream()
                    .collect(Collectors.groupingBy(SaleEntity::getCustomerId,
                            Collectors.summingDouble(SaleEntity::getTotalRevenue)));

            Transaction transaction = session.beginTransaction();

            for (Map.Entry<Integer, Double> entry : customerSalesMap.entrySet())
            {
                CustomerEntity customer = session.get(CustomerEntity.class, entry.getKey());
                if (customer != null)
                {
                    customer.setRevenue(BigDecimal.valueOf(entry.getValue()));
                    session.update(customer);
                }
            }

            transaction.commit();
        }
    }

    public double calculateDiscountRate(double quantity)
    {
        double discountRate = 0;

        if (quantity >= 6000)
        {
            discountRate += 0.20;
        } else if (quantity >= 3000)
        {
            discountRate += 0.10;
        }

        discountRate += (int) (quantity / 1000) * 0.02;
        return Math.min(discountRate, 1.0);
    }

    public double totalSales()
    {
        try (Session session = database.getSession())
        {
            List<SaleEntity> sales = session.createQuery("FROM SaleEntity", SaleEntity.class).list();
            return sales.stream().mapToDouble(SaleEntity::getTotalRevenue).sum();
        }
    }

    public void performSales(double salesQuantity)
    {
        try (Session session = database.getSession())
        {
            Transaction transaction = session.beginTransaction();

            int randomCustomerID = getRandomCustomerID();

            StorageEntity selectedStorage = findNearestStorage(session, salesQuantity, randomCustomerID);

            if (selectedStorage == null)
            {
                LOGGER.log(Level.WARNING, "Kein geeignetes Lager gefunden.");
                return;
            }

            double deliveryCosts = travelcosts.calculateDeliveryCosts(selectedStorage.getStorageID(), randomCustomerID);
            insertSales(randomCustomerID, salesQuantity, calculateSalesPricePerTon(), getMargin(selectedStorage.getFillLevel().doubleValue()),
                    calculateSalesPricePerTon() * salesQuantity, deliveryCosts,
                    salesQuantity * calculateSalesPricePerTon() - deliveryCosts, session);

            BigDecimal newFillLevel = selectedStorage.getFillLevel().subtract(
                    BigDecimal.valueOf(salesQuantity).divide(selectedStorage.getCapacity(), 2, RoundingMode.HALF_UP)
            );
            updateFillLevel(selectedStorage.getStorageID(), newFillLevel, session);

            transaction.commit();
        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Error during sale performance", e);
        }
    }



    @SuppressWarnings("unused")
	private double calculateAveragePriceFromPurchases()
    {
        try (Session session = database.getSession())
        {
            List<PurchaseEntity> purchases = session.createQuery("FROM PurchaseEntity", PurchaseEntity.class).list();

            double totalCost = 0.0;
            double totalQuantity = 0.0;

            for (PurchaseEntity purchase : purchases)
            {
                totalCost += purchase.getPricePerTon() * purchase.getQuantity();
                totalQuantity += purchase.getQuantity();
            }

            if (totalQuantity == 0)
            {
                return 0.0;
            } else
            {
                return totalCost / totalQuantity;
            }
        }
    }

    private int getRandomCustomerID()
    {
        try (Session session = database.getSession())
        {
            List<CustomerEntity> customers = session.createQuery("FROM CustomerEntity", CustomerEntity.class).list();
            if (!customers.isEmpty())
            {
                Random random = new Random();
                int randomIndex = random.nextInt(customers.size());
                return customers.get(randomIndex).getId();
            }
            return -1;
        }
    }

    private double calculateSalesPricePerTon()
    {
        Double result = calculateAveragePriceFromPurchases();
        if (result != null)
        {
            return result;
        } else
        {
            LOGGER.log(Level.WARNING, "Es wurden keine durchschnittlichen Preise pro Tonne gefunden.");
            return 0.0;
        }
    }

    private double getMargin(double fillLevel)
    {
        return fillLevel >= 0.8 ? 0.1 : 0.3 - 0.2 * (0.8 - fillLevel) / 0.8;
    }

    private static double generateRandomSalesQuantity(int min, int max)
    {
        Random random = new Random();
        return min + (max - min) * random.nextDouble();
    }

    private void insertSales(int customerID, double salesQuantity, double salesPricePerTon, double margin,
                             double totalEarnings, double deliveryCosts, double profit, Session session)
    {
        SaleEntity saleEntity = new SaleEntity();
        double discountRate = calculateDiscountRate(salesQuantity);
        double finalPricePerTon = salesPricePerTon * (1 - discountRate);
        saleEntity.setCustomerId(customerID);
        saleEntity.setQuantity(salesQuantity);
        saleEntity.setOurPricePerTon(finalPricePerTon);
        saleEntity.setMargin(margin);
        saleEntity.setTotalRevenue(totalEarnings * (1 - discountRate));
        saleEntity.setDeliveryCostsCustomer(deliveryCosts);
        saleEntity.setProfit(profit * (1 - discountRate));
        saleEntity.setDiscountRate(discountRate);
        LOGGER.log(Level.INFO, "salesPricePerTon: " + salesPricePerTon);
        LOGGER.log(Level.INFO, "discountRate: " + discountRate);
        LOGGER.log(Level.INFO, "finalPricePerTon: " + finalPricePerTon);
        LOGGER.log(Level.INFO, "TotalRevenue: " + saleEntity.getTotalRevenue());
        session.save(saleEntity);
    }

    private void updateFillLevel(int storageID, BigDecimal newFillLevel, Session session)
    {
        StorageEntity storage = session.get(StorageEntity.class, storageID);
        if (storage != null)
        {
            storage.setFillLevel(newFillLevel);
            session.update(storage);
        }
    }

    private StorageEntity findNearestStorage(Session session, double salesQuantity, int randomCustomerID)
    {
        Query<StorageEntity> query = session.createQuery("FROM StorageEntity", StorageEntity.class);
        List<StorageEntity> storages = query.list();

        StorageEntity selectedStorage = null;
        double minDistance = Double.MAX_VALUE;

        for (StorageEntity storage : storages)
        {
            BigDecimal effectiveFillLevel = storage.getFillLevel().multiply(storage.getCapacity());
            if (effectiveFillLevel.compareTo(BigDecimal.valueOf(salesQuantity)) < 0)
            {
                LOGGER.log(Level.WARNING, "Nicht genug Bestand im Lager " + storage.getStorageID() + " (" + storage.getCity() + ").");
                continue;
            }
            double distance = travelcosts.getDistanceToCustomer(storage.getStorageID(), randomCustomerID);
            if (distance < minDistance)
            {
                minDistance = distance;
                selectedStorage = storage;
            }
        }
        return selectedStorage;
    }

    public static void main(String[] args)
    {
        Database database = new Database();
        Travelcosts travelcosts = new Travelcosts(37.0, database);
        Sale sale = new Sale(database, travelcosts);
        sale.performSales(generateRandomSalesQuantity(1000, 10000));
    }
}
