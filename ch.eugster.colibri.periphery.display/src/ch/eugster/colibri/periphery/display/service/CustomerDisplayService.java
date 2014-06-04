package ch.eugster.colibri.periphery.display.service;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;

public interface CustomerDisplayService
{
	public static final String EVENT_ADMIN_TOPIC_ERROR = Topic.DISPLAY_ERROR.topic();

	void clearDisplay();

	void displayText(int timerDelay, String text);

	void displayText(String text);

	void displayText(String converter, String text);

	CustomerDisplaySettings getCustomerDisplaySettings();

	CustomerDisplaySettings createCustomerDisplaySettings();
	
//	SalespointCustomerDisplaySettings getSalespointCustomerDisplaySettings();
	
	String convertToString(Object object);
	
	IStatus testDisplay(Properties properties, String text);
}
