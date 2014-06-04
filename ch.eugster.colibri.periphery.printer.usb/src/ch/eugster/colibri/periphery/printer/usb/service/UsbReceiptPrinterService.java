package ch.eugster.colibri.periphery.printer.usb.service;

import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.POSPrinterControl113;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.periphery.printer.service.AbstractReceiptPrinterService;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.periphery.printer.usb.Activator;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Salespoint;

public class UsbReceiptPrinterService extends AbstractReceiptPrinterService 
{
	private POSPrinterControl113 printer;
	
	protected void activate(ComponentContext context)
	{
		super.activate(context);
		this.printer = (POSPrinterControl113) new POSPrinter();
		this.openPrinter("POSPrinter");
	}
	
	protected void deactivate(ComponentContext context)
	{
		this.closePrinter();
		this.printer = null;
		super.deactivate(context);
	}

	private void closePrinter()
	{
		try
		{
			if (this.printer.getDeviceEnabled())
			{
				this.printer.setDeviceEnabled(false);
				this.printer.close();
			}
		}
		catch (Exception e)
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Der Belegdrucker kann nicht angesprochen werden.")));
			}
		}
	}

	private void openPrinter(String deviceName)
	{
		try
		{
			printer.open(deviceName);
			printer.claim(1000);
			printer.setDeviceEnabled(true);
		}
		catch (Throwable e)
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Der Belegdrucker kann nicht angesprochen werden.")));
			}
		}
	}

	@Override
	public void print(String text) 
	{
		try
		{
			if (printer != null)
			{
				boolean deviceEnabled = printer.getDeviceEnabled();
				if (!deviceEnabled)
				{
					printer.setDeviceEnabled(true);
				}
//				if (this.isPrintLogo())
//				{
//					this.printNVBitImage(this.getLogo(), this.getPrintLogoMode().mode());
//				}
				this.printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, text);
				this.cutPaper(this.getLinesBeforeCut());
				if (!deviceEnabled)
				{
					this.printer.setDeviceEnabled(deviceEnabled);
				}
			}
		}
		catch (Exception e)
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Der Belegdrucker kann nicht angesprochen werden.")));
			}
		}
	}

//	private void printNVBitImage(int n, int m)
//	{
//		this.printer.write(AsciiConstants.FS);
//		this.printer.write(AsciiConstants.p);
//		this.printer.write(n);
//		this.printer.write(m);
//		this.printer.flush();
//	}
	
//	@Override
//	public void print(String text, Salespoint salespoint) 
//	{
//		SalespointReceiptPrinterSettings settings = salespoint == null ? null : salespoint.getReceiptPrinterSettings();
//		if (printer == null)
//		{
//			this.openPrinter(settings == null ? this.getReceiptPrinterSettings().getPort() : settings.getPort());
//		}
//		if (printer != null)
//		{
//			if (this.isPrintLogo())
//			{
//				this.printNVBitImage(this.getLogo(), this.getPrintLogoMode().mode());
//			}
//			Converter converter = new Converter(settings == null ? this.getReceiptPrinterSettings().getConverter() : settings.getConverter());
//			final String printable = converter.convert(text);
//			println(printable.getBytes());
//			this.cutPaper(settings == null ? this.getReceiptPrinterSettings().getLinesBeforeCut() : settings.getLinesBeforeCut());
//			closePrinter();
//		}
//	}

//	private void print(byte[] bytes)
//	{
//		try 
//		{
//			printer.write(bytes);
//		} 
//		catch (IOException e) 
//		{
//		}
//	}
	
//	private void println(byte[] bytes)
//	{
//		try 
//		{
//			printer.write(bytes);
//			printer.write(new byte[] { '\n' });
//		} 
//		catch (IOException e) 
//		{
//		}
//	}
	
//	private void println()
//	{
//		try 
//		{
//			printer.write(new byte[] { '\n' });
//		} 
//		catch (IOException e) 
//		{
//		}
//	}
	
