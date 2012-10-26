package ch.eugster.colibri.print.service;

import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.ILayoutType;

public interface PrintService
{
	ComponentContext getContext();

	String getLayoutTypeId();

	ILayoutType getLayoutType();
	
	ReceiptPrinterService getReceiptPrinterService();

	ILayoutType getLayoutType(ReceiptPrinterService receiptPrinterService);

	ILayoutType getLayoutType(String receiptPrinterComponentName);

	String getMenuLabel();

	void printDocument(IPrintable printable);

	void testDocument(ILayoutType layoutType, Printout printout);
}
