/*
 * Created on 18.07.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve.old;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.TaxCodeMappingQuery;
import ch.eugster.colibri.persistence.queries.TaxQuery;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.galileo.galserve.AbstractFindArticleServer;
import ch.eugster.colibri.provider.galileo.galserve.IFindArticleServer;

public class FindArticleServerOldCom4j extends AbstractFindArticleServer implements IFindArticleServer
{
	private Igdserve galserve;
	
	public FindArticleServerOldCom4j(PersistenceService persistenceService, Map<String, IProperty> properties)
	{
		super(persistenceService, properties);
	}

	public Customer getCustomer(int customerId)
	{
		Customer customer = null;
		if (isConnect())
		{
			log(LogService.LOG_INFO, "Verbindung öffnen.");
			if (this.open())
			{
				if (this.galserve.do_getkunde(customerId))
				{
					log(LogService.LOG_INFO, "Kunden gefunden; aktualisieren.");
					customer = this.updateCustomer(customerId);
				}

				this.close();
			}
		}
		return customer;
	}
	
	@Override
	public IStatus findAndRead(final Barcode barcode, final Position position)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.PROVIDER_QUERY.topic());
		String msg = null;

		log(LogService.LOG_INFO, "Verbindung checken in Server.");
		if (isConnect())
		{
			log(LogService.LOG_INFO, "Verbindung öffnen.");
			if (this.open())
			{
				if (barcode.getType().equals(Barcode.Type.CUSTOMER))
				{
					log(LogService.LOG_INFO, "Kundennummer setzen.");
					final int customerId = this.getCustomerId(barcode);
					log(LogService.LOG_INFO, "Kunden suchen.");
					try
					{
						if (this.galserve.do_getkunde(customerId))
						{
							log(LogService.LOG_INFO, "Kunden gefunden; aktualisieren.");
							this.updatePosition(barcode, position);
						}
						else
						{
							msg = "Kundennummer \"" + customerId + "\" nicht vorhanden.";
							log(LogService.LOG_INFO, msg);
						}
					}
					catch(Exception e)
					{
						log(LogService.LOG_ERROR, e.getLocalizedMessage());
					}
					position.setSearchValue(null);
				}
				else
				{
					log(LogService.LOG_INFO, "Position mit Barcodedaten aktualisieren.");
					barcode.updatePosition(position);
					if (position.isEbook())
					{
						log(LogService.LOG_INFO, "Position mit EBookdaten aktualisieren.");
						this.updatePosition(barcode, position);
					}
					else
					{
						log(LogService.LOG_INFO, "Artikel suchen.");
						try
						{
							this.galserve.do_NSearch(barcode.getProductCode());
						
							if (((Boolean) this.galserve.gefunden()).booleanValue())
							{
								log(LogService.LOG_INFO, "Artikel gefunden; Position aktualisieren.");
								this.updatePosition(barcode, position);
								log(LogService.LOG_INFO, "Defaultwerte des Barcodes übernehmen, falls notwendig.");
								barcode.updatePosition(position);
							}
							else
							{
								if (position.isOrdered())
								{
									msg = barcode.getType().getArticle() + " " + barcode.getType() + " mit dem Code "
											+ barcode.getProductCode() + " konnte nicht gefunden werden.";
									status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), Topic.PROVIDER_QUERY.topic(), new Exception(msg));
									log(LogService.LOG_INFO, (msg));
								}
								else
								{
									msg = barcode.getType().getArticle() + " " + barcode.getType() + " mit dem Code "
											+ barcode.getProductCode() + " konnte nicht gefunden werden.\nBitte erfassen Sie die zusätzlich benötigten Daten manuell.";
									status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), Topic.PROVIDER_QUERY.topic(), new Exception(msg));
									log(LogService.LOG_INFO, (msg));
								}
							}
						}
						catch(Exception e)
						{
							msg = barcode.getType().getArticle() + " " + barcode.getType() + " mit dem Code "
									+ barcode.getProductCode() + " konnte nicht gefunden werden.\nBitte erfassen Sie die zusätzlich benötigten Daten manuell. " + e.getLocalizedMessage();
							status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.PROVIDER_QUERY.topic(), new Exception(msg));
							log(LogService.LOG_ERROR, (msg));
						}
					}
				}
				this.close();
			}
			else
			{
				if (barcode.getType().equals(Barcode.Type.CUSTOMER))
				{
					log(LogService.LOG_INFO, "Suchwert auf null setzen.");
					position.setSearchValue(null);
				}
				else
				{
					barcode.updatePosition(position);
				}
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.PROVIDER_QUERY.topic(), new Exception("Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " kann nicht hergestellt werden."));
			}
		}
		return status;
	}

	protected void setProduct(final Barcode barcode, final Position position)
	{
		position.setEbook(barcode.isEbook());
		position.setProduct(this.getProduct(barcode, position));
		position.setFromStock(((Boolean)this.galserve.lagerabholfach()).booleanValue());
//		if (barcode.getType().equals(Barcode.Type.ORDER))
//		{
//			position.setOrder(barcode.getProductCode());
//		}
//		else
//		{
//			position.setOrder(null);
//		}
		position.setProvider(Activator.getDefault().getConfiguration().getProviderId());
		position.setBookProvider(!((Boolean)this.galserve.nichtbuchen()).booleanValue());
		position.setProviderBooked(false);
		position.setOrdered(((Boolean)this.galserve.bestellt()).booleanValue());

		if (position.getPrice() == position.getReceipt().getSettlement().getSalespoint().getProposalPrice() || position.getPrice() == 0D)
		{
			final double price = ((Double)this.galserve.preis()).doubleValue();
			position.setPrice(price);
		}

		if (position.getQuantity() == position.getReceipt().getSettlement().getSalespoint().getProposalQuantity() || position.getQuantity() == 0)
		{
			final int quantity = ((Integer)this.galserve.menge()).intValue();
			position.setQuantity(quantity == 0 ? 1 : quantity);
		}
		position.setDiscountProhibited(((Boolean)this.galserve.keinrabatt()).booleanValue());
		if (position.isDiscountProhibited())
		{
			position.setDiscount(0D);
		}

		setExternalProductGroup(position);
		setTax(position);
		
		position.setOption(position.isOrdered() ? Position.Option.ORDERED : Position.Option.ARTICLE);
		this.setOrder(position);
	}

	private void setTax(Position position)
	{
		final String taxCode = this.galserve.mwst().toString();
		final PersistenceService persistenceService = this.persistenceService;
		if (persistenceService != null)
		{
			final TaxCodeMappingQuery mappingQuery = (TaxCodeMappingQuery) persistenceService.getCacheService()
						.getQuery(TaxCodeMapping.class);
			if (mappingQuery != null)
			{
				final TaxCodeMapping taxCodeMapping = mappingQuery.selectTaxCodeMappingByProviderAndCode(Activator.getDefault().getConfiguration().getProviderId(), taxCode);
				if (taxCodeMapping != null)
				{
					position.setCurrentTax(taxCodeMapping.getTax().getCurrentTax());
				}
			}
		}
	}
	
	private void setOrder(final Position position)
	{
		position.setOrdered(((Boolean)this.galserve.bestellt()).booleanValue());
		if (position.isOrdered())
		{
//			position.setOrder(this.galserve..bestnummer().toString());
			position.setOrderedQuantity(((Integer)this.galserve.menge()).intValue());
			if (position.getQuantity() > position.getOrderedQuantity())
			{
				position.setQuantity(position.getOrderedQuantity());
			}
			position.setFromStock(((Boolean)this.galserve.lagerabholfach()).booleanValue());
			position.getReceipt().setCustomer(this.updateCustomer(((Integer) this.galserve.kundennr()).intValue()));
			position.getReceipt().setCustomerCode(position.getReceipt().getCustomer().getId().toString());
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
			product.setCode(this.galserve.bestnummer().toString());
			position.setOrder(barcode.getProductCode());
		}
		else
		{
			product.setCode(barcode.getProductCode());
			position.setOrder(null);
		}
		product.setAuthor(this.galserve.autor().toString());
		product.setPublisher(this.galserve.verlag().toString());
		product.setTitle(this.galserve.titel().toString());
		return product;
	}

	private void setExternalProductGroup(Position position)
	{
		ExternalProductGroup externalProductGroup = null;
		String epgCode = this.galserve.wgruppe().toString();
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
	
	protected Customer updateCustomer(final int customerId)
	{
		final Customer customer = new Customer();
		customer.setAccount(((Double) this.galserve.nkundkonto()).doubleValue());
		customer.setAddress(this.galserve.cstrasse().toString());
		customer.setCity(this.galserve.cort().toString());
		customer.setCountry(this.galserve.cland().toString());
		customer.setEmail(this.galserve.cemail().toString());
		customer.setFax(this.galserve.ctelefax().toString());
		customer.setFirstname(this.galserve.cvorname().toString());
		customer.setHasAccount(((Boolean) this.galserve.lkundkarte()).booleanValue());
		customer.setId(Integer.valueOf(customerId));
		customer.setLastname(this.galserve.cnamE1().toString());
		customer.setLastname2(this.galserve.cnamE2().toString());
		customer.setLastname3(this.galserve.cnamE3().toString());
		customer.setMobile(this.galserve.cnatel().toString());
		customer.setPersonalTitle(this.galserve.canrede().toString());
		customer.setPhone(this.galserve.ctelefon().toString());
		customer.setPhone2(this.galserve.ctelefoN2().toString());
		customer.setSalutation(this.galserve.ctitel().toString());
		customer.setZip(this.galserve.cplz().toString());
		customer.setProviderId(Activator.getDefault().getConfiguration().getProviderId());
		return customer;
	}

	protected void setEbookProduct(final Barcode barcode, final Position position)
	{
		Product product = Product.newInstance(position);
		product.setCode(barcode.getProductCode());
		position.setProduct(product);
		position.setEbook(barcode.isEbook());
		position.setBookProvider(!((Boolean)this.galserve.nichtbuchen()).booleanValue());
		position.setFromStock(false);
		position.setOrder("");
		position.setProvider(Activator.getDefault().getConfiguration().getProviderId());
		position.setProviderBooked(false);
		position.setOrdered(false);
		position.setPrice(position.getReceipt().getSettlement().getSalespoint().getProposalPrice());
		position.setQuantity(position.getReceipt().getSettlement().getSalespoint().getProposalQuantity());

		if (persistenceService != null)
		{
			CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) persistenceService.getCacheService().getQuery(CommonSettings.class);
			CommonSettings commonSettings = commonSettingsQuery.findDefault();
			ProductGroup productGroup = commonSettings.getEBooks();
			position.setProductGroup(productGroup);
			Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(Activator.getDefault().getConfiguration().getProviderId());
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
	public IStatus start()
	{
		this.status = super.start();
		try
		{
			this.galserve = ClassFactory.creategdserve();
		}
		catch (Exception e)
		{
			this.status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Die Verbindung zu Warenbewirtschaftung kann nicht hergestellt werden.", e);
		}
		return this.status;
	}
	
	@Override
	public void stop()
	{
		if (this.galserve != null)
		{
			this.galserve = null;
		}
		super.stop();
		this.status = Status.CANCEL_STATUS;
	}

	public boolean open()
	{
		IProperty property = properties.get(GalileoProperty.DATABASE_PATH.key());
		String database = property.value();
		this.wasOpen = this.open;
		if (!this.open)
		{
			try
			{
				this.open = this.galserve.do_NOpen(database);
				if (!this.open)
				{
					this.status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), new Exception("Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " kann nicht hergestellt werden."));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), e);
			}
		}
		return this.open;
	}

	public void close()
	{
		IProperty property = properties.get(GalileoProperty.KEEP_CONNECTION.key());
		int keepConnection = Integer.valueOf(property.value()).intValue();
		if (!this.wasOpen && this.open && keepConnection == 0)
		{
			this.galserve.do_NClose();
			this.open = false;
			this.wasOpen = false;
		}
	}

}
