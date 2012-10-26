/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.viewers.ArrayContentProvider;

public class CurrencyContentProvider extends ArrayContentProvider
{
	@Override
	public Object[] getElements(final Object object)
	{
		if (object instanceof Object[])
		{
			return (Object[]) object;
		}

		return new String[0];
	}
}
