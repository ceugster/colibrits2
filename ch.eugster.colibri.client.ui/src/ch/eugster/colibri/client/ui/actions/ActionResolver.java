package ch.eugster.colibri.client.ui.actions;

import ch.eugster.colibri.client.ui.buttons.ConfigurableButton;
import ch.eugster.colibri.client.ui.buttons.LockButton;
import ch.eugster.colibri.client.ui.buttons.ReceiptParkingButton;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;

public class ActionResolver
{
	public static Class<? extends ConfigurableAction> getActionClass(final FunctionType functionType)
	{
		if (functionType.equals(FunctionType.FUNCTION_LOCK))
		{
			return LockAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_LOGOUT))
		{
			return LogoutAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_SHUTDOWN))
		{
			return ShutdownAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_SELECT_CUSTOMER))
		{
			return SelectCustomerAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_STORE_RECEIPT))
		{
			return StoreReceiptAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_SHOW_CURRENT_RECEIPT_LIST))
		{
			return ShowCurrentReceiptListAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_SHOW_PARKED_RECEIPT_LIST))
		{
			return ReceiptParkingAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_SHOW_COIN_COUNTER_PANEL))
		{
			return ShowCoinCounterPanelAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_RESTITUTION))
		{
			return RestitutionAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_TOTAL_SALES))
		{
			return TotalSalesAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_SALESPOINT_SALES))
		{
			return SalespointSalesAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_USER_SALES))
		{
			return UserSalesAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_OPEN_DRAWER))
		{
			return OpenDrawerAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION))
		{
			return StoreReceiptExpressAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION))
		{
			return StoreReceiptShorthandAction.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_PRINT_LAST_RECEIPT))
		{
			return PrintLastReceiptAction.class;
		}
//		else if (functionType.equals(FunctionType.FUNCTION_FREE_COPY))
//		{
//			return FreeCopyAction.class;
//		}
		throw new RuntimeException("No such function type");
	}

	public static Class<? extends ConfigurableAction> getActionClass(final KeyType keyType)
	{
		if (keyType.equals(KeyType.PRODUCT_GROUP))
		{
			return ProductGroupAction.class;
		}
		else if (keyType.equals(KeyType.PAYMENT_TYPE))
		{
			return PaymentTypeAction.class;
		}
		else if (keyType.equals(KeyType.TAX_RATE))
		{
			return TaxRateAction.class;
		}
		else if (keyType.equals(KeyType.OPTION))
		{
			return OptionAction.class;
		}
		else if (keyType.equals(KeyType.FUNCTION))
		{
			return null;
		}
		else
		{
			throw new RuntimeException("No such class, try to get the class by using FunctionType.getClassName()");
		}
	}

	public static Class<? extends ConfigurableButton> getButtonClass(final FunctionType functionType)
	{
		if (functionType.equals(FunctionType.FUNCTION_LOCK))
		{
			return LockButton.class;
		}
		else if (functionType.equals(FunctionType.FUNCTION_SHOW_PARKED_RECEIPT_LIST))
		{
			return ReceiptParkingButton.class;
		}
		else
		{
			return ConfigurableButton.class;
		}
	}

	public static Class<ConfigurableButton> getButtonClass(final KeyType keyType)
	{
		if (keyType.equals(KeyType.FUNCTION))
		{
			return null;
		}
		else
		{
			return ConfigurableButton.class;
		}
	}

}
