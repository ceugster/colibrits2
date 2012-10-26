package ch.eugster.colibri.print.settlement.sections;

import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutType;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;
import ch.eugster.colibri.print.settlement.Activator;

public class SettlementLayoutType extends AbstractLayoutType
{
	public SettlementLayoutType(final ReceiptPrinterService receiptPrinterService)
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
		return false;
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
			this.layoutSectionTypes = SettlementLayoutSectionType.values();
		}
		return this.layoutSectionTypes;
	}

	@Override
	public String getName()
	{
		return "Layout Tagesabschluss";
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
		if (printable instanceof Settlement)
		{
			final SettlementLayoutSectionType[] layoutSectionTypes = (SettlementLayoutSectionType[]) this
					.getLayoutSectionTypes();
			for (final SettlementLayoutSectionType layoutSectionType : layoutSectionTypes)
			{
				final ILayoutSection layoutArea = layoutSectionType.getLayoutSection();
				document.addAll(layoutArea.prepareSection(printable));
			}
			final String text = this.finish(document);
			this.getReceiptPrinterService().print(text);
		}
	}

}
