/*
 * Created on 05.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.ui.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public class DeletedEntityViewerFilter extends ViewerFilter
{

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element)
	{
		if (element instanceof AbstractEntity)
		{
			return !((AbstractEntity) element).isDeleted();
		}

		return true;
	}

}
