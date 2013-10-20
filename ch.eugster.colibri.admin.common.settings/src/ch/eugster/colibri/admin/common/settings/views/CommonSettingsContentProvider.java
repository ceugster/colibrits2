/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.common.settings.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.common.settings.Activator;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.provider.service.ProviderUpdater;
import ch.eugster.colibri.scheduler.service.UpdateScheduler;

public class CommonSettingsContentProvider implements ITreeContentProvider
{
	private ProviderUpdaterParent providerUpdaterParent;
	
	@Override
	public void dispose()
	{
	}
	

	@Override
	public Object[] getChildren(final Object parent)
	{
		List<Object> entries = new ArrayList<Object>();
		if (parent instanceof TreeViewer)
		{
			entries.add(new GeneralSettingsParent());
			this.providerUpdaterParent = new ProviderUpdaterParent();
			entries.add(this.providerUpdaterParent);
		}
		if (parent instanceof ProviderUpdaterParent)
		{
			entries.addAll(getProviderUpdateSchedulers());
			entries.addAll(getProviderUpdaters());
		}
		return entries.toArray(new Object[0]);
	}

	private List<Object> getProviderUpdaters()
	{
		List<Object> entries = new ArrayList<Object>();
		ServiceTracker<ProviderUpdater, ProviderUpdater> tracker = new ServiceTracker<ProviderUpdater, ProviderUpdater>(Activator.getDefault().getBundle().getBundleContext(), ProviderUpdater.class, null);
		tracker.open();
		try
		{
			Object[] services = tracker.getServices();
			if (services instanceof Object[])
			{
				for (Object service : services)
				{
					entries.add(service);
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return entries;
	}
	
	private List<Object> getProviderUpdateSchedulers()
	{
		List<Object> entries = new ArrayList<Object>();
		ServiceTracker<UpdateScheduler, UpdateScheduler> tracker = new ServiceTracker<UpdateScheduler, UpdateScheduler>(Activator.getDefault().getBundle().getBundleContext(), UpdateScheduler.class, null);
		tracker.open();
		try
		{
			Object[] services = tracker.getServices();
			if (services instanceof Object[])
			{
				for (Object service : services)
				{
					entries.add(service);
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return entries;
	}
	
	@Override
	public Object[] getElements(final Object element)
	{
		return this.getChildren(element);
	}

	@Override
	public Object getParent(final Object child)
	{
		if (child instanceof ProviderProperty)
		{
			return this.providerUpdaterParent;
		}
		return null;
	}

	@Override
	public boolean hasChildren(final Object parent)
	{
		if (parent instanceof TreeViewer)
		{
			return true;
		}
		if (parent instanceof ProviderUpdaterParent)
		{
			ServiceTracker<ProviderUpdater, ProviderUpdater> tracker = new ServiceTracker<ProviderUpdater, ProviderUpdater>(Activator.getDefault().getBundle().getBundleContext(), ProviderUpdater.class, null);
			tracker.open();
			try
			{
				Object[] services = tracker.getServices();
				return services == null ? false : services.length > 0;
			}
			finally
			{
				tracker.close();
			}
		}
		return false;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

	public class GeneralSettingsParent implements Parent
	{
		public String getName()
		{
			return "Allgemeine Einstellungen";
		}
	}

	public interface Parent
	{
		String getName();
	}

	public class ProviderUpdaterParent implements Parent
	{
		public ProviderUpdaterParent()
		{
		}

		public String getName()
		{
			return "Schnittstellen";
		}
	}
}
