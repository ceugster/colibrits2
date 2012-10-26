package ch.eugster.colibri.persistence.queries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;

public class ProviderPropertyQuery extends AbstractQuery<ProviderProperty>
{
	public Collection<ProviderProperty> selectByProvider(final String provider)
	{
		return this.selectByProviderAndSalespoint(provider, null);
	}

	public Collection<ProviderProperty> selectByProviderAndSalespoint(final String provider, final Salespoint salespoint)
	{
		final ExpressionBuilder builder = new ExpressionBuilder(ProviderProperty.class);

		final Expression p = builder.get("provider").equal(provider);

		Expression s = null;
		if (salespoint == null)
		{
			s = builder.get("salespoint").isNull();
		}
		else
		{
			s = builder.get("salespoint").equal(salespoint);
		}

		return this.select(p.and(s));
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

	public Map<String, ProviderProperty> selectByProviderAsMap(final String provider, final Map<String, String> defaults)
	{
		final Map<String, ProviderProperty> properties = new HashMap<String, ProviderProperty>();
		final Collection<ProviderProperty> collection = this.selectByProviderAndSalespoint(provider, null);
		for (final ProviderProperty property : collection)
		{
			properties.put(property.getKey(), property);
		}
		final String[] keys = defaults.keySet().toArray(new String[0]);
		for (final String key : keys)
		{
			if (properties.get(key) == null)
			{
				final ProviderProperty property = ProviderProperty.newInstance(provider);
				property.setKey(key);
				property.setValue(defaults.get(key));
				properties.put(key, property);
			}
		}
		return properties;
	}

	@Override
	protected Class<ProviderProperty> getEntityClass()
	{
		return ProviderProperty.class;
	}
}
