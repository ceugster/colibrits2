/*
 * Created on 12.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.user.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.eugster.colibri.admin.user.Activator;

public class UserPreferenceInitializer extends AbstractPreferenceInitializer
{

	public UserPreferenceInitializer()
	{
	}

	@Override
	public void initializeDefaultPreferences()
	{
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(IUserPreferenceConstants.KEY_MSG_TITLE_USERNAME_EMPTY,
				IUserPreferenceConstants.MSG_TITLE_USERNAME_EMPTY);
	}

}
