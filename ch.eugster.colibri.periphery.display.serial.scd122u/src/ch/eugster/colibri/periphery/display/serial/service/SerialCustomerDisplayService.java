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
					this.getEvent(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Der Belegdrucker kann nicht angesprochen werden.")));
		}
	}

	@Override
	public void displayText(final String converter, String text)
	{
		this.clearDisplay();
		byte[] bytes = SerialCustomerDisplayService.this.correctText(text);
		this.writeBytes(bytes);
	}

	protected void activate(final ComponentContext context)
	{
		super.activate(context);
		String portname = this.getPort();
		if (portname != null)
		{
			portname = portname.endsWith(":") ? portname.substring(0, portname.length() - 1) : portname;
			String[] ports = SerialPortList.getPortNames();
			for (String port : ports)
			{
				if (port.equalsIgnoreCase(portname)) 
				{
			        display = new SerialPort(port);
			        try {
			            display.openPort();//Open serial port
			            display.setParams(SerialPort.BAUDRATE_9600, 
			                                 SerialPort.DATABITS_8,
			                                 SerialPort.STOPBITS_1,
			                                 SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
			        }
			        catch (SerialPortException ex) 
			        {
			        	display = null;
			        	sendEvent(ex);
			        }
			        break;
				}
			}
		}
	}

	protected void deactivate(final ComponentContext context)
	{
		if (this.display != null)
		{
			this.clearDisplay();
			try 
			{
	            display.closePort();//Close serial port
			}
			catch (final Exception e)
			{
				sendEvent(e);
			}
		}
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
//		ComponentContext context = this.getContext();
//		if (context != null)
//		{
//			Bundle bundle = context.getBundleContext().getBundle();
//			bundle.stop();
//			boolean open = false;
//			try
//			{
//				String port = deviceName.endsWith(":") ? deviceName.substring(0, deviceName.length() - 1) : deviceName;
//				display = new SerialPort(port);
//				open = display.openPort();
//				if (open)
//				{
					display.writeBytes(new byte[] { 0x0c});
					byte[] bytes = this.correctText(new Converter(conversions), text);
					display.writeBytes(bytes);
//				}
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//				throw e;
//			}
//			finally
//			{
//				if (open)
//				{
//					display.closePort();
//				}
//				bundle.start();
//			}
//		}
	}

	@Override
	public void testAscii(byte[] bytes) throws Exception
	{
		if (display == null)
		{
			throw new NullPointerException("Das Kundendisplay konnte nicht angesprochen werden.");
		}
		display.writeBytes( new byte[] { 0x0c});
		display.writeBytes(bytes);
	}

}
