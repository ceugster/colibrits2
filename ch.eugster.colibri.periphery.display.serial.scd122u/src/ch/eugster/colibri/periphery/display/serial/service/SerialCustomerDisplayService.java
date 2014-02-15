package ch.eugster.colibri.periphery.display.serial.service;

import java.io.FileNotFoundException;
import java.io.PrintStream;

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
	public void displayText(String text)
	{
		this.clearText();
		this.openDisplay();
		if (this.display != null)
		{
			text = this.correctText(text);
			System.out.println(text);
			this.display.println(text);
		}
		this.closeDisplay();
	}

	@Override
	public void displayText(final String converter, String text)
	{
		this.clearText();
		this.openDisplay();
		if (this.display != null)
		{
			text = this.correctText(new Converter(converter), text);
			System.out.println(text);
			this.display.println(text);
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
			initDisplay(this.display);
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

	private void initDisplay(PrintStream ps)
	{
		for (int i = 0; i < validFonts.length; i++)
		{
			switch (i)
			{
				case IC_AMERICAN:
					validFonts[i] = true;
					break;
				case IC_BRITISH:
					validFonts[i] = true;
					break;
				case IC_DANISH1:
					validFonts[i] = true;
					break;
				case IC_DANISH2:
					validFonts[i] = true;
					break;
				case IC_FRENCH:
					validFonts[i] = true;
					break;
				case IC_GERMAN:
					validFonts[i] = true;
					break;
				case IC_ITALIAN:
					validFonts[i] = true;
					break;
				case IC_JAPANESE:
					validFonts[i] = true;
					break;
				case IC_NORWEGIAN:
					validFonts[i] = true;
					break;
				case IC_RUSSIAN:
					validFonts[i] = true;
					break;
				case IC_SWEDISH:
					validFonts[i] = true;
					break;
				default:
					validFonts[i] = false;
			}
		}
		this.setDisplayEmulationMode(ps, 55);
		this.selectInternationalCharacterSet(ps, 0);
	}
	
	/**
	 * @param font
	 */
	private void selectInternationalFont(PrintStream ps, int font)
	{
		if (validFonts[font])
		{
			ps.write(AsciiConstants.ESC);
			ps.write(AsciiConstants.f);
			ps.write(font);
			ps.flush();
		}
	}
	
	public void selectInternationalCharacterSet(PrintStream ps, int characterSet)
	{
		int set = 0;
		switch (characterSet)
		{
			case AMERICAN:
				set = IC_AMERICAN;
				break;
			case FRENCH:
				set = IC_FRENCH;
				break;
			case GERMAN:
				set = IC_GERMAN;
				break;
			case ITALIAN:
				set = IC_ITALIAN;
				break;
			case SPANISH:
				set = IC_SPANISH;
				break;
			default:
				set = 0;
		}
		this.selectInternationalFont(ps, set);
	}
	
	private void setDisplayEmulationMode(PrintStream ps, int mode)
	{
		ps.write(AsciiConstants.STX);
		ps.write(AsciiConstants.ENQ);
		ps.write(AsciiConstants.C);
		ps.write(mode);
		ps.write(AsciiConstants.ETX);
		ps.flush();
	}
	
	protected static final int IC_AMERICAN = AsciiConstants.A;
	protected static final int IC_GERMAN = AsciiConstants.G;
	protected static final int IC_ITALIAN = AsciiConstants.I;
	protected static final int IC_JAPANESE = AsciiConstants.J;
	protected static final int IC_BRITISH = AsciiConstants.U;
	protected static final int IC_FRENCH = AsciiConstants.F;
	protected static final int IC_SPANISH = AsciiConstants.S;
	protected static final int IC_NORWEGIAN = AsciiConstants.N;
	protected static final int IC_SWEDISH = AsciiConstants.W;
	protected static final int IC_DANISH1 = AsciiConstants.D;
	protected static final int IC_DANISH2 = AsciiConstants.E;
	protected static final int IC_SLAVONIC = AsciiConstants.L;
	protected static final int IC_RUSSIAN = AsciiConstants.R;
	
	protected static final int EM_DSP800 = 48;
	protected static final int EM_ESC_POS = 49;
	protected static final int EM_ADM788 = 50;
	protected static final int EM_ADM787 = 51;
	protected static final int EM_AEDEX = 52;
	protected static final int EM_UTC_P = 53;
	protected static final int EM_UTC_S = 54;
	protected static final int EM_CD5220 = 55;
	
	public static final int AMERICAN = 0;
	public static final int GERMAN = 1;
	public static final int FRENCH = 2;
	public static final int ITALIAN = 3;
	public static final int SPANISH = 4;

	protected static boolean[] validFonts = new boolean[127];
	
}
