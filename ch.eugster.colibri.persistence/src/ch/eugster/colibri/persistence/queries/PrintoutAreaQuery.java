package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.PrintoutArea;

public class PrintoutAreaQuery extends AbstractQuery<PrintoutArea>
{
	@Override
	protected Class<PrintoutArea> getEntityClass()
	{
		return PrintoutArea.class;
	}
}
