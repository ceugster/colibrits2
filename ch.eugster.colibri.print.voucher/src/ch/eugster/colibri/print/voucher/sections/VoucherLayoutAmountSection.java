package ch.eugster.colibri.print.voucher.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class VoucherLayoutAmountSection extends AbstractLayoutSection
{
	public VoucherLayoutAmountSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("\n");
//		builder = builder.append(getFontSize(ReceiptPrinterService.Size.DOUBLE_HEIGHT));
		builder = builder.append("             G U T S C H E I N\n");
		builder = builder.append("             *****************\n");
		builder = builder.append("               WWW VVVVVVVVV\n");
//		builder = builder.append(getFontSize(ReceiptPrinterService.Size.NORMAL));
		builder = builder.append("\n");
		return builder.toString();
	}

	@Override
	public PrintOption getDefaultPrintOptionDetail()
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
		return null;
	}

	@Override
	protected IKey[] getKeysTotal()
	{
		return null;
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
		return false;
	}

	public enum DetailKey implements IKey
	{
		W, V;

		private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

		@Override
		public String label()
		{
			switch (this)
			{
				case W:
				{
					return "Währung";
				}
				case V:
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
		public String replace(final ILayoutSection layoutSection, final IPrintable printable, final String marker)
		{
			if (printable instanceof Receipt)
			{
				final Receipt receipt = (Receipt) printable;
				switch (this)
				{
					case W:
					{
						return layoutSection.replaceMarker(receipt.getDefaultCurrency().getCode(), marker, true);
					}
					case V:
					{
						double amount = 0d;
						final Collection<Payment> payments = receipt.getBackVouchers();
						for (final Payment payment : payments)
						{
							if (payment.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
							{
								amount += payment.getAmount();
							}
						}
						final java.util.Currency currency = receipt.getDefaultCurrency().getCurrency();
						DetailKey.amountFormatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
						DetailKey.amountFormatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
						return layoutSection.replaceMarker(DetailKey.amountFormatter.format(Math.abs(amount)), marker, false);
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

	@Override
	protected boolean hasData(IPrintable printable)
	{
		if (printable instanceof Receipt)
		{
			Receipt receipt = (Receipt) printable;
			return receipt.getBackVouchers().size() > 0;
		}
		return false;
	}

}
