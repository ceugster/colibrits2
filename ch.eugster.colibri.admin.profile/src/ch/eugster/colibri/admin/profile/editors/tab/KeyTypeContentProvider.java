/*
 * Created on 12.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class KeyTypeContentProvider implements ITreeContentProvider
{
	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private TaxRate[] taxRates;

	public KeyTypeContentProvider()
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
	public Object[] getChildren(final Object parentElement)
	{
		if (parentElement instanceof PersistenceService)
		{
			PersistenceService service = (PersistenceService) parentElement;
			KeyType[] keyTypes = KeyType.values();
			for (KeyType keyType : keyTypes)
			{
				if (keyType.equals(KeyType.PRODUCT_GROUP))
				{
					for (ProductGroupGroup productGroupGroup : ProductGroupGroup.values())
					{
						for (ProductGroupType productGroupType : productGroupGroup.getChildren())
						{
							ProductGroupQuery query = (ProductGroupQuery) service.getServerService().getQuery(ProductGroup.class);
							productGroupType.setChildren(query.selectByProductGroupType(productGroupType));
						}
					}
				}
				else if (keyType.equals(KeyType.PAYMENT_TYPE))
				{
					for (PaymentTypeGroup paymentTypeGroup : PaymentTypeGroup.values())
					{
						PaymentTypeQuery query = (PaymentTypeQuery) service.getServerService().getQuery(PaymentType.class);
						paymentTypeGroup.setPaymentTypes(query.selectByGroup(paymentTypeGroup));
					}
				}
				else if (keyType.equals(KeyType.TAX_RATE))
				{
					final TaxRateQuery queryService = (TaxRateQuery) service.getServerService().getQuery(TaxRate.class);
					taxRates = queryService.selectAll(false).toArray(new TaxRate[0]);
				}
			}
			return KeyType.values();
		}
		else if (parentElement instanceof KeyType)
		{
			KeyType keyType = (KeyType) parentElement;
			switch(keyType)
			{
			case PRODUCT_GROUP:
			{
				return ProductGroupGroup.values();
			}
			case PAYMENT_TYPE:
			{
				return PaymentTypeGroup.values();
			}
			case TAX_RATE:
			{
				return taxRates;
			}
			case OPTION:
			{
				return Option.values();
			}
			case FUNCTION:
			{
				return FunctionType.values();
			}
			}
		}
		else if (parentElement instanceof ProductGroupGroup)
		{
			return ((ProductGroupGroup) parentElement).getChildren();
		}
		else if (parentElement instanceof ProductGroupType)
		{
			return ((ProductGroupType) parentElement).getChildren().toArray(new ProductGroup[0]);
		}
		else if (parentElement instanceof PaymentTypeGroup)
		{
			return ((PaymentTypeGroup) parentElement).getPaymentTypes().toArray(new PaymentType[0]);
		}
//		else if (parentElement instanceof TaxRate)
//		{
//			return ((TaxRate) parentElement).getTaxes().toArray(new Tax[0]);
//		}
		return new Object[0];
	}

	@Override
	public Object[] getElements(final Object inputElement)
	{
		return this.getChildren(inputElement);
	}

	@Override
	public Object getParent(final Object element)
	{
		if (element instanceof ProductGroup)
		{
			return ((ProductGroup) element).getProductGroupType();
		}
		else if (element instanceof ProductGroupType)
		{
			return ((ProductGroupType) element).getParent();
		}
		else if (element instanceof ProductGroupGroup)
		{
			return KeyType.PRODUCT_GROUP;
		}
		else if (element instanceof PaymentType)
		{
			return ((PaymentType) element).getPaymentTypeGroup();
		}
		else if (element instanceof PaymentTypeGroup)
		{
			return KeyType.PAYMENT_TYPE;
		}
		else if (element instanceof TaxRate)
		{
			return KeyType.TAX_RATE;
		}
		else if (element instanceof Option)
		{
			return KeyType.OPTION;
		}
		else if (element instanceof FunctionType)
		{
			return KeyType.FUNCTION;
		}
		return null;
	}

	@Override
	public boolean hasChildren(final Object element)
	{
		if (element instanceof PersistenceService)
		{
			return KeyType.values().length > 0;
		}
		else if (element instanceof KeyType)
		{
			final KeyType keyType = (KeyType) element;
			if (keyType.equals(KeyType.PRODUCT_GROUP))
			{
				return ProductGroupGroup.values().length > 0;
			}
			else if (keyType.equals(KeyType.PAYMENT_TYPE))
			{
				return PaymentTypeGroup.values().length > 0;
			}
			else if (keyType.equals(KeyType.TAX_RATE))
			{
				return taxRates == null ? false : taxRates.length > 0;
			}
			else if (keyType.equals(KeyType.OPTION))
			{
				return Position.Option.values().length > 0;
			}
			else if (keyType.equals(KeyType.FUNCTION))
			{
				return FunctionType.values().length > 0;
			}
		}
		else if (element instanceof ProductGroupGroup)
		{
			return ((ProductGroupGroup) element).getChildren().length > 0;
		}
		else if (element instanceof ProductGroupType)
		{
			return ((ProductGroupType) element).getChildren().size() > 0;
		}
		else if (element instanceof PaymentTypeGroup)
		{
			return ((PaymentTypeGroup) element).getPaymentTypes().size() > 0;
		}
//		else if (element instanceof TaxRate)
//		{
//			TaxRate taxRate = (TaxRate) element;
//			return taxRate.getTaxes().size() > 0;
//		}
		return false;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}
}
