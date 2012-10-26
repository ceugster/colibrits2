package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Button;

public class DriverViewerFilter extends ViewerFilter
{
	private Button button;

	public DriverViewerFilter(final Button button)
	{
		this.button = button;
	}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element)
	{
		if (element instanceof SupportedDriver)
		{
			final SupportedDriver driver = (SupportedDriver) element;
			final boolean embedded = driver.equals(SupportedDriver.DERBY_EMBEDDED);
			return this.button.getSelection() ? embedded : !embedded;
		}
		return false;
	}

}
