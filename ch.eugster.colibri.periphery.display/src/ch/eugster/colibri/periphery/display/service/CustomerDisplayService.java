package ch.eugster.colibri.periphery.display.service;

import org.osgi.service.component.ComponentContext;

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
	
	void testDisplay(String deviceName, String conversions, String text) throws Exception;
	
	void testAscii(String deviceName, byte[] bytes) throws Exception;
	
	ComponentContext getContext();
}
