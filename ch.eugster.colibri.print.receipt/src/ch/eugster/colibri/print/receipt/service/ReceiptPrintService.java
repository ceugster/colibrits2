package ch.eugster.colibri.print.receipt.service;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.print.receipt.Activator;
import ch.eugster.colibri.print.receipt.sections.ReceiptLayoutType;
import ch.eugster.colibri.print.section.ILayoutType;
import ch.eugster.colibri.print.service.AbstractPrintService;

public class ReceiptPrintService extends AbstractPrintService
{
	@Override
	public ILayoutType getLayoutType(final ReceiptPrinterService receiptPrinterService)
	{
		return new ReceiptLayoutType(receiptPrinterService);
	}

	@Override
	public String getLayoutTypeId()
	{
		return Activator.PLUGIN_ID;
	}

	@Override
	public String getMenuLabel()
	{
		return "Layout Beleg bearbeiten";
	}

	protected boolean openDrawerAllowed()
	{
		return true; 
	}
}
