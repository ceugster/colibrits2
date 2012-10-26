/*
 * Created on 14.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class ProfileView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.profile.view";

	private TreeViewer viewer;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public ProfileView()
	{
		EntityMediator.addListener(Profile.class, this);
		EntityMediator.addListener(Configurable.class, this);
		EntityMediator.addListener(Tab.class, this);
		EntityMediator.addListener(Key.class, this);
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		this.viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		this.viewer.setContentProvider(new ProfileContentProvider());
		this.viewer.setLabelProvider(new ProfileLabelProvider());
		this.viewer.setSorter(new ProfileSorter());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.addDoubleClickListener(this);

		this.createContextMenu();

		this.getSite().setSelectionProvider(this.viewer);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class.getName(), null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				final UIJob job = new UIJob("Lade Profile...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						ProfileView.this.viewer.setInput(service);
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
				final UIJob job = new UIJob("Lade Profile...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						ProfileView.this.viewer.setInput(service);
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
				final UIJob job = new UIJob("Entlade Profile...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						if (ProfileView.this.viewer.getContentProvider() != null)
						{
							ProfileView.this.viewer.setInput(null);
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
		EntityMediator.removeListener(Profile.class, this);
		EntityMediator.removeListener(Configurable.class, this);
		EntityMediator.removeListener(Tab.class, this);
		EntityMediator.removeListener(Key.class, this);
		super.dispose();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event)
	{
		final ISelection selection = event.getSelection();
		final Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof Profile)
		{
			final Profile profile = (Profile) object;
			Activator.getDefault().editProfile(profile);
		}
		else if (object instanceof Configurable)
		{
			final Configurable configurable = (Configurable) object;
			Activator.getDefault().editConfigurable(configurable);
		}
		else if (object instanceof Tab)
		{
			final Tab tab = (Tab) object;
			Activator.getDefault().editTab(tab);
		}
	}

	public TreeViewer getViewer()
	{
		return this.viewer;
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		UIJob job = new UIJob("refreshing viewer...")
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				viewer.remove(entity);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		if (entity instanceof Profile)
		{
			UIJob job = new UIJob("refreshing viewer...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					viewer.add(viewer, entity);
					viewer.setSelection(new StructuredSelection(new Object[] { entity }));
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
		else if (entity instanceof Configurable)
		{
			UIJob job = new UIJob("refreshing viewer...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					Configurable configurable = (Configurable) entity;
					Profile profile = configurable.getProfile();
					viewer.add(profile, configurable);
					viewer.setSelection(new StructuredSelection(new Configurable[] { configurable }));
//					viewer.expandToLevel(2);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
		else if (entity instanceof Tab)
		{
			UIJob job = new UIJob("refreshing viewer...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					Tab tab = (Tab) entity;
					Configurable configurable = tab.getConfigurable();
//					configurable.addTab(tab);
					viewer.add(configurable, tab);
					viewer.setSelection(new StructuredSelection(new Tab[] { tab }));
//					viewer.expandToLevel(3);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
//		else if (entity instanceof Key)
//		{
//			UIJob job = new UIJob("refreshing viewer...")
//			{
//				@Override
//				public IStatus runInUIThread(IProgressMonitor monitor)
//				{
//					viewer.expandToLevel(3);
//					return Status.OK_STATUS;
//				}
//			};
//			job.schedule();
//		}
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		if (entity instanceof Profile)
		{
			UIJob job = new UIJob("refreshing viewer...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					viewer.refresh(entity);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
		else if (entity instanceof Configurable)
		{
			UIJob job = new UIJob("refreshing viewer...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					Configurable configurable = (Configurable) entity;
					viewer.refresh(configurable.getProfile());
//					viewer.expandToLevel(2);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
		else if (entity instanceof Tab)
		{
			UIJob job = new UIJob("refreshing viewer...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					Tab tab = (Tab) entity;
					viewer.refresh(tab);
//					viewer.expandToLevel(3);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
//		else if (entity instanceof Key)
//		{
//			UIJob job = new UIJob("refreshing viewer...")
//			{
//				@Override
//				public IStatus runInUIThread(IProgressMonitor monitor)
//				{
//					viewer.expandToLevel(3);
//					return Status.OK_STATUS;
//				}
//			};
//			job.schedule();
//		}
	}

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
