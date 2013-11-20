package ch.eugster.colibri.provider.service;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.PaymentQuery;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.Activator;

public abstract class AbstractProviderUpdater implements ProviderUpdater 
{
	protected ComponentContext context;

	@Override
	public int compareTo(ProviderUpdater other) 
	{
		return other.getRanking().compareTo(this.getRanking());
	}

	protected IStatus getStatus(Exception e)
	{
		if (e == null)
		{
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		}
		else
		{
			return new Status(IStatus.ERROR, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), new Exception("Die Verbindung zu " + this.getName() + " kann nicht hergestellt werden."));
		}
	}

	@Override
	public Collection<Position> getPositions(ConnectionService service,
			int max) 
	{
		SalespointQuery salespointQuery = (SalespointQuery) service.getQuery(Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint == null)
		{
			return new ArrayList<Position>();
		}
		PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectProviderUpdates(salespoint, getProviderId(), max);
	}
	
	@Override
	public IStatus updatePositions(PersistenceService persistenceService,
			Collection<Position> positions)
	{
		IStatus status = getStatus(null);
		if (positions.size() == 0)
		{
			status = this.checkConnection();
		}
		if (status.getSeverity() == IStatus.OK)
		{
			for (Position position : positions)
			{
				if (!position.isProviderBooked() || ! position.isServerUpdated())
				{
					if (!position.isProviderBooked())
					{
						status = updateProvider(position);
					}
					if (status.getSeverity() == IStatus.ERROR)
					{
						break;
					}
					if (status.getSeverity() == IStatus.OK)
					{
						try
						{
							if (!position.isServerUpdated())
							{
								if (position.getOtherId() == null)
								{
									position.setServerUpdated(true);
								}
								else
								{
									try
									{
										Position serverPosition = (Position) persistenceService.getServerService().find(Position.class, position.getOtherId());
										serverPosition.setServerUpdated(true);
										serverPosition.setProviderBooked(position.isProviderBooked());
										persistenceService.getServerService().merge(serverPosition);
									}
									catch (Exception e)
									{
										status = getStatus(e);
									}
								}
							}
						}
						finally
						{
							if (status.getSeverity() == IStatus.OK)
							{
								try
								{
									position = (Position) persistenceService.getCacheService().merge(position);
								}
								catch(Exception e)
								{
									status = getStatus(e);
								}
							}
						}
					}
				}
			}
		}
		return status;
	}

	@Override
	public Collection<Payment> getPayments(ConnectionService service, int max) 
	{
		SalespointQuery salespointQuery = (SalespointQuery) service.getQuery(Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint == null)
		{
			return new ArrayList<Payment>();
		}
		PaymentQuery query = (PaymentQuery) service.getQuery(Payment.class);
		return query.selectProviderUpdates(salespoint, this.getProviderId(), max);
	}

	@Override
	public IStatus updatePayments(PersistenceService persistenceService,
			Collection<Payment> payments) 
	{
		IStatus status = getStatus(null);
		for (Payment payment : payments)
		{
			if (!payment.isProviderBooked() || ! payment.isServerUpdated())
			{
				if (!payment.isProviderBooked())
				{
					status = updateProvider(payment);
				}
				if (status.getSeverity() == IStatus.ERROR)
				{
					return status;
				}
				if (status.getSeverity() == IStatus.OK)
				{
					try
					{
						if (!payment.isServerUpdated())
						{
							if (payment.getOtherId() == null)
							{
								payment.setServerUpdated(true);
							}
							else
							{
								try
								{
									Payment serverPayment = (Payment) persistenceService.getServerService().find(Payment.class, payment.getOtherId());
									serverPayment.setServerUpdated(true);
									serverPayment.setProviderBooked(payment.isProviderBooked());
									persistenceService.getServerService().merge(serverPayment);
								}
								catch (Exception e)
								{
									status = getStatus(e);
								}
							}
						}
					}
					finally
					{
						if (status.getSeverity() == IStatus.OK)
						{
							try
							{
								payment = (Payment) persistenceService.getCacheService().merge(payment);
							}
							catch(Exception e)
							{
								status = getStatus(e);
							}
						}
					}
				}
			}
		}
		return status;
	}

}
