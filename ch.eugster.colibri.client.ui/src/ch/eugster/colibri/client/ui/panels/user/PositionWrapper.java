/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.PositionChangeEvent;
import ch.eugster.colibri.client.ui.events.PositionChangeListener;
import ch.eugster.colibri.client.ui.events.ReceiptChangeMediator;
import ch.eugster.colibri.client.ui.panels.user.pos.numeric.ValueDisplay;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderQuery;

public class PositionWrapper implements PropertyChangeListener, DisposeListener
{
	public static final String KEY_POSITION = "position";

	public static final String KEY_PROPERTY_ID = "id";

	public static final String KEY_PROPERTY_TIMESTAMP = "timestamp";

	public static final String KEY_PROPERTY_VERSION = "version";

	public static final String KEY_PROPERTY_DELETED = "deleted";

	public static final String KEY_PROPERTY_PRICE = "price";

	public static final String KEY_PROPERTY_DISCOUNT = "discount";

	public static final String KEY_PROPERTY_QUANTITY = "quantity";

	public static final String KEY_PROPERTY_ROUND_FACTOR = "roundFactor";

	public static final String KEY_PROPERTY_ORDERED = "ordered";

	public static final String KEY_PROPERTY_OPTION = "option";

	public static final String KEY_PROPERTY_QUOTATION = "quotation";

	public static final String KEY_PROPERTY_BOOK_SERVER = "bookServer";

	public static final String KEY_PROPERTY_RECEIPT = "receipt";

	public static final String KEY_PROPERTY_PRODUCT_GROUP = "productGroup";

	public static final String KEY_PROPERTY_CURRENT_TAX = "currentTax";

	public static final String KEY_PROPERTY_CURRENCY = "currency";

	public static final String KEY_PROPERTY_SEARCH_VALUE = "searchValue";

	public static final String KEY_PROPERTY_PRODUCT = "product";

	public static final String KEY_PROPERTY_DEFAULT_CURRENCY_AMOUNT = "defaultCurrencyAmount";

	public static final String KEY_PROPERTY_FOREIGN_CURRENCY_AMOUNT = "foreignCurrencyAmount";

	private KeyListener keyListener;

	private final List<PositionChangeListener> positionChangeListeners = new ArrayList<PositionChangeListener>();

	private final List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>();
	
	private final String[] propertyNames = new String[] { ReceiptWrapper.KEY_PROPERTY_POSITIONS };

	private Position position;

	private final ServiceTracker<ProviderQuery, ProviderQuery> providerQueryTracker;

//	private final ServiceTracker<ProviderIdService, ProviderIdService> providerIdServiceTracker;

	private final ServiceTracker<BarcodeVerifier, BarcodeVerifier> barcodeVerifierTracker;

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private final ServiceTracker<LogService, LogService> logServiceTracker;

	private final ValueDisplay valueDisplay;

	private UserPanel userPanel;

	private NumberFormat currencyFormatter;
	
	public PositionWrapper(final UserPanel userPanel, final ValueDisplay valueDisplay)
	{
		this.valueDisplay = valueDisplay;

		this.userPanel = userPanel;
		this.userPanel.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(final KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					PositionWrapper.this.keyPressed(e);
				}
			}
		});

		currencyFormatter = DecimalFormat.getCurrencyInstance();

		new ReceiptChangeMediator(userPanel, this, this.propertyNames);

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(),
				LogService.class, null);
		this.logServiceTracker.open();

		this.barcodeVerifierTracker = new ServiceTracker<BarcodeVerifier, BarcodeVerifier>(Activator.getDefault().getBundle().getBundleContext(),
				BarcodeVerifier.class, null);
		this.barcodeVerifierTracker.open();

		this.providerQueryTracker = new ServiceTracker<ProviderQuery, ProviderQuery>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderQuery.class, null);
		this.providerQueryTracker.open();

//		this.providerIdServiceTracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle().getBundleContext(),
//				ProviderIdService.class, null);
//		this.providerIdServiceTracker.open();

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	public void addPositionChangeListener(final PositionChangeListener listener)
	{
		if (listener != null)
		{
			if (!this.positionChangeListeners.contains(listener))
			{
				this.positionChangeListeners.add(listener);
			}
		}
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener)
	{
		if (listener != null)
		{
			if (!this.propertyChangeListeners.contains(listener))
			{
				this.propertyChangeListeners.add(listener);
			}
		}
	}

	public void clearPosition()
	{
		final Position newPosition = Position.reinitialize(this.position);
		replacePosition(newPosition);
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
//		this.providerIdServiceTracker.close();
		this.providerQueryTracker.close();
		this.logServiceTracker.close();
	}

