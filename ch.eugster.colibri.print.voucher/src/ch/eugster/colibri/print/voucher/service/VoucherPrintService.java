package ch.eugster.colibri.print.voucher.service;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.print.section.ILayoutType;
import ch.eugster.colibri.print.service.AbstractPrintService;
import ch.eugster.colibri.print.voucher.Activator;
import ch.eugster.colibri.print.voucher.sections.VoucherLayoutType;

public class VoucherPrintService extends AbstractPrintService
{
	@Override
	public ILayoutType getLayoutType(final ReceiptPrinterService receiptPrinterService)
	{
		return new VoucherLayoutType(receiptPrinterService);
	}

	@Override
	public String getMenuLabel()
	{
		return "Layout Gutschein bearbeiten";
	}

	@Override
	public String getLayoutTypeId()
	{
		return Activator.PLUGIN_ID;
	}

}
