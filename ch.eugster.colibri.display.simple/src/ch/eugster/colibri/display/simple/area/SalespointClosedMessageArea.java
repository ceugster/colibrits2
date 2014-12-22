package ch.eugster.colibri.display.simple.area;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Locale;

import ch.eugster.colibri.display.area.AbstractLayoutArea;
import ch.eugster.colibri.display.area.IKey;
import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.display.area.ILayoutAreaType;
import ch.eugster.colibri.persistence.model.print.IPrintable;

public class SalespointClosedMessageArea extends AbstractLayoutArea implements ILayoutArea
{
	private static DateFormat dateFormatter = SimpleDateFormat.getDateInstance();

	private static DateFormat timeFormatter = SimpleDateFormat.getTimeInstance();

	public SalespointClosedMessageArea(final ILayoutAreaType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPattern()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Kasse geschlossen");
		return builder.toString();
	}

	@Override
	public int getDefaultTimerDelay()
	{
		return 0;
	}

	@Override
	protected Collection<String> getItems(final IPrintable printable)
	{
		final Collection<String> items = new ArrayList<String>();
		String pattern = this.getPattern();
		final String[] markers = this.getMarkers(pattern);
		for (final String marker : markers)
		{
			pattern = this.replace(null, marker, pattern);
		}
		items.add(pattern);
		return items;
	}

	@Override
	protected IKey[] getKeys()
	{
		return Key.values();
	}

	public enum Key implements IKey
	{
		D, T;

		@Override
		public String label()
		{
			switch (this)
			{
				case D:
				{
					return "Datum";
				}
				case T:
				{
					return "Zeit";
				}
				default:
				{
					throw new RuntimeException("Invalid Key");
				}
			}
		}

		@Override
		public String replace(final ILayoutArea layoutArea, final IPrintable printable, final String marker)
		{
			switch (this)
			{
				case D:
				{
					return layoutArea.replaceMarker(SalespointClosedMessageArea.dateFormatter.format(GregorianCalendar.getInstance(Locale.getDefault()).getTime()), marker,
							true);
				}
				case T:
				{
					return layoutArea.replaceMarker(SalespointClosedMessageArea.timeFormatter.format(GregorianCalendar.getInstance(Locale.getDefault()).getTime()), marker,
							true);
				}
				default:
				{
					throw new RuntimeException("Invalid Key");
				}
			}
		}
	}
}
