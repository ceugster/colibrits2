/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.kundenserver.old;

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
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.ProductQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.galileo.kundenserver.CustomerServer;

public class CustomerServerOld extends CustomerServer
{
	private Ikundenserver kserver;

	public CustomerServerOld(Map<String, IProperty> properties)
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
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.CUSTOMER_UPDATE.topic());
		IProperty property = properties.get(GalileoProperty.DATABASE_PATH.key());
		String database = property.value();
		boolean wasopen = this.open;
		if (!this.open)
		{
			this.open = this.kserver.db_open(database);
		}
		Activator.getDefault().setCurrentlyFailoverMode(!this.open);
		if (this.open)
		{
			Customer customer = null;
			final int result = this.kserver.getkundennr();
			if ((result != 0))
			{
				double amount = this.addNotUpdatedAmount(Integer.valueOf(result).toString());
				customer = new Customer();
				customer.setId(result);
				customer.setSalutation(this.kserver.canrede().toString());
				customer.setEmail(this.kserver.cemail().toString());
				customer.setCountry(this.kserver.cland().toString());
				customer.setLastname(this.kserver.cnamE1().toString());
				customer.setLastname2(this.kserver.cnamE2().toString());
				customer.setLastname3(this.kserver.cnamE3().toString());
				customer.setMobile(this.kserver.cnatel().toString());
				customer.setCity(this.kserver.cort().toString());
				customer.setZip(this.kserver.cplz().toString());
				customer.setAddress(this.kserver.cstrasse().toString());
				customer.setFax(this.kserver.ctelefax().toString());
				customer.setPhone(this.kserver.ctelefon().toString());
				customer.setPhone2(this.kserver.ctelefoN2().toString());
				customer.setPersonalTitle(this.kserver.ctitel().toString());
				customer.setFirstname(this.kserver.cvorname().toString());
				customer.setHasAccount((Boolean) this.kserver.lkundkarte());
				customer.setAccount(((Double) this.kserver.nkundkonto()).doubleValue() + amount);
//				final double nachlass = Math.abs((Double) CustomerServerOld.this.kserver.nnachlass());
//				final double discount = BigDecimal.valueOf(nachlass / 100).round(new MathContext(2)).doubleValue();
//				customer.setDiscount(discount);
				Object object = this.kserver.lrggewaehlt();
				if (object instanceof Boolean && ((Boolean) object).booleanValue())
				{
					Integer value = (Integer) this.kserver.nrgnummer();
					status = checkInvoice(value.toString());
					if (status.isOK())
					{
						final Product product = Product.newInstance(position);
						product.setInvoiceNumber(value.toString());
						position.setProduct(product);
						position.setQuantity(1);
						position.setPrice((Double) this.kserver.nrgbetrag());
//						position.setDiscount(discount);
						position.setSearchValue(product.getInvoiceNumber());
						position.setProductGroup(productGroup);
						position.setOption(Option.PAYED_INVOICE);
					}
				}
			}
			else
			{
				status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(),
						Topic.CUSTOMER_UPDATE.topic());
			}
			position.getReceipt().setCustomer(customer);

			property = properties.get(GalileoProperty.KEEP_CONNECTION.key());
			int keepConnection = Integer.valueOf(property.value()).intValue();
			if (keepConnection == 0)
			{
				if (!wasopen)
				{
					this.open = !((Boolean) this.kserver.db_close()).booleanValue();
				}
			}
		}
		else
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.CUSTOMER_UPDATE.topic(), new Exception("Keine Verbindung"));
		}
		return status;
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
	
	private double addNotUpdatedAmount(String customerCode)
	{
		double amount = 0D;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = tracker.getService();
			if (service != null)
			{
				ReceiptQuery query = (ReceiptQuery) service.getCacheService().getQuery(Receipt.class);
				amount = query.selectByCustomerCodeNotUpdated(customerCode);
			}
		}
		finally
		{
			tracker.close();
		}
		return amount;
	}
	
	public IStatus start()
	{
		this.status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		try
		{
			kserver = (Ikundenserver) ClassFactory.createkundenserver();
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
