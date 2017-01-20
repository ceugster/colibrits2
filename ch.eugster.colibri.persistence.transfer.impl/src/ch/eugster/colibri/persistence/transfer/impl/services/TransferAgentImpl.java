package ch.eugster.colibri.persistence.transfer.impl.services;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.transfer.impl.Activator;
import ch.eugster.colibri.persistence.transfer.services.TransferAgent;

public class TransferAgentImpl implements TransferAgent
{
	private LogService logService;

	private PersistenceService persistenceService;

	private IStatus getStatus(Exception e)
	{
		if (e == null)
		{
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_TRANSFER.topic());
		}
		else
		{
			return new Status(IStatus.ERROR, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_TRANSFER.topic(), new Exception("Die Verbindung zur Datenbank auf dem Server kann nicht hergestellt werden."));
		}
	}

	public IStatus transfer(int count)
	{
		IStatus status = getStatus(null);
		if (!persistenceService.getServerService().isLocal())
		{
			/*
			 * First check if failOver
			 */
			try
			{
				final PositionQuery positionQuery = (PositionQuery) this.persistenceService.getServerService().getQuery(Position.class);
				positionQuery.sumCurrent(ProductGroupType.WITHDRAWAL);

				ReceiptTransfer receiptTransfer = new ReceiptTransfer(this.logService, this.persistenceService);
				status = receiptTransfer.transferReceipts(count);
//				if (status.getSeverity() == IStatus.OK)
//				{
//					SettlementTransfer settlementTransfer = new SettlementTransfer(this.logService, this.persistenceService);
//					status = settlementTransfer.transferSettlements(count);
//				}
			}
			catch (Exception e)
			{
				status = getStatus(e);
			}
		}
		return status;
	}

	public void transfer(Receipt receipt)
	{
		ReceiptTransfer receiptTransfer = new ReceiptTransfer(this.logService, this.persistenceService);
		receiptTransfer.transfer(receipt);
	}
	
	public void log(String message)
	{
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, message);
		}
	}

	protected void activate(final ComponentContext componentContext)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_DEBUG, "Service " + componentContext.getProperties().get("component.name")
					+ " aktiviert.");
		}
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_DEBUG, "Service " + componentContext.getProperties().get("component.name")
					+ " deaktiviert.");
		}
	}

	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	protected void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	protected void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

}
