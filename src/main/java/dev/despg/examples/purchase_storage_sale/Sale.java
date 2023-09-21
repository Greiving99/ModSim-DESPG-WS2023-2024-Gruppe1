package dev.despg.examples.purchase_storage_sale;

import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import dev.despg.examples.util.Database;

public final class Sale extends SimulationObject
{
    private static final Logger LOGGER = Logger.getLogger(Sale.class.getName());
    private final Database database;
    private final Travelcosts travelcosts;
    private final SaleRepository saleRepository;

    public Sale(Database database, Travelcosts travelcosts)
    {
        this.database = database;
        this.travelcosts = travelcosts;
        this.saleRepository = new SaleRepository(database.getSession());
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
            Transaction transaction = session.beginTransaction();
            List<Object[]> results = saleRepository.getCustomerTotalSales();
            for (Object[] result : results)
            {
                int customerId = (int) result[0];
                BigDecimal totalSales = BigDecimal.valueOf((double) result[1]);
                CustomerEntity customer = session.get(CustomerEntity.class, customerId);
                if (customer != null)
                {
                    customer.setRevenue(totalSales);
                    session.update(customer);
                }
            }
            transaction.commit();
        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Error during updating customer total sales", e);
        }
    }

    public double calculateDiscountRate(double quantity)
    {
        double discountRate = 0;

        if (quantity >= 6000)
        {
            discountRate += 0.20;  // 20% Rabatt
        } else if (quantity >= 3000)
        {
            discountRate += 0.10;  // 10% Rabatt
        }

        discountRate += (int) (quantity / 1000) * 0.02;  // Dynamischer Rabatt
        return Math.min(discountRate, 1.0);  // Max 100% Rabatt
    }

    public double totalSales()
    {
        return saleRepository.getTotalSalesRevenue();
    }

    public void performSales(double salesQuantity)
    {
        try (Session session = database.getSession())
        {
            Transaction transaction = session.beginTransaction();

            int randomCustomerID = getRandomCustomerID(session);

            StorageEntity selectedStorage = findNearestStorage(session, salesQuantity, randomCustomerID);

            if (selectedStorage == null)
            {
                LOGGER.log(Level.WARNING, "Kein geeignetes Lager gefunden.");
                return;
            }

            double deliveryCosts = travelcosts.calculateDeliveryCosts(selectedStorage.getStorageID(), randomCustomerID);
            insertSales(randomCustomerID, salesQuantity, calculateSalesPricePerTon(session), getMargin(selectedStorage.getFillLevel().doubleValue()),
                    calculateSalesPricePerTon(session) * salesQuantity, deliveryCosts,
                    salesQuantity * calculateSalesPricePerTon(session) - deliveryCosts, session);

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

    private int getRandomCustomerID(Session session)
    {
        Query<CustomerEntity> query = session.createQuery("FROM CustomerEntity ORDER BY function('RAND')");
        query.setMaxResults(1);
        CustomerEntity customer = query.uniqueResult();
        return customer.getId();
    }

    private double calculateSalesPricePerTon(Session session)
    {
        Double result = saleRepository.getAveragePricePerTon();
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
