/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ProductContentProvider implements ITreeContentProvider
{
	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public ProductContentProvider()
	{
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	@Override
	public Object[] getChildren(final Object parent)
	{
		if (parent instanceof PersistenceService)
		{
			ProductGroupGroup[] productGroupGroups = ProductGroupGroup.values();
			final PersistenceService persistenceService = (PersistenceService) persistenceServiceTracker.getService();
			if (persistenceService != null)
			{
				final ProductGroupQuery query = (ProductGroupQuery) persistenceService.getServerService().getQuery(ProductGroup.class);
				for (ProductGroupGroup productGroupGroup : productGroupGroups)
				{
					ProductGroupType[] productGroupTypes = productGroupGroup.getChildren();
					for (ProductGroupType productGroupType : productGroupTypes)
					{
						productGroupType.setChildren(query.selectByProductGroupType(productGroupType));
					}
				}
			}
			return productGroupGroups;
		}
		if (parent instanceof ProductGroupGroup)
		{
			return ((ProductGroupGroup) parent).getChildren();
		}
		if (parent instanceof ProductGroupType)
		{
			final ProductGroupType productGroupType = (ProductGroupType) parent;
			return productGroupType.getChildren().toArray(new ProductGroup[0]);
		}
		return new ProductGroup[0];
	}

	@Override
	public Object[] getElements(final Object element)
	{
		return this.getChildren(element);
	}

	@Override
	public Object getParent(final Object child)
	{
		if (child instanceof ProductGroupType)
		{
			return ((ProductGroupType) child).getParent();
		}
		if (child instanceof ProductGroup)
		{
			final ProductGroup productGroup = (ProductGroup) child;
			return productGroup.getProductGroupType();
		}

		return null;
	}

	@Override
	public boolean hasChildren(final Object parent)
	{
		if (parent instanceof PersistenceService)
		{
			return ProductGroupGroup.values().length > 0;
		}
		else if (parent instanceof ProductGroupGroup)
		{
			return ((ProductGroupGroup) parent).getChildren().length > 0;
		}
		else if (parent instanceof ProductGroupType)
		{
			ProductGroupType productGroupType = (ProductGroupType) parent;
			return productGroupType.getChildren().size() > 0;
		}
		return false;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

}
