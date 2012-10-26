/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.persistence.model.CurrentTax;

public class CurrentTaxLabelProvider extends LabelProvider
{
	@Override
	public Image getImage(final Object object)
	{
		return null;
	}

	@Override
	public String getText(final Object object)
	{
		if (object instanceof CurrentTax)
		{
			final CurrentTax currentTax = (CurrentTax) object;
			return currentTax.format();
		}

		return "";
	}

}