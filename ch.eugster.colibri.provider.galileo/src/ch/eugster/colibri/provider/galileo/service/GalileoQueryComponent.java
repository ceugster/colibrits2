package ch.eugster.colibri.provider.galileo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.queries.TaxCodeMappingQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.ProviderConfiguration;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.galserve.GalserverFactory;
import ch.eugster.colibri.provider.galileo.galserve.IFindArticleServer;
import ch.eugster.colibri.provider.galileo.kundenserver.CustomerServerFactory;
import ch.eugster.colibri.provider.galileo.kundenserver.ICustomerServer;
import ch.eugster.colibri.provider.galileo.service.GalileoConfiguratorComponent.GalileoTaxCode;
import ch.eugster.colibri.provider.service.AbstractProviderQuery;
import ch.eugster.colibri.provider.service.ProviderQuery;

public class GalileoQueryComponent extends AbstractProviderQuery implements ProviderQuery
{
	private IFindArticleServer findArticleServer;

	private ICustomerServer customerServer;

	private boolean showFailoverMessage = true;
	
	private final Collection<BarcodeVerifier> barcodeVerifiers = new ArrayList<BarcodeVerifier>();

	public GalileoQueryComponent()
	{
	}
	
	public boolean checkBarcode(Barcode barcode)
	{
		return !barcode.getCode().startsWith(Barcode.PREFIX_VOUCHER);
	}
	
	public void updateCustomer(Receipt receipt)
	{
		String code = receipt.getCustomerCode();
		if (code != null)
		{
			try
			{
				int customerId = Integer.valueOf(code);
				receipt.setCustomer(this.findArticleServer.getCustomer(customerId));
			}
			catch (NumberFormatException e)
			{
//				receipt.setCustomer(null);
			}
		}
	}

	public void setStatus(IStatus status)
	{
	}
	
	@Override
	public boolean canMap(final CurrentTax currentTax)
	{
		return Activator.getDefault().getConfiguration().canMap(currentTax);
	}

	@Override
	public boolean canMap(final Tax tax)
	{
		return Activator.getDefault().getConfiguration().canMap(tax);
	}
	
	public boolean isConnect()
	{
		return this.findArticleServer != null && this.findArticleServer.isConnect();
	}

	public String getProviderId()
	{
		return Activator.getDefault().getConfiguration().getProviderId();
	}
	
	@Override
	public IStatus findAndRead(final Barcode barcode, final Position position)
	{
		IStatus status = Status.OK_STATUS;
		if (status.getException() == null)
		{
			log(LogService.LOG_INFO, "Verbindung checken.");
			if (findArticleServer.isConnect())
			{
				log(LogService.LOG_INFO, "Suche in Warenbewirtschaftung nach \"" + barcode.getCode() + "\".");
				status = this.findArticleServer.findAndRead(barcode, position);
				if ((status.isOK()) || (status.getSeverity() == IStatus.ERROR))
				{
					log(LogService.LOG_INFO, "Suche nach \"" + barcode.getCode() + "\" abgeschlossen.");
					this.sendEvent(this.getEvent(status, false));
				}
			}
		}
		return status;
	}
	
	private void sendEvent(Event event)
	{
		if (this.eventAdmin != null)
		{
			this.eventAdmin.sendEvent(event);
		}
	}

	@Override
	public String getImageName()
	{
		return Activator.getDefault().getConfiguration().getImageName();
	}

	@Override
	public IStatus selectCustomer(final Position position, ProductGroup productGroup)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.PROVIDER_QUERY.topic());
		if (this.customerServer.isConnect())
		{
			log(LogService.LOG_INFO, "Starte Kundensuche...");
			try
			{
				status = this.customerServer.selectCustomer(position, productGroup);
			}
			catch (Exception e)
			{
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), "Fehler bei Aufruf von Kundensuche. " + e.getLocalizedMessage(),e);
				log(LogService.LOG_ERROR, status.getMessage());
