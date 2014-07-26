package ch.eugster.colibri.periphery.printer.service;

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
import ch.eugster.colibri.periphery.printer.Activator;
import ch.eugster.colibri.persistence.model.PrintMode;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;
import ch.eugster.colibri.persistence.queries.ReceiptPrinterSettingsQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractReceiptPrinterService implements ReceiptPrinterService
{
	private ComponentContext context;

	private ReceiptPrinterSettings receiptPrinterSettings;

	private SalespointReceiptPrinterSettings salespointReceiptPrinterSettings;

	private ConnectionService connectionService;

	private LogService logService;

	private EventAdmin eventAdmin;

	protected Salespoint salespoint;
	
	public ComponentContext getContext()
	{
		return context;
	}
	
	protected void setSalespointReceiptPrinterSettings()
	{
		this.salespointReceiptPrinterSettings = this.getSalespointReceiptPrinterSettings(connectionService);
	}

	private SalespointReceiptPrinterSettings getSalespointReceiptPrinterSettings(final ConnectionService service)
	{
		if (this.salespointReceiptPrinterSettings == null)
		{
			final SalespointQuery query = (SalespointQuery) service.getQuery(Salespoint.class);
			Salespoint salespoint = query.getCurrentSalespoint();
			this.salespointReceiptPrinterSettings = salespoint == null ? null : salespoint.getReceiptPrinterSettings();
		}
		return this.salespointReceiptPrinterSettings;
	}

	protected String getPort()
	{
		if (isClientApp())
		{
			if (this.getSalespointReceiptPrinterSettings(connectionService) != null)
			{
				return this.getSalespointReceiptPrinterSettings(connectionService).getPort();
			}
		}
		return this.getReceiptPrinterSettings().getPort();
	}

	protected boolean isPrintLogo()
	{
		if (isClientApp())
		{
			if (this.getSalespointReceiptPrinterSettings(connectionService) != null)
			{
				return this.getSalespointReceiptPrinterSettings(connectionService).isPrintLogo();
			}
		}
		return this.getReceiptPrinterSettings().isPrintLogo();
	}

	protected int getLogo()
	{
		if (isClientApp())
		{
			if (this.getSalespointReceiptPrinterSettings(connectionService) != null)
			{
				return this.getSalespointReceiptPrinterSettings(connectionService).getLogo();
			}
		}
		return this.getReceiptPrinterSettings().getLogo();
	}

	protected PrintMode getPrintLogoMode()
	{
		if (isClientApp())
		{
			if (this.getSalespointReceiptPrinterSettings(connectionService) != null)
			{
				return this.getSalespointReceiptPrinterSettings(connectionService).getPrintLogoMode();
			}
		}
		return this.getReceiptPrinterSettings().getPrintLogoMode();
	}

	protected int getLinesBeforeCut()
	{
		if (isClientApp())
		{
			if (this.getSalespointReceiptPrinterSettings(connectionService) != null)
			{
				return this.getSalespointReceiptPrinterSettings(connectionService).getLinesBeforeCut();
			}
		}
		return this.getReceiptPrinterSettings().getLinesBeforeCut();
	}
	
	protected int getColumnCount()
	{
		if (isClientApp())
		{
			if (this.getSalespointReceiptPrinterSettings(connectionService) != null)
			{
				return this.getSalespointReceiptPrinterSettings(connectionService).getCols();
			}
		}
		return this.getReceiptPrinterSettings().getCols();
	}

	protected Converter getConverter()
	{
		if (isClientApp())
		{
			if (this.getSalespointReceiptPrinterSettings(connectionService) != null)
			{
				return new Converter(this.getSalespointReceiptPrinterSettings(connectionService).getConverter());
			}
		}
		return new Converter(this.getReceiptPrinterSettings().getConverter());
	}

	protected void cutPaper()
	{
		this.doCutPaper(this.getLinesBeforeCut());
	}

	protected void cutPaper(int feed)
	{
		this.doCutPaper(feed);
	}

//	@Override
//	public ReceiptPrinterSettings getReceiptPrinterSettings()
//	{
//		if (this.receiptPrinterSettings == null)
//		{
//			this.setReceiptPrinterSettings();
//		}
//		return this.receiptPrinterSettings;
//	}
//
	public void setReceiptPrinterSettings()
	{
		this.receiptPrinterSettings = this.getReceiptPrinterSettings();
		if (this.receiptPrinterSettings == null)
		{
			this.receiptPrinterSettings = this.createReceiptPrinterSettings();
		}
	}

	protected void activate(final ComponentContext context)
	{
		this.context = context;

		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_DEBUG, "Service " + this.getClass().getName() + " aktiviert.");
		}
		salespoint = getSalespoint();
	}
	
	protected Salespoint getSalespoint()
	{
		SalespointQuery query = (SalespointQuery) this.connectionService.getQuery(Salespoint.class);
		return query.getCurrentSalespoint();
	}

	protected void deactivate(final ComponentContext context)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_DEBUG, "Service " + this.getClass().getName() + " deaktiviert.");
		}
	}

	protected abstract void doCutPaper(int linesBeforeCut);

	protected Event getEvent(final IStatus status)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, ReceiptPrinterService.EVENT_ADMIN_TOPIC_ERROR);
		properties.put(EventConstants.BUNDLE_ID, Activator.PLUGIN_ID);
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		properties.put("status", status);
		final Event event = new Event(ReceiptPrinterService.EVENT_ADMIN_TOPIC_ERROR, properties);
		return event;
	}

	protected EventAdmin getEventAdmin()
	{
		return this.eventAdmin;
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

	public ReceiptPrinterSettings createReceiptPrinterSettings()
	{
		final Object device = this.context.getProperties().get("custom.device");
		final Object cols = this.context.getProperties().get("custom.cols");
		final Object linesBeforeCut = this.context.getProperties().get("custom.lines.before.cut");
		final Object port = this.context.getProperties().get("custom.port");
		final String converter = convertToString(this.context.getProperties().get("custom.convert"));
		final ReceiptPrinterSettings settings = ReceiptPrinterSettings.newInstance();
		settings.setCols(cols instanceof Integer ? ((Integer) cols).intValue() : 0);
		settings.setComponentName((String) this.context.getProperties().get("component.name"));
		settings.setConverter(converter instanceof String ? (String) converter : "");
		settings.setName(device instanceof String ? (String) device : "???");
		settings.setPort(port instanceof String ? (String) port : "");
		settings.setPrintLogoMode(PrintMode.NORMAL);
		settings.setLinesBeforeCut(linesBeforeCut instanceof Integer ? ((Integer) linesBeforeCut).intValue() : 0);
		return settings;
	}

	@Override
	public ReceiptPrinterSettings getReceiptPrinterSettings()
	{
		final String componentName = (String) this.context.getProperties().get("component.name");
		final ReceiptPrinterSettingsQuery query = (ReceiptPrinterSettingsQuery) connectionService
				.getQuery(ReceiptPrinterSettings.class);
		return query.findByComponentName(componentName);
	}

	private boolean isClientApp()
	{
		String app = System.getProperty("eclipse.application");
		return app.contains("client");
	}
}
