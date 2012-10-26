package ch.eugster.colibri.persistence.queries;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;

public class CustomerDisplaySettingsQuery extends AbstractQuery<CustomerDisplaySettings>
{
	public CustomerDisplaySettings findByComponentName(final String componentName)
	{
		Expression component = new ExpressionBuilder(CustomerDisplaySettings.class).get("componentName").equal(componentName);
		component = component.and(new ExpressionBuilder().get("deleted").equal(Boolean.valueOf(false)));
		return this.find(component);
	}

	@Override
	protected Class<CustomerDisplaySettings> getEntityClass()
	{
		return CustomerDisplaySettings.class;
	}
}
