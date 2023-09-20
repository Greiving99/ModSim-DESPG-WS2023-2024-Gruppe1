package dev.despg.examples.purchase_storage_sale;
import jakarta.persistence.*;

@Entity
@Table(name = "sale")
public class SaleEntity
{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "S_ID")
    private int id;

    @Column(name = "C_ID")
    private int customerId;

    @Column(name = "Quantity")
    private double quantity;

    @Column(name = "OurPricePerTon")
    private double ourPricePerTon;

    @Column(name = "Margin")
    private double margin;

    @Column(name = "TotalRevenue")
    private double totalRevenue;

    @Column(name = "Deliverycosts_Customer")
    private double deliveryCostsCustomer;

    @Column(name = "Profit")
    private double profit;

    @Column(name = "DiscountRate")
    private double discountRate;

    // Getter und Setter für alle Felder...
/**
 *
 * @return
 */
    public int getId()
    {
        return id;
    }
/**
 *
 * @param id
 */
    public void setId(int id)
    {
        this.id = id;
    }
/**
 *
 * @return
 */
    public int getCustomerId()
   {
        return customerId;
    }
/**
 *
 * @param customerId
 */
    public void setCustomerId(int customerId)
    {
        this.customerId = customerId;
    }
/**
 *
 * @return
 */
    public double getQuantity()
    {
        return quantity;
    }
/**
 *
 * @param quantity
 */
    public void setQuantity(double quantity)
    {
        this.quantity = quantity;
    }
/**
 *
 * @return
 */
    public double getOurPricePerTon()
    {
        return ourPricePerTon;
    }
/**
 *
 * @param ourPricePerTon
 */
    public void setOurPricePerTon(double ourPricePerTon)
    {
        this.ourPricePerTon = ourPricePerTon;
    }
/**
 *
 * @return
 */
    public double getMargin()
    {
        return margin;
    }
/**
 *
 * @param margin
 */
    public void setMargin(double margin)
    {
        this.margin = margin;
    }
/**
 *
 * @return
 */
    public double getTotalRevenue()
    {
        return totalRevenue;
    }
/**
 *
 * @param totalRevenue
 */
    public void setTotalRevenue(double totalRevenue)
    {
        this.totalRevenue = totalRevenue;
    }
/**
 *
 * @return
 */
    public double getDeliveryCostsCustomer()
    {
        return deliveryCostsCustomer;
    }
/**
 *
 * @param deliveryCostsCustomer
 */
    public void setDeliveryCostsCustomer(double deliveryCostsCustomer)
    {
        this.deliveryCostsCustomer = deliveryCostsCustomer;
    }
/**
 *
 * @return
 */
    public double getProfit()
    		{
        return profit;
    }
/**
 *
 * @param profit
 */
    public void setProfit(double profit)
    {
        this.profit = profit;
    }
/**
 *
 * @return
 */
    public double getDiscountRate()
    {
        return discountRate;
    }
/**
 *
 * @param discountRate
 */
    public void setDiscountRate(double discountRate)
    {
        this.discountRate = discountRate;
    }
}
