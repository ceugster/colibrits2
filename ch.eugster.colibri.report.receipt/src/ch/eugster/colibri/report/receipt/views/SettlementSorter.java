package ch.eugster.colibri.report.receipt.views;

import java.util.Calendar;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2)
	{
		if ((e1 instanceof Settlement) && (e2 instanceof Settlement))
		{
			final Settlement s1 = (Settlement) e1;
			final Settlement s2 = (Settlement) e2;

			final Calendar date1 = s1.getSettled();
			final Calendar date2 = s2.getSettled();

			if ((date1 == null) && (date2 == null))
			{
				return 0;
			}
			else if (date2 == null)
			{
				return 1;
			}
			else if (date1 == null)
			{
				return -1;
			}
			else
			{
				return date1.compareTo(date2);
			}
		}
		return 0;
	}

}
