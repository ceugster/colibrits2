package ch.eugster.colibri.periphery.converters;

import java.util.ArrayList;
import java.util.List;

public class Converter
{
	private int[] conversionTable;

	public Converter(final String converter)
	{
		this.initializeConverter(converter);
	}

	public byte[] convert(final byte[] text)
	{
		final List<byte[]> list = new ArrayList<byte[]>();
		for (int i = 0; i < text.length; i++)
		{
			int c = text[i];
			if (c < 0)
			{
				c = c + 256;
			}
			if (this.conversionTable.length - 1 < c)
			{
				list.add(new byte[] { text[i] });
			}
			else
			{
				if (this.conversionTable[c] == 0)
				{
					list.add(new byte[] { text[i] });
				}
				else
				{
					list.add(new byte[] { (byte) this.conversionTable[c] });
				}
			}
		}
		int size = list.size();
		for (byte[] c : list)
		{
			if (c[0] == 64)
			{
				c = new byte[7];
				c[0] = (byte)27;
				c[1] = (byte)82;
				c[2] = (byte)0;
				c[3] = (byte)64;
				c[4] = (byte)27;
				c[5] = (byte)82;
				c[6] = (byte)2;
				size += 6;
			}
		}
		byte[] chars = new byte[size];
		int i = 0;
		for (byte[] item : list)
		{
			int length = item.length;
			for (int j = 0; j < length; j++)
			{
				chars[i + j] = item[j];
			}
			i += length;
		}
		return chars;
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
