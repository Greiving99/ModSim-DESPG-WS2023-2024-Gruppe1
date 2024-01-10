package dev.despg.examples.purchase_storage_sale;

import jakarta.persistence.*;

@Entity
@Table(name = "purchase")
public class PurchaseEntity
{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "P_ID")
    private int id;

    @Column(name = "Supplier_ID")
    private int supplierId;

    @Column(name = "Storage_ID")
    private int storageId;

    @Column(name = "Quantity")
    private double quantity;

    @Column(name = "PricePerTon")
    private double pricePerTon;

    @Column(name = "Totalcosts")
    private double totalCosts;

    @Column(name = "Gravelcosts")
    private double gravelCosts;

    @Column(name = "Deliverycosts")
    private double deliveryCosts;

    // Getter and Setter
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
    public int getSupplierId()
    {
        return supplierId;
    }
/**
 *
 * @param supplierId
 */
    public void setSupplierId(int supplierId)
    {
        this.supplierId = supplierId;
    }
/**
 *
 * @return
 */
    public int getStorageId()
    {
        return storageId;
    }
/**
 *
 * @param storageId
 */
    public void setStorageId(int storageId)
    {
        this.storageId = storageId;
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
    public double getPricePerTon()
    {
        return pricePerTon;
    }
/**
 *
 * @param pricePerTon
 */
    public void setPricePerTon(double pricePerTon)
    {
        this.pricePerTon = pricePerTon;
    }
/**
 *
 * @return
 */
    public double getTotalCosts()
    {
        return totalCosts;
    }
/**
 *
 * @param totalCosts
 */
    public void setTotalCosts(double totalCosts)
    {
        this.totalCosts = totalCosts;
    }
/**
 *
 * @return
 */
    public double getGravelCosts()
    {
        return gravelCosts;
    }
/**
 *
 * @param gravelCosts
 */
    public void setGravelCosts(double gravelCosts)
    {
        this.gravelCosts = gravelCosts;
    }
/**
 *
 * @return
 */
    public double getDeliveryCosts()
    {
        return deliveryCosts;
    }
/**
 *
 * @param deliveryCosts
 */
    public void setDeliveryCosts(double deliveryCosts)
    {
        this.deliveryCosts = deliveryCosts;
    }
}
