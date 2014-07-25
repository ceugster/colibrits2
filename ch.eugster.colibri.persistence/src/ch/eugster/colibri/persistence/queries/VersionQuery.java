package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.Version;

public class VersionQuery extends AbstractQuery<Version>
{
	private Version version;

	public Version findDefault()
	{
		if (this.version == null)
		{
			this.version = this.find(Long.valueOf(1L));
		}
		else
		{
			this.getConnectionService().refresh(this.version);
		}
		return this.version;
	}

	public void setDefault(final Version version)
	{
		this.version = version;
	}

	@Override
	protected Class<Version> getEntityClass()
	{
		return Version.class;
	}
}
