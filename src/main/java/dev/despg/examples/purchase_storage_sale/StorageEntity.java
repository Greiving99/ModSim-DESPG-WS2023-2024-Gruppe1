package dev.despg.examples.purchase_storage_sale;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;

@Entity
@Table(name = "storage")
public class StorageEntity
{

    @Id
    @Column(name = "Storage_ID")
    private int storageID;

    @Column(name = "Capacity")
    private BigDecimal capacity;

    @Column(name = "FillLevel")
    private BigDecimal fillLevel;

    @Column(name = "City")
    private String city;

    @Column(name = "Latitude")
    private BigDecimal latitude;

    @Column(name = "Longitude")
    private BigDecimal longitude;

    // Getter and Setter for all vars
/**
 *
 * @return
 */
    public int getStorageID()
    {
        return storageID;
    }
/**
 *
 * @param storageID
 */
    public void setStorageID(int storageID)
    {
        this.storageID = storageID;
    }
/**
 *
 * @return
 */
    public BigDecimal getCapacity()
    {
        return capacity;
    }
/**
 *
 * @param capacity
 */
    public void setCapacity(BigDecimal capacity)
    {
        this.capacity = capacity;
    }

    public BigDecimal getFillLevel()
    {
        return fillLevel;
    }
/**
 *
 * @param fillLevel
 */
    public void setFillLevel(BigDecimal fillLevel)
    {
        this.fillLevel = fillLevel;
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
}
