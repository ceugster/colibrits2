package ch.eugster.colibri.display.area;

import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;

public abstract class AbstractLayoutType implements ILayoutType
{
	private CustomerDisplayService customerDisplayService;

	private SalespointCustomerDisplaySettings settings;
	
	protected ILayoutAreaType[] layoutAreaTypes;

	public AbstractLayoutType(final CustomerDisplayService customerDisplayService)
	{
		this.customerDisplayService = customerDisplayService;
	}

	protected int getCols()
	{
		return settings.getCols();
	}
	
	protected int getRows()
	{
		return settings.getRows();
	}
	
	protected String getPort()
	{
		return settings.getPort();
	}
	
	protected String getConverter()
	{
		return settings.getConverter();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.display.area.ILayoutType#displayPaymentAddedMessage
	 * (ch.eugster.colibri.persistence.model.Payment)
	 */
	@Override
	public void displayPaymentAddedMessage(final Payment payment)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.display.area.ILayoutType#displayPositionAddedMessage
	 * (ch.eugster.colibri.persistence.model.Position)
	 */
	@Override
	public void displayPositionAddedMessage(final Position position)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.display.area.ILayoutType#displaySalespointClosedMessage
	 * ()
	 */
	@Override
	public void displaySalespointClosedMessage()
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.display.area.ILayoutType#displayTestMessage(ch.eugster
	 * .colibri.display.area.ILayoutArea,
	 * ch.eugster.colibri.persistence.model.Display)
	 */
	@Override
	public void displayTestMessage(final ILayoutArea layoutArea, final Display display)
	{
		final Collection<String> document = new ArrayList<String>();
		document.addAll(layoutArea.prepareDisplay());
		final String text = this.finish(document);
		this.getCustomerDisplayService().displayText(layoutArea.getTimerDelay(), text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.display.area.ILayoutType#getCustomerDisplayService()
	 */
	@Override
	public CustomerDisplayService getCustomerDisplayService()
	{
		return this.customerDisplayService;
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	protected String finish(final Collection<String> lines)
	{
		StringBuilder result = new StringBuilder();
		for (final String line : lines)
		{
			String[] sublines = line.split("[\r\n]");
			for (String subline : sublines)
			{
				subline = padRight(subline, 20);
				if (subline.trim().length() > 0)
				{
					result = result.append(subline);
				}
			}
		}
		return result.toString();
	}

	protected void setCustomerDisplayService(final CustomerDisplayService customerDisplayService)
	{
		this.customerDisplayService = customerDisplayService;
	}

	protected void setLayoutAreaTypes(final ILayoutAreaType[] layoutAreaTypes)
	{
		this.layoutAreaTypes = layoutAreaTypes;
	}

	public static String padLeft(final String s, final int n)
	{
		return String.format("%1$" + n + "s", s);
	}

	public static String padRight(final String s, final int n)
	{
		return String.format("%1$-" + n + "s", s);
	}
}
