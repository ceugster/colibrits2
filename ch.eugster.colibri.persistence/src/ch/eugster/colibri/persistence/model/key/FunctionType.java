package ch.eugster.colibri.persistence.model.key;

import java.util.ArrayList;
import java.util.List;

public enum FunctionType
{
//	FUNCTION_LOCK, FUNCTION_LOGOUT, FUNCTION_SHUTDOWN, FUNCTION_SELECT_CUSTOMER, FUNCTION_STORE_RECEIPT, FUNCTION_SHOW_CURRENT_RECEIPT_LIST, FUNCTION_SHOW_PARKED_RECEIPT_LIST, FUNCTION_SHOW_COIN_COUNTER_PANEL, FUNCTION_RESTITUTION, FUNCTION_TOTAL_SALES, FUNCTION_SALESPOINT_SALES, FUNCTION_USER_SALES, FUNCTION_OPEN_DRAWER, FUNCTION_STORE_RECEIPT_EXPRESS_ACTION, FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION, FUNCTION_PRINT_LAST_RECEIPT;
//	FUNCTION_LOCK, FUNCTION_LOGOUT, FUNCTION_SHUTDOWN, FUNCTION_SELECT_CUSTOMER, FUNCTION_STORE_RECEIPT, FUNCTION_SHOW_CURRENT_RECEIPT_LIST, FUNCTION_SHOW_PARKED_RECEIPT_LIST, FUNCTION_SHOW_COIN_COUNTER_PANEL, FUNCTION_RESTITUTION, FUNCTION_TOTAL_SALES, FUNCTION_SALESPOINT_SALES, FUNCTION_USER_SALES, FUNCTION_OPEN_DRAWER, FUNCTION_STORE_RECEIPT_EXPRESS_ACTION, FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION, FUNCTION_PRINT_LAST_RECEIPT, FUNCTION_FREE_COPY, FUNCTION_REVERSE_RECEIPT;
	FUNCTION_LOCK, FUNCTION_LOGOUT, FUNCTION_SHUTDOWN, FUNCTION_SELECT_CUSTOMER, FUNCTION_SHOW_CURRENT_RECEIPT_LIST, FUNCTION_SHOW_PARKED_RECEIPT_LIST, FUNCTION_SHOW_COIN_COUNTER_PANEL, FUNCTION_RESTITUTION, FUNCTION_TOTAL_SALES, FUNCTION_SALESPOINT_SALES, FUNCTION_USER_SALES, FUNCTION_OPEN_DRAWER, FUNCTION_STORE_RECEIPT_EXPRESS_ACTION, FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION, FUNCTION_PRINT_LAST_RECEIPT, FUNCTION_FREE_COPY, FUNCTION_REVERSE_RECEIPT;

	/**
	 * 
	 * @return functions that can be used in tabs
	 */
	public static FunctionType[] assignables()
	{
		List<FunctionType> assignables = new ArrayList<FunctionType>();
		for (FunctionType functionType : FunctionType.values())
		{
			if (functionType.isAssignable())
			{
				assignables.add(functionType);
			}
		}
		return assignables.toArray(new FunctionType[0]);
	}

	public boolean isFailOverEnabled()
	{
		switch (this)
		{
			case FUNCTION_SELECT_CUSTOMER:
			{
				return false;
			}
			default:
			{
				return true;
			}
		}
	}

	public boolean isLockable()
	{
		switch (this)
		{
			case FUNCTION_LOCK:
			{
				return false;
			}
			default:
			{
				return true;
			}
		}
	}

	public boolean isAssignable()
	{
		switch (this)
		{
			case FUNCTION_REVERSE_RECEIPT:
			{
				return false;
			}
			default:
			{
				return true;
			}
		}
	}

	public String key()
	{
		switch (this)
		{
			case FUNCTION_LOCK:
			{
				return "function.lock";
			}
			case FUNCTION_LOGOUT:
			{
				return "function.logout";
			}
			case FUNCTION_SHUTDOWN:
			{
				return "function.shutdown";
			}
			case FUNCTION_SELECT_CUSTOMER:
			{
				return "function.customer";
			}
//			case FUNCTION_STORE_RECEIPT:
//			{
//				return "function.store.receipt";
//			}
			case FUNCTION_SHOW_CURRENT_RECEIPT_LIST:
			{
				return "function.show.current.receipt.list";
			}
			case FUNCTION_SHOW_PARKED_RECEIPT_LIST:
			{
				return "function.show.parked.receipt.list";
			}
			case FUNCTION_SHOW_COIN_COUNTER_PANEL:
			{
				return "function.show.counter.panel";
			}
			case FUNCTION_RESTITUTION:
			{
				return "function.restitution";
			}
			case FUNCTION_TOTAL_SALES:
			{
				return "function.sales.total";
			}
			case FUNCTION_SALESPOINT_SALES:
			{
				return "function.sales.salespoint";
			}
			case FUNCTION_USER_SALES:
			{
				return "function.sales.user";
			}
			case FUNCTION_OPEN_DRAWER:
			{
				return "function.open.drawer";
			}
			case FUNCTION_STORE_RECEIPT_EXPRESS_ACTION:
			{
				return "function.store.receipt.express";
			}
			case FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION:
			{
				return "function.store.receipt.shorthand";
			}
			case FUNCTION_PRINT_LAST_RECEIPT:
			{
				return "function.print.last.receipt";
			}
			case FUNCTION_FREE_COPY:
			{
				return "function.free.copy";
			}
			case FUNCTION_REVERSE_RECEIPT:
			{
				return "function.reverse.receipt";
			}
			default:
			{
				throw new RuntimeException("No such action type");
			}
		}
	}

