package ch.eugster.colibri.print.receipt.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class ReceiptLayoutTaxSection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	private static NumberFormat percentFormatter = DecimalFormat.getPercentInstance();

	public ReceiptLayoutTaxSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public Collection<String> prepareAreaDetail(final IPrintable printable)
	{
		final Collection<String> area = new ArrayList<String>();

		if (printable instanceof Receipt)
		{
			final Receipt receipt = (Receipt) printable;
			final Collection<CurrentTax> currentTaxes = receipt.getTaxes();
			for (final CurrentTax currentTax : currentTaxes)
			{
				final double amount = receipt.getPositionsBrutAmount(currentTax);
				if ((amount != 0D) || this.getPrintOption(AreaType.DETAIL).equals(PrintOption.ALWAYS))
				{
					area.addAll(this.prepareArea(receipt, currentTax));
				}
			}
		}
		return this.correctLineSizes(area);
	}

	@Override
	protected String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("C SSSSSSSS PPPPP BBBBBBBB MMMMMMM NNNNNNNN\n");
		return builder.toString();
	}

	@Override
	protected String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("Total MWST       BBBBBBBB MMMMMMM NNNNNNNN");
		return builder.toString();
	}

	@Override
	protected PrintOption getDefaultPrintOptionDetail()
	{
		return PrintOption.OPTIONALLY;
	}

	@Override
	protected PrintOption getDefaultPrintOptionTotal()
	{
		return PrintOption.OPTIONALLY;
	}

	@Override
	protected IKey[] getKeys(final AreaType areaType)
	{
		switch (areaType)
		{
			case TITLE:
			{
				return TitleKey.values();
			}
			case DETAIL:
			{
				return DetailKey.values();
			}
			case TOTAL:
			{
				return TotalKey.values();
			}
			default:
			{
				throw new RuntimeException("invalid area type");
			}
		}
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
		if (printable instanceof Receipt)
		{
			return ((Receipt) printable).getTaxes().size() > 0;
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
		return false;
	}

	@Override
	protected boolean hasTotalArea()
	{
		return true;
	}

	private Collection<String> prepareArea(final Receipt receipt, final CurrentTax currentTax)
	{
		final Collection<String> area = new ArrayList<String>();
		final CurrentTaxWrapper wrapper = new CurrentTaxWrapper(receipt, currentTax);
		final Collection<String> lines = this.getPattern(AreaType.DETAIL);
		final String[] markers = this.getMarkers(AreaType.DETAIL, lines);
		for (String line : lines)
		{
			for (final String marker : markers)
			{
				line = this.replace(AreaType.DETAIL, wrapper, marker, line);
			}
			area.add(line);
		}
		return area;
	}

	public enum DetailKey implements IKey
	{
		C, S, P, B, M, N;

		@Override
		public String label()
		{
			switch (this)
			{
				case C:
				{
					return "Steuercode";
				}
				case S:
				{
					return "Steuerbezeichnung";
				}
				case P:
				{
					return "Steuerprozent";
				}
				case B:
				{
					return "Bruttobetrag";
				}
				case M:
				{
					return "Steuerbetrag";
				}
				case N:
				{
					return "Nettobetrag";
				}
				default:
				{
					throw new RuntimeException("invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutSection, final IPrintable printable, final String marker)
		{
			if (printable instanceof CurrentTaxWrapper)
			{
				final CurrentTaxWrapper currentTaxWrapper = (CurrentTaxWrapper) printable;

				switch (this)
				{
					case C:
					{
						return layoutSection.replaceMarker(currentTaxWrapper.getCurrentTax().getTax().getTaxRate()
								.getCode(), marker, true);
					}
					case S:
					{
						String text = currentTaxWrapper.getCurrentTax().getTax().getText();
						if ((text == null) || text.isEmpty())
						{
							text = currentTaxWrapper.getCurrentTax().getTax().getTaxType().getName();
						}
						return layoutSection.replaceMarker(text, marker, true);
					}
					case P:
					{
						ReceiptLayoutTaxSection.percentFormatter.setMaximumFractionDigits(1);
						final double percentage = currentTaxWrapper.getCurrentTax().getPercentage();
						final String percentageString = ReceiptLayoutTaxSection.percentFormatter.format(percentage);
						return layoutSection.replaceMarker(percentageString, marker, false);
					}
					case B:
					{
						ReceiptLayoutTaxSection.amountFormatter.setMinimumFractionDigits(currentTaxWrapper.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutTaxSection.amountFormatter.setMaximumFractionDigits(currentTaxWrapper.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						final String brutAmount = ReceiptLayoutTaxSection.amountFormatter.format(currentTaxWrapper
								.getBrutAmount());
						return layoutSection.replaceMarker(brutAmount, marker, false);
					}
					case M:
					{
						ReceiptLayoutTaxSection.amountFormatter.setMinimumFractionDigits(currentTaxWrapper.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutTaxSection.amountFormatter.setMaximumFractionDigits(currentTaxWrapper.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						final String taxAmount = ReceiptLayoutTaxSection.amountFormatter.format(currentTaxWrapper
								.getTaxAmount());
						return layoutSection.replaceMarker(taxAmount, marker, false);
					}
					case N:
					{
						ReceiptLayoutTaxSection.amountFormatter.setMinimumFractionDigits(currentTaxWrapper.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutTaxSection.amountFormatter.setMaximumFractionDigits(currentTaxWrapper.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						final String netAmount = ReceiptLayoutTaxSection.amountFormatter.format(currentTaxWrapper
								.getBrutAmount() - currentTaxWrapper.getTaxAmount());
						return layoutSection.replaceMarker(netAmount, marker, false);
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
			return null;
		}

		@Override
		public String replace(final ILayoutSection layoutSection, final IPrintable printable, final String marker)
		{
			return null;
		}

	}

	public enum TotalKey implements IKey
	{
		B, M, N;

		@Override
		public String label()
		{
			switch (this)
			{
				case B:
				{
					return "Total Bruttobetrag";
				}
				case M:
				{
					return "Total Mehrwertsteuer";
				}
				case N:
				{
					return "Total Nettobetrag";
				}
				default:
				{
					throw new RuntimeException("invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutSection, final IPrintable printable, final String marker)
		{
			if (printable instanceof Receipt)
			{
				final Receipt receipt = (Receipt) printable;

				switch (this)
				{
					case B:
					{
						ReceiptLayoutTaxSection.amountFormatter.setMinimumFractionDigits(receipt.getDefaultCurrency()
								.getCurrency().getDefaultFractionDigits());
						ReceiptLayoutTaxSection.amountFormatter.setMaximumFractionDigits(receipt.getDefaultCurrency()
								.getCurrency().getDefaultFractionDigits());
						final double bruttoAmount = receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
								Position.AmountType.NETTO);
						final String brutto = ReceiptLayoutTaxSection.amountFormatter.format(bruttoAmount);
						return layoutSection.replaceMarker(brutto, marker, false);
					}
					case M:
					{
						ReceiptLayoutTaxSection.amountFormatter.setMinimumFractionDigits(receipt.getDefaultCurrency()
								.getCurrency().getDefaultFractionDigits());
						ReceiptLayoutTaxSection.amountFormatter.setMaximumFractionDigits(receipt.getDefaultCurrency()
								.getCurrency().getDefaultFractionDigits());
						final String tax = ReceiptLayoutTaxSection.amountFormatter.format(receipt
								.getPositionsTaxAmount());
						return layoutSection.replaceMarker(tax, marker, false);
					}
					case N:
					{
						ReceiptLayoutTaxSection.amountFormatter.setMinimumFractionDigits(receipt.getDefaultCurrency()
								.getCurrency().getDefaultFractionDigits());
						ReceiptLayoutTaxSection.amountFormatter.setMaximumFractionDigits(receipt.getDefaultCurrency()
								.getCurrency().getDefaultFractionDigits());
						final double amount = receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
								Position.AmountType.NETTO);
						final double taxAmount = receipt.getPositionsTaxAmount();
						final String netAmount = ReceiptLayoutTaxSection.amountFormatter.format(amount - taxAmount);
						return layoutSection.replaceMarker(netAmount, marker, false);
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

	private class CurrentTaxWrapper implements IPrintable
	{
		private final Receipt receipt;

		private final CurrentTax currentTax;

		public CurrentTaxWrapper(final Receipt receipt, final CurrentTax currentTax)
		{
			this.receipt = receipt;
			this.currentTax = currentTax;
		}

		public double getBrutAmount()
		{
			return this.receipt.getPositionsBrutAmount(this.currentTax);
		}

		public CurrentTax getCurrentTax()
		{
			return this.currentTax;
		}

		public Receipt getReceipt()
		{
			return this.receipt;
		}

		public double getTaxAmount()
		{
			return this.receipt.getPositionsTaxAmount(this.currentTax);
		}
	}
}
