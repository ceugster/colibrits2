package ch.eugster.colibri.persistence.queries;

import java.util.HashMap;
import java.util.Map;

import ch.eugster.colibri.persistence.model.Role;

public class RoleQuery extends AbstractQuery<Role>
{
	public boolean isNameUnique(final String name, final Long id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return super.isUniqueValue(params, id);
	}

	@Override
	protected Class<Role> getEntityClass()
	{
		return Role.class;
	}
}
