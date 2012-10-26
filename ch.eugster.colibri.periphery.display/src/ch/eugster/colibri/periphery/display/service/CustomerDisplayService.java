package ch.eugster.colibri.periphery.display.service;

import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;

public interface CustomerDisplayService
{
	public static final String EVENT_ADMIN_TOPIC_ERROR = "ch/eugster/colibri/periphery/display/error";

	void clearText();

	void displayText(int timerDelay, String text);

	void displayText(String text);

	void displayText(String converter, String text);

	CustomerDisplaySettings getCustomerDisplaySettings();

	CustomerDisplaySettings createCustomerDisplaySettings();
	
	SalespointCustomerDisplaySettings getSalespointCustomerDisplaySettings();
	
	String convertToString(Object object);
}
