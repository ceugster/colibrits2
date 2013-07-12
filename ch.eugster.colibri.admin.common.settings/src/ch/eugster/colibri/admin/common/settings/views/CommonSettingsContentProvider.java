/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.common.settings.views;

import java.util.Collection;
import java.util.Vector;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.common.settings.Activator;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderInterface;
import ch.eugster.colibri.voucher.client.VoucherService;

public class CommonSettingsContentProvider implements ITreeContentProvider
{
	private ProviderPropertyParent providerPropertyParent;

	private GeneralSettingsParent generalSettingsParent;

	private VoucherServiceParent voucherServiceParent;
	
	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getChildren(final Object parent)
	{
		final Collection<Object> items = new Vector<Object>();

		this.generalSettingsParent = new GeneralSettingsParent();
		items.add(this.generalSettingsParent);

		final ServiceTracker<ProviderInterface, ProviderInterface> tracker = new ServiceTracker<ProviderInterface, ProviderInterface>(Activator.getDefault().getBundle().getBundleContext(), ProviderInterface.class,
				null)
		{

			@Override
			public ProviderInterface addingService(ServiceReference<ProviderInterface> reference) {
				ProviderInterface service = this.getService(reference);
				CommonSettingsContentProvider.this.providerPropertyParent = new ProviderPropertyParent();
				items.add(CommonSettingsContentProvider.this.providerPropertyParent);
				return service;
			}

			@Override
			public void removedService(ServiceReference<ProviderInterface> reference,
					ProviderInterface service) {
				items.remove(CommonSettingsContentProvider.this.providerPropertyParent);
				super.removedService(reference, service);
			}
			
		};
		tracker.open();

		final ServiceTracker<VoucherService, VoucherService> voucherTracker = new ServiceTracker<VoucherService, VoucherService>(Activator.getDefault().getBundle().getBundleContext(), VoucherService.class, null)
		{

			@Override
			public VoucherService addingService(ServiceReference<VoucherService> reference) {
				VoucherService service = this.getService(reference);
				CommonSettingsContentProvider.this.voucherServiceParent = new VoucherServiceParent();
				items.add(CommonSettingsContentProvider.this.voucherServiceParent);
				return service;
			}

			@Override
			public void removedService(ServiceReference<VoucherService> reference,
					VoucherService service) {
				items.remove(CommonSettingsContentProvider.this.voucherServiceParent);
				super.removedService(reference, service);
			}
		};
		voucherTracker.open();
		
		return items.toArray(new Object[0]);
	}

	@Override
	public Object[] getElements(final Object element)
	{
		return this.getChildren(element);
	}

	@Override
	public Object getParent(final Object child)
	{
		if (child instanceof ProviderProperty)
		{
			return this.providerPropertyParent;
		}
		return null;
	}

	@Override
	public boolean hasChildren(final Object parent)
	{
		if (parent instanceof PersistenceService)
		{
			return true;
		}
		return false;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

	public class GeneralSettingsParent implements Parent
	{
		public String getName()
		{
			return "Allgemeine Einstellungen";
		}
	}

	public interface Parent
	{
		String getName();
	}

	public class ProviderPropertyParent implements Parent
	{
		public String getName()
		{
			return "Warenbewirtschaftung";
		}
	}

	public class VoucherServiceParent implements Parent
	{
		public String getName()
		{
			return "eGutschein";
		}
	}
}
