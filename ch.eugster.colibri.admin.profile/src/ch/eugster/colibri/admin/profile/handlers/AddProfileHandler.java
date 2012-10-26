package ch.eugster.colibri.admin.profile.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.persistence.model.Profile;

/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class AddProfileHandler extends AbstractHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		final Profile profile = Profile.newInstance();
		profile.initialize();
		Activator.getDefault().editProfile(profile);
		return null;
	}
}
