/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.settlement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.BackAction;
import ch.eugster.colibri.client.ui.actions.SettleAction;
import ch.eugster.colibri.client.ui.buttons.ProfileButton;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementDetail;
import ch.eugster.colibri.persistence.model.SettlementDetail.Part;
import ch.eugster.colibri.persistence.model.SettlementMoney;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.queries.KeyQuery;
import ch.eugster.colibri.persistence.queries.PaymentQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.panels.ProfilePanel;

public class CoinCounterPanel extends ProfilePanel
{
	private static final NumberFormat doubleFormatter = DecimalFormat.getNumberInstance();

	private static final long serialVersionUID = 0l;

	private final UserPanel userPanel;

	private final Map<Long, MoneyRow[]> moneyRows = new HashMap<Long, MoneyRow[]>();

	private final Map<Long, VoucherRow[]> voucherRows = new HashMap<Long, VoucherRow[]>();

	private final Collection<ProfileButton> deleteButtons = new ArrayList<ProfileButton>();

	private final Map<String, Double> totalMoneys = new HashMap<String, Double>();

	private final Map<String, Double> totalVouchers = new HashMap<String, Double>();

	private Color fgSelected;

	private Color fg;

	private Color bg;

	public CoinCounterPanel(final UserPanel userPanel)
	{
		super(userPanel.getProfile());
		this.userPanel = userPanel;
		this.init();
	}

	public List<SettlementDetail> getSettlementDetails(final Settlement settlement)
	{
		final List<SettlementDetail> details = new ArrayList<SettlementDetail>();
		final List<Stock> stocks = this.userPanel.getSalespoint().getStocks();
		for (final Stock stock : stocks)
		{
			details.addAll(this.getSettlementDetails(stock, settlement));
		}
		return details;
	}

	public List<SettlementMoney> getSettlementMoney(final Settlement settlement)
	{
		final List<SettlementMoney> moneys = new ArrayList<SettlementMoney>();
		final List<Stock> stocks = this.userPanel.getSalespoint().getStocks();
		for (final Stock stock : stocks)
		{
			moneys.addAll(this.getSettlementMoney(stock, settlement));
		}
		return moneys;
	}

	public UserPanel getUserPanel()
	{
		return this.userPanel;
	}

	@Override
	protected void update()
	{
	}

	public void clear()
	{
		ProfileButton[] buttons = deleteButtons.toArray(new ProfileButton[0]);
		for (ProfileButton button : buttons)
		{
			button.doClick();
		}
	}

