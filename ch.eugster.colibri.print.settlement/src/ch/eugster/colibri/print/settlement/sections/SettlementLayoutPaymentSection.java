package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;

import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementPayment;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutPaymentSection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutPaymentSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("AAAAAAAAAAAAAAAA WWW FFFFFFFFF LLLLLLLLLLL");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Zahlungsarten\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("Zahlungsarten                  LLLLLLLLLLL\n");
		builder = builder.append("==========================================");
		return builder.toString();
	}

	@Override
	protected IKey[] getKeysDetail()
	{
		return DetailKey.values();
	}

	@Override
	protected IKey[] getKeysTitle()
	{
		return TitleKey.values();
	}

	@Override
	protected IKey[] getKeysTotal()
	{
		return TotalKey.values();
	}

	@Override
	protected boolean hasData(IPrintable printable)
	{
		if (printable instanceof Settlement)
		{
			return ((Settlement) printable).getPayments().size() > 0;
		}
		return true;
	}

	@Override
	protected boolean hasDetailArea()
	{
		return true;
	}

	@Override
	protected boolean hasTitleArea()
	{
		return true;
	}

	@Override
	protected boolean hasTotalArea()
	{
		return true;
	}

	@Override
	protected Collection<String> prepareAreaDetail(final IPrintable printable)
	{
		final Collection<String> lines = new ArrayList<String>();
		if (printIt(AreaType.DETAIL, printable))
		{
			if (printable instanceof Settlement)
			{
				final Settlement settlement = (Settlement) printable;
				final Collection<SettlementPayment> settlementPayments = settlement.getPayments();
				for (final SettlementPayment settlementPayment : settlementPayments)
				{
					lines.addAll(super.prepareAreaDetail(settlementPayment));
				}
			}
		}
		return lines;
	}

	@Override
	protected Collection<String> prepareAreaTotal(final IPrintable printable)
	{
		final Collection<String> lines = new ArrayList<String>();
		if (printIt(AreaType.TOTAL, printable))
		{
			if (printable instanceof Settlement)
			{
				final Settlement settlement = (Settlement) printable;
				final Collection<SettlementPayment> settlementPayments = settlement.getPayments();
				if (!settlementPayments.isEmpty())
				{
					final SettlementPayment[] payments = settlementPayments.toArray(new SettlementPayment[0]);
					final SettlementPayment totalPayment = SettlementPayment.newInstance(settlement,
							payments[0].getPaymentType());
					for (final SettlementPayment payment : payments)
					{
						totalPayment.setDefaultCurrencyAmount(totalPayment.getDefaultCurrencyAmount()
								+ payment.getDefaultCurrencyAmount());
						totalPayment.setForeignCurrencyAmount(totalPayment.getForeignCurrencyAmount()
								+ payment.getForeignCurrencyAmount());
						totalPayment.setQuantity(totalPayment.getQuantity() + payment.getQuantity());
					}
					lines.addAll(super.prepareAreaTotal(totalPayment));
				}
			}
		}
		return lines;
	}

	public enum DetailKey implements IKey
	{
		A, W, L, F;

		@Override
		public String label()
		{
			switch (this)
			{
				case A:
				{
					return "Zahlungsart";
				}
				case W:
				{
					return "Währung";
				}
				case L:
				{
					return "Zahlungsbetrag LW";
				}
				case F:
				{
					return "Zahlungsbetrag FW";
				}
				default:
				{
					throw new RuntimeException("invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutArea, final IPrintable printable, final String marker)
		{
			if (printable instanceof SettlementPayment)
			{
				final SettlementPayment payment = (SettlementPayment) printable;

				switch (this)
				{
					case A:
					{
						final String name = payment.getPaymentType().getName();
						return layoutArea.replaceMarker(name, marker, true);
					}
					case W:
					{
						if (!payment.getPaymentType().getCurrency()
								.equals(payment.getSettlement().getSalespoint().getPaymentType().getCurrency()))
						{
							return layoutArea.replaceMarker(payment.getPaymentType().getCurrency().getCode(), marker,
									true);
						}
						else
						{
							return layoutArea.replaceMarker("", marker, true);
						}
					}
					case L:
					{
						final double amount = payment.getDefaultCurrencyAmount();
						final Currency currency = payment.getSettlement().getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						amountFormatter.setGroupingUsed(false);
						SettlementLayoutPaymentSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutPaymentSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						final String formattedAmount = SettlementLayoutPaymentSection.amountFormatter.format(amount);
						return layoutArea.replaceMarker(formattedAmount, marker, false);
					}
					case F:
					{
						if (!payment.getPaymentType().getCurrency()
								.equals(payment.getSettlement().getSalespoint().getPaymentType().getCurrency()))
						{
							final double amount = payment.getForeignCurrencyAmount();
							final Currency currency = payment.getPaymentType().getCurrency().getCurrency();
							amountFormatter.setGroupingUsed(false);
							SettlementLayoutPaymentSection.amountFormatter.setMinimumFractionDigits(currency
									.getDefaultFractionDigits());
							SettlementLayoutPaymentSection.amountFormatter.setMaximumFractionDigits(currency
									.getDefaultFractionDigits());
							final String formattedAmount = SettlementLayoutPaymentSection.amountFormatter
									.format(amount);
							return layoutArea.replaceMarker(formattedAmount, marker, false);
						}
						else
						{
							return layoutArea.replaceMarker("", marker, true);
						}
					}
					default:
					{
						throw new RuntimeException("invalid key");
					}
				}
			}
			return marker;
		}
	}

	public enum TitleKey implements IKey
	{
		;

		@Override
		public String label()
		{
			return "Zahlungsarten";
		}

		@Override
		public String replace(final ILayoutSection layoutArea, final IPrintable printable, final String marker)
		{
			return marker;
		}
	}

	public enum TotalKey implements IKey
	{
		L;

		@Override
		public String label()
		{
			switch (this)
			{
				case L:
				{
					return "Summe Zahlungen LW";
				}
				default:
				{
					throw new RuntimeException("invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutArea, final IPrintable printable, final String marker)
		{
			if (printable instanceof SettlementPayment)
			{
				final SettlementPayment detail = (SettlementPayment) printable;

				switch (this)
				{
					case L:
					{
						final double amount = detail.getDefaultCurrencyAmount();
						final Currency currency = detail.getPaymentType().getCurrency().getCurrency();
						SettlementLayoutPaymentSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutPaymentSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(SettlementLayoutPaymentSection.amountFormatter.format(amount),
								marker, false);
					}
					default:
					{
						throw new RuntimeException("invalid key");
					}
				}
			}
			return marker;
		}
	}

}
