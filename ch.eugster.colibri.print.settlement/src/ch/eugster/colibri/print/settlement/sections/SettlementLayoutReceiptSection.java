package ch.eugster.colibri.print.settlement.sections;

import java.text.DateFormat;
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
import ch.eugster.colibri.persistence.model.SettlementReceipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutReceiptSection extends AbstractLayoutSection
{
	private static DateFormat dateFormatter = SimpleDateFormat.getDateInstance();

	private static DateFormat timeFormatter = SimpleDateFormat.getTimeInstance();

	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutReceiptSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("NNNNNNNNNNNN DDDDDDDDDD TTTTT AAAAAAAAAAAA");
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
		builder = builder.append("Stornierte Belege\n");
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
	public PrintOption getDefaultPrintOptionTotal()
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
			return ((Settlement) printable).getReversedReceipts().size() > 0;
		}
		if (printable instanceof SettlementReceipt)
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
				final Collection<SettlementReceipt> settlementReceipts = settlement.getReversedReceipts();
				for (final SettlementReceipt settlementReceipt : settlementReceipts)
				{
					lines.addAll(super.prepareAreaDetail(settlementReceipt));
				}
			}
		}
		return lines;
	}

	public enum DetailKey implements IKey
	{
		N, D, T, A;

		@Override
		public String label()
		{
			switch (this)
			{
				case N:
				{
					return "Nummer";
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
			if (printable instanceof SettlementReceipt)
			{
				final SettlementReceipt receipt = (SettlementReceipt) printable;

				switch (this)
				{
					case N:
					{
						final String format = receipt.getSettlement().getSalespoint().getCommonSettings()
								.getReceiptNumberFormat();
						if ((format == null) || format.isEmpty())
						{
							return layoutArea.replaceMarker(receipt.getNumber().toString(), marker, true);
						}
						final NumberFormat formatter = new DecimalFormat(format);
						return layoutArea
								.replaceMarker(formatter.format(receipt.getNumber().longValue()), marker, true);
					}
					case D:
					{
						Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
						calendar.setTime(receipt.getTime());
						return layoutArea.replaceMarker(
								SettlementLayoutReceiptSection.dateFormatter.format(calendar.getTime()), marker, false);
					}
					case T:
					{
						Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
						calendar.setTime(receipt.getTime());
						return layoutArea.replaceMarker(
								SettlementLayoutReceiptSection.timeFormatter.format(calendar.getTime()), marker, false);
					}
					case A:
					{
						final Currency currency = receipt.getSettlement().getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						final double amount = receipt.getAmount();
						SettlementLayoutReceiptSection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutReceiptSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutReceiptSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(SettlementLayoutReceiptSection.amountFormatter.format(amount),
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
