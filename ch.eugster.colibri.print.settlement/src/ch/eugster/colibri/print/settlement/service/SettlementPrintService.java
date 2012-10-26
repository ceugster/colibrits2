package ch.eugster.colibri.print.settlement.service;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.ILayoutType;
import ch.eugster.colibri.print.service.AbstractPrintService;
import ch.eugster.colibri.print.settlement.Activator;
import ch.eugster.colibri.print.settlement.sections.SettlementLayoutType;

public class SettlementPrintService extends AbstractPrintService
{
	@Override
	public ILayoutType getLayoutType(final ReceiptPrinterService receiptPrinterService)
	{
		return new SettlementLayoutType(receiptPrinterService);
	}

	@Override
	public String getMenuLabel()
	{
		return "Layout Tagesabschluss bearbeiten";
	}

	@Override
	public void printDocument(final IPrintable printable)
	{
		if (this.isReady())
		{
			final ILayoutType layoutType = this.getLayoutType();
			if (layoutType != null)
			{
				layoutType.printDocument(printable);
			}
		}
	}

	@Override
	public String getLayoutTypeId()
	{
		return Activator.PLUGIN_ID;
	}

}
