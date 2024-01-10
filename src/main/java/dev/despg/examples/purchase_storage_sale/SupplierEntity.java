package dev.despg.examples.purchase_storage_sale;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "supplier")
public class SupplierEntity
{

    @Id
    @Column(name = "Supplier_ID")
    private int supplierID;

    @Column(name = "Name")
    private String name;

    @Column(name = "City")
    private String city;

    @Column(name = "Longitude")
    private BigDecimal longitude;

    @Column(name = "Latitude")
    private BigDecimal latitude;

    @Column(name = "PricePerTon")
    private BigDecimal pricePerTon;

    // getters and setters
/**
 *
 * @param pricePerTon
 */
    public int getSupplierID()
    {
        return supplierID;
    }
/**
 *
 * @param supplierID
 */
    public void setSupplierID(int supplierID)
    {
        this.supplierID = supplierID;
    }
/**
 *
 * @return
 */
    public String getName()
    {
        return name;
    }
/**
 *
 * @param name
 */
    public void setName(String name)
    {
        this.name = name;
    }
/**
 *
 * @return
 */
    public String getCity()
    {
        return city;
    }
/**
 *
 * @param city
 */
    public void setCity(String city)
    {
        this.city = city;
    }
/**
 *
 * @return
 */
    public BigDecimal getLongitude()
    {
        return longitude;
    }
/**
 *
 * @param longitude
 */
    public void setLongitude(BigDecimal longitude)
    {
        this.longitude = longitude;
    }
/**
 *
 * @return
 */
    public BigDecimal getLatitude()
    {
        return latitude;
    }
/**
 *
 * @param latitude
 */
    public void setLatitude(BigDecimal latitude)
    {
        this.latitude = latitude;
    }
/**
 *
 * @return
 */
    public BigDecimal getPricePerTon()
    {
        return pricePerTon;
    }
/**
 *
 * @param pricePerTon
 */
    public void setPricePerTon(BigDecimal pricePerTon)
    {
        this.pricePerTon = pricePerTon;
    }
}
