package ch.eugster.colibri.admin.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import ch.eugster.colibri.admin.Activator;

public class AdminApplicationActionBarAdvisor extends ActionBarAdvisor implements EventHandler
{
	public static final String CONNECTION_STATUS = "ch.eugster.admin.statusline.connection";

	// File Menu
	private IWorkbenchAction quitAction;

	// Edit Menu
	private IWorkbenchAction undoAction;

	private IWorkbenchAction redoAction;

	private IWorkbenchAction cutAction;

	private IWorkbenchAction copyAction;

	private IWorkbenchAction pasteAction;

	private IWorkbenchAction deleteAction;

	private IWorkbenchAction selectAllAction;

	// Window Menu
	private IWorkbenchAction preferencesAction;

	private IWorkbenchAction aboutAction;

	private StatusLineContributionItem connectionInformation;

	private final ServiceRegistration<EventHandler> eventHandlerServiceRegistration;

	private UIJob job;
	
	public AdminApplicationActionBarAdvisor(final IActionBarConfigurer configurer)
	{
		super(configurer);

		final Collection<String> t = new ArrayList<String>();
		t.add("ch/eugster/colibri/transfer");
		final String[] topics = t.toArray(new String[t.size()]);

		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);
	}

	@Override
	public void dispose()
	{
		if (this.eventHandlerServiceRegistration != null)
		{
			this.eventHandlerServiceRegistration.unregister();
		}
		if (this.job != null)
		{
			this.job.cancel();
		}
		super.dispose();
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (this.connectionInformation != null)
		{
			if (event.getTopic().equals("ch/eugster/colibri/transfer"))
			{
				if (event.getProperty("status") instanceof IStatus)
				{
					final IStatus status = (IStatus) event.getProperty("status");
					if (status.getSeverity() == IStatus.OK)
					{
						job = new UIJob("Verbindungsinformationen aktualisieren")
						{
							@Override
							public IStatus runInUIThread(final IProgressMonitor monitor)
							{
								final Image image = Activator.getDefault().getImageRegistry().get("OK");
								AdminApplicationActionBarAdvisor.this.connectionInformation.setImage(image);
								AdminApplicationActionBarAdvisor.this.connectionInformation.setText("Verbindung: "
										+ status.getMessage());
								AdminApplicationActionBarAdvisor.this.connectionInformation.setErrorImage(null);
								AdminApplicationActionBarAdvisor.this.connectionInformation.setErrorText(null);
								return Status.OK_STATUS;
							}
						};
						job.schedule();
					}
					else if (status.getSeverity() == IStatus.CANCEL)
					{
						job = new UIJob("Verbindungsinformationen aktualisieren")
						{
							@Override
							public IStatus runInUIThread(final IProgressMonitor monitor)
							{
								final Image image = Activator.getDefault().getImageRegistry().get("EXCLAMATION");
								AdminApplicationActionBarAdvisor.this.connectionInformation.setImage(image);
								AdminApplicationActionBarAdvisor.this.connectionInformation
										.setText("Keine Verbindung.");
								AdminApplicationActionBarAdvisor.this.connectionInformation.setErrorImage(null);
								AdminApplicationActionBarAdvisor.this.connectionInformation.setErrorText(null);
								return Status.OK_STATUS;
							}
						};
						job.schedule();
					}
					else if (status.getSeverity() == IStatus.ERROR)
					{
						job = new UIJob("Verbindungsinformationen aktualisieren")
						{
							@Override
							public IStatus runInUIThread(final IProgressMonitor monitor)
							{
								final Image image = Activator.getDefault().getImageRegistry().get("ERROR");
								AdminApplicationActionBarAdvisor.this.connectionInformation.setImage(image);
								AdminApplicationActionBarAdvisor.this.connectionInformation
										.setText("Verbindungsfehler");
								AdminApplicationActionBarAdvisor.this.connectionInformation.setErrorImage(null);
								AdminApplicationActionBarAdvisor.this.connectionInformation.setErrorText(null);
								return Status.OK_STATUS;
							}
						};
						job.schedule();
					}
				}
			}
		}
	}

	protected MenuManager createActionMenu()
	{
		final MenuManager actionMenu = new MenuManager("&Aktionen", "ch.eugster.colibri.admin.menu.actions");
		actionMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		return actionMenu;
	}

	protected MenuManager createEditMenu()
	{
		final MenuManager editMenu = new MenuManager("&Bearbeiten", IWorkbenchActionConstants.M_EDIT);
		editMenu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_START));
		editMenu.add(this.undoAction);
		editMenu.add(this.redoAction);
		editMenu.add(new Separator("editCutCopyPaste"));
		editMenu.add(this.cutAction);
		editMenu.add(this.copyAction);
		editMenu.add(this.pasteAction);
		editMenu.add(new Separator("editDeleteSelectAll"));
		editMenu.add(this.deleteAction);
		editMenu.add(this.selectAllAction);
		editMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		editMenu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_END));
		return editMenu;
	}

	protected MenuManager createFileMenu()
	{
		final MenuManager fileMenu = new MenuManager("&Datei", IWorkbenchActionConstants.M_FILE);
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		fileMenu.add(this.quitAction);
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
		return fileMenu;
	}

	protected MenuManager createHelpMenu()
	{
		final MenuManager helpMenu = new MenuManager("&Hilfe", IWorkbenchActionConstants.M_HELP);
		helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		helpMenu.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
		helpMenu.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));
		helpMenu.add(this.aboutAction);
		return helpMenu;
	}

