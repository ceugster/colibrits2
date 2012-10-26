/*
 * Created on 17.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.position;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.eclipse.core.runtime.Status;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.DiscountAction;
import ch.eugster.colibri.client.ui.actions.PaymentTypeAction;
import ch.eugster.colibri.client.ui.buttons.ConfigurableButton;
import ch.eugster.colibri.client.ui.events.ReceiptChangeMediator;
import ch.eugster.colibri.client.ui.panels.user.ReceiptWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.AmountType;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.ui.buttons.HTMLButton;

public class PositionListModel extends AbstractTableModel implements PropertyChangeListener, ActionListener
{
	public static final long serialVersionUID = 0l;

	private PositionListSelectionModel selectionListModel;

	private final NumberFormat numberFormatter = NumberFormat.getNumberInstance();

	private final NumberFormat percentageFormatter = NumberFormat.getPercentInstance();

	private final TableColumn[] tableColumns = new TableColumn[Column.values().length];

	private final UserPanel userPanel;

	private java.util.Currency currency;

	private final ReceiptChangeMediator receiptChangeMediator;

	private final String[] receiptProperties = new String[] { ReceiptWrapper.KEY_PROPERTY_POSITIONS };

	public PositionListModel(final UserPanel userPanel)
	{
		this.userPanel = userPanel;
		this.createTableColumns();
		this.receiptChangeMediator = new ReceiptChangeMediator(userPanel, this, this.receiptProperties);
		userPanel.getPositionWrapper().setKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(final KeyEvent e)
			{
				PositionListModel.this.keyPressed(e);
			}
		});
	}
	
	private Position[] getModelData()
	{
		Receipt receipt = this.userPanel.getReceiptWrapper().getReceipt();
		if (receipt == null)
		{
			return new Position[0];
		}
		else
		{
			Position[] positions = receipt.getPositions().toArray(new Position[0]);
			Arrays.sort(positions);
			return positions;
		}
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (this.userPanel.getCurrentState().equals(UserPanel.State.POSITION_INPUT))
		{
			if (event.getSource() instanceof HTMLButton)
			{
				if (event.getActionCommand().equals(DiscountAction.ACTION_COMMAND))
				{
					if (this.selectionListModel.getMinSelectionIndex() == -1)
					{
						this.fireTableDataChanged();
					}
				}
				else
				{
					this.testForTableUpdate();
				}
			}
		}
		else if (this.userPanel.getCurrentState().equals(UserPanel.State.PAYMENT_INPUT))
		{
			if (event.getSource() instanceof ConfigurableButton)
			{
				ConfigurableButton button = (ConfigurableButton) event.getSource();
				if (button.getAction() instanceof PaymentTypeAction)
				{
					this.fireTableDataChanged();
				}
			}
		}
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex)
	{
		switch (Column.values()[columnIndex])
		{
			case ARTICLE:
				return String.class;
			case QUANTITY:
				return Long.class;
			case PRICE:
				return Double.class;
			case DISCOUNT:
				return Double.class;
			case AMOUNT:
				return Double.class;
			case TAX_CODE:
				return Integer.class;
			case OPTION:
				return String.class;
			case CUSTOMER_CARD:
				return String.class;
		}
		return null;
	}

	@Override
	public int getColumnCount()
	{
		return Column.values().length;
	}

	@Override
	public String getColumnName(final int colIndex)
	{
		return Column.values()[colIndex].toString();
	}

	public Column[] getColumns()
	{
		return Column.values();
	}

	public Position getPosition(final int rowIndex)
	{
		Position[] positions = this.getModelData();
		if ((rowIndex >= 0) && (rowIndex < positions.length))
		{
			return positions[rowIndex];
		}
		return null;
	}

	@Override
	public int getRowCount()
	{
		return this.getModelData().length;
	}

	public PositionListSelectionModel getSelectionListModel()
	{
		return this.selectionListModel;
	}

	public TableColumn[] getTableColumns()
	{
		return this.tableColumns;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex)
	{
		final Position[] positions = this.getModelData();

		if ((this.currency == null)
				|| !this.currency.getCurrencyCode().equals(positions[rowIndex].getForeignCurrency().getCode()))
		{
			this.currency = java.util.Currency.getInstance(positions[rowIndex].getForeignCurrency().getCode());
		}
		if (rowIndex < positions.length)
		{
			switch (Column.values()[columnIndex])
			{
				case ARTICLE:
					final StringBuilder article = new StringBuilder();
					if (positions[rowIndex].getProduct() == null)
					{
						article.append(new StringBuffer(positions[rowIndex].getProductGroup().getCode()));
					}
					else
					{
						if (positions[rowIndex].getOption().equals(Position.Option.PAYED_INVOICE))
						{
							article.append(positions[rowIndex].getProductGroup().getName() + " - "
									+ positions[rowIndex].getProduct().getInvoiceNumber());
						}
						else
						{
							article.append(positions[rowIndex].getProduct().getExternalProductGroup().getCode());
						}
					}

					if (positions[rowIndex].getProduct() == null)
					{
						if (positions[rowIndex].getSearchValue() == null)
						{
							article.append(" - ").append(positions[rowIndex].getProductGroup().getName());
						}
						else
						{
							article.append(" - ").append(positions[rowIndex].getSearchValue());
						}
					}
					else
					{
						StringBuilder title = new StringBuilder();
						if (!positions[rowIndex].getProduct().getAuthor().isEmpty())
						{
							title = title.append(this.shortenAuthorName(positions[rowIndex].getProduct().getAuthor()));
							if (!positions[rowIndex].getProduct().getTitle().isEmpty())
							{
								title = title.append(", ");
							}
						}
						if (!positions[rowIndex].getProduct().getTitle().isEmpty())
						{
							title = title.append(positions[rowIndex].getProduct().getTitle());
						}

						if (title.length() == 0)
						{
							title = title.append(positions[rowIndex].getProduct().getCode());
						}

						if (title.length() > 0)
						{
							article.append(" - ").append(title);
						}
					}
					return article.toString();
				case QUANTITY:
					return positions[rowIndex].getQuantity();
				case PRICE:
					this.numberFormatter.setMinimumFractionDigits(this.currency.getDefaultFractionDigits());
					this.numberFormatter.setMaximumFractionDigits(this.currency.getDefaultFractionDigits());
					return this.numberFormatter.format(positions[rowIndex].getPrice());
				case DISCOUNT:
				{
					double discount = positions[rowIndex].getDiscount();
					return discount == 0D ? "" : this.percentageFormatter.format(positions[rowIndex].getDiscount());
				}
				case AMOUNT:
					this.numberFormatter.setMinimumFractionDigits(this.currency.getDefaultFractionDigits());
					this.numberFormatter.setMaximumFractionDigits(this.currency.getDefaultFractionDigits());
					return this.numberFormatter.format(positions[rowIndex].getAmount(
							Receipt.QuotationType.DEFAULT_CURRENCY, AmountType.NETTO));
				case TAX_CODE:
				{
					double percentage = positions[rowIndex].getCurrentTax().getPercentage();
					System.out.println("" + percentage);
					percentageFormatter.setMinimumFractionDigits(0);
					percentageFormatter.setMaximumFractionDigits(2);
					return percentageFormatter.format(percentage);
				}
				case OPTION:
				{
					Position.Option option = positions[rowIndex].getOption();
					return  option == null ? "" : option.toCode();
				}
				case CUSTOMER_CARD:
					return "N";
			}
		}
		return null;
	}

	public void keyPressed(final KeyEvent event)
	{
		if (this.userPanel.getCurrentState().equals(UserPanel.State.POSITION_INPUT))
		{
			this.testForTableUpdate();
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource().equals(this.receiptChangeMediator))
		{
			if (event.getNewValue() instanceof Receipt)
			{
				this.fireTableDataChanged();
			}
		}
		else if (event.getSource() instanceof Receipt)
		{
			if (event.getPropertyName().equals(ReceiptWrapper.KEY_PROPERTY_POSITIONS))
			{

			}
		}
	}

	public void setSelectionListModel(final PositionListSelectionModel selectionListModel)
	{
		this.selectionListModel = selectionListModel;
	}

	private void addPosition(final Position newPosition)
	{
		Collection<Position> positions = this.userPanel.getReceiptWrapper().getReceipt().getPositions();
		if (!this.mergePosition(positions, newPosition))
		{
			this.userPanel.getReceiptWrapper().getReceipt().getAllPositions().add(newPosition);
			Position[] data = this.getModelData();
			final int row = data.length - 1;
			this.fireTableRowsInserted(0,0);
			this.userPanel.getPositionWrapper().preparePosition(this.userPanel.getReceiptWrapper().getReceipt());
		}
		displayPosition(newPosition);
	}

	private void displayPosition(Position position)
	{
		sendEvent(position);
	}
	
	private Event getEvent(ServiceReference<EventAdmin> reference, final String topics, final Position position)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
		properties.put(EventConstants.SERVICE, reference);
		properties.put(EventConstants.SERVICE_ID, reference.getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		properties.put(IPrintable.class.getName(), position);
		properties.put("status", Status.OK_STATUS);
		return new Event(topics, properties);
	}

	private void sendEvent(final Position position)
	{
		ServiceTracker<EventAdmin, EventAdmin> tracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
		try
		{
			tracker.open();
			final EventAdmin eventAdmin = (EventAdmin) tracker.getService();
			if (eventAdmin != null)
			{
				eventAdmin.sendEvent(this.getEvent(tracker.getServiceReference(), "ch/eugster/colibri/client/add/position", position));
			}
		}
		finally
		{
			tracker.close();
		}
	}

	private void createTableColumns()
	{
		for (int i = 0; i < Column.values().length; i++)
		{
			this.tableColumns[i] = new TableColumn();
			this.tableColumns[i] = new TableColumn();
			this.tableColumns[i].setHeaderValue(Column.values()[i].toString());
			this.tableColumns[i].setModelIndex(i);
			final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(Column.values()[i].align());
			this.tableColumns[i].setCellRenderer(renderer);
			this.tableColumns[i].setResizable(true);
		}
	}

	private boolean mergePosition(final Collection<Position> positions, final Position newPosition)
	{
		for (final Position position : positions)
		{
			if ((position.getProduct() == null) && (newPosition.getProduct() != null))
			{
				continue;
			}
			if ((position.getProduct() != null) && (newPosition.getProduct() == null))
			{
				continue;
			}
			if ((position.getProduct() != null) && (newPosition.getProduct() != null))
			{
				if (!position.getProduct().getCode().equals(newPosition.getProduct().getCode()))
				{
					continue;
				}
			}

			if (!position.getForeignCurrency().equals(newPosition.getForeignCurrency()))
			{
				continue;
			}
			if (!position.getCurrentTax().equals(newPosition.getCurrentTax()))
			{
				continue;
			}
			if (position.getDiscount() != newPosition.getDiscount())
			{
				continue;
			}
			if (!position.getOption().equals(newPosition.getOption()))
			{
				continue;
			}
			if (position.getPrice() != newPosition.getPrice())
			{
				continue;
			}
			if (!position.getProductGroup().equals(newPosition.getProductGroup()))
			{
				continue;
			}
			if (position.getForeignCurrencyQuotation() != newPosition.getForeignCurrencyQuotation())
			{
				continue;
			}
			if (position.getForeignCurrencyRoundFactor() != newPosition.getForeignCurrencyRoundFactor())
			{
				continue;
			}
			if (Math.abs(position.getQuantity() + newPosition.getQuantity()) == Math.abs(position.getQuantity())
					+ Math.abs(newPosition.getQuantity()))
			{
				position.setQuantity(position.getQuantity() + newPosition.getQuantity());
				this.fireTableDataChanged();
				this.userPanel.getPositionWrapper().preparePosition(this.userPanel.getReceiptWrapper().getReceipt());
				return true;
			}
		}
		return false;
	}

	private void replacePosition(final Position updatedPosition)
	{
		final int index = this.selectionListModel.getMinSelectionIndex();
		final Position[] livingPositions = this.getModelData();
		livingPositions[index] = updatedPosition;
		final Position[] allPositions = this.userPanel.getReceiptWrapper().getReceipt().getAllPositions()
				.toArray(new Position[0]);
		for (int i = 0; i < allPositions.length; i++)
		{
			if (!allPositions[i].isDeleted())
			{
				allPositions[i] = null;
			}
		}

		final Collection<Position> positions = new Vector<Position>();
		for (final Position position : allPositions)
		{
			if (position != null)
			{
				positions.add(position);
			}
		}

		for (final Position position : livingPositions)
		{
			positions.add(position);
		}

		this.userPanel.getReceiptWrapper().getReceipt().setPositions(positions);

		this.fireTableDataChanged();
		this.selectionListModel.clearSelection();
		displayPosition(updatedPosition);
		this.userPanel.getPositionWrapper().preparePosition(this.userPanel.getReceiptWrapper().getReceipt());
	}

	private String shortenAuthorName(final String oldAuthorName)
	{
		String newAuthorName = oldAuthorName;
		if (oldAuthorName.contains(","))
		{
			newAuthorName = oldAuthorName.substring(0, oldAuthorName.indexOf(","));
		}
		return newAuthorName;
	}

	private void testForTableUpdate()
	{
		if (this.userPanel.getPositionWrapper().isPositionComplete())
		{
			final Position newPosition = this.userPanel.getPositionWrapper().getPosition();
			if (this.selectionListModel.getMinSelectionIndex() > -1)
			{
				this.replacePosition(newPosition);
			}
			else
			{
				this.addPosition(newPosition);
			}
		}
	}

	public enum Column
	{
		ARTICLE, QUANTITY, PRICE, DISCOUNT, AMOUNT, TAX_CODE, OPTION, CUSTOMER_CARD;

		public int align()
		{
			switch (this)
			{
				case ARTICLE:
				{
					return SwingConstants.LEFT;
				}
				case QUANTITY:
				{
					return SwingConstants.RIGHT;
				}
				case PRICE:
				{
					return SwingConstants.RIGHT;
				}
				case DISCOUNT:
				{
					return SwingConstants.RIGHT;
				}
				case AMOUNT:
				{
					return SwingConstants.RIGHT;
				}
				case TAX_CODE:
				{
					return SwingConstants.RIGHT;
				}
				case OPTION:
				{
					return SwingConstants.LEFT;
				}
				case CUSTOMER_CARD:
				{
					return SwingConstants.LEFT;
				}
				default:
				{
					throw new RuntimeException("Ungültige Spalte");
				}
			}
		}

		@Override
		public String toString()
		{
			switch (this)
			{
				case ARTICLE:
				{
					return "Artikel";
				}
				case QUANTITY:
				{
					return "Menge";
				}
				case PRICE:
				{
					return "Preis";
				}
				case DISCOUNT:
				{
					return "Rabatt";
				}
				case AMOUNT:
				{
					return "Betrag";
				}
				case TAX_CODE:
				{
					return "M";
				}
				case OPTION:
				{
					return "O";
				}
				case CUSTOMER_CARD:
				{
					return "K";
				}
				default:
					throw new RuntimeException("Ungültige Spalte");
			}
		}

	}
}