//	@Override
//	public void print(String[] text) 
//	{
//		if (printer == null)
//		{
//			this.openPrinter(this.getPort());
//		}
//		if (printer != null)
//		{
//			if (this.isPrintLogo())
//			{
//				this.printNVBitImage(this.getLogo(), this.getPrintLogoMode().mode());
//			}
//			for (String line : text)
//			{
//				final String printable = this.getConverter().convert(line);
//				println(printable.getBytes());
//			}
//			this.cutPaper(this.getLinesBeforeCut());
//			this.closePrinter();
//		}
//	}

//	@Override
//	public void openDrawer(Currency currency) 
//	{
//		if (currency == null)
//		{
//			return;
//		}
//		if (printer == null)
//		{
//			this.openPrinter(this.getPort());
//		}
//		if (printer != null)
//		{
//			Collection<Stock> stocks = salespoint.getStocks();
//			for (Stock stock : stocks)
//			{
//				if (stock.getPaymentType().getCurrency().getId().equals(currency.getId()))
//				{
//					if (salespoint.getPaymentType().getCurrency().getId().equals(stock.getPaymentType().getCurrency().getId()))
//					{
//						this.print(new byte[] { 16, 20, 1, 0, 4});
//					}
//					else
//					{
//						this.print(new byte[] { 16, 20, 1, 1, 4});
//					}
//				}
//			}
//			this.closePrinter();
//		}
//	}

	public char[] getFontSize(ReceiptPrinterService.Size size) 
	{
		switch(size)
		{
		case NORMAL:
		{
			return new char[] { 29, 33, 0};
		}
		case DOUBLE_WIDTH:
		{
			return new char[] { 29, 33, 16};
		}
		case DOUBLE_HEIGHT:
		{
			return new char[] { 29, 33, 1};
		}
		case DOUBLE_WIDTH_AND_HEIGHT:
		{
			return new char[] { 29, 33, 17};
		}
		default:
		{
			return new char[] { 29, 33, 0};
		}
		}
	}

@Override
public void openDrawer(Currency currency) {
	// TODO Auto-generated method stub
	
}

@Override
public void print(String text, Salespoint salespoint) {
	// TODO Auto-generated method stub
	
}

@Override
public void print(String[] text) {
	// TODO Auto-generated method stub
	
}

@Override
public void testPrint(String deviceName, String conversions, String text,
		int feed) 
{
	try
	{
		this.printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, text);
		for (int i = 0; i < feed; i++)
		{
			this.printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "\n");
		}
	}
	catch(Exception e)
	{
		if (this.getEventAdmin() != null)
		{
			this.getEventAdmin().sendEvent(
					this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Der Belegdrucker kann nicht angesprochen werden.")));
		}
	}
}

@Override
protected void doCutPaper(int linesBeforeCut) 
{
	try
	{
		for (int i = 0; i < linesBeforeCut; i++)
		{
			this.printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "\n");
		}
		this.printer.cutPaper(POSPrinterConst.PTR_CP_FULLCUT);
	}
	catch(Exception e)
	{
		if (this.getEventAdmin() != null)
		{
			this.getEventAdmin().sendEvent(
					this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Der Belegdrucker kann nicht angesprochen werden.")));
		}
	}
}

//	@Override
//	public void testPrint(String deviceName, String conversions, String text, int feed) 
//	{
//		final Converter converter = new Converter(conversions);
//		final String printable = converter.convert(text);
//		try 
//		{
//			if (printer == null)
//			{
//				this.openPrinter(deviceName);
//			}
//			if (printer != null)
//			{
//				println(printable.getBytes());
//				this.cutPaper(feed);
//			}
//		} 
//		finally
//		{
//			this.closePrinter();
//		}
//	}

//	@Override
//	protected void doCutPaper(int linesBeforeCut) 
//	{
//		for (int i = 0; i < linesBeforeCut; i++)
//		{
//			this.println();
//		}
//		String cut = new String(new byte[] { 29, 86, 0 });
//		this.println(cut.getBytes());
//	}
}
