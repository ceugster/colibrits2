/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.jface.viewers.ArrayContentProvider;

import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;

public class PeripheryContentProvider extends ArrayContentProvider
{
	@Override
	public Object[] getElements(final Object object)
	{
		if (object instanceof ReceiptPrinterSettings[])
		{
			return (ReceiptPrinterSettings[]) object;
		}

		if (object instanceof CustomerDisplaySettings[])
		{
			return (CustomerDisplaySettings[]) object;
		}

		return new Object[0];
	}
}
