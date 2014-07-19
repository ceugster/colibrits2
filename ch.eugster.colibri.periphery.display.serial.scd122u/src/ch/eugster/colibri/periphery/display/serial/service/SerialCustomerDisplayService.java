package ch.eugster.colibri.periphery.display.serial.service;

import java.util.Properties;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.periphery.constants.AsciiConstants;
import ch.eugster.colibri.periphery.converters.Converter;
import ch.eugster.colibri.periphery.display.serial.Activator;
import ch.eugster.colibri.periphery.display.service.AbstractCustomerDisplayService;

public class SerialCustomerDisplayService extends AbstractCustomerDisplayService
{
	private SerialPort serialPort;
	
//	private PrintStream display;

	@Override
	public void clearDisplay()
	{
		byte[] print = new byte[] { AsciiConstants.ESC, AsciiConstants.AT };
		try 
		{
			this.serialPort.writeBytes(print);
		}
		catch (final Exception e)
		{
			sendEvent(e);
		}
//		this.openDisplay();
//		if (this.display != null)
//		{
//			try 
//			{
//				this.display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
//				this.display.flush();
//			} 
//			catch (IOException e) 
//			{
//			}
//		}
//		this.closeDisplay();
	}

	@Override
	public void displayText(final int timerDelay, final String text)
	{
		if (timerDelay == 0L)
		{
			this.displayText(text);
		}
		else
		{
			Job job = new Job("Display") 
			{
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					SerialCustomerDisplayService.this.clearDisplay();
					SerialCustomerDisplayService.this.writeBytes(text);
					return Status.OK_STATUS;
				}
				
			};
			job.schedule(timerDelay * 1000);
		}
	}

	@Override
	public void displayText(final String text)
	{
//		this.openDisplay();
//		if (this.display != null)
//		{
//			try 
//			{
				this.clearDisplay();
				this.writeBytes(text);
//				this.display.flush();
//			} 
//			catch (IOException e) 
//			{
//			}
//		}
//		this.closeDisplay();
	}
	
	private void writeBytes(String text)
	{
		if (this.serialPort!= null)
		{
			try
			{
				this.serialPort.writeString(text);
			}
			catch (final Exception e)
			{
				sendEvent(e);
			}
		}
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
	public void displayText(final String converter, String text)
	{
		this.clearDisplay();
		this.writeBytes(text);
	}

	protected void activate(final ComponentContext context)
	{
		super.activate(context);
		String portname = this.getPort();
		if (portname != null)
		{
			String port = portname.endsWith(":") ? portname.substring(0, portname.length() - 1) : portname;
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
	        }
		}
	}

	protected void deactivate(final ComponentContext context)
	{
		if (this.serialPort != null)
		{
			this.clearDisplay();
			try 
			{
	            serialPort.closePort();//Close serial port
			}
			catch (final Exception e)
			{
				sendEvent(e);
			}
		}
		super.deactivate(context);
	}

	private int convert(String number)
	{
		try
		{
			return Integer.valueOf(number).intValue();
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}

	@Override
	public IStatus testDisplay(Properties properties, String text) 
	{
		SerialPort mySerialPort = null;
		String port = null;
		try
		{
			String portname = properties.getProperty("port");
			String converter = properties.getProperty("converter");
			int rows = convert(properties.getProperty("rows"));
			int cols = convert(properties.getProperty("cols"));
			byte[] print = this.correctText(new Converter(converter), text, rows * cols);
			port = portname.endsWith(":") ? portname.substring(0, portname.length() - 1) : portname;
	        mySerialPort = new SerialPort(port);
	        try {
	            mySerialPort.openPort();//Open serial port
	            mySerialPort.setParams(SerialPort.BAUDRATE_9600, 
	                                 SerialPort.DATABITS_8,
	                                 SerialPort.STOPBITS_1,
	                                 SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
	        }
	        catch (SerialPortException ex) 
	        {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Fehler beim Testen des Ports " + port + ": " + ex.getLocalizedMessage());
	        }
			mySerialPort.writeBytes(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
			mySerialPort.writeBytes(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
			mySerialPort.writeBytes(new byte[] { AsciiConstants.ESC, AsciiConstants.S });
			mySerialPort.writeBytes(new byte[] { AsciiConstants.ESC, AsciiConstants.R, 2 });
			mySerialPort.writeBytes(print);
			mySerialPort.writeBytes("\n\n\n".getBytes());
		}
		catch (final Exception e)
		{
			String msg = e.getLocalizedMessage();
			if (msg == null)
			{
				msg = "Es ist ein unbekannter Fehler an Port " + port + " aufgetreten.";
			}
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e);
		}
		finally
		{
			try
			{
				if (mySerialPort != null) mySerialPort.closePort();
			}
			catch (Exception e)
			{
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e);
			}
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "OK");
	}

}
