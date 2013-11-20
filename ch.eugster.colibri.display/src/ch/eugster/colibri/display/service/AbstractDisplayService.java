package ch.eugster.colibri.display.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.display.Activator;
import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.display.area.ILayoutAreaType;
import ch.eugster.colibri.display.area.ILayoutType;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.DisplayArea;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.queries.DisplayQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractDisplayService implements DisplayService, EventHandler
{
	private boolean ready = false;

	private LogService logService;

	private PersistenceService persistenceService;

	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;

	/**
	 * For each customer display service there exists one layoutType if a
	 * customer display service is removed, there must be removed the
	 * correspondent layoutType to;
	 */
	private Map<String, ILayoutType> layoutTypes;

	private Map<String, CustomerDisplayService> customerDisplayServices;

	private ComponentContext context;

	private Salespoint salespoint;

	private Display display;

	public void addCustomerDisplayService(final CustomerDisplayService customerDisplayService)
	{
		if (this.customerDisplayServices == null)
		{
			this.customerDisplayServices = new HashMap<String, CustomerDisplayService>();
		}
		this.customerDisplayServices.put(customerDisplayService.getCustomerDisplaySettings().getComponentName(),
				customerDisplayService);

		if (this.layoutTypes == null)
		{
			this.layoutTypes = new HashMap<String, ILayoutType>();
		}
		final ILayoutType layoutType = this.getLayoutType(customerDisplayService);
		this.layoutTypes.put(customerDisplayService.getCustomerDisplaySettings().getComponentName(), layoutType);
	}

	@Override
	public void displayTestMessage(final ILayoutArea layoutArea, final Display display)
	{
		final ILayoutType layoutType = this.getLayoutType(display.getCustomerDisplaySettings().getComponentName());
		if (layoutType != null)
		{
			layoutType.displayTestMessage(layoutArea, display);
		}
	}

	@Override
	public ComponentContext getContext()
	{
		return this.context;
	}

	@Override
	public ILayoutType getLayoutType()
	{
		if (this.salespoint == null || this.salespoint.getCustomerDisplaySettings() == null)
		{
			return null;
		}
		else
		{
			return this.layoutTypes.get(this.salespoint.getCustomerDisplaySettings()
					.getCustomerDisplaySettings().getComponentName());
		}
	}

	@Override
	public ILayoutType getLayoutType(final String CustomerDisplayComponentName)
	{
		ILayoutType layoutType = this.layoutTypes.get(CustomerDisplayComponentName);
		return layoutType;
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (isReady())
		{
			if (event.getTopic().equals(Topic.STORE_RECEIPT.topic()))
			{
				this.displayWelcomeMessage();
			}
			else if (event.getTopic().equals(Topic.SALESPOINT_CLOSED.topic()))
			{
				this.displaySalespointClosedMessage();
			}
			else if (event.getTopic().equals(Topic.USER_LOGGED_IN.topic()))
			{
				this.displayWelcomeMessage(0);
			}
			else if (event.getTopic().equals(Topic.CLIENT_STARTED.topic()))
			{
				this.displaySalespointClosedMessage();
			}
			else if (event.getTopic().equals(Topic.POSITION_ADDED.topic()))
			{
				if (event.getProperty(IPrintable.class.getName()) instanceof Position)
				{
					final Position position = (Position) event.getProperty(IPrintable.class.getName());
					this.displayPositionAddedMessage(position);
				}
			}
			else if (event.getTopic().equals(Topic.PAYMENT_ADDED.topic()))
			{
				if (event.getProperty(IPrintable.class.getName()) instanceof Payment)
				{
					final Payment payment = (Payment) event.getProperty(IPrintable.class.getName());
					this.displayPaymentAddedMessage(payment);
				}
			}
		}
	}

	public void removeCustomerDisplayService(final CustomerDisplayService customerDisplayService)
	{
		if (this.customerDisplayServices != null)
		{
			this.customerDisplayServices.remove(customerDisplayService.getCustomerDisplaySettings().getComponentName());
		}
		if (this.layoutTypes != null)
		{
			this.layoutTypes.remove(customerDisplayService.getCustomerDisplaySettings().getComponentName());
		}
	}

	public void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	public void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	public void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	public void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	protected void activate(final ComponentContext context)
	{
		this.context = context;

		this.setReady(this.setDisplayContext());

		final Collection<String> t = new ArrayList<String>();
		t.add(Topic.STORE_RECEIPT.topic());
		t.add(Topic.SALESPOINT_CLOSED.topic());
		t.add(Topic.USER_LOGGED_IN.topic());
		t.add(Topic.CLIENT_STARTED.topic());
		t.add(Topic.POSITION_ADDED.topic());
		t.add(Topic.PAYMENT_ADDED.topic());
		final String[] topics = t.toArray(new String[t.size()]);
		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);
		
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " aktiviert.");
		}
	}

	protected void deactivate(final ComponentContext context)
	{
		this.setReady(false);

		this.eventHandlerServiceRegistration.unregister();

		if (display != null)
		{
			String name = this.display.getCustomerDisplaySettings().getComponentName();
			CustomerDisplayService service = this.getCustomerDisplayService(name);
			if (service != null)
			{
				service.clearText();
			}
		}

		this.context = null;

		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " deaktiviert.");
		}
	}

	protected CustomerDisplayService getCustomerDisplayService(final String componentName)
	{
		return componentName == null ? null : this.customerDisplayServices.get(componentName);
	}

	protected LogService getLogService()
	{
		return this.logService;
	}

	protected PersistenceService getPersistenceService()
	{
		return this.persistenceService;
	}

	protected Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	protected PersistenceService getServerService()
	{
		return this.persistenceService;
	}

	protected boolean isReady()
	{
		return this.ready;
	}

	protected void setReady(final boolean ready)
	{
		this.ready = ready;
	}

	private Display getDisplay(final ConnectionService service)
	{
		Display display = null;
		if (service != null)
		{
			if (this.salespoint != null)
			{
				if (this.salespoint.getCustomerDisplaySettings() != null)
				{
					/**
					 * There is a customerDisplay selected for the salespoint
					 */
					final DisplayQuery query = (DisplayQuery) service.getQuery(Display.class);
					final ILayoutType layoutType = this.getLayoutType();
					if (layoutType != null)
					{
						display = query.findByDisplayTypeAndSalespoint(layoutType.getId(), this.salespoint);

						if (display == null)
						{
							display = query.findTemplate(layoutType.getId(), this.salespoint
									.getCustomerDisplaySettings().getCustomerDisplaySettings());
						}
						if (display == null)
						{
							display = Display.newInstance(layoutType.getId(), this.salespoint);
						}
						if (display != null)
						{
							final ILayoutAreaType[] layoutAreaTypes = layoutType.getLayoutAreaTypes();
							for (final ILayoutAreaType layoutAreaType : layoutAreaTypes)
							{
								layoutAreaType.setColumnCount(display.getColumns());
								layoutAreaType.setRowCount(display.getRows());
								final Integer displayAreaType = Integer.valueOf(layoutAreaType.ordinal());
								DisplayArea displayArea = display.getDisplayArea(displayAreaType);
								if (displayArea == null)
								{
									if (display.getParent() != null)
									{
										displayArea = display.getParent().getDisplayArea(displayAreaType);
									}
								}
								if (displayArea == null)
								{
									layoutAreaType.getLayoutArea().setPattern(layoutAreaType.getLayoutArea().getDefaultPattern());
									layoutAreaType.getLayoutArea().setTimerDelay(layoutAreaType.getLayoutArea().getDefaultTimerDelay());
								}
								else
								{
									layoutAreaType.getLayoutArea().setPattern(displayArea.getPattern());
									layoutAreaType.getLayoutArea().setTimerDelay(displayArea.getTimerDelay());
								}
							}
						}
					}
				}
			}
		}
		return display;
	}

	private Salespoint getSalespoint(final ConnectionService service)
	{
		if (service != null)
		{
			final SalespointQuery query = (SalespointQuery) service.getQuery(Salespoint.class);
			return query.getCurrentSalespoint();
		}
		return null;
	}

	private boolean setDisplayContext()
	{
		this.salespoint = this.getSalespoint(this.persistenceService.getCacheService());
		this.display = this.getDisplay(this.persistenceService.getCacheService());

//		if (this.salespoint == null)
//		{
//			this.salespoint = this.getSalespoint(this.persistenceService.getServerService());
//			this.display = this.getDisplay(this.persistenceService.getServerService());
//		}
		if ((this.salespoint == null) || (this.display == null))
		{
			return false;
		}
		return true;
	}

	public static String padLeft(final String s, final int n)
	{
		return String.format("%1$" + n + "s", s);
	}

	public static String padRight(final String s, final int n)
	{
		return String.format("%1$-" + n + "s", s);
	}
}
