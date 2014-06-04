/*
 * Created on 18.07.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve.sql;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.TaxCodeMappingQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.galileo.galserve.AbstractUpdateProviderServer;
import ch.eugster.colibri.provider.galileo.galserve.IUpdateProviderServer;

public class UpdateProviderServerSqlCom4j extends AbstractUpdateProviderServer implements IUpdateProviderServer
{
	private Igdserve2g galserve;
	
	public UpdateProviderServerSqlCom4j(PersistenceService persistenceService, Map<String, IProperty> properties)
	{
		super(persistenceService, properties);
	}
	
	@Override
	protected boolean getBookProvider()
	{
		return !((Boolean)this.galserve.nichtbuchen()).booleanValue();
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

	protected boolean doGetCustomer(Integer customerId)
	{
		return this.galserve.do_getkunde(customerId);
	}

	protected boolean doSearch(Barcode barcode)
	{
		return this.galserve.do_NSearch(barcode.getProductCode());
	}
	
	protected IStatus galileoTransactionWritten()
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		final String msg = "Galileo: Transaktion geschrieben.";

		try
		{
			final Boolean result = (Boolean)this.galserve.vtranswrite();
			if (result.booleanValue())
			{
				log(LogService.LOG_INFO, msg);
			}
			else
			{
				status = new Status(IStatus.WARNING, Activator.getDefault().getBundle().getSymbolicName(), msg);
				log(LogService.LOG_WARNING, msg + " FEHLER!");
			}
		}
		catch(Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), msg, e);
			log(LogService.LOG_ERROR, msg + " FEHLER: " + e.getLocalizedMessage());
		}

		return status;
	}

	protected boolean doPayInvoice(Integer invoiceNumber)
	{
		return this.galserve.do_BucheRechnung(invoiceNumber.intValue());
	}

	protected String getInvoiceError()
	{
		String error = (String) this.galserve.crgerror();
		return error == null ? "" : error;
	}
	
	protected IStatus setProviderValues(final Position position)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		try
		{
			this.galserve.vbestellt(position.isOrdered());
			this.galserve.vcouponnr(position.getReceipt().getNumber().toString());
			this.galserve.vkundennr(this.getGalileoCustomerCode(position.getReceipt()));
			this.galserve.vlagerabholfach(position.isFromStock());
			this.galserve.vmenge(Math.abs(position.getQuantity()));
			this.galserve.vpreis(Math.abs(position.getPrice()));
			this.galserve.vebook(Boolean.valueOf(position.isEbook()));
			this.galserve.vmwst(getTaxCode(position));
			this.galserve.vrabatt(-Math.abs(position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
					Position.AmountType.DISCOUNT)));

			if (position.getProduct() == null)
			{
				this.galserve.vwgname(cut(position.getProductGroup().getName(), 30));
				this.galserve.vwgruppe(cut(position.getProductGroup().getCode(), 2));
			}
			else
			{
				this.galserve.vnummer(position.getProduct().getCode());
				if (position.getOption().equals(Position.Option.PAYED_INVOICE))
				{
					this.galserve.vwgname(cut(position.getProductGroup().getName(), 30));
					this.galserve.vwgruppe(cut(position.getProductGroup().getCode(), 2));
				}
				else
				{
					if (position.getProduct().getExternalProductGroup() == null)
					{
						ExternalProductGroup epg = this.getDefaultExternalProductGroup(position);
						position.getProduct().setExternalProductGroup(epg);
					}
					this.galserve.vwgname(cut(position.getProduct().getExternalProductGroup().getText(), 30));
					this.galserve.vwgruppe(cut(position.getProduct().getExternalProductGroup().getCode(), 3));
					try
					{
						TaxCodeMapping taxCodeMapping = position.getCurrentTax().getTax().getTaxCodeMapping(Activator.getDefault().getConfiguration().getProviderId());
						if (taxCodeMapping.isDeleted())
						{
							throw new NullPointerException();
						}
						this.galserve.vmwst(taxCodeMapping.getCode());
					}
					catch (NullPointerException e)
					{
//						String msg = "Beim Versuch, die Warenbewirtschaftung zu aktualisieren, ist ein Fehler aufgetreten (Fehler Mwst-Mapping ungültig).";
					}
				}
			}
		}
		catch(Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), new Exception("Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " kann nicht hergestellt werden."));
		}
		return status;
	}

	protected boolean doUpdateOrdered(String order, int quantity)
	{
		return this.galserve.do_delabholfach(order, Math.abs(quantity));
	}
	
	protected void updateCustomerAccount(final Position position)
	{
		if (position.getReceipt().getCustomer() != null && position.getReceipt().getCustomer().getHasAccount())
		{
			try
			{
				position.getReceipt().getCustomer().setAccount(((Double)this.galserve.nkundkonto()).doubleValue());
			}
			catch(Exception e)
			{
			}
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
			position.setOrder(barcode.getCode());
		}
		else
		{
			product.setCode(barcode.getProductCode());
		}
		product.setAuthor(this.galserve.autor().toString());
		product.setPublisher(this.galserve.verlag().toString());
		product.setTitle(this.galserve.titel().toString());
		return product;
	}

	protected void setProduct(final Barcode barcode, final Position position)
	{
		position.setEbook(barcode.isEbook());
		position.setProduct(this.getProduct(barcode, position));
		position.setFromStock(((Boolean)this.galserve.lagerabholfach()).booleanValue());
		if (barcode.getType().equals(Barcode.Type.ORDER))
		{
			position.setOrder(barcode.getCode());
		}
		else
		{
			position.setOrder(null);
		}
		position.setProvider(Activator.getDefault().getConfiguration().getProviderId());
		position.setBookProvider(!((Boolean)this.galserve.nichtbuchen()).booleanValue());
		position.setProviderBooked(false);
		position.setOrdered(((Boolean)this.galserve.bestellt()).booleanValue());

		if (position.getPrice() == position.getReceipt().getSettlement().getSalespoint().getProposalPrice())
		{
			final double price = ((Double)this.galserve.preis()).doubleValue();
			if (position.getPrice() != price)
			{
				position.setPrice(price);
			}
		}

		if (position.getQuantity() == position.getReceipt().getSettlement().getSalespoint().getProposalQuantity())
		{
			final int quantity = ((Integer)this.galserve.menge()).intValue();
			if (position.getQuantity() == 0)
			{
				position.setQuantity(quantity);
			}
		}

		setExternalProductGroup(position);
		setTax(position);
		
		position.setOption(position.isOrdered() ? Position.Option.ORDERED : Position.Option.ARTICLE);
		this.setOrder(position);
	}

	private void setOrder(final Position position)
	{
		position.setOrdered(((Boolean)this.galserve.bestellt()).booleanValue());
		if (position.isOrdered())
		{
			position.setOrder(this.galserve.bestnummer().toString());
			position.setFromStock(((Boolean)this.galserve.lagerabholfach()).booleanValue());
			position.getReceipt().setCustomer(this.updateCustomer(((Integer) this.galserve.kundennr()).intValue()));
			position.getReceipt().setCustomerCode(position.getReceipt().getCustomer().getId().toString());
		}
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
	
	private void setTax(Position position)
	{
		final String taxCode = this.galserve.mwst().toString();
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
	
	@Override
	public IStatus start()
	{
		this.status = super.start();
		try
		{
			this.galserve = ClassFactory.creategdserve2g();
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
		close(false);
	}

	public void close(boolean force)
	{
		IProperty property = properties.get(GalileoProperty.KEEP_CONNECTION.key());
		int keepConnection = Integer.valueOf(property.value()).intValue();
		if (this.open && (force || (!this.wasOpen && keepConnection == 0)))
		{
			this.galserve.do_NClose();
			this.open = false;
			this.wasOpen = false;
		}
	}

	protected boolean doSellArticle(String code) throws Exception
	{
		return this.galserve.do_verkauf(code);
	}

	protected boolean doReverseArticle(String code) throws Exception
	{
		return this.galserve.do_storno(code);
	}

	protected boolean doSellProductGroup() throws Exception
	{
		return this.galserve.do_wgverkauf();
	}

	protected boolean doReverseProductGroup() throws Exception
	{
		return this.galserve.do_wgstorno();
	}

}