//				this.sendEvent(this.getEvent(status, true));
			}
			finally
			{
				log(LogService.LOG_INFO, position.getReceipt().getCustomer() == null ? "Kundensuche abgebrochen." : "Kunden ausgewählt: " + position.getReceipt().getCustomerCode() + " - " + position.getReceipt().getCustomer().getFullname() + ".");
				if (status.isOK())
				{
					this.sendEvent(this.getEvent(status, true));
				}
			}
		}
		return status;
	}

	protected void activate(final ComponentContext componentContext)
	{
		super.activate(componentContext);
		this.loadProperties(persistenceService.getCacheService(), Activator.getDefault().getConfiguration().getProviderId(), GalileoConfiguration.GalileoProperty.asMap());
		this.startFindArticleServer();
		if (this.findArticleServer == null)
		{
			this.loadProperties(persistenceService.getServerService(), Activator.getDefault().getConfiguration().getProviderId(), GalileoConfiguration.GalileoProperty.asMap());
			this.startFindArticleServer();
		}
		this.startCustomerServer();
		log(LogService.LOG_INFO, "Service " + this.context.getProperties().get("component.name") + " aktiviert.");
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		this.stopCustomerServer();
		this.stopFindArticleServer();
		super.deactivate(componentContext);
	}

	protected void addBarcodeVerifier(final BarcodeVerifier barcodeVerifier)
	{
		this.barcodeVerifiers.add(barcodeVerifier);
	}

	protected void removeBarcodeVerifier(final BarcodeVerifier barcodeVerifier)
	{
		this.barcodeVerifiers.remove(barcodeVerifier);
	}

	private Event getEvent(final IStatus status, boolean force)
	{
		final String topic = status.getMessage();
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();

		if (status.getException() != null)
		{
			if (this.showFailoverMessage)
			{
				properties.put("show.failover.message", Boolean.valueOf(this.showFailoverMessage));
				this.showFailoverMessage = false;
			}
		}
		else
		{
			if (!this.showFailoverMessage)
			{
				this.showFailoverMessage = true;
			}
		}

		properties.put(EventConstants.EVENT_TOPIC, topic);
		properties.put(EventConstants.BUNDLE_ID, Activator.getDefault().getBundle().getSymbolicName());
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
		if (status.getException() != null)
		{
			properties.put(EventConstants.EXCEPTION, status.getException());
			properties.put(EventConstants.EXCEPTION_MESSAGE, status.getException().getMessage() == null ? "" : status.getException().getMessage());
		}
		properties.put("message", "Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " kann nicht hergestellt werden. Die Daten müssen manuell erfasst werden.");
		properties.put("status", status);
		properties.put("force", Boolean.valueOf(force));
		for (Topic t : Topic.values())
		{
			if (t.topic().equals(status.getMessage()))
			{
				properties.put("topic", t.topic());
				break;
			}
		}
		final Event event = new Event(topic, properties);
		return event;
	}

	private void startFindArticleServer()
	{
		if (this.findArticleServer == null)
		{
			this.findArticleServer = GalserverFactory.createFindArticleServer(persistenceService, properties);
			if (this.findArticleServer != null)
			{
				this.findArticleServer.start();
			}
		}
	}

	private void startCustomerServer()
	{
		if (this.customerServer == null)
		{
			this.customerServer = CustomerServerFactory.createCustomerServer(properties);
			if (this.customerServer != null)
			{
				this.customerServer.start();
			}
		}
	}

	private void stopFindArticleServer()
	{
		if (this.findArticleServer != null)
		{
			this.findArticleServer.stop();
			this.findArticleServer = null;
		}
	}

	private void stopCustomerServer()
	{
		if (this.customerServer != null)
		{
			this.customerServer.stop();
			this.customerServer = null;
		}
	}

	@Override
	public ProviderConfiguration getConfiguration() 
	{
		return Activator.getDefault().getConfiguration();
	}

	@Override
	public String getName() 
	{
		return Activator.getDefault().getConfiguration().getName();
	}

	@Override
	public IStatus checkTaxCodes(PersistenceService service)
	{
		if (service != null)
		{
			TaxCodeMappingQuery query = (TaxCodeMappingQuery) service.getCacheService().getQuery(TaxCodeMapping.class);
			GalileoTaxCode[] taxCodes = GalileoConfiguratorComponent.GalileoTaxCode.values();
			for (GalileoTaxCode taxCode : taxCodes)
			{
				String providerId = Activator.getDefault().getConfiguration().getProviderId();
				String code = GalileoTaxCode.getCode(taxCode.name());
				TaxCodeMapping mapping = query.selectTaxCodeMappingByProviderAndCode(providerId, code);
				if (mapping == null || mapping.isDeleted())
				{
					return new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), "Mehrwertsteuer für " + taxCode.name() + " in " + this.getName() + " nicht gemappt.");
				}
			}
		}
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), "");
		return status;
	}
}
