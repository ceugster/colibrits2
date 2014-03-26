/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.kundenserver.sql;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.ProductQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.galileo.kundenserver.CustomerServer;

public class CustomerServerSql extends CustomerServer
{
	private Ikundenserver2g kserver;

	public CustomerServerSql(Map<String, IProperty> properties)
	{
		super(properties);
	}
	
	public boolean isConnect()
	{
		IProperty property = properties.get(GalileoProperty.CONNECT.key());
		return Integer.valueOf(property.value()).intValue() > 0;
	}
	
	public IStatus selectCustomer(final Position position, final ProductGroup productGroup)
	{
		IProperty property = properties.get(GalileoProperty.DATABASE_PATH.key());
		String database = property.value();
		boolean wasopen = this.open;
		if (!this.open)
		{
			this.open = CustomerServerSql.this.kserver.db_open(database);
		}
		if (this.open)
		{
			Customer customer = null;
			final int result = CustomerServerSql.this.kserver.getkundennr();
			if ((result != 0))
			{
				customer = new Customer();
				customer.setId(result);
				customer.setSalutation(CustomerServerSql.this.kserver.canrede().toString());
				customer.setEmail(CustomerServerSql.this.kserver.cemail().toString());
				customer.setCountry(CustomerServerSql.this.kserver.cland().toString());
				customer.setLastname(CustomerServerSql.this.kserver.cnamE1().toString());
				customer.setLastname2(CustomerServerSql.this.kserver.cnamE2().toString());
				customer.setLastname3(CustomerServerSql.this.kserver.cnamE3().toString());
				customer.setMobile(CustomerServerSql.this.kserver.cnatel().toString());
				customer.setCity(CustomerServerSql.this.kserver.cort().toString());
				customer.setZip(CustomerServerSql.this.kserver.cplz().toString());
				customer.setAddress(CustomerServerSql.this.kserver.cstrasse().toString());
				customer.setFax(CustomerServerSql.this.kserver.ctelefax().toString());
				customer.setPhone(CustomerServerSql.this.kserver.ctelefon().toString());
				customer.setPhone2(CustomerServerSql.this.kserver.ctelefoN2().toString());
				customer.setPersonalTitle(CustomerServerSql.this.kserver.ctitel().toString());
				customer.setFirstname(CustomerServerSql.this.kserver.cvorname().toString());
				customer.setHasAccount((Boolean) CustomerServerSql.this.kserver.lkundkarte());
				customer.setAccount((Double) CustomerServerSql.this.kserver.nkundkonto());
//				final double nachlass = Math.abs((Double) CustomerServerSql.this.kserver.nnachlass());
//				final double discount = BigDecimal.valueOf(nachlass / 100).round(new MathContext(2)).doubleValue();
//				customer.setDiscount(discount);
				Object object = CustomerServerSql.this.kserver.lrggewaehlt();
				if (object instanceof Boolean && ((Boolean) object).booleanValue())
				{
					Integer value = (Integer) CustomerServerSql.this.kserver.nrgnummer();
					this.status = checkInvoice(value.toString());
					if (status.isOK())
					{
						final Product product = Product.newInstance(position);
						product.setInvoiceNumber(value.toString());
						position.setProduct(product);
						position.setQuantity(1);
						position.setPrice((Double) CustomerServerSql.this.kserver.nrgbetrag());
//						position.setDiscount(discount);
						position.setSearchValue(product.getInvoiceNumber());
						position.setProductGroup(productGroup);
						position.setOption(Option.PAYED_INVOICE);
					}
				}
			}
			else
			{
				CustomerServerSql.this.status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(),
						Topic.CUSTOMER_UPDATE.topic());
			}
			position.getReceipt().setCustomer(customer);

			property = properties.get(GalileoProperty.KEEP_CONNECTION.key());
			int keepConnection = Integer.valueOf(property.value()).intValue();
			if (keepConnection == 0)
			{
				if (!wasopen)
				{
					this.open = !((Boolean) CustomerServerSql.this.kserver.db_close()).booleanValue();
				}
			}
		}
		else
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.CUSTOMER_UPDATE.topic(), new Exception("Keine Verbindung"));
		}
		return this.status;
	}

	private IStatus checkInvoice(String invoiceNumber)
	{
		IStatus status = new Status(IStatus.INFO, Activator.getDefault().getBundle().getSymbolicName(), "Es kann nicht überprüft werden, ob die Rechnung bereits einmal an der Kasse bezahlt worden ist.");
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = tracker.getService();
			if (service != null)
			{
				ProductQuery query = (ProductQuery) service.getCacheService().getQuery(Product.class);
				if (query.selectInvoice(invoiceNumber).size() == 0)
				{
					if (service.getServerService().isConnected())
					{
						query = (ProductQuery) service.getServerService().getQuery(Product.class);
						List<Product> invoices = query.selectInvoice(invoiceNumber);
						if (invoices.size() == 0)
						{
							status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.CUSTOMER_UPDATE.topic());
						}
						else
						{
							status = new Status(IStatus.INFO, Activator.getDefault().getBundle().getSymbolicName(), "Die Rechnung wurde an der Kasse " + invoices.iterator().next().getPosition().getReceipt().getSettlement().getSalespoint().getName() + " bezahlt.");
						}
					}
					else
					{
						status = new Status(IStatus.INFO, Activator.getDefault().getBundle().getSymbolicName(), "Rechnung wurde an dieser Kasse nicht bezahlt, ob sie an einer anderen Kasse bezahlt worden ist, kann zur Zeit nicht überprüft werden, da die Verbindung zum Server nicht hergestellt ist.");
					}
				}
				else
				{
					status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.CUSTOMER_UPDATE.topic());
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return status;
	}
	
	public IStatus start()
	{
		this.status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		try
		{
			kserver = (Ikundenserver2g) ClassFactory.createkundenserver2g();
		}
		catch(Throwable e)
		{
			this.status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Die Verbindung zu Warenbewirtschaftung kann nicht hergestellt werden.", e);
		}
		return this.status;
	}

	public void stop()
	{
		if (this.kserver != null)
		{
			this.kserver = null;
		}
	}

}
