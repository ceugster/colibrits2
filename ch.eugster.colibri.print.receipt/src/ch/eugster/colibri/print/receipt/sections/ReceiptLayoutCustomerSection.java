package ch.eugster.colibri.print.receipt.sections;

import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class ReceiptLayoutCustomerSection extends AbstractLayoutSection
{
	public ReceiptLayoutCustomerSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("Kundenkarte:    KKKKKKKKKKK\n");
		builder = builder.append("Kontostand:     SSSSSSSSSSS");
		return builder.toString();
	}

	@Override
	public Collection<String> prepareArea(final AreaType areaType, final IPrintable printable)
	{
		final Collection<String> area = new ArrayList<String>();
		if (printable instanceof Receipt)
		{
			final Receipt receipt = (Receipt) printable;

			if ((receipt.getCustomer() != null) || this.getPrintOption(areaType).equals(PrintOption.ALWAYS))
			{
				final Collection<String> lines = this.getPattern(areaType);
				final String[] markers = this.getMarkers(areaType, lines);

				if ((markers.length > 0) || this.getPrintOption(areaType).equals(PrintOption.ALWAYS))
				{
					for (String line : lines)
					{
						for (final String marker : markers)
						{
							line = this.replace(areaType, receipt, marker, line);
						}
						area.add(line);
					}
				}
			}
		}
		return area;
	}

	@Override
	protected PrintOption getDefaultPrintOptionDetail()
	{
		return PrintOption.OPTIONALLY;
	}

	@Override
	protected PrintOption getDefaultPrintOptionTotal()
	{
		return PrintOption.NEVER;
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
							return layoutSection.replaceMarker(Double.valueOf(receipt.getCustomer().getAccount())
									.toString(), marker, false);
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

	@Override
	protected boolean hasData(IPrintable printable)
	{
		if (printable instanceof Receipt)
		{
			return ((Receipt) printable).getCustomer() != null;
		}
		return false;
	}

}
