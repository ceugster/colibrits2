package ch.eugster.colibri.admin.payment.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.payment.Activator;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class PaymentView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.payment.view";

	private TreeViewer viewer;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent)
	{
		this.viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		this.viewer.setContentProvider(new PaymentContentProvider());
		this.viewer.setLabelProvider(new PaymentLabelProvider());
		this.viewer.setSorter(new PaymentViewerSorter());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.addDoubleClickListener(this);

		this.createContextMenu();

		this.getSite().setSelectionProvider(this.viewer);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				final UIJob job = new UIJob("Lade Zahlungsarten...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						PaymentView.this.viewer.setInput(service);
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
				return service;
			}

			@Override
			public void modifiedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.modifiedService(reference, service);
				final UIJob job = new UIJob("Lade Zahlungsarten...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						PaymentView.this.viewer.setInput(service);
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
			}

			@Override
			public void removedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.removedService(reference, service);
				final UIJob job = new UIJob("Lade Zahlungsarten...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						if (PaymentView.this.viewer.getContentProvider() != null)
						{
							PaymentView.this.viewer.setInput(PaymentView.this.viewer);
						}
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
			}
		};
		this.persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		EntityMediator.removeListener(PaymentType.class, this);
		EntityMediator.removeListener(Money.class, this);
		super.dispose();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event)
	{
		final ISelection selection = event.getSelection();
		final Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof PaymentTypeGroup)
		{
			this.viewer.setExpandedState(object, true);
		}
		else if (object instanceof PaymentType)
		{
			Activator.getDefault().editPaymentType((PaymentType) object);
		}
		else if (object instanceof Money)
		{
			Activator.getDefault().editMoney((Money) object);
		}

	}

	public TreeViewer getViewer()
	{
		return this.viewer;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
		EntityMediator.addListener(PaymentType.class, this);
		EntityMediator.addListener(Money.class, this);
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof PaymentType)
		{
			final PaymentType paymentType = (PaymentType) entity;
			UIJob job = new UIJob("Updating View...")
			{

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					viewer.remove(paymentType.getPaymentTypeGroup(), new PaymentType[] { paymentType });
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof Money)
		{
			final Money money = (Money) entity;
			UIJob job = new UIJob("Updating View...")
			{

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					viewer.remove(money.getPaymentType(), new Money[] { money });
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		if (entity instanceof PaymentType)
		{
			UIJob job = new UIJob("Updating View...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					final PaymentType paymentType = (PaymentType) entity;
					final PaymentTypeGroup group = paymentType.getPaymentTypeGroup();
					if (!group.getPaymentTypes().contains(paymentType))
					{
						group.getPaymentTypes().add(paymentType);
					}
					viewer.refresh(group);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof Money)
		{
			UIJob job = new UIJob("Updating View...")
			{

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					final Money money = (Money) entity;
					final PaymentType paymentType = money.getPaymentType();
					if (!paymentType.getMoneys().contains(money))
					{
						paymentType.addMoney(money);
					}
					viewer.refresh(paymentType);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		UIJob job = new UIJob("Updating View...")
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) 
			{
				viewer.refresh(entity);
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		this.viewer.getControl().setFocus();
	}

	private void createContextMenu()
	{
		final MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		final Menu menu = menuManager.createContextMenu(this.viewer.getControl());
		this.viewer.getControl().setMenu(menu);

		this.getSite().registerContextMenu(menuManager, this.viewer);
	}
}