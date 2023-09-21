package dev.despg.examples.purchase_storage_sale;
import org.hibernate.Session;
import java.util.List;

public class SaleRepository
{
	private final Session session;

    public SaleRepository(Session session)
    {
        this.session = session;
    }
/**
 *
 * @param saleEntity
 */
    @SuppressWarnings("deprecation")
	public void save(SaleEntity saleEntity)
    {
        session.save(saleEntity);
    }
/**
 *
 * @return
 */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public List<Object[]> getCustomerTotalSales()
    {
        String sumQuery = "SELECT c.id, SUM(s.totalRevenue) FROM SaleEntity s JOIN s.customer c GROUP BY c.id";
        return session.createQuery(sumQuery).getResultList();
    }
/**
 *
 * @return
 */
    public Double getTotalSalesRevenue()
    {
        return session.createQuery("SELECT SUM(s.totalRevenue) FROM SaleEntity s", Double.class).uniqueResult();
    }
/**
 *
 * @return
 */
    public double getAveragePricePerTon()
    {
        return session.createQuery("SELECT AVG(p.pricePerTon) FROM PurchaseEntity p", Double.class).uniqueResult();
    }

}
