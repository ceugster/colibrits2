package ch.eugster.colibri.admin.common.settings.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.common.settings.Activator;
import ch.eugster.colibri.admin.common.settings.editors.GeneralSettingsEditor;
import ch.eugster.colibri.admin.common.settings.editors.GeneralSettingsEditorInput;
import ch.eugster.colibri.admin.common.settings.views.CommonSettingsContentProvider.GeneralSettingsParent;
import ch.eugster.colibri.admin.common.settings.views.CommonSettingsContentProvider.ProviderPropertyParent;
import ch.eugster.colibri.admin.provider.editors.ProviderPropertiesEditor;
import ch.eugster.colibri.admin.provider.editors.ProviderPropertiesEditorInput;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderConfigurator;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class CommonSettingsView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.common.settings.view";

	private TreeViewer viewer;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderConfigurator, ProviderConfigurator> providerConfiguratorTracker;

	public CommonSettingsView()
	{
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		final Tree tree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setHeaderVisible(false);

		this.viewer = new TreeViewer(tree);
		this.viewer.setContentProvider(new CommonSettingsContentProvider());
		this.viewer.setLabelProvider(new CommonSettingsLabelProvider());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });

		this.createContextMenu(this.viewer);
		this.viewer.addDoubleClickListener(this);

		this.getSite().setSelectionProvider(this.viewer);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				CommonSettingsView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						CommonSettingsView.this.viewer.setInput(service);
					}
				});
				return service;
			}

			@Override
			public void modifiedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.modifiedService(reference, service);
				CommonSettingsView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						CommonSettingsView.this.viewer.setInput(service);
					}
				});
			}

			@Override
			public void removedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.removedService(reference, service);
				CommonSettingsView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						if (CommonSettingsView.this.viewer.getContentProvider() != null)
						{
							CommonSettingsView.this.viewer.setInput(CommonSettingsView.this.viewer);
						}
					}
				});
			}
		};
		this.persistenceServiceTracker.open();

		this.providerConfiguratorTracker = new ServiceTracker<ProviderConfigurator, ProviderConfigurator>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderConfigurator.class, null)
		{
			@Override
			public ProviderConfigurator addingService(final ServiceReference<ProviderConfigurator> reference)
			{
				final ProviderConfigurator service = (ProviderConfigurator) super.addingService(reference);
				CommonSettingsView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						CommonSettingsView.this.viewer.setInput(service);
					}
				});
				return service;
			}

			@Override
			public void modifiedService(final ServiceReference<ProviderConfigurator> reference, final ProviderConfigurator service)
			{
				super.modifiedService(reference, service);
				CommonSettingsView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						CommonSettingsView.this.viewer.setInput(service);
					}
				});
			}

			@Override
			public void removedService(final ServiceReference<ProviderConfigurator> reference, final ProviderConfigurator service)
			{
				super.removedService(reference, service);
				CommonSettingsView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						if (CommonSettingsView.this.viewer.getContentProvider() != null)
						{
							CommonSettingsView.this.viewer.setInput(CommonSettingsView.this.viewer);
						}
					}
				});
			}
		};
		this.providerConfiguratorTracker.open();
	}

	@Override
	public void dispose()
	{
		this.providerConfiguratorTracker.close();
		this.persistenceServiceTracker.close();
		super.dispose();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event)
	{
		final ISelection selection = event.getSelection();
		final Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof ProviderPropertyParent)
		{
			this.editProviderProperties();
		}
		else if (object instanceof GeneralSettingsParent)
		{
			this.editGeneralSettings();
		}
	}

	public void editGeneralSettings()
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			CommonSettings settings = (CommonSettings) persistenceService.getServerService().find(CommonSettings.class,
					Long.valueOf(1L));
			if (settings == null)
			{
				settings = CommonSettings.newInstance();
			}
			try
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.openEditor(new GeneralSettingsEditorInput(settings), GeneralSettingsEditor.ID, true);
			}
			catch (final PartInitException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void editProviderProperties()
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ProviderConfigurator configurator = (ProviderConfigurator) this.providerConfiguratorTracker
					.getService();
			if (configurator == null)
			{
				final MessageDialog dialog = new MessageDialog(this.getSite().getShell(), "Warenbewirtschaftung", null,
						"Die Kasse hat keine Anbindung an eine Warenbewirtschaftung.", MessageDialog.INFORMATION,
						new String[] { "OK" }, 0);
				dialog.open();
			}
			else
			{
				try
				{
					PlatformUI
							.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.openEditor(new ProviderPropertiesEditorInput(persistenceService, configurator),
									ProviderPropertiesEditor.ID, true);
				}
				catch (final PartInitException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
	}

	@Override
	public void setFocus()
	{
		this.viewer.getTree().setFocus();
	}

}
