package ch.eugster.colibri.persistence.queries;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.ReadAllQuery;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;

public class CurrentTaxQuery extends AbstractQuery<CurrentTax>
{
	@Override
	protected Class<CurrentTax> getEntityClass()
	{
		return CurrentTax.class;
	}

	public List<CurrentTax> selectNewerThan(Tax tax, long date) throws Exception
	{
		Expression expression = new ExpressionBuilder(CurrentTax.class).get("tax").equal(tax);
		expression = expression.and(new ExpressionBuilder().get("validFrom").greaterThan(date));
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		List<CurrentTax> currentTaxes = this.select(expression);
		return currentTaxes;
	}
	
	public List<CurrentTax> selectByValidFrom(Tax tax, long date) throws Exception
	{
		Expression expression = new ExpressionBuilder(CurrentTax.class).get("tax").equal(tax);
		expression = expression.and(new ExpressionBuilder().get("validFrom").equal(date));
		List<CurrentTax> currentTaxes = this.select(expression);
		return currentTaxes;
	}
	
	/**
	 * Don't use this in normal environment, use selectNewerThan(tax, date) instead. Use only for initializing a database
	 * @param entityManager
	 * @param tax
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<CurrentTax> selectNewerThan(EntityManager entityManager, Tax tax, Long date)
	{
		Expression expression = new ExpressionBuilder(CurrentTax.class).get("tax").equal(tax);
		expression = expression.and(new ExpressionBuilder().get("validFrom").greaterThan(date));
		final ReadAllQuery readAllQuery = new ReadAllQuery(this.getEntityClass(), expression);
		final Query query = JpaHelper.createQuery(readAllQuery, entityManager);
		return query.getResultList();
	}

}
