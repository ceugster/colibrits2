package ch.eugster.colibri.display;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.display";

	private static Activator instance;
	
	private BundleContext context;

	public BundleContext getBundleContext()
	{
		return this.context;
	}

	public static Activator getDefault()
	{
		return instance;
	}
	
	@Override
	public void start(final BundleContext context) throws Exception
	{
		this.context = context;
		instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		context = null;
		instance = null;
	}

}
