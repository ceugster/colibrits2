package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.Tab;

public class TabQuery extends AbstractQuery<Tab>
{
	@Override
	protected Class<Tab> getEntityClass()
	{
		return Tab.class;
	}
}
