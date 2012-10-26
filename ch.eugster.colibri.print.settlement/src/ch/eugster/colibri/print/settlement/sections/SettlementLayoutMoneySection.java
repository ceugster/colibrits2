package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementMoney;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutMoneySection extends AbstractLayoutSection
{
	private static NumberFormat integerFormatter = DecimalFormat.getIntegerInstance();

	private static NumberFormat doubleFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutMoneySection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("CCC TTTTTTTTTTTTTTTTTTTTTTTT MMM AAAAAAAAA");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("CCC Kassensturz CCC\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("CCC Total Kassensturz         AAAAAAAAAAAA\n");
		builder = builder.append("==========================================");
		return builder.toString();
	}

	@Override
	public PrintOption getDefaultPrintOptionDetail()
	{
		return PrintOption.OPTIONALLY;
	}

	@Override
	public PrintOption getDefaultPrintOptionTitle()
	{
		return PrintOption.OPTIONALLY;
	}

	@Override
	public PrintOption getDefaultPrintOptionTotal()
	{
		return PrintOption.OPTIONALLY;
	}

	@Override
	public Collection<String> prepareSection(final IPrintable printable)
	{
		final Collection<String> sections = new ArrayList<String>();
		if (printable instanceof Settlement)
		{
			final Settlement settlement = (Settlement) printable;
			final Collection<SettlementMoney> allMoneys = settlement.getMoneys();
			final Map<Long, Collection<SettlementMoney>> subSections = this.prepareSubSections(allMoneys);
			final Long[] keys = subSections.keySet().toArray(new Long[0]);
			Arrays.sort(keys);
			for (final Long key : keys)
			{
				final Collection<SettlementMoney> currencyMoneys = subSections.get(key);
				if ((currencyMoneys != null) && !currencyMoneys.isEmpty())
				{
					final SettlementMoney[] moneys = currencyMoneys.toArray(new SettlementMoney[0]);
					if (moneys.length > 0)
					{
						Arrays.sort(moneys);
						if (this.hasTitleArea())
						{
							if (!this.getPrintOption(AreaType.TITLE).equals(PrintOption.NEVER))
							{
								sections.addAll(this.prepareArea(AreaType.TITLE, moneys[0]));
							}
						}
						if (this.hasDetailArea())
						{
							if (!this.getPrintOption(AreaType.DETAIL).equals(PrintOption.NEVER))
							{
								Arrays.sort(moneys);
								for (final SettlementMoney detail : moneys)
								{
									sections.addAll(this.prepareArea(AreaType.DETAIL, detail));
								}
							}
						}
						if (this.hasTotalArea())
						{
							if (!this.getPrintOption(AreaType.TOTAL).equals(PrintOption.NEVER))
							{
								final SettlementMoney totalDetail = SettlementMoney.newInstance(settlement,
										moneys[0].getStock(), moneys[0].getPaymentType());
								for (final SettlementMoney detail : moneys)
								{
									totalDetail.setAmount(totalDetail.getAmount() + detail.getAmount());
									totalDetail.setQuantity(totalDetail.getQuantity() + detail.getQuantity());
								}
								sections.addAll(this.prepareArea(AreaType.TOTAL, totalDetail));
							}
						}
					}
				}
			}
		}
		return sections;
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

	private Map<Long, Collection<SettlementMoney>> prepareSubSections(final Collection<SettlementMoney> moneys)
	{
		final Map<Long, Collection<SettlementMoney>> subSections = new HashMap<Long, Collection<SettlementMoney>>();
		for (final SettlementMoney money : moneys)
		{
			final Currency currency = money.getPaymentType().getCurrency();
			Collection<SettlementMoney> subSection = subSections.get(currency.getId());
			if (subSection == null)
			{
				subSection = new ArrayList<SettlementMoney>();
				subSections.put(currency.getId(), subSection);
			}
			subSection.add(money);
		}
		return subSections;
	}

	public enum DetailKey implements IKey
	{
		C, T, M, A;

		@Override
		public String label()
		{
			switch (this)
			{
				case C:
				{
					return "Währungscode";
				}
				case T:
				{
					return "Text";
				}
				case M:
				{
					return "Anzahl";
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
			if (printable instanceof SettlementMoney)
			{
				final SettlementMoney money = (SettlementMoney) printable;

				switch (this)
				{
					case C:
					{
						return layoutArea.replaceMarker(money.getPaymentType().getCurrency().getCode(), marker, true);
					}
					case T:
					{
						final String text = money.getCode();
						return layoutArea.replaceMarker(text, marker, true);
					}
					case M:
					{
						final String quantity = SettlementLayoutMoneySection.integerFormatter.format(money
								.getQuantity());
						return layoutArea.replaceMarker(quantity, marker, false);
					}
					case A:
					{

						final java.util.Currency currency = money.getPaymentType().getCurrency().getCurrency();
						SettlementLayoutMoneySection.doubleFormatter.setGroupingUsed(false);
						SettlementLayoutMoneySection.doubleFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutMoneySection.doubleFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(
								SettlementLayoutMoneySection.doubleFormatter.format(money.getAmount()), marker, false);
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
		C;

		@Override
		public String label()
		{
			switch (this)
			{
				case C:
				{
					return "Währungscode";
				}
				default:
				{
					throw new RuntimeException("Invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutArea, final IPrintable printable, final String marker)
		{
			if (printable instanceof SettlementMoney)
			{
				final SettlementMoney detail = (SettlementMoney) printable;

				switch (this)
				{
					case C:
					{
						return layoutArea.replaceMarker(detail.getPaymentType().getCurrency().getCode(), marker, true);
					}
					default:
					{
						throw new RuntimeException("Invalid key");
					}
				}
			}
			return marker;
		}
	}

	public enum TotalKey implements IKey
	{
		C, A;

		@Override
		public String label()
		{
			switch (this)
			{
				case C:
				{
					return "Währungscode";
				}
				case A:
				{
					return "Gesamtbetrag Kassensturz";
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
			if (printable instanceof SettlementMoney)
			{
				final SettlementMoney detail = (SettlementMoney) printable;

				switch (this)
				{
					case C:
					{
						final String code = detail.getPaymentType().getCurrency().getCode();
						return layoutArea.replaceMarker(code, marker, true);
					}
					case A:
					{
						final java.util.Currency currency = detail.getPaymentType().getCurrency().getCurrency();
						SettlementLayoutMoneySection.doubleFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutMoneySection.doubleFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						final String amount = SettlementLayoutMoneySection.doubleFormatter.format(detail.getAmount());
						return layoutArea.replaceMarker(amount, marker, false);
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
