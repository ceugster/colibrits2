package ch.eugster.colibri.persistence.transfer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

	public static final String PLUGIN_ID = "ch.eugster.colibri.persistence.transfer";

	private static Activator activator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext context) throws Exception
	{
		Activator.activator = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception
	{
		Activator.activator = null;
	}

	public static Activator getDefault()
	{
		return Activator.activator;
	}

}
