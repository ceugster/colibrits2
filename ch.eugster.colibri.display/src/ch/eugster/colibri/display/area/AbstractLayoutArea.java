package ch.eugster.colibri.display.area;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import ch.eugster.colibri.persistence.model.print.IPrintable;

public abstract class AbstractLayoutArea implements ILayoutArea
{
	private static DateFormat dateFormatter = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);

	private static DateFormat timeFormatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);

	private ILayoutAreaType layoutAreaType;

	private String pattern;

	private int timerDelay;

	public AbstractLayoutArea(final ILayoutAreaType layoutAreaType)
	{
		this.layoutAreaType = layoutAreaType;
	}

	public String getHelp()
	{
		StringBuilder builder = new StringBuilder("Verwendbare Variablen:\n");
		final IKey[] keys = this.getKeys();
		for (final IKey key : keys)
		{
			builder = builder.append(key.toString() + " - " + key.label() + "\n");
		}
		return builder.toString();
	}

	public ILayoutAreaType getLayoutAreaType()
	{
		return this.layoutAreaType;
	}

	public String getPattern()
	{
		return this.pattern == null ? this.getDefaultPattern() : this.pattern;
	}

	public int getTimerDelay()
	{
		return this.timerDelay;
	}

	@Override
	public Collection<String> prepareDisplay()
	{
		final Collection<String> result = new ArrayList<String>();
		if (this.pattern != null)
		{
			final String[] lines = this.pattern.split("\n");
			final int min = Math.min(lines.length, this.getLayoutAreaType().getRowCount());
			for (int i = 0; i < min; i++)
			{
				lines[i] = lines[i].replace("\r", "");
				System.out.println("Vorher:  '" + lines[i] + "'");
				if (lines[i].length() > this.getLayoutAreaType().getColumnCount())
				{
					lines[i] = lines[i].substring(0, this.getLayoutAreaType().getColumnCount());
				}
				else if (lines[i].length() < this.getLayoutAreaType().getColumnCount())
				{
					lines[i] = AbstractLayoutType.padRight(lines[i], this.layoutAreaType.getColumnCount());
				}
				System.out.println("Nachher: '" + lines[i] + "'");
				result.add(lines[i]);
			}
		}
		return result;
	}

	@Override
	public Collection<String> prepareDisplay(final IPrintable printable)
	{
		final Collection<String> sections = new ArrayList<String>();
		sections.addAll(this.getItems(printable));
		return sections;
	}

	public String replaceMarker(final Date source, final String marker)
	{
		if ((marker == null) || marker.isEmpty())
		{
			return "";
		}
		if (source == null)
		{
			return AbstractLayoutType.padRight("", marker.length());
		}
		final String result = (marker.charAt(0) == 'D') ? this.formatDate(source) : (marker.charAt(0) == 'T' ? this.formatTime(source) : "");
		return this.replaceMarker(result, marker, true);
	}

	public String replaceMarker(final Long source, final String marker)
	{
		if ((marker == null) || marker.isEmpty())
		{
			return "";
		}
		if (source == null)
		{
			return AbstractLayoutType.padRight("", marker.length());
		}
		final String result = source.toString();
		return this.replaceMarker(result, marker, true);
	}

	public String replaceMarker(final String source, final String target, final boolean padRight)
	{
		/*
		 * target: string to replace source: string to replace with
		 */
		if ((target == null) || target.isEmpty())
		{
			return "";
		}
		if (((source == null) || source.isEmpty()))
		{
			return AbstractLayoutType.padRight("", target.length());
		}
		if (source.length() > target.length())
		{
			return source.substring(0, target.length());
		}
		if (source.length() < target.length())
		{
			return padRight ? AbstractLayoutType.padRight(source, target.length()) : AbstractLayoutType.padLeft(source, target.length());
		}
		return source;
	}

	public void setPattern(final String pattern)
	{
		this.pattern = pattern;
	}

	public void setTimerDelay(final int timerDelay)
	{
		this.timerDelay = timerDelay;
	}

	protected String[] buildMarkers(final String pattern)
	{
		final Collection<String> markers = new ArrayList<String>();
		final String[] lines = pattern.split("\n");
		for (final String line : lines)
		{
			final String[] items = line.split(" ");
			for (int i = 0; i < items.length; i++)
			{
				if (!items[i].isEmpty() && this.isKey(items[i].charAt(0)))
				{
					final String marker = this.getMarker(items[i].charAt(0), items[i].length());
					if (items[i].equals(marker))
					{
						markers.add(marker);
					}
				}
			}
		}
		return markers.toArray(new String[0]);
	}

	protected abstract Collection<String> getItems(final IPrintable printable);

	protected abstract IKey[] getKeys();

	protected String getMarker(final char c, final int length)
	{
		final char[] array = new char[length];
		Arrays.fill(array, c);
		return String.valueOf(array);
	}

	protected String[] getMarkers(final String pattern)
	{
		return this.buildMarkers(pattern);
	}

	protected String getPattern(final IPrintable printable)
	{
		return this.pattern;
	}

	protected boolean isKey(final char c)
	{
		final IKey[] keys = this.getKeys();
		for (final IKey key : keys)
		{
			if (key.toString().charAt(0) == c)
			{
				return true;
			}
		}
		return false;
	}

	protected String replace(final IPrintable printable, final String marker, String pattern)
	{
		for (final IKey key : this.getKeys())
		{
			if (key.toString().charAt(0) == marker.charAt(0))
			{
				final String item = key.replace(this, printable, marker);
				pattern = pattern.replace(marker, item);
			}
		}
		return pattern;
	}

	private String formatDate(final Date date)
	{
		return AbstractLayoutArea.dateFormatter.format(date);
	}

	private String formatTime(final Date date)
	{
		return AbstractLayoutArea.timeFormatter.format(date);
	}

}
