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
	private Database database;
	private Travelcosts travelcosts;
	private double salesquantity;
	@SuppressWarnings("unused")
	private int randomCustomerID;
    public Sale(Database database, Travelcosts travelcosts) {
        this.database = database;
        this.travelcosts = travelcosts;
        SimulationObjects.getInstance().add(this);
    }

    public boolean simulate(long timeStep)
    {
      performSales(salesquantity);
      return false;
    }

    public void updateCustomerTotalSales()
    {
        try (Session session = database.getSession())
        {
            Transaction transaction = session.beginTransaction();

            String sumQuery = "SELECT c.id, SUM(s.totalRevenue) FROM SaleEntity s JOIN s.customer c GROUP BY c.id";
            List<Object[]> results = session.createQuery(sumQuery).getResultList();

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

        // Stellen Sie sicher, dass der maximale Rabattsatz nicht mehr als 100% beträgt.
        return Math.min(discountRate, 1.0);
    }


    public double totalSales()
    {
        try (Session session = database.getSession())
        {
            Query<Double> query = session.createQuery("SELECT SUM(s.totalRevenue) FROM SaleEntity s", Double.class);
            return query.uniqueResult();
        }
    }

    public void performSales(double salesQuantity)
    {
        try (Session session = database.getSession())
        {
            Transaction transaction = session.beginTransaction();

            int randomCustomerID = getRandomCustomerID();

            // Suche das Lager mit der kürzesten Entfernung zum Kunden.
            Query<StorageEntity> query = session.createQuery("FROM StorageEntity", StorageEntity.class);
            List<StorageEntity> storages = query.list();
            int nearestStorageID = -1;
            double minDistance = Double.MAX_VALUE;
            salesQuantity = generateRandomSalesQuantity(1000, 10000);
            StorageEntity selectedStorage = null;

            for (StorageEntity storage : storages)
            {
                BigDecimal effectiveFillLevel = storage.getFillLevel().multiply(storage.getCapacity());
                if (effectiveFillLevel.compareTo(BigDecimal.valueOf(salesQuantity)) < 0)
                {
                    System.out.println("Nicht genug Bestand im Lager " + storage.getStorageID() + " (" + storage.getCity() + ").");
                    continue;
                }
                double distance = travelcosts.getDistanceToCustomer(storage.getStorageID(), randomCustomerID);
                if (distance < minDistance)
                {
                    minDistance = distance;
                    nearestStorageID = storage.getStorageID();
                    selectedStorage = storage;
                }
            }

            if (nearestStorageID == -1)
            {
                System.out.println("Kein geeignetes Lager gefunden.");
                return;
            }

            // Führe den Verkaufstransaktion mit dem gefundenen Lager durch
            double deliveryCosts = travelcosts.calculateDeliveryCosts(nearestStorageID, randomCustomerID);
            insertSales(randomCustomerID, salesQuantity, calculateSalesPricePerTon(), getMargin(selectedStorage.getFillLevel().doubleValue()),
                        calculateSalesPricePerTon() * salesQuantity, deliveryCosts,
                        salesQuantity * calculateSalesPricePerTon() - deliveryCosts, session);

            BigDecimal newFillLevel =
            		selectedStorage.getFillLevel().subtract(BigDecimal.valueOf(salesQuantity).divide(selectedStorage.getCapacity(),
            				2, RoundingMode.HALF_UP));
            updateFillLevel(nearestStorageID, newFillLevel, session);

            transaction.commit();
        }
    }

    private int getRandomCustomerID()
    {
        try (Session session = database.getSession())
        {
            Query<CustomerEntity> query = session.createQuery("FROM CustomerEntity ORDER BY function('RAND')");
            query.setMaxResults(1);
            CustomerEntity customer = query.uniqueResult();
            return customer.getId();
        }
    }

    private double calculateSalesPricePerTon()
    {
        try (Session session = database.getSession())
        {
            Query<Double> query = session.createQuery("SELECT AVG(p.pricePerTon) FROM PurchaseEntity p", Double.class);
            Double result = query.uniqueResult();
            if (result != null)
            {
                return result;
            } else
            {
                System.out.println("Es wurden keine durchschnittlichen Preise pro Tonne gefunden.");
                return 0.0;  // Oder einen anderen Standardwert oder eine Ausnahme werfen.
            }
        }
    }

    private double getMargin(double fillLevel)
    {
        if (fillLevel >= 0.8)
        {
            return 0.1;
        } else
        {
            return 0.3 - 0.2 * (0.8 - fillLevel) / 0.8;
        }
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

        // Gesamtrabatt berechnen
        double discountRate = calculateDiscountRate(salesQuantity);

        // Den finalen Verkaufspreis pro Tonne unter Berücksichtigung des Gesamtrabatts berechnen
        double finalPricePerTon = salesPricePerTon * (1 - discountRate);

        saleEntity.setCustomerId(customerID);
        saleEntity.setQuantity(salesQuantity);
        saleEntity.setOurPricePerTon(finalPricePerTon); // Verwenden Sie den finalen Preis pro Tonne
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

    private double getDynamicDiscountRate(double quantity)
    {
        int discountPercentage = (int) (quantity / 1000) * 2;
        return discountPercentage / 100.0;
    }

    public static void main(String[] args)
    {
        Database database = new Database();
        Travelcosts travelcosts = new Travelcosts(37.0, database);
        Sale sale = new Sale(database, travelcosts);
        double salesQuantity = generateRandomSalesQuantity(1000, 10000);
        sale.performSales(salesQuantity);

    }
}
