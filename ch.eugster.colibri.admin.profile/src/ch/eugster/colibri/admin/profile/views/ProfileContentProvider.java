/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.queries.ProfileQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ProfileContentProvider implements ITreeContentProvider
{
	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getChildren(final Object parent)
	{
		if (parent instanceof PersistenceService)
		{
			final PersistenceService persistenceService = (PersistenceService) parent;
			if (persistenceService != null)
			{
				final ProfileQuery query = (ProfileQuery) persistenceService.getServerService().getQuery(Profile.class);
				return query.selectAll(true).toArray(new Profile[0]);
			}
		}
		else if (parent instanceof Profile)
		{
			final Profile profile = (Profile) parent;
			return profile.getConfigurables().toArray(new Configurable[0]);
		}
		else if (parent instanceof Configurable)
		{
			final Configurable configurable = (Configurable) parent;
			return configurable.getTabs().toArray(new Tab[0]);
		}

		return new Profile[0];
	}

	@Override
	public Object[] getElements(final Object element)
	{
		return this.getChildren(element);
	}

	@Override
	public Object getParent(final Object child)
	{
		if (child instanceof Configurable)
		{
			return ((Configurable) child).getProfile();
		}
		else if (child instanceof Tab)
		{
			return ((Tab) child).getConfigurable();
		}
		return null;
	}

	@Override
	public boolean hasChildren(final Object parent)
	{
		if (parent instanceof PersistenceService)
		{
			final PersistenceService persistenceService = (PersistenceService) parent;
			if (persistenceService != null)
			{
				final ProfileQuery query = (ProfileQuery) persistenceService.getServerService().getQuery(Profile.class);
				return query.countValid() > 0l;
			}
		}
		else if (parent instanceof Profile)
		{
			return ((Profile) parent).getConfigurables().size() > 0;
		}
		else if (parent instanceof Configurable)
		{
			return ((Configurable) parent).getTabs().size() > 0;
		}

		return false;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

}
