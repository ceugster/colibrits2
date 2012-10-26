/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ExternalProductGroupContentProvider implements ITreeContentProvider
{
	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getElements(final Object element)
	{
		return getChildren(element);
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

	@Override
	public Object[] getChildren(Object parentElement) 
	{
		if (parentElement instanceof PersistenceService)
		{
			final PersistenceService persistenceService = (PersistenceService) parentElement;
			if (persistenceService != null)
			{
				final ExternalProductGroupQuery query = (ExternalProductGroupQuery) persistenceService.getServerService().getQuery(ExternalProductGroup.class);
				return query.selectAll(true).toArray(new ExternalProductGroup[0]);
			}
		}
		else if (parentElement instanceof ExternalProductGroup)
		{
			ExternalProductGroup externalProductGroup = (ExternalProductGroup) parentElement;
			return new ProductGroupMapping[] { externalProductGroup.getProductGroupMapping() };
		}
		return new ExternalProductGroup[0];
	}

	@Override
	public Object getParent(Object element) 
	{
		if (element instanceof ProductGroupMapping)
		{
			ProductGroupMapping mapping = (ProductGroupMapping) element;
			return mapping.getExternalProductGroup();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) 
	{
		if (element instanceof ExternalProductGroup)
		{
			ExternalProductGroup externalProductGroup = (ExternalProductGroup) element;
			return externalProductGroup.getProductGroupMapping() != null;
		}
		return false;
	}

}
