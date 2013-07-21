package ch.eugster.colibri.print.settlement.sections;

import java.util.Calendar;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutFooterSection extends AbstractLayoutSection
{
	public SettlementLayoutFooterSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		String number = this.getCommonSettingsTaxNumber();
		number = (number == null || number.isEmpty()) ? "000 000" : number;
		int left = (42 - "MWST-Nr: ".length() - number.length()) / 2;
		StringBuilder n = new StringBuilder();
		for (int i = 0; i < left; i++)
		{
			n = n.append(" ");
		}
		n = n.append("MWST-Nr.: ");
		n = n.append(number);
		for (int i = n.length(); i < 42; i++)
		{
			n = n.append(" ");
		}
		builder = builder.append("------------------------------------------\n");
		builder = builder.append(n.toString() + "\n");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		return builder.toString();
	}

	@Override
	public PrintOption getDefaultPrintOptionDetail()
	{
		return PrintOption.ALWAYS;
	}

	@Override
	public boolean hasData(IPrintable printable)
	{
		printable.getClass();
		return true;
	}

	public int getEditorAreaHeight()
	{
		return 128;
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
		K, D, T, U;

		@Override
		public String label()
		{
			switch (this)
			{
				case K:
				{
					return "Kassenbezeichnung";
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
			if (printable instanceof Settlement)
			{
				final Settlement settlement = (Settlement) printable;

				switch (this)
				{
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
