package ch.eugster.colibri.print.receipt.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class ReceiptLayoutPaymentSection extends AbstractLayoutSection
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public ReceiptLayoutPaymentSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	protected String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Bezahlt");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("AAAAAAAAAAAAA WWW FFFFFFFF KKKKK LLLLLLLLL");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("Total bezahlt WWW FFFFFFFF KKKKK LLLLLLLLL\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	protected PrintOption getDefaultPrintOptionDetail()
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
		if (printable instanceof Receipt)
		{
			return ((Receipt) printable).getPayments().size() > 0;
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
		return false;
	}

	@Override
	protected Collection<String> prepareAreaDetail(final IPrintable printable)
	{
		final Collection<String> area = new ArrayList<String>();

		if (printable instanceof Receipt)
		{
			final Receipt receipt = (Receipt) printable;
			final Collection<Payment> payments = receipt.getPayments();
			for (final Payment payment : payments)
			{
				area.addAll(super.prepareAreaDetail(payment));
			}
		}
		return this.correctLineSizes(area);
	}

	protected Collection<String> prepareAreaTotal(final Receipt receipt)
	{
		final Collection<String> area = new ArrayList<String>();
		final double total = receipt.getPaymentAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
		if ((total != 0D) || this.getPrintOption(AreaType.TOTAL).equals(PrintOption.ALWAYS))
		{
			final Collection<String> lines = this.getPattern(AreaType.TOTAL);

			final String[] markers = this.getMarkers(AreaType.TOTAL, lines);
			for (String line : lines)
			{
				for (final String marker : markers)
				{
					line = this.replace(AreaType.TOTAL, receipt, marker, line);
				}
				area.add(line);
			}
		}
		return area;
	}

	public enum DetailKey implements IKey
	{
		A, F, K, L, W;

		@Override
		public String label()
		{
			switch (this)
			{
				case A:
				{
					return "Zahlungsart";
				}
				case F:
				{
					return "Zahlungsbetrag FW";
				}
				case K:
				{
					return "Kurs";
				}
				case L:
				{
					return "Zahlungsbetrag LW";
				}
				case W:
				{
					return "W�hrung";
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
			if (printable instanceof Payment)
			{
				final Payment payment = (Payment) printable;

				switch (this)
				{
					case A:
					{
						if (payment.isBack())
						{
							return layoutSection.replaceMarker("R�ckgeld " + payment.getPaymentType().getCode(),
									marker, true);
						}
						else
						{
							return layoutSection.replaceMarker(payment.getPaymentType().getCode(), marker, true);
						}
					}
					case W:
					{
						if (!payment.getPaymentType().getCurrency().getId()
								.equals(payment.getReceipt().getDefaultCurrency().getId()))
						{
							return layoutSection.replaceMarker(payment.getPaymentType().getCurrency().getCode(),
									marker, true);
						}
						return layoutSection.replaceMarker("", marker, false);
					}
					case L:
					{
						final double amount = payment.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
						ReceiptLayoutPaymentSection.amountFormatter.setMinimumFractionDigits(payment.getPaymentType()
								.getCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutPaymentSection.amountFormatter.setMaximumFractionDigits(payment.getPaymentType()
								.getCurrency().getCurrency().getDefaultFractionDigits());
						final String formattedAmount = ReceiptLayoutPaymentSection.amountFormatter.format(Math.abs(amount));
						return layoutSection.replaceMarker(formattedAmount, marker, false);
					}
					case F:
					{
						if (!payment.getPaymentType().getCurrency().getId()
								.equals(payment.getReceipt().getDefaultCurrency().getId()))
						{
							ReceiptLayoutPaymentSection.amountFormatter.setMinimumFractionDigits(payment
									.getPaymentType().getCurrency().getCurrency().getDefaultFractionDigits());
							ReceiptLayoutPaymentSection.amountFormatter.setMaximumFractionDigits(payment
									.getPaymentType().getCurrency().getCurrency().getDefaultFractionDigits());
							final double amount = payment.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY);
							final String formattedAmount = ReceiptLayoutPaymentSection.amountFormatter.format(amount);
							return layoutSection.replaceMarker(formattedAmount, marker, false);
						}
						return layoutSection.replaceMarker("", marker, false);
					}
					case K:
					{
						if (payment.isBack())
						{
							return layoutSection.replaceMarker(payment.getPaymentType().getCurrency().getCode(), marker, false);
						}
						else if (!payment.getPaymentType().getCurrency().getId()
								.equals(payment.getReceipt().getDefaultCurrency().getId()))
						{
							ReceiptLayoutPaymentSection.amountFormatter.setMinimumFractionDigits(0);
							ReceiptLayoutPaymentSection.amountFormatter.setMaximumFractionDigits(3);
							double quotation = payment.getForeignCurrencyQuotation();
							final String formattedAmount = ReceiptLayoutPaymentSection.amountFormatter.format(quotation);
							return layoutSection.replaceMarker(formattedAmount, marker, false);
						}
						return layoutSection.replaceMarker("", marker, false);
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
		F, K, L, W;

		@Override
		public String label()
		{
			switch (this)
			{
				case F:
				{
					return "Fremdw�hrungsbetrag";
				}
				case K:
				{
					return "Kurs";
				}
				case L:
				{
					return "Summe Zahlungen LW";
				}
				case W:
				{
					return "Fremdw�hrungscode";
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
					case F:
					{
						if (receipt.getForeignCurrency().getId().equals(receipt.getDefaultCurrency().getId()))
						{
							return layoutSection.replaceMarker("", marker, true);
						}
						else
						{
							ReceiptLayoutPaymentSection.amountFormatter.setMinimumFractionDigits(receipt
									.getForeignCurrency().getCurrency().getDefaultFractionDigits());
							ReceiptLayoutPaymentSection.amountFormatter.setMaximumFractionDigits(receipt
									.getForeignCurrency().getCurrency().getDefaultFractionDigits());
							final double amount = receipt.getPaymentAmount(Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY);
							return layoutSection.replaceMarker(ReceiptLayoutPaymentSection.amountFormatter.format(amount),
									marker, false);
						}
					}
					case K:
					{
						if (receipt.getForeignCurrency().getId().equals(receipt.getDefaultCurrency().getId()))
						{
							return layoutSection.replaceMarker("", marker, true);
						}
						else
						{
							ReceiptLayoutPaymentSection.amountFormatter.setMinimumFractionDigits(0);
							ReceiptLayoutPaymentSection.amountFormatter.setMaximumFractionDigits(6);
							final double quotation = receipt.getForeignCurrency().getQuotation();
							return layoutSection.replaceMarker(ReceiptLayoutPaymentSection.amountFormatter.format(quotation),
									marker, false);
						}
					}
					case L:
					{
						ReceiptLayoutPaymentSection.amountFormatter.setMinimumFractionDigits(receipt
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutPaymentSection.amountFormatter.setMaximumFractionDigits(receipt
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						final double amount = receipt.getPaymentAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
						final String formattedAmount = ReceiptLayoutPaymentSection.amountFormatter.format(amount);
						return layoutSection.replaceMarker(formattedAmount, marker, false);
					}
					case W:
					{
						if (receipt.getForeignCurrency().getId().equals(receipt.getDefaultCurrency().getId()))
						{
							return layoutSection.replaceMarker("", marker, false);
						}
						else
						{
							return layoutSection.replaceMarker(receipt.getForeignCurrency().getCurrency().getCurrencyCode(), marker, false);
						}
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
