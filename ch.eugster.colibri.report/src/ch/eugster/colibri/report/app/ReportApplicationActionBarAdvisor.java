package ch.eugster.colibri.report.app;

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

import ch.eugster.colibri.report.Activator;

public class ReportApplicationActionBarAdvisor extends ActionBarAdvisor implements EventHandler
{
	public static final String CONNECTION_STATUS = "ch.eugster.report.statusline.connection";

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

	public ReportApplicationActionBarAdvisor(final IActionBarConfigurer configurer)
	{
		super(configurer);

		final String[] topics = new String[1];
		topics[0] = "ch/eugster/colibri/persistence/server/database";

		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);

	}

	@Override
	public void dispose()
	{
		this.eventHandlerServiceRegistration.unregister();
		super.dispose();
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (this.connectionInformation != null)
		{
			if (event.getTopic().equals("ch/eugster/colibri/persistence/server/database"))
			{
				if (event.getProperty("status") instanceof Integer)
				{
					final Integer status = (Integer) event.getProperty("status");
					if (status.intValue() == IStatus.OK)
					{
						final UIJob uiJob = new UIJob("send message")
						{
							@Override
							public IStatus runInUIThread(final IProgressMonitor monitor)
							{
								final Image image = Activator.getDefault().getImageRegistry().get("OK");
								ReportApplicationActionBarAdvisor.this.connectionInformation.setImage(image);
								ReportApplicationActionBarAdvisor.this.connectionInformation
										.setText("Verbindung hergestellt.");
								ReportApplicationActionBarAdvisor.this.connectionInformation.setErrorImage(null);
								ReportApplicationActionBarAdvisor.this.connectionInformation.setErrorText(null);
								return Status.OK_STATUS;
							}
						};
						uiJob.schedule();
					}
					else if (status.intValue() == IStatus.CANCEL)
					{
						final UIJob uiJob = new UIJob("send message")
						{
							@Override
							public IStatus runInUIThread(final IProgressMonitor monitor)
							{
								final Image image = Activator.getDefault().getImageRegistry().get("EXCLAMATION");
								ReportApplicationActionBarAdvisor.this.connectionInformation.setImage(image);
								ReportApplicationActionBarAdvisor.this.connectionInformation
										.setText("Keine Verbindung.");
								ReportApplicationActionBarAdvisor.this.connectionInformation.setErrorImage(null);
								ReportApplicationActionBarAdvisor.this.connectionInformation.setErrorText(null);
								return Status.OK_STATUS;
							}
						};
						uiJob.schedule();
					}
					else if (status.intValue() == IStatus.ERROR)
					{
						final UIJob uiJob = new UIJob("send message")
						{
							@Override
							public IStatus runInUIThread(final IProgressMonitor monitor)
							{
								final Image image = Activator.getDefault().getImageRegistry().get("ERROR");
								ReportApplicationActionBarAdvisor.this.connectionInformation.setErrorImage(image);
								ReportApplicationActionBarAdvisor.this.connectionInformation
										.setErrorText("Verbindungsfehler");
								ReportApplicationActionBarAdvisor.this.connectionInformation
										.setText("Verbindungsfehler");
								return Status.OK_STATUS;
							}
						};
						uiJob.schedule();
					}
				}
			}
		}
	}

	protected MenuManager createActionMenu()
	{
		final MenuManager actionMenu = new MenuManager("&Aktionen", "ch.eugster.colibri.report.menu.actions");
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
//		final MenuManager perspectiveMenu = new MenuManager("&Perspektiven", "ch.eugster.colibri.report.menu.perspectives");
//		perspectiveMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
////		perspectiveMenu.add(new PerspectiveSwitcherMenu("ch.eugster.colibri.report."));
//		return perspectiveMenu;
//	}

	protected MenuManager createViewMenu()
	{
		final MenuManager viewMenu = new MenuManager("&Sichten", "ch.eugster.colibri.report.menu.views");
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
		this.connectionInformation = new StatusLineContributionItem(ReportApplicationActionBarAdvisor.CONNECTION_STATUS);
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
		this.quitAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("quit"));
		this.register(this.quitAction);
	}

	// private IToolBarManager createEditToolBar()
	// {
	// final IToolBarManager toolBar = new ToolBarManager(SWT.FLAT);
	// toolBar.add(this.cutAction);
	// toolBar.add(this.copyAction);
	// toolBar.add(this.pasteAction);
	// toolBar.add(this.selectAllAction);
	// return toolBar;
	// }

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
}
