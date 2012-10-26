package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;

public class PrintoutQuery extends AbstractQuery<Printout>
{
	public Printout findByPrintoutTypeAndSalespoint(final String printoutType, final Salespoint salespoint)
	{
		if (salespoint.getReceiptPrinterSettings() != null)
		{
			Expression builder = new ExpressionBuilder(Printout.class).get("printoutType").equal(printoutType);
			builder = builder.and(new ExpressionBuilder().get("salespoint").equal(salespoint));
			builder = builder.and(new ExpressionBuilder().get("receiptPrinterSettings").equal(
					salespoint.getReceiptPrinterSettings().getReceiptPrinterSettings()));
			return this.find(builder);
		}
		return null;
	}

	public Printout findTemplate(final String printoutType, final ReceiptPrinterSettings receiptPrinterSettings)
	{
		Expression builder = new ExpressionBuilder(Printout.class).get("printoutType").equal(printoutType);
		builder = builder.and(new ExpressionBuilder().get("receiptPrinterSettings").equal(receiptPrinterSettings));
		builder = builder.and(new ExpressionBuilder().get("salespoint").isNull());
		return this.find(builder);
	}

	public Collection<Printout> selectAll()
	{
		final Expression expression = new ExpressionBuilder(Printout.class);

		final List<Expression> orders = new ArrayList<Expression>();
		orders.add(new ExpressionBuilder().get("salespoint").isNull().ascending());
		final Collection<Printout> printouts = this.select(expression, orders, 0);
		return printouts;
	}

	public Collection<Printout> selectPrintoutChildren()
	{
		final Expression builder = new ExpressionBuilder(Printout.class).get("printout").notNull();
		final Collection<Printout> printouts = this.select(builder);
		return printouts;
	}

	public Collection<Printout> selectPrintoutParents()
	{
		final Expression builder = new ExpressionBuilder(Printout.class).get("printout").isNull();
		final Collection<Printout> printouts = this.select(builder);
		return printouts;
	}

	@Override
	protected Class<Printout> getEntityClass()
	{
		return Printout.class;
	}
}
