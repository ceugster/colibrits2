/*
 * Created on 25.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import java.text.NumberFormat;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;

public abstract class AbstractGalileoServer implements IServer
{
	protected GalileoConfiguration configuration = new GalileoConfiguration();
	
	protected Map<String, IProperty> properties;

	protected boolean open = false;
	
	protected boolean wasOpen;
	
	protected PersistenceService persistenceService;
	
	protected ServiceTracker<LogService, LogService> logServiceTracker;

	protected ServiceTracker<BarcodeVerifier, BarcodeVerifier> barcodeVerifierTracker;

	protected IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), "Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " wurde erfolgreich hergestellt.");

	public AbstractGalileoServer(PersistenceService persistenceService, Map<String, IProperty> properties)
	{
		this.persistenceService = persistenceService;
		this.properties = properties;
	}
	
	protected int getCustomerId(final Barcode barcode)
	{
		int customerId = 0;
		final String code = barcode.getDetail();
		try
		{
			customerId = Long.valueOf(code).intValue();
		}
		catch (final NumberFormatException e)
		{
		}
		return customerId;
	}

	protected void updatePosition(final Barcode barcode, final Position position)
	{
		String value = null;
		switch (barcode.getType())
		{
			case ARTICLE:
			{
				log(LogService.LOG_INFO, "Aktualisiere Artikeldaten aus Warenbewirtschaftung.");
				if (barcode.isEbook())
				{
					this.setEbookProduct(barcode, position);
				}
				else
				{
					this.setProduct(barcode, position);
					value = position.getProduct().getAuthorAndTitleShortForm();
				}
				break;
			}
			case ORDER:
			{
				log(LogService.LOG_INFO, "Aktualisiere Bestelldaten aus Warenbewirtschaftung.");
				this.setProduct(barcode, position);
				value = position.getOrder();
				break;
			}
			case INVOICE:
			{
				log(LogService.LOG_INFO, "Aktualisiere Rechnungsdaten aus Warenbewirtschaftung.");
				this.setProduct(barcode, position);
				value = position.getProduct().getCode();
				break;
			}
			case CUSTOMER:
			{
				log(LogService.LOG_INFO, "Aktualisiere Kundendaten aus Warenbewirtschaftung.");
				this.setCustomer(barcode, position);
				value = this.getCustomerData(position.getReceipt().getCustomer());
				break;
			}
			default:
			{

			}
		}
		log(LogService.LOG_INFO, "Suche " + barcode.getType().toString() + " mit Code " + barcode.getCode()
					+ ": Gefunden: " + value);
	}

	protected String getCustomerData(final Customer customer)
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append((customer.getLastname() == null) || customer.getLastname().isEmpty() ? "" : customer
				.getLastname());
		if ((customer.getFirstname() != null) && !customer.getFirstname().isEmpty())
		{
			if (builder.length() > 0)
			{
				builder = builder.append(" ");
			}
			builder = builder.append(customer.getFirstname());
		}
		if (builder.length() > 0)
		{
			builder = builder.append(", Kontostand: ");
			builder = builder.append(NumberFormat.getCurrencyInstance().format(customer.getAccount()));
		}
		return builder.toString();
	}

	protected void setCustomer(final Barcode barcode, final Position position)
	{
		position.getReceipt().setCustomer(this.updateCustomer(this.getCustomerId(barcode)));
	}

	protected abstract void setProduct(Barcode barcode, Position position);

	protected abstract void setEbookProduct(Barcode barcode, Position position);

	protected abstract Customer updateCustomer(final int customerId);

	public IStatus getStatus()
	{
		return status;
	}
	
	public boolean isConnect()
	{
		IProperty property = properties.get(GalileoProperty.CONNECT.key());
		return Integer.valueOf(property.value()).intValue() > 0;
	}
	
	protected BarcodeVerifier[] getBarcodeVerifiers()
	{
		return barcodeVerifierTracker.getServices(new BarcodeVerifier[0]);
	}
	
	protected void log(int severity, String message)
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(severity, message);
		}
	}
	
	@Override
	public IStatus checkConnection()
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
				"Die Verbindung zur Warenbewirtschaftung Galileo wurde erfolgreich hergestellt.");
		if (this.open())
		{
			this.close();
		}
		else
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					Topic.SCHEDULED_PROVIDER_UPDATE.topic(), new Exception("Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " kann nicht hergestellt werden."));
		}
		return status;
	}

	@Override
	public IStatus start()
	{
		logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(), LogService.class, null);
		logServiceTracker.open();
		barcodeVerifierTracker = new ServiceTracker<BarcodeVerifier, BarcodeVerifier>(Activator.getDefault().getBundle().getBundleContext(), BarcodeVerifier.class, null);
		barcodeVerifierTracker.open();
		return status;
	}

	public abstract boolean open();

	public abstract void close();

	@Override
	public void stop()
	{
		logServiceTracker.close();
		barcodeVerifierTracker.close();
	}
}
