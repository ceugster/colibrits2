package ch.eugster.colibri.display.simple.service;

import ch.eugster.colibri.display.area.ILayoutType;
import ch.eugster.colibri.display.service.AbstractDisplayService;
import ch.eugster.colibri.display.service.DisplayService;
import ch.eugster.colibri.display.simple.area.SimpleLayoutType;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;

public class SimpleDisplayService extends AbstractDisplayService implements DisplayService
{

	@Override
	public void displayPaymentAddedMessage(final Payment payment)
	{
		if (this.getLayoutType() != null)
		{
			this.getLayoutType().displayPaymentAddedMessage(payment);
		}
	}

	@Override
	public void displayPositionAddedMessage(final Position position)
	{
		if (this.getLayoutType() != null)
		{
			this.getLayoutType().displayPositionAddedMessage(position);
		}
	}

	@Override
	public void displaySalespointClosedMessage()
	{
		if (this.getLayoutType() != null)
		{
			this.getLayoutType().displaySalespointClosedMessage();
		}
	}

	@Override
	public void displayWelcomeMessage()
	{
		if (this.getLayoutType() != null)
		{
			this.getLayoutType().displayWelcomeMessage();
		}
	}

	@Override
	public ILayoutType getLayoutType(final CustomerDisplayService customerDisplayService)
	{
		return new SimpleLayoutType(customerDisplayService);
	}

	@Override
	public String getMenuLabel()
	{
		return "Layout Kundendisplay bearbeiten";
	}

	@Override
	public void displayWelcomeMessage(int delay) 
	{
		if (this.getLayoutType() != null)
		{
			this.getLayoutType().displayWelcomeMessage(delay);
		}
	}

}
