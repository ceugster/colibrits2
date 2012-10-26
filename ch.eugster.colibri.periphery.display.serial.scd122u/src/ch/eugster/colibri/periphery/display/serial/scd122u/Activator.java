package ch.eugster.colibri.periphery.display.serial.scd122u;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.periphery.serial.display.scd122u";

	private static BundleContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext bundleContext) throws Exception
	{
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext bundleContext) throws Exception
	{
		Activator.context = null;
	}

	static BundleContext getContext()
	{
		return Activator.context;
	}

}