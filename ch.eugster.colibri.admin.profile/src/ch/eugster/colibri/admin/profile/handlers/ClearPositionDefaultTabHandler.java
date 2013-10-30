/*
 * Created on 28.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Tab;

public class ClearPositionDefaultTabHandler extends AbstractPersistenceClientHandler
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
				if (ssel.getFirstElement() instanceof Tab)
				{
					final Tab tab = (Tab) ssel.getFirstElement();
					tab.getConfigurable().setPositionDefaultTab(null);
					if (persistenceService != null)
					{
						try
						{
							persistenceService.getServerService().merge(tab.getConfigurable());
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
							IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
							ErrorDialog.openError((Shell) ctx.getVariable("activeShell"), "Fehler", "Der bestehende Default-Tab konnte nicht entfernt werden.", status);
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public void setEnabled(final Object object)
	{
		if (object instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) object;
			final Object sel = ctx.getParent().getVariable("selection");

			if (sel instanceof StructuredSelection)
			{
				final StructuredSelection ssel = (StructuredSelection) sel;
				if (ssel.size() == 1)
				{
					if (ssel.getFirstElement() instanceof Tab)
					{
						final Tab selectedTab = (Tab) ssel.getFirstElement();
						final Tab defaultTab = ((Tab) ssel.getFirstElement()).getConfigurable().getPositionDefaultTab();
						this.setBaseEnabled((defaultTab != null) && selectedTab.equals(defaultTab));
					}
				}
			}
		}
	}

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
