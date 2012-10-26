/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.periphery.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.ServiceReference;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;

public class ReceiptPrinterEditorInput extends AbstractEntityEditorInput<ReceiptPrinterSettings>
{
	private ServiceReference<ReceiptPrinterService> reference;

	public ReceiptPrinterEditorInput(final ReceiptPrinterSettings periphery, ServiceReference<ReceiptPrinterService> reference)
	{
		super(periphery);
		this.setServiceReference(reference);
	}

	private void setServiceReference(ServiceReference<ReceiptPrinterService> reference)
	{
		this.reference = reference;
	}

	public ServiceReference<ReceiptPrinterService> getServiceReference()
	{
		return this.reference;
	}

	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof ReceiptPrinterEditorInput)
		{
			final ReceiptPrinterEditorInput input = (ReceiptPrinterEditorInput) other;
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
		if (adapter.getName().equals(ReceiptPrinterSettings.class.getName()))
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
		final ServiceReference<ReceiptPrinterService> reference = this.getServiceReference();
		if (reference instanceof ServiceReference)
		{
			final String device = (String) reference.getProperty("custom.device");
			if (device instanceof String)
			{
				return device;
			}
		}
		return "Belegdrucker";
	}

	@Override
	public String getToolTipText()
	{
		final ServiceReference<ReceiptPrinterService> reference = this.getServiceReference();
		if (reference instanceof ServiceReference)
		{
			final String device = (String) reference.getProperty("custom.device");
			if (device instanceof String)
			{
				return device;
			}
		}
		return "Belegdrucker";
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
