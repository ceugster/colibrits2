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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
		this.providerQueryTracker.close();
		this.logServiceTracker.close();
	}

	public boolean doesPositionNeedPrice()
	{
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
			Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enth�lt keine W�hrung");
			return false;
		}
		if (this.position.getProductGroup() == null)
		{
			Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enth�lt keine Warengruppe");
			return false;
		}
		if (this.position.getProduct() != null)
		{
			if (this.position.getOption() == null || !this.position.getOption().equals(Position.Option.PAYED_INVOICE))
			{
				if (this.position.getProduct().getExternalProductGroup() == null)
				{
					Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enth�lt keine externe Warengruppe");
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
					Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enth�lt keine Mehrwertsteuer");
					return false;
				}
			}
		}
		if (this.position.getOption() == null)
		{
			if (this.position.getProductGroup().getProductGroupType().equals(ProductGroupType.SALES_RELATED))
			{
				Activator.getDefault().log(LogService.LOG_WARNING, "Die Position enth�lt keine Option (Bestellt/Lager)");
				return false;
			}
		}
		if (this.position.getPrice() == 0d)
		{
			if (!position.isOrdered() || this.userPanel.getMainTabbedPane().isFailOver())
			{
				Activator.getDefault().log(LogService.LOG_WARNING, "Die Position hat keinen Preis");
				return false;
			}
		}
		if (this.position.getQuantity() == 0 || (this.position.isOrdered() && this.position.getOrderedQuantity() == 0))
		{
			Activator.getDefault().log(LogService.LOG_WARNING, "Die Position hat keine Menge");
			return false;
		}
		Activator.getDefault().log(LogService.LOG_INFO, "Die Position ist vollst�ndig");
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
				final Barcode barcode = this.verifyInput(input);
				if (barcode instanceof Barcode)
				{
					if (this.userPanel.getReceiptWrapper().getReceipt().isInternal())
					{
						MessageDialog.showInformation(Activator.getDefault().getFrame(), userPanel.getProfile(), "Fehler", "Geldeinlagen und -entnahmen d�rfen keine anderen Positionen enthalten.", MessageDialog.TYPE_WARN, userPanel.getMainTabbedPane().isFailOver());
					}
					else
					{
						log(LogService.LOG_INFO, "Barcode: " + barcode.getName());
						if (barcode.getCode().startsWith(Barcode.PREFIX_CUSTOMER))
						{
							log(LogService.LOG_INFO, "Typ: " + barcode.getType().toString());
							this.position.getReceipt().setCustomerCode(barcode.getDetail());
							this.findAndRead(barcode);
						}
						else if (barcode.isEbook())
						{
							log(LogService.LOG_INFO, "Typ: " + barcode.getType().toString());
							this.position.setEbook(barcode.isEbook());
							this.setDefaultProductGroup();
							this.findAndRead(barcode);
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
							.getReceipt().getSettlement().getSalespoint().getProfile(), "Barcode ung�ltig",
							new int[] { MessageDialog.BUTTON_OK }, 0, userPanel.getMainTabbedPane().isFailOver());
					dialog.setMessage(text.toString());
					dialog.pack();
					dialog.setVisible(true);
				}
			}
			if (this.keyListener != null)
			{
				this.keyListener.keyPressed(event);
			}
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
			if (providerQuery.checkBarcode(barcode)) 
			{
				log(LogService.LOG_INFO, "Providerservice wird aufgerufen.");
				final IStatus status = providerQuery.findAndRead(barcode, this.position);
				if (status.getSeverity() == IStatus.CANCEL)
				{
					if (this.position.isOrdered())
					{
						this.replacePosition(Position.newInstance(this.position.getReceipt()));
						log(LogService.LOG_WARNING, "Artikel " + barcode.getProductCode() + " wurde nicht gefunden.");
						MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.position.getReceipt()
								.getSettlement().getSalespoint().getProfile(), "Nicht gefunden", status.getMessage(),
								MessageDialog.BUTTON_OK, userPanel.getMainTabbedPane().isFailOver());
					}
					else
					{
						if (!this.isPositionComplete())
						{
							log(LogService.LOG_WARNING, "Artikel " + barcode.getProductCode() + " wurde nicht gefunden.");
							MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.position.getReceipt()
									.getSettlement().getSalespoint().getProfile(), "Nicht gefunden", status.getMessage(),
									MessageDialog.BUTTON_OK, userPanel.getMainTabbedPane().isFailOver());
						}
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
				barcode = ((BarcodeVerifier) barcodeVerifier).verify(code);
				if (barcode != null)
				{
					barcode.updatePosition(position);
					break;
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
							+ barcode.getCode() + " wurde bereits erfasst.", MessageDialog.TYPE_WARN, userPanel.getMainTabbedPane().isFailOver());
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
				int quantity = 0;
				int orderedQuantity = 0;
				Iterator<Position> iter = positions.iterator();
				Long number = Long.valueOf(0L);
				while (iter.hasNext())
				{
					Position p = iter.next();
					if (p.isDeleted())
					{
						continue;
					}
					else
					{
						quantity += p.getQuantity();
						orderedQuantity = p.getOrderedQuantity() > orderedQuantity ? p.getOrderedQuantity() : orderedQuantity;
						number = p.getReceipt().getNumber();
					}
				}
				if (quantity == 0)
				{
					return true;
				}
				MessageDialog.showInformation(Activator.getDefault().getFrame(), this.position.getReceipt()
						.getSettlement().getSalespoint().getProfile(), "Bereits erfasst", "Der Abholfachbeleg "
						+ barcode.getCode() + " wurde bereits im geparkten Beleg " + number
						+ " erfasst.", MessageDialog.TYPE_WARN, userPanel.getMainTabbedPane().isFailOver());
				return false;
			}
		}
		return true;
	}

}
