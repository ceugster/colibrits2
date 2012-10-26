package ch.eugster.colibri.report.settlement.views;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jface.viewers.LabelProvider;

import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementLabelProvider extends LabelProvider
{

	@Override
	public String getText(Object element)
	{
		if (element instanceof Settlement)
		{
			Settlement settlement = (Settlement) element;
			if (settlement.getSettled() instanceof Calendar)
			{
				return settlement.getSalespoint().getName() + " - "
						+ SimpleDateFormat.getDateTimeInstance().format(settlement.getSettled().getTime());
			}
		}
		return super.getText(element);
	}

}
