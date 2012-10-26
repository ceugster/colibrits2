package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.Configurable;

public class ConfigurableQuery extends AbstractQuery<Configurable>
{
	@Override
	protected Class<Configurable> getEntityClass()
	{
		return Configurable.class;
	}
}
