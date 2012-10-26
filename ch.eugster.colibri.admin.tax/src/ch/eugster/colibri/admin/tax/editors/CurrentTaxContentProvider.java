/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import org.eclipse.jface.viewers.ArrayContentProvider;

import ch.eugster.colibri.persistence.model.CurrentTax;

public class CurrentTaxContentProvider extends ArrayContentProvider
{
	@Override
	public Object[] getElements(final Object object)
	{
		if (object instanceof CurrentTax[])
		{
			return (CurrentTax[]) object;
		}

		return new CurrentTax[0];
	}
}
