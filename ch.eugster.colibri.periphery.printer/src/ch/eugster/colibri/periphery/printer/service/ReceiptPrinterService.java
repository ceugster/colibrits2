package ch.eugster.colibri.periphery.printer.service;

import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;

public interface ReceiptPrinterService
{
	public static final String EVENT_ADMIN_TOPIC_ERROR = "ch/eugster/colibri/periphery/printer/error";

	void cutPaper();

	void cutPaper(int linesBeforeCut);

	void openDrawer(PaymentType paymentType);
	
	ReceiptPrinterSettings createReceiptPrinterSettings();
	
	ReceiptPrinterSettings getReceiptPrinterSettings();
	
	String convertToString(Object object);
	
	void print(String text, Salespoint salespoint);

	void print(String text);

	void print(String[] text);

	void testPrint(String deviceName, String conversions, String text, int feed);

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
}
