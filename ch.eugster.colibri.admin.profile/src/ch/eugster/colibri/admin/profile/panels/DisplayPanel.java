/*
 * Created on 18.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.panels;

import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.Profile;

public abstract class DisplayPanel extends ch.eugster.colibri.ui.panels.DisplayPanel
{
	public static final long serialVersionUID = 0l;

	public DisplayPanel(final Profile profile)
	{
		super(profile);
		EntityMediator.addListener(Profile.class, this);
	}

	@Override
	public void finalize()
	{
		EntityMediator.removeListener(Profile.class, this);
	}

	public abstract void setTestData();

}