//	public boolean isFreeCopy()
//	{
//		return freeCopy;
//	}
//	
//	public void setFreeCopy(boolean freeCopy)
//	{
//		this.freeCopy = freeCopy;
//	}
	
	public boolean doesPositionNeedPrice()
	{
//		if (this.freeCopy)
//		{
//			return false;
//		}
		return this.position.getPrice() == 0d ? true : false;
	}
	
	public void updateCustomer(Receipt receipt)
	{
		if (receipt.getCustomerCode() != null)
		{
			ProviderQuery query = this.providerQueryTracker.getService();
			if (query != null)
			{
				query.updateCustomer(receipt);
			}
		}
	}

	public boolean doesPositionNeedQuantity()
	{
		return this.position.getQuantity() == 0 ? true : false;
	}

	public Position getPosition()
	{
		return this.position;
	}

	public boolean isPositionComplete()
	{
		if (this.position.getForeignCurrency() == null)
		{
			Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enthält keine Währung");
			return false;
		}
		if (this.position.getProductGroup() == null)
		{
			Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enthält keine Warengruppe");
			return false;
		}
		if (this.position.getProduct() != null)
		{
			if (this.position.getOption() == null || !this.position.getOption().equals(Position.Option.PAYED_INVOICE))
			{
				if (this.position.getProduct().getExternalProductGroup() == null)
				{
					Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enthält keine externe Warengruppe");
					return false;
				}
			}
		}
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final Long id = this.position.getProductGroup().getProductGroupType().getTaxTypeId();
			if ((id != null)
					&& (((TaxType) persistenceService.getCacheService().find(TaxType.class, id)).getTaxes().size() > 0))
			{
				if (this.position.getCurrentTax() == null)
				{
					Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enthält keine Mehrwertsteuer");
					return false;
				}
			}
		}
		if (this.position.getOption() == null)
		{
			if (this.position.getProductGroup().getProductGroupType().equals(ProductGroupType.SALES_RELATED))
			{
				Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enthält keine Option (Bestellt/Lager)");
				return false;
			}
		}
		if (!this.position.isOrdered() && this.position.getPrice() == 0d)
		{
			Activator.getDefault().log(LogService.LOG_WARNING, "Die Position hat keinen Preice");
			return false;
		}
		if (this.position.getQuantity() == 0 || (this.position.isOrdered() && this.position.getOrderedQuantity() == 0))
		{
			Activator.getDefault().log(LogService.LOG_WARNING, "Die Position hat keine Menge");
			return false;
		}
		Activator.getDefault().log(LogService.LOG_INFO, "Die Position ist vollständig");
		return true;
	}

	private void log(int level, String message)
	{
		LogService service = logServiceTracker.getService();
		if (service != null)
		{
			service.log(level, message);
		}
	}
	
	public boolean isPositionEmpty()
	{
		if (this.position.getProductGroup() == null)
		{
			if (this.position.getPrice() == this.position.getReceipt().getSettlement().getSalespoint()
					.getProposalPrice())
			{
				if (this.position.getQuantity() == this.position.getReceipt().getSettlement().getSalespoint()
						.getProposalQuantity())
				{
					return true;
				}
			}
		}

		return false;
	}

	public void keyPressed(final KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.VK_ENTER)
		{
			log(LogService.LOG_DEBUG, "Enter Key pressed.");
			final String input = this.valueDisplay.readAndInitDisplay();
			if ((input != null) && (input.length() > 0))
			{
//				if (input.toUpperCase().startsWith(Barcode.PREFIX_VOUCHER))
//				{
//					if (this.userPanel.getCurrentState().equals(UserPanel.State.POSITION_INPUT))
//					{
//						log(LogService.LOG_INFO, "eGutschein gescant.");
//						log(LogService.LOG_INFO, "Prüfe eGutschein Service Verfügbarkeit...");
//						ServiceTracker<VoucherService, VoucherService> tracker = new ServiceTracker<VoucherService, VoucherService>(Activator.getDefault().getBundle().getBundleContext(), VoucherService.class, null);
//						tracker.open();
//						try
//						{
//							VoucherService service = tracker.getService();
//							if (service == null)
//							{
//								log(LogService.LOG_INFO, "eGutschein Service nicht verfügbar.");
//								final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
//										.getReceipt().getSettlement().getSalespoint().getProfile(), "Service nicht verfügbar",
//										new int[] { MessageDialog.BUTTON_OK }, 0);
//								dialog.setMessage("Der Service für die Bearbeitung von eGutscheinen ist nicht verfügbar.");
//								dialog.pack();
//								dialog.setVisible(true);
//							}
//							else
//							{
//								currencyFormatter.setCurrency(userPanel.getSalespoint().getPaymentType().getCurrency().getCurrency());
//								log(LogService.LOG_INFO, "eGutschein Service verfügbar.");
//								if (userPanel.getCurrentState().equals(UserPanel.State.POSITION_INPUT))
//								{
//									Result result = service.getAccountBalance(input);
//									if (result.isOK())
//									{
//										log(LogService.LOG_INFO, "Verfügbarer Betrag: " + currencyFormatter.format(result.getAmount()) + ".");
//										double balance = result.getAmount();
//										double creditAmount = userPanel.getValueDisplay().getAmount();
//										creditAmount = CreditAccountDialog.showInformation(Activator.getDefault().getFrame(), userPanel.getProfile(), "Kontostandabfrage", userPanel.getSalespoint().getPaymentType().getCurrency(), balance, creditAmount, CreditAccountDialog.TYPE_INFORMATION);
//										if (creditAmount > 0D)
//										{
////											PersistenceService persistenceService = persistenceServiceTracker.getService();
////											if (persistenceService != null)
////											{
//												this.position.setProductGroup(this.getDefaultVoucherProductGroup());
//												this.position.setBookProvider(true);
//												this.position.setProviderBooked(false);
//												this.position.setPrice(creditAmount);
//												this.position.setQuantity(1);
////												this.position.getReceipt().setState(Receipt.State.)
////												this.position.setProviderState(ProviderState.OPEN);
//												this.position.setProvider(service.getProviderId());
//												if (this.position.getQuantity() == 0)
//												{
//													this.position.setQuantity(1);
//												}
//												this.position.setSearchValue(input);
////											}
//										}	
//									}
//									else
//									{
////										log(LogService.LOG_INFO, "Ungültiger Betrag für die Aufladung.");
////										final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
////												.getReceipt().getSettlement().getSalespoint().getProfile(), "Ungültiger Betrag",
////												new int[] { MessageDialog.BUTTON_OK }, 0);
////										dialog.setMessage("Der Betrag von " + currencyFormatter.format(amount) + " ist ungültig.\n\n Sie müssen einen Betrag grösser als " + currencyFormatter.format(0D) + " eingeben.");
////										dialog.pack();
////										dialog.setVisible(true);
//										final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
//												.getReceipt().getSettlement().getSalespoint().getProfile(), "Fehler",
//												new int[] { MessageDialog.BUTTON_OK }, 0);
//										dialog.setMessage(result.getErrorMessage());
//										dialog.pack();
//										dialog.setVisible(true);
//									}
////										log(LogService.LOG_INFO, "Das eGutschein-Konto für " + input + " soll mit " + currencyFormatter.format(amount) + " aufgeladen werden.");
////										result = service.getAccountBalance(input);
////										if (result.isOK())
////										{
////											log(LogService.LOG_INFO, "Verfügbarer Betrag: " + currencyFormatter.format(result.getAmount()) + ".");
////											PersistenceService persistenceService = persistenceServiceTracker.getService();
////											if (persistenceService != null)
////											{
////												this.position.setProductGroup(this.getDefaultVoucherProductGroup());
////												this.position.setBookProvider(true);
////												this.position.setProviderBooked(false);
////												this.position.setProviderState(ProviderState.OPEN);
////												this.position.setProvider(service.getProviderId());
////												if (this.position.getQuantity() == 0)
////												{
////													this.position.setQuantity(1);
////												}
////												this.position.setSearchValue(input);
////											}
////										}
////										else
////										{
////											final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
////													.getReceipt().getSettlement().getSalespoint().getProfile(), "Fehler",
////													new int[] { MessageDialog.BUTTON_OK }, 0);
////											dialog.setMessage(result.getErrorMessage());
////											dialog.pack();
////											dialog.setVisible(true);
////										}
////									}
//								}
//								else if (userPanel.getCurrentState().equals(UserPanel.State.PAYMENT_INPUT))
//								{
//									log(LogService.LOG_INFO, "Das eGutschein-Konto für " + input + " soll belastet werden.");
//									Result result = service.getAccountBalance(input);
//									if (result.isOK())
//									{
//										double balanceAmount = result.getAmount();
//										double paymentAmount = this.userPanel.getPaymentWrapper().getPayment().getAmount();
//										if (paymentAmount == 0D)
//										{
//											paymentAmount = this.userPanel.getReceiptWrapper().getReceiptDifference();
//										}
//										if (Math.abs(paymentAmount) <= Math.abs(balanceAmount))
//										{
//											result = service.reserveAmount(input, paymentAmount);
//											if (result.isOK())
//											{
//												Payment payment = this.userPanel.getPaymentWrapper().getPayment();
//												payment.setBookProvider(true);
//												payment.setProviderId(service.getProviderId());
//												payment.setCode(input);
//												payment.setProviderBooked(false);
//												payment.setProviderState(ProviderState.RESERVED);
//												payment.setAmount(paymentAmount);
//												payment.setBack(false);
//												payment.setPaymentType(getDefaultVoucherPaymentType());
//												this.userPanel.getPaymentWrapper().replacePayment(payment);
//											}
//											else
//											{
//												log(LogService.LOG_INFO, "Fehler " + result.getErrorCode() + ": " + result.getErrorMessage());
//												final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
//														.getReceipt().getSettlement().getSalespoint().getProfile(), "Fehler",
//														new int[] { MessageDialog.BUTTON_OK }, 0);
//												dialog.setMessage(result.getErrorMessage());
//												dialog.pack();
//												dialog.setVisible(true);
//											}
//										}
//										else
//										{
//											log(LogService.LOG_INFO, "Der Bezahlungsbetrag ist nicht gedeckt (verfügbarer Betrag: " + currencyFormatter.format(paymentAmount) + ", Verfügbarer Betrag: " + currencyFormatter.format(balanceAmount) + ").");
//											final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
//													.getReceipt().getSettlement().getSalespoint().getProfile(), "Ungültiger Betrag",
//													new int[] { MessageDialog.BUTTON_OK }, 0);
//											dialog.setMessage("Der Betrag von " + currencyFormatter.format(paymentAmount) + " ist nicht gedeckt.\n\nEs sind maximal " + currencyFormatter.format(balanceAmount) + " verfügbar.");
//											dialog.pack();
//											dialog.setVisible(true);
//										}
//									}
//									else
//									{
//										final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
//												.getReceipt().getSettlement().getSalespoint().getProfile(), "Fehler",
//												new int[] { MessageDialog.BUTTON_OK }, 0);
//										dialog.setMessage(result.getErrorMessage());
//										dialog.pack();
//										dialog.setVisible(true);
//									}
//								}
//							}
//						}
//						finally
//						{
//							tracker.close();
//						}
//					}
//				}
//				else
//				{
					final Barcode barcode = this.verifyInput(input);
					if (barcode instanceof Barcode)
					{
						if (this.userPanel.getReceiptWrapper().getReceipt().isInternal())
						{
							MessageDialog.showInformation(Activator.getDefault().getFrame(), userPanel.getProfile(), "Fehler", "Geldeinlagen und -entnahmen dürfen keine anderen Positionen enthalten.", MessageDialog.TYPE_WARN);
						}
						else
						{
							log(LogService.LOG_INFO, "Barcode: " + barcode.getName());
							if (barcode.getCode().startsWith(Barcode.PREFIX_CUSTOMER))
							{
								log(LogService.LOG_INFO, "Typ: " + barcode.getType().toString());
								this.position.getReceipt().setCustomerCode(barcode.getCode());
								this.findAndRead(barcode);
							}
							else if (barcode.isEbook())
							{
								log(LogService.LOG_INFO, "Typ: " + barcode.getType().toString());
								this.position.setEbook(barcode.isEbook());
								this.setDefaultProductGroup();
							}
							else if (this.verifyOrderedNotYetScanned(barcode))
							{
								log(LogService.LOG_INFO, "Provider wird gesetzt.");
								this.setProviderId(barcode);
								log(LogService.LOG_INFO, "Default-WG setzen.");
								this.setDefaultProductGroup();
								log(LogService.LOG_INFO, "Start Titelsuche.");
								this.findAndRead(barcode);
							}
						}
					}
					else
					{
						final String[] descs = this.getAvailableBarcodeNames();
						StringBuilder text = new StringBuilder("<html>\n");
						text = text.append("Die Eingabe '" + input + "' kann nicht verarbeitet werden.");
						if (descs.length > 0)
						{
							text = text.append(" Folgende Barcodes werden erkannt:\n<ul>\n");
							for (final String desc : descs)
							{
								text = text.append("<li>" + desc + "\n");
							}
							text.append("</ul>\n");
						}
						final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
								.getReceipt().getSettlement().getSalespoint().getProfile(), "Barcode ungültig",
								new int[] { MessageDialog.BUTTON_OK }, 0);
						dialog.setMessage(text.toString());
						// dialog.setPreferredSize(new Dimension(480, 240 +
						// descs.length * 80));
						dialog.pack();
						dialog.setVisible(true);
					}
				}
				if (this.keyListener != null)
				{
					this.keyListener.keyPressed(event);
				}
