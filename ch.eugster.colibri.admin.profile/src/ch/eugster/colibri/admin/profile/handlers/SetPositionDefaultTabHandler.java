/*
 * Created on 28.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.persistence.model.Configurable.ConfigurableType;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class SetPositionDefaultTabHandler extends AbstractHandler implements IHandler
{
	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	@Override
	public void setEnabled(Object evaluationContext) 
	{
		EvaluationContext context = (EvaluationContext) evaluationContext;
		Object object = context.getVariable("selection");
		if (object instanceof StructuredSelection)
		{
			StructuredSelection ssel = (StructuredSelection) object;
			if (ssel != null && !ssel.isEmpty())
			{
				if (ssel.getFirstElement() instanceof Tab)
				{
					Tab tab = (Tab) ssel.getFirstElement();
					ConfigurableType type = tab.getConfigurable().getConfigurableType();
					if (type.equals(ConfigurableType.PRODUCT_GROUP) || type.equals(ConfigurableType.FUNCTION))
					{
						this.setBaseEnabled(true);
						return;
					}
				}
			}
		}
		setBaseEnabled(false);
	}

	public SetPositionDefaultTabHandler()
	{
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

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
					tab.getConfigurable().setPositionDefaultTab(tab);
					final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker
							.getService();
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
							ErrorDialog.openError((Shell) ctx.getVariable("activeShell"), "Fehler", "Der Tab " + tab.getName() + " konnte nicht als Default gesetzt werden.", status);
						}
					}
				}
			}
		}
		return null;
	}
}
