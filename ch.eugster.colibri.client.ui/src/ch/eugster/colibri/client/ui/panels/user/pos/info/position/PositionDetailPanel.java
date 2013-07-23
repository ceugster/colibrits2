/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eugster.colibri.client.ui.panels.user.pos.info.position;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.BorderUIResource;

import ch.eugster.colibri.client.ui.actions.ClearAction;
import ch.eugster.colibri.client.ui.actions.DiscountAction;
import ch.eugster.colibri.client.ui.actions.DownAction;
import ch.eugster.colibri.client.ui.actions.PriceAction;
import ch.eugster.colibri.client.ui.actions.QuantityAction;
import ch.eugster.colibri.client.ui.actions.UpAction;
import ch.eugster.colibri.client.ui.actions.UserPanelProfileAction;
import ch.eugster.colibri.client.ui.buttons.PositionButton;
import ch.eugster.colibri.client.ui.events.PositionChangeMediator;
import ch.eugster.colibri.client.ui.panels.MainTabbedPane;
import ch.eugster.colibri.client.ui.panels.user.PositionWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.ui.panels.ProfilePanel;

/**
 * 
 * @author ceugster
 */
public class PositionDetailPanel extends ProfilePanel implements ActionListener, PropertyChangeListener, ListSelectionListener, TableModelListener
{
	public static final long serialVersionUID = 0l;

	private static NumberFormat quantityFormatter = NumberFormat.getIntegerInstance();

	private static NumberFormat priceFormatter = NumberFormat.getNumberInstance();

	private static NumberFormat discountFormatter = NumberFormat.getPercentInstance();

	private final Collection<ActionListener> actionListeners = new ArrayList<ActionListener>();

	private final Collection<ListSelectionListener> listSelectionListeners = new ArrayList<ListSelectionListener>();

	private final UserPanel userPanel;

	private PositionButton quantityButton;

	private PositionButton priceButton;

	private PositionButton discountButton;

	private PositionButton up;

	private PositionButton down;

	private JLabel quantityValueLabel;

	private JLabel priceValueLabel;

	private JLabel discountValueLabel;

	private JLabel productNameLabel;

	private JLabel currentTaxNameLabel;

	private JLabel optionNameLabel;

	private JLabel productValueLabel;

	private JLabel currentTaxValueLabel;

	private JLabel optionValueLabel;

	private java.awt.Color fg;

	private java.awt.Color bg;

	private java.awt.Color bgSelected;

	private final String[] positionProperties = new String[] { PositionWrapper.KEY_PROPERTY_PRODUCT, PositionWrapper.KEY_PROPERTY_CURRENT_TAX,
			PositionWrapper.KEY_PROPERTY_DISCOUNT, PositionWrapper.KEY_PROPERTY_OPTION, PositionWrapper.KEY_PROPERTY_PRICE,
			PositionWrapper.KEY_PROPERTY_PRODUCT_GROUP, PositionWrapper.KEY_PROPERTY_QUANTITY, PositionWrapper.KEY_PROPERTY_SEARCH_VALUE };

	public PositionDetailPanel(final UserPanel userPanel, final Profile profile)
	{
		super(profile);
		this.userPanel = userPanel;
		this.init();
	}

	public void actionPerformed(final ActionEvent event)
	{
		if (event.getActionCommand().equals(ClearAction.ACTION_COMMAND))
		{
			this.userPanel.getPositionWrapper().clearPosition();
		}
	}

	public void addActionListener(final ActionListener listener)
	{
		if (listener != null)
		{
			if (!this.actionListeners.contains(listener))
			{
				this.actionListeners.add(listener);
			}
		}
	}

	public void addListSelectionListener(final ListSelectionListener listener)
	{
		if (listener != null)
		{
			if (!this.listSelectionListeners.contains(listener))
			{
				this.listSelectionListeners.add(listener);
			}
		}
	}

	public PositionButton getPriceButton()
	{
		return this.priceButton;
	}

