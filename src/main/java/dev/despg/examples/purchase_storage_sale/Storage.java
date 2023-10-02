package dev.despg.examples.purchase_storage_sale;

import dev.despg.core.SimulationObject;
import dev.despg.examples.util.Database;
import dev.despg.core.SimulationObjects;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Storage extends SimulationObject
{

    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());
    private Database database;
    private List<Double> fillLevels;
    private List<Integer> storageIDs;

    public Storage(Database database)
    {
        this.database = database;
        this.fillLevels = new ArrayList<>();
        this.storageIDs = new ArrayList<>();
        SimulationObjects.getInstance().add(this);
    }
/**
 *
 */
    @Override
    public boolean simulate(long timeStep)
    {
        generateRandomData();
        return false;
    }
/**
 *
 */
    @SuppressWarnings("deprecation")
	public void generateRandomData()
    {
        try (Session session = database.getSession())
        {
            Transaction transaction = session.beginTransaction();

            Random random = new Random();
            double minFilllevel = 0.1;
            double maxFilllevel = 1.0;

            List<StorageEntity> storages = session.createQuery("FROM StorageEntity", StorageEntity.class).list();

            for (StorageEntity storage : storages)
            {
                double randomCapacity = generateRandomCapacity(10000, 100000);
                double randomFilllevel = minFilllevel + (maxFilllevel - minFilllevel) * random.nextDouble();
                BigDecimal roundedCapacity = BigDecimal.valueOf(randomCapacity).setScale(1, RoundingMode.HALF_UP);
                BigDecimal roundedFilllevel = BigDecimal.valueOf(randomFilllevel).setScale(4, RoundingMode.HALF_UP);
                storage.setCapacity(roundedCapacity);
                storage.setFillLevel(roundedFilllevel);
                session.saveOrUpdate(storage);

                fillLevels.add(randomFilllevel);
                storageIDs.add(storage.getStorageID());
            }

            transaction.commit();
        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Error generating random data for storage: " + e.getMessage());
        }
    }

    protected static double generateRandomCapacity(double min, double max)
    {
        Random random = new Random();
        return Math.round((min + (max - min) * random.nextDouble()) * 100.0) / 100.0;
    }
/**
 *
 * @param storageID
 * @return
 */
    public double getCurrentFillLevel(int storageID)
    {
        try (Session session = database.getSession())
        {
            StorageEntity storageEntity = session.get(StorageEntity.class, storageID);
            return storageEntity != null ? storageEntity.getFillLevel().doubleValue() : 0.0;
        }
    }
/**
 *
 * @param storageID
 * @return
 */
    public double getTotalCapacity(int storageID)
    {
        try (Session session = database.getSession())
        {
            StorageEntity storageEntity = session.get(StorageEntity.class, storageID);
            return storageEntity != null ? storageEntity.getCapacity().doubleValue() : 0.0;
        }
    }

    public static void main(String[] args)
    {
        Database database = new Database();
        Storage storage = new Storage(database);
        storage.generateRandomData();
    }
}
