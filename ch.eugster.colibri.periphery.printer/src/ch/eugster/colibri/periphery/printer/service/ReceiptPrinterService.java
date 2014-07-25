package ch.eugster.colibri.periphery.printer.service;

import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;

public interface ReceiptPrinterService
{
	public static final String EVENT_ADMIN_TOPIC_ERROR = Topic.PRINT_ERROR.topic();

//	void cutPaper();

//	void cutPaper(int linesBeforeCut);

	ComponentContext getContext();

	void openDrawer(Currency currency);
	
	ReceiptPrinterSettings createReceiptPrinterSettings();
	
	ReceiptPrinterSettings getReceiptPrinterSettings();
	
	String convertToString(Object object);
	
	void print(String text, Salespoint salespoint);

	void print(String text);

	void print(String[] text);
	
	void testPrint(String deviceName, String conversions, String text, int feed) throws Exception;

	void testAscii(String deviceName, byte[] bytes) throws Exception;

	public enum Drawer
	{
		DRAWER1, DRAWER2;
		
		public String label()
		{
			switch(this)
			{
			case DRAWER1:
			{
				return "Schublade 1";
			}
			case DRAWER2:
			{
				return "Schublade 2";
			}
			default:
				throw new RuntimeException("Invalid drawer");
			}
		}
	}

	public enum Size
	{
		NORMAL, DOUBLE_WIDTH, DOUBLE_HEIGHT, DOUBLE_WIDTH_AND_HEIGHT;
	}
}
