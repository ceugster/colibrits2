package ch.eugster.colibri.periphery.converters;

public class Converter
{
	private int[] conversionTable;

	public Converter(final String converter)
	{
		this.initializeConverter(converter);
	}

	public String convert(final String text)
	{
		final char[] target = new char[text.length()];
		for (int i = 0; i < text.length(); i++)
		{
			final int c = text.charAt(i);
			if (this.conversionTable.length < c)
			{
				target[i] = text.charAt(i);
			}
			else
			{
				if (this.conversionTable[c] == 0)
				{
					target[i] = text.charAt(i);
				}
				else
				{
					target[i] = (char) this.conversionTable[c];
				}
			}
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < target.length; i++)
		{
			char c = target[i];
			if (c == '@')
			{
				builder.append((char)27);
				builder.append((char)82);
				builder.append((char) 0);
				builder.append(c);
				builder.append((char) 27);
				builder.append((char) 82);
				builder.append((char) 2);
			}
			else
			{
				builder.append(c);
			}
		}
		return builder.toString();
//		return new String(target);
	}

	public void setConverter(final String converter)
	{
		this.initializeConverter(converter);
	}

	private void initializeConverter(String converter)
	{
		if (converter == null)
		{
			this.conversionTable = new int[0];
		}
		else if (converter.isEmpty())
		{
			this.conversionTable = new int[0];
		}
		else
		{
			converter = converter.replaceAll("\r", "");
			final String[] splitteds = converter.split("\n");

			int max = 0;
			int i = 0;

			final int[][] values = new int[splitteds.length][2];
			for (final String splitted : splitteds)
			{
				if (!splitted.startsWith("#"))
				{
					final String[] vals = splitted.split("=");
					if (vals.length == 2)
					{
						try
						{
							final int char0 = Integer.valueOf(vals[0]).intValue();
//							String[] parts = vals[1].split("[|]");
//							
							final int char1 = Integer.valueOf(vals[1]).intValue();
							if (max < char0)
							{
								max = char0;
							}
							values[i][0] = char0;
							values[i][1] = char1;
							i++;
						}
						catch (final NumberFormatException e)
						{

						}
					}
				}
			}
			this.conversionTable = new int[max + 1];
			for (int index = 0; index < values.length; index++)
			{
				this.conversionTable[values[index][0]] = values[index][1];
			}
		}
	}

}
