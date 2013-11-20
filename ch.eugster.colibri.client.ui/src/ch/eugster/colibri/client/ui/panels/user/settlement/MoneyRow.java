package ch.eugster.colibri.client.ui.panels.user.settlement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ch.eugster.colibri.client.ui.actions.CifferAction;
import ch.eugster.colibri.client.ui.buttons.ProfileButton;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementMoney;
import ch.eugster.colibri.persistence.model.Stock;

public class MoneyRow
{
	private final NumberFormat integerFormatter = NumberFormat.getIntegerInstance();

	private final NumberFormat doubleFormatter = DecimalFormat.getNumberInstance();

	private final UserPanel userPanel;

	private Stock stock;

	private final Money money;

	private final ProfileButton countButton;

	private final ProfileButton valueButton;

	private final JLabel sumLabel;

	public MoneyRow(final UserPanel userPanel, final Stock stock, final Money money)
	{
		this.userPanel = userPanel;
		this.stock = stock;
		this.money = money;

		final java.util.Currency currency = money.getPaymentType().getCurrency().getCurrency();
		this.integerFormatter.setGroupingUsed(false);
		this.doubleFormatter.setGroupingUsed(false);
		this.doubleFormatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
		this.doubleFormatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());

		this.countButton = this.createCountButton();
		this.valueButton = this.createValueButton();

		this.sumLabel = new JLabel();
		this.sumLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.sumLabel.setText(this.doubleFormatter.format(money.getSum()));
	}

	public void addCount(final int count)
	{
		this.money.addCount(count);
		this.countButton.setText(this.integerFormatter.format(this.money.getCount()));
	}

	public int getCount()
	{
		return this.money.getCount();
	}

	public ProfileButton getCountButton()
	{
		return this.countButton;
	}

	public Money getMoney()
	{
		return this.money;
	}

	public SettlementMoney getSettlementMoney(final Settlement settlement)
	{
		final SettlementMoney money = SettlementMoney.newInstance(settlement, this.stock);
		money.setCode(this.doubleFormatter.format(this.money.getValue()));
		money.setText(this.doubleFormatter.format(this.money.getValue()));
		money.setMoney(this.getMoney());
		money.setQuantity(this.getCount());
		money.setAmount(this.money.getSum());
		return money;
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public JLabel getSumLabel()
	{
		return this.sumLabel;
	}

	public ProfileButton getValueButton()
	{
		return this.valueButton;
	}

	public void setCount(final int count)
	{
		this.money.setCount(count);
		this.countButton.setText(this.integerFormatter.format(this.money.getCount()));
	}

	public void setStock(final Stock stock)
	{
		this.stock = stock;
	}

	private ProfileButton createCountButton()
	{
		final CifferAction action = new CifferAction(this.integerFormatter.format(this.money.getCount()),
				this.userPanel);
		final ProfileButton button = new ProfileButton(action, this.userPanel.getProfile(), userPanel.getMainTabbedPane().isFailOver());
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					final String value = MoneyRow.this.userPanel.getValueDisplay().getValue();
					if (value.isEmpty())
					{
						MoneyRow.this.setCount(0);
					}
					else
					{
						MoneyRow.this.setCount(Integer.valueOf(value).intValue());
					}
				}
				catch (final NumberFormatException nfe)
				{
				}
			}
		});
		button.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(final PropertyChangeEvent evt)
			{
				if (evt.getPropertyName().equals("text"))
				{
					MoneyRow.this.sumLabel.setText(MoneyRow.this.doubleFormatter.format(MoneyRow.this.money.getSum()));
				}
			}
		});
		return button;
	}

	private ProfileButton createValueButton()
	{
		final CifferAction action = new CifferAction(this.doubleFormatter.format(this.money.getValue()), this.userPanel);
		final ProfileButton button = new ProfileButton(action, this.userPanel.getProfile(), userPanel.getMainTabbedPane().isFailOver());
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					final String value = MoneyRow.this.userPanel.getValueDisplay().getValue();
					if (value.isEmpty())
					{
						MoneyRow.this.addCount(1);
					}
					else
					{
						MoneyRow.this.setCount(Integer.valueOf(value).intValue());
					}
				}
				catch (final NumberFormatException nfe)
				{
				}
			}
		});
		return button;
	}
}
