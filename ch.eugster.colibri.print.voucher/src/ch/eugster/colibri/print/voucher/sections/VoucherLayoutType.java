package ch.eugster.colibri.print.voucher.sections;

import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutType;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;
import ch.eugster.colibri.print.voucher.Activator;

public class VoucherLayoutType extends AbstractLayoutType
{
	public VoucherLayoutType(final ReceiptPrinterService receiptPrinterService)
	{
		this.setReceiptPrinterService(receiptPrinterService);
	}

	@Override
	public boolean automaticPrint()
	{
		return true;
	}

	@Override
	public boolean automaticPrintSelectable()
	{
		return true;
	}

	@Override
	public String getId()
	{
		return Activator.PLUGIN_ID;
	}

	@Override
	public ILayoutSectionType[] getLayoutSectionTypes()
	{
		if (this.layoutSectionTypes == null)
		{
			this.layoutSectionTypes = VoucherLayoutSectionType.values();
		}
		return this.layoutSectionTypes;
	}

	@Override
	public String getName()
	{
		return "Gutscheinlayout";
	}

	/**
	 * 
	 * @param printable
	 * 
	 *            computes the areas and fills in the values of printable and
	 *            prints out the document
	 */
	@Override
	public void printDocument(final IPrintable printable)
	{
		final Collection<String> document = new ArrayList<String>();
		if (printable instanceof Receipt)
		{
			Receipt receipt = (Receipt) printable;
			if (receipt.hasVoucherBack())
			{
				final VoucherLayoutSectionType[] layoutSectionTypes = (VoucherLayoutSectionType[]) this
						.getLayoutSectionTypes();
				for (final VoucherLayoutSectionType layoutSectionType : layoutSectionTypes)
				{
					final ILayoutSection layoutSection = layoutSectionType.getLayoutSection();
					document.addAll(layoutSection.prepareSection(printable));
				}
				final String text = this.finish(document);
				this.getReceiptPrinterService().print(text);
			}
		}
	}

}