	private JPanel createActionPanel()
	{
		final JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 4));

		final SettleAction settleAction = new SettleAction(this);
		final ProfileButton settleButton = new ProfileButton(settleAction, this.userPanel.getProfile(), userPanel.getMainTabbedPane().isFailOver());
		settleButton.addActionListener(this.userPanel);
		panel.add(settleButton);

		final BackAction backAction = new BackAction(this.userPanel.getProfile());
		final ProfileButton backButton = new ProfileButton(backAction, this.userPanel.getProfile(), userPanel.getMainTabbedPane().isFailOver());
		backButton.addActionListener(this.userPanel);
		panel.add(backButton);

		return panel;
	}

	private JPanel createMoneyPanel(final Stock stock)
	{
		totalMoneys.put(stock.getPaymentType().getCurrency().getCode(), Double.valueOf(0D));

		final Money[] moneys = stock.getPaymentType().getMoneys().toArray(new Money[0]);
		Arrays.sort(moneys);

		final JPanel moneyPanel = new JPanel();
		moneyPanel.setLayout(new GridLayout(moneys.length + 1, 3));

		final ProfileButton deleteButton = new ProfileButton(this.userPanel.getProfile());
		this.deleteButtons.add(deleteButton);
		final JLabel totalCurrencyLabel = new JLabel();

		final MoneyRow[] moneyRows = new MoneyRow[moneys.length];
		for (int i = 0; i < moneys.length; i++)
		{
			final MoneyRow moneyRow = new MoneyRow(this.userPanel, stock, moneys[i]);
			moneyPanel.add(moneyRow.getCountButton());
			moneyPanel.add(moneyRow.getValueButton());
			moneyPanel.add(moneyRow.getSumLabel());

			deleteButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					moneyRow.setCount(0);
				}
			});
			moneyRow.getSumLabel().addPropertyChangeListener(new PropertyChangeListener()
			{
				@Override
				public void propertyChange(final PropertyChangeEvent evt)
				{
					if (evt.getPropertyName().equals("text"))
					{
						try
						{
							final Double oldVal = Double.valueOf((String) evt.getOldValue());
							final Double newVal = Double.valueOf((String) evt.getNewValue());
							final Double val = Double.valueOf(totalCurrencyLabel.getText());
							final double oldValue = oldVal == null ? 0D : oldVal.doubleValue();
							final double newValue = newVal == null ? 0D : newVal.doubleValue();
							final double value = val == null ? 0D : val.doubleValue();
							double labelValue = value - oldValue + newValue;
							totalCurrencyLabel.setText(CoinCounterPanel.doubleFormatter.format(labelValue));
							totalMoneys.put(stock.getPaymentType().getCurrency().getCode(), Double.valueOf(labelValue));

						}
						catch (final NumberFormatException e)
						{

						}
					}
				}
			});
			moneyRows[i] = moneyRow;
		}

		if (moneys.length > 0)
		{
			this.moneyRows.put(stock.getId(), moneyRows);
		}

		deleteButton.setText("Leeren");
		moneyPanel.add(deleteButton);

		final JLabel label = new JLabel();
		moneyPanel.add(label);

		totalCurrencyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalCurrencyLabel.setText(CoinCounterPanel.doubleFormatter.format(0D));
		moneyPanel.add(totalCurrencyLabel);

		return moneyPanel;
	}

	private JPanel createPanel(final Stock stock)
	{
		final Money[] moneys = stock.getPaymentType().getMoneys().toArray(new Money[0]);
		final Key[] keys = this.getVouchers(stock.getPaymentType().getCurrency());
		int cols = moneys.length == 0 ? 0 : 1;
		cols += keys.length == 0 ? 0 : 1;

		final JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, cols));

		if (moneys.length > 0)
		{
			panel.add(this.createMoneyPanel(stock), BorderLayout.WEST);
		}
		if (keys.length > 0)
		{
			panel.add(this.createVoucherPanel(stock, keys), BorderLayout.EAST);
		}
		return panel;
	}

	private JTabbedPane createTabbedPane()
	{
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(tabbedPane.getFont().deriveFont(this.userPanel.getProfile().getTabbedPaneFontStyle(),
				this.userPanel.getProfile().getTabbedPaneFontSize()));
		tabbedPane.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent event)
			{
				for (int i = 0; i < tabbedPane.getTabCount(); i++)
				{
					if (tabbedPane.getSelectedIndex() == i)
					{
						tabbedPane.setForegroundAt(i, CoinCounterPanel.this.fgSelected);

					}
					else
					{
						tabbedPane.setForegroundAt(i, CoinCounterPanel.this.fg);
						tabbedPane.setBackgroundAt(i, CoinCounterPanel.this.bg);
					}
				}
			}
		});

		return tabbedPane;
	}

	private JPanel createVoucherPanel(final Stock stock, final Key[] keys)
	{
		totalVouchers.put(stock.getPaymentType().getCurrency().getCode(), Double.valueOf(0D));

		final int cols = 3;
		int rows = stock.getPaymentType().getMoneys().size();
		rows = rows > keys.length ? rows : keys.length;

		final JPanel voucherPanel = new JPanel();
		voucherPanel.setLayout(new GridLayout(rows + 1, cols));

		final VoucherRow[] voucherRows = new VoucherRow[keys.length];
		final ProfileButton deleteButton = new ProfileButton(this.userPanel.getProfile());
		this.deleteButtons.add(deleteButton);
		final JLabel totalVoucherLabel = new JLabel();

		for (int i = 0; i < rows; i++)
		{
			if (keys.length > i)
			{
				final VoucherRow voucherRow = new VoucherRow(this.userPanel, stock, keys[i]);
				voucherPanel.add(voucherRow.getCountButton());
				voucherPanel.add(voucherRow.getVoucherButton());
				voucherPanel.add(voucherRow.getValueLabel());

				deleteButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent e)
					{
						voucherRow.setCount(0);
						;
					}
				});
				voucherRow.getValueLabel().addPropertyChangeListener(new PropertyChangeListener()
				{
					@Override
					public void propertyChange(final PropertyChangeEvent evt)
					{
						if (evt.getPropertyName().equals("text"))
						{
							try
							{
								final Double oldVal = Double.valueOf((String) evt.getOldValue());
								final Double newVal = Double.valueOf((String) evt.getNewValue());
								final Double val = Double.valueOf(totalVoucherLabel.getText());
								final double oldValue = oldVal == null ? 0D : oldVal.doubleValue();
								final double newValue = newVal == null ? 0D : newVal.doubleValue();
								final double value = val == null ? 0D : val.doubleValue();
								double labelValue = value - oldValue + newValue;
								totalVoucherLabel.setText(CoinCounterPanel.doubleFormatter.format(labelValue));
								totalVouchers.put(stock.getPaymentType().getCurrency().getCode(),
										Double.valueOf(labelValue));

							}
							catch (final NumberFormatException e)
							{

							}
						}
					}
				});
				voucherRows[i] = voucherRow;
			}
			else
			{
				voucherPanel.add(new JLabel());
				voucherPanel.add(new JLabel());
				voucherPanel.add(new JLabel());
			}
		}

		if (voucherRows.length > 0)
		{
			this.voucherRows.put(stock.getId(), voucherRows);
		}

		deleteButton.setText("Leeren");
		voucherPanel.add(deleteButton);

		final JLabel label = new JLabel();
		voucherPanel.add(label);

		totalVoucherLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalVoucherLabel.setText(CoinCounterPanel.doubleFormatter.format(0D));
		voucherPanel.add(totalVoucherLabel);

		return voucherPanel;
	}

	public double getCountMoneySum()
	{
		double value = 0d;
		Collection<MoneyRow[]> currencies = moneyRows.values();
		for (MoneyRow[] rows : currencies)
		{
			for (MoneyRow row : rows)
			{
				value += Double.valueOf(row.getSumLabel().getText()).doubleValue();
			}
		}
		return value;
	}

