package ch.eugster.colibri.print.section;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.print.IPrintable;

public interface ILayoutType
{
	boolean automaticPrint();

	boolean automaticPrintSelectable();

	String getId();

	ILayoutSectionType[] getLayoutSectionTypes();

	String getName();

	ReceiptPrinterService getReceiptPrinterService();

	void printDocument(IPrintable printable);

	void testDocument(Printout printout);
}
