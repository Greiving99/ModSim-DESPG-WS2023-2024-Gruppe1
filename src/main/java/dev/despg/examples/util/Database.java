package dev.despg.examples.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * The class Database represents a database connection using Hibernate.
 * It provides methods for creating and managing Hibernate sessions.
 */
public class Database
{

    private static SessionFactory sessionFactory;

    // Static block to initialize the Hibernate SessionFactory
    static
    {
        try
        {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Gets a new Hibernate Session.
     * @return A new Hibernate Session.
     */
    public Session getSession()
    {
        return sessionFactory.openSession();
    }

    /**
     * Checks if the Hibernate SessionFactory is initialized.
     * @return true if the SessionFactory is not null; false otherwise.
     */
    public boolean connectionExist()
    {
        return sessionFactory != null;
    }

    public static void main(String[] args)
    {
        // Test the database connection using Hibernate
        Database database = new Database();
        Session session = database.getSession();
        if (session.isConnected())
        {
            System.out.println("Connected to the database successfully using Hibernate.");
        } else
        {
            System.out.println("Failed to connect to the database using Hibernate.");
        }
        session.close();
    }
}
