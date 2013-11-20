/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.kundenserver;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;

public class CustomerServer
{
	private String database;

	private boolean keepConnection;

	private boolean connect;
	
	private boolean open;

	private IStatus status;

	private Ikundenserver kserver;

	public IStatus selectCustomer(final Position position)
	{
		return selectCustomer(position, null);
	}
	
	public boolean isConnect()
	{
		return connect;
	}

	public IStatus selectCustomer(final Position position, final ProductGroup productGroup)
	{
		if (kserver == null)
		{
			kserver = ClassFactory.createkundenserver();
		}
		if (!CustomerServer.this.open)
		{
			CustomerServer.this.open = CustomerServer.this.kserver.db_open(CustomerServer.this.database);
		}
		if (CustomerServer.this.open)
		{
			Customer customer = null;
			final int result = CustomerServer.this.kserver.getkundennr();
			if ((result != 0))
			{
				customer = new Customer();
				customer.setId(result);
				customer.setSalutation(CustomerServer.this.kserver.canrede().toString());
				customer.setEmail(CustomerServer.this.kserver.cemail().toString());
				customer.setCountry(CustomerServer.this.kserver.cland().toString());
				customer.setLastname(CustomerServer.this.kserver.cnamE1().toString());
				customer.setLastname2(CustomerServer.this.kserver.cnamE2().toString());
				customer.setLastname3(CustomerServer.this.kserver.cnamE3().toString());
				customer.setMobile(CustomerServer.this.kserver.cnatel().toString());
				customer.setCity(CustomerServer.this.kserver.cort().toString());
				customer.setZip(CustomerServer.this.kserver.cplz().toString());
				customer.setAddress(CustomerServer.this.kserver.cstrasse().toString());
				customer.setFax(CustomerServer.this.kserver.ctelefax().toString());
				customer.setPhone(CustomerServer.this.kserver.ctelefon().toString());
				customer.setPhone2(CustomerServer.this.kserver.ctelefoN2().toString());
				customer.setPersonalTitle(CustomerServer.this.kserver.ctitel().toString());
				customer.setFirstname(CustomerServer.this.kserver.cvorname().toString());
				customer.setHasAccount((Boolean) CustomerServer.this.kserver.lkundkarte());
				customer.setAccount((Double) CustomerServer.this.kserver.nkundkonto());
				final double nachlass = Math.abs((Double) CustomerServer.this.kserver.nnachlass());
				final double discount = BigDecimal.valueOf(nachlass / 100).round(new MathContext(2)).doubleValue();
				customer.setDiscount(discount);
				Object object = CustomerServer.this.kserver.lrggewaehlt();
				if (object instanceof Boolean && ((Boolean) object).booleanValue())
				{
					final Product product = Product.newInstance(position);
					Integer value = (Integer) CustomerServer.this.kserver.nrgnummer();
					product.setInvoiceNumber(value.toString());
					position.setProduct(product);
					position.setQuantity(1);
					position.setPrice((Double) CustomerServer.this.kserver.nrgbetrag());
					position.setDiscount(discount);
					position.setSearchValue(product.getInvoiceNumber());
					position.setProductGroup(productGroup);
					position.setOption(Option.PAYED_INVOICE);
				}
				CustomerServer.this.status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
						Topic.CUSTOMER_UPDATE.topic());
			}
			else
			{
				CustomerServer.this.status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(),
						Topic.CUSTOMER_UPDATE.topic());
			}
			position.getReceipt().setCustomer(customer);

			if (!CustomerServer.this.keepConnection)
			{
				this.open = !((Boolean) CustomerServer.this.kserver.db_close()).booleanValue();
			}
		}
		else
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.CUSTOMER_UPDATE.topic(), new Exception("Keine Verbindung"));
		}
		return this.status;
	}

	public void start()
	{
		final GalileoConfiguration configuration = new GalileoConfiguration();

		final ServiceTracker<PersistenceService, PersistenceService> serviceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		serviceTracker.open();
		try
		{
			Map<String, IProperty> properties = GalileoConfiguration.GalileoProperty.asMap();
			final PersistenceService persistenceService = (PersistenceService) serviceTracker.getService();
			if (persistenceService != null)
			{
				final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getCacheService()
						.getQuery(ProviderProperty.class);
				Map<String, ProviderProperty>  providerProperties = query.selectByProviderAsMap(configuration.getProviderId());
				for (final ProviderProperty providerProperty : providerProperties.values())
				{
					IProperty property = properties.get(providerProperty.getKey());
					property.setPersistedProperty(providerProperty);
				}
				final SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getCacheService().getQuery(
						Salespoint.class);
				Salespoint salespoint = salespointQuery.getCurrentSalespoint();
				if (salespoint != null)
				{
					providerProperties = query.selectByProviderAndSalespointAsMap(configuration.getProviderId(), salespoint);
					for (final ProviderProperty providerProperty : providerProperties.values())
					{
						IProperty property = properties.get(providerProperty.getKey());
						property.setPersistedProperty(providerProperty);
					}
				}
				this.database = properties.get(GalileoProperty.DATABASE_PATH.key()).value();
				this.connect = Boolean.valueOf(properties.get(GalileoProperty.CONNECT.key()).value()).booleanValue();
				this.keepConnection = Boolean.valueOf(properties.get(GalileoProperty.KEEP_CONNECTION.key()).value()).booleanValue();
			}
		}
		finally
		{
			serviceTracker.close();
		}
	}

	public void stop()
	{
		if (this.kserver != null)
		{
			this.kserver.db_close();
			this.kserver = null;
		}
	}

}
