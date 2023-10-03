package dev.despg.examples.purchase_storage_sale;


import dev.despg.examples.util.Database;
import dev.despg.core.Randomizer;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Supplier extends SimulationObject
{

    private static final Logger LOGGER = Logger.getLogger(Supplier.class.getName());
    private Database database;

    public Supplier(Database database)
    {
        this.database = database;
        SimulationObjects.getInstance().add(this);
    }
/**
 *
 */
    @SuppressWarnings("deprecation")
	public void updatePricesPerTon()
    {
        Properties supplierProbs = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties"))
        {
            if (is == null)
            {
                throw new IOException("config.properties not found in the classpath");
            }
            supplierProbs.load(is);

            double minPrice = Double.parseDouble(supplierProbs.getProperty("minPrice"));
            double maxPrice = Double.parseDouble(supplierProbs.getProperty("maxPrice"));

            try (Session session = database.getSession())
            {
                Transaction transaction = session.beginTransaction();

                List<SupplierEntity> suppliers = session.createQuery("FROM SupplierEntity", SupplierEntity.class).list();

                for (SupplierEntity supplier : suppliers)
                {
                    double randomPrice = minPrice + (maxPrice - minPrice) * Randomizer.nextDouble();
                    BigDecimal roundedPrice = BigDecimal.valueOf(randomPrice).setScale(3, RoundingMode.HALF_UP);
                    supplier.setPricePerTon(roundedPrice);
                    session.saveOrUpdate(supplier);
                }

                transaction.commit();
                LOGGER.info("PricePerTon has been updated successfully.");
            }
        } catch (NumberFormatException e)
        {
            LOGGER.log(Level.SEVERE, "Error parsing price values from the properties file.", e);
        } catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Error reading the properties file.", e);
        }
    }

    public static void main(String[] args)
    {
        Database database = new Database();
        Supplier supplier = new Supplier(database);
        supplier.updatePricesPerTon();
    }
/**
 *
 */
    @Override
    public boolean simulate(long timeStep)
    {
        updatePricesPerTon();
        return false;
    }
}
