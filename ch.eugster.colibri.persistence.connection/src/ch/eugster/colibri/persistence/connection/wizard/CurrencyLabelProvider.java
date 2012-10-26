/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class CurrencyLabelProvider extends LabelProvider
{
	@Override
	public Image getImage(final Object object)
	{
		return null;
	}

	@Override
	public String getText(final Object object)
	{
		if (object instanceof String[])
		{
			final String[] currency = (String[]) object;
			return currency[4] + " - " + currency[1] + " (" + currency[5] + ")";
		}

		return "";
	}

}
