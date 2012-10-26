/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Stock;

public class StockEditorInput extends AbstractEntityEditorInput<Stock>
{
	public StockEditorInput(final Stock stock)
	{
		super(stock);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof StockEditorInput)
		{
			final StockEditorInput input = (StockEditorInput) object;
			final Stock money = (Stock) input.getAdapter(Stock.class);
			if ((money.getId() != null) && money.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(Stock.class.getName()))
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
		return "Test";
	}

	@Override
	public AbstractEntity getParent()
	{
		return entity.getSalespoint();
	}

	@Override
	public String getToolTipText()
	{
		return "TestTooltip";
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
