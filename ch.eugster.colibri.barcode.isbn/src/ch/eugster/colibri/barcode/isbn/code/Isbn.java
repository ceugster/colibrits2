/*
 * Created on 23.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.colibri.barcode.isbn.code;

import ch.eugster.colibri.barcode.code.AbstractBarcode;
import ch.eugster.colibri.barcode.ean13.code.Ean13;
import ch.eugster.colibri.barcode.isbn.Activator;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Isbn extends AbstractBarcode
{
	public static final int ISBN_LENGTH = 10;

	protected Isbn(final String code)
	{
		super(code);
	}

	public Ean13 convertToEan13()
	{
		StringBuffer code = new StringBuffer(this.toString());
		if (code.length() == 10)
		{
			code = code.delete(code.length() - 1, code.length());
		}
		return Ean13.verify(code.insert(0, Ean13.PREFIX_ISBN).toString());
	}

	public String getDescription()
	{
		return Activator.getDescription();
	}

	public String getName()
	{
		return Activator.getName();
	}

	public static Isbn verify(String code)
	{
		code = Isbn.removeHyphens(code);

		if ((code.length() < 9) || (code.length() > 10))
		{
			return null;
		}

		if (code.length() == Isbn.ISBN_LENGTH - 1)
		{
			code = code + Isbn.computeChecksum(code);
		}

		return Isbn.validate(code);
	}

	protected static String computeChecksum(final String code)
	{
		int checksum = 0;
		for (int i = 0; i < code.length(); i++)
		{
			checksum = checksum + new Integer(code.substring(i, i + 1)).intValue() * (11 - (i + 1));
		}

		checksum = 11 - checksum % 11;
		String checksumcode = null;
		switch (checksum)
		{
			case 10:
				checksumcode = "X";
				break;
			case 11:
				checksumcode = "0";
				break;
			default:
				checksumcode = new Integer(checksum).toString();
				break;
		}
		return checksumcode;
	}

	private static String removeHyphens(String code)
	{
		code = code.trim().toUpperCase();
		final StringBuilder newCode = new StringBuilder();
		for (int i = 0; i < code.length(); i++)
		{
			if (i == code.length() - 1)
			{
				if ("0123456789X".contains(code.substring(i, i + 1)))
				{
					newCode.append(code.substring(i, i));
				}
			}
			else
			{
				if ("0123456789".contains(code.substring(i, i + 1)))
				{
					newCode.append(code.substring(i, i));
				}
			}
		}
		return newCode.toString();
	}

	private static Isbn validate(final String code)
	{
		if (Isbn.computeChecksum(code.substring(0, code.length() - 1)).equals(code.substring(code.length() - 1)))
		{
			return new Isbn(code);
		}

		return null;
	}
}
