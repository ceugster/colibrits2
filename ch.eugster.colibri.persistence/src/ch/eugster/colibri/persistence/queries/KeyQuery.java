package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.Key;

public class KeyQuery extends AbstractQuery<Key>
{
	@Override
	protected Class<Key> getEntityClass()
	{
		return Key.class;
	}
}
