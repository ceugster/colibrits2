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
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.ReceiptPrinterSettingsQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractReceiptPrinterService implements ReceiptPrinterService
{
	private ComponentContext context;

	private ReceiptPrinterSettings receiptPrinterSettings;

	private PersistenceService persistenceService;

	private LogService logService;

	private EventAdmin eventAdmin;

	protected Salespoint salespoint;
	
	private Converter converter;

	protected String getPort()
	{
		if (salespoint == null || salespoint.getReceiptPrinterSettings() == null)
		{
			return receiptPrinterSettings.getPort();
		}
		return salespoint.getReceiptPrinterSettings().getPort();
	}

	protected int getLinesBeforeCut()
	{
		if (salespoint == null || salespoint.getReceiptPrinterSettings() == null)
		{
			return receiptPrinterSettings.getLinesBeforeCut();
		}
		return salespoint.getReceiptPrinterSettings().getLinesBeforeCut();
	}
	
	@Override
	public void cutPaper()
	{
		this.doCutPaper(this.getLinesBeforeCut());
	}

	@Override
	public void cutPaper(int feed)
	{
		this.doCutPaper(feed);
	}

	@Override
	public ReceiptPrinterSettings getReceiptPrinterSettings()
	{
		if (this.receiptPrinterSettings == null)
		{
			this.setReceiptPrinterSettings();
		}
		return this.receiptPrinterSettings;
	}

	public void setReceiptPrinterSettings()
	{
		this.receiptPrinterSettings = this.getReceiptPrinterSettings(this.persistenceService.getCacheService());
//		if (this.receiptPrinterSettings == null)
//		{
//			if (this.persistenceService.getServerService().isConnected())
//			{
//				this.receiptPrinterSettings = this.getReceiptPrinterSettings(this.persistenceService.getServerService());
//			}
//		}
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
			this.logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " aktiviert.");
		}
		SalespointQuery query = (SalespointQuery) this.persistenceService.getCacheService().getQuery(Salespoint.class);
		salespoint = query.getCurrentSalespoint();
	}

	protected void deactivate(final ComponentContext context)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " deaktiviert.");
		}
	}

	protected abstract void doCutPaper(int linesBeforeCut);

	protected int getColumnCount()
	{
		if (salespoint == null || salespoint.getReceiptPrinterSettings() == null)
		{
			return this.getReceiptPrinterSettings().getCols();
		}
		return this.salespoint.getReceiptPrinterSettings().getCols();
	}

	protected Converter getConverter()
	{
		if (this.converter == null)
		{
			if (salespoint == null || salespoint.getReceiptPrinterSettings() == null)
			{
				this.converter = new Converter(this.receiptPrinterSettings.getConverter());
			}
			else
			{
				this.converter = new Converter(salespoint.getReceiptPrinterSettings().getConverter());
			}
		}
		return this.converter;
	}

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
		settings.setLinesBeforeCut(linesBeforeCut instanceof Integer ? ((Integer) linesBeforeCut).intValue() : 0);
		return settings;
	}

	private ReceiptPrinterSettings getReceiptPrinterSettings(final ConnectionService service)
	{
		final String componentName = (String) this.context.getProperties().get("component.name");
		final ReceiptPrinterSettingsQuery query = (ReceiptPrinterSettingsQuery) service
				.getQuery(ReceiptPrinterSettings.class);
		return query.findByComponentName(componentName);
	}
}
