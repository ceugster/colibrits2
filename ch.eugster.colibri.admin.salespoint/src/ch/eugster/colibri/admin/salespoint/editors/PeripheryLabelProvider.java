/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;

public class PeripheryLabelProvider extends LabelProvider
{
	@Override
	public Image getImage(final Object object)
	{
		return null;
	}

	@Override
	public String getText(final Object object)
	{
		if (object instanceof CustomerDisplaySettings)
		{
			final String name = ((CustomerDisplaySettings) object).getName();
			return name == null ? "" : name;
		}
		if (object instanceof ReceiptPrinterSettings)
		{
			final String name = ((ReceiptPrinterSettings) object).getName();
			return name == null ? "" : name;
		}
		return "";
	}
}
