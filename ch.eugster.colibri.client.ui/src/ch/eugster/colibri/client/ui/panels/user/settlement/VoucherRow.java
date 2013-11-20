package ch.eugster.colibri.client.ui.panels.user.settlement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ch.eugster.colibri.client.ui.actions.CifferAction;
import ch.eugster.colibri.client.ui.buttons.ProfileButton;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementMoney;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.ui.actions.ProfileAction;

public class VoucherRow
{
	private final NumberFormat integerFormatter = NumberFormat.getIntegerInstance();

	private final NumberFormat doubleFormatter = DecimalFormat.getNumberInstance();

	private final UserPanel userPanel;

	private final Stock stock;

	private int count;
	
	private double value = 0D;

	private final ProfileButton countButton;

	private final VoucherButton voucherButton;

	private final JLabel valueLabel;

	public VoucherRow(final UserPanel userPanel, final Stock stock, final Key key)
	{
		this.userPanel = userPanel;
		this.stock = stock;

		final java.util.Currency currency = key.paymentType.getCurrency().getCurrency();
		this.integerFormatter.setGroupingUsed(false);
		this.doubleFormatter.setGroupingUsed(false);
		this.doubleFormatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
		this.doubleFormatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());

		this.countButton = this.createCountButton();
		this.voucherButton = this.createVoucherButton(key);

		this.valueLabel = new JLabel();
		this.valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.valueLabel.setText(this.doubleFormatter.format(this.value));
	}

	public void addCount(final int count)
	{
		this.setCount(this.getCount() + count);
	}

	public void addValue(final double value)
	{
		if (voucherButton.getKey().getValue() == 0D)
		{
			this.setValue(this.value + value);
		}
		else
		{
			this.setValue(this.value + (value * this.voucherButton.getKey().getValue()));
		}
		this.addCount(1);
	}

	public int getCount()
	{
		return this.count;
	}

	public ProfileButton getCountButton()
	{
		return this.countButton;
	}

	public SettlementMoney getSettlementMoney(final Settlement settlement)
	{
		final SettlementMoney money = SettlementMoney.newInstance(settlement, this.stock, this.voucherButton.getKey().paymentType);
		money.setText(this.voucherButton.getKey().paymentType.getName());
		money.setCode(this.voucherButton.getKey().paymentType.getCode());
		money.setAmount(this.getValue());
		return money;
	}

	public double getValue()
	{
		return value;
	}

	public JLabel getValueLabel()
	{
		return this.valueLabel;
	}

	public Key getVoucher()
	{
		return this.voucherButton.getKey();
	}

	public ProfileButton getVoucherButton()
	{
		return this.voucherButton;
	}

	public void setCount(final int count)
	{
		this.count = count;
		this.countButton.setText(this.integerFormatter.format(this.getCount()));
	}

	public void setValue(final double value)
	{
		this.value= value;
		this.valueLabel.setText(this.doubleFormatter.format(value));
	}

	private ProfileButton createCountButton()
	{
		final CifferAction action = new CifferAction(this.integerFormatter.format(this.getCount()), this.userPanel);
		final ProfileButton button = new ProfileButton(action, this.userPanel.getProfile(), userPanel.getMainTabbedPane().isFailOver());
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				VoucherRow.this.setCount(0);
				VoucherRow.this.setValue(0D);
			}
		});
		return button;
	}

	private VoucherButton createVoucherButton(Key key)
	{
		final VoucherAction action = new VoucherAction(key.paymentType.getCode(), "add", this.userPanel.getProfile());
		final VoucherButton button = new VoucherButton(action, this.userPanel.getProfile(), key);
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					final String value = VoucherRow.this.userPanel.getValueDisplay().getValue();
					if (!value.isEmpty())
					{
						VoucherRow.this.addValue(Double.valueOf(value).doubleValue());
					}
				}
				catch (final NumberFormatException nfe)
				{
				}
			}
		});
		return button;
	}

	private class VoucherAction extends ProfileAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public VoucherAction(String text, final String actionCommand, final Profile profile)
		{
			super(text, actionCommand, profile);
		}
	}

	private class VoucherButton extends ProfileButton
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Key key;
		
		public VoucherButton(ProfileAction action, final Profile profile, Key key)
		{
			super(action, profile, userPanel.getMainTabbedPane().isFailOver());
			this.key = key;
		}
		
		public Key getKey()
		{
			return key;
		}
	}
}
