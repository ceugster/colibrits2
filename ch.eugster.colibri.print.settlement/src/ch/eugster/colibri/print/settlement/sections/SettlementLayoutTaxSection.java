package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;

import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementTax;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutTaxSection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	private static NumberFormat percentFormatter = DecimalFormat.getPercentInstance();

	public SettlementLayoutTaxSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("BBBBBBBBBBBBBBBBB PPPPP AAAAAAAAA MMMMMMMM");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Mehrwertsteuern\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("Mehrwertsteuern         AAAAAAAAA MMMMMMMM\n");
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
			return ((Settlement) printable).getTaxes().size() > 0;
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
				final Collection<SettlementTax> settlementTaxes = settlement.getTaxes();
				for (final SettlementTax settlementTax : settlementTaxes)
				{
					if (settlementTax.getCurrentTax().getPercentage() != 0D)
					{
						lines.addAll(super.prepareAreaDetail(settlementTax));
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
				final Collection<SettlementTax> settlementTaxes = settlement.getTaxes();
				if (!settlementTaxes.isEmpty())
				{
					final SettlementTax[] taxes = settlementTaxes.toArray(new SettlementTax[0]);
					final SettlementTax totalTax = SettlementTax.newInstance(settlement, taxes[0].getCurrentTax());
					for (final SettlementTax tax : taxes)
					{
						if (tax.getCurrentTax().getPercentage() != 0D)
						{
							totalTax.setBaseAmount(totalTax.getBaseAmount() + tax.getBaseAmount());
							totalTax.setQuantity(totalTax.getQuantity() + tax.getQuantity());
							totalTax.setTaxAmount(totalTax.getTaxAmount() + tax.getTaxAmount());
						}
					}
					lines.addAll(super.prepareAreaTotal(totalTax));
				}
			}
		}
		return lines;
	}

	public enum DetailKey implements IKey
	{
		B, P, A, M;

		@Override
		public String label()
		{
			switch (this)
			{
				case B:
				{
					return "Steuerbezeichnung";
				}
				case P:
				{
					return "Steuerprozent";
				}
				case A:
				{
					return "Basisbetrag";
				}
				case M:
				{
					return "Steuerbetrag";
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
			if (printable instanceof SettlementTax)
			{
				final SettlementTax tax = (SettlementTax) printable;

				switch (this)
				{
					case B:
					{
						String text = tax.getCurrentTax().getTax().getText();
						if ((text == null) || text.isEmpty())
						{
							text = tax.getCurrentTax().getTax().getTaxType().getName() + " "
									+ tax.getCurrentTax().getTax().getTaxRate().getName();
						}
						return layoutArea.replaceMarker(text, marker, true);
					}
					case P:
					{
						SettlementLayoutTaxSection.percentFormatter.setMaximumFractionDigits(1);
						final double percentage = tax.getCurrentTax().getPercentage();
						final String percentageString = SettlementLayoutTaxSection.percentFormatter.format(percentage);
						return layoutArea.replaceMarker(percentageString, marker, false);
					}
					case A:
					{
						final Currency currency = tax.getSettlement().getSalespoint().getPaymentType().getCurrency()
								.getCurrency();
						amountFormatter.setGroupingUsed(false);
						SettlementLayoutTaxSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutTaxSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						final String amount = SettlementLayoutTaxSection.amountFormatter.format(tax.getBaseAmount());
						return layoutArea.replaceMarker(amount, marker, false);
					}
					case M:
					{
						final Currency currency = tax.getSettlement().getSalespoint().getPaymentType().getCurrency()
								.getCurrency();
						amountFormatter.setGroupingUsed(false);
						SettlementLayoutTaxSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutTaxSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						final String amount = SettlementLayoutTaxSection.amountFormatter.format(-tax.getTaxAmount());
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
		A, M;

		@Override
		public String label()
		{
			switch (this)
			{
				case A:
				{
					return "Total Basisbetrag";
				}
				case M:
				{
					return "Total Mehrwertsteuer";
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
			if (printable instanceof SettlementTax)
			{
				final SettlementTax tax = (SettlementTax) printable;

				switch (this)
				{
					case A:
					{
						final Currency currency = tax.getSettlement().getSalespoint().getPaymentType().getCurrency()
								.getCurrency();
						SettlementLayoutTaxSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutTaxSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						final String amount = SettlementLayoutTaxSection.amountFormatter.format(tax.getBaseAmount());
						return layoutArea.replaceMarker(amount, marker, false);
					}
					case M:
					{
						final Currency currency = tax.getSettlement().getSalespoint().getPaymentType().getCurrency()
								.getCurrency();
						SettlementLayoutTaxSection.amountFormatter.setMinimumFractionDigits(currency
								.getDefaultFractionDigits());
						SettlementLayoutTaxSection.amountFormatter.setMaximumFractionDigits(currency
								.getDefaultFractionDigits());
						final String amount = SettlementLayoutTaxSection.amountFormatter.format(-tax.getTaxAmount());
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