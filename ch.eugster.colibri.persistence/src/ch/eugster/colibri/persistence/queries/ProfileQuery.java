package ch.eugster.colibri.persistence.queries;

import java.util.HashMap;
import java.util.Map;

import ch.eugster.colibri.persistence.model.Profile;

public class ProfileQuery extends AbstractQuery<Profile>
{
	public boolean isUniqueName(final String name, final Long id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return this.isUniqueValue(params, id);
	}

	@Override
	protected Class<Profile> getEntityClass()
	{
		return Profile.class;
	}
}
