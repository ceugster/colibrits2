package ch.eugster.colibri.periphery.display.serial.service;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

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
	private SerialPort display;
	
//	private PrintStream display;

	@Override
	public void clearDisplay()
	{
		byte[] print = new byte[] { AsciiConstants.ESC, AsciiConstants.AT };
		try 
		{
			this.display.writeBytes(print);
		}
		catch (final Exception e)
		{
			sendEvent(e);
		}
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
					byte[] bytes = SerialCustomerDisplayService.this.correctText(text);
					SerialCustomerDisplayService.this.writeBytes(bytes);
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
				byte[] bytes = this.correctText(text);
				this.writeBytes(bytes);
//				this.display.flush();
//			} 
//			catch (IOException e) 
//			{
//			}
//		}
//		this.closeDisplay();
	}
	
	private void writeBytes(byte[] bytes)
	{
		if (this.display!= null)
		{
			try
			{
				this.display.writeBytes(bytes);
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
					this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Das Kundendisplay kann nicht angesprochen werden.")));
		}
	}

	@Override
	public void displayText(final String converter, String text)
	{
		this.clearDisplay();
		byte[] bytes = SerialCustomerDisplayService.this.correctText(text);
		this.writeBytes(bytes);
	}
	
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
		this.display = openPort(this.getPort());
	}

	private void closePort(SerialPort display)
	{
		if (display != null)
		{
			try 
			{
	            display.closePort();//Close serial port
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
		closePort(this.display);
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
			if (!deviceName.equals(this.display.getPortName()))
			{
				oldPort = this.display.getPortName();
				this.closePort(this.display);
				this.display = this.openPort(deviceName);
			}
		}
		
		this.display.writeBytes(new byte[] { 0x0c});
		byte[] bytes = conversions == null || conversions.isEmpty() ? text.getBytes() : this.correctText(new Converter(conversions), text);
		this.display.writeBytes(bytes);

		if (oldPort != null)
		{
			this.closePort(this.display);
			this.display = this.openPort(deviceName);
		}
	}

}
