package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementPosition;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutProductGroupSummarySection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutProductGroupSummarySection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
//		builder = builder.append("Total Bewegungen\n");
//		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Total Bewegungen   MMMM AAAAAAAAAA TTTTTTT\n");
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
		return false;
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
				lines.addAll(super.prepareAreaDetail(settlement));
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
		M, A, T;

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
				case T:
				{
					return "Summe Mehrwertsteuer";
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
				final Settlement settlement = (Settlement) printable;
				List<SettlementPosition> positions = settlement.getPositions();
				
				switch (this)
				{
					case M:
					{
						int quantity = 0;
						for (SettlementPosition position : positions)
						{
							ProductGroupGroup group = position.getProductGroup().getProductGroupType().getParent();
							if (group.equals(ProductGroupGroup.SALES) || group.equals(ProductGroupGroup.EXPENSES))
							{
								quantity += position.getQuantity();
							}
						}
						String qty = Integer.toString(quantity);
						return layoutArea.replaceMarker(qty, marker, false);
					}
					case A:
					{
						double amount = 0d;
						for (SettlementPosition position : positions)
						{
							ProductGroupGroup group = position.getProductGroup().getProductGroupType().getParent();
							if (group.equals(ProductGroupGroup.SALES) || group.equals(ProductGroupGroup.EXPENSES))
							{
								amount += position.getDefaultCurrencyAmount();
							}
						}
						final Currency currency = settlement.getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						SettlementLayoutProductGroupSummarySection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutProductGroupSummarySection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutProductGroupSummarySection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(SettlementLayoutProductGroupSummarySection.amountFormatter.format(amount),
								marker, false);
					}
					case T:
					{
						double amount = 0d;
						for (SettlementPosition position : positions)
						{
							ProductGroupGroup group = position.getProductGroup().getProductGroupType().getParent();
							if (group.equals(ProductGroupGroup.SALES) || group.equals(ProductGroupGroup.EXPENSES))
							{
								amount -= position.getTaxAmount();
							}
						}
						final Currency currency = settlement.getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						SettlementLayoutProductGroupSummarySection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutProductGroupSummarySection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutProductGroupSummarySection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(SettlementLayoutProductGroupSummarySection.amountFormatter.format(amount),
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
