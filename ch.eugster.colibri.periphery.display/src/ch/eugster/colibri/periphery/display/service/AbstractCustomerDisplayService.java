package ch.eugster.colibri.periphery.display.service;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.periphery.converters.Converter;
import ch.eugster.colibri.periphery.display.Activator;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;
import ch.eugster.colibri.persistence.queries.CustomerDisplaySettingsQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractCustomerDisplayService implements CustomerDisplayService
{
	private ComponentContext context;

	private CustomerDisplaySettings customerDisplaySettings;

	private SalespointCustomerDisplaySettings salespointCustomerDisplaySettings;

	private PersistenceService persistenceService;

	private LogService logService;

	private EventAdmin eventAdmin;

	private Converter converter;

	@Override
	public CustomerDisplaySettings getCustomerDisplaySettings()
	{
		if (this.customerDisplaySettings == null)
		{
			this.setCustomerDisplaySettings();
		}
		return this.customerDisplaySettings;
	}
	
	protected String getPort()
	{
		String port = null;
		if (this.salespointCustomerDisplaySettings == null)
		{
			this.salespointCustomerDisplaySettings = this.getSalespointCustomerDisplaySettings(persistenceService.getCacheService());
		}
		if (this.salespointCustomerDisplaySettings == null)
		{
			port = this.customerDisplaySettings.getPort();
		}
		else
		{
			port = this.salespointCustomerDisplaySettings.getPort();
		}
		return port;
	}

	protected SalespointCustomerDisplaySettings getSalespointCustomerDisplaySettings()
	{
		return this.salespointCustomerDisplaySettings;
	}

	protected void setCustomerDisplaySettings()
	{
		this.customerDisplaySettings = this.getCustomerDisplaySettings(this.persistenceService.getCacheService());
//		if (this.customerDisplaySettings == null)
//		{
//			if (this.persistenceService.getServerService().isConnected())
//			{
//				this.customerDisplaySettings = this.getCustomerDisplaySettings(this.persistenceService.getServerService());
//			}
//		}
		if (this.customerDisplaySettings == null)
		{
			this.customerDisplaySettings = this.createCustomerDisplaySettings();
		}
	}

	protected void setSalespointCustomerDisplaySettings()
	{
		this.salespointCustomerDisplaySettings = this.getSalespointCustomerDisplaySettings(this.persistenceService.getCacheService());
		if (this.salespointCustomerDisplaySettings == null)
		{
			this.salespointCustomerDisplaySettings = this.createSalespointCustomerDisplaySettings();
		}
	}

	protected void activate(final ComponentContext context)
	{
		this.context = context;

		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " aktiviert.");
		}
	}

	protected String correctText(final Converter converter, final String text)
	{
		return this.correctText(converter, text, this.getColumnCount() * this.getRowCount());
	}

	protected String correctText(final Converter converter, String text, final int width)
	{
		if (text.length() > this.getColumnCount() * this.getRowCount())
		{
			text.substring(0, this.getColumnCount() * this.getRowCount());
		}
		text = converter.convert(text);
		if (text.length() < this.getColumnCount() * this.getRowCount())
		{
			StringBuilder builder = new StringBuilder(text);
			for (int i = text.length(); i < this.getColumnCount() * this.getRowCount(); i++)
			{
				builder = builder.append(" ");
			}
			text = builder.toString();
		}
		return text;
	}

	protected String correctText(final String text)
	{
		return this.correctText(this.getConverter(), text, this.getColumnCount() * this.getRowCount());
	}

	protected void deactivate(final ComponentContext context)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " deaktiviert.");
		}
	}

	protected int getColumnCount()
	{
		if (this.getSalespointCustomerDisplaySettings() == null)
		{
			return this.getCustomerDisplaySettings().getCols();
		}
		else
		{
			return this.getSalespointCustomerDisplaySettings().getCols();
		}
	}

	protected Converter getConverter()
	{
		if (this.converter == null)
		{
			this.converter = new Converter(this.customerDisplaySettings.getConverter());
		}
		return this.converter;
	}

	protected Event getEvent(final IStatus status)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE_ID, Activator.PLUGIN_ID);
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		properties.put("status", status);
		final Event event = new Event(CustomerDisplayService.EVENT_ADMIN_TOPIC_ERROR, properties);
		return event;
	}

	protected EventAdmin getEventAdmin()
	{
		return this.eventAdmin;
	}

	protected int getRowCount()
	{
		if (this.getSalespointCustomerDisplaySettings() == null)
		{
			return this.getCustomerDisplaySettings().getRows();
		}
		else
		{
			return this.getSalespointCustomerDisplaySettings().getRows();
		}
	}

	protected void setEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	protected void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected void unsetEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	protected void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	@Override
	public CustomerDisplaySettings createCustomerDisplaySettings()
	{
		final CustomerDisplaySettings settings = CustomerDisplaySettings.newInstance();
		settings.setRows(this.getDefaultRowCount());
		settings.setCols(this.getDefaultColumnCount());
		settings.setComponentName((String) this.context.getProperties().get("component.name"));
		settings.setConverter(this.getDefaultConverter());
		settings.setName((String) this.context.getProperties().get("custom.device"));
		settings.setPort(this.getDefaultPort());
		return settings;
	}

	protected SalespointCustomerDisplaySettings createSalespointCustomerDisplaySettings()
	{
		final SalespointCustomerDisplaySettings settings = SalespointCustomerDisplaySettings.newInstance();
		settings.setRows(this.getDefaultRowCount());
		settings.setCols(this.getDefaultColumnCount());
		settings.setConverter(this.getDefaultConverter());
		settings.setPort(this.getDefaultPort());
		return settings;
	}

	private CustomerDisplaySettings getCustomerDisplaySettings(final ConnectionService service)
	{
		final String componentName = (String) this.context.getProperties().get("component.name");
		final CustomerDisplaySettingsQuery query = (CustomerDisplaySettingsQuery) service
				.getQuery(CustomerDisplaySettings.class);
		return query.findByComponentName(componentName);
	}

	private SalespointCustomerDisplaySettings getSalespointCustomerDisplaySettings(final ConnectionService service)
	{
		final SalespointQuery query = (SalespointQuery) service.getQuery(Salespoint.class);
		Salespoint salespoint = query.getCurrentSalespoint();
		return salespoint.getCustomerDisplaySettings();
	}

	private int getDefaultColumnCount()
	{
		final Integer cols = (Integer) this.context.getProperties().get("custom.cols");
		return cols == null ? 1 : cols.intValue();
	}
	
	@Override
	public String convertToString(Object object)
	{
		if (object instanceof String)
		{
			return (String) object;
		}
		else if (object instanceof String[])
		{
			final StringBuilder result = new StringBuilder();
			final String[] lines = (String[]) object;
			for (final String line : lines)
			{
				result.append(line + "\n");
			}
			return result.toString();
		}
		return null;
	}

	private String getDefaultConverter()
	{
		return convertToString(this.context.getProperties().get("custom.convert"));
	}

	private String getDefaultPort()
	{
		final String port = (String) this.context.getProperties().get("custom.port");
		return port == null ? "" : port;
	}

	private int getDefaultRowCount()
	{
		final Integer rows = (Integer) this.context.getProperties().get("custom.rows");
		return rows == null ? 1 : rows.intValue();
	}
}
