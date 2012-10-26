package ch.eugster.colibri.barcode.code128.service.impl;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.code128.Activator;
import ch.eugster.colibri.barcode.code128.code.Code128;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;

public class Code128Service implements BarcodeVerifier
{
	private ComponentContext context;

	private LogService logService;

	@Override
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
		final Barcode barcode = Code128.verify(code);
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