//	public double getCountVoucherSum()
//	{
//		double value = 0d;
//		Collection<VoucherRow[]> vouchers = voucherRows.values();
//		for (VoucherRow[] rows : vouchers)
//		{
//			for (VoucherRow row : rows)
//			{
//				value += Double.valueOf(row.getValue().getSumLabel().getText()).doubleValue();
//			}
//		}
//		return value;
//	}

	private Collection<SettlementMoney> getMoneyDetails(final Stock stock, final Settlement settlement)
	{
		final Collection<SettlementMoney> moneys = new ArrayList<SettlementMoney>();
		final MoneyRow[] moneyRows = this.moneyRows.get(stock.getId());
		if (moneyRows != null)
		{
			for (final MoneyRow moneyRow : moneyRows)
			{
				if (moneyRow.getCount() > 0)
				{
					moneys.add(moneyRow.getSettlementMoney(settlement));
				}
			}
		}
		return moneys;
	}

	private Collection<SettlementDetail> getSettlementDetails(final Stock stock, final Settlement settlement)
	{
		final Collection<SettlementDetail> details = new ArrayList<SettlementDetail>();
		details.addAll(this.getSettlementSummary(stock, settlement));
		return details;
	}

	private Collection<SettlementMoney> getSettlementMoney(final Stock stock, final Settlement settlement)
	{
		final Collection<SettlementMoney> moneys = new ArrayList<SettlementMoney>();
		moneys.addAll(this.getMoneyDetails(stock, settlement));
		moneys.addAll(this.getVoucherDetails(stock, settlement));
		return moneys;
	}

	private Collection<SettlementDetail> getSettlementSummary(final Stock stock, final Settlement settlement)
	{
		final Collection<SettlementDetail> details = new ArrayList<SettlementDetail>();

		SettlementDetail detail = SettlementDetail.newInstance(settlement, stock);
		detail.setDebit(stock.getAmount());
		detail.setVariableStock(stock.isVariable());
		detail.setPart(Part.BEGIN_STOCK);
		details.add(detail);

		double creditAmount = 0d;
		final MoneyRow[] moneyRows = this.moneyRows.get(stock.getId());
		if (moneyRows != null)
		{
			for (final MoneyRow moneyRow : moneyRows)
			{
				creditAmount += moneyRow.getMoney().getSum();
			}
		}
		final VoucherRow[] voucherRows = this.voucherRows.get(stock.getId());
		if (voucherRows != null)
		{
			for (final VoucherRow voucherRow : voucherRows)
			{
				creditAmount += voucherRow.getValue();
			}
		}

		double debitAmount = this.getTotalIncome(settlement, stock.getPaymentType().getCurrency());

		detail = SettlementDetail.newInstance(settlement, stock);
		detail.setDebit(debitAmount);
		detail.setCredit(creditAmount);
		detail.setVariableStock(stock.isVariable());
		detail.setPart(Part.INCOME);
		details.add(detail);

		debitAmount += stock.getAmount();
		creditAmount += stock.getAmount();

		double diff = debitAmount + stock.getAmount() - creditAmount;

		detail = SettlementDetail.newInstance(settlement, stock);
		detail.setDebit(diff < 0D ? Math.abs(diff) : 0D);
		detail.setCredit(diff < 0D ? 0D : diff);
		detail.setVariableStock(stock.isVariable());
		detail.setPart(Part.DIFFERENCE);
		details.add(detail);

		return details;
	}

	private double getTotalIncome(final Settlement settlement, final Currency currency)
	{
		final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		final PersistenceService service = (PersistenceService) tracker.getService();
		if (service != null)
		{
			final PaymentQuery query = (PaymentQuery) service.getCacheService().getQuery(Payment.class);
			return query.sumCashAndVoucher(settlement, currency);
		}
		tracker.close();
		return 0;
	}

	private Collection<SettlementMoney> getVoucherDetails(final Stock stock, final Settlement settlement)
	{
		final Collection<SettlementMoney> moneys = new ArrayList<SettlementMoney>();
		final VoucherRow[] voucherRows = this.voucherRows.get(stock.getId());
		if (voucherRows != null)
		{
			for (final VoucherRow voucherRow : voucherRows)
			{
				if (voucherRow.getCount() > 0)
				{
					moneys.add(voucherRow.getSettlementMoney(settlement));
				}
			}
		}
		return moneys;
	}

