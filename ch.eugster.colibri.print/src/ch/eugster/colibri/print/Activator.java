package ch.eugster.colibri.print;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.print";

	private static Activator activator;

	private BundleContext context;

	public BundleContext getBundleContext()
	{
		return this.context;
	}

	@Override
	public void start(final BundleContext context) throws Exception
	{
		Activator.activator = this;
		this.context = context;
	}

	@Override
	public void stop(final BundleContext context) throws Exception
	{
		Activator.activator = null;
	}

	public static Activator getDefault()
	{
		return Activator.activator;
	}

}
