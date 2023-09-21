package dev.despg.examples.purchase_storage_sale;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Simulation;
import dev.despg.core.Time;
import java.math.BigDecimal;
import java.math.RoundingMode;
//import java.sql.SQLException;
//import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
//import dev.despg.examples.gravelshipping.*;
import dev.despg.examples.administration.*;
import dev.despg.examples.util.Database;
/**
 * Gibt den Preis pro Tonne für einen bestimmten Lieferanten zurück.
 *
 * @param lieferantenID die ID des Lieferanten
 * @return der Preis pro Tonne für den angegebenen Lieferanten
 */
public class GravelShipping extends Simulation
{
  private static Logger logger = Logger.getLogger("GravelShipping");
  private static Integer gravelToShip = 2000;
  private static Integer gravelShipped = 0;
  private final int gravelToShippedFinal = gravelToShip;
  private static Integer successfulLoadings = 0;
  private static Integer successfulLoadingSizes = 0;
  private static Integer unsuccessfulLoadings = 0;
  private static Integer unsuccessfulLoadingSizes = 0;
  private static final int NUM_TRUCKS = 5;
  private static final int NUM_LOADING_DOCKS = 3;
  private static final int NUM_WEIGHING_STATIONS = 2;
  private Storage storage;
  private Purchase purchase;
  private Supplier supplier;
  private Sale sale;
  private Customer customer;

  /**
     * Defines the setup of simulation objects and starting events before executing
     * the simulation. Prints utilization statistics afterwards
     *
     * @param args not used
     */
  public static void main(String[] args)
  {
    EventQueue eventqueue = EventQueue.getInstance();

    for (int i = 0; i < NUM_TRUCKS; i++)
       eventqueue.add(new Event(0L, GravelLoadingEventTypes.Loading, new Truck("T" + i), LoadingDock.class, null));

     for (int i = 0; i < NUM_LOADING_DOCKS; i++)
            new LoadingDock("LD" + i);

    for (int i = 0; i < NUM_WEIGHING_STATIONS; i++)
            new WeighingStation("WS" + i);

    Database database = new Database();
    Travelcosts travelcosts = new Travelcosts(37.0, database);
    Storage storage = new Storage(database);
    Purchase purchase = new Purchase(database, travelcosts);
    Supplier supplier = new Supplier(database);
    Sale sale = new Sale(database, travelcosts);
    //Customer customer = new Customer(database);
    GravelShipping gs = new GravelShipping();
    gs.setStorage(storage);
    gs.setPurchase(purchase);
    gs.setSupplier(supplier);
    gs.setSale(sale);
   // gs.setCustomer(customer);
    long timeStep = gs.simulate();

    // output some statistics after simulation run
    logger.log(Level.INFO, "Gravel shipped\t\t = " + gravelShipped + " tons");
    logger.log(Level.INFO, "Mean Time / Gravel Unit\t = " + ((double) timeStep / gravelShipped) + " minutes");
    logger.log(Level.INFO,
        String.format("Successful loadings\t = %d(%.2f%%), mean size %.2ft", successfulLoadings,
        (double) successfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100,
        (double) successfulLoadingSizes / successfulLoadings));
    logger.log(Level.INFO,
        String.format("Unsuccessful loadings\t = %d(%.2f%%), mean size %.2ft", unsuccessfulLoadings,
        (double) unsuccessfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100,
        (double) unsuccessfulLoadingSizes / unsuccessfulLoadings));
  }

  /**
     * Prints information after every timeStep in which an event got triggered.
     */
  @Override
    protected void printEveryStep(long numberOfSteps, long timeStep)
  {
    String time = numberOfSteps + ". " + Time.stepsToDateString(timeStep);
    String eventQueue = "EventQueue: " + EventQueue.getInstance().toString();
    int numberOfTrucksLoadingQueue = EventQueue.getInstance().countEvents(timeStep, true,
                GravelLoadingEventTypes.Loading, null, null);
    int numberOfTrucksWeighingQueue = EventQueue.getInstance().countEvents(timeStep, true,
                GravelLoadingEventTypes.Weighing, null, null);

    String shipped = String.format("- %dt / %dt (%.2f%%)", gravelShipped, gravelToShip,
                (double) gravelShipped / gravelToShippedFinal * 100);

    logger.log(Level.INFO,
                time + " " + shipped + " #Trucks Loading: " + numberOfTrucksLoadingQueue + ", #Trucks Weighing: "
                        + numberOfTrucksWeighingQueue);
    logger.log(Level.CONFIG, eventQueue);

    // double ourPricePerTon = sale.ourPricePerTon();

    logger.log(Level.INFO, "Lager statistics:");
    for (int lagerID = 1; lagerID <= 3; lagerID++)
    {
      double totalCapacity = storage.getTotalCapacity(lagerID);
      double currentFillLevel = storage.getCurrentFillLevel(lagerID);
     // double volumeDiscount = sale.volumeDiscount(totalCapacity, ourPricePerTon);
      logger.log(Level.INFO, "Storage " + lagerID + ":");
      logger.log(Level.INFO, "Total Capacity: " + totalCapacity + " tons");
      logger.log(Level.INFO, "Current Fill Level: "
                    + BigDecimal.valueOf(currentFillLevel).setScale(2, RoundingMode.HALF_UP) + "%");
      logger.log(Level.INFO,
                    "Remain Capacity: "
                            + BigDecimal.valueOf(totalCapacity - (totalCapacity * currentFillLevel / 100))
                                    .setScale(2, RoundingMode.HALF_UP)
                            + " tons");

      logger.log(Level.INFO, "Purchase Transaction");

      logger.log(Level.INFO, "Sale statistics");

      logger.log(Level.INFO, "Sale Transaction");

    }
  }

  public void setStorage(Storage storage)
  {
    this.storage = storage;
  }

  public void setPurchase(Purchase purchase)
  {
    this.purchase = purchase;
  }

  public void setSale(Sale sale)
  {
    this.sale = sale;
  }

  public void setCustomer(Customer customer)
{
    this.customer = customer;
  }

  public void setSupplier(Supplier supplier)
{
    this.supplier = supplier;
  }

  public static Integer getGravelToShip()
{
    return gravelToShip;
  }

  public static void setGravelToShip(Integer gravelToShip)
  {
    GravelShipping.gravelToShip = gravelToShip;
  }

  public static Integer getGravelShipped()
  {
    return gravelShipped;
  }

  public static void increaseGravelShipped(Integer gravelShipped)
  {
    GravelShipping.gravelShipped += gravelShipped;
  }

  public static void increaseSuccessfulLoadings()
  {
    successfulLoadings++;
  }

  public static void increaseSuccessfulLoadingSizes(Integer successfulLoadingSizes)
  {
    GravelShipping.successfulLoadingSizes += successfulLoadingSizes;
  }

  public static void increaseUnsuccessfulLoadings()
  {
    GravelShipping.unsuccessfulLoadings++;
  }

  public static void increaseUnsuccessfulLoadingSizes(Integer unsuccessfulLoadingSizes)
  {
    GravelShipping.unsuccessfulLoadingSizes += unsuccessfulLoadingSizes;
  }
}
