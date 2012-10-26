package ch.eugster.colibri.periphery.printer.serial.service;

import java.io.PrintStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.periphery.converters.Converter;
import ch.eugster.colibri.periphery.printer.serial.Activator;
import ch.eugster.colibri.periphery.printer.service.AbstractReceiptPrinterService;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;

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
			this.printer.print(new char[] { 27, 116, 0 });
			this.printer.print(new char[] { 27, 82, 2 });
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
			final String printable = this.getConverter().convert(text);
			printer.print(printable + "\n");
			this.cutPaper(this.getLinesBeforeCut());
			closePrinter();
		}
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
			for (final String line : text)
			{
				final String printable = this.getConverter().convert(line);
				printer.print(printable + "\n");
			}
			this.cutPaper(this.getLinesBeforeCut());
			this.closePrinter();
		}
	}

	@Override
	public void openDrawer(PaymentType paymentType) 
	{
		if (printer == null)
		{
			this.openPrinter(this.getPort());
		}
		if (printer != null)
		{
			if (paymentType.getId().equals(salespoint.getPaymentType().getId()))
			{
				this.printer.print(new char[] { 16, 20, 1, 0, 4});
			}
			else
			{
				this.printer.print(new char[] { 16, 20, 1, 1, 4});
			}
			this.closePrinter();
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
			printer.print(printable + "\n");
			this.cutPaper(feed);
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
