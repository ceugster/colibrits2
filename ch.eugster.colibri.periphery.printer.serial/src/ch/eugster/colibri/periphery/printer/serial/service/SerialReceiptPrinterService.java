package ch.eugster.colibri.periphery.printer.serial.service;

import java.util.Collection;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

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
import ch.eugster.colibri.persistence.model.Stock;

public class SerialReceiptPrinterService extends AbstractReceiptPrinterService 
{
	private SerialPort printer;

	private SerialPort openPort(String portname)
	{
		SerialPort serialPort = null;
		if (portname != null)
		{
			portname = portname.endsWith(":") ? portname.substring(0, portname.length() - 1) : portname;
			String[] ports = SerialPortList.getPortNames();
			for (String port : ports)
			{
				if (port.equalsIgnoreCase(portname)) 
				{
			        serialPort = new SerialPort(port);
			        try {
			            serialPort.openPort();//Open serial port
			            serialPort.setParams(SerialPort.BAUDRATE_9600, 
			                                 SerialPort.DATABITS_8,
			                                 SerialPort.STOPBITS_1,
			                                 SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
			        }
			        catch (SerialPortException ex) 
			        {
			        	serialPort = null;
			        	sendEvent(ex);
			        }
			        break;
				}
			}
		}
		return serialPort;
	}

	protected void activate(final ComponentContext context)
	{
		super.activate(context);
		this.printer = openPort(this.getPort());
	}

	private void closePort(SerialPort printer)
	{
		if (printer != null)
		{
			try 
			{
	            printer.closePort();//Close serial port
			}
			catch (final Exception e)
			{
				sendEvent(e);
			}
		}
	}
	
	protected void deactivate(final ComponentContext context)
	{
		closePort(this.printer);
		super.deactivate(context);
	}

	private void sendEvent(Exception e)
	{
		if (this.getEventAdmin() != null)
		{
			this.getEventAdmin().sendEvent(
					this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Der Belegdrucker kann nicht angesprochen werden.")));
		}
	}

	@Override
	public void print(String text) 
	{
		if (printer != null)
		{
			if (this.isPrintLogo())
			{
				this.printNVBitImage(this.getLogo(), this.getPrintLogoMode().mode());
			}
			final byte[] printable = this.getConverter().convert(text.getBytes());
			println(printable);
			this.cutPaper(this.getLinesBeforeCut());
		}
	}

	private void printNVBitImage(int n, int m)
	{
		try 
		{
			this.printer.writeBytes(new byte[] { AsciiConstants.FS, AsciiConstants.p, (byte)n, (byte)m });
		} 
		catch (SerialPortException e) 
		{
			sendEvent(e);
		}
	}
	
	@Override
	public void print(String text, Salespoint salespoint) 
	{
		if (printer != null)
		{
			if (this.isPrintLogo())
			{
				this.printNVBitImage(this.getLogo(), this.getPrintLogoMode().mode());
			}
			final byte[] printable = getConverter().convert(text.getBytes());
			println(printable);
			this.cutPaper(this.getLinesBeforeCut());
		}
	}

	private void print(byte[] bytes)
	{
		try 
		{
			printer.writeBytes(bytes);
		} 
		catch (SerialPortException e) 
		{
			sendEvent(e);
		}
	}
	
	private void println(byte[] bytes)
	{
		try 
		{
			for (int i = 0; i < bytes.length; i++)
			{
				if (bytes[i] == 64)
				{
					printer.writeBytes(new byte[] { 27, 82, 0 });
				}
				printer.writeBytes(new byte[] { bytes[i] });
				if (bytes[i] == 64)
				{
					printer.writeBytes(new byte[] { 27, 82, 2 });
				}
			}
			printer.writeBytes(new byte[] { '\n' });
		} 
		catch (SerialPortException e) 
		{
			sendEvent(e);
		}
	}
	
	private void println()
	{
		try 
		{
			printer.writeBytes(new byte[] { '\n' });
		} 
		catch (SerialPortException e) 
		{
			sendEvent(e);
		}
	}
	
	@Override
	public void print(String[] text) 
	{
		if (printer != null)
		{
			if (this.isPrintLogo())
			{
				this.printNVBitImage(this.getLogo(), this.getPrintLogoMode().mode());
			}
			for (String line : text)
			{
				final byte[] printable = this.getConverter().convert(line.getBytes());
				println(printable);
			}
			this.cutPaper(this.getLinesBeforeCut());
		}
	}

	@Override
	public void openDrawer(Currency currency) 
	{
		if (currency == null)
		{
			return;
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
						this.print(new byte[] { 16, 20, 1, 0, 4});
					}
					else
					{
						this.print(new byte[] { 16, 20, 1, 1, 4});
					}
				}
			}
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
	public void testPrint(String deviceName, String conversions, String text, int feed) throws Exception
	{
		if (deviceName == null || deviceName.isEmpty())
		{
			throw new NullPointerException("Keinen Port übergeben.");
		}
		String port = deviceName.endsWith(":") ? deviceName.substring(0, deviceName.length() - 1) : deviceName;
		String oldPort = null;

		if (this.printer == null)
		{
			this.printer = this.openPort(port);
		}
		else
		{
			if (!port.equals(this.printer.getPortName()))
			{
				oldPort = this.printer.getPortName();
				this.closePort(this.printer);
				this.printer = this.openPort(port);
			}
		}
		
		byte[] bytes = conversions == null || conversions.isEmpty() ? new Converter(conversions).convert(text.getBytes()) : text.getBytes();
		this.printer.writeBytes(bytes);
		this.doCutPaper(5);

		if (oldPort != null)
		{
			this.closePort(this.printer);
			this.printer = this.openPort(port);
		}
	}

	@Override
	protected void doCutPaper(int linesBeforeCut) 
	{
		for (int i = 0; i < linesBeforeCut; i++)
		{
			this.println();
		}
		String cut = new String(new byte[] { 29, 86, 0 });
		this.println(cut.getBytes());
	}
}
