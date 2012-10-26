package ch.eugster.colibri.admin.profile.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.persistence.model.Profile;

public class CopyProfileHandler extends AbstractHandler implements IHandler
{

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
			final Object object = ctx.getParent().getVariable("selection");

			if (object instanceof StructuredSelection)
			{
				final StructuredSelection ssel = (StructuredSelection) object;
				if (ssel.getFirstElement() instanceof Profile)
				{
					final Profile oldProfile = (Profile) ssel.getFirstElement();
					final Profile newProfile = oldProfile.update(Profile.newInstance());
					Activator.getDefault().editProfile(newProfile);
				}
			}
		}
		return null;
	}

}
