package ch.eugster.colibri.persistence.model.key;

import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.TaxRate;

public enum KeyType
{
	PRODUCT_GROUP, PAYMENT_TYPE, TAX_RATE, OPTION, FUNCTION;

	public String getActionCommand()
	{
		if (equals(PRODUCT_GROUP))
		{
			return "key.product.group";
		}
		else if (equals(PAYMENT_TYPE))
		{
			return "key.payment.type";
		}
		else if (equals(TAX_RATE))
		{
			return "key.tax.rate";
		}
		else if (equals(OPTION))
		{
			return "key.option";
		}
		else if (equals(FUNCTION))
		{
			return "function";
		}
		else
		{
			throw new RuntimeException("No such key type");
		}
	}

	public String key()
	{
		if (equals(PRODUCT_GROUP))
		{
			return "key.product.group";
		}
		else if (equals(PAYMENT_TYPE))
		{
			return "key.payment.type";
		}
		else if (equals(TAX_RATE))
		{
			return "key.tax.rate";
		}
		else if (equals(OPTION))
		{
			return "key.option";
		}
		else if (equals(FUNCTION))
		{
			return "key";
		}
		else
		{
			throw new RuntimeException("No such key type");
		}
	}

	@Override
	public String toString()
	{
		if (equals(PRODUCT_GROUP))
		{
			return "Warengruppen";
		}
		else if (equals(PAYMENT_TYPE))
		{
			return "Zahlungsarten";
		}
		else if (equals(TAX_RATE))
		{
			return "Mehrwertsteuern";
		}
		else if (equals(OPTION))
		{
			return "Optionen";
		}
		else if (equals(FUNCTION))
		{
			return "Funktionen";
		}
		else
		{
			throw new RuntimeException("No such key type");
		}
	}

	public static boolean validate(final Object object)
	{
		if (object instanceof ProductGroup)
		{
			return true;
		}
		if (object instanceof PaymentType)
		{
			return true;
		}
		if (object instanceof TaxRate)
		{
			return true;
		}
		if (object instanceof Position.Option)
		{
			return true;
		}
		if (object instanceof FunctionType)
		{
			return true;
		}
		return false;
	}
}
