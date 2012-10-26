package ch.eugster.colibri.display.area;

import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;

public interface ILayoutType
{

	void displayPaymentAddedMessage(final Payment payment);

	/**
	 * 
	 * @param printable
	 * 
	 *            computes the areas and fills in the values of printable and
	 *            prints out the document
	 */
	void displayPositionAddedMessage(final Position position);

	void displaySalespointClosedMessage();

	/**
	 * 
	 * @param layoutAreas
	 * @param displayArea
	 * 
	 *            computes the areas and prints out a test document
	 */
	void displayTestMessage(final ILayoutArea layoutArea, final Display display);

	void displayWelcomeMessage();

	CustomerDisplayService getCustomerDisplayService();

	String getId();

	ILayoutAreaType[] getLayoutAreaTypes();

	String getName();

}