	public String toCode()
	{
		switch (this)
		{
			case FUNCTION_LOCK:
			{
				return "Sperren";
			}
			case FUNCTION_LOGOUT:
			{
				return "Abmelden";
			}
			case FUNCTION_SHUTDOWN:
			{
				return "Beenden";
			}
			case FUNCTION_SELECT_CUSTOMER:
			{
				return "Kunde";
			}
//			case FUNCTION_STORE_RECEIPT:
//			{
//				return "Beleg speichern";
//			}
			case FUNCTION_SHOW_CURRENT_RECEIPT_LIST:
			{
				return "Belegliste";
			}
			case FUNCTION_SHOW_PARKED_RECEIPT_LIST:
			{
				return "Parkieren";
			}
			case FUNCTION_SHOW_COIN_COUNTER_PANEL:
			{
				return "Abschluss";
			}
			case FUNCTION_RESTITUTION:
			{
				return "Rücknahme";
			}
			case FUNCTION_TOTAL_SALES:
			{
				return "Umsatz";
			}
			case FUNCTION_SALESPOINT_SALES:
			{
				return "Kassenumsatz";
			}
			case FUNCTION_USER_SALES:
			{
				return "Benutzerumsatz";
			}
			case FUNCTION_OPEN_DRAWER:
			{
				return "Schublade";
			}
			case FUNCTION_STORE_RECEIPT_EXPRESS_ACTION:
			{
				return "Beleg abschliessen";
			}
			case FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION:
			{
				return "Express speichern";
			}
			case FUNCTION_PRINT_LAST_RECEIPT:
			{
				return "Beleg drucken";
			}
			case FUNCTION_FREE_COPY:
			{
				return "Gratis";
			}
			case FUNCTION_REVERSE_RECEIPT:
			{
				return "Beleg stornieren";
			}
			default:
			{
				throw new RuntimeException("No such action type");
			}
		}
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case FUNCTION_LOCK:
			{
				return "Kasse sperren";
			}
			case FUNCTION_LOGOUT:
			{
				return "Benutzer abmelden";
			}
			case FUNCTION_SHUTDOWN:
			{
				return "Kasse beenden";
			}
			case FUNCTION_SELECT_CUSTOMER:
			{
				return "Kundenauswahl";
			}
//			case FUNCTION_STORE_RECEIPT:
//			{
//				return "Beleg speichern";
//			}
			case FUNCTION_SHOW_CURRENT_RECEIPT_LIST:
			{
				return "Belegliste zeigen";
			}
			case FUNCTION_SHOW_PARKED_RECEIPT_LIST:
			{
				return "Belege parkieren";
			}
			case FUNCTION_SHOW_COIN_COUNTER_PANEL:
			{
				return "Tagesabschluss durchführen";
			}
			case FUNCTION_RESTITUTION:
			{
				return "Rücknahme eines Artikels";
			}
			case FUNCTION_TOTAL_SALES:
			{
				return "Gesamten Tagesumsatz anzeigen";
			}
			case FUNCTION_SALESPOINT_SALES:
			{
				return "Kassenumsatz anzeigen";
			}
			case FUNCTION_USER_SALES:
			{
				return "Benutzerumsatz anzeigen";
			}
			case FUNCTION_OPEN_DRAWER:
			{
				return "Schublade öffnen";
			}
			case FUNCTION_STORE_RECEIPT_EXPRESS_ACTION:
			{
				return "Rückgeld berechnen und Beleg speichern";
			}
			case FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION:
			{
				return "Differenz berechnen und Beleg speichern";
			}
			case FUNCTION_PRINT_LAST_RECEIPT:
			{
				return "Letzten Beleg drucken";
			}
			case FUNCTION_FREE_COPY:
			{
				return "Gratis";
			}
			case FUNCTION_REVERSE_RECEIPT:
			{
				return "Beleg stornieren";
			}
			default:
			{
				throw new RuntimeException("Invalid Action Type selected");
			}
		}
	}

}