//	protected MenuManager createPerspectiveMenu()
//	{
//		final MenuManager perspective = new MenuManager("&Perspektiven", "ch.eugster.colibri.admin.menu.perspectives");
//		perspective.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
//		perspective.add(new PerspectiveSwitcherMenu("ch.eugster.colibri.admin."));
//		return perspective;
//	}

	protected MenuManager createViewMenu()
	{
		final MenuManager viewMenu = new MenuManager("&Sichten", "ch.eugster.colibri.admin.menu.views");
		viewMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		return viewMenu;
	}

	protected MenuManager createWindowMenu()
	{
		final MenuManager windowMenu = new MenuManager("&Fenster", IWorkbenchActionConstants.M_WINDOW);
		windowMenu.add(new GroupMarker(IWorkbenchActionConstants.WINDOW_EXT));
		windowMenu.add(this.preferencesAction);
		return windowMenu;
	}

	@Override
	protected void fillCoolBar(final ICoolBarManager coolBar)
	{
		coolBar.add(this.createFileToolBar());
		// coolBar.add(this.createEditToolBar());
//		coolBar.add(this.createPerspectiveSwitcherToolBar());
	}

	@Override
	protected void fillMenuBar(final IMenuManager menuBar)
	{
		menuBar.add(this.createFileMenu());
		menuBar.add(this.createEditMenu());
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(this.createActionMenu());
		menuBar.add(this.createViewMenu());
//		menuBar.add(this.createPerspectiveMenu());
		menuBar.add(this.createWindowMenu());
		menuBar.add(this.createHelpMenu());
	}

	@Override
	protected void fillStatusLine(final IStatusLineManager statusLine)
	{
		this.connectionInformation = new StatusLineContributionItem(AdminApplicationActionBarAdvisor.CONNECTION_STATUS, true, 80);
		statusLine.add(this.connectionInformation);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window)
	{
		this.makeFileActions(window);
		this.makeEditActions(window);
		this.makeWindowActions(window);
		this.makeHelpActions(window);
	}

	protected void makeEditActions(final IWorkbenchWindow window)
	{
		this.undoAction = ActionFactory.UNDO.create(window);
		this.undoAction.setText("Rückgängig");
		this.register(this.undoAction);

		this.redoAction = ActionFactory.REDO.create(window);
		this.redoAction.setText("Wiederholen");
		this.register(this.redoAction);

		this.cutAction = ActionFactory.CUT.create(window);
		this.cutAction.setText("Ausschneiden");
		this.register(this.cutAction);

		this.copyAction = ActionFactory.COPY.create(window);
		this.copyAction.setText("Kopieren");
		this.register(this.copyAction);

		this.pasteAction = ActionFactory.PASTE.create(window);
		this.pasteAction.setText("Einfügen");
		this.register(this.pasteAction);

		this.deleteAction = ActionFactory.DELETE.create(window);
		this.deleteAction.setText("Entfernen");
		this.register(this.deleteAction);

		this.selectAllAction = ActionFactory.SELECT_ALL.create(window);
		this.selectAllAction.setText("Alles auswählen");
		this.register(this.selectAllAction);
	}

	protected void makeFileActions(final IWorkbenchWindow window)
	{
		this.quitAction = ActionFactory.QUIT.create(window);
		this.quitAction.setText("Beenden");
		this.quitAction.setToolTipText("Programm beenden");
		this.quitAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("EXIT"));
		this.register(this.quitAction);
	}

	protected void makeHelpActions(final IWorkbenchWindow window)
	{
		this.aboutAction = ActionFactory.ABOUT.create(window);
		this.aboutAction.setText("Über ColibriTS...");
	}

	protected void makeWindowActions(final IWorkbenchWindow window)
	{
		this.preferencesAction = ActionFactory.PREFERENCES.create(window);
		this.preferencesAction.setText("Einstellungen");
		this.preferencesAction.setToolTipText("Programmeinstellungen");
		this.preferencesAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("PREFS"));
		this.register(this.preferencesAction);
	}

	private IToolBarManager createFileToolBar()
	{
		final IToolBarManager toolBar = new ToolBarManager(SWT.FLAT);
		toolBar.add(this.quitAction);
		return toolBar;
	}

//	private IToolBarManager createPerspectiveSwitcherToolBar()
//	{
//		final IToolBarManager toolBar = new ToolBarManager(SWT.FLAT);
//		toolBar.add(new PerspectiveSwitcherToolbar("ch.eugster.colibri.admin."));
//		return toolBar;
//	}

}
