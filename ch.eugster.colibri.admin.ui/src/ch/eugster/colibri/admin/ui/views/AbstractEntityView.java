package ch.eugster.colibri.admin.ui.views;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.model.AbstractEntity;

public abstract class AbstractEntityView<T extends AbstractEntity> extends ViewPart implements EntityListener
{
	@Override
	public void postDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void postLoad(final AbstractEntity entity)
	{
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
	}

	@Override
	public void postRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
	}

	@Override
	public void preDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void prePersist(final AbstractEntity entity)
	{
	}

	@Override
	public void preRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void preUpdate(final AbstractEntity entity)
	{
	}

	protected MenuManager createContextMenu(final Viewer viewer)
	{
		final MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		final Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		getSite().registerContextMenu(menuManager, viewer);

		return menuManager;
	}

}
