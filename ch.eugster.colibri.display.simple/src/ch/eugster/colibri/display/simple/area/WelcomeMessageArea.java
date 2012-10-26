package ch.eugster.colibri.display.simple.area;

import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.display.area.AbstractLayoutArea;
import ch.eugster.colibri.display.area.IKey;
import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.display.area.ILayoutAreaType;
import ch.eugster.colibri.persistence.model.print.IPrintable;

public class WelcomeMessageArea extends AbstractLayoutArea implements ILayoutArea
{

	public WelcomeMessageArea(final ILayoutAreaType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPattern()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("Willkommen!");
		return builder.toString();
	}

	@Override
	public int getDefaultTimerDelay()
	{
		return 5;
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
		;

		@Override
		public String label()
		{
			return "";
		}

		@Override
		public String replace(final ILayoutArea layoutArea, final IPrintable printable, final String marker)
		{
			return "";
		}
	}

}
