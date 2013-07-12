package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.CommonSettingsProperty;

public class CommonSettingsPropertyQuery extends AbstractQuery<CommonSettingsProperty>
{
	@Override
	protected Class<CommonSettingsProperty> getEntityClass()
	{
		return CommonSettingsProperty.class;
	}
}
