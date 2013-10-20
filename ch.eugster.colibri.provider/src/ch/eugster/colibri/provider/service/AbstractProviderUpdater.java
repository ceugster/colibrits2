package ch.eugster.colibri.provider.service;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.PaymentQuery;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.ConnectionService.ConnectionType;

public abstract class AbstractProviderUpdater implements ProviderUpdater 
{
	protected ComponentContext context;

	@Override
	public int compareTo(ProviderUpdater other) 
	{
		return other.getRanking().compareTo(this.getRanking());
	}

	@Override
	public Collection<Position> getPositions(ConnectionService service,
			int max, ConnectionType connectionType) 
	{
		SalespointQuery salespointQuery = (SalespointQuery) service.getQuery(Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint == null)
		{
			return new ArrayList<Position>();
		}
		PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectProviderUpdates(salespoint, getProviderId(), max, connectionType);
	}

	@Override
	public IStatus updatePositions(ConnectionService connectionService,
			Collection<Position> positions, IProgressMonitor monitor) 
	{
		IStatus status = Status.OK_STATUS;
		for (Position position : positions)
		{
			if (monitor.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			status = updateProvider(position);
			position = (Position) connectionService.merge(position);
			if (status.getSeverity() == IStatus.CANCEL)
			{
				return status;
			}
			monitor.worked(1);
		}
		return status;
	}

	@Override
	public Collection<Payment> getPayments(ConnectionService service, int max,
			ConnectionType connectionType) 
	{
		SalespointQuery salespointQuery = (SalespointQuery) service.getQuery(Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint == null)
		{
			return new ArrayList<Payment>();
		}
		PaymentQuery query = (PaymentQuery) service.getQuery(Payment.class);
		return query.selectProviderUpdates(salespoint, this.getProviderId(), max, connectionType);
	}

	@Override
	public IStatus updatePayments(ConnectionService connectionService,
			Collection<Payment> payments, IProgressMonitor monitor) 
	{
		monitor.beginTask("Aktualisiere " + this.getName(), payments.size());
		IStatus status = Status.OK_STATUS;
		for (Payment payment : payments)
		{
			if (monitor.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			status = updateProvider(payment);
			payment = (Payment) connectionService.merge(payment);
			if (status.getSeverity() == IStatus.CANCEL)
			{
				return status;
			}
			monitor.worked(1);
		}
		return status;
	}

}