//	private PaymentType[] getVouchers(final Currency currency)
//	{
//		PaymentType[] vouchers = null;
//		final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
//				PersistenceService.class, null);
//		tracker.open();
//		final PersistenceService service = (PersistenceService) tracker.getService();
//		if (service != null)
//		{
//			final PaymentTypeQuery query = (PaymentTypeQuery) service.getCacheService().getQuery(PaymentType.class);
//			vouchers = query.selectByPaymentTypeGroupAndCurrency(PaymentTypeGroup.VOUCHER, currency).toArray(
//					new PaymentType[0]);
//		}
//		tracker.close();
//		return vouchers == null ? new PaymentType[0] : vouchers;
//	}

	private Key[] getVouchers(final Currency currency)
	{
		Key[] vouchers = null;
		final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		final PersistenceService service = (PersistenceService) tracker.getService();
		if (service != null)
		{
			final KeyQuery query = (KeyQuery) service.getCacheService().getQuery(Key.class);
			vouchers = query.selectVouchers(this.getProfile(), currency).toArray(new Key[0]);
		}
		tracker.close();
		return vouchers;
	}

	private void init()
	{
		this.setLayout(new BorderLayout());

		final Stock[] stocks = this.userPanel.getSalespoint().getStocks().toArray(new Stock[0]);
		if (stocks.length > 0)
		{
			final java.util.Currency currency = stocks[0].getSalespoint().getPaymentType().getCurrency().getCurrency();
			CoinCounterPanel.doubleFormatter.setGroupingUsed(false);
			CoinCounterPanel.doubleFormatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
			CoinCounterPanel.doubleFormatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());

			if (stocks.length == 1)
			{
				this.add(this.createPanel(stocks[0]), BorderLayout.CENTER);
			}
			else
			{
				this.fgSelected = new Color(this.userPanel.getProfile().getTabbedPaneFgSelected());
				this.fg = new Color(this.userPanel.getProfile().getTabbedPaneFg());
				this.bg = new Color(this.userPanel.getProfile().getTabbedPaneBg());

				final JTabbedPane tabbedPane = this.createTabbedPane();
				Arrays.sort(stocks);
				for (final Stock stock : stocks)
				{
					tabbedPane.add(stock.getPaymentType().getCurrency().getCode(), this.createPanel(stock));
				}
				this.add(tabbedPane, BorderLayout.CENTER);
			}
		}
		this.add(this.createActionPanel(), BorderLayout.SOUTH);
	}
}
