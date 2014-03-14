package ch.eugster.colibri.print.voucher.sections;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class VoucherLayoutCustomerSection extends AbstractLayoutSection
{
	public VoucherLayoutCustomerSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------");
		builder = builder.append("Kundenkarte:    KKKKKKKKKKK\n");
		builder = builder.append("Kontostand:     SSSSSSSSSSS\n");
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
		N, K, S;

		@Override
		public String label()
		{
			switch (this)
			{
				case N:
				{
					return "Kundenname";
				}
				case K:
				{
					return "Kontonummer";
				}
				case S:
				{
					return "Kontostand";
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
				if (receipt.getCustomer() != null)
				{
					switch (this)
					{
						case N:
						{
							return layoutSection.replaceMarker(receipt.getCustomer().getFullname(), marker, true);
						}
						case K:
						{
							return layoutSection.replaceMarker(receipt.getCustomerCode(), marker, false);
						}
						case S:
						{
							double account = receipt.getCustomer().getAccount();
							amountFormatter.setMaximumFractionDigits(receipt.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
							amountFormatter.setMinimumFractionDigits(receipt.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
							return layoutSection.replaceMarker(amountFormatter.format(account), marker, false);
						}
						default:
						{
							throw new RuntimeException("invalid key");
						}
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
			String code = ((Receipt) printable).getCustomerCode();
			return code != null && !code.isEmpty();
		}
		return false;
	}

}
