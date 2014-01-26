/*
 * Created on 25.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;

public abstract class AbstractUpdateProviderServer extends AbstractGalileoServer
{
	public AbstractUpdateProviderServer(PersistenceService persistenceService, Map<String, IProperty> properties)
	{
		super(persistenceService, properties);
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

	protected ExternalProductGroup findExternalProductGroup(String code)
	{
		ExternalProductGroup externalProductGroup = null;
		if (persistenceService != null)
		{
			final ExternalProductGroupQuery query = (ExternalProductGroupQuery) persistenceService.getCacheService()
					.getQuery(ExternalProductGroup.class);
			externalProductGroup = query.selectByProviderAndCode(Activator.getDefault().getConfiguration().getProviderId(), code);
			if (externalProductGroup == null)
			{
				externalProductGroup = findDefaultExternalProductGroup();
			}
		}
		return externalProductGroup;
	}
	
	protected ExternalProductGroup findDefaultExternalProductGroup()
	{
		ExternalProductGroup externalProductGroup = null;
		if (persistenceService != null)
		{
			CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) persistenceService.getCacheService().getQuery(CommonSettings.class);
			CommonSettings commonSettings = commonSettingsQuery.findDefault();
			Collection<ProductGroupMapping> mappings = commonSettings.getDefaultProductGroup().getProductGroupMappings(Activator.getDefault().getConfiguration().getProviderId());
			if (!mappings.isEmpty())
			{
				externalProductGroup = mappings.iterator().next().getExternalProductGroup();
			}
		}
		return externalProductGroup;
	}

	protected void setCustomer(final Barcode barcode, final Position position)
	{
		position.getReceipt().setCustomer(this.getCustomer(this.getCustomerId(barcode)));
	}

	protected abstract Customer getCustomer(final int customerId);

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

	protected BarcodeVerifier[] getBarcodeVerifiers()
	{
		return barcodeVerifierTracker.getServices(new BarcodeVerifier[0]);
	}
	
	protected void setStatus(IStatus status)
	{
		this.status = status;
	}
	
	protected void log(int severity, String message)
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(severity, message);
		}
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

	protected abstract void setProduct(Barcode barcode, Position position);

	protected abstract void setEbookProduct(Barcode barcode, Position position);
}
