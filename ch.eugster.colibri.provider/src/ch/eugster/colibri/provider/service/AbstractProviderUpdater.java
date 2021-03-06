package ch.eugster.colibri.provider.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.PaymentQuery;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractProviderUpdater extends AbstractProviderService implements ProviderUpdater 
{
	@Override
	public List<Position> getPositions(PersistenceService service,
			int max) 
	{
		SalespointQuery salespointQuery = (SalespointQuery) service.getCacheService().getQuery(Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint == null)
		{
			return new ArrayList<Position>();
		}
		PositionQuery query = (PositionQuery) service.getCacheService().getQuery(Position.class);
		try
		{
			return query.selectProviderUpdates(salespoint, getProviderId(), !service.getServerService().isLocal(), max);
		}
		catch (Exception e)
		{
			return new ArrayList<Position>();
		}
	}
	
	@Override
	public IStatus updatePositions(PersistenceService persistenceService,
			List<Position> positions)
	{
		IStatus status = getStatus(null);
		if (positions.size() == 0)
		{
			status = this.checkConnection();
		}
		if (status.isOK())
		{
			for (Position position : positions)
			{
				status = updateProvider(position);
				try
				{
					if (!position.isServerUpdated())
					{
						if (position.getOtherId() != null)
						{
							try
							{
								Position serverPosition = (Position) persistenceService.getServerService().find(Position.class, position.getOtherId());
								if (serverPosition != null)
								{
									serverPosition.setServerUpdated(true);
									serverPosition.setProviderBooked(position.isProviderBooked());
									serverPosition.setProviderState(position.getProviderState());
									persistenceService.getServerService().merge(serverPosition);
								}
								position.setServerUpdated(true);
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
		return status;
	}

	@Override
	public List<Payment> getPayments(PersistenceService service, int max) 
	{
		SalespointQuery salespointQuery = (SalespointQuery) service.getCacheService().getQuery(Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint == null)
		{
			return new ArrayList<Payment>();
		}
		PaymentQuery query = (PaymentQuery) service.getCacheService().getQuery(Payment.class);
		return query.selectProviderUpdates(salespoint, this.getProviderId(), max);
	}

	@Override
	public IStatus updatePayments(PersistenceService persistenceService,
			List<Payment> payments) 
	{
		IStatus status = getStatus(null);
		if (payments.size() == 0)
		{
			status = this.checkConnection();
		}
		if (status.isOK())
		{
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
								if (payment.getOtherId() != null)
								{
									try
									{
										Payment serverPayment = (Payment) persistenceService.getServerService().find(Payment.class, payment.getOtherId());
										serverPayment.setServerUpdated(true);
										serverPayment.setProviderBooked(payment.isProviderBooked());
										serverPayment.setProviderState(payment.getProviderState());
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
									payment.setServerUpdated(true);
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
		}
		return status;
	}

	@Override
	public int compareTo(ProviderUpdater other) 
	{
		return other.getRanking().compareTo(this.getRanking());
	}

}
