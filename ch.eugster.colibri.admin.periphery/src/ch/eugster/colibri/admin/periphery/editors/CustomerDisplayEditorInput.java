/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.periphery.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.admin.periphery.views.PeripheryView;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;

public class CustomerDisplayEditorInput extends AbstractEntityEditorInput<CustomerDisplaySettings>
{
	private ServiceReference<CustomerDisplayService> reference;

	public CustomerDisplayEditorInput(final CustomerDisplaySettings periphery, ServiceReference <CustomerDisplayService>reference)
	{
		super(periphery);
		this.setServiceReference(reference);
	}

	public void setServiceReference(ServiceReference<CustomerDisplayService> reference)
	{
		this.reference = reference;
	}

	public ServiceReference<CustomerDisplayService> getServiceReference()
	{
		return this.reference;
	}

	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof CustomerDisplayEditorInput)
		{
			final CustomerDisplayEditorInput input = (CustomerDisplayEditorInput) other;
			if (input.getServiceReference().getProperty("component.name")
					.equals(this.getServiceReference().getProperty("component.name")))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter)
	{
		if (adapter.getName().equals(CustomerDisplaySettings.class.getName()))
		{
			return this.entity;
		}
		else
		{
			return null;
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	@Override
	public String getName()
	{
		final ServiceReference<CustomerDisplayService> reference = this.getServiceReference();
		if (reference instanceof ServiceReference)
		{
			final String device = (String) reference.getProperty("custom.device");
			if (device instanceof String)
			{
				return device;
			}
		}
		return "Kundendisplay";
	}

	@Override
	public String getToolTipText()
	{
		final ServiceReference<CustomerDisplayService> reference = this.getServiceReference();
		if (reference instanceof ServiceReference)
		{
			final String device = (String) reference.getProperty("custom.device");
			if (device instanceof String)
			{
				return device;
			}
		}
		return "Kundendisplay";
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
