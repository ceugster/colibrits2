package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.service.SettlementService;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutHeaderSection extends AbstractLayoutSection
{
	public SettlementLayoutHeaderSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	protected String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("\n");
		builder = builder.append("\n");
		builder = builder.append("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK\n");
		builder = builder.append("\n");
		builder = builder.append("Tagesabschluss            DDDDDDDDDD TTTTT\n");
		builder = builder.append("Anzahl Belege             AAAAAAAAAA\n");
		return builder.toString();
	}

	@Override
	protected String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("\n");
		builder = builder.append("\n");
		builder = builder.append("******************************************\n");
		builder = builder.append("\n");
		builder = builder.append("        Tagesabschluss provisorisch\n");
		builder = builder.append("\n");
		builder = builder.append("******************************************\n");
		return builder.toString();
	}

	protected Collection<String> prepareAreaTitle(final IPrintable printable)
	{
		final Collection<String> lines = new ArrayList<String>();
		if (printable instanceof Settlement)
		{
			Settlement settlement = (Settlement) printable;
			if (settlement.getState() != null && settlement.getState().equals(SettlementService.State.PROVISIONAL))
			{
				if (printIt(AreaType.TITLE, printable))
				{
					final Collection<String> patternLines = adaptPatternTitle(this.getPattern(AreaType.TITLE), printable);
					final String[] markers = this.getMarkers(AreaType.TITLE, patternLines);
					for (String patternLine : patternLines)
					{
						for (final String marker : markers)
						{
							patternLine = this.replace(AreaType.TITLE, printable, marker, patternLine);
						}
						if (!patternLine.trim().isEmpty() || this.getPrintOption(AreaType.TITLE).equals(PrintOption.ALWAYS))
						{
							lines.add(patternLine);
						}
					}
				}
			}
		}
		return lines;
	}

	@Override
	protected PrintOption getDefaultPrintOptionDetail()
	{
		return PrintOption.ALWAYS;
	}

	@Override
	protected PrintOption getDefaultPrintOptionTitle()
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

	public enum DetailKey implements IKey
	{
		A, K, D, T, U;

		@Override
		public String label()
		{
			switch (this)
			{
				case A:
				{
					return "Anzahl Belege";
				}
				case K:
				{
					return "Kassenbezeichnung";
				}
				case D:
				{
					return "Abschlussdatum";
				}
				case T:
				{
					return "Abschlusszeit";
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
			if (printable instanceof Settlement)
			{
				final Settlement settlement = (Settlement) printable;

				switch (this)
				{
					case A:
					{
						return layoutSection.replaceMarker(
								DecimalFormat.getInstance().format(settlement.getReceiptCount()), marker, false);
					}
					case K:
					{
						return layoutSection.replaceMarker(settlement.getSalespoint().getName(), marker, true);
					}
					case D:
					{
						Calendar calendar = settlement.getTimestamp();
						return layoutSection.replaceMarker(calendar.getTime(), marker);
					}
					case T:
					{
						Calendar calendar = settlement.getTimestamp();
						return layoutSection.replaceMarker(calendar.getTime(), marker);
					}
					case U:
					{
						return layoutSection.replaceMarker(settlement.getUser(), marker);
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
