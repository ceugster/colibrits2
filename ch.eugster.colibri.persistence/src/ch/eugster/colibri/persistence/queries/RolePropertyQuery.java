package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.RoleProperty;

public class RolePropertyQuery extends AbstractQuery<RoleProperty>
{
	@Override
	protected Class<RoleProperty> getEntityClass()
	{
		return RoleProperty.class;
	}
}
