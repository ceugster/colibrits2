package ch.eugster.colibri.report.receipt.handlers;

import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.report.receipt.Activator;

public class PrintReceiptHandler extends AbstractHandler implements IHandler
{
	@Override
	public void setEnabled(Object evaluationContext)
	{
		// TODO Auto-generated method stub
		super.setEnabled(evaluationContext);
	}

	private final ServiceTracker<EventAdmin, EventAdmin> eventServiceTracker;

	public PrintReceiptHandler()
	{
		this.eventServiceTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.eventServiceTracker.open();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			EvaluationContext context = (EvaluationContext) event.getApplicationContext();
			ISelection selection = (ISelection) context.getParent().getVariable("selection");
			if (selection instanceof StructuredSelection)
			{
				StructuredSelection ssel = (StructuredSelection) selection;
				if (ssel.getFirstElement() instanceof Receipt)
				{
					Receipt receipt = (Receipt) ssel.getFirstElement();
					this.sendEvent(receipt);
				}
			}
		}
		return null;
	}

	private Event getEvent(final String topics, final Receipt receipt)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
		properties.put(EventConstants.SERVICE, this.eventServiceTracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID,
				this.eventServiceTracker.getServiceReference().getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
		properties.put(IPrintable.class.getName(), receipt);
		properties.put("force", true);
		properties.put("status", Status.OK_STATUS);
		return new Event(topics, properties);
	}

	private void sendEvent(final Receipt receipt)
	{
		final EventAdmin eventAdmin = (EventAdmin) this.eventServiceTracker.getService();
		if (eventAdmin != null)
		{
			eventAdmin.sendEvent(this.getEvent(Topic.PRINT_RECEIPT.topic(), receipt));
		}
	}

}
