/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.payment.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.payment.Activator;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class PaymentContentProvider implements ITreeContentProvider
{
	@Override
	public Object[] getChildren(final Object parent)
	{
		if (parent instanceof PersistenceService)
		{
			PaymentTypeGroup[] paymentTypeGroups = PaymentTypeGroup.values();
			ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
					PersistenceService.class, null);
			tracker.open();
			try
			{
				final PersistenceService persistenceService = (PersistenceService) tracker.getService();
				if (persistenceService != null)
				{
					for (PaymentTypeGroup paymentTypeGroup : paymentTypeGroups)
					{
						final PaymentTypeQuery query = (PaymentTypeQuery) persistenceService.getServerService().getQuery(PaymentType.class);
						paymentTypeGroup.setPaymentTypes(query.selectByGroup(paymentTypeGroup));
					}
				}
			}
			finally
			{
				tracker.close();
			}
			return paymentTypeGroups;
		}
		else if (parent instanceof PaymentTypeGroup)
		{
			PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) parent;
			return paymentTypeGroup.getPaymentTypes().toArray(new PaymentType[0]);
		}
		else if (parent instanceof PaymentType)
		{
			final PaymentType paymentType = (PaymentType) parent;
			return paymentType.getMoneys().toArray(new Money[0]);
		}

		return PaymentTypeGroup.values();
	}

	@Override
	public Object[] getElements(final Object element)
	{
		return this.getChildren(element);
	}

	@Override
	public Object getParent(final Object child)
	{
		if (child instanceof PaymentType)
		{
			final PaymentType paymentType = (PaymentType) child;
			return paymentType.getPaymentTypeGroup();
		}
		if (child instanceof Money)
		{
			final Money money = (Money) child;
			return money.getPaymentType();
		}

		return null;
	}

	@Override
	public boolean hasChildren(final Object parent)
	{
		if (parent instanceof PersistenceService)
		{
			return PaymentTypeGroup.values().length > 0;
		}
		else if (parent instanceof PaymentTypeGroup)
		{
			PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) parent;
			return paymentTypeGroup.getPaymentTypes().size() > 0;
		}
		else if (parent instanceof PaymentType)
		{
			return ((PaymentType) parent).getMoneys().size() > 0;
		}

		return false;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
