/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.Salespoint;

public class SalespointEditorInput extends AbstractEntityEditorInput<Salespoint>
{
	public SalespointEditorInput(final Salespoint salespoint)
	{
		super(salespoint);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof SalespointEditorInput)
		{
			final SalespointEditorInput input = (SalespointEditorInput) object;
			final Salespoint salespoint = (Salespoint) input.getAdapter(Salespoint.class);
			if ((salespoint.getId() != null) && salespoint.getId().equals(entity.getId()))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter)
	{
		if (adapter.getName().equals(Salespoint.class.getName()))
		{
			return entity;
		}
		else
		{
			return null;
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		return entity.getName();
	}

	@Override
	public String getToolTipText()
	{
		return entity.getName();
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
