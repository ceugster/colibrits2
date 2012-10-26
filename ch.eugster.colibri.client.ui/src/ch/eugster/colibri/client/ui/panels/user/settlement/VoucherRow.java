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
import ch.eugster.colibri.persistence.model.PaymentType;
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

	private final PaymentType voucher;

	private int count;

	private final ProfileButton countButton;

	private final ProfileButton voucherButton;

	private final JLabel valueLabel;

	public VoucherRow(final UserPanel userPanel, final Stock stock, final PaymentType voucher)
	{
		this.userPanel = userPanel;
		this.stock = stock;
		this.voucher = voucher;

		final java.util.Currency currency = voucher.getCurrency().getCurrency();
		this.integerFormatter.setGroupingUsed(false);
		this.doubleFormatter.setGroupingUsed(false);
		this.doubleFormatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
		this.doubleFormatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());

		this.countButton = this.createCountButton();
		this.voucherButton = this.createVoucherButton();

		this.valueLabel = new JLabel();
		this.valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.valueLabel.setText(this.doubleFormatter.format(voucher.getValue()));
	}

	public void addCount(final int count)
	{
		this.setCount(this.getCount() + count);
	}

	public void addValue(final double value)
	{
		this.setValue(this.voucher.getValue() + value);
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
		final SettlementMoney money = SettlementMoney.newInstance(settlement, this.stock, this.voucher);
		money.setText(this.voucher.getName());
		money.setCode(this.voucher.getCode());
		money.setAmount(this.getValue());
		return money;
	}

	public double getValue()
	{
		return this.voucher.getValue();
	}

	public JLabel getValueLabel()
	{
		return this.valueLabel;
	}

	public PaymentType getVoucher()
	{
		return this.voucher;
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
		this.voucher.setValue(value);
		this.valueLabel.setText(this.doubleFormatter.format(this.voucher.getValue()));
	}

	private ProfileButton createCountButton()
	{
		final CifferAction action = new CifferAction(this.integerFormatter.format(this.getCount()), this.userPanel);
		final ProfileButton button = new ProfileButton(action, this.userPanel.getProfile());
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

	private ProfileButton createVoucherButton()
	{
		final VoucherAction action = new VoucherAction(this.voucher.getCode(), "add", this.userPanel.getProfile());
		final ProfileButton button = new ProfileButton(action, this.userPanel.getProfile());
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

		public VoucherAction(final String text, final String actionCommand, final Profile profile)
		{
			super(text, actionCommand, profile);
		}
	}
}
