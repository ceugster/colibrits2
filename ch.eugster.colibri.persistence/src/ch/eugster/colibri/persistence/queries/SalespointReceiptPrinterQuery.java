package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;

public class SalespointReceiptPrinterQuery extends AbstractQuery<SalespointReceiptPrinterSettings>
{
	@Override
	public List<SalespointReceiptPrinterSettings> selectAll(final boolean deletedToo)
	{
		final Expression group = new ExpressionBuilder(SalespointReceiptPrinterSettings.class);
		if (!deletedToo)
		{
			group.getBuilder().get("deleted").equal(deletedToo);
		}
		try
		{
			return this.select(group);
		}
		catch (Exception e)
		{
			return new ArrayList<SalespointReceiptPrinterSettings>();
		}
	}

	@Override
	protected Class<SalespointReceiptPrinterSettings> getEntityClass()
	{
		return SalespointReceiptPrinterSettings.class;
	}
}
