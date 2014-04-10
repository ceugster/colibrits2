package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;

import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementPosition;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutExpensesSection extends AbstractLayoutSection
{
	private static NumberFormat quantityFormatter = DecimalFormat.getIntegerInstance();

	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutExpensesSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("WWWWWWWWWWWWWWWWWW MMMM BBBBBBBBBB TTTTTTT\n");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Ausgaben              M     Betrag    MwSt\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("Ausgaben           MMMM BBBBBBBBBB TTTTTTT\n");
		builder = builder.append("==========================================");
		return builder.toString();
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
				final Collection<SettlementPosition> settlementPositions = settlement.getPositions();
				for (final SettlementPosition settlementPosition : settlementPositions)
				{
					if (settlementPosition.getProductGroupType().getParent().equals(ProductGroupGroup.EXPENSES))
					{
						lines.addAll(super.prepareAreaDetail(settlementPosition));
					}
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
				final Collection<SettlementPosition> settlementPositions = settlement.getPositions();
				if (!settlementPositions.isEmpty())
				{
					final SettlementPosition[] positions = settlementPositions.toArray(new SettlementPosition[0]);
					final SettlementPosition totalExpenses = SettlementPosition.newInstance(settlement, null,
							settlement.getSalespoint().getPaymentType().getCurrency());
					for (final SettlementPosition position : positions)
					{
						if (position.getProductGroupType().getParent().equals(ProductGroupGroup.EXPENSES))
						{
							totalExpenses.setDefaultCurrencyAmount(totalExpenses.getDefaultCurrencyAmount()
									+ position.getDefaultCurrencyAmount());
							totalExpenses.setTaxAmount(totalExpenses.getTaxAmount() + position.getTaxAmount());
							totalExpenses.setQuantity(totalExpenses.getQuantity() + position.getQuantity());
						}
					}
					lines.addAll(super.prepareAreaTotal(totalExpenses));
				}
			}
		}
		return lines;
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
			Settlement settlement = (Settlement) printable;
			return settlement.countPositions(ProductGroupType.EXPENSES_INVESTMENT) > 0 || settlement.countPositions(ProductGroupType.EXPENSES_MATERIAL) > 0;
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

	public enum DetailKey implements IKey
	{
		W, M, B, T;

		@Override
		public String label()
		{
			switch (this)
			{
				case W:
				{
					return "Bezeichnung";
				}
				case M:
				{
					return "Menge";
				}
				case B:
				{
					return "Betrag";
				}
				case T:
				{
					return "Mehrwertsteuerbetrag";
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
			if (printable instanceof SettlementPosition)
			{
				final SettlementPosition detail = (SettlementPosition) printable;
				switch (this)
				{
					case W:
					{
						StringBuilder pg = new StringBuilder();
						String code = detail.getProductGroup().getCode();
						String name = detail.getProductGroup().getName();
						pg = pg.append(code.isEmpty() ? name : (name.isEmpty() ? code : code + " " + name));
						return layoutArea.replaceMarker(pg.toString(), marker, true);
					}
					case M:
					{
						final String quantity = SettlementLayoutExpensesSection.quantityFormatter.format(detail
								.getQuantity());
						return layoutArea.replaceMarker(quantity, marker, false);
					}
					case B:
					{
						final Currency currency = detail.getDefaultCurrency().getCurrency();
						SettlementLayoutExpensesSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutExpensesSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						amountFormatter.setGroupingUsed(false);
						final String amount = SettlementLayoutExpensesSection.amountFormatter.format(detail
								.getDefaultCurrencyAmount());
						return layoutArea.replaceMarker(amount, marker, false);
					}
					case T:
					{
						final Currency currency = detail.getDefaultCurrency().getCurrency();
						SettlementLayoutExpensesSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutExpensesSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						double amount = detail.getDefaultCurrencyAmount();
						boolean taxInclusive = detail.getSettlement().getSalespoint().getCommonSettings()
								.isTaxInclusive();
						double taxAmount = detail.getTaxAmount();
						taxAmount = taxInclusive ? (amount < 0 ? Math.abs(taxAmount) : -Math.abs(taxAmount))
								: (amount < 0 ? -Math.abs(taxAmount) : Math.abs(taxAmount));
						return layoutArea.replaceMarker(
								SettlementLayoutExpensesSection.amountFormatter.format(taxAmount), marker, false);
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
		W;

		@Override
		public String label()
		{
			switch (this)
			{
				case W:
				{
					return "Bezeichnung";
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
			if (printable instanceof SettlementPosition)
			{
				final SettlementPosition detail = (SettlementPosition) printable;

				switch (this)
				{
					case W:
					{
						return layoutArea.replaceMarker(detail.getProductGroup().getProductGroupType().toString(),
								marker, true);
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

	public enum TotalKey implements IKey
	{
		W, M, B, T;

		@Override
		public String label()
		{
			switch (this)
			{
				case W:
				{
					return "Bezeichnung";
				}
				case M:
				{
					return "Gesamtmenge";
				}
				case B:
				{
					return "Gesamtbetrag";
				}
				case T:
				{
					return "Mehrwertsteuerbetrag";
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
			if (printable instanceof SettlementPosition)
			{
				final SettlementPosition detail = (SettlementPosition) printable;

				switch (this)
				{
					case W:
					{
						return layoutArea.replaceMarker(detail.getProductGroup().getProductGroupType().toString(),
								marker, true);
					}
					case M:
					{
						final String quantity = SettlementLayoutExpensesSection.quantityFormatter.format(detail
								.getQuantity());
						return layoutArea.replaceMarker(quantity, marker, false);
					}
					case B:
					{
						final Currency currency = detail.getDefaultCurrency().getCurrency();
						amountFormatter.setGroupingUsed(false);
						SettlementLayoutExpensesSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutExpensesSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						String amount = SettlementLayoutExpensesSection.amountFormatter.format(detail
								.getDefaultCurrencyAmount());
						return layoutArea.replaceMarker(amount, marker, false);
					}
					case T:
					{
						final Currency currency = detail.getDefaultCurrency().getCurrency();
						SettlementLayoutExpensesSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutExpensesSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						double amount = detail.getDefaultCurrencyAmount();
						boolean taxInclusive = detail.getSettlement().getSalespoint().getCommonSettings()
								.isTaxInclusive();
						double taxAmount = detail.getTaxAmount();
						taxAmount = taxInclusive ? (amount < 0 ? Math.abs(taxAmount) : -Math.abs(taxAmount))
								: (amount < 0 ? -Math.abs(taxAmount) : Math.abs(taxAmount));
						return layoutArea.replaceMarker(
								SettlementLayoutExpensesSection.amountFormatter.format(taxAmount), marker, false);
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
