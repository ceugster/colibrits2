package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.CommonSettings.HostnameResolver;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;

public class SalespointQuery extends AbstractQuery<Salespoint>
{
	private Salespoint thisSalespoint;

	public void clearSalespointHosts(final String host)
	{
		final Collection<Salespoint> salespoints = this.selectByHost(host);
		for (final Salespoint salespoint : salespoints)
		{
			salespoint.setHost(null);
			try
			{
				this.getConnectionService().merge(salespoint, true);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void setCurrentSalespoint(Salespoint salespoint)
	{
		this.thisSalespoint = salespoint;
	}

	public Salespoint getCurrentSalespoint()
	{
		if (this.thisSalespoint == null)
		{
			final CommonSettingsQuery settingsQuery = (CommonSettingsQuery) this.getConnectionService().getQuery(
					CommonSettings.class);
			if (settingsQuery != null)
			{
				final CommonSettings settings = settingsQuery.find(Long.valueOf(1L));
				if (settings != null)
				{
					final Collection<Salespoint> salespoints = this.selectByHost(this.getHostname(settings
							.getHostnameResolver()));
					if (!salespoints.isEmpty())
					{
						this.thisSalespoint = salespoints.iterator().next();
					}
					if (this.thisSalespoint != null)
					{
						if ((this.thisSalespoint.getSettlement() == null)
								|| (this.thisSalespoint.getSettlement().getSettled() != null))
						{
							final SettlementQuery queryService = (SettlementQuery) this.getConnectionService()
									.getQuery(Settlement.class);
							try
							{
								List<Settlement> settlements = queryService.select(new ExpressionBuilder(
										Settlement.class).get("salespoint").equal(this.thisSalespoint)
										.and(new ExpressionBuilder(Settlement.class).get("settled").isNull()));
								if (settlements.isEmpty())
								{
									this.thisSalespoint.setSettlement(Settlement.newInstance(this.thisSalespoint));
								}
								else
								{
									this.thisSalespoint.setSettlement(settlements.iterator().next());
								}
								this.thisSalespoint = (Salespoint) this.getConnectionService().merge(this.thisSalespoint);
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		else
		{
			this.thisSalespoint = (Salespoint) this.getConnectionService().refresh(this.thisSalespoint);
		}
		return this.thisSalespoint;
	}

	public List<Salespoint> selectByMapping(String mapping)
	{
		Expression expression = new ExpressionBuilder(Salespoint.class).get("mapping").equal(mapping);
		try
		{
			return this.select(expression);
		}
		catch (Exception e)
		{
			return new ArrayList<Salespoint>();
		}
	}

	public String getHostname(final HostnameResolver resolver)
	{
		final CommonSettings settings = (CommonSettings) (this.getConnectionService().find(CommonSettings.class,
				Long.valueOf(1L)));
		return settings == null ? null : settings.getHostnameResolver().getHostname();
	}

	public boolean isNameUnique(final String name, final Long id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return this.isUniqueValue(params, id);
	}

	public List<Salespoint> selectByHost(final String host)
	{
		try
		{
			return this.select(new ExpressionBuilder(Salespoint.class).get("host").equal(host)
					.and(new ExpressionBuilder().get("deleted").equal(false)));
		}
		catch (Exception e)
		{
			return new ArrayList<Salespoint>();
		}
	}

	@Override
	protected Class<Salespoint> getEntityClass()
	{
		return Salespoint.class;
	}

}
