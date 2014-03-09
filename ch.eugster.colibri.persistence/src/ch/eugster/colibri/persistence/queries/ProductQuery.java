package ch.eugster.colibri.persistence.queries;

import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Product;

public class ProductQuery extends AbstractQuery<Product>
{
	@Override
	protected Class<Product> getEntityClass()
	{
		return Product.class;
	}
	
	public List<Product> selectInvoice(String number)
	{
		Expression select = new ExpressionBuilder(Product.class).get("invoiceNumber").equal(number);
		return this.select(select);
	}
}
