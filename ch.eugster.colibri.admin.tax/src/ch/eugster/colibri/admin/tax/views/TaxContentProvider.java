/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.tax.TaxActivator;
import ch.eugster.colibri.admin.tax.views.TaxView.Mode;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TaxContentProvider implements ITreeContentProvider
{
	private TreeViewer viewer;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public TaxContentProvider(final TreeViewer viewer)
	{
		this.viewer = viewer;

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(TaxActivator.getDefault().getBundle().getBundleContext(),
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
		if (parent instanceof Mode)
		{
			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
			if (persistenceService != null)
			{
				final Mode mode = (Mode) parent;
				if (mode.equals(Mode.GROUP))
				{
					final TaxRateQuery query = (TaxRateQuery) persistenceService.getServerService().getQuery(TaxRate.class);
					return query.selectAll(false).toArray(new TaxRate[0]);
				}
				else if (mode.equals(Mode.TYPE))
				{
					final TaxTypeQuery query = (TaxTypeQuery) persistenceService.getServerService().getQuery(TaxType.class);
					return query.selectAll(false).toArray(new TaxType[0]);
				}
			}
			return new Tax[0];
		}
		else if (parent instanceof TaxRate)
		{
			final TaxRate taxRate = (TaxRate) parent;
			return taxRate.getTaxes().toArray(new Tax[0]);
		}
		else if (parent instanceof TaxType)
		{
			final TaxType taxType = (TaxType) parent;
			return taxType.getTaxes().toArray(new Tax[0]);
		}
		else if (parent instanceof Tax)
		{
			final Tax tax = (Tax) parent;
			return tax.getCurrentTaxes().toArray(new CurrentTax[0]);
		}

		return new Tax[0];
	}

	@Override
	public Object[] getElements(final Object element)
	{
		return this.getChildren(element);
	}

	@Override
	public Object getParent(final Object child)
	{
		if (child instanceof CurrentTax)
		{
			final CurrentTax currentTax = (CurrentTax) child;
			return currentTax.getTax();
		}
		else if (child instanceof Tax)
		{
			final Tax tax = (Tax) child;
			if (this.viewer.getInput() instanceof Mode)
			{
				final Mode mode = (Mode) this.viewer.getInput();
				if (mode.equals(Mode.GROUP))
				{
					return tax.getTaxRate();
				}
				else if (mode.equals(Mode.TYPE))
				{
					return tax.getTaxType();
				}
			}
			return null;
		}

		return null;
	}

	@Override
	public boolean hasChildren(final Object parent)
	{
		if (parent instanceof Mode)
		{
			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
			if (persistenceService != null)
			{
				final Mode mode = (Mode) parent;
				if (mode.equals(Mode.GROUP))
				{
					final TaxRateQuery query = (TaxRateQuery) persistenceService.getServerService().getQuery(TaxRate.class);
					return query.selectAll(false).size() > 0;
				}
				else if (mode.equals(Mode.TYPE))
				{
					final TaxTypeQuery query = (TaxTypeQuery) persistenceService.getServerService().getQuery(TaxType.class);
					return query.selectAll(false).size() > 0;
				}
			}
			return false;
		}
		else if (parent instanceof TaxRate)
		{
			final TaxRate taxRate = (TaxRate) parent;
			return taxRate.getTaxes().size() > 0;
		}
		else if (parent instanceof TaxType)
		{
			final TaxType taxType = (TaxType) parent;
			return taxType.getTaxes().size() > 0;
		}
		else if (parent instanceof Tax)
		{
			final Tax tax = (Tax) parent;
			return tax.getCurrentTaxes().size() > 0;
		}
		return false;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}
}
