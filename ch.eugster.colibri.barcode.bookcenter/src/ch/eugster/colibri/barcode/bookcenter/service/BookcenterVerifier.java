package ch.eugster.colibri.barcode.bookcenter.service;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.bookcenter.Activator;
import ch.eugster.colibri.barcode.bookcenter.code.BookCenterCode;
import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;

public class BookcenterVerifier implements BarcodeVerifier
{
	private ComponentContext context;

	private LogService logService;

	public String getBarcodeDescription()
	{
		return Activator.getDescription();
	}

	public String getProperty(final String key)
	{
		return (String) this.context.getProperties().get(key);
	}

	@Override
	public Barcode verify(final String code)
	{
		final Barcode barcode = BookCenterCode.verify(code);
		if (barcode != null)
		{
			if (this.logService != null)
			{
				this.logService.log(LogService.LOG_INFO, "Barcode " + code + " validiert als: " + barcode.getDescription());
			}
		}
		return barcode;
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.context = componentContext;
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name") + " aktiviert.");
		}
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		this.context = null;
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name") + " deaktiviert.");
		}
	}

	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	protected void unsetLogService(final LogService logService)
	{

		this.logService = null;
	}
}
