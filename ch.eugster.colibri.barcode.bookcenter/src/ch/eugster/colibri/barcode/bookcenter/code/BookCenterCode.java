package ch.eugster.colibri.barcode.bookcenter.code;

import ch.eugster.colibri.barcode.bookcenter.Activator;
import ch.eugster.colibri.barcode.code.AbstractBarcode;
import ch.eugster.colibri.barcode.code.Barcode;

public class BookCenterCode extends AbstractBarcode
{
	public static final int MIN_BOOKCENTER_CODE_LENGTH = 7;

	public static final int MAX_BOOKCENTER_CODE_LENGTH = 8;

	protected BookCenterCode(final String code)
	{
		super(code);
	}

	public String getDescription()
	{
		return Activator.getDescription();
	}

	public String getName()
	{
		return Activator.getName();
	}

	public static Barcode verify(final String code)
	{
		if (code.length() < BookCenterCode.MIN_BOOKCENTER_CODE_LENGTH || code.length() > MAX_BOOKCENTER_CODE_LENGTH)
		{
			return null;
		}

		try
		{
			Long.parseLong(code);
		}
		catch (final NumberFormatException e)
		{
			return null;
		}
		return new BookCenterCode(code);
	}
}
