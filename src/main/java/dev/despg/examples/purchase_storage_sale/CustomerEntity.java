package dev.despg.examples.purchase_storage_sale;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class CustomerEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Customer_ID")
    private int id;

    @Column(name = "Forname")
    private String forname;

    @Column(name = "Lastname")
    private String lastname;

    @Column(name = "City")
    private String city;

    @Column(name = "Longitude")
    private BigDecimal longitude;

    @Column(name = "Latitude")
    private BigDecimal latitude;

    @Column(name = "revenue")
    private BigDecimal revenue;


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
    public String getForname()
    {
        return forname;
    }
/**
 *
 * @param forname
 */
    public void setForname(String forname)
    {
        this.forname = forname;
    }
/**
 *
 * @return
 */
    public String getLastname()
    {
        return lastname;
    }
/**
 *
 * @param lastname
 */
    public void setLastname(String lastname)
    {
        this.lastname = lastname;
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
    public BigDecimal getRevenue()
    {
        return revenue;
    }
/**
 *
 * @param totalSales
 */
    public void setRevenue(BigDecimal totalSales)
    {
        this.revenue = totalSales;
    }
}
