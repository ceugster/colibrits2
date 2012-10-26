package ch.eugster.colibri.admin.profile.handlers;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Tab;

/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class AddTabHandler extends AbstractHandler
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
				if (ssel.getFirstElement() instanceof Configurable)
				{
					final Configurable configurable = (Configurable) ssel.getFirstElement();
					int pos = 0;
					Collection<Tab> tabs = configurable.getTabs();
					for (Tab tab : tabs)
					{
						if (!tab.isDeleted() && tab.getPos() > pos)
						{
							pos = tab.getPos();
						}
					}
					final Tab tab = Tab.newInstance(configurable);
					tab.setPos(++pos);
					Activator.getDefault().editTab(tab);
				}
			}
		}
		return null;
	}
}
