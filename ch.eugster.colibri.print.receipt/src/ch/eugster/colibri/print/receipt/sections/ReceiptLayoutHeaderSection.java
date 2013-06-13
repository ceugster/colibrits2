package ch.eugster.colibri.print.receipt.sections;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Receipt.State;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class ReceiptLayoutHeaderSection extends AbstractLayoutSection
{
	public ReceiptLayoutHeaderSection(final ILayoutSectionType layoutAreaType)
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
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("******************************************\n");
		builder = builder.append("***                                    ***\n");
		builder = builder.append("***   B E L E G   S T O R N I E R T    ***\n");
		builder = builder.append("***                                    ***\n");
		builder = builder.append("******************************************");
		return builder.toString();
	}

	@Override
	public PrintOption getDefaultPrintOptionDetail()
	{
		return PrintOption.ALWAYS;
	}

	@Override
	public PrintOption getDefaultPrintOptionTitle()
	{
		return PrintOption.OPTIONALLY;
	}

	@Override
	public String getHelp(final AreaType areaType)
	{
		if (areaType.equals(AreaType.TITLE))
		{
			return "Dieser Bereich ist\nfür die Kennzeichnung\nstornierter Belege\nreserviert.";
		}
		else
		{
			return super.getHelp(areaType);
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
		return this.getPatternDetail().length > 0;
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
	protected Collection<String> prepareAreaTitle(final IPrintable printable)
	{
		final Collection<String> lines = new ArrayList<String>();

		if (printable instanceof Receipt)
		{
			final Receipt receipt = (Receipt) printable;
			if (receipt.getState().equals(State.REVERSED))
			{
				lines.add(this.getDefaultPatternTitle());
			}
		}

		// if (this.hasArea(AreaType.TITLE) &&
		// !this.getPrintOption(AreaType.TITLE).equals(PrintOption.NEVER))
		// {
		// final Collection<String> patternLines =
		// this.getPattern(AreaType.TITLE, printable);
		// final String[] markers = this.getMarkers(AreaType.TITLE,
		// patternLines);
		// for (String patternLine : patternLines)
		// {
		// for (final String marker : markers)
		// {
		// patternLine = this.replace(AreaType.TITLE, printable, marker,
		// patternLine);
		// }
		// if (!patternLine.trim().isEmpty() ||
		// this.getPrintOption(AreaType.TITLE).equals(PrintOption.ALWAYS))
		// {
		// lines.add(patternLine);
		// }
		// }
		// }
		return lines;
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
						simpleIntegerFormatter.setMinimumIntegerDigits(marker.length());
						final String number = receipt.getNumber() == null ? "" : simpleIntegerFormatter.format(receipt.getNumber());
						return layoutSection.replaceMarker(number, marker, false);
					}
					case D:
					{
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(receipt.getTimestamp());
						return layoutSection.replaceMarker(calendar.getTime(), marker);
					}
					case T:
					{
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(receipt.getTimestamp());
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
}