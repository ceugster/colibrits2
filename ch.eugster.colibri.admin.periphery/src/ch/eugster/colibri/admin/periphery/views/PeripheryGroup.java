package ch.eugster.colibri.admin.periphery.views;

public enum PeripheryGroup
{
	RECEIPT_PRINTER, CUSTOMER_DISPLAY;

	public String getServiceName()
	{
		switch (this)
		{
			case RECEIPT_PRINTER:
			{
				return "ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService";
			}
			case CUSTOMER_DISPLAY:
			{
				return "ch.eugster.colibri.periphery.display.service.CustomerDisplayService";
			}
			default:
			{
				throw new RuntimeException("Invalid Periphery Group");
			}
		}
	}

	public String image()
	{
		switch (this)
		{
			case RECEIPT_PRINTER:
			{
				return "image.printer";
			}
			case CUSTOMER_DISPLAY:
			{
				return "image.display";
			}
			default:
			{
				throw new RuntimeException("Invalid Periphery Group");
			}
		}
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case RECEIPT_PRINTER:
			{
				return "Belegdrucker";
			}
			case CUSTOMER_DISPLAY:
			{
				return "Kundendisplay";
			}
			default:
			{
				throw new RuntimeException("Invalid Periphery Group");
			}
		}
	}
}
