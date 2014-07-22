package ch.eugster.colibri.periphery.display.usb.service;

import java.io.PrintStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

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
		this.openDisplay();
		if (this.display != null)
		{
			this.display.write((byte) 0x0c);
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
					UsbCustomerDisplayService.this.displayText(text);
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
			System.out.println(print);
			for (int i = 0; i < print.length; i++)
			{
				this.display.write(print[i]);
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
			System.out.println(print);
			for (int i = 0; i < print.length; i++)
			{
				this.display.write(print[i]);
			}
		}
		this.closeDisplay();
	}

	private void closeDisplay()
	{
		if (this.display != null)
		{
			this.display.close();
		}
	}

	private void openDisplay()
	{
		try
		{
			this.display = new PrintStream(this.getPort());
			this.display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
		}
		catch (final Exception e)
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Das Kundendisplay kann nicht angesprochen werden.")));
			}
		} 
	}
	@Override
	public void testDisplay(String deviceName, String conversions, String text) throws Exception
	{
			PrintStream display = new PrintStream(deviceName);
			display.write(new byte[] { AsciiConstants.ESC, AsciiConstants.AT });
			display.write(text.getBytes());
			display.write("\n\n\n".getBytes());
			display.flush();
			display.close();
	}

	@Override
	public void testAscii(byte[] ascii) throws Exception {
		// TODO Auto-generated method stub
		
	}


}
