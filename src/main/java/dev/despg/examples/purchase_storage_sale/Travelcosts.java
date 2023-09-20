package dev.despg.examples.purchase_storage_sale;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import dev.despg.core.SimulationObject;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import dev.despg.examples.util.Database;

public final class Travelcosts extends SimulationObject
{
    private double dieselPrice;
    private double fuelConsumption;
    private Database database;
    private long lastDieselPriceRefreshTime;
    private static final long DIESEL_PRICE_REFRESH_INTERVAL = 60 * 60 * 1000;

    public Travelcosts(double fuelConsumption, Database database)
    {
        this.fuelConsumption = fuelConsumption;
        this.dieselPrice = getDieselPrice();
        this.lastDieselPriceRefreshTime = 0L;
        this.database = database;
    }

    public double getDistanceToSupplier(int storageID, int supplierID)
    {
        double distance = 0.0;

        try (Session session = database.getSession())
        {
            Query<Object[]> query = session.createQuery("SELECT latitude, longitude FROM StorageEntity WHERE storageID = :id", Object[].class);
            query.setParameter("id", storageID);
            List<Object[]> storageResult = query.list();

            if (!storageResult.isEmpty())
            {
                double storageLat = ((Number) storageResult.get(0)[0]).doubleValue();
                double storageLon = ((Number) storageResult.get(0)[1]).doubleValue();

                query = session.createQuery("SELECT latitude, longitude FROM SupplierEntity WHERE supplierID = :id", Object[].class);
                query.setParameter("id", supplierID);
                List<Object[]> supplierResult = query.list();

                if (!supplierResult.isEmpty())
                {
                    double supplierLat = ((Number) supplierResult.get(0)[0]).doubleValue();
                    double supplierLon = ((Number) supplierResult.get(0)[1]).doubleValue();
                    distance = calculateDistance(storageLat, storageLon, supplierLat, supplierLon);
                }
            }
        }
        return distance;
    }

    public double calculateDeliveryCosts(int storageID, int customerID)
    {
        double minDistance = 0.0;
        double fuelCost = 0.0;
        String closestCity = "";

        try (Session session = database.getSession())
        {
            Query<Object[]> query = session.createQuery("SELECT latitude, longitude FROM CustomerEntity WHERE id = :id", Object[].class);
            query.setParameter("id", customerID);
            List<Object[]> customerResult = query.list();

            if (!customerResult.isEmpty())
            {
                double customerLat = ((Number) customerResult.get(0)[0]).doubleValue();
                double customerLon = ((Number) customerResult.get(0)[1]).doubleValue();

                query = session.createQuery("SELECT city, latitude, longitude FROM StorageEntity WHERE storageID = :id", Object[].class);
                query.setParameter("id", storageID);
                List<Object[]> storageResult = query.list();

                if (!storageResult.isEmpty())
                {
                    closestCity = (String) storageResult.get(0)[0];
                    double storageLat = ((Number) storageResult.get(0)[1]).doubleValue();
                    double storageLon = ((Number) storageResult.get(0)[2]).doubleValue();
                    minDistance = calculateDistance(storageLat, storageLon, customerLat, customerLon);
                }
            }

            double dieselPrice = getDieselPrice();
            double distanceInKm = minDistance;
            fuelCost = (fuelConsumption / 100) * distanceInKm * dieselPrice;
        }

        System.out.printf("Storage %s. "
                        + "Distance to the customer: %.2f km.%n", closestCity, minDistance);
        System.out.printf("Diesel price: %.3f EUR / Liter.%n", dieselPrice);
        System.out.printf("Travel costs: %.2f EUR.%n", fuelCost);

        return fuelCost;
    }

