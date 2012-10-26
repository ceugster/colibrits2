package ch.eugster.colibri.report.receipt.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementLabelProvider extends LabelProvider implements IBaseLabelProvider
{
	private final DateFormat sdf = SimpleDateFormat.getDateTimeInstance();

	@Override
	public String getText(final Object element)
	{
		if (element instanceof Settlement)
		{
			final Settlement settlement = (Settlement) element;
			final Calendar settled = settlement.getSettled();
			if (settled != null)
			{
				return this.sdf.format(settled.getTime());
			}
		}
		return "";
	}

}
