package ch.eugster.colibri.periphery.display.usb.service;

import java.io.PrintStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.periphery.constants.AsciiConstants;
import ch.eugster.colibri.periphery.converters.Converter;
import ch.eugster.colibri.periphery.display.service.AbstractCustomerDisplayService;
import ch.eugster.colibri.periphery.display.usb.Activator;

public class UsbCustomerDisplayService extends AbstractCustomerDisplayService
{
	private PrintStream display;
	
	@Override
	public void clearDisplay()
	{
		this.writeBytes(new byte[] { 0x0c });
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
					UsbCustomerDisplayService.this.clearDisplay();
					byte[] bytes = UsbCustomerDisplayService.this.correctText(text);
					UsbCustomerDisplayService.this.writeBytes(bytes);
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
		byte[] bytes = this.correctText(text);
		this.writeBytes(bytes);
	}
	
	private void writeBytes(byte[] bytes)
	{
		if (this.display!= null)
		{
			try
			{
				this.display.write(new byte[] { 0x0c});
				this.display.flush();
				this.display.write(bytes);
				this.display.flush();
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
		byte[] bytes = this.correctText(text);
		this.writeBytes(bytes);
	}
	
	private PrintStream openPort(String deviceName)
	{
		if (deviceName != null)
		{
			try
			{
		        display = new PrintStream(deviceName);
		        this.writeBytes(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
			}
			catch (Exception e)
			{
				
			}
		}
		return display;
	}

	protected void activate(final ComponentContext context)
	{
		super.activate(context);
		this.display = openPort(this.getPort());
	}

	private void closePort(PrintStream display)
	{
		if (display != null)
		{
			try 
			{
	            display.close();
	            display = null;
			}
			catch (final Exception e)
			{
				sendEvent(e);
			}
		}
	}
	
	protected void deactivate(final ComponentContext context)
	{
		this.clearDisplay();
		this.closePort(this.display);
		super.deactivate(context);
	}

//	private int convert(String number)
//	{
//		try
//		{
//			return Integer.valueOf(number).intValue();
//		}
//		catch(NumberFormatException e)
//		{
//			return 0;
//		}
//	}

	@Override
	public void testDisplay(String deviceName, String conversions, String text) throws Exception
	{
		if (deviceName == null || deviceName.isEmpty())
		{
			throw new NullPointerException("Keinen Port übergeben.");
		}
		String oldPort = null;

		if (this.display == null)
		{
			this.display = this.openPort(deviceName);
		}
		else
		{
			if (!deviceName.equals(this.getPort()))
			{
				oldPort = this.getPort();
				this.closePort(this.display);
				this.display = this.openPort(deviceName);
			}
		}
		
		this.writeBytes(new byte[] { 0x0c});
		byte[] bytes = conversions == null || conversions.isEmpty() ? this.correctText(new Converter(conversions), text) : text.getBytes();
		this.writeBytes(bytes);

		if (oldPort != null)
		{
			this.closePort(this.display);
			this.display = this.openPort(oldPort);
		}
	}

}
