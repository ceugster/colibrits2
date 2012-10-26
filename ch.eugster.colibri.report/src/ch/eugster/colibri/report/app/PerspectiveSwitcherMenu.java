package ch.eugster.colibri.report.app;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class PerspectiveSwitcherMenu extends ContributionItem
{
	private static final String KEY_PERSPECTIVE_DESCR = "k_p_descr";

	private final SelectionListener menuListener = new SwitchPerspectiveMenuListener();

	public PerspectiveSwitcherMenu(final String id)
	{
		super(id);
	}

	/**
	 * Fills a drop-down menu with all available perspectives. The current one
	 * is selected.
	 */
	@Override
	public void fill(final Menu menu, final int index)
	{
		final String activePerspective = PerspectiveSwitcherMenu.getPerspectiveId();

		final IPerspectiveDescriptor[] perspectives = PlatformUI.getWorkbench().getPerspectiveRegistry()
				.getPerspectives();

		for (final IPerspectiveDescriptor perspective : perspectives)
		{
			if (perspective.getId().startsWith(getId()))
			{
				final MenuItem item = new MenuItem(menu, SWT.RADIO, menu.getItemCount());
				item.setData(PerspectiveSwitcherMenu.KEY_PERSPECTIVE_DESCR, perspective);
				item.setText(perspective.getLabel());
				final Image image = perspective.getImageDescriptor().createImage();
				item.setImage(image);
				item.addDisposeListener(new DisposeListener()
				{
					@Override
					public void widgetDisposed(final DisposeEvent e)
					{
						image.dispose();
					}
				});
				item.addSelectionListener(this.menuListener);
				if (perspective.getId().equals(activePerspective))
				{
					item.setSelection(true);
				}
			}
		}
	}

	@Override
	public final boolean isDynamic()
	{
		return true;
	}

	private static IWorkbenchPage getActivePage()
	{
		IWorkbenchPage result = null;
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
		{
			result = window.getActivePage();
		}
		return result;
	}

	private static String getPerspectiveId()
	{
		String result = null;
		final IWorkbenchPage page = PerspectiveSwitcherMenu.getActivePage();
		if (page != null)
		{
			final IPerspectiveDescriptor descriptor = page.getPerspective();
			if (descriptor != null)
			{
				result = descriptor.getId();
			}
		}
		return result;
	}

	/**
	 * Switch perspective in the active page
	 */
	private static final class SwitchPerspectiveMenuListener extends SelectionAdapter
	{
		@Override
		public void widgetSelected(final SelectionEvent e)
		{
			final MenuItem item = (MenuItem) e.widget;
			if (item.getSelection())
			{
				final IWorkbenchPage page = PerspectiveSwitcherMenu.getActivePage();
				if (page != null)
				{
					final IPerspectiveDescriptor descriptor = (IPerspectiveDescriptor) item
							.getData(PerspectiveSwitcherMenu.KEY_PERSPECTIVE_DESCR);
					page.setPerspective(descriptor);
				}
			}
		}

	}
}
