package ch.eugster.colibri.periphery.display;

import ch.eugster.colibri.periphery.display.service.AbstractCustomerDisplayService;

public class DummyCustomerDisplayService extends AbstractCustomerDisplayService {

	@Override
	public void clearDisplay() 
	{
		System.out.println();
	}

	@Override
	public void displayText(int timerDelay, String text) 
	{
		try
		{
			Thread.sleep(1000 * timerDelay);
		}
		catch (InterruptedException e)
		{
		}
		displayText(text);
	}

	@Override
	public void displayText(String text) 
	{
		if (text.length() > this.getColumnCount())
		{
			System.out.println(text.substring(0,this.getColumnCount()));
			System.out.println(text.substring(this.getColumnCount()));
			System.out.println();
		}
		else
		{
			System.out.println(text);
			System.out.println();
			System.out.println();
		}
	}

	@Override
	public void displayText(String converter, String text) 
	{
		displayText(text);
	}

	@Override
	public void testDisplay(String deviceName, String conversions, String text)
			throws Exception 
	{
		displayText(text);
	}

}
