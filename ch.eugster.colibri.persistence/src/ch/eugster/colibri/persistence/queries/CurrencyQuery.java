package ch.eugster.colibri.persistence.queries;

import java.text.NumberFormat;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Currency;

public class CurrencyQuery extends AbstractQuery<Currency>
{
	private static NumberFormat quotationFormatter;

	private static NumberFormat roundFactorFormatter;

	public boolean isCodeUnique(final String code, final Long id) throws Exception
	{
		Expression codeExpr = new ExpressionBuilder().get("code").equal(code);
		codeExpr = codeExpr.and(new ExpressionBuilder().get("deleted").equal(false));
		if (id != null)
		{
			codeExpr = codeExpr.and(new ExpressionBuilder().get("id").equal(id).not());
		}
		return this.select(codeExpr).isEmpty();
	}

	public Currency selectByCode(final String code)
	{
		final Expression expression = new ExpressionBuilder().get("code").equal(code);
		return this.find(expression);
	}

	@Override
	protected Class<Currency> getEntityClass()
	{
		return Currency.class;
	}

	public static String formatQuotation(final double quotation)
	{
		if (CurrencyQuery.quotationFormatter == null)
		{
			CurrencyQuery.quotationFormatter = NumberFormat.getNumberInstance();
			CurrencyQuery.quotationFormatter.setMaximumFractionDigits(6);
			CurrencyQuery.quotationFormatter.setMinimumFractionDigits(0);
		}
		return CurrencyQuery.quotationFormatter.format(quotation);
	}

	public static String formatRoundFactor(final double roundFactor)
	{
		if (CurrencyQuery.roundFactorFormatter == null)
		{
			CurrencyQuery.roundFactorFormatter = NumberFormat.getNumberInstance();
			CurrencyQuery.roundFactorFormatter.setMaximumFractionDigits(2);
			CurrencyQuery.roundFactorFormatter.setMinimumFractionDigits(0);
		}
		return CurrencyQuery.roundFactorFormatter.format(roundFactor);
	}
}
