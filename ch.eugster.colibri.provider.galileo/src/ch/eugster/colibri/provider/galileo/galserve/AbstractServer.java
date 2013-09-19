/*
 * Created on 25.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.queries.TaxCodeMappingQuery;
import ch.eugster.colibri.persistence.queries.TaxQuery;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.service.ProviderInterface;

import com4j.ComException;

public abstract class AbstractServer implements IServer
{
	private Igdserve galserve;

	private GalileoConfiguration configuration = new GalileoConfiguration();

	private String database;

	private boolean keepConnection;

	private boolean connect;
	
	private boolean open;

	private boolean wasOpen;
	
	private ServiceTracker<LogService, LogService> logServiceTracker;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;
	
	private ServiceTracker<BarcodeVerifier, BarcodeVerifier> barcodeVerifierTracker;

	protected IStatus status;

	protected GalileoConfiguration getConfiguration()
	{
		return configuration;
	}
	
	public boolean isConnect()
	{
		this.status = this.start();
		return connect;
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

	
	private String getCustomerData(final Customer customer)
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

	private void setProduct(final Barcode barcode, final Position position)
	{
		position.setEbook(barcode.isEbook());
		position.setProduct(this.getProduct(barcode, position));
		position.setFromStock(((Boolean)this.getGalserve().lagerabholfach()).booleanValue());
		if (barcode.getType().equals(Barcode.Type.ORDER))
		{
			position.setOrder(barcode.getCode());
		}
		else
		{
			position.setOrder(null);
		}
		position.setProvider(this.getConfiguration().getProviderId());
		position.setBookProvider(!((Boolean)this.getGalserve().nichtbuchen()).booleanValue());
		position.setProviderBooked(false);
		position.setOrdered(((Boolean)this.getGalserve().bestellt()).booleanValue());

		if (position.getPrice() == position.getReceipt().getSettlement().getSalespoint().getProposalPrice())
		{
			final double price = ((Double)this.getGalserve().preis()).doubleValue();
			if (position.getPrice() != price)
			{
				position.setPrice(price);
			}
		}

		if (position.getQuantity() == position.getReceipt().getSettlement().getSalespoint().getProposalQuantity())
		{
			final int quantity = ((Integer)this.getGalserve().menge()).intValue();
			if (position.getQuantity() == 0)
			{
				position.setQuantity(quantity);
			}
		}

		setExternalProductGroup(position);
		setTax(position);
		
		position.setOption(position.isOrdered() ? Position.Option.ORDERED : Position.Option.ARTICLE);
		this.setOrder(position);

		final boolean noDiscount = ((Boolean)this.getGalserve().keinrabatt()).booleanValue();
		this.setDiscount(position, noDiscount);
	}

	private void setTax(Position position)
	{
		final String taxCode = this.getGalserve().mwst().toString();
		final PersistenceService persistenceService = this.getPersistenceService();
		if (persistenceService != null)
		{
			final TaxCodeMappingQuery mappingQuery = (TaxCodeMappingQuery) persistenceService.getCacheService()
						.getQuery(TaxCodeMapping.class);
			if (mappingQuery != null)
			{
				final TaxCodeMapping taxCodeMapping = mappingQuery.selectTaxCodeMappingByProviderAndCode(this.getConfiguration().getProviderId(), taxCode);
				if (taxCodeMapping != null)
				{
					position.setCurrentTax(taxCodeMapping.getTax().getCurrentTax());
				}
			}
		}
	}
	
	private void setDiscount(final Position position, final boolean noDiscount)
	{
		if (!noDiscount)
		{
			double nachlass = 0D;
			Object object = this.getGalserve().nnachlass();
			if (object instanceof Integer)
			{
				nachlass = ((Integer) object).doubleValue();
			}
			else if (object instanceof Double)
			{
				nachlass = ((Double) object).doubleValue();
			}
			else if (object instanceof Float)
			{
				nachlass = ((Float) object).doubleValue();
			}
			double discount = BigDecimal.valueOf(nachlass).round(new MathContext(2)).doubleValue();
			if (discount == 0D)
			{
				if (((position.getReceipt().getCustomer() != null) && (position.getReceipt().getCustomer()
						.getDiscount() != 0)))
				{
					discount = position.getReceipt().getCustomer().getDiscount();
				}
			}
			if (discount > 0D)
			{
				position.setDiscount(discount);
			}
		}
	}

	private void setOrder(final Position position)
	{
		position.setOrdered(((Boolean)this.getGalserve().bestellt()).booleanValue());
		if (position.isOrdered())
		{
			position.setOrder(this.getGalserve().bestnummer().toString());
			position.setFromStock(((Boolean)this.getGalserve().lagerabholfach()).booleanValue());
		}
	}

	private Product getProduct(final Barcode barcode, final Position position)
	{
		Product product = position.getProduct();
		if (product == null)
		{
			product = Product.newInstance(position);
			position.setProduct(product);
		}
		if (barcode.getType().equals(Barcode.Type.ORDER))
		{
			product.setCode(this.getGalserve().bestnummer().toString());
			position.setOrder(barcode.getCode());
		}
		else
		{
			product.setCode(barcode.getProductCode());
		}
		product.setAuthor(this.getGalserve().autor().toString());
		product.setPublisher(this.getGalserve().verlag().toString());
		product.setTitle(this.getGalserve().titel().toString());
		return product;
	}

	private void setExternalProductGroup(Position position)
	{
		ExternalProductGroup externalProductGroup = null;
		String epgCode = this.getGalserve().wgruppe().toString();
		if (epgCode.isEmpty())
		{
			externalProductGroup = this.findDefaultExternalProductGroup();
		}
		if (externalProductGroup == null)
		{
			externalProductGroup = this.findExternalProductGroup(epgCode);
		}
		position.getProduct().setExternalProductGroup(externalProductGroup);
	}
	
	private ExternalProductGroup findExternalProductGroup(String code)
	{
		ExternalProductGroup externalProductGroup = null;
		final PersistenceService persistenceService = this.getPersistenceService();
		if (persistenceService != null)
		{
			final ExternalProductGroupQuery query = (ExternalProductGroupQuery) persistenceService.getCacheService()
					.getQuery(ExternalProductGroup.class);
			externalProductGroup = query.selectByProviderAndCode(this.getConfiguration().getProviderId(), code);
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
		final PersistenceService persistenceService = this.getPersistenceService();
		if (persistenceService != null)
		{
			CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) persistenceService.getCacheService().getQuery(CommonSettings.class);
			CommonSettings commonSettings = commonSettingsQuery.findDefault();
			Collection<ProductGroupMapping> mappings = commonSettings.getDefaultProductGroup().getProductGroupMappings(this.getConfiguration().getProviderId());
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

	protected Customer getCustomer(final int customerId)
	{
		final Customer customer = new Customer();
		customer.setAccount(((Double) this.getGalserve().nkundkonto()).doubleValue());
		customer.setAddress(this.getGalserve().cstrasse().toString());
		customer.setCity(this.getGalserve().cort().toString());
		customer.setCountry(this.getGalserve().cland().toString());
		customer.setEmail(this.getGalserve().cemail().toString());
		customer.setFax(this.getGalserve().ctelefax().toString());
		customer.setFirstname(this.getGalserve().cvorname().toString());
		customer.setHasAccount(((Boolean) this.getGalserve().lkundkarte()).booleanValue());
		customer.setId(Integer.valueOf(customerId));
		customer.setLastname(this.getGalserve().cnamE1().toString());
		customer.setLastname2(this.getGalserve().cnamE2().toString());
		customer.setLastname3(this.getGalserve().cnamE3().toString());
		customer.setMobile(this.getGalserve().cnatel().toString());
		customer.setPersonalTitle(this.getGalserve().canrede().toString());
		customer.setPhone(this.getGalserve().ctelefon().toString());
		customer.setPhone2(this.getGalserve().ctelefoN2().toString());
		customer.setSalutation(this.getGalserve().ctitel().toString());
		customer.setZip(this.getGalserve().cplz().toString());
		customer.setProviderId(this.getConfiguration().getProviderId());
		return customer;
	}

	private void setEbookProduct(final Barcode barcode, final Position position)
	{
		Product product = Product.newInstance(position);
		product.setCode(barcode.getProductCode());
		position.setProduct(product);
		position.setEbook(barcode.isEbook());
		position.setBookProvider(!((Boolean)this.getGalserve().nichtbuchen()).booleanValue());
		position.setFromStock(false);
		position.setOrder("");
		position.setProvider(this.getConfiguration().getProviderId());
		position.setProviderBooked(false);
		position.setOrdered(false);
		position.setPrice(position.getReceipt().getSettlement().getSalespoint().getProposalPrice());
		position.setQuantity(position.getReceipt().getSettlement().getSalespoint().getProposalQuantity());

		final PersistenceService persistenceService = this.getPersistenceService();
		if (persistenceService != null)
		{
			CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) persistenceService.getCacheService().getQuery(CommonSettings.class);
			CommonSettings commonSettings = commonSettingsQuery.findDefault();
			ProductGroup productGroup = commonSettings.getDefaultProductGroup();
			position.setProductGroup(productGroup);
			Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(this.getConfiguration().getProviderId());
			if (!mappings.isEmpty())
			{
				product.setExternalProductGroup(mappings.iterator().next().getExternalProductGroup());
			}

			TaxTypeQuery taxTypeQuery = (TaxTypeQuery) persistenceService.getCacheService().getQuery(TaxType.class);
			TaxType type = taxTypeQuery.selectByCode("U");
			TaxRateQuery taxRateQuery = (TaxRateQuery) persistenceService.getCacheService().getQuery(TaxRate.class);
			TaxRate rate = taxRateQuery.selectByCode("N");
			TaxQuery taxQuery = (TaxQuery) persistenceService.getCacheService().getQuery(Tax.class);
			Collection<Tax> taxes = taxQuery.selectByTaxTypeAndTaxRate(type, rate);
			if (!taxes.isEmpty())
			{
				Tax tax = taxes.iterator().next();
				position.setCurrentTax(tax.getCurrentTax());
			}
		}

		position.setOption(Position.Option.ARTICLE);
	}

	@Override
	public IStatus checkConnection(String path)
	{
		IStatus status = null;
		Igdserve galserve = ClassFactory.creategdserve();
		if (galserve.do_NOpen(path))
		{
			galserve.do_NClose();
			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Die Verbindung zur Warenbewirtschaftung Galileo wurde erfolgreich hergestellt.");
		}
		else
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Das Herstellen der Verbindung zur Warenbewirtschaftung Galileo ist fehlgeschlagen.");
		}
		galserve.dispose();
		return status;
	}

	protected PersistenceService getPersistenceService()
	{
		return persistenceServiceTracker.getService();
	}
	
	protected BarcodeVerifier[] getBarcodeVerifiers()
	{
		return barcodeVerifierTracker.getServices(new BarcodeVerifier[0]);
	}
	
	protected void setStatus(IStatus status)
	{
		this.status = status;
	}
	
	protected IStatus getStatus()
	{
		return this.status;
	}
	
	protected Igdserve getGalserve()
	{
		return this.galserve;
	}
	
	protected boolean open()
	{
		this.wasOpen = this.open;
		if ((this.getGalserve() == null) || (this.status.getSeverity() == IStatus.ERROR))
		{
			this.status = this.start();
		}

		if (this.status.getSeverity() != IStatus.ERROR)
		{
			if (!this.open)
			{
				try
				{
					this.open = this.getGalserve().do_NOpen(this.database);
					if (!this.open)
					{
						this.status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ProviderInterface.Topic.PROVIDER_FAILOVER.topic(), new RuntimeException("Verbindung zur Warenbewirtschaftung konnte nicht hergestellt werden."));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return this.open;
	}

	public IStatus start()
	{
		this.status = Status.OK_STATUS;

		this.status = new Status(IStatus.OK, Activator.PLUGIN_ID, ProviderInterface.Topic.ARTICLE_UPDATE.topic());

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(),
				LogService.class, null);
		this.logServiceTracker.open();

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.barcodeVerifierTracker = new ServiceTracker<BarcodeVerifier, BarcodeVerifier>(Activator.getDefault().getBundle().getBundleContext(), BarcodeVerifier.class, null);
		this.barcodeVerifierTracker.open();

		updateProperties();
		try
		{
			this.galserve = ClassFactory.creategdserve();
		}
		catch (ComException e)
		{
			this.status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Die Verbindung zu Warenbewirtschaftung kann nicht hergestellt werden.", e);
		}

		return this.status;
	}

	protected void updateProperties()
	{
		Map<String, IProperty> properties = GalileoConfiguration.GalileoProperty.asMap();
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getCacheService()
					.getQuery(ProviderProperty.class);
			Map<String, ProviderProperty>  providerProperties = query.selectByProviderAsMap(this.configuration.getProviderId());
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
				providerProperties = query.selectByProviderAndSalespointAsMap(this.configuration.getProviderId(), salespoint);
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
	
	protected void close()
	{
		if (!this.wasOpen && this.open && !this.keepConnection)
		{
			this.getGalserve().do_NClose();
			this.open = false;
			this.wasOpen = false;
		}
	}

	@Override
	public void stop()
	{
		if (this.galserve != null)
		{
			this.galserve.dispose();
			this.galserve = null;
		}
		if (this.persistenceServiceTracker != null)
		{
			this.persistenceServiceTracker.close();
		}
		if (this.barcodeVerifierTracker != null)
		{
			this.barcodeVerifierTracker.close();
		}
		if (this.logServiceTracker != null)
		{
			this.logServiceTracker.close();
		}
		this.status = Status.CANCEL_STATUS;
	}

	protected void log(int severity, String message)
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(severity, message);
		}
	}
	
}
