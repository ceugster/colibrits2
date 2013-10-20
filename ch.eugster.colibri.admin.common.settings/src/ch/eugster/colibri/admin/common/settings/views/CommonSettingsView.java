package ch.eugster.colibri.admin.common.settings.views;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
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
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.common.settings.Activator;
import ch.eugster.colibri.admin.common.settings.editors.GeneralSettingsEditor;
import ch.eugster.colibri.admin.common.settings.editors.GeneralSettingsEditorInput;
import ch.eugster.colibri.admin.common.settings.views.CommonSettingsContentProvider.GeneralSettingsParent;
import ch.eugster.colibri.admin.provider.editors.ProviderPropertiesEditor;
import ch.eugster.colibri.admin.provider.editors.ProviderPropertiesEditorInput;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderConfigurator;
import ch.eugster.colibri.provider.service.ProviderUpdater;
import ch.eugster.colibri.provider.voucher.VoucherService;
import ch.eugster.colibri.scheduler.service.UpdateScheduler;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class CommonSettingsView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.common.settings.view";

	private TreeViewer viewer;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderConfigurator, ProviderConfigurator> providerConfiguratorTracker;

	private ServiceTracker<VoucherService, VoucherService> voucherServiceTracker;

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
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.providerConfiguratorTracker = new ServiceTracker<ProviderConfigurator, ProviderConfigurator>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderConfigurator.class, null);
		this.providerConfiguratorTracker.open();

		this.voucherServiceTracker = new ServiceTracker<VoucherService, VoucherService>(Activator.getDefault().getBundle().getBundleContext(),
				VoucherService.class, null);
		this.voucherServiceTracker.open();
		
		viewer.setInput(viewer);
	}

	@Override
	public void dispose()
	{
		this.voucherServiceTracker.close();
		this.providerConfiguratorTracker.close();
		this.persistenceServiceTracker.close();
		super.dispose();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event)
	{
		final ISelection selection = event.getSelection();
		final Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof ProviderUpdater)
		{
			ProviderUpdater updater = (ProviderUpdater) object;
			this.editProviderProperties(updater);
		}
		else if (object instanceof GeneralSettingsParent)
		{
			this.editGeneralSettings();
		}
		else if (object instanceof UpdateScheduler)
		{
			UpdateScheduler scheduler = (UpdateScheduler) object;
			this.editSchedulerSettings(scheduler);
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

	public void editSchedulerSettings(UpdateScheduler scheduler)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			if (persistenceService.getServerService().isConnected())
			{
				CommonSettings settings = (CommonSettings) persistenceService.getServerService().find(CommonSettings.class,
						Long.valueOf(1L));
				if (settings != null)
				{
					try
					{
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.openEditor(new ProviderPropertiesEditorInput(persistenceService, scheduler), ProviderPropertiesEditor.ID, true);
						return;
					}
					catch (final PartInitException e)
					{
						IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
						ErrorDialog.openError(this.getSite().getShell(), "Fehler", "Der Editor konnte nicht geöffnet werden.", status);
					}
				}
			}
			else
			{
				MessageDialog.open(MessageDialog.ERROR, this.getSite().getShell(), "Verbindungsfehler", "Es besteht keine Verbindung zum Datenbankserver.", SWT.None);
			}
		}
	}

	public void editProviderProperties(ProviderUpdater providerUpdater)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			try
			{
				PlatformUI
						.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.openEditor(new ProviderPropertiesEditorInput(persistenceService, providerUpdater),
								ProviderPropertiesEditor.ID, true);
			}
			catch (final PartInitException e)
			{
				e.printStackTrace();
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
