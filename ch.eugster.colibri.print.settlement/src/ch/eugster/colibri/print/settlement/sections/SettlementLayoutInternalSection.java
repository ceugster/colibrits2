package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.Locale;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementInternal;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutInternalSection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutInternalSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("BBBBBBBBBBBB DDDDDDDDDD TTTTT AAAAAAAAAAAA");
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
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Einlagen/Entnahmen\n");
		builder = builder.append("------------------------------------------");
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
			return ((Settlement) printable).getInternals().size() > 0;
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
				final Collection<SettlementInternal> internals = settlement.getInternals();
				for (final SettlementInternal internal : internals)
				{
					lines.addAll(super.prepareAreaDetail(internal));
				}
			}
		}
		return lines;
	}

	public enum DetailKey implements IKey
	{
		B, D, T, A;

		@Override
		public String label()
		{
			switch (this)
			{
				case B:
				{
					return "Typ";
				}
				case D:
				{
					return "Datum";
				}
				case T:
				{
					return "Zeit";
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
			if (printable instanceof SettlementInternal)
			{
				final SettlementInternal internal = (SettlementInternal) printable;

				switch (this)
				{
					case B:
					{
						return layoutArea.replaceMarker(internal.getProductGroup().getCode(), marker, true);
					}
					case D:
					{
						Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
						calendar.setTime(internal.getDate() == null ? calendar.getTime() : internal.getDate());
						return layoutArea.replaceMarker(SimpleDateFormat.getDateInstance().format(calendar.getTime()),
								marker, true);
					}
					case T:
					{
						Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
						calendar.setTime(internal.getDate() == null ? calendar.getTime() : internal.getDate());
						return layoutArea.replaceMarker(SimpleDateFormat.getTimeInstance().format(calendar.getTime()),
								marker, true);
					}
					case A:
					{
						final Currency currency = internal.getSettlement().getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						final double amount = internal.getDefaultCurrencyAmount();
						SettlementLayoutInternalSection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutInternalSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutInternalSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(SettlementLayoutInternalSection.amountFormatter.format(amount),
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
