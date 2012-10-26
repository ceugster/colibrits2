package ch.eugster.colibri.insert.receipt.app;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator, CommandProvider
{
	private ServiceTracker<InsertReceiptService, InsertReceiptService> insertReceiptServiceTracker;

	private static BundleContext context;

	static BundleContext getContext()
	{
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception
	{
		Activator.context = bundleContext;

		bundleContext.registerService(CommandProvider.class.getName(), this, null);

		insertReceiptServiceTracker = new ServiceTracker<InsertReceiptService, InsertReceiptService>(context, InsertReceiptService.class, null);
		insertReceiptServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception
	{
		Activator.context = null;
		insertReceiptServiceTracker.close();
	}

	@Override
	public String getHelp()
	{
		StringBuilder help = new StringBuilder();
		help.append("\nAvailable Commands:");
		help.append("\n\tinsert [number of receipts] - ");
		help.append("insert receipts\n");
		help.append("\n\tsettle - settle day\n");
		return help.toString();
	}

	public void _insert(CommandInterpreter commandInterpreter)
	{
		InsertReceiptService service = (InsertReceiptService) insertReceiptServiceTracker.getService();
		if (service == null)
		{
			commandInterpreter.println("Service not available.");
			return;
		}

		String arg = commandInterpreter.nextArgument();
		if (arg == null)
		{
			arg = "1";
		}
		try
		{
			int count = Integer.valueOf(arg);
			commandInterpreter.print("Inserting...");
			service.insertReceipts(count);
			commandInterpreter.println("OK");
		}
		catch (NumberFormatException e)
		{
			commandInterpreter.println("Invalid argument " + arg + ". Must be of type integer.");
			return;
		}
	}

	public void _settle(CommandInterpreter commandInterpreter)
	{
		InsertReceiptService service = (InsertReceiptService) insertReceiptServiceTracker.getService();
		if (service == null)
		{
			commandInterpreter.println("Service not available.");
			return;
		}

		commandInterpreter.print("Settle day...");
		service.settleDay(null);
		commandInterpreter.println("OK");
	}
}
