package ch.eugster.colibri.periphery.printer.serial.service;

import java.io.PrintStream;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.periphery.converters.Converter;
import ch.eugster.colibri.periphery.printer.serial.Activator;
import ch.eugster.colibri.periphery.printer.service.AbstractReceiptPrinterService;
import ch.eugster.colibri.periphery.printer.service.AsciiConstants;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Stock;

public class SerialReceiptPrinterService extends AbstractReceiptPrinterService 
{
	private PrintStream printer;
	
	protected void activate(ComponentContext context)
	{
		super.activate(context);
	}
	
	protected void deactivate(ComponentContext context)
	{
		super.deactivate(context);
	}

	private void closePrinter()
	{
		if (this.printer != null)
		{
			this.printer.flush();
			this.printer.close();
			this.printer = null;
		}
	}

	private void openPrinter(String deviceName)
	{
		try
		{
			this.printer = new PrintStream(deviceName);
			this.printer.print(new char[] { AsciiConstants.ESC, AsciiConstants.AT });
			this.printer.print(new char[] { AsciiConstants.ESC, AsciiConstants.S });
			this.printer.print(new char[] { AsciiConstants.ESC, AsciiConstants.R, 2 });
		}
		catch (final Exception e)
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
		if (printer == null)
		{
			this.openPrinter(this.getPort());
		}
		if (printer != null)
		{
			if (this.isPrintLogo())
			{
				this.printNVBitImage(this.getLogo(), this.getPrintLogoMode().mode());
			}
			final String printable = this.getConverter().convert(text);
			printer.print(printable + "\n");
			this.cutPaper(this.getLinesBeforeCut());
			closePrinter();
		}
	}

	private void printNVBitImage(int n, int m)
	{
		this.printer.write(AsciiConstants.FS);
		this.printer.write(AsciiConstants.p);
		this.printer.write(n);
		this.printer.write(m);
		this.printer.flush();
	}
	
	@Override
	public void print(String text, Salespoint salespoint) 
	{
		SalespointReceiptPrinterSettings settings = salespoint == null ? null : salespoint.getReceiptPrinterSettings();
		if (printer == null)
		{
			this.openPrinter(settings == null ? this.getReceiptPrinterSettings().getPort() : settings.getPort());
		}
		if (printer != null)
		{
			if (this.isPrintLogo())
			{
				this.printNVBitImage(this.getLogo(), this.getPrintLogoMode().mode());
			}
			Converter converter = new Converter(settings == null ? this.getReceiptPrinterSettings().getConverter() : settings.getConverter());
			final String printable = converter.convert(text);
			printer.print(printable + "\n");
			this.cutPaper(settings == null ? this.getReceiptPrinterSettings().getLinesBeforeCut() : settings.getLinesBeforeCut());
			closePrinter();
		}
	}

	@Override
	public void print(String[] text) 
	{
		if (printer == null)
		{
			this.openPrinter(this.getPort());
		}
		if (printer != null)
		{
			if (this.isPrintLogo())
			{
				this.printNVBitImage(this.getLogo(), this.getPrintLogoMode().mode());
			}
			for (String line : text)
			{
				final String printable = this.getConverter().convert(line);
				printer.print(printable + "\n");
			}
			this.cutPaper(this.getLinesBeforeCut());
			this.closePrinter();
		}
	}

	@Override
	public void openDrawer(Currency currency) 
	{
		if (currency == null)
		{
			return;
		}
		if (printer == null)
		{
			this.openPrinter(this.getPort());
		}
		if (printer != null)
		{
			Collection<Stock> stocks = salespoint.getStocks();
			for (Stock stock : stocks)
			{
				if (stock.getPaymentType().getCurrency().getId().equals(currency.getId()))
				{
					if (salespoint.getPaymentType().getCurrency().getId().equals(stock.getPaymentType().getCurrency().getId()))
					{
						this.printer.print(new char[] { 16, 20, 1, 0, 4});
					}
					else
					{
						this.printer.print(new char[] { 16, 20, 1, 1, 4});
					}
				}
			}
			this.closePrinter();
		}
	}

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
	public void testPrint(String deviceName, String conversions, String text, int feed) 
	{
		final Converter converter = new Converter(conversions);
		final String printable = converter.convert(text);
		try 
		{
			if (printer == null)
			{
				this.openPrinter(deviceName);
			}
			if (printer != null)
			{
				printer.print(printable + "\n");
				this.cutPaper(feed);
			}
		} 
		finally
		{
			this.closePrinter();
		}
	}

	@Override
	protected void doCutPaper(int linesBeforeCut) 
	{
		for (int i = 0; i < linesBeforeCut; i++)
		{
			this.printer.println();
		}
		String cut = new String(new char[] { 29, 86, 0 });
		this.printer.println(cut);
	}
}
