package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementPayment;
import ch.eugster.colibri.persistence.model.SettlementPosition;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutSummarySection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutSummarySection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Zusammenfassung\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Bewegungen              MMMMM AAAAAAAAAAAA\n");
		builder = builder.append("Zahlungen               VVVVV ZZZZZZZZZZZZ\n");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
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
				SettlementPayment payment = SettlementPayment.newInstance(settlement, null);
				for (final SettlementPayment settlementPayment : settlementPayments)
				{
					payment.setQuantity(payment.getQuantity() + settlementPayment.getQuantity());
					payment.setDefaultCurrencyAmount(payment.getDefaultCurrencyAmount()
							+ settlementPayment.getDefaultCurrencyAmount());
				}

				final Collection<SettlementPosition> settlementPositions = settlement.getPositions();
				SettlementPosition position = SettlementPosition.newInstance(settlement, null, null);
				for (final SettlementPosition settlementPosition : settlementPositions)
				{
					position.setQuantity(position.getQuantity() + settlementPosition.getQuantity());
					position.setDefaultCurrencyAmount(position.getDefaultCurrencyAmount()
							+ settlementPosition.getDefaultCurrencyAmount());
				}

				Collection<IPrintable> printables = new ArrayList<IPrintable>();
				printables.add(payment);
				printables.add(position);
				lines.addAll(prepareAreaDetail(printables.toArray(new IPrintable[0])));
			}
		}
		return lines;
	}

	protected Collection<String> prepareAreaDetail(final IPrintable[] printables)
	{
		final Collection<String> lines = new ArrayList<String>();
		final Collection<String> patternLines = this.getPattern(AreaType.DETAIL);
		final String[] markers = this.getMarkers(AreaType.DETAIL, patternLines);
		for (String patternLine : patternLines)
		{
			for (final String marker : markers)
			{
				for (IPrintable printable : printables)
				{
					if (printIt(AreaType.DETAIL, printable))
					{
						patternLine = this.replace(AreaType.DETAIL, printable, marker, patternLine);
					}
				}
			}
			if (!patternLine.trim().isEmpty() || this.getPrintOption(AreaType.DETAIL).equals(PrintOption.ALWAYS))
			{
				lines.add(patternLine);
			}
		}
		return lines;
	}

	public enum DetailKey implements IKey
	{
		M, A, V, Z;

		@Override
		public String label()
		{
			switch (this)
			{
				case M:
				{
					return "Anzahl Bewegungen";
				}
				case A:
				{
					return "Summe Bewegungen";
				}
				case V:
				{
					return "Anzahl Zahlungen";
				}
				case Z:
				{
					return "Summe Zahlungen";
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
					case V:
					{
						String quantity = Integer.valueOf(payment.getQuantity()).toString();
						quantity = layoutArea.replaceMarker(quantity, marker, false);
						return quantity;
					}
					case Z:
					{
						final Currency currency = payment.getSettlement().getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						double amount = payment.getDefaultCurrencyAmount();
						SettlementLayoutSummarySection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutSummarySection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutSummarySection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(SettlementLayoutSummarySection.amountFormatter.format(amount),
								marker, false);
					}
				}
			}
			else if (printable instanceof SettlementPosition)
			{
				final SettlementPosition position = (SettlementPosition) printable;

				switch (this)
				{
					case M:
					{
						String quantity = Integer.valueOf(position.getQuantity()).toString();
						return layoutArea.replaceMarker(quantity, marker, false);
					}
					case A:
					{
						final Currency currency = position.getSettlement().getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						double amount = position.getDefaultCurrencyAmount();
						SettlementLayoutSummarySection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutSummarySection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutSummarySection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(SettlementLayoutSummarySection.amountFormatter.format(amount),
								marker, false);
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
}
