package ch.eugster.colibri.persistence.transfer.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public static final String PLUGIN_ID = "ch.eugster.colibri.persistence.transfer.impl";

	private static Activator activator;

	private BundleContext context;

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
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception
	{
		this.context = null;
		Activator.activator = null;
	}

	public static Activator getDefault()
	{
		return Activator.activator;
	}

	public BundleContext getBundleContext()
	{
		return this.context;
	}
}
