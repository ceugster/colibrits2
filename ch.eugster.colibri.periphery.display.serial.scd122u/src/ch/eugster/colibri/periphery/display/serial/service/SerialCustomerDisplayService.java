package ch.eugster.colibri.periphery.display.serial.service;

import j.extensions.comm.SerialComm;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

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
	private SerialComm serialComm;
	
	private PrintStream display;

	@Override
	public void clearDisplay()
	{
		this.openDisplay();
		if (this.display != null)
		{
			try 
			{
				this.display.write(new byte[] { 0x0c });
			} 
			catch (IOException e) 
			{
			}
		}
		this.closeDisplay();
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
					SerialCustomerDisplayService.this.displayText(text);
					return Status.OK_STATUS;
				}
				
			};
			job.schedule(timerDelay * 1000);
		}
	}

	@Override
	public void displayText(final String text)
	{
		this.clearDisplay();
		this.openDisplay();
		if (this.display != null)
		{
			byte[] print = this.correctText(text);
			try 
			{
				this.display.write(print);
			} 
			catch (IOException e) 
			{
			}
		}
		this.closeDisplay();
	}
	
	@Override
	public void displayText(final String converter, String text)
	{
		this.clearDisplay();
		this.openDisplay();
		if (this.display != null)
		{
			byte[] print = this.correctText(new Converter(converter), text);
			try 
			{
				this.display.write(print);
			} 
			catch (IOException e) 
			{
			}
		}
		this.closeDisplay();
	}

	private void closeDisplay()
	{
		if (this.display != null)
		{
			this.display.flush();
			this.display.close();
			this.display = null;
		}
	}

	protected void activate(final ComponentContext context)
	{
		super.activate(context);
		String portname = this.getPort();
		String port = portname.endsWith(":") ? portname.substring(0, portname.length() - 1) : portname;
		SerialComm[] serialComms = SerialComm.getCommPorts();
		{
			for (SerialComm serialComm : serialComms)
			{
				if (serialComm.getSystemPortName().equals(port)) 
				{
					this.serialComm = serialComm;
					this.serialComm.openPort();
				}
			}
		}
	}

	protected void deactivate(final ComponentContext context)
	{
		if (this.serialComm != null)
		{
			this.clearDisplay();
			this.serialComm.closePort();
		}
		super.deactivate(context);
	}

	private void openDisplay()
	{
		try
		{
			this.display = new PrintStream(serialComm.getOutputStream());
			this.display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
		}
		catch (IOException e) 
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Port " + getPort() + " wird bereits verwendet.")));
			}
		} 
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
		PrintStream myDisplay = null;
		SerialComm mySerialComm = null;
		String port = null;
		try
		{
			String portname = properties.getProperty("port");
			String converter = properties.getProperty("converter");
			int rows = convert(properties.getProperty("rows"));
			int cols = convert(properties.getProperty("cols"));
			byte[] print = this.correctText(new Converter(converter), text, rows * cols);
			port = portname.endsWith(":") ? portname.substring(0, portname.length() - 1) : portname;
			SerialComm[] serialComms = SerialComm.getCommPorts();
			for (SerialComm serialComm : serialComms)
			{
				System.out.println(serialComm.getSystemPortName());
				if (serialComm.getSystemPortName().equals(port)) 
				{
					mySerialComm = serialComm;
					break;
				}
			}
			if (mySerialComm == null)
			{
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Der Port " + portname + " ist nicht vorhanden.");
			}
			if (!mySerialComm.openPort())
			{
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Der Port " + portname + " konnte nicht geöffnet werden.");
			}
			myDisplay = new PrintStream(mySerialComm.getOutputStream());
			myDisplay.write(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
			myDisplay.write(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
			myDisplay.write(new byte[] { AsciiConstants.ESC, AsciiConstants.S });
			myDisplay.write(new byte[] { AsciiConstants.ESC, AsciiConstants.R, 2 });
			myDisplay.write(print);
			myDisplay.write("\n\n\n".getBytes());
			myDisplay.flush();
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
				if (myDisplay != null) myDisplay.close();
				if (mySerialComm != null) mySerialComm.closePort();
			}
			catch (Exception e)
			{
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e);
			}
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "OK");
	}

}
