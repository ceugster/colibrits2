package ch.eugster.colibri.periphery.printer.usb.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.POSPrinterControl113;
import jpos.util.JposPropertiesConst;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.periphery.converters.Converter;
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
		copyJposConfigFile();
		this.printer = (POSPrinterControl113) new POSPrinter();
		this.openPrinter("POSPrinter");
	}

	protected void deactivate(ComponentContext context) 
	{
		this.closePrinter();
		this.printer = null;
		super.deactivate(context);
	}

	private void copyJposConfigFile()
	{
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final File root = workspace.getRoot().getRawLocation().toFile();
		File jpos = new File(root.getAbsolutePath() + "/configuration/jpos.xml");
		if (!jpos.isFile())
		{
			jpos.getParentFile().mkdirs();
			BufferedReader reader = null;
			BufferedWriter writer = null;
			try
			{
				URL url = Activator.getContext().getBundle().getResource("/jpos.xml");
				reader = new BufferedReader(new InputStreamReader(url.openStream()));
				writer = new BufferedWriter(new FileWriter(jpos));
				String line = reader.readLine();
				while (line != null)
				{
					line = reader.readLine();
					writer.write(line);
					writer.newLine();
				}
			}
			catch (Exception e)
			{
				
			}
			finally
			{
				if (reader != null)
				{
					try 
					{
						reader.close();
					} 
					catch (IOException e) 
					{
					}
				}
				if (writer != null)
				{
					try 
					{
						writer.flush();
						writer.close();
					} 
					catch (IOException e) 
					{
					}
				}
			}
		}
		System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, jpos.getAbsolutePath());
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
		}
	}

	private boolean openPrinter(String deviceName) 
	{
		try 
		{
			printer.open(deviceName);
			printer.claim(1000);
			printer.setDeviceEnabled(true);
			return true;
		} 
		catch (Throwable e) 
		{
		}
		return false;
	}

	@Override
	public void print(String text) {
		try {
			if (this.printer != null) {
				boolean deviceEnabled = printer.getDeviceEnabled();
				if (!deviceEnabled) {
					printer.setDeviceEnabled(true);
				}
				// if (this.isPrintLogo())
				// {
				// this.printNVBitImage(this.getLogo(),
				// this.getPrintLogoMode().mode());
				// }
				this.printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, text);
				this.cutPaper(this.getLinesBeforeCut());
				if (!deviceEnabled) {
					this.printer.setDeviceEnabled(deviceEnabled);
				}
			}
		} catch (Exception e) {
			if (this.getEventAdmin() != null) {
				this.getEventAdmin()
						.sendEvent(
								this.getEvent(new Status(IStatus.CANCEL,
										Activator.PLUGIN_ID,
										"Der Belegdrucker kann nicht angesprochen werden.")));
			}
		}
	}

	public char[] getFontSize(ReceiptPrinterService.Size size) {
		switch (size) {
		case NORMAL: {
			return new char[] { 29, 33, 0 };
		}
		case DOUBLE_WIDTH: {
			return new char[] { 29, 33, 16 };
		}
		case DOUBLE_HEIGHT: {
			return new char[] { 29, 33, 1 };
		}
		case DOUBLE_WIDTH_AND_HEIGHT: {
			return new char[] { 29, 33, 17 };
		}
		default: {
			return new char[] { 29, 33, 0 };
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
	public void testPrint(String deviceName, String conversions, String text, int feed) throws Exception
	{
		if (deviceName == null || deviceName.isEmpty())
		{
			throw new NullPointerException("Keinen Port übergeben.");
		}

		byte[] bytes = conversions == null || conversions.isEmpty() ? text.getBytes() : new Converter(conversions).convert(text.getBytes());
		this.print(new String(bytes));
		this.doCutPaper(5);
	}

	@Override
	protected void doCutPaper(int linesBeforeCut) {
		try {
			for (int i = 0; i < linesBeforeCut; i++) {
				this.printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "\n");
			}
			this.printer.cutPaper(POSPrinterConst.PTR_CP_FULLCUT);
		} catch (Exception e) {
			if (this.getEventAdmin() != null) {
				this.getEventAdmin()
						.sendEvent(
								this.getEvent(new Status(IStatus.CANCEL,
										Activator.PLUGIN_ID,
										"Der Belegdrucker kann nicht angesprochen werden.")));
			}
		}
	}
}
