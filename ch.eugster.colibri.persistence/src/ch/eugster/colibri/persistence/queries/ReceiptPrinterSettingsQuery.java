package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;

public class ReceiptPrinterSettingsQuery extends AbstractQuery<ReceiptPrinterSettings>
{
	public ReceiptPrinterSettings findByComponentName(final String componentName)
	{
		Expression expression = new ExpressionBuilder(ReceiptPrinterSettings.class).get("componentName").equal(componentName);
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(Boolean.valueOf(false)));
		try
		{
			ReceiptPrinterSettings[] settings = this.select(expression).toArray(new ReceiptPrinterSettings[0]);
			return settings.length == 0 ? null : settings[settings.length - 1];
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public List<ReceiptPrinterSettings> selectByComponentName(final String componentName)
	{
		Expression component = new ExpressionBuilder(ReceiptPrinterSettings.class).get("componentName").equal(componentName);
		component = component.and(new ExpressionBuilder().get("deleted").equal(Boolean.valueOf(false)));
		try
		{
			return this.select(component);
		}
		catch (Exception e)
		{
			return new ArrayList<ReceiptPrinterSettings>();
		}
	}

	@Override
	protected Class<ReceiptPrinterSettings> getEntityClass()
	{
		return ReceiptPrinterSettings.class;
	}

}