//			}
		}
	}
	
	private void setDefaultProductGroup()
	{
		if (this.position.getProductGroup() == null)
		{
			CommonSettings settings = this.position.getReceipt().getSettlement().getSalespoint().getCommonSettings();
			if (this.position.isEbook())
			{
				this.position.setProductGroup(settings.getEBooks());
			}
			else
			{
				this.position.setProductGroup(settings.getDefaultProductGroup());
			}
		}
	}

	public Position preparePosition(final Receipt receipt)
	{
		Activator.getDefault().log(LogService.LOG_DEBUG, "Enter PositionWrapper.preparePosition()");
		final Position newPosition = Position.newInstance(receipt);
		final ProviderQuery providerQuery = (ProviderQuery) this.providerQueryTracker.getService();
		if (providerQuery != null)
		{
			if (providerQuery.getConfiguration().updateLocalItems())
			{
				newPosition.setProvider(providerQuery.getProviderId());
				newPosition.setBookProvider(providerQuery.getConfiguration().updateLocalItems());
			}
		}
		Activator.getDefault().log(LogService.LOG_DEBUG, "Exit PositionWrapper.preparePosition()");
		return replacePosition(newPosition);
	}
	
	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource() instanceof Receipt)
		{
			if (event.getPropertyName().equals(ReceiptWrapper.KEY_PROPERTY_POSITIONS))
			{
				this.preparePosition((Receipt) event.getSource());
			}
		}
		else if (event.getSource() instanceof Position)
		{
			PropertyChangeListener[] listeners = this.propertyChangeListeners.toArray(new PropertyChangeListener[0]);
			for (PropertyChangeListener listener : listeners)
			{
				listener.propertyChange(event);
			}
		}
	}

	public void removePositionChangeListener(final PositionChangeListener listener)
	{
		if (listener != null)
		{
			if (this.positionChangeListeners.contains(listener))
			{
				this.positionChangeListeners.remove(listener);
			}
		}
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener)
	{
		if (listener != null)
		{
			if (this.propertyChangeListeners.contains(listener))
			{
				this.propertyChangeListeners.remove(listener);
			}
		}
	}

	public Position replacePosition(final Position position)
	{
		Activator.getDefault().log(LogService.LOG_DEBUG, "Enter PositionWrapper.replacePosition()");
		this.firePositionChangeEvent(new PositionChangeEvent(this.position, position));
		if (this.position != null)
		{
			this.position.removePropertyChangeListener(PositionWrapper.KEY_PROPERTY_PRODUCT_GROUP, this);
			this.position.removePropertyChangeListener(PositionWrapper.KEY_PROPERTY_QUANTITY, this);
			this.position.removePropertyChangeListener(PositionWrapper.KEY_PROPERTY_PRICE, this);
		}
		this.position = position;
		this.position.addPropertyChangeListener(PositionWrapper.KEY_PROPERTY_PRODUCT_GROUP, this);
		this.position.addPropertyChangeListener(PositionWrapper.KEY_PROPERTY_QUANTITY, this);
		this.position.addPropertyChangeListener(PositionWrapper.KEY_PROPERTY_PRICE, this);
		Activator.getDefault().log(LogService.LOG_DEBUG, "Exit PositionWrapper.replacePosition()");
		return this.position;
	}

	public void setKeyListener(final KeyListener keyListener)
	{
		this.keyListener = keyListener;
	}

	private void findAndRead(final Barcode barcode)
	{
		final ProviderQuery providerQuery = (ProviderQuery) this.providerQueryTracker.getService();
		if (providerQuery!= null && providerQuery.isConnect())
		{
			log(LogService.LOG_INFO, "Providerservice wird aufgerufen.");
			final IStatus status = providerQuery.findAndRead(barcode, this.position);
			if (status.getSeverity() == IStatus.CANCEL)
			{
				if (this.position.isOrdered())
				{
					log(LogService.LOG_WARNING, "Artikel " + barcode.getProductCode() + " wurde nicht gefunden.");
					MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.position.getReceipt()
							.getSettlement().getSalespoint().getProfile(), "Nicht gefunden", status.getMessage(),
							MessageDialog.BUTTON_OK);
					this.replacePosition(Position.newInstance(this.position.getReceipt()));
				}
				else
				{
					if (!this.isPositionComplete())
					{
						log(LogService.LOG_WARNING, "Artikel " + barcode.getProductCode() + " wurde nicht gefunden.");
						MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.position.getReceipt()
								.getSettlement().getSalespoint().getProfile(), "Nicht gefunden", status.getMessage(),
								MessageDialog.BUTTON_OK);
					}
				}
			}
		}
	}

	private void firePositionChangeEvent(final PositionChangeEvent event)
	{
		final PositionChangeListener[] listeners = this.positionChangeListeners.toArray(new PositionChangeListener[0]);
		for (final PositionChangeListener listener : listeners)
		{
			listener.positionChange(event);
		}
	}

	private String[] getAvailableBarcodeNames()
	{
		final Collection<String> barcodes = new ArrayList<String>();
		final Object[] barcodeVerifiers = this.barcodeVerifierTracker.getServices();
		if (barcodeVerifiers != null)
		{
			for (final Object barcodeVerifier : barcodeVerifiers)
			{
				barcodes.add(((BarcodeVerifier) barcodeVerifier).getBarcodeDescription());
			}
		}
		return barcodes.toArray(new String[0]);
	}

	private void setProviderId(final Barcode barcode)
	{
		final ProviderQuery providerQuery = (ProviderQuery) this.providerQueryTracker.getService();
		if (providerQuery != null)
		{
			this.position.setProvider(providerQuery.getProviderId());
			this.position.setBookProvider(true);
		}
	}

	private Barcode verifyInput(final String code)
	{
		Barcode barcode = null;
		final Object[] barcodeVerifiers = this.barcodeVerifierTracker.getServices();
		if (barcodeVerifiers != null)
		{
			for (final Object barcodeVerifier : barcodeVerifiers)
			{
				synchronized (barcodeVerifier)
				{
					barcode = ((BarcodeVerifier) barcodeVerifier).verify(code);
					if (barcode != null)
					{
						barcode.updatePosition(position);
//						position.setSearchValue(barcode.getCode());
						break;
					}
				}
			}
		}
		return barcode;
	}
	
