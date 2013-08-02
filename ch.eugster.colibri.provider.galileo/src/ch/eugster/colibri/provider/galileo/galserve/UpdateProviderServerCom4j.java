/*
 * Created on 18.07.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.service.ProviderInterface;

public class UpdateProviderServerCom4j extends AbstractServer implements IUpdateProviderServer
{
	@Override
	public IStatus updateProvider(final Position position)
	{
		this.status = Status.OK_STATUS;
		if (isConnect())
		{
			if (position.isBookProvider())
			{
				if (this.open())
				{
					if (position.getSearchValue() != null && position.getProduct() == null)
					{
						Barcode barcode = null;
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
								this.getGalserve().do_NSearch(barcode.getProductCode());
								if (((Boolean) this.getGalserve().gefunden()).booleanValue())
								{
									this.updatePosition(barcode, position);
									barcode.updatePosition(position);
								}
								else
								{
									String msg = barcode.getType().getArticle() + " " + barcode.getType() + " mit dem Code "
											+ barcode.getProductCode() + " konnte nicht gefunden werden.\nBitte erfassen Sie die zus�tzlich ben�tigten Daten manuell.";
									status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, msg);
									log(LogService.LOG_INFO, (msg));
								}
							}
						}
					}
					/*
					 * Nur verbuchen, wenn Flag isBookProvider gesetzt
					 */
					if (position.isProviderBooked())
					{
						/*
						 * Wenn die Position im Provider bereits verbucht ist, kann nur
						 * noch eine R�ckbuchung �ber das Stornieren einer Rechnung
						 * erfolgen.
						 */
						if (position.getReceipt().getState().equals(Receipt.State.REVERSED))
						{
							if (position.getProduct() == null)
							{
								log(LogService.LOG_INFO, "R�ckbuchung " + position.getProductGroup().getCode() + ".");
								status = this.reverseProductGroup(position);
							}
							else
							{
								if (position.getOption().equals(Position.Option.ARTICLE)
										|| position.getOption().equals(Position.Option.ORDERED))
								{
									log(LogService.LOG_INFO, "R�ckbuchung " + position.getProduct().getCode() + ".");
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
		
					this.close();
				}
			}
		}
		return status;
	}

	private Integer convert(final String code)
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
			log(LogService.LOG_INFO, "Code: " + code + ". konnte nicht konvertiert werden (ung�ltiger Datentyp).");
		}
		return id;
	}

	private IStatus galileoTransactionWritten()
	{
		IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID, ProviderInterface.Topic.ARTICLE_UPDATE.topic());
		final String msg = "Galileo: Transaktion geschrieben.";

		final Boolean result = (Boolean)this.getGalserve().vtranswrite();
		if (result.booleanValue())
		{
			log(LogService.LOG_INFO, msg);
		}
		else
		{
			status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, msg);
			log(LogService.LOG_INFO, msg + " FEHLER!");
		}

		return status;
	}

	private IStatus payInvoice(final Position position)
	{
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			final Integer invoiceNumber = this.convert(position.getProduct().getInvoiceNumber());
			if (invoiceNumber != null)
			{
				if (this.getGalserve().do_BucheRechnung(invoiceNumber))
				{
					position.setProviderBooked(true);
					this.updateCustomerAccount(position);
					log(LogService.LOG_INFO, "Galileo: Rechnung " + position.getProduct().getInvoiceNumber()
								+ " verbuchen... Ok.");
				}
				else
				{
					final String msg = this.getGalserve().crgerror().toString();
					log(LogService.LOG_INFO, "Galileo: Rechnung " + position.getProduct().getInvoiceNumber()
								+ " verbuchen... Fehler: " + msg);
				}
			}
			else
			{
				final String msg = "Rechnungsnummer nicht vorhanden, Rechnung kann nicht verbucht werden.";
				log(LogService.LOG_INFO, msg);
			}
			status = this.galileoTransactionWritten();
		}
		return status;
	}

	private IStatus reverseArticle(final Position position)
	{
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			if (position.getQuantity() < 0)
			{
				/*
				 * Eine negative Menge bedeuted die R�cknahme eines Artikels, dieser
				 * muss beim Stornieren einer Rechnung wieder 'verkauft' werden.
				 */
				if (this.getGalserve().do_verkauf(position.getCode()))
				{
					position.setProviderBooked(false);
					this.updateCustomerAccount(position);
					log(LogService.LOG_INFO, "Galileo: do_verkauf() f�r " + position.getSearchValue()
								+ " aufgerufen... Ok!");
				}
				else
				{
					log(LogService.LOG_INFO, "Galileo: do_verkauf() f�r " + position.getSearchValue()
								+ " aufgerufen... Fehler!");
				}
			}
			else
			{
				if (this.getGalserve().do_storno(position.getCode()))
				{
					position.setProviderBooked(false);
					this.updateCustomerAccount(position);
					log(LogService.LOG_INFO, "Galileo: do_storno() f�r " + position.getSearchValue()
								+ " aufgerufen... Ok!");
				}
				else
				{
					log(LogService.LOG_INFO, "Galileo: do_storno() f�r " + position.getSearchValue()
								+ " aufgerufen... Fehler!");
				}
			}

			status = this.galileoTransactionWritten();
		}
		return status;
	}

	private IStatus reverseProductGroup(final Position position)
	{
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			if (position.getQuantity() < 0)
			{
				/*
				 * Bei negativer Menge (Urspr�nglich eine R�ckbuchung eines
				 * Artikels, muss dieser nun wieder 'verkauft' werden
				 */
				if (this.getGalserve().do_wgverkauf())
				{
					position.setProviderBooked(false);
					log(LogService.LOG_INFO, "Galileo: do_wgverkauf() f�r " + position.getProductGroup().getName()
								+ " aufgerufen... Ok!");
				}
				else
				{
					status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Beim Versuch, die Daten an die Warenbewirtschaftung zu �bermitteln, ist ein Fehler aufgetreten (Galileo: do_wgverkauf()");
					log(LogService.LOG_INFO, status.getMessage());
				}
			}
			else
			{
				if (this.getGalserve().do_wgstorno())
				{
					position.setProviderBooked(false);
						log(LogService.LOG_INFO, "Galileo: do_wgstorno() f�r " + position.getProductGroup().getName()
								+ " aufgerufen... Ok!");
				}
				else
				{
					status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Beim Versuch, die Daten an die Warenbewirtschaftung zu �bermitteln, ist ein Fehler aufgetreten (Galileo: do_wgverkauf()");
						log(LogService.LOG_INFO, status.getMessage());
				}
			}
			status = this.galileoTransactionWritten();
		}
		return status;
	}

	private IStatus sellArticle(final Position position)
	{
		IStatus status = this.setProviderValues(position);
		if (status.getSeverity() == IStatus.OK)
		{
			if (position.getQuantity() < 0)
			{
				/*
				 * Eine negative Menge bedeutet die R�cknahme eines Artikels, damit
				 * muss dieser storniert werden.
				 */
				if (this.getGalserve().do_storno(position.getCode()))
				{
					position.setProviderBooked(true);
					this.updateCustomerAccount(position);
						log(LogService.LOG_INFO, "Galileo: do_storno() f�r " + position.getSearchValue()
								+ " aufgerufen... Ok!");
				}
				else
				{
						log(LogService.LOG_INFO, "Galileo: do_storno() f�r " + position.getSearchValue()
								+ " aufgerufen... Fehler!");
				}
			}
			else
			{
				/*
				 * Eine positive Menge bedeutet den Verkauf eines Artikels.
				 */
				if (this.getGalserve().do_verkauf(position.getCode()))
				{
					position.setProviderBooked(true);
					this.updateCustomerAccount(position);
						log(LogService.LOG_INFO, "Galileo: do_verkauf() f�r " + position.getSearchValue() + " aufgerufen... Ok!");
				}
				else
				{
						log(LogService.LOG_INFO, "Galileo: do_verkauf() f�r " +
								position.getSearchValue() + " aufgerufen... FEHLER!");
				}
			}
			return this.galileoTransactionWritten();
		}
		return status;
	}

	private IStatus sellProductGroup(final Position position)
	{
		this.setProviderValues(position);

		if (position.getQuantity() < 0)
		{
			/*
			 * Eine negative Menge bedeutet die R�ckbuchung eines Artikels,
			 * dieser muss somit storniert werden
			 */
			if (this.getGalserve().do_wgstorno())
			{
				position.setProviderBooked(true);
				log(LogService.LOG_INFO, "Galileo: do_wgstorno() f�r " + position.getProductGroup().getName()
							+ " aufgerufen... Ok!");
			}
			else
			{
				log(LogService.LOG_INFO, "Galileo: do_wgstorno() f�r " + position.getProductGroup().getName()
							+ " aufgerufen... FEHLER!");
			}
		}
		else
		{
			boolean sold = this.getGalserve().do_wgverkauf();
			if (sold)
			{
				position.setProviderBooked(true);
				log(LogService.LOG_INFO, "Galileo: do_wgverkauf() f�r " + position.getProductGroup().getName()
							+ " aufgerufen... Ok!");
			}
			else
			{
				log(LogService.LOG_INFO, "Galileo: do_wgverkauf() f�r " + position.getProductGroup().getName()
							+ " aufgerufen... Fehler!");
			}
		}

		return this.galileoTransactionWritten();
	}

	private Integer getGalileoCustomerCode(final Receipt receipt)
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
			value = this.convert(receipt.getCustomer().getCode());
		}
		return value == null ? Integer.valueOf(0) : value;
	}

	private IStatus setProviderValues(final Position position)
	{
		IStatus status = Status.OK_STATUS;
		try
		{
			this.getGalserve().vbestellt(position.isOrdered());
			this.getGalserve().vcouponnr(position.getReceipt().getNumber().toString());
			this.getGalserve().vkundennr(this.getGalileoCustomerCode(position.getReceipt()));
			this.getGalserve().vlagerabholfach(position.isFromStock());
			this.getGalserve().vmenge(Math.abs(position.getQuantity()));
			this.getGalserve().vpreis(Math.abs(position.getPrice()));
			this.getGalserve().vebook(Boolean.valueOf(position.isEbook()));
			this.getGalserve().vrabatt(-Math.abs(position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
					Position.AmountType.DISCOUNT)));

			if (position.getProduct() == null)
			{
				this.getGalserve().vwgname(position.getProductGroup().getName());
				this.getGalserve().vwgruppe(position.getProductGroup().getCode());
			}
			else
			{
				this.getGalserve().vnummer(position.getProduct().getCode());
				if (position.getOption().equals(Position.Option.PAYED_INVOICE))
				{
					this.getGalserve().vwgname(position.getProductGroup().getName());
					this.getGalserve().vwgruppe(position.getProductGroup().getCode());
				}
				else
				{
					this.getGalserve().vwgname(position.getProduct().getExternalProductGroup().getText());
					this.getGalserve().vwgruppe(position.getProduct().getExternalProductGroup().getCode());
					try
					{
						TaxCodeMapping taxCodeMapping = position.getCurrentTax().getTax().getTaxCodeMapping(this.getConfiguration().getProviderId());
						if (taxCodeMapping.isDeleted())
						{
							throw new NullPointerException();
						}
						this.getGalserve().vmwst(taxCodeMapping.getCode());
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
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ProviderInterface.Topic.PROVIDER_TAX_NOT_SPECIFIED.topic(), e);
		}
		return status;
	}

	private IStatus doVerkauf(Position position, IStatus status)
	{
		if (status.getSeverity() == IStatus.OK)
		{
			if (this.getGalserve().do_verkauf(position.getCode()))
			{
				log(LogService.LOG_INFO, "Galileo: do_verkauf() aufgerufen... Ok!");
			}
			else
			{
				String msg = "Beim Versuch, die Warenbewirtschaftung zu aktualisieren, ist ein Fehler aufgetreten (Fehler aus Galileo: do_verkauf() fehlgeschlagen).";
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ProviderInterface.Topic.ARTICLE_UPDATE.topic());
				log(LogService.LOG_INFO, msg);
			}
		}
		return status;
	}
	
	private IStatus doDelAbholfach(Position position, IStatus status)
	{
		if (status.getSeverity() == IStatus.OK)
		{
			if (this.getGalserve().do_delabholfach(position.getSearchValue(), Math.abs(position.getQuantity())))
			{
				position.setProviderBooked(true);
				this.updateCustomerAccount(position);
				log(LogService.LOG_INFO, "Galileo: do_delabholfach() aufgerufen... Ok!");
				status = this.galileoTransactionWritten();
			}
			else
			{
				String error = (String) this.getGalserve().crgerror();
				String msg = "Beim Versuch, die Warenbewirtschaftung zu aktualisieren, ist ein Fehler aufgetreten (Fehler aus Galileo: do_delabholfach() fehlgeschlagen:\n" + error + ").";
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ProviderInterface.Topic.ARTICLE_UPDATE.topic());
				log(LogService.LOG_INFO, msg);
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
				position.getReceipt().getCustomer().setAccount(((Double)this.getGalserve().nkundkonto()).doubleValue());
			}
		}
	}

}