/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.editors;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.ExternalProductGroup;

public class ExternalProductGroupSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final ExternalProductGroup epg1 = (ExternalProductGroup) element1;
		final ExternalProductGroup epg2 = (ExternalProductGroup) element2;

		if ((epg1 != null) && (epg2 != null))
		{
			return epg1.getCode().compareTo(epg2.getCode());
		}
		else if (epg1 == null)
		{
			return -1;
		}
		else if (epg2 == null)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
