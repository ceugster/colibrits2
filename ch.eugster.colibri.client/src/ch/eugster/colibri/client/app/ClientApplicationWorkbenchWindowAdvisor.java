package ch.eugster.colibri.client.app;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ClientApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
	public static final String TITLE = "ColibriTS II";

	public ClientApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer)
	{
		return new ClientApplicationActionBarAdvisor(configurer);
	}
	
	@Override
	public void postWindowCreate()
	{
		this.getWindowConfigurer().getWindow().getShell().setMaximized(true);
	}

	@Override
	public void postWindowOpen()
	{
		super.postWindowOpen();

		this.getWindowConfigurer().setTitle(TITLE);

		IActionBarConfigurer configurer = getWindowConfigurer().getActionBarConfigurer();
		/* deletes unwanted Contribution from Toolbar */
		IContributionItem[] coolItems = configurer.getCoolBarManager().getItems();
		for (int i = 0; i < coolItems.length; i++)
		{
			if (coolItems[i].getId() != null && !coolItems[i].getId().startsWith("ch.eugster.colibri.client."))
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
						if (!item.getId().startsWith("ch.eugster.colibri.client."))
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
						if (!item.getId().startsWith("ch.eugster.colibri.client."))
						{
							if (!item.getId().startsWith("additions"))
							{
								manager.remove(item);
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
						if (!item.getId().startsWith("ch.eugster.colibri.client."))
						{
							if (!item.getId().startsWith("additions"))
							{
								if (!item.getId().startsWith("org.eclipse.equinox.p2."))
								{
									manager.remove(item);
								}
							}
						}
					}
				}
			}
			else if (menuItem.getId().equals(IWorkbenchActionConstants.MB_ADDITIONS))
			{
			}
			else if (menuItem.getId().startsWith("ch.eugster.colibri.client."))
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
			if ("org.eclipse.update.internal.ui.preferences.MainPreferencePage".equals(node.getId()))
			{
				pm.remove(node);
			}
			else if ("org.eclipse.ui.preferencePages.Workbench".equals(node.getId()))
			{
				pm.remove(node);
			}
		}
	}
}
