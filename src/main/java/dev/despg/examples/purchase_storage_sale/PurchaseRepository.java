package dev.despg.examples.purchase_storage_sale;
import org.hibernate.Session;
import java.util.List;

public class PurchaseRepository
{
	 private final Session session;

	    public PurchaseRepository(Session session)
	    {
	        this.session = session;
	    }
/**
 *
 * @return
 */
	    public List<StorageEntity> getAllStorages()
	    {
	        return session.createQuery("FROM StorageEntity", StorageEntity.class).list();
	    }
/**
 *
 * @return
 */
	    public List<SupplierEntity> getAllSuppliers()
	    {
	        return session.createQuery("FROM SupplierEntity WHERE pricePerTon IS NOT NULL", SupplierEntity.class).list();
	    }
/**
 *
 * @param supplierID
 * @return
 */
	    public SupplierEntity getSupplierById(int supplierID)
	    {
	        return session.get(SupplierEntity.class, supplierID);
	    }
/**
 *
 * @param storageID
 * @return
 */
	    public StorageEntity getStorageById(int storageID)
	    {
	        return session.get(StorageEntity.class, storageID);
	    }

}
