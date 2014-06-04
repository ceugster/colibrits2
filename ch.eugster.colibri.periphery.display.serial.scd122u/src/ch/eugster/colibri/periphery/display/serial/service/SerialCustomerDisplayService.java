package ch.eugster.colibri.periphery.display.serial.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import javax.comm.CommPort;
import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ch.eugster.colibri.periphery.constants.AsciiConstants;
import ch.eugster.colibri.periphery.converters.Converter;
import ch.eugster.colibri.periphery.display.serial.Activator;
import ch.eugster.colibri.periphery.display.service.AbstractCustomerDisplayService;

public class SerialCustomerDisplayService extends AbstractCustomerDisplayService
{
	private CommPort commPort;
	
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
//			try 
//			{
				this.display.flush();
				this.display.close();
//			} 
//			catch (IOException e) 
//			{
//			}
			this.display = null;
		}
		if (this.commPort != null)
		{
			this.commPort.close();
			this.commPort = null;
		}
	}

	private void openDisplay()
	{
		try
		{
			String port = getPort();
			port = port.endsWith(":") ? port.substring(0, port.length() - 1) : port;
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(port);
			commPort = portId.open("customerdisplay", 2000);
			display = new PrintStream(commPort.getOutputStream());
			display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
//			display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.S });
//			display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.R, 2 });
		}
		catch (final FileNotFoundException e)
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Das Kundendisplay kann nicht angesprochen werden.")));
			}
		} 
		catch (NoSuchPortException e) 
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Port " + getPort() + " für das Kundendisplay existiert nicht.")));
			}
		} 
		catch (PortInUseException e) 
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Port " + getPort() + " wird bereits verwendet.")));
			}
		} 
		catch (IOException e) 
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Ein IO-Fehler ist aufgetreten.")));
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
		PrintStream display = null;
		CommPort commPort = null;
		try
		{
			String port = properties.getProperty("port");
			String converter = properties.getProperty("converter");
			int rows = convert(properties.getProperty("rows"));
			int cols = convert(properties.getProperty("cols"));
			byte[] print = this.correctText(new Converter(converter), text, rows * cols);
			port = port.endsWith(":") ? port.substring(0, port.length() - 1) : port;
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(port);
			commPort = portId.open("customerdisplay", 2000);
			display = new PrintStream(commPort.getOutputStream());
			display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
			display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.S });
			display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.R, 2 });
			display.write(print);
			display.write("\n\n\n".getBytes());
			display.flush();
		}
		catch (final Exception e)
		{
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e);
		}
		finally
		{
			try
			{
				if (display != null) display.close();
				if (commPort != null) commPort.close();
			}
			catch (Exception e)
			{
				
			}
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "OK");
	}

}
