package ch.eugster.colibri.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.ui";

	private static Activator activator;

	public static Activator getDefault()
	{
		return activator;
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put("reset", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/undo_edit.png")));
		imageRegistry.put("login", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/login48.png")));
		imageRegistry.put("wait", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/wait_animated.gif")));
		imageRegistry.put("ch.eugster.colibri.admin.common.settings.perspective",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/gluehbirne_16.png")));
		imageRegistry.put("ch.eugster.colibri.admin.currency.perspective",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_blue_16.png")));
		imageRegistry.put("ch.eugster.colibri.admin.payment.perspective",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_green_16.png")));
		imageRegistry.put("ch.eugster.colibri.admin.periphery.perspective",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/usb_16.png")));
		imageRegistry.put("ch.eugster.colibri.admin.product.perspective",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/book_closed_16.png")));
		imageRegistry.put("ch.eugster.colibri.admin.profile.perspective",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/sunshine.png")));
		imageRegistry.put("ch.eugster.colibri.admin.salespoint.perspective",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/salespoint.png")));
		imageRegistry.put("ch.eugster.colibri.admin.tax.perspective",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/percent_16.png")));
		imageRegistry.put("ch.eugster.colibri.admin.user.perspective",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/user_green_16.png")));
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		Activator.activator = this;

	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		Activator.activator = null;
		super.stop(context);
	}

}
