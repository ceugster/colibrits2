package ch.eugster.colibri.display.simple;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.display";

	private BundleContext context;

	public BundleContext getBundleContext()
	{
		return this.context;
	}

	@Override
	public void start(final BundleContext context) throws Exception
	{
		this.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		context = null;
	}

}
