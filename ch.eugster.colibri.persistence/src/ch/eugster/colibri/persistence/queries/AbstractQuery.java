package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;

import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.service.ConnectionService;

public abstract class AbstractQuery<T extends AbstractEntity> implements IQuery<T>
{
	private ConnectionService connectionService;
	
	public long countValid()
	{
		return this.count(new ExpressionBuilder(this.getEntityClass()).get("deleted").equal(false));
	}

	protected ConnectionService getConnectionService()
	{
		return connectionService;
	}

	@SuppressWarnings("unchecked")
	protected AbstractQuery<T> getQuery(Class<T> clazz)
	{
		return (AbstractQuery<T>) this.connectionService.getQuery(clazz);
	}

	@Override
	public T find(final Long id)
	{
		T entity = null;
		EntityManager entityManager = null;
		try
		{
			entityManager = this.connectionService.getEntityManager();
			if (entityManager != null)
			{
				entity = entityManager.find(this.getEntityClass(), id);
			}
		}
		catch (Exception e)
		{
		}
		return entity;
	}

	public boolean isUniqueValue(final Map<String, Object> params, final Long id)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		if (params != null && !params.isEmpty())
		{
			boolean firstEntry = true;
			final Set<Entry<String, Object>> entries = params.entrySet();
			for (Entry<String, Object> entry : entries)
			{
				if (firstEntry)
				{
					firstEntry = false;
					expression = expression.get(entry.getKey()).equal(entry.getValue());
				}
				else
				{
					expression = expression.and(new ExpressionBuilder().get(entry.getKey()).equal(entry.getValue()));
				}
			}
		}
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		if (id != null)
		{
			expression = expression.and(new ExpressionBuilder().get("id").notEqual(id));
		}

		return this.count(expression) == 0;
	}

	@Override
	public Collection<T> selectAll(final boolean deletedToo)
	{
		if (deletedToo)
		{
			return this.select(new ExpressionBuilder(this.getEntityClass()));
		}
		else
		{
			return this.select(new ExpressionBuilder(this.getEntityClass()).get("deleted").equal(false));
		}
	}

	public void setConnectionService(final ConnectionService connectionService)
	{
		this.connectionService = connectionService;
	}

	protected long count(final Expression expression)
	{
		long result = 0L;
		EntityManager entityManager = null;
		try
		{
			entityManager = this.connectionService.getEntityManager();
			if (entityManager != null)
			{
				if (!entityManager.getTransaction().isActive())
				{
					final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
					reportQuery.addCount("id", Long.class);
					reportQuery.setShouldReturnSingleValue(true);
					final Query query = createQuery(entityManager, reportQuery, 0);
					result = ((Long) query.getSingleResult()).longValue();
				}
			}
		}
		catch (Exception e)
		{
			this.getConnectionService().resetEntityManager(e);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	protected T find(final Expression expression)
	{
		T result = null;
		EntityManager entityManager = null;
		try
		{
			entityManager = this.connectionService.getEntityManager();
			if (entityManager != null)
			{
				
				final Query query = createQuery(entityManager, expression);
				try
				{
					final List<T> list = query.getResultList();
					
					result = list.isEmpty() ? null : list.iterator().next();
				}
				catch (final NoResultException e)
				{
				}
			}
		}
		catch (Exception e)
		{
			this.getConnectionService().resetEntityManager(e);
		}
		return result;
	}

	protected Expression getDateRangeExpression(final Long dateFrom, final Long dateTo)
	{
		Expression range = null;
		if ((dateFrom != null) && (dateTo != null))
		{
			range = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("timestamp")
					.between(dateFrom.longValue(), dateTo.longValue());
		}
		else if (dateTo != null)
		{
			range = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("timestamp")
					.lessThanEqual(dateTo.longValue());
		}
		else if (dateFrom != null)
		{
			range = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("timestamp")
					.greaterThanEqual(dateFrom.longValue());
		}
		return range;
	}

	protected abstract Class<T> getEntityClass();

	protected Expression getSalespointExpression(final Salespoint[] salespoints)
	{
		Expression sps = null;
		if (salespoints != null)
		{
			for (final Salespoint salespoint : salespoints)
			{
				if (sps == null)
				{
					sps = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("settlement")
							.get("salespoint").equal(salespoint);
				}
				else
				{
					sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").equal(salespoint));
				}
			}
		}
		return sps;
	}

	protected List<T> select(final Expression expression)
	{
		return this.select(expression, 0);
	}
	
	protected Query createQuery(EntityManager entityManager, DatabaseQuery databaseQuery, int maxResults)
	{
		final Query query = JpaHelper.createQuery(databaseQuery, entityManager);
		query.setHint(QueryHints.JDBC_TIMEOUT, this.getConnectionService().getTimeout());
		if (maxResults > 0)
		{
			query.setMaxResults(maxResults);
		}
		return query;
	}

	protected Query createQuery(EntityManager entityManager, Expression expression)
	{
		final JpaEntityManager em = JpaHelper.getEntityManager(entityManager);
		final Query query = em.createQuery(expression, this.getEntityClass());
		query.setHint(QueryHints.JDBC_TIMEOUT, this.getConnectionService().getTimeout());
		return query;
	}

	protected List<T> select(final Expression expression, final int maxResults)
	{
		return select(expression, null, maxResults);
	}

	@SuppressWarnings("unchecked")
	protected List<T> select(final Expression expression, final List<Expression> order, final int maxResults)
	{
		List<T> result = new ArrayList<T>();
		EntityManager entityManager = null;
		try
		{
			entityManager = this.connectionService.getEntityManager();
			if (entityManager != null)
			{
				final ReadAllQuery readAllQuery = new ReadAllQuery(this.getEntityClass(), expression);
				if (order != null)
				{
					readAllQuery.setOrderByExpressions(order);
				}
				final Query query = createQuery(entityManager, readAllQuery, maxResults);
//				Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
//				String time = SimpleDateFormat.getDateTimeInstance().format(calendar.getTime());
				result = query.getResultList();
			}
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
			this.connectionService.resetEntityManager(e);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected List<ReportQueryResult> selectReportQueryResults(ReportQuery reportQuery)
	{
		List<ReportQueryResult> results = new ArrayList<ReportQueryResult>();
		EntityManager entityManager = null;
		try
		{
			entityManager = this.connectionService.getEntityManager();
			if (entityManager != null)
			{
				final Query query = createQuery(entityManager, reportQuery, 0);
				results = query.getResultList();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getConnectionService().resetEntityManager(e);
		}
		return results;
	}
}
