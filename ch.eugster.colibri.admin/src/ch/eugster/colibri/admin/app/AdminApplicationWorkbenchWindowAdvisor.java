package ch.eugster.colibri.admin.app;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.framework.Bundle;

import ch.eugster.colibri.admin.Activator;

public class AdminApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
	public static final String TITLE = "ColibriTS II Administrator";

	private IDialogSettings settings;

	public AdminApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
//		configurer.getWindow().addPerspectiveListener(this);
		settings = Activator.getDefault().getDialogSettings().getSection("admin.start");
		if (settings == null)
		{
			settings = Activator.getDefault().getDialogSettings().addNewSection("admin.start");
		}
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer)
	{
		return new AdminApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen()
	{
		this.getWindowConfigurer().setShowCoolBar(true);
		this.getWindowConfigurer().setShowPerspectiveBar(false);
	}

	@Override
	public void postWindowOpen()
	{
		super.postWindowOpen();

		this.getWindowConfigurer().setTitle(prepareTitle());

		IActionBarConfigurer configurer = getWindowConfigurer().getActionBarConfigurer();
		/* deletes unwanted Contribution from Toolbar */
		IContributionItem[] coolItems = configurer.getCoolBarManager().getItems();
		for (int i = 0; i < coolItems.length; i++)
		{
			if (coolItems[i].getId() != null && !coolItems[i].getId().startsWith("ch.eugster.colibri.admin."))
			{
				configurer.getCoolBarManager().remove(coolItems[i]);
			}
		}
		// deletes unwanted Menuitems
		IContributionItem[] menuItems = configurer.getMenuManager().getItems();
		for (IContributionItem menuItem : menuItems)
		{
			if (menuItem.getId().equals(IWorkbenchActionConstants.M_FILE))
			{
				if (menuItem instanceof IMenuManager)
				{
					IMenuManager manager = (IMenuManager) menuItem;
					IContributionItem[] items = manager.getItems();
					for (IContributionItem item : items)
					{
						if (!item.getId().startsWith("ch.eugster.colibri.admin."))
						{
							if (!item.getId().startsWith("ch.eugster.colibri.persistence."))
							{
								if (!item.getId().startsWith("additions"))
								{
									if (!item.getId().startsWith("quit"))
									{
										manager.remove(item);
									}
								}
							}
						}
					}
				}
			}
			else if (menuItem.getId().equals(IWorkbenchActionConstants.M_WINDOW))
			{
				if (menuItem instanceof IMenuManager)
				{
					IMenuManager manager = (IMenuManager) menuItem;
					IContributionItem[] items = manager.getItems();
					for (IContributionItem item : items)
					{
						if (!item.getId().startsWith("ch.eugster.colibri.admin."))
						{
							if (!item.getId().startsWith("additions"))
							{
								if (!item.getId().startsWith("preferences"))
								{
									manager.remove(item);
								}
							}
						}
					}
				}
			}
			else if (menuItem.getId().equals(IWorkbenchActionConstants.M_HELP))
			{
				if (menuItem instanceof IMenuManager)
				{
					IMenuManager manager = (IMenuManager) menuItem;
					IContributionItem[] items = manager.getItems();
					for (IContributionItem item : items)
					{
						if (!item.getId().startsWith("ch.eugster.colibri.admin."))
						{
							if (!item.getId().startsWith("additions"))
							{
								if (!item.getId().startsWith("org.eclipse.equinox.p2."))
								{
									if (!item.getId().equals("about"))
									{
										manager.remove(item);
									}
								}
							}
						}
					}
				}
			}
			else if (menuItem.getId().equals(IWorkbenchActionConstants.MB_ADDITIONS))
			{
			}
			else if (menuItem.getId().startsWith("ch.eugster.colibri.admin."))
			{
			}
			else
			{
				configurer.getMenuManager().remove(menuItem);
			}
		}
		configurer.getCoolBarManager().update(true);
		configurer.getMenuManager().update(true);

		// deletes unwanted Preferences
		PreferenceManager pm = getWindowConfigurer().getWindow().getWorkbench().getPreferenceManager();
		IPreferenceNode[] prefNodes = pm.getRootSubNodes();
		for (IPreferenceNode node : prefNodes)
		{
			if (true)
			{
			}
			if (!node.getId().startsWith("org.eclipse.equinox.internal.p2.ui."))
			{
				pm.remove(node);
			}
		}

		if (this.getWindowConfigurer().getWindow().getActivePage() != null)
		{
			String perspectiveId = this.getWindowConfigurer().getWindow().getActivePage().getPerspective().getId();
			if (!perspectiveId.startsWith("ch.eugster.colibri.admin."))
			{
				perspectiveId = settings.get("current.perspective");
				if (perspectiveId == null)
				{
					perspectiveId = "ch.eugster.colibri.admin.salespoint.perspective";
				}

				try
				{
					PlatformUI.getWorkbench().showPerspective(perspectiveId,
							PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				}
				catch (WorkbenchException e)
				{
					e.printStackTrace();
				}
				settings.put("current.perspective", perspectiveId);
			}
		}
	}

	public String prepareTitle()
	{
		Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
		for (Bundle bundle : bundles)
		{
			if (bundle.getSymbolicName().equals("ch.eugster.colibri.product"))
			{
				String version = bundle.getHeaders().get("Bundle-Version");
				return TITLE + " v" + version == null ? "???" : version;
			}
		}
		return TITLE;
	}

}