//	private ProductGroup getDefaultVoucherProductGroup()
//	{
//		return this.userPanel.getSalespoint().getCommonSettings().getDefaultVoucherProductGroup();
//	}
//
//	private PaymentType getDefaultVoucherPaymentType()
//	{
//		return this.userPanel.getSalespoint().getCommonSettings().getDefaultVoucherPaymentType();
//	}

	private boolean verifyOrderedNotYetScanned(final Barcode barcode)
	{
		if (barcode.getType().equals(Barcode.Type.ORDER))
		{
			// Search current receipt
			Collection<Position> positions = this.getPosition().getReceipt().getPositions();
			for (final Position position : positions)
			{
				if (position.getSearchValue() != null && position.getSearchValue().equals(barcode.getCode()) && (position != this.getPosition()))
				{
					MessageDialog.showInformation(Activator.getDefault().getFrame(), this.position.getReceipt()
							.getSettlement().getSalespoint().getProfile(), "Bereits erfasst", "Der Abholfachbeleg "
							+ barcode.getCode() + " wurde bereits erfasst.", MessageDialog.TYPE_WARN);
					return false;
				}
			}

			// Search parked
			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker
					.getService();
			if (persistenceService != null)
			{
				final PositionQuery query = (PositionQuery) persistenceService.getCacheService().getQuery(
						Position.class);
				positions = query.countBySearchValue(barcode.getCode());
				if (positions.isEmpty())
				{
					return true;
				}

				final Position position = positions.iterator().next();
				MessageDialog.showInformation(Activator.getDefault().getFrame(), this.position.getReceipt()
						.getSettlement().getSalespoint().getProfile(), "Bereits erfasst", "Der Abholfachbeleg "
						+ barcode.getCode() + " wurde bereits im geparkten Beleg " + position.getReceipt().getNumber()
						+ " erfasst.", MessageDialog.TYPE_WARN);
				return false;
			}
		}
		return true;
	}

}
