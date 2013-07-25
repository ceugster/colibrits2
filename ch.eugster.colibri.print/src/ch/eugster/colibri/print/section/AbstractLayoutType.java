package ch.eugster.colibri.print.section;

import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Printout;

public abstract class AbstractLayoutType implements ILayoutType
{
	protected ReceiptPrinterService receiptPrinterService;

	protected ILayoutSectionType[] layoutSectionTypes;

	@Override
	public abstract ILayoutSectionType[] getLayoutSectionTypes();

	/**
	 * 
	 * @param layoutAreas
	 * @param printout
	 * 
	 *            computes the areas and prints out a test document
	 */
	@Override
	public void testDocument(final Printout printout)
	{
		final Collection<String> document = new ArrayList<String>();
		final ILayoutSectionType[] layoutSectionTypes = this.getLayoutSectionTypes();
		for (final ILayoutSectionType layoutSectionType : layoutSectionTypes)
		{
			document.addAll(layoutSectionType.getLayoutSection().prepareSection());
		}
		final String text = this.finish(document);
		this.getReceiptPrinterService().print(text, printout.getSalespoint());
//		this.getReceiptPrinterService().cutPaper();
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	protected String finish(final Collection<String> document)
	{
		document.add(this.addLinesBeforeCut());
		final StringBuilder result = new StringBuilder();
		for (final String section : document)
		{
			final String[] lines = section.split("\n");
			for (String line : lines)
			{
				line = line.replace("\r", "");
				if (line.length() > this.receiptPrinterService.getReceiptPrinterSettings().getCols())
				{
					line = line.substring(0, this.receiptPrinterService.getReceiptPrinterSettings().getCols());
				}
				if (line.length() < this.receiptPrinterService.getReceiptPrinterSettings().getCols())
				{
					line = AbstractLayoutType.padRight(line, this.receiptPrinterService.getReceiptPrinterSettings()
							.getCols());
				}
				System.out.println(line);
				result.append(line);
			}
		}
		return result.toString() + "\n";
	}

	protected void setLayoutSectionTypes(final ILayoutSectionType[] layoutSectionTypes)
	{
		this.layoutSectionTypes = layoutSectionTypes;
	}

	protected void setReceiptPrinterService(final ReceiptPrinterService receiptPrinterService)
	{
		this.receiptPrinterService = receiptPrinterService;
	}

	private String addLinesBeforeCut()
	{
		final int linesBeforeCut = this.getReceiptPrinterService().getReceiptPrinterSettings().getLinesBeforeCut();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < linesBeforeCut; i++)
		{
			builder = builder.append(AbstractLayoutType.padRight("", this.getReceiptPrinterService()
					.getReceiptPrinterSettings().getCols()));
		}
		return builder.toString();
	}

	public static String padLeft(final String s, final int n)
	{
		return String.format("%1$" + n + "s", s);
	}

	public static String padRight(final String s, final int n)
	{
		return String.format("%1$-" + n + "s", s);
	}

	@Override
	public ReceiptPrinterService getReceiptPrinterService()
	{
		return this.receiptPrinterService;
	}

	@Override
	public boolean hasCustomerEditableAreaTypes() 
	{
		ILayoutSectionType[] sectionTypes = this.getLayoutSectionTypes();
		for (ILayoutSectionType sectionType : sectionTypes)
		{
			if (sectionType.isCustomerEditable())
			{
				return true;
			}
		}
		return false;
	}
}
