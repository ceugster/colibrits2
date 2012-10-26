/*
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;

public class AddExternalProductGroupHandler extends AbstractPersistenceClientHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		Activator.getDefault().editExternalProductGroup(ExternalProductGroup.newInstance());
		return Status.OK_STATUS;
	}

	@Override
	public void setEnabled(Object evaluationContext) 
	{
		setBaseEnabled(persistenceService != null);
	}
	
	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
