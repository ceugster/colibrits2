package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.DisplayArea;

public class DisplayAreaQuery extends AbstractQuery<DisplayArea>
{
	@Override
	protected Class<DisplayArea> getEntityClass()
	{
		return DisplayArea.class;
	}
}
