package ch.eugster.colibri.barcode.ean13.service.impl;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.ean13.Activator;
import ch.eugster.colibri.barcode.ean13.code.Ean13;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.model.Position;

public class Ean13Verifier implements BarcodeVerifier
{
	private ComponentContext context;

	private LogService logService;

	public Ean13Verifier()
	{

	}

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
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Validiere " + code + ": ");
		}

		final Barcode barcode = Ean13.verify(code);
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, (barcode == null ? "Ungültiger Barcode" : barcode.getDescription()));
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
