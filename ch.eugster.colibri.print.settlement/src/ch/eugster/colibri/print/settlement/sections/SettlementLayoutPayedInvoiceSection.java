package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementPayedInvoice;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutPayedInvoiceSection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutPayedInvoiceSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("NNNNNNNNNNNNNNNNNNN DDDDDDDDDD BBBBBBBBBBB");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Bezahlte Rechnungen\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("Bezahlte Rechnungen            BBBBBBBBBBB\n");
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
			return settlement.getPayedInvoices().size() > 0;
		}
		if (printable instanceof SettlementPayedInvoice)
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
				final Collection<SettlementPayedInvoice> settlementPayedInvoices = settlement.getPayedInvoices();
				for (final SettlementPayedInvoice settlementPayedInvoice : settlementPayedInvoices)
				{
					lines.addAll(super.prepareAreaDetail(settlementPayedInvoice));
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
				SettlementPayedInvoice totalPayedInvoice = SettlementPayedInvoice.newInstance(settlement);
				final Collection<SettlementPayedInvoice> settlementPayedInvoices = settlement.getPayedInvoices();
				for (final SettlementPayedInvoice settlementPayedInvoice : settlementPayedInvoices)
				{
					if (totalPayedInvoice.getDefaultCurrency() == null)
					{
						totalPayedInvoice.setDefaultCurrency(settlementPayedInvoice.getDefaultCurrency());
					}
					totalPayedInvoice.setDefaultCurrencyAmount(totalPayedInvoice.getDefaultCurrencyAmount()
							+ settlementPayedInvoice.getDefaultCurrencyAmount());
				}
				lines.addAll(super.prepareAreaTotal(totalPayedInvoice));
			}
		}
		return lines;
	}

	public enum DetailKey implements IKey
	{
		N, D, B;

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
				case B:
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
			if (printable instanceof SettlementPayedInvoice)
			{
				final SettlementPayedInvoice payedInvoice = (SettlementPayedInvoice) printable;

				switch (this)
				{
					case N:
					{
						String value = "";

						if (payedInvoice.getNumber().isEmpty())
						{
							value = payedInvoice.getProductGroup().getName();
						}
						else
						{
							value = payedInvoice.getNumber();
						}

						return layoutArea.replaceMarker(value, marker, true);
					}
					case D:
					{
						String formattedDate = "";
						Calendar date = payedInvoice.getDate();
						if (date != null)
						{
							formattedDate = SimpleDateFormat.getDateInstance().format(date.getTime());
						}
						return layoutArea.replaceMarker(formattedDate, marker, true);
					}
					case B:
					{
						final Currency currency = payedInvoice.getSettlement().getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						final double amount = payedInvoice.getDefaultCurrencyAmount();
						SettlementLayoutPayedInvoiceSection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutPayedInvoiceSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutPayedInvoiceSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(
								SettlementLayoutPayedInvoiceSection.amountFormatter.format(amount), marker, false);
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
		B;

		@Override
		public String label()
		{
			switch (this)
			{
				case B:
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
			if (printable instanceof SettlementPayedInvoice)
			{
				final SettlementPayedInvoice payedInvoice = (SettlementPayedInvoice) printable;

				switch (this)
				{
					case B:
					{
						final Currency currency = payedInvoice.getSettlement().getSalespoint().getPaymentType()
								.getCurrency().getCurrency();
						final double amount = payedInvoice.getDefaultCurrencyAmount();
						SettlementLayoutPayedInvoiceSection.amountFormatter.setGroupingUsed(false);
						SettlementLayoutPayedInvoiceSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutPayedInvoiceSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						return layoutArea.replaceMarker(
								SettlementLayoutPayedInvoiceSection.amountFormatter.format(amount), marker, false);
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
