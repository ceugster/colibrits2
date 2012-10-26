package ch.eugster.colibri.admin.product.dnd;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.ServerService;

public class ProductGroupViewerDropAdapter extends ViewerDropAdapter
{
	private ProductGroupType target;
	
	public ProductGroupViewerDropAdapter(final Viewer viewer)
	{
		super(viewer);
	}

	private boolean applyFilter(final Object object)
	{
		if (object instanceof ProductGroup)
		{
			ProductGroup productGroup = (ProductGroup) object;
			return !productGroup.isDeleted();
		}
		return false;
	}

	private ProductGroup updateProductGroup(ProductGroup productGroup, ProductGroupType productGroupType)
	{
		productGroup.getProductGroupType().removeChild(productGroup);
		productGroup.setProductGroupType(productGroupType);
		productGroupType.addChild(productGroup);
		ProductGroupGroup group = productGroup.getProductGroupType().getParent();
		if (group.equals(ProductGroupGroup.INTERNAL))
		{
			productGroup.setDefaultTax(null);
		}
		else
		{
			Tax taxToReturn = null;
			Collection<Tax> taxes = getTaxes(productGroupType);
			for (Tax tax : taxes)
			{
				if (taxToReturn == null)
				{
					taxToReturn = tax;
				}
				else
				{
					if (taxToReturn.getCurrentTax().getPercentage() < tax.getCurrentTax().getPercentage())
					{
						taxToReturn = tax;
					}
				}
			}
			productGroup.setDefaultTax(taxToReturn);
		}
		return productGroup;
	}
	
	private Collection<Tax> getTaxes(ProductGroupType productGroupType)
	{
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = (PersistenceService) tracker.getService();
			if (service != null)
			{
				ServerService server = (ServerService) service.getServerService();
				TaxTypeQuery query = (TaxTypeQuery) server.getQuery(TaxType.class);
				return query.selectTaxes(productGroupType);
			}
		}
		finally
		{
			tracker.close();
		}
		return null;
	}
	
	@Override
	public boolean performDrop(final Object data)
	{
		if (this.target instanceof ProductGroupType)
		{
			if (data instanceof ProductGroup[])
			{
				ProductGroup[] productGroups = (ProductGroup[]) data;
				for (ProductGroup productGroup : productGroups)
				{
					if (applyFilter(productGroup))
					{
						if (!productGroup.getProductGroupType().equals(target))
						{
							productGroup = updateProductGroup(productGroup, target);
							store(productGroup);
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	private void store(ProductGroup productGroup)
	{
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = (PersistenceService) tracker.getService();
			if (service != null)
			{
				ServerService server = (ServerService) service.getServerService();
				server.merge(productGroup);
			}
		}
		finally
		{
			tracker.close();
		}
	}
	
	@Override
	public boolean validateDrop(final Object target, final int operation, final TransferData transferType)
	{
		if (ProductGroupTransfer.getTransfer().isSupportedType(transferType))
		{
			if (target instanceof ProductGroupType)
			{
				ProductGroup[] productGroups = ProductGroupTransfer.getTransfer().getData();
				if (productGroups.length == 0)
				{
					return false;
				}
				else
				{
					for (ProductGroup productGroup : productGroups)
					{
						if (productGroup.getProductGroupType().equals(target)) {
							return false;
						}
					}
				}
				this.target = (ProductGroupType) target;
				return true;
			}
		}
		this.target = null;
		return false;
	}
}
