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
import ch.eugster.colibri.persistence.model.CurrentTaxCodeMapping;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.TaxQuery;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.ProviderState;
import ch.eugster.colibri.provider.galileo.Activator;

public abstract class AbstractUpdateProviderServer extends AbstractGalileoServer
{
	public AbstractUpdateProviderServer(PersistenceService persistenceService, Map<String, IProperty> properties)
	{
		super(persistenceService, properties);
	}

	public IStatus updateProvider(final Position position)
	{
		this.status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		if (isConnect())
		{
			if (position.isBookProvider())
			{
				if (this.open())
				{
					Barcode barcode = null;
					/*
					 * Zuerst Receipt und Position aktualisieren, falls sie im Failovermodus erfasst worden ist.
					 */
					if (position.getReceipt().getCustomer() == null && position.getReceipt().getCustomerCode() != null && position.getReceipt().getCustomerCode().length() > 0)
					{
						barcode = this.createCustomerBarcode(position.getReceipt().getCustomerCode());
						if (barcode != null)
						{
							int customerId = this.getCustomerId(barcode);
							if (customerId > 0)
							{
								try
								{
									if (doGetCustomer(customerId))
									{
										this.updatePosition(barcode, position);
									}
									else
									{
										String msg = "Kundennummer \"" + customerId + "\" nicht vorhanden.";
										log(LogService.LOG_INFO, msg);
									}
								}
								catch(Exception e)
								{
									String msg = "Fehler beim Aufruf von do_getkunde: " + e.getLocalizedMessage();
									log(LogService.LOG_ERROR, msg);
								}
							}
						}
					}
					if (position.getSearchValue() != null && position.getProduct() == null)
					{
						BarcodeVerifier[] verifiers = this.getBarcodeVerifiers();
						for (BarcodeVerifier verifier : verifiers)
						{
							barcode = verifier.verify(position.getSearchValue());
							if (barcode != null)
							{
								break;
							}
						}
						if (barcode != null)
						{
							barcode.updatePosition(position);
							if (position.isEbook())
							{
								this.updatePosition(barcode, position);
							}
							else
							{
								try
								{
									if (doSearch(barcode))
									{
										this.updatePosition(barcode, position);
										barcode.updatePosition(position);
									}
									else
									{
										String msg = barcode.getType().getArticle() + " " + barcode.getType() + " mit dem Code "
												+ barcode.getProductCode() + " konnte nicht gefunden werden.\nBitte erfassen Sie die zusätzlich benötigten Daten manuell.";
										status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), msg);
										log(LogService.LOG_INFO, (msg));
									}
								}
								catch(Exception e)
								{
									String msg = "Fehler beim Aufruf von do_NSearch: " + e.getLocalizedMessage();
									status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), msg, e);
									log(LogService.LOG_ERROR, (msg));
								}
							}
						}
					}
					/*
					 * Nur verbuchen, wenn Flag isBookProvider gesetzt
					 */
					try
					{
						if (position.isProviderBooked())
						{
							/*
							 * Wenn die Position im Provider bereits verbucht ist, kann nur
							 * noch eine Rückbuchung über das Stornieren einer Rechnung
							 * erfolgen.
							 */
							if (position.getReceipt().getState().equals(Receipt.State.REVERSED))
							{
								if (position.getProduct() == null)
								{
									log(LogService.LOG_INFO, "Rückbuchung " + position.getProductGroup().getCode() + ".");
									status = this.reverseProductGroup(position);
								}
								else
								{
									if (position.getOption().equals(Position.Option.ARTICLE)
											|| position.getOption().equals(Position.Option.ORDERED))
									{
										log(LogService.LOG_INFO, "Rückbuchung " + position.getProduct().getCode() + ".");
										status = this.reverseArticle(position);
									}
								}
							}
						}
						else
						{
							/*
							 * Wenn die Position im Provider noch nicht verbucht ist, dann
							 * muss sie nun erfolgen, vorausgesetzt, der Status des Belegs
							 * ist <code>Receipt.Type.SAVED</code>
							 */
							if (position.getReceipt().getState().equals(Receipt.State.SAVED))
							{
								if (position.getProduct() == null)
								{
									log(LogService.LOG_INFO, "Verbuche Warengruppe " + position.getProductGroup().getCode() + ".");
									status = this.sellProductGroup(position);
								}
								else
								{
									if (position.getProduct() != null && position.getProduct().getInvoiceNumber() != null)
									{
										position.setOption(Option.PAYED_INVOICE);
									}
									switch (position.getOption())
									{
										case ARTICLE:
										{
											log(LogService.LOG_INFO, "Verbuche Artikel " + position.getProduct().getCode() + ".");
											status = this.sellArticle(position);
											break;
										}
										case ORDERED:
										{
											log(LogService.LOG_INFO, "Verbuche Bestellung " + position.getOrder() + ".");
											status = this.storeOrder(position);
											break;
										}
										case PAYED_INVOICE:
										{
											log(LogService.LOG_INFO, "Verbuche bezahlte Rechnung " + position.getProduct().getInvoiceNumber() + ".");
											status = this.payInvoice(position);
											break;
										}
									}
								}
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						this.close();
					}
				}
				else
				{
					status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), new Exception("Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " kann nicht hergestellt werden."));
				}
			}
		}
		return status;
	}

	protected abstract boolean doSearch(Barcode barcode);
	
	protected abstract boolean doGetCustomer(Integer customerId);
	
	protected abstract boolean getBookProvider();

	protected abstract boolean doSellArticle(String code) throws Exception;
	
	protected abstract boolean doReverseArticle(String code) throws Exception;
	
	protected abstract boolean doSellProductGroup() throws Exception;
	
	protected abstract boolean doReverseProductGroup() throws Exception;
	
	protected abstract String getInvoiceError();
	
	protected abstract boolean doPayInvoice(Integer invoiceNumber);
	
	protected abstract IStatus setProviderValues(Position position);
	
	protected abstract void updateCustomerAccount(Position position);
	
	protected abstract IStatus galileoTransactionWritten();

	protected abstract boolean doUpdateOrdered(String order, int quantity);

	protected IStatus sellArticle(final Position position)
	{
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			if (position.getQuantity() < 0)
			{
				/*
				 * Eine negative Menge bedeutet die Rücknahme eines Artikels, damit
				 * muss dieser storniert werden.
				 */
				try
				{
					if ((ProviderState.BOOKED & position.getProviderState()) == 0)
					{
						if (doReverseArticle(position.getCode()))
						{
							status = update(position, true, ProviderState.BOOKED | position.getProviderState(), "do_storno", status, true);
						}
						else
						{
							status = warn(position, ProviderState.BOOK_ERROR | position.getProviderState(), "do_storno", "");
						}
					}
				}
				catch(Exception e)
				{
					status = error(position, e, "do_storno");
				}
			}
			else
			{
				/*
				 * Eine positive Menge bedeutet den Verkauf eines Artikels.
				 */
				try
				{
					if ((ProviderState.BOOKED & position.getProviderState()) == 0)
					{
						if (doSellArticle(position.getCode()))
						{
							status = update(position, true, ProviderState.BOOKED | position.getProviderState(), "do_verkauf", status, true);
						}
						else
						{
							status = warn(position, ProviderState.BOOK_ERROR | position.getProviderState(), "do_verkauf", "");
						}
					}
				}
				catch(Exception e)
				{
					status = error(position, e, "do_verkauf");
				}
			}
		}
		return status;
	}

	protected IStatus reverseArticle(final Position position)
	{
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			if (position.getQuantity() < 0)
			{
				/*
				 * Eine negative Menge bedeuted die Rücknahme eines Artikels, dieser
				 * muss beim Stornieren einer Rechnung wieder 'verkauft' werden.
				 */
				try
				{
//					if ((position.getProviderState() & ProviderState.BOOKED) != 0)
//					{
						if (doSellArticle(position.getCode()))
						{
							status = update(position, false, ProviderState.BOOKED ^ position.getProviderState(), "do_verkauf", status, true);
						}
						else
						{
							status = warn(position, position.getProviderState() | ProviderState.BOOK_ERROR, "do_verkauf", "");
						}
//					}
				}
				catch(Exception e)
				{
					status = error(position, e, "do_verkauf");
				}
			}
			else
			{
				try
				{
//					if ((position.getProviderState() & ProviderState.BOOKED) != 0)
//					{
						if (doReverseArticle(position.getCode()))
						{
							status = update(position, false, ProviderState.BOOKED ^ position.getProviderState(), "do_storno", status, true);
						}
						else
						{
							status = warn(position, ProviderState.BOOK_ERROR | position.getProviderState(), "do_storno", "");
						}
//					}
				}
				catch(Exception e)
				{
					status = error(position, e, "do_storno");
				}
			}

		}
		return status;
	}

	protected IStatus sellProductGroup(final Position position)
	{
		this.setProviderValues(position);

		if (position.getQuantity() < 0)
		{
			/*
			 * Eine negative Menge bedeutet die Rückbuchung eines Artikels,
			 * dieser muss somit storniert werden
			 */
			try
			{
				if ((ProviderState.BOOKED & position.getProviderState()) == 0)
				{
					if (doReverseProductGroup())
					{
						status = update(position, true, ProviderState.BOOKED | position.getProviderState(), "do_wgstorno", status, true);
					}
					else
					{
						warn(position, ProviderState.BOOK_ERROR | position.getProviderState(), "do_wgstorno", "");
					}
				}
			}
			catch(Exception e)
			{
				status = error(position, e, "do_wgstorno");
			}
		}
		else
		{
			try
			{
				if ((ProviderState.BOOKED & position.getProviderState()) == 0)
				{
					if (doSellProductGroup())
					{
						status = update(position, true, ProviderState.BOOKED | position.getProviderState(), "do_wgverkauf", status, true);
					}
					else
					{
						warn(position, ProviderState.BOOK_ERROR | position.getProviderState(), "do_wgverkauf", "");
					}
				}
			}
			catch(Exception e)
			{
				status = error(position, e, "do_wgverkauf");
			}
		}
		return status;
	}

	protected IStatus reverseProductGroup(final Position position)
	{
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			if (position.getQuantity() < 0)
			{
				/*
				 * Bei negativer Menge (Ursprünglich eine Rückbuchung eines
				 * Artikels, muss dieser nun wieder 'verkauft' werden
				 */
				try
				{
					if ((position.getProviderState() & ProviderState.BOOKED) != 0)
					{
						if (doSellProductGroup())
						{
							status = update(position, false, ProviderState.BOOKED ^ position.getProviderState(), "do_wgverkauf", status, true);
						}
						else
						{
							status = warn(position, ProviderState.BOOK_ERROR | position.getProviderState(), "do_wgverkauf", "");
						}
					}
				}
				catch(Exception e)
				{
					status = error(position, e, "do_wgverkauf");
				}
			}
			else
			{
				try
				{
					int state = position.getProviderState() & ProviderState.BOOKED;
					if (state != 0)
					{
						if (doReverseProductGroup())
						{
							status = update(position, false, ProviderState.BOOKED ^ position.getProviderState(), "do_wgstorno", status, true);
						}
						else
						{
							status = warn(position, ProviderState.BOOK_ERROR | position.getProviderState(), "do_wgstorno", "");
						}
					}
				}
				catch(Exception e)
				{
					status = error(position, e, "do_wgstorno");
				}
			}
		}
		return status;
	}

	protected String cut(String value, int maxlength)
	{
		if (value == null)
		{
			return "";
		}
		return value.length() > maxlength ? value.substring(0, maxlength) : value;
	}
	
	protected Integer getGalileoCustomerCode(final Receipt receipt)
	{
		Integer value = null;
		if (receipt.getCustomer() == null)
		{
			if (receipt.getCustomerCode().isEmpty())
			{
				log(LogService.LOG_INFO, "Keine Kundendaten mit Suche verbunden.");
				value = Integer.valueOf(0);
			}
			else
			{
				value = this.convert(receipt.getCustomerCode());
			}
		}
		else
		{
			value = this.convert(receipt.getCustomerCode());
		}
		return value == null ? Integer.valueOf(0) : value;
	}

	protected String getTaxCode(Position position)
	{
		String tax = "0";
		if (this.configuration.canMap(position.getCurrentTax()))
		{
			CurrentTaxCodeMapping mapping = position.getCurrentTax().getCurrentTaxCodeMapping(position.getProvider());
			if (mapping != null)
			{
				tax = mapping.getCode();
			}
		}
		else if (this.configuration.canMap(position.getCurrentTax().getTax()))
		{
			TaxCodeMapping mapping = position.getCurrentTax().getTax().getTaxCodeMapping(position.getProvider());
			if (mapping != null)
			{
				tax = mapping.getCode();
			}
		}
		return tax;
	}
	
	protected void setEbookProduct(final Barcode barcode, final Position position)
	{
		Product product = Product.newInstance(position);
		product.setCode(barcode.getProductCode());
		position.setProduct(product);
		position.setEbook(barcode.isEbook());
		position.setBookProvider(getBookProvider());
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

	private IStatus storeOrder(final Position position) throws Exception
	{
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			status = this.sellArticle(position);
			if (position.getOrder() != null)
			{
				status = this.removeOrdered(position, status);
			}
		}
		return status;
	}

	protected IStatus payInvoice(final Position position) throws Exception
	{
		if (position.getProduct().getInvoiceNumber() != null)
		{
			position.setOption(Option.PAYED_INVOICE);
		}
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			final Integer invoiceNumber = this.convert(position.getProduct().getInvoiceNumber());
			if (invoiceNumber != null)
			{
				try
				{
					if ((position.getProviderState() & ProviderState.INVOICE_PAYED) == 0)
					{
						if (doPayInvoice(invoiceNumber))
						{
							status = update(position, true, ProviderState.INVOICE_PAYED | position.getProviderState(), "do_bucheRechnung", status, false);
						}
						else
						{
							final String msg = getInvoiceError();
							status = warn(position, ProviderState.PAY_INVOICE_ERROR | position.getProviderState(), "do_bucheRechnung", msg);
						}
					}
				}
				catch(Exception e)
				{
					status = error(position, e, "do_bucheRechnung");
				}
			}
			else
			{
				final String msg = "Rechnungsnummer nicht vorhanden, Rechnung kann nicht verbucht werden.";
				log(LogService.LOG_INFO, msg);
			}
		}
		return status;
	}
	
	protected IStatus removeOrdered(Position position, IStatus status)
	{
		if (status.getSeverity() == IStatus.OK)
		{
			try
			{
				if ((position.getProviderState() & ProviderState.ORDER_DELETED) == 0)
				{
					if (doUpdateOrdered(position.getOrder(), Math.abs(position.getQuantity())))
					{
						status = update(position, true, position.getProviderState() | ProviderState.ORDER_DELETED, "do_delAbholfach", status, false);
					}
					else
					{
						status = warn(position, ProviderState.DELETE_ORDER_ERROR | position.getProviderState(), "do_delAbholfach", "");
					}
				}
			}
			catch(Exception e)
			{
				status = error(position, e, "do_delAbholfach");
			}
		}
		return status;
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
		position.getReceipt().setCustomer(this.updateCustomer(this.getCustomerId(barcode)));
	}

	protected abstract Customer updateCustomer(final int customerId);

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
	
	protected Barcode createCustomerBarcode(String code)
	{
		int currentSize = code.length() + Barcode.PREFIX_CUSTOMER.length();
		StringBuilder builder = new StringBuilder();
		for (int i = currentSize; i < 12; i++)
		{
			builder = builder.append("0");
		}
		String customerCode = Barcode.PREFIX_CUSTOMER + builder.toString() + code;
		BarcodeVerifier[] verifiers = this.getBarcodeVerifiers();
		Barcode barcode = null;
		for (BarcodeVerifier verifier : verifiers)
		{
			barcode = verifier.verify(customerCode);
			if (barcode != null)
			{
				break;
			}
		}
		return barcode;
	}
	
	protected Integer convert(final String code)
	{
		if (code == null)
		{
			log(LogService.LOG_INFO, "Kein Code vorhanden.");
			return null;
		}

		Integer id = null;
		try
		{
			id = Integer.valueOf(code);
			log(LogService.LOG_INFO, "Code: " + code + " konvertiert.");
		}
		catch (final NumberFormatException e)
		{
			log(LogService.LOG_ERROR, "Code: " + code + " konnte nicht konvertiert werden (ungültiger Datentyp).");
		}
		return id;
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

	private  IStatus warn(Position position, int state, String method, String error)
	{
		String msg = "Beleg " + position.getReceipt().getNumber() + ": Fehler beim Verbuchen in " + this.configuration.getName() + " (" + method + "().";
		position.setProviderState(state);
		log(LogService.LOG_WARNING, msg);
		return new Status(IStatus.WARNING, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), new Exception(msg));
	}
	
	private  IStatus error(Position position, Exception e, String method)
	{
		status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), e);
		log(LogService.LOG_ERROR, "Galileo: " + method + "() für " + position.getSearchValue() + " aufgerufen... Fehler: " + e.getLocalizedMessage());
		return status;
	}
	
	private IStatus update(Position position, boolean booked, int state, String method, IStatus status, boolean writeTransaction)
	{
		if (writeTransaction)
		{
			status = this.galileoTransactionWritten();
		}
		position.setProviderBooked(booked);
		position.setProviderState(state);
		this.updateCustomerAccount(position);
		log(LogService.LOG_INFO, "Galileo: " + method + "() für " + position.getProductGroup().getName() + " aufgerufen... Ok!");
		return status;
	}
	
	protected abstract void setProduct(Barcode barcode, Position position);
}
