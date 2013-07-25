package ch.eugster.colibri.admin.currency.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Text;

import ch.eugster.colibri.persistence.model.Currency;

public class CurrencyViewerFilter extends ViewerFilter 
{
	private Text filter;
	
	public CurrencyViewerFilter(Text filter)
	{
		this.filter = filter;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) 
	{
		if (filter == null || filter.isDisposed() || filter.getText().isEmpty())
		{
			return true;
		}
		if (element instanceof Currency)
		{
			Currency currency = (Currency) element;
			String value = filter.getText().toLowerCase().trim();
			return currency.getCode().toLowerCase().contains(value) || currency.getName().contains(value) || currency.getRegion().contains(value);
		}
		return true;
	}

}
