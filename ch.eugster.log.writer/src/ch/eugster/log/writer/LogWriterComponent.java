package ch.eugster.log.writer;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

public class LogWriterComponent
{
	private LogService logService;
	
	protected void setLogService(LogService logService)
	{
		this.logService = logService;
	}

	protected void unsetLogService(LogService logService)
	{
		this.logService = null;
	}

	protected void activate(ComponentContext context)
	{
		logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " aktiviert.");
	}

	protected void deactivate(ComponentContext context)
	{
		logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " deaktiviert.");
	}
}
