package ch.eugster.colibri.persistence.connection.config;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.queries.CurrentTaxQuery;
import ch.eugster.colibri.persistence.queries.TaxQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;

public class TaxUpdater
{
	public static void updateTaxes(ConnectionService service)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		TaxQuery taxQuery = (TaxQuery) service.getQuery(Tax.class);
		Collection<Tax> taxes = taxQuery.selectAll(true);
		for (Tax tax : taxes)
		{
			CurrentTaxQuery currentTaxQuery = (CurrentTaxQuery) service.getQuery(CurrentTax.class);
			Collection<CurrentTax> currentTaxes = currentTaxQuery.selectNewerThan(tax, tax.getCurrentTax()
					.getValidFrom().longValue());
			for (CurrentTax currentTax : currentTaxes)
			{
				if (tax.getCurrentTax().getValidFrom().longValue() < currentTax.getValidFrom().longValue()
						&& currentTax.getValidFrom().longValue() <= calendar.getTimeInMillis())
				{
					tax.setCurrentTax(currentTax);
					try
					{
						tax = (Tax) service.merge(tax);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void updateTaxes(EntityManager entityManager, Tax tax)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		for (CurrentTax currentTax : tax.getCurrentTaxes())
		{
			if ((tax.getCurrentTax() == null || tax.getCurrentTax().getValidFrom().longValue() < currentTax.getValidFrom().longValue())
					&& currentTax.getValidFrom().longValue() <= calendar.getTimeInMillis())
			{
				tax.setCurrentTax(currentTax);
				entityManager.getTransaction().begin();
				tax = (Tax) entityManager.merge(tax);
				entityManager.getTransaction().commit();
			}
		}
	}
}
