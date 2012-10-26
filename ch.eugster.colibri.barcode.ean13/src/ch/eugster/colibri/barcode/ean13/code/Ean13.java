package ch.eugster.colibri.barcode.ean13.code;

/*
 * Created on 08.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

import java.math.BigInteger;

import ch.eugster.colibri.barcode.code.AbstractBarcode;
import ch.eugster.colibri.barcode.ean13.Activator;
import ch.eugster.colibri.persistence.model.Position;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Ean13 extends AbstractBarcode
{
	public static final int EAN13_LENGTH = 13;

	public static final String PREFIX_ISBN = "978";

	public static final String PREFIX_ORDER = "989";

	public static final String PREFIX_CUSTOMER = "992";

	/**
	 * 
	 */
	protected Ean13(final String ean13)
	{
		super(ean13);

	}

	public String getDescription()
	{
		return "EAN13 Barcode (" + this.getSubType().toString() + ")";
	}

	@Override
	public String getDetail()
	{
		String detail = null;
		switch (this.getSubType())
		{
			case ORDER:
			{
				detail = this.getCode().replaceFirst(Ean13.PREFIX_ORDER, "");
				break;
			}
			case CUSTOMER:
			{
				detail = this.getCode().replaceFirst(Ean13.PREFIX_CUSTOMER, "");
				break;
			}
			case ISBN:
			{
				detail = this.getCode().replaceFirst(Ean13.PREFIX_ISBN, "");
				break;
			}
		}
		if (detail != null)
		{
			try
			{
				detail = Long.valueOf(detail.substring(0, detail.length() - 1)).toString();
			}
			catch (final NumberFormatException e)
			{

			}
		}
		return detail;
	}

	public String getName()
	{
		return Activator.getName();
	}

	public Ean13.SubType getSubType()
	{
		if (this.getCode().substring(0, 3).equals(Ean13.PREFIX_ISBN))
		{
			return Ean13.SubType.ISBN;
		}
		else if (this.getCode().substring(0, 3).equals(Ean13.PREFIX_CUSTOMER))
		{
			return Ean13.SubType.CUSTOMER;
		}
		else if (this.getCode().substring(0, 3).equals(Ean13.PREFIX_ORDER))
		{
			return Ean13.SubType.ORDER;
		}
		else
		{
			return Ean13.SubType.UNKNOWN;
		}
	}

	@Override
	public Type getType()
	{
		switch (this.getSubType())
		{
			case ISBN:
			{
				return Type.ARTICLE;
			}
			case ORDER:
			{
				return Type.ORDER;
			}
			case CUSTOMER:
			{
				return Type.CUSTOMER;
			}
			case UNKNOWN:
			{
				return Type.ARTICLE;
			}
			default:
			{
				return Type.ARTICLE;
			}
		}
	}

	@Override
	public void updatePosition(final Position position)
	{
		super.updatePosition(position);
		switch (this.getSubType())
		{
			case ORDER:
			{
				position.setOrdered(true);
				position.setOrder(this.getDetail());
			}
			case CUSTOMER:
			{
				position.getReceipt().setCustomerCode(this.getDetail());
			}
		}

	}

	/**
	 * 
	 * @param code
	 *            The code as as string that has to be verified to be a ean13
	 *            code
	 * @return @see Ean13 object or <code>null</code> if the parameter is not a
	 *         ean13 code
	 */
	public static Ean13 verify(String code)
	{
		if (code == null)
		{
			return null;
		}

		if ((code.length() < Ean13.EAN13_LENGTH - 1) || (code.length() > Ean13.EAN13_LENGTH))
		{
			return null;
		}

		try
		{
			new BigInteger(code);
		}
		catch (final NumberFormatException e)
		{
			return null;
		}

		if (code.length() == Ean13.EAN13_LENGTH - 1)
		{
			code = code + Integer.valueOf(Ean13.computeChecksum(code));
		}

		return Ean13.validate(code);
	}

	private static int computeChecksum(final String code)
	{
		int sumOdd = 0;
		for (int i = 1; i < code.length(); i = i + 2)
		{
			sumOdd += Integer.parseInt(code.substring(i, i + 1)) * 3;
		}

		int sumEven = 0;
		for (int i = 0; i < code.length(); i = i + 2)
		{
			sumEven += Integer.parseInt(code.substring(i, i + 1));
		}

		int mod = (sumOdd + sumEven) % 10;
		if (mod == 0)
		{
			mod = 10;
		}
		return (10 - mod);
	}

	private static Ean13 validate(final String code)
	{
		if (Ean13.computeChecksum(code.substring(0, code.length() - 1)) == Integer.parseInt(code.substring(code.length() - 1)))
		{
			return new Ean13(code);
		}

		return null;
	}

	public enum SubType
	{
		UNKNOWN, ISBN, CUSTOMER, ORDER;

		@Override
		public String toString()
		{
			switch (this)
			{
				case UNKNOWN:
				{
					return "unbekannter Typ";
				}
				case ISBN:
				{
					return "13 stellige ISBN";
				}
				case CUSTOMER:
				{
					return "Interne Verwendung: enth�lt Kundennummer";
				}
				case ORDER:
				{
					return "Interne Verwendung: enth�lt Bestellnummer";
				}
				default:
				{
					return null;
				}
			}
		}
	}
}