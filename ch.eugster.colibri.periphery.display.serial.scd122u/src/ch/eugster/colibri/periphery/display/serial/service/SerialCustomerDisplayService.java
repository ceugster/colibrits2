package ch.eugster.colibri.periphery.display.serial.service;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ch.eugster.colibri.periphery.converters.Converter;
import ch.eugster.colibri.periphery.display.serial.Activator;
import ch.eugster.colibri.periphery.display.service.AbstractCustomerDisplayService;

public class SerialCustomerDisplayService extends AbstractCustomerDisplayService
{
	private PrintStream display;

	@Override
	public void clearText()
	{
		this.openDisplay();
		if (this.display != null)
		{
			this.display.print((char) 0x0c);
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
		this.clearText();
		this.openDisplay();
		if (this.display != null)
		{
			this.display.println(this.correctText(text));
		}
		this.closeDisplay();
	}

	@Override
	public void displayText(final String converter, final String text)
	{
		this.clearText();
		this.openDisplay();
		if (this.display != null)
		{
			this.display.println(this.correctText(new Converter(converter), text));
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
			this.display = new PrintStream(this.getCustomerDisplaySettings().getPort());
		}
		catch (final FileNotFoundException e)
		{
			if (this.getEventAdmin() != null)
			{
				this.getEventAdmin().sendEvent(
						this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Das Kundendisplay kann nicht angesprochen werden.")));
			}
		}
	}

}