	public PositionButton getQuantityButton()
	{
		return this.quantityButton;
	}

	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource() instanceof Position)
		{
			final Position position = (Position) event.getSource();
			if (event.getPropertyName().equals(PositionWrapper.KEY_PROPERTY_PRODUCT))
			{
				this.updateProductLabel(position);
			}
			else if (event.getPropertyName().equals(PositionWrapper.KEY_PROPERTY_SEARCH_VALUE))
			{
				this.updateProductLabel(position);
			}
			else if (event.getPropertyName().equals(PositionWrapper.KEY_PROPERTY_CURRENT_TAX))
			{
				this.updateTaxLabel((CurrentTax) event.getNewValue());
			}
			else if (event.getPropertyName().equals(PositionWrapper.KEY_PROPERTY_DISCOUNT))
			{
				this.updateDiscountLabel(((Double) event.getNewValue()).doubleValue());
			}
			else if (event.getPropertyName().equals(PositionWrapper.KEY_PROPERTY_OPTION))
			{
				this.updateOptionLabel(position.getProductGroup(), (Option) event.getNewValue());
			}
			else if (event.getPropertyName().equals(PositionWrapper.KEY_PROPERTY_PRICE))
			{
				this.updatePriceLabel(((Double) event.getNewValue()).doubleValue());
			}
			else if (event.getPropertyName().equals(PositionWrapper.KEY_PROPERTY_PRODUCT_GROUP))
			{
				this.updateProductLabel(position);
			}
			else if (event.getPropertyName().equals(PositionWrapper.KEY_PROPERTY_QUANTITY))
			{
				this.updateQuantityLabel(((Integer) event.getNewValue()).intValue());
			}
		}
		else if (event.getSource() instanceof PositionChangeMediator)
		{
			if (event.getNewValue() instanceof Position)
			{
				this.updateValueLabels((Position) event.getNewValue());
			}
		}
	}

	public void removeActionListener(final ActionListener listener)
	{
		if (listener != null)
		{
			if (this.actionListeners.contains(listener))
			{
				this.actionListeners.remove(listener);
			}
		}
	}

	public void removeListSelectionListener(final ListSelectionListener listener)
	{
		if (listener != null)
		{
			if (this.listSelectionListeners.contains(listener))
			{
				this.listSelectionListeners.remove(listener);
			}
		}
	}

	public void tableChanged(final TableModelEvent event)
	{
		if (event.getSource() instanceof PositionListModel)
		{
			final PositionListModel model = (PositionListModel) event.getSource();
			final PositionListSelectionModel selectionModel = model.getSelectionListModel();
			this.up.setEnabled((model.getRowCount() > 0) && (selectionModel.getMinSelectionIndex() > 0));
			this.down.setEnabled((model.getRowCount() > 0) && (selectionModel.getMinSelectionIndex() < model.getRowCount() - 1));
		}
	}

	public void valueChanged(final ListSelectionEvent event)
	{
		if (event.getSource() instanceof PositionListSelectionModel)
		{
			final PositionListSelectionModel selectionModel = (PositionListSelectionModel) event.getSource();
			if ((selectionModel.getMinSelectionIndex() > -1)
					&& (selectionModel.getMaxSelectionIndex() < selectionModel.getListModel().getRowCount()))
			{
				this.userPanel.getPositionWrapper().replacePosition(
						selectionModel.getListModel().getPosition(selectionModel.getMinSelectionIndex()));
			}
			else
			{
				this.userPanel.getPositionWrapper().preparePosition(this.userPanel.getReceiptWrapper().getReceipt());
			}
			if (!this.userPanel.getCurrentState().equals(UserPanel.State.LOCKED))
			{
				for (final ListSelectionListener listener : this.listSelectionListeners)
				{
					listener.valueChanged(event);
				}
			}
		}
	}

	@Override
	protected void update()
	{
		this.bg = new java.awt.Color(this.profile.getValueLabelBg());
		this.bgSelected = new java.awt.Color(this.profile.getValueLabelBgSelected());
		this.fg = new java.awt.Color(this.profile.getValueLabelFg());

		this.quantityValueLabel.setFont(this.quantityValueLabel.getFont().deriveFont(this.profile.getValueLabelFontStyle(),
				this.profile.getValueLabelFontSize()));
		this.quantityValueLabel.setForeground(new java.awt.Color(this.profile.getValueLabelFg()));
		this.quantityValueLabel.setBackground(new java.awt.Color(this.profile.getValueLabelBg()));

		this.priceValueLabel.setFont(this.priceValueLabel.getFont().deriveFont(this.profile.getValueLabelFontStyle(),
				this.profile.getValueLabelFontSize()));
		this.priceValueLabel.setForeground(new java.awt.Color(this.profile.getValueLabelFg()));
		this.priceValueLabel.setBackground(new java.awt.Color(this.profile.getValueLabelBg()));

		this.discountValueLabel.setFont(this.discountValueLabel.getFont().deriveFont(this.profile.getValueLabelFontStyle(),
				this.profile.getValueLabelFontSize()));
		this.discountValueLabel.setForeground(new java.awt.Color(this.profile.getValueLabelFg()));
		this.discountValueLabel.setBackground(new java.awt.Color(this.profile.getValueLabelBg()));

		this.productNameLabel.setFont(this.productNameLabel.getFont().deriveFont(this.profile.getNameLabelFontStyle(),
				this.profile.getNameLabelFontSize()));
		this.productNameLabel.setForeground(new java.awt.Color(this.profile.getNameLabelFg()));
		this.productNameLabel.setBackground(new java.awt.Color(this.profile.getNameLabelBg()));

		this.productValueLabel.setFont(this.productValueLabel.getFont().deriveFont(this.profile.getValueLabelFontStyle(),
				this.profile.getValueLabelFontSize()));
		this.productValueLabel.setForeground(new java.awt.Color(this.profile.getValueLabelFg()));
		this.productValueLabel.setBackground(new java.awt.Color(this.profile.getValueLabelBg()));

		this.currentTaxNameLabel.setFont(this.currentTaxNameLabel.getFont().deriveFont(this.profile.getNameLabelFontStyle(),
				this.profile.getNameLabelFontSize()));
		this.currentTaxNameLabel.setForeground(new java.awt.Color(this.profile.getNameLabelFg()));
		this.currentTaxNameLabel.setBackground(new java.awt.Color(this.profile.getNameLabelBg()));

		this.currentTaxValueLabel.setFont(this.currentTaxValueLabel.getFont().deriveFont(this.profile.getValueLabelFontStyle(),
				this.profile.getValueLabelFontSize()));
		this.currentTaxValueLabel.setForeground(new java.awt.Color(this.profile.getValueLabelFg()));
		this.currentTaxValueLabel.setBackground(new java.awt.Color(this.profile.getValueLabelBg()));

		this.optionNameLabel.setFont(this.optionNameLabel.getFont().deriveFont(this.profile.getNameLabelFontStyle(),
				this.profile.getNameLabelFontSize()));
		this.optionNameLabel.setForeground(new java.awt.Color(this.profile.getNameLabelFg()));
		this.optionNameLabel.setBackground(new java.awt.Color(this.profile.getNameLabelBg()));

		this.optionValueLabel.setFont(this.optionValueLabel.getFont().deriveFont(this.profile.getValueLabelFontStyle(),
				this.profile.getValueLabelFontSize()));
		this.optionValueLabel.setForeground(new java.awt.Color(this.profile.getValueLabelFg()));
		this.optionValueLabel.setBackground(new java.awt.Color(this.profile.getValueLabelBg()));
	}

	private JPanel createArticlePanel(final Profile profile)
	{
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel childPanel = new JPanel();
		childPanel.setLayout(new BorderLayout());
		this.productNameLabel = this.createNameLabel(profile, "Warengruppe - Artikel", SwingConstants.LEADING);
		childPanel.add(this.productNameLabel, BorderLayout.NORTH);
		this.productValueLabel = this.createValueLabel(profile, SwingConstants.LEADING);
		childPanel.add(this.productValueLabel, BorderLayout.CENTER);
		panel.add(childPanel, BorderLayout.CENTER);

		final JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());

		childPanel = new JPanel();
		childPanel.setLayout(new BorderLayout());
		this.currentTaxNameLabel = this.createNameLabel(profile, "MWST", SwingConstants.CENTER);
		childPanel.add(this.currentTaxNameLabel, BorderLayout.NORTH);
		this.currentTaxValueLabel = this.createValueLabel(profile, SwingConstants.CENTER);
		childPanel.add(this.currentTaxValueLabel, BorderLayout.CENTER);
		rightPanel.add(childPanel, BorderLayout.WEST);

		childPanel = new JPanel();
		childPanel.setLayout(new BorderLayout());
		this.optionNameLabel = this.createNameLabel(profile, "Option", SwingConstants.CENTER);
		childPanel.add(this.optionNameLabel, BorderLayout.NORTH);
		this.optionValueLabel = this.createValueLabel(profile, SwingConstants.CENTER);
		childPanel.add(this.optionValueLabel, BorderLayout.CENTER);
		rightPanel.add(childPanel, BorderLayout.EAST);

		panel.add(rightPanel, BorderLayout.EAST);

		return panel;
	}

	private JLabel createNameLabel(final Profile profile, final String text, final int horizontalAlignment)
	{
		final JLabel label = new JLabel(text);
		final FontMetrics metrics = label.getFontMetrics(label.getFont());
		label.setPreferredSize(new Dimension(metrics.stringWidth(text) + 6, metrics.getHeight()));
		label.setBorder(new EmptyBorder(2, 2, 2, 2));
		label.setOpaque(true);
		label.setBackground(new java.awt.Color(profile.getNameLabelBg()));
		label.setForeground(new java.awt.Color(profile.getNameLabelFg()));
		label.setFont(label.getFont().deriveFont(profile.getNameLabelFontStyle(), profile.getNameLabelFontSize()));
		label.setHorizontalAlignment(horizontalAlignment);
		// label.setFocusable(false);
		return label;
	}

	private JPanel createQuantityPriceDiscountPanel(final Profile profile)
	{
		/*
		 * Button Area
		 */
		final JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 5));

		JPanel childPanel = new JPanel();
		childPanel.setLayout(new BorderLayout());

		UserPanelProfileAction action = new QuantityAction(this.userPanel, profile);
		this.userPanel.addStateChangeListener(action);
		this.userPanel.getValueDisplay().addPropertyChangeListener("value", action);
		this.quantityButton = new PositionButton(action, this.userPanel, profile);
		childPanel.add(this.quantityButton, BorderLayout.CENTER);

		this.quantityValueLabel = this.createValueLabel(profile, SwingConstants.RIGHT);
		childPanel.add(this.quantityValueLabel, BorderLayout.SOUTH);
		panel.add(childPanel);

		childPanel = new JPanel();
		childPanel.setLayout(new BorderLayout());

		action = new PriceAction(this.userPanel, profile);
		this.userPanel.addStateChangeListener(action);
		this.userPanel.getValueDisplay().addPropertyChangeListener("value", action);
		this.priceButton = new PositionButton(action, this.userPanel, profile);
		childPanel.add(this.priceButton, BorderLayout.CENTER);

		this.priceValueLabel = this.createValueLabel(profile, SwingConstants.RIGHT);
		childPanel.add(this.priceValueLabel, BorderLayout.SOUTH);
		panel.add(childPanel);

		childPanel = new JPanel();
		childPanel.setLayout(new BorderLayout());

		action = new DiscountAction(this.userPanel, profile);
		this.userPanel.addStateChangeListener(action);
		this.userPanel.getValueDisplay().addPropertyChangeListener("value", action);
		this.discountButton = new PositionButton(action, this.userPanel, profile);
		this.discountButton.addActionListener(this.userPanel);
		childPanel.add(this.discountButton, BorderLayout.CENTER);

		this.discountValueLabel = this.createValueLabel(profile, SwingConstants.RIGHT);
		childPanel.add(this.discountValueLabel, BorderLayout.SOUTH);
		panel.add(childPanel);

		childPanel = new JPanel();
		childPanel.setLayout(new BorderLayout());

		final UpAction upAction = new UpAction(this.userPanel, profile, this.userPanel.getPositionListPanel().getModel(), this.userPanel
				.getPositionListPanel().getModel().getSelectionListModel());
		this.userPanel.addStateChangeListener(upAction);
		this.userPanel.getValueDisplay().addPropertyChangeListener("value", upAction);
		this.up = new PositionButton(upAction, this.userPanel, profile);
		this.up.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				for (final ActionListener listener : PositionDetailPanel.this.actionListeners)
				{
					listener.actionPerformed(event);
				}
			}
		});
		this.userPanel.getPositionListPanel().getModel().addTableModelListener(upAction);
		this.userPanel.getPositionListPanel().getModel().getSelectionListModel().addListSelectionListener(upAction);
		childPanel.add(this.up, BorderLayout.CENTER);
		panel.add(childPanel);

		childPanel = new JPanel();
		childPanel.setLayout(new BorderLayout());

		final DownAction downAction = new DownAction(this.userPanel, profile, this.userPanel.getPositionListPanel().getModel(), this.userPanel
				.getPositionListPanel().getModel().getSelectionListModel());
		this.userPanel.addStateChangeListener(downAction);
		this.userPanel.getValueDisplay().addPropertyChangeListener("value", downAction);
		this.down = new PositionButton(downAction, this.userPanel, profile);
		this.down.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				for (final ActionListener listener : PositionDetailPanel.this.actionListeners)
				{
					listener.actionPerformed(event);
				}
			}
		});
		this.userPanel.getPositionListPanel().getModel().addTableModelListener(downAction);
		this.userPanel.getPositionListPanel().getModel().getSelectionListModel().addListSelectionListener(downAction);
		childPanel.add(this.down, BorderLayout.CENTER);
		panel.add(childPanel);
		return panel;
	}

	private JLabel createValueLabel(final Profile profile, final int textAlign)
	{
		final JLabel label = new JLabel();
		label.setBorder(BorderUIResource.getEtchedBorderUIResource());
		label.setOpaque(true);
		label.setForeground(this.fg);
		label.setFont(label.getFont().deriveFont(profile.getValueLabelFontStyle(), profile.getValueLabelFontSize()));
		label.setHorizontalAlignment(textAlign);
		// label.setFocusable(false);
		return label;
	}

	private void init()
	{
		int minFractionDigits = 2;
		int maxFractionDigits = 2;

		this.bg = new java.awt.Color(this.profile.getValueLabelBg());
		this.bgSelected = new java.awt.Color(this.profile.getValueLabelBgSelected());
		this.fg = new java.awt.Color(this.profile.getValueLabelFg());

		try
		{
			final java.util.Currency currency = Currency.getInstance(this.userPanel.getMainTabbedPane().getSalespoint().getPaymentType().getCurrency()
					.getCode());
			minFractionDigits = currency.getDefaultFractionDigits();
			maxFractionDigits = currency.getDefaultFractionDigits();
		}
		catch (final IllegalArgumentException e)
		{

		}
		PositionDetailPanel.priceFormatter.setMinimumFractionDigits(minFractionDigits);
		PositionDetailPanel.priceFormatter.setMaximumFractionDigits(maxFractionDigits);
		PositionDetailPanel.discountFormatter.setMinimumFractionDigits(0);
		PositionDetailPanel.discountFormatter.setMaximumFractionDigits(2);

		this.setLayout(new BorderLayout());
		this.add(this.createArticlePanel(this.profile), BorderLayout.NORTH);
		this.add(this.createQuantityPriceDiscountPanel(this.profile), BorderLayout.CENTER);

		if (this.productValueLabel.getText().isEmpty())
		{
			this.updateProductLabel(null);
		}
		if (this.currentTaxValueLabel.getText().isEmpty())
		{
			final Tax tax = this.userPanel.getSalespoint().getProposalTax();
			if (tax != null)
			{
				this.updateTaxLabel(tax.getCurrentTax());
			}
		}
		if (this.optionValueLabel.getText().isEmpty())
		{
			this.updateOptionLabel(null, Position.Option.ARTICLE);
		}
		if (this.quantityValueLabel.getText().isEmpty())
		{
			this.updateQuantityLabel(this.userPanel.getSalespoint().getProposalQuantity());
		}
		if (this.priceValueLabel.getText().isEmpty())
		{
			this.updatePriceLabel(0d);
		}
		if (this.discountValueLabel.getText().isEmpty())
		{
			this.updateDiscountLabel(0d);
		}

		new PositionChangeMediator(this.userPanel, this, this.positionProperties);

		this.update();

		this.discountButton.requestFocus();
	}

	private void updateDiscountLabel(final double discount)
	{
		this.discountValueLabel.setText(PositionDetailPanel.discountFormatter.format(discount));

		// if (discount == 0d)
		// this.discountValueLabel.setBackground(this.bgSelected);
		// else
		// this.discountValueLabel.setBackground(this.bg);
	}

	private void updateOptionLabel(final ProductGroup productGroup, final Option option)
	{
		if (option != null)
		{
			this.optionValueLabel.setText(option.toCode());
		}
		if (productGroup == null)
		{
			this.productValueLabel.setBackground(new java.awt.Color(this.profile.getValueLabelBgSelected()));
		}
		else
		{
			this.productValueLabel.setBackground(new java.awt.Color(this.profile.getValueLabelBg()));
		}

		if (option == null)
		{
			this.optionValueLabel.setBackground(this.bgSelected);
		}
		else
		{
			this.optionValueLabel.setBackground(this.bg);
		}
	}

	private void updatePriceLabel(final double price)
	{
		this.priceValueLabel.setText(PositionDetailPanel.priceFormatter.format(price));

		if (price == 0d)
		{
			this.priceValueLabel.setBackground(this.bgSelected);
		}
		else
		{
			this.priceValueLabel.setBackground(this.bg);
		}
		this.productNameLabel.setForeground(price == 0D ? Color.RED : Color.BLACK);
	}

	private void updateProductLabel(final Position position)
	{
		StringBuilder value = new StringBuilder("");

		if (position != null)
		{
			if (position.getProductGroup() != null)
			{
				final ProductGroup productGroup = position.getProductGroup();
				if (productGroup.getCode() != null)
				{
					value = value.append(productGroup.getCode());
					// if (position.getProduct() == null)
					// {
					// if (productGroup.getName() != null)
					// {
					// value = value.append(" - " + productGroup.getName());
					// }
					// }
				}
				else if (productGroup.getName() != null)
				{
					value.append(productGroup.getName());
				}
			}

			if (position.getProduct() != null)
			{
				final Product product = position.getProduct();
				if (value.length() > 0)
				{
					value = value.append(" - ");
				}
				if (position.getOption() != null && position.getOption().equals(Option.PAYED_INVOICE))
				{
					value.append(product.getInvoiceNumber());
				}
				else if (product.getAuthor().isEmpty() && product.getTitle().isEmpty())
				{
					if (product.getCode().trim().length() > 0)
					{
						if (value.length() > 0)
						{
							value = value.append(" - ");
						}
						value = value.append(product.getCode());
					}
				}
				else
				{
					if (!product.getAuthor().isEmpty())
					{
						value = value.append(product.getAuthor());
						if (!product.getTitle().isEmpty())
						{
							value = value.append(", ");
						}
					}
					if (!product.getTitle().isEmpty())
					{
						value = value.append(product.getTitle());
					}
				}
			}
			else
			{
				if (position.getSearchValue() != null)
				{
					value = value.append((value.length() == 0 ? position.getSearchValue() : " - " + position.getSearchValue()));
				}
			}
			this.productValueLabel.setText(value.toString());
			this.productNameLabel.setForeground(position.getProductGroup() == null ? Color.RED : Color.BLACK);
		}
		this.productValueLabel.setText(value.toString());
	}

	private void updateQuantityLabel(final int quantity)
	{
		// if (quantity == 0) quantity = 1;
		// if (quantity != 0 || quantity !=
		// this.userPanel.getSalespoint().getProposalQuantity())
		// {
		this.quantityValueLabel.setText(PositionDetailPanel.quantityFormatter.format(quantity));
		// }

		if (quantity == 0)
		{
			this.quantityValueLabel.setBackground(this.bgSelected);
		}
		else
		{
			this.quantityValueLabel.setBackground(this.bg);
		}
	}

	private void updateTaxLabel(final CurrentTax currentTax)
	{
		if (currentTax == null)
		{
			this.currentTaxValueLabel.setText(" ");
		}
		else
		{
			this.currentTaxValueLabel.setText(currentTax.getTax().getTaxRate().getCode());
		}

		if (currentTax == null)
		{
			this.currentTaxValueLabel.setBackground(this.bgSelected);
		}
		else
		{
			this.currentTaxValueLabel.setBackground(this.bg);
		}
		this.currentTaxNameLabel.setForeground(currentTax == null ? Color.RED : Color.BLACK);
	}

	private void updateValueLabels(final Position position)
	{
		this.updateProductLabel(position);
		this.updateTaxLabel(position.getCurrentTax());
		this.updateDiscountLabel(position.getDiscount());
		this.updateOptionLabel(position.getProductGroup(), position.getOption());
		this.updatePriceLabel(position.getPrice());
		this.updateQuantityLabel(position.getQuantity());
	}
}
