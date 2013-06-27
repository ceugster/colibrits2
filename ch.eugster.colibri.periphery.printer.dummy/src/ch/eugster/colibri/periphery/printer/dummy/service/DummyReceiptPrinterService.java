package ch.eugster.colibri.periphery.printer.dummy.service;

import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.periphery.converters.Converter;
import ch.eugster.colibri.periphery.printer.service.AbstractReceiptPrinterService;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;

public class DummyReceiptPrinterService extends AbstractReceiptPrinterService 
{
	protected void activate(ComponentContext context)
	{
		super.activate(context);
	}
	
	protected void deactivate(ComponentContext context)
	{
		super.deactivate(context);
	}

	@Override
	public void print(String text) 
	{
		final String printable = text;
		int cols = this.getReceiptPrinterSettings().getCols();
		StringBuilder lines = new StringBuilder();
		for (int i = 0; i < printable.length(); i += 42)
		{
			lines = lines.append(printable.substring(i, i + Math.min(cols, printable.length() - i)) + "\n");
		}
		System.out.println(lines.toString());
	}

	@Override
	public void print(String text, Salespoint salespoint) 
	{
		SalespointReceiptPrinterSettings settings = salespoint == null ? null : salespoint.getReceiptPrinterSettings();
		Converter converter = new Converter(settings == null ? this.getReceiptPrinterSettings().getConverter() : settings.getConverter());
		final String printable = converter.convert(text);
		System.out.println(printable);
	}

	@Override
	public void print(String[] text) 
	{
		for (final String line : text)
		{
			final String printable = this.getConverter().convert(line);
			System.out.println(printable);
		}
	}

	@Override
	public void openDrawer(PaymentType paymentType) 
	{
	}

	@Override
	public void testPrint(String deviceName, String conversions, String text, int feed) 
	{
	}

	@Override
	protected void doCutPaper(int linesBeforeCut) 
	{
	}
}
