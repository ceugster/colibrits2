package ch.eugster.colibri.persistence.queries;

import java.util.Collection;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;

public class SalespointReceiptPrinterQuery extends AbstractQuery<SalespointReceiptPrinterSettings>
{
	@Override
	public Collection<SalespointReceiptPrinterSettings> selectAll(final boolean deletedToo)
	{
		final Expression group = new ExpressionBuilder(SalespointReceiptPrinterSettings.class);
		if (!deletedToo)
		{
			group.getBuilder().get("deleted").equal(deletedToo);
		}
		return this.select(group);
	}

	@Override
	protected Class<SalespointReceiptPrinterSettings> getEntityClass()
	{
		return SalespointReceiptPrinterSettings.class;
	}
}
