/*
 * Created on 2009 2 1
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.currency.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Currency;

public class CurrencySorter extends ViewerSorter
{
	private CommonSettings settings;

	public CurrencySorter(final CommonSettings settings)
	{
		this.settings = settings;
	}

	@Override
	public int compare(final Viewer viewer, final Object object1, final Object object2)
	{
		if ((object1 instanceof Currency) && (object2 instanceof Currency))
		{
			final Currency cur1 = (Currency) object1;
			final Currency cur2 = (Currency) object2;

			if (this.settings != null)
			{
				if (this.settings.getReferenceCurrency() != null)
				{
					if (cur1.equals(this.settings.getReferenceCurrency()))
					{
						return -1;
					}
					else if (cur2.equals(this.settings.getReferenceCurrency()))
					{
						return 1;
					}
				}
			}

			final int count1 = cur1.getPaymentTypes().size();
			final int count2 = cur2.getPaymentTypes().size();

			if (count1 != count2)
			{
				return count2 - count1;
			}

			return cur1.format().compareTo(cur2.format());
		}
		else
		{
			return 0;
		}
	}
}
