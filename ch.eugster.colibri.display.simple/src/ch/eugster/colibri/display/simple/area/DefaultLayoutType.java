package ch.eugster.colibri.display.simple.area;

import java.util.Collection;

import ch.eugster.colibri.display.area.AbstractLayoutType;
import ch.eugster.colibri.display.area.ILayoutAreaType;
import ch.eugster.colibri.display.simple.Activator;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;

public class DefaultLayoutType extends AbstractLayoutType
{
	public DefaultLayoutType(final CustomerDisplayService customerDisplayService)
	{
		super(customerDisplayService);
	}

	@Override
	public void displaySalespointClosedMessage()
	{
		final ILayoutAreaType layoutAreaType = this.getLayoutAreaTypes()[DefaultLayoutAreaType.SALESPOINT_CLOSED_MESSAGE.ordinal()];
		final Collection<String> displayText = layoutAreaType.getLayoutArea().prepareDisplay(null);
		final String text = this.finish(displayText);
		this.getCustomerDisplayService().displayText(layoutAreaType.getLayoutArea().getTimerDelay(), text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.display.area.ILayoutType#displayWelcomeMessage()
	 */
	@Override
	public void displayWelcomeMessage()
	{
		final ILayoutAreaType layoutAreaType = this.getLayoutAreaTypes()[DefaultLayoutAreaType.WELCOME_MESSAGE.ordinal()];
		final Collection<String> displayText = layoutAreaType.getLayoutArea().prepareDisplay(null);
		final String text = this.finish(displayText);
		this.getCustomerDisplayService().displayText(layoutAreaType.getLayoutArea().getTimerDelay(), text);
	}

	@Override
	public void displayPositionAddedMessage(Position position)
	{
		final ILayoutAreaType layoutAreaType = this.getLayoutAreaTypes()[DefaultLayoutAreaType.POSITION_ADDED_MESSAGE.ordinal()];
		final Collection<String> displayText = layoutAreaType.getLayoutArea().prepareDisplay(position);
		final String text = this.finish(displayText);
		this.getCustomerDisplayService().displayText(text);
	}

	@Override
	public void displayPaymentAddedMessage(Payment payment)
	{
		final ILayoutAreaType layoutAreaType = this.getLayoutAreaTypes()[DefaultLayoutAreaType.PAYMENT_ADDED_MESSAGE.ordinal()];
		final Collection<String> displayText = layoutAreaType.getLayoutArea().prepareDisplay(payment);
		final String text = this.finish(displayText);
		this.getCustomerDisplayService().displayText(text);
	}

	@Override
	public String getId()
	{
		return Activator.PLUGIN_ID;
	}

	public ILayoutAreaType[] getLayoutAreaTypes()
	{
		if (this.layoutAreaTypes == null)
		{
			this.layoutAreaTypes = DefaultLayoutAreaType.values();
		}
		return this.layoutAreaTypes;
	}

	@Override
	public String getName()
	{
		return "Layouts Kundendisplay";
	}

}
