package ch.eugster.colibri.scheduler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
	private static Activator activator;

	private BundleContext context;

	public BundleContext getBundleContext()
	{
		return this.context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext bundleContext) throws Exception
	{
		Activator.activator = this;
		this.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext bundleContext) throws Exception
	{
		Activator.activator = null;
	}

	public static Activator getDefault()
	{
		return Activator.activator;
	}

}
