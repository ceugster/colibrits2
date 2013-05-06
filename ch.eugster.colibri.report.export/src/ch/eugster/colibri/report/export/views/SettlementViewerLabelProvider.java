package ch.eugster.colibri.report.export.views;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementViewerLabelProvider extends LabelProvider implements
		IBaseLabelProvider {

	@Override
	public String getText(Object element)
	{
		if (element instanceof Settlement)
		{
			Settlement settlement = (Settlement) element;
			return SimpleDateFormat.getDateTimeInstance().format(settlement.getSettled().getTime());
		}
		return "";
	}

}