    public double getDistanceToCustomer(int storageID, int customerID)
    {
        double distance = 0.0;

        try (Session session = database.getSession())
        {
            Query<Object[]> query = session.createQuery("SELECT latitude, longitude FROM StorageEntity WHERE storageID = :id", Object[].class);
            query.setParameter("id", storageID);
            List<Object[]> storageResult = query.list();

            if (!storageResult.isEmpty())
            {
                double storageLat = ((Number) storageResult.get(0)[0]).doubleValue();
                double storageLon = ((Number) storageResult.get(0)[1]).doubleValue();

                query = session.createQuery("SELECT latitude, longitude FROM CustomerEntity WHERE id = :id", Object[].class);
                query.setParameter("id", customerID);
                List<Object[]> customerResult = query.list();

                if (!customerResult.isEmpty())
                {
                    double customerLat = ((Number) customerResult.get(0)[0]).doubleValue();
                    double customerLon = ((Number) customerResult.get(0)[1]).doubleValue();
                    distance = calculateDistance(storageLat, storageLon, customerLat, customerLon);
                }
            }
        }
        return distance;
    }

    public double calculateTravelcosts(int storageID, int supplierID)
    {
        double minDistance = getDistanceToSupplier(storageID, supplierID);
        double fuelCost = 0.0;
        String closestCity = "";

        try (Session session = database.getSession())
        {
            Query<Object[]> query = session.createQuery("SELECT latitude, longitude FROM SupplierEntity WHERE supplierID = :id", Object[].class);
            query.setParameter("id", supplierID);
            List<Object[]> supplierResult = query.list();
            if (!supplierResult.isEmpty())
            {
                double supplierLat = ((Number) supplierResult.get(0)[0]).doubleValue();
                double supplierLon = ((Number) supplierResult.get(0)[1]).doubleValue();

                Query<Object[]> storageQuery = session.createQuery("SELECT city, latitude, longitude FROM StorageEntity WHERE storageID = :id", Object[].class);
                storageQuery.setParameter("id", storageID);
                List<Object[]> storageResult = storageQuery.list();

                if (!storageResult.isEmpty())
                {
                    closestCity = (String) storageResult.get(0)[0];
                    double storageLat = ((Number) storageResult.get(0)[1]).doubleValue();
                    double storageLon = ((Number) storageResult.get(0)[2]).doubleValue();
                    minDistance = calculateDistance(storageLat, storageLon, supplierLat, supplierLon);
                }
            }

            double dieselPrice = getDieselPrice();
            double distanceInKm = minDistance;
            fuelCost = (fuelConsumption / 100) * distanceInKm * dieselPrice;

            System.out.printf("Storage %s. "
                            + "Distance to the warehouse: %.2f km.%n", closestCity, minDistance);
            System.out.printf("Diesel price: %.3f EUR / Liter.%n", dieselPrice);
            System.out.printf("Travel costs: %.2f EUR.%n", fuelCost);

            return fuelCost;
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2)
    {
        final double r = 6371; // Earth radius in km
        Double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }

    private double getDieselPrice()
    {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDieselPriceRefreshTime < DIESEL_PRICE_REFRESH_INTERVAL)
        {
            return dieselPrice;
        }

        String url = "https://www.benzinpreis.de/statistiken/deutschland/preisfixing";
        double price = 0.0;

        try
        {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("div.greenub:contains(Diesel)").first().siblingElements().select("b");

            if (!elements.isEmpty())
            {
                Element element = elements.first();
                String priceString = element.text().replace(" EUR / Liter", "");
                priceString = priceString.replace(",", ".");
                price = Double.parseDouble(priceString);
            } else
            {
                System.out.println("Desired row not found.");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        dieselPrice = price;
        lastDieselPriceRefreshTime = currentTime;
        return price;
    }
    private int getStorageIDFromDatabase()
    {
        int storageID = -1;

        try (Session session = database.getSession())
        {
            Query<Integer> query = session.createQuery("SELECT storageID FROM StorageEntity", Integer.class).setMaxResults(1);
            storageID = query.uniqueResult();
        }
        return storageID;
    }

    private int getSupplierIDFromDatabase()
    {
        int supplierID = -1;

        try (Session session = database.getSession())
        {
            Query<Integer> query = session.createQuery("SELECT supplierID FROM SupplierEntity", Integer.class).setMaxResults(1);
            supplierID = query.uniqueResult();
        }
        return supplierID;
    }


    @Override
    public boolean simulate(long timeStep)
    {
        return false;
    }
}
