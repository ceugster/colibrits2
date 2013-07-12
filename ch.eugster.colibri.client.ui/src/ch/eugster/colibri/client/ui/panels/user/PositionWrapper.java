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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

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
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderIdService;
import ch.eugster.colibri.provider.service.ProviderInterface;
import ch.eugster.colibri.voucher.client.VoucherService;

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

	private final Collection<PositionChangeListener> positionChangeListeners = new ArrayList<PositionChangeListener>();

	private final String[] propertyNames = new String[] { ReceiptWrapper.KEY_PROPERTY_POSITIONS };

	private Position position;

	private final ServiceTracker<ProviderInterface, ProviderInterface> providerInterfaceTracker;

	private final ServiceTracker<ProviderIdService, ProviderIdService> providerIdServiceTracker;

	private final ServiceTracker<BarcodeVerifier, BarcodeVerifier> barcodeVerifierTracker;

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private final ServiceTracker<LogService, LogService> logServiceTracker;

	private final ValueDisplay valueDisplay;

	private boolean freeCopy;
	
	public PositionWrapper(final UserPanel userPanel, final ValueDisplay valueDisplay)
	{
		this.valueDisplay = valueDisplay;

		userPanel.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(final KeyEvent e)
			{
				PositionWrapper.this.keyPressed(e);
			}
		});

		new ReceiptChangeMediator(userPanel, this, this.propertyNames);

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(),
				LogService.class, null);
		this.logServiceTracker.open();

		this.barcodeVerifierTracker = new ServiceTracker<BarcodeVerifier, BarcodeVerifier>(Activator.getDefault().getBundle().getBundleContext(),
				BarcodeVerifier.class, null);
		this.barcodeVerifierTracker.open();

		this.providerInterfaceTracker = new ServiceTracker<ProviderInterface, ProviderInterface>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderInterface.class, null);
		this.providerInterfaceTracker.open();

		this.providerIdServiceTracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderIdService.class, null);
		this.providerIdServiceTracker.open();

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

	public void clearPosition()
	{
		final Position newPosition = Position.reinitialize(this.position);
		this.firePositionChangeEvent(new PositionChangeEvent(this.position, newPosition));
		this.position = newPosition;
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		this.providerIdServiceTracker.close();
		this.providerInterfaceTracker.close();
		this.logServiceTracker.close();
	}

	public boolean isFreeCopy()
	{
		return freeCopy;
	}
	
	public void setFreeCopy(boolean freeCopy)
	{
		this.freeCopy = freeCopy;
	}
	
	public boolean doesPositionNeedPrice()
	{
		if (this.freeCopy)
		{
			return false;
		}
		return this.position.getPrice() == 0d ? true : false;
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
			return false;
		}
		if (this.position.getProductGroup() == null)
		{
			return false;
		}
		if (this.position.getProduct() != null)
		{
			if (this.position.getOption() == null || !this.position.getOption().equals(Position.Option.PAYED_INVOICE))
			{
				if (this.position.getProduct().getExternalProductGroup() == null)
				{
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
					return false;
				}
			}
		}
		if (this.position.getOption() == null)
		{
			if (this.position.getProductGroup().getProductGroupType().equals(ProductGroupType.SALES_RELATED))
			{
				return false;
			}
		}
		if (this.position.getPrice() == 0d)
		{
			return this.freeCopy;
		}
		if (this.position.getQuantity() == 0)
		{
			return false;
		}
		return true;
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
			final String input = this.valueDisplay.readAndInitDisplay();
			if ((input != null) && (input.length() > 0))
			{
				if (input.startsWith("GCD"))
				{
					checkBalance(input);
				}
				else
				{
					final Barcode barcode = this.verifyInput(input);
					if (barcode instanceof Barcode)
					{
						if (this.verifyOrderedNotYetScanned(barcode))
						{
							this.setProviderId(barcode);
							this.findAndRead(barcode);
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
			}
		}
	}

	private void checkBalance(String code)
	{
		ServiceTracker<VoucherService, VoucherService> tracker = new ServiceTracker<VoucherService, VoucherService>(Activator.getDefault().getBundle().getBundleContext(), VoucherService.class, null);
		tracker.open();
		VoucherService service = tracker.getService();
		if (service == null)
		{
			final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
					.getReceipt().getSettlement().getSalespoint().getProfile(), "Service nicht verfügbar",
					new int[] { MessageDialog.BUTTON_OK }, 0);
			dialog.setMessage("Der Service für die Abfrage von eGutscheinen ist nicht verfügbar.");
			dialog.pack();
			dialog.setVisible(true);
		}
		else
		{
			try
			{
				double value = service.getAccountBalance(code);
				final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
						.getReceipt().getSettlement().getSalespoint().getProfile(), "Kontostand " + code,
						new int[] { MessageDialog.BUTTON_OK }, 0);
				dialog.setMessage("Der aktuelle Kontostand beträgt " + NumberFormat.getNumberInstance().format(value) + ".");
				dialog.pack();
				dialog.setVisible(true);
			}
			catch (Exception e)
			{
				final MessageDialog dialog = new MessageDialog(Activator.getDefault().getFrame(), this.position
						.getReceipt().getSettlement().getSalespoint().getProfile(), "Fehler während der Abfrage",
						new int[] { MessageDialog.BUTTON_OK }, 0);
				dialog.setMessage("Während der Abfrage ist ein Fehler aufgetreten.\n\n" + e.getLocalizedMessage());
				dialog.pack();
				dialog.setVisible(true);
			}
		}
		tracker.close();
	}
	
	public Position preparePosition(final Receipt receipt)
	{
		final Position newPosition = Position.newInstance(receipt);
		final ProviderIdService providerIdService = (ProviderIdService) this.providerIdServiceTracker.getService();
		if (providerIdService != null)
		{
			if (providerIdService.getConfiguration().updateLocalItems())
			{
				newPosition.setProvider(providerIdService.getProviderId());
				newPosition.setBookProvider(providerIdService.getConfiguration().updateLocalItems());
			}
		}
		this.firePositionChangeEvent(new PositionChangeEvent(this.position, newPosition));
		this.position = newPosition;
		return this.position;
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

	public Position replacePosition(final Position position)
	{
		this.firePositionChangeEvent(new PositionChangeEvent(this.position, position));
		this.position = position;
		return this.position;
	}

	public void setKeyListener(final KeyListener keyListener)
	{
		this.keyListener = keyListener;
	}

	private void findAndRead(final Barcode barcode)
	{
		final ProviderInterface providerInterface = (ProviderInterface) this.providerInterfaceTracker.getService();
		if (providerInterface != null)
		{
			final IStatus status = providerInterface.findAndRead(barcode, this.position);
			if (status.getSeverity() == IStatus.CANCEL)
			{
				MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.position.getReceipt()
						.getSettlement().getSalespoint().getProfile(), "Nicht gefunden", status.getMessage(),
						MessageDialog.BUTTON_OK);
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
		final ProviderIdService providerIdService = (ProviderIdService) this.providerIdServiceTracker.getService();
		if (providerIdService != null)
		{
			this.position.setProvider(providerIdService.getProviderId());
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
						break;
					}
				}
			}
		}
		return barcode;
	}

	private boolean verifyOrderedNotYetScanned(final Barcode barcode)
	{
		if (barcode.getType().equals(Barcode.Type.ORDER))
		{
			// Search current receipt
			Collection<Position> positions = this.getPosition().getReceipt().getPositions();
			for (final Position position : positions)
			{
				if (position.getSearchValue().equals(barcode.getCode()) && (position != this.getPosition()))
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
