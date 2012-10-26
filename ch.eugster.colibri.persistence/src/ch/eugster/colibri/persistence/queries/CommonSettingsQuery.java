package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.CommonSettings;

public class CommonSettingsQuery extends AbstractQuery<CommonSettings>
{
	private CommonSettings commonSettings;

	public CommonSettings findDefault()
	{
		if (this.commonSettings == null)
		{
			this.commonSettings = this.find(Long.valueOf(1L));
		}
		return this.commonSettings;
	}

	public void setDefault(final CommonSettings commonSettings)
	{
		this.commonSettings = commonSettings;
	}

	@Override
	protected Class<CommonSettings> getEntityClass()
	{
		return CommonSettings.class;
	}
}
