/*
 * Created on 20.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.views;

import ch.eugster.colibri.admin.tax.views.TaxView.Mode;

public interface IModeSelectionProvider
{
	public void addModeSelectionListener(IModeSelectionListener listener);
	
	public void removeModeSelectionListener(IModeSelectionListener listener);
	
	public void notifyModeSelectionListeners(Mode mode);
}
