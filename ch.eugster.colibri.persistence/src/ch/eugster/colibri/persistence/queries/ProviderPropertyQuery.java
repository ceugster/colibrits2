package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;

public class ProviderPropertyQuery extends AbstractQuery<ProviderProperty>
{
	public List<ProviderProperty> selectByProvider(final String provider)
	{
		return this.selectByProviderAndSalespoint(provider, null);
	}

	public List<ProviderProperty> selectByProviderAndSalespoint(final String provider, final Salespoint salespoint)
	{
		final Expression d = new ExpressionBuilder(ProviderProperty.class).get("deleted").equal(false);

		final Expression p = d.and(new ExpressionBuilder().get("provider").equal(provider));

		Expression s = null;
		if (salespoint == null)
		{
			s = d.and(new ExpressionBuilder().get("salespoint").isNull());
		}
		else
		{
			s = d.and(new ExpressionBuilder().get("salespoint").equal(salespoint));
		}
		try
		{
			final List<Expression> orders = new ArrayList<Expression>();
			orders.add(new ExpressionBuilder().get("id").ascending());
			List<ProviderProperty> providerProperties = this.select(p.and(s), orders, 0);
			return providerProperties;
		}
		catch (Exception e)
		{
			return new ArrayList<ProviderProperty>();
		}
	}

	public Map<String, ProviderProperty> selectByProviderAndSalespointAsMap(final String provider, final Salespoint salespoint)
	{
		final Map<String, ProviderProperty> properties = new HashMap<String, ProviderProperty>();
		final Collection<ProviderProperty> collection = this.selectByProviderAndSalespoint(provider, salespoint);
		for (final ProviderProperty property : collection)
		{
			properties.put(provider, property);
		}
		return properties;
	}

	public Map<String, ProviderProperty> selectByProviderAsMap(final String provider)
	{
		final Map<String, ProviderProperty> properties = new HashMap<String, ProviderProperty>();
		final Collection<ProviderProperty> collection = this.selectByProviderAndSalespoint(provider, null);
		for (final ProviderProperty property : collection)
		{
			properties.put(property.getKey(), property);
		}

		return properties;
	}

//	public Map<String, ProviderProperty> selectByProviderAsMap(final String provider, final Map<String, String> defaults)
//	{
//		final Map<String, ProviderProperty> properties = new HashMap<String, ProviderProperty>();
//		final Collection<ProviderProperty> collection = this.selectByProviderAndSalespoint(provider, null);
//		for (final ProviderProperty property : collection)
//		{
//			properties.put(property.getKey(), property);
//		}
//		final String[] keys = defaults.keySet().toArray(new String[0]);
//		for (final String key : keys)
//		{
//			if (properties.get(key) == null)
//			{
//				final ProviderProperty property = ProviderProperty.newInstance(provider, prop);
//				property.setKey(key);
//				property.setValue(defaults.get(key));
//				properties.put(key, property);
//			}
//		}
//		return properties;
//	}

	@Override
	protected Class<ProviderProperty> getEntityClass()
	{
		return ProviderProperty.class;
	}
}
