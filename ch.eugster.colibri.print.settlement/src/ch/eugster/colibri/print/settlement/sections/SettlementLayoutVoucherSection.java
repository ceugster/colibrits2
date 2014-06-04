package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementPayment;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutVoucherSection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutVoucherSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("GGGGGGGGGGGGGGGGGGGGGGG QQQQQ AAAAAAAAAAAA\n");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Gutscheine\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("Gutscheine              QQQQQ AAAAAAAAAAAA\n");
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
			return ((Settlement) printable).getVouchers().size() > 0;
		}
		if (printable instanceof SettlementPayment)
		{
			return true;
		}
		return false;
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
				final List<SettlementPayment> payments = settlement.getVouchers();
				for (final SettlementPayment payment : payments)
				{
					lines.addAll(super.prepareAreaDetail(payment));
				}
			}
		}
		return lines;
	}

	public enum DetailKey implements IKey
	{
		G, Q, A;

		@Override
		public String label()
		{
			switch (this)
			{
				case G:
				{
					return "Bezeichnung";
				}
				case Q:
				{
					return "Menge";
				}
				case A:
				{
					return "Betrag";
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
					case G:
					{
						String name = payment.getPaymentType().getName();
						return layoutArea.replaceMarker(name, marker, true);
					}
					case Q:
					{
						int qty = payment.getQuantity();
						return layoutArea.replaceMarker(Integer.toString(qty), marker, false);
					}
					case A:
					{
						final Currency currency = payment.getSettlement().getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						final double amount = payment.getDefaultCurrencyAmount();
						SettlementLayoutVoucherSection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutVoucherSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutVoucherSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(
								SettlementLayoutVoucherSection.amountFormatter.format(amount), marker, false);
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
			return "";
		}

		@Override
		public String replace(final ILayoutSection layoutArea, final IPrintable printable, final String marker)
		{
			return marker;
		}
	}

	public enum TotalKey implements IKey
	{
		Q, A;

		@Override
		public String label()
		{
			switch (this)
			{
				case Q:
				{
					return "Menge";
				}
				case A:
				{
					return "Betrag";
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
			if (printable instanceof Settlement)
			{
				final Settlement settlement= (Settlement) printable;
				List<SettlementPayment> vouchers = settlement.getVouchers();

				switch (this)
				{
					case Q:
					{
						int qty = 0;
						for (SettlementPayment voucher : vouchers)
						{
							qty += voucher.getQuantity();
						}
						return layoutArea.replaceMarker(Integer.toString(qty), marker, false);
					}
					case A:
					{
						double amount = 0d;
						for (SettlementPayment voucher : vouchers)
						{
							amount += voucher.getDefaultCurrencyAmount();
						}
						final Currency currency = settlement.getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						SettlementLayoutVoucherSection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutVoucherSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutVoucherSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(
								SettlementLayoutVoucherSection.amountFormatter.format(amount), marker, false);
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
