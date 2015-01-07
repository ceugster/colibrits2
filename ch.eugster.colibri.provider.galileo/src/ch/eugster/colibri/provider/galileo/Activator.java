package ch.eugster.colibri.provider.galileo;

import java.util.Hashtable;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.service.GalileoConfiguratorComponent;
import ch.eugster.colibri.provider.galileo.service.GalileoIdService;
import ch.eugster.colibri.provider.service.ProviderConfigurator;
import ch.eugster.colibri.provider.service.ProviderIdService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	private ServiceTracker<LogService, LogService> logServiceTracker;

	// The shared instance
	private static Activator plugin;

	private GalileoConfiguration configuration;

	private ServiceRegistration<ProviderIdService> providerIdRegistration;
	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception
	{
		super.start(context);
		Activator.plugin = this;

		logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(),
				LogService.class, null);
		logServiceTracker.open();

		final LogService logService = (LogService) logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Plugin " + context.getBundle().getSymbolicName() + " gestartet.");
		}
		
		providerIdRegistration = this.getBundle().getBundleContext().registerService(ProviderIdService.class, new GalileoIdService(), new Hashtable<String, Object>());
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception
	{
		providerIdRegistration.unregister();
		final LogService logService = (LogService) logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Plugin " + context.getBundle().getSymbolicName() + " gestoppt.");
		}

		logServiceTracker.close();

		Activator.plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return Activator.plugin;
	}

	public GalileoConfiguration getConfiguration()
	{
		if (this.configuration == null)
		{
			this.configuration = new GalileoConfiguration();
		}
		return this.configuration;
	}
}
