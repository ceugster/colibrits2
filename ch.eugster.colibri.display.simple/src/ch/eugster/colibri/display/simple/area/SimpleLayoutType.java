package ch.eugster.colibri.display.simple.area;

import java.util.Collection;

import ch.eugster.colibri.display.area.AbstractLayoutType;
import ch.eugster.colibri.display.area.ILayoutAreaType;
import ch.eugster.colibri.display.simple.Activator;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;

public class SimpleLayoutType extends AbstractLayoutType
{
	public SimpleLayoutType(final CustomerDisplayService customerDisplayService)
	{
		super(customerDisplayService);
	}

	@Override
	public void displaySalespointClosedMessage()
	{
		final ILayoutAreaType layoutAreaType = this.getLayoutAreaTypes()[SimpleLayoutAreaType.SALESPOINT_CLOSED_MESSAGE.ordinal()];
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
		final ILayoutAreaType layoutAreaType = this.getLayoutAreaTypes()[SimpleLayoutAreaType.WELCOME_MESSAGE.ordinal()];
		displayWelcomeMessage(layoutAreaType.getLayoutArea().getTimerDelay());
	}

	@Override
	public void displayWelcomeMessage(int delay)
	{
		final ILayoutAreaType layoutAreaType = this.getLayoutAreaTypes()[SimpleLayoutAreaType.WELCOME_MESSAGE.ordinal()];
		final Collection<String> displayText = layoutAreaType.getLayoutArea().prepareDisplay(null);
		final String text = this.finish(displayText);
		this.getCustomerDisplayService().displayText(delay, text);
	}

	@Override
	public void displayPositionAddedMessage(Position position)
	{
		final ILayoutAreaType layoutAreaType = this.getLayoutAreaTypes()[SimpleLayoutAreaType.POSITION_ADDED_MESSAGE.ordinal()];
		final Collection<String> displayText = layoutAreaType.getLayoutArea().prepareDisplay(position);
		final String text = this.finish(displayText);
		this.getCustomerDisplayService().displayText(text);
	}

	@Override
	public void displayPaymentAddedMessage(Payment payment)
	{
		final ILayoutAreaType layoutAreaType = this.getLayoutAreaTypes()[SimpleLayoutAreaType.PAYMENT_ADDED_MESSAGE.ordinal()];
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
			this.layoutAreaTypes = SimpleLayoutAreaType.values();
		}
		return this.layoutAreaTypes;
	}

	@Override
	public String getName()
	{
		return "Layouts Kundendisplay";
	}

	@Override
	public boolean hasCustomerEditableAreaTypes() 
	{
		for (ILayoutAreaType areaType : SimpleLayoutAreaType.values())
		{
			if (areaType.isCustomerEditable())
			{
				return true;
			}
		}
		return false;
	}

}
