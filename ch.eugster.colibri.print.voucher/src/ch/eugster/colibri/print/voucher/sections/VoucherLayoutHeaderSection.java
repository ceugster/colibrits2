package ch.eugster.colibri.print.voucher.sections;

import java.util.Calendar;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class VoucherLayoutHeaderSection extends AbstractLayoutSection
{
	public VoucherLayoutHeaderSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		String header = this.getCommonSettingsHeader();
		if (header != null)
		{
			builder.append(header);
		}
		builder = builder.append("\n");
		builder = builder.append("\n");
		builder = builder.append("KKKKKKKKKKKKKKKKKKKKKKKKK\n");
		builder = builder.append("NNNNNNN                   DDDDDDDDDD TTTTT\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public PrintOption getDefaultPrintOptionDetail()
	{
		return PrintOption.ALWAYS;
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
		K, N, D, T, U;

		@Override
		public String label()
		{
			switch (this)
			{
				case K:
				{
					return "Kassenbezeichnung";
				}
				case N:
				{
					return "Belegnummer";
				}
				case D:
				{
					return "Belegdatum";
				}
				case T:
				{
					return "Erstellungszeit";
				}
				case U:
				{
					return "Benutzer";
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
					case K:
					{
						return layoutSection.replaceMarker(receipt.getSettlement().getSalespoint().getName(), marker,
								true);
					}
					case N:
					{
						final String number = receipt.getNumber() == null ? "" : receipt.getNumber().toString();
						return layoutSection.replaceMarker(number, marker, false);
					}
					case D:
					{
						Calendar calendar = Calendar.getInstance();
						calendar = receipt.getTimestamp();
						return layoutSection.replaceMarker(calendar.getTime(), marker);
					}
					case T:
					{
						Calendar calendar = Calendar.getInstance();
						calendar = receipt.getTimestamp();
						return layoutSection.replaceMarker(calendar.getTime(), marker);
					}
					case U:
					{
						return layoutSection.replaceMarker(receipt.getUser(), marker);
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
		return true;
	}
}