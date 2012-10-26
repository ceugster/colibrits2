package ch.eugster.colibri.display.service;

import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.display.area.ILayoutType;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;

public interface DisplayService
{
	void displayPaymentAddedMessage(Payment payment);

	void displayPositionAddedMessage(Position position);

	void displaySalespointClosedMessage();

	void displayTestMessage(ILayoutArea layoutArea, Display display);

	void displayWelcomeMessage();

	ComponentContext getContext();

	ILayoutType getLayoutType();

	ILayoutType getLayoutType(CustomerDisplayService customerDisplayService);

	ILayoutType getLayoutType(String customerDisplayComponentName);

	String getMenuLabel();
}
