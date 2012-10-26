package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;

import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementRestitutedPosition;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutRestitutionSection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutRestitutionSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("PPPPPPPPPPPPPPPPPPPPPPPPPPPPP AAAAAAAAAAAA\n");
		builder = builder.append("   CCCCCCCCCCCCC TTTTTTTTTTTTTTTTTTTTTTTTT");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Rücknahmen\n");
		builder = builder.append("------------------------------------------");
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
		if (printable instanceof Settlement)
		{
			return ((Settlement) printable).getRestitutedPositions().size() > 0;
		}
		if (printable instanceof SettlementRestitutedPosition)
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
				final Collection<SettlementRestitutedPosition> restitutedPositions = settlement
						.getRestitutedPositions();
				for (final SettlementRestitutedPosition restitutedPosition : restitutedPositions)
				{
					lines.addAll(super.prepareAreaDetail(restitutedPosition));
				}
			}
		}
		return lines;
	}

	public enum DetailKey implements IKey
	{
		P, C, T, A;

		@Override
		public String label()
		{
			switch (this)
			{
				case P:
				{
					return "Warengruppe";
				}
				case C:
				{
					return "Code";
				}
				case T:
				{
					return "Titel";
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
			if (printable instanceof SettlementRestitutedPosition)
			{
				final SettlementRestitutedPosition restitutedPosition = (SettlementRestitutedPosition) printable;

				switch (this)
				{
					case P:
					{
						ProductGroup pg = restitutedPosition.getProductGroup();
						String code = pg.getCode() + " " + pg.getName();
						return layoutArea.replaceMarker(code, marker, true);
					}
					case C:
					{
						String code = restitutedPosition.getCode();
						return layoutArea.replaceMarker(code, marker, true);
					}
					case T:
					{
						return layoutArea.replaceMarker(restitutedPosition.getText(), marker, true);
					}
					case A:
					{
						final Currency currency = restitutedPosition.getSettlement().getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						final double amount = restitutedPosition.getDefaultCurrencyAmount();
						SettlementLayoutRestitutionSection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutRestitutionSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutRestitutionSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(
								SettlementLayoutRestitutionSection.amountFormatter.format(amount), marker, false);
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
