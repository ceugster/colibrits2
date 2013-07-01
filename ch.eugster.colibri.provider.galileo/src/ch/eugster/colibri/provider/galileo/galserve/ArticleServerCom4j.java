/*
 * Created on 18.07.2008
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
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Receipt;
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
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.Property;
import ch.eugster.colibri.provider.service.ProviderInterface;

import com4j.ComException;

public class ArticleServerCom4j implements IArticleServer
{
	private Igdserve galserve;

	private GalileoConfiguration configuration;

	private String database;

	private boolean keepConnection;

	private String bibwinPath;

	private boolean searchCd;

	private boolean open;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private IStatus status;

	public ArticleServerCom4j()
	{
		this.status = Status.OK_STATUS;
	}

	@Override
	public IStatus deleteOrdered(final Position position)
	{
		if (position.isOrdered())
		{
			if (this.open())
			{
				LogService log = (LogService) this.logServiceTracker.getService();
				if (log != null)
				{
					log.log(LogService.LOG_INFO, "Sende Nachricht an Warenbewirtschaftung: Lösche Artikel mit Bestellnummer " + position.getOrder() + ".");
				}
				if (!this.galserve.do_delabholfach(position.getOrder(), position.getQuantity()))
				{
					this.status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Die Bestellung " + position.getOrder()
							+ " konnte nicht gelöscht werden.");
				}
				if (log != null)
				{
					log.log(LogService.LOG_INFO, "Bestätigung aus Warenbewirtschaftung: " + (status.getSeverity() == IStatus.OK ? " Artikel gelöscht." : "Artikel konnte nicht gelöscht werden."));
				}
				this.close();
			}
		}

		return this.status;
	}

	@Override
	public IStatus findAndRead(final Barcode barcode, final Position position)
	{
		this.status = Status.OK_STATUS;
		
		String msg = null;

		if (this.open())
		{
			LogService log = (LogService) this.logServiceTracker.getService();
			if (barcode.getType().equals(Barcode.Type.CUSTOMER))
			{
				final int customerId = this.getCustomerId(barcode);
				if (this.galserve.do_getkunde(customerId))
				{
					this.updatePosition(barcode, position);
				}
				else
				{
					msg = "Kundennummer \"" + customerId + "\" nicht vorhanden.";
					if (log != null)
					{
						log.log(LogService.LOG_INFO, msg);
					}
				}
			}
			else
			{
				barcode.updatePosition(position);
				if (position.isEbook())
				{
					this.updatePosition(barcode, position);
				}
				else
				{
					if (this.galserve.do_NSearch(barcode.getProductCode()))
					{
						this.updatePosition(barcode, position);
						barcode.updatePosition(position);
					}
					else
					{
						msg = barcode.getType().getArticle() + " " + barcode.getType() + " mit dem Code "
								+ barcode.getProductCode() + " konnte nicht gefunden werden.";
						this.status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, msg);
						if (log != null)
						{
							log.log(LogService.LOG_INFO, (msg));
						}
					}
				}
			}

			this.close();
		}
		
		return this.status;
	}

	@Override
	public String getBibIniPath() throws Exception
	{
		final StringBuilder bibini = new StringBuilder("");

		if (this.open())
		{
			bibini.append(this.galserve.cbibini().toString());
	
			this.close();
		}
		
		return bibini.toString();
	}

	@Override
	public Customer selectCustomer(final Barcode barcode) throws Exception
	{
		Customer customer = null;

		if (this.open())
		{
			final LogService logService = (LogService) this.logServiceTracker.getService();

			final int customerId = this.getCustomerId(barcode);
			if (logService != null)
			{
				logService.log(LogService.LOG_INFO, "Suche Kunden " + customerId + ".");
			}
			if (this.galserve.do_getkunde(customerId))
			{
				customer = this.getCustomer(customerId);
			}
			if (logService != null)
			{
				logService.log(LogService.LOG_INFO, (customer == null ? "Kunden " + customerId + " nicht gefunden." : "Kunden gefunden."));
			}
	
			this.close();
		}
		
		return customer;
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
		this.logServiceTracker.close();
		this.status = Status.CANCEL_STATUS;
	}

	@Override
	public IStatus updateProvider(final Position position)
	{
		IStatus status = this.status;

		if (position.isBookProvider())
		{
			if (this.open())
			{
				final LogService logService = (LogService) this.logServiceTracker.getService();
				/*
				 * Nur verbuchen, wenn Flag isBookProvider gesetzt
				 */
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
							if (logService != null)
							{
								logService.log(LogService.LOG_INFO, "Rückbuchung " + position.getProductGroup().getCode() + ".");
							}
							status = this.reverseProductGroup(position);
						}
						else
						{
							if (position.getOption().equals(Position.Option.ARTICLE)
									|| position.getOption().equals(Position.Option.ORDERED))
							{
								if (logService != null)
								{
									logService.log(LogService.LOG_INFO, "Rückbuchung " + position.getProduct().getCode() + ".");
								}
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
							if (logService != null)
							{
								logService.log(LogService.LOG_INFO, "Verbuche Warengruppe " + position.getProductGroup().getCode() + ".");
							}
							status = this.sellProductGroup(position);
						}
						else
						{
							switch (position.getOption())
							{
								case ARTICLE:
								{
									if (logService != null)
									{
										logService.log(LogService.LOG_INFO, "Verbuche Artikel " + position.getProduct().getCode() + ".");
									}
									status = this.sellArticle(position);
									break;
								}
								case ORDERED:
								{
									if (logService != null)
									{
										logService.log(LogService.LOG_INFO, "Verbuche Bestellung " + position.getOrder() + ".");
									}
									status = this.storeOrder(position);
									break;
								}
								case PAYED_INVOICE:
								{
									if (logService != null)
									{
										logService.log(LogService.LOG_INFO, "Verbuche bezahlte Rechnung " + position.getProduct().getInvoiceNumber() + ".");
									}
									status = this.payInvoice(position);
									break;
								}
							}
						}
					}
				}
	
				this.close();
			}
		}

		return status;
	}

	private void close()
	{
		if (this.open && !this.keepConnection)
		{
			this.galserve.do_NClose();
			this.open = false;
		}
	}

	private Integer convert(final String code)
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();

		if (code == null)
		{
			if (logService != null)
			{
				logService.log(LogService.LOG_INFO, "Kein Code vorhanden.");
			}
			return null;
		}

		Integer id = null;
		try
		{
			id = Integer.valueOf(code);
			if (logService != null)
			{
				logService.log(LogService.LOG_INFO, "Code: " + code + " konvertiert.");
			}
		}
		catch (final NumberFormatException e)
		{
			if (logService != null)
			{
				logService.log(LogService.LOG_INFO, "Code: " + code + ". konnte nicht konvertiert werden (ungültiger Datentyp).");
			}
		}
		return id;
	}

	private IStatus galileoTransactionWritten()
	{
		this.status = new Status(IStatus.OK, Activator.PLUGIN_ID, ProviderInterface.Topic.ARTICLE_UPDATE.topic());
		final LogService logService = (LogService) this.logServiceTracker.getService();
		final String msg = "Galileo: Transaktion geschrieben.";

		final Boolean result = (Boolean)this.galserve.vtranswrite();
		if (result.booleanValue())
		{
			if (logService != null)
			{
				logService.log(LogService.LOG_INFO, msg);
			}
		}
		else
		{
			this.status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, msg);
			if (logService != null)
			{
				logService.log(LogService.LOG_INFO, msg + " FEHLER!");
			}
		}

		return this.status;
	}

	private Customer getCustomer(final int customerId)
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
		customer.setProviderId(this.configuration.getProviderId());
		return customer;
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

	private int getCustomerId(final Barcode barcode)
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

	private Integer getGalileoCustomerCode(final Receipt receipt)
	{
		Integer value = null;
		if (receipt.getCustomer() == null)
		{
			if (receipt.getCustomerCode().isEmpty())
			{
				final LogService logService = (LogService) this.logServiceTracker.getService();
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, "Keine Kundendaten mit Suche verbunden.");
				}
				value = Integer.valueOf(0);
			}
			else
			{
				value = this.convert(receipt.getCustomerCode());
			}
		}
		else
		{
			value = this.convert(receipt.getCustomer().getCode());
		}
		return value == null ? Integer.valueOf(0) : value;
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

	private boolean open()
	{
		if ((this.galserve == null) || (this.status.getSeverity() == IStatus.ERROR))
		{
			this.status = this.start();
		}

		if (this.status.getSeverity() != IStatus.ERROR)
		{
			if (!this.open)
			{
				try
				{
					this.open = this.galserve.do_NOpen(this.database);
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

	@Override
	public IStatus checkConnection(String path)
	{
		IStatus status = null;
		Igdserve galserve = ClassFactory.creategdserve();
		if (galserve.do_NOpen(path))
		{
			galserve.do_NClose();
			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Die Verbindung zur Warenbewirtschaftung Galileo konnte erfolgreich hergestellt werden.");
		}
		else
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Das Herstellen der Verbindung zur Warenbewirtschaftung Galileo ist fehlgeschlagen.");
		}
		galserve.dispose();
		return status;
	}

	private IStatus payInvoice(final Position position)
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();

		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			final Integer invoiceNumber = this.convert(position.getProduct().getInvoiceNumber());
			if (invoiceNumber != null)
			{
				if (this.galserve.do_BucheRechnung(invoiceNumber))
				{
					position.setProviderBooked(true);
					this.updateCustomerAccount(position);
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: Rechnung " + position.getProduct().getInvoiceNumber()
								+ " verbuchen... Ok.");
					}
				}
				else
				{
					final String msg = this.galserve.crgerror().toString();
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: Rechnung " + position.getProduct().getInvoiceNumber()
								+ " verbuchen... Fehler: " + msg);
					}
				}
			}
			else
			{
				final String msg = "Rechnungsnummer nicht vorhanden, Rechnung kann nicht verbucht werden.";
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, msg);
				}
			}
			status = this.galileoTransactionWritten();
		}
		return status;
	}

	private IStatus reverseArticle(final Position position)
	{
		LogService logService = (LogService) this.logServiceTracker.getService();

		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			if (position.getQuantity() < 0)
			{
				/*
				 * Eine negative Menge bedeuted die Rücknahme eines Artikels, dieser
				 * muss beim Stornieren einer Rechnung wieder 'verkauft' werden.
				 */
				if (this.galserve.do_verkauf(position.getCode()))
				{
					position.setProviderBooked(false);
					this.updateCustomerAccount(position);
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_verkauf() für " + position.getSearchValue()
								+ " aufgerufen... Ok!");
					}
				}
				else
				{
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_verkauf() für " + position.getSearchValue()
								+ " aufgerufen... Fehler!");
					}
				}
			}
			else
			{
				if (this.galserve.do_storno(position.getCode()))
				{
					position.setProviderBooked(false);
					this.updateCustomerAccount(position);
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_storno() für " + position.getSearchValue()
								+ " aufgerufen... Ok!");
					}
				}
				else
				{
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_storno() für " + position.getSearchValue()
								+ " aufgerufen... Fehler!");
					}
				}
			}

			status = this.galileoTransactionWritten();
		}
		return status;
	}

	private IStatus reverseProductGroup(final Position position)
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();

		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			if (position.getQuantity() < 0)
			{
				/*
				 * Bei negativer Menge (Ursprünglich eine Rückbuchung eines
				 * Artikels, muss dieser nun wieder 'verkauft' werden
				 */
				if (this.galserve.do_wgverkauf())
				{
					position.setProviderBooked(false);
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_wgverkauf() für " + position.getProductGroup().getName()
								+ " aufgerufen... Ok!");
					}
				}
				else
				{
					status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Beim Versuch, die Daten an die Warenbewirtschaftung zu übermitteln, ist ein Fehler aufgetreten (Galileo: do_wgverkauf()");
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, status.getMessage());
					}
				}
			}
			else
			{
				if (this.galserve.do_wgstorno())
				{
					position.setProviderBooked(false);
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_wgstorno() für " + position.getProductGroup().getName()
								+ " aufgerufen... Ok!");
					}
				}
				else
				{
					status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Beim Versuch, die Daten an die Warenbewirtschaftung zu übermitteln, ist ein Fehler aufgetreten (Galileo: do_wgverkauf()");
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, status.getMessage());
					}
				}
			}
			status = this.galileoTransactionWritten();
		}
		return status;
	}

	private IStatus sellArticle(final Position position)
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();

		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			if (position.getQuantity() < 0)
			{
				/*
				 * Eine negative Menge bedeutet die Rücknahme eines Artikels, damit
				 * muss dieser storniert werden.
				 */
				if (this.galserve.do_storno(position.getCode()))
				{
					position.setProviderBooked(true);
					this.updateCustomerAccount(position);
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_storno() für " + position.getSearchValue()
								+ " aufgerufen... Ok!");
					}
				}
				else
				{
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_storno() für " + position.getSearchValue()
								+ " aufgerufen... Fehler!");
					}
				}
			}
			else
			{
				/*
				 * Eine positive Menge bedeutet den Verkauf eines Artikels.
				 */
				if (this.galserve.do_verkauf(position.getCode()))
				{
					position.setProviderBooked(true);
					this.updateCustomerAccount(position);
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_verkauf() für " + position.getSearchValue() + " aufgerufen... Ok!");
					}
				}
				else
				{
					if (logService != null)
					{
						logService.log(LogService.LOG_INFO, "Galileo: do_verkauf() für " +
								position.getSearchValue() + " aufgerufen... FEHLER!");
					}
				}
			}
			return this.galileoTransactionWritten();
		}
		return status;
	}

	private IStatus sellProductGroup(final Position position)
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();

		this.setProviderValues(position);

		if (position.getQuantity() < 0)
		{
			/*
			 * Eine negative Menge bedeutet die Rückbuchung eines Artikels,
			 * dieser muss somit storniert werden
			 */
			if (this.galserve.do_wgstorno())
			{
				position.setProviderBooked(true);
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, "Galileo: do_wgstorno() für " + position.getProductGroup().getName()
							+ " aufgerufen... Ok!");
				}
			}
			else
			{
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, "Galileo: do_wgstorno() für " + position.getProductGroup().getName()
							+ " aufgerufen... FEHLER!");
				}
			}
		}
		else
		{
			boolean sold = this.galserve.do_wgverkauf();
			if (sold)
			{
				position.setProviderBooked(true);
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, "Galileo: do_wgverkauf() für " + position.getProductGroup().getName()
							+ " aufgerufen... Ok!");
				}
			}
			else
			{
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, "Galileo: do_wgverkauf() für " + position.getProductGroup().getName()
							+ " aufgerufen... Fehler!");
				}
			}
		}

		return this.galileoTransactionWritten();
	}

	private void setCustomer(final Barcode barcode, final Position position)
	{
		position.getReceipt().setCustomer(this.getCustomer(this.getCustomerId(barcode)));
	}

	private void setDiscount(final Position position, final boolean noDiscount)
	{
		if (!noDiscount)
		{
			double nachlass = 0D;
			Object object = this.galserve.nnachlass();
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
		position.setOrdered(((Boolean)this.galserve.bestellt()).booleanValue());
		if (position.isOrdered())
		{
			position.setOrder(this.galserve.bestnummer().toString());
			position.setFromStock(((Boolean)this.galserve.lagerabholfach()).booleanValue());
		}
	}

	private void setProduct(boolean ebook, Barcode barcode, Position position)
	{
		if (ebook)
		{
			setEbookProduct(barcode, position);
		}
		else
		{
			setProduct(barcode, position);
		}
	}

	private void setEbookProduct(final Barcode barcode, final Position position)
	{
		Product product = Product.newInstance(position);
		product.setCode(barcode.getProductCode());
		position.setProduct(product);
		position.setEbook(barcode.isEbook());
		position.setBookProvider(!((Boolean)this.galserve.nichtbuchen()).booleanValue());
		position.setFromStock(false);
		position.setOrder("");
		position.setProvider(this.configuration.getProviderId());
		position.setProviderBooked(false);
		position.setOrdered(false);
		position.setPrice(position.getReceipt().getSettlement().getSalespoint().getProposalPrice());
		position.setQuantity(position.getReceipt().getSettlement().getSalespoint().getProposalQuantity());

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) persistenceService.getCacheService().getQuery(CommonSettings.class);
			CommonSettings commonSettings = commonSettingsQuery.findDefault();
			ProductGroup productGroup = commonSettings.getDefaultProductGroup();
			position.setProductGroup(productGroup);
			Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(this.configuration.getProviderId());
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

	private void setProduct(final Barcode barcode, final Position position)
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
		position.setProvider(this.configuration.getProviderId());
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

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			String epgCode = this.galserve.wgruppe().toString();
			if (epgCode.isEmpty())
			{
				CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) persistenceService.getCacheService().getQuery(CommonSettings.class);
				CommonSettings commonSettings = commonSettingsQuery.findDefault();
				Collection<ProductGroupMapping> mappings = commonSettings.getDefaultProductGroup().getProductGroupMappings(this.configuration.getProviderId());
				if (!mappings.isEmpty())
				{
					epgCode = mappings.iterator().next().getExternalProductGroup().getCode();
				}
			}
			final ExternalProductGroupQuery query = (ExternalProductGroupQuery) persistenceService.getCacheService()
					.getQuery(ExternalProductGroup.class);
			final ExternalProductGroup externalProductGroup = query.selectByProviderAndCode(
					this.configuration.getProviderId(), epgCode);
			position.getProduct().setExternalProductGroup(externalProductGroup);

			final String taxCode = this.galserve.mwst().toString();
			final TaxCodeMappingQuery mappingQuery = (TaxCodeMappingQuery) persistenceService.getCacheService()
					.getQuery(TaxCodeMapping.class);
			if (mappingQuery != null)
			{
				final TaxCodeMapping taxCodeMapping = mappingQuery.selectTaxCodeMappingByProviderAndCode(this.configuration.getProviderId(), taxCode);
				if (taxCodeMapping != null)
				{
					position.setCurrentTax(taxCodeMapping.getTax().getCurrentTax());
				}
			}
		}

		position.setOption(position.isOrdered() ? Position.Option.ORDERED : Position.Option.ARTICLE);
		this.setOrder(position);

		final boolean noDiscount = ((Boolean)this.galserve.keinrabatt()).booleanValue();
		this.setDiscount(position, noDiscount);
	}

	private IStatus setProviderValues(final Position position)
	{
		IStatus status = Status.OK_STATUS;
		try
		{
			this.galserve.vbestellt(position.isOrdered());
			this.galserve.vcouponnr(position.getReceipt().getNumber().toString());
			this.galserve.vkundennr(this.getGalileoCustomerCode(position.getReceipt()));
			this.galserve.vlagerabholfach(position.isFromStock());
			this.galserve.vmenge(Math.abs(position.getQuantity()));
			this.galserve.vpreis(Math.abs(position.getPrice()));
			this.galserve.vebook(Boolean.valueOf(position.isEbook()));
			this.galserve.vrabatt(-Math.abs(position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
					Position.AmountType.DISCOUNT)));

			if (position.getProduct() == null)
			{
				this.galserve.vwgname(position.getProductGroup().getName());
				this.galserve.vwgruppe(position.getProductGroup().getCode());
			}
			else
			{
				this.galserve.vnummer(position.getProduct().getCode());
				if (position.getOption().equals(Position.Option.PAYED_INVOICE))
				{
					this.galserve.vwgname(position.getProductGroup().getName());
					this.galserve.vwgruppe(position.getProductGroup().getCode());
				}
				else
				{
					this.galserve.vwgname(position.getProduct().getExternalProductGroup().getText());
					this.galserve.vwgruppe(position.getProduct().getExternalProductGroup().getCode());
					try
					{
						TaxCodeMapping taxCodeMapping = position.getCurrentTax().getTax().getTaxCodeMapping(this.configuration.getProviderId());
						if (taxCodeMapping.isDeleted())
						{
							throw new NullPointerException();
						}
						this.galserve.vmwst(taxCodeMapping.getCode());
					}
					catch (NullPointerException e)
					{
						status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ProviderInterface.Topic.PROVIDER_TAX_NOT_SPECIFIED.topic(), e);
					}
				}
			}
		}
		catch(Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Beim Aktualisieren der Daten für die Warenbewirtschaftung ist ein Fehler aufgetreten.", e);
		}
		return status;
	}

	private IStatus start()
	{
		this.status = new Status(IStatus.OK, Activator.PLUGIN_ID, ProviderInterface.Topic.ARTICLE_UPDATE.topic());

		this.configuration = new GalileoConfiguration();

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(),
				LogService.class, null);
		this.logServiceTracker.open();

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			Map<String, ProviderProperty> properties = null;
			final SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getCacheService().getQuery(
					Salespoint.class);
			Salespoint salespoint = salespointQuery.getCurrentSalespoint();
			if (salespoint != null)
			{
				if (salespoint.isLocalProviderProperties())
				{
					properties = salespoint.getProviderProperties();
				}
				else
				{
					final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getCacheService()
							.getQuery(ProviderProperty.class);
					properties = query.selectByProviderAsMap(this.configuration.getProviderId());
				}

				for (final Property property : Property.values())
				{
					ProviderProperty providerProperty = properties.get(property.key());
					if (providerProperty == null)
					{
						providerProperty = ProviderProperty.newInstance(this.configuration.getProviderId());
						providerProperty.setKey(property.key());
						providerProperty.setValue(property.value());
						properties.put(property.key(), providerProperty);
					}
				}

				this.database = properties.get(Property.DATABASE_PATH.key()).getValue();
				this.keepConnection = Boolean.parseBoolean(properties.get(Property.KEEP_CONNECTION.key()).getValue());
				this.bibwinPath = properties.get(Property.BIBWIN_PATH.key()).getValue();
				this.searchCd = Boolean.parseBoolean(properties.get(Property.SEARCH_CD.key()).getValue());
			}
		}

		try
		{
			this.galserve = ClassFactory.creategdserve();
			this.galserve.cbibini(this.bibwinPath);
			this.galserve.lcdsuche(this.searchCd);
		}
		catch (ComException e)
		{
			this.status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Die Verbindung zu Warenbewirtschaftung kann nicht hergestellt werden.", e);
		}

		return this.status;
	}

	private IStatus doVerkauf(Position position, IStatus status)
	{
		if (status.getSeverity() == IStatus.OK)
		{
			final LogService logService = (LogService) this.logServiceTracker.getService();
			if (this.galserve.do_verkauf(position.getCode()))
			{
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, "Galileo: do_verkauf() aufgerufen... Ok!");
				}
				status = doDelAbholfach(position, status);
			}
			else
			{
				String msg = "Beim Versuch, die Warenbewirtschaftung zu aktualisieren, ist ein Fehler aufgetreten (Fehler aus Galileo: do_verkauf() fehlgeschlagen).";
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ProviderInterface.Topic.ARTICLE_UPDATE.topic());
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, msg);
				}
			}
		}
		return status;
	}
	
	private IStatus doDelAbholfach(Position position, IStatus status)
	{
		if (status.getSeverity() == IStatus.OK)
		{
			final LogService logService = (LogService) this.logServiceTracker.getService();
			if (this.galserve.do_delabholfach(position.getSearchValue(), Math.abs(position.getQuantity())))
			{
				position.setProviderBooked(true);
				this.updateCustomerAccount(position);
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, "Galileo: do_delabholfach() aufgerufen... Ok!");
				}
				status = this.galileoTransactionWritten();
			}
			else
			{
				String error = (String) this.galserve.crgerror();
				String msg = "Beim Versuch, die Warenbewirtschaftung zu aktualisieren, ist ein Fehler aufgetreten (Fehler aus Galileo: do_delabholfach() fehlgeschlagen:\n" + error + ").";
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ProviderInterface.Topic.ARTICLE_UPDATE.topic());
				if (logService != null)
				{
					logService.log(LogService.LOG_INFO, msg);
				}
			}
		}
		return status;
	}
	
	private IStatus storeOrder(final Position position)
	{
//		final LogService logService = (LogService) this.logServiceTracker.getService();
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			status = this.doVerkauf(position, status);
			status = this.doDelAbholfach(position, status);
		}
		return status;
	}

	private void updateCustomerAccount(final Position position)
	{
		if (position.getReceipt().getCustomer() != null)
		{
			if (position.getReceipt().getCustomer().getHasAccount())
			{
				position.getReceipt().getCustomer().setAccount(((Double)this.galserve.nkundkonto()).doubleValue());
			}
		}
	}

	private void updatePosition(final Barcode barcode, final Position position)
	{
		String value = null;
		LogService log = (LogService) this.logServiceTracker.getService();

		switch (barcode.getType())
		{
			case ARTICLE:
			{
				if (log != null)
				{
					log.log(LogService.LOG_INFO, "Aktualisiere Artikeldaten aus Warenbewirtschaftung.");
				}
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
				if (log != null)
				{
					log.log(LogService.LOG_INFO, "Aktualisiere Bestelldaten aus Warenbewirtschaftung.");
				}
				this.setProduct(barcode, position);
				value = position.getOrder();
				break;
			}
			case INVOICE:
			{
				if (log != null)
				{
					log.log(LogService.LOG_INFO, "Aktualisiere Rechnungsdaten aus Warenbewirtschaftung.");
				}
				this.setProduct(barcode, position);
				value = position.getProduct().getCode();
				break;
			}
			case CUSTOMER:
			{
				if (log != null)
				{
					log.log(LogService.LOG_INFO, "Aktualisiere Kundendaten aus Warenbewirtschaftung.");
				}
				this.setCustomer(barcode, position);
				value = this.getCustomerData(position.getReceipt().getCustomer());
				break;
			}
			default:
			{

			}
		}

		if (log != null)
		{
			log.log(LogService.LOG_INFO, "Suche " + barcode.getType().toString() + " mit Code " + barcode.getCode()
					+ ": Gefunden: " + value);
		}
	}
}
