package ch.eugster.colibri.print.receipt.sections;

import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.receipt.Activator;
import ch.eugster.colibri.print.section.AbstractLayoutType;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class ReceiptLayoutType extends AbstractLayoutType
{
	private ILayoutSectionType[] layoutSectionTypes;

	public ReceiptLayoutType(final ReceiptPrinterService receiptPrinterService)
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
			this.layoutSectionTypes = ReceiptLayoutSectionType.values();
		}
		return this.layoutSectionTypes;
	}

	@Override
	public String getName()
	{
		return "Beleglayout";
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
//			Receipt receipt = (Receipt) printable;
			final ReceiptLayoutSectionType[] layoutSectionTypes = (ReceiptLayoutSectionType[]) this
					.getLayoutSectionTypes();
			for (final ReceiptLayoutSectionType layoutSectionType : layoutSectionTypes)
			{
//				if (!layoutSectionType.equals(ReceiptLayoutSectionType.CUSTOMER) || receipt.getCustomer() != null)
//				{
					final ILayoutSection layoutSection = layoutSectionType.getLayoutSection();
					document.addAll(layoutSection.prepareSection(printable));
//				}
			}
			final String text = this.finish(document);
			this.getReceiptPrinterService().print(text);
		}
	}

}
