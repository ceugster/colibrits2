package ch.eugster.colibri.periphery.display.service;

import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;

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

	protected ConnectionService connectionService;

	private LogService logService;

	private EventAdmin eventAdmin;

	public ComponentContext getContext()
	{
		return context;
	}
	
	protected void activate(final ComponentContext context)
	{
		this.context = context;

		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_DEBUG, "Service " + this.getClass().getName() + " aktiviert.");
		}
	}

	protected byte[] correctText(final Converter converter, final String text)
	{
		return this.correctText(converter, text, this.getColumnCount() * this.getRowCount());
	}

	protected byte[] correctText(final Converter converter, String text, final int width)
	{
		if (text.length() > width)
		{
			text.substring(0, width);
		}
		byte[] bytes = converter.convert(text.getBytes());
		if (text.length() < width)
		{
			StringBuilder builder = new StringBuilder(text);
			for (int i = text.length(); i < width; i++)
			{
				builder = builder.append(" ");
			}
			text = builder.toString();
		}
		return bytes;
	}

	protected byte[] correctText(final String text)
	{
		return this.correctText(this.getConverter(), text, this.getColumnCount() * this.getRowCount());
	}

	protected void deactivate(final ComponentContext context)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_DEBUG, "Service " + this.getClass().getName() + " deaktiviert.");
		}
	}

	protected String getPort()
	{
		if (isClientApp())
		{
			if (this.getSalespointCustomerDisplaySettings() != null)
			{
				return this.getSalespointCustomerDisplaySettings().getPort();
			}
		}
		return this.getCustomerDisplaySettings().getPort();
	}

	protected int getColumnCount()
	{
		if (isClientApp())
		{
			if (this.getSalespointCustomerDisplaySettings() != null)
			{
				return this.getSalespointCustomerDisplaySettings().getCols();
			}
		}
		return this.getCustomerDisplaySettings().getCols();
	}

	protected Converter getConverter()
	{
		if (isClientApp())
		{
			if (this.getSalespointCustomerDisplaySettings() != null)
			{
				return new Converter(this.getSalespointCustomerDisplaySettings().getConverter());
			}
		}
		return new Converter(this.getCustomerDisplaySettings().getConverter());
	}

	protected Event getEvent(final IStatus status)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE_ID, Activator.PLUGIN_ID);
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
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
		if (this.getSalespointCustomerDisplaySettings() != null)
		{
			return this.getSalespointCustomerDisplaySettings().getRows();
		}
		return this.getCustomerDisplaySettings().getRows();
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
		this.connectionService = isClientApp() ? persistenceService.getCacheService() : persistenceService.getServerService();
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
		this.connectionService = null;
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

	public CustomerDisplaySettings getCustomerDisplaySettings()
	{
		final String componentName = (String) this.context.getProperties().get("component.name");
		final CustomerDisplaySettingsQuery query = (CustomerDisplaySettingsQuery) connectionService
				.getQuery(CustomerDisplaySettings.class);
		this.customerDisplaySettings = query.findByComponentName(componentName);
		if (this.customerDisplaySettings == null)
		{
			this.customerDisplaySettings = this.createCustomerDisplaySettings();
		}
		return this.customerDisplaySettings;
	}

	protected SalespointCustomerDisplaySettings getSalespointCustomerDisplaySettings()
	{
		if (this.salespointCustomerDisplaySettings == null)
		{
			final SalespointQuery query = (SalespointQuery) connectionService.getQuery(Salespoint.class);
			Salespoint salespoint = query.getCurrentSalespoint();
			this.salespointCustomerDisplaySettings = salespoint == null ? null : salespoint.getCustomerDisplaySettings();
		}
		return this.salespointCustomerDisplaySettings;
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
	
	private boolean isClientApp()
	{
		String app = System.getProperty("eclipse.application");
		return app.contains("client");
	}
}
