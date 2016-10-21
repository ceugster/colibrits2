package ch.eugster.colibri.persistence.events;

import org.eclipse.core.runtime.IStatus;

public enum Topic 
{
	SCHEDULED, SCHEDULED_PROVIDER_UPDATE, SCHEDULED_TRANSFER, CUSTOMER_UPDATE, STORE_RECEIPT, PRINT_SETTLEMENT, PRINT_RECEIPT, PRINT_VOUCHER, SETTLE_PERFORMED, LOCAL_DATABASE, DISPLAY_ERROR, PRINT_ERROR, USER_LOGGED_IN, SALESPOINT_CLOSED, PAYMENT_ADDED, POSITION_ADDED, CLIENT_STARTED, PROVIDER_QUERY, DATABASE_COMPATIBILITY_ERROR, LOCK, UNLOCK;
	
	public static String[] topics()
	{
		Topic[] topics = Topic.values();
		String[] names = new String[topics.length];
		for (int i = 0; i < topics.length; i++)
		{
			names[i] = topics[i].topic();
		}
		return names;
	}
	
	public String topic()
	{
		switch(this)
		{
		case SCHEDULED:
		{
			return "ch/eugster/colibri/scheduled";
		}
		case SCHEDULED_PROVIDER_UPDATE:
		{
			return "ch/eugster/colibri/provider/update";
		}
		case SCHEDULED_TRANSFER:
		{
			return "ch/eugster/colibri/transfer";
		}
		case CUSTOMER_UPDATE:
		{
			return "ch/eugster/colibri/provider/customer/update";
		}
		case STORE_RECEIPT:
		{
			return "ch/eugster/colibri/store/receipt";
		}
		case PRINT_SETTLEMENT:
		{
			return "ch/eugster/colibri/print/settlement";
		}
		case PRINT_RECEIPT:
		{
			return "ch/eugster/colibri/print/receipt";
		}
		case PRINT_VOUCHER:
		{
			return "ch/eugster/colibri/print/voucher";
		}
		case SETTLE_PERFORMED:
		{
			return "ch/eugster/colibri/SETTLE_PERFORMED";
		}
		case LOCAL_DATABASE:
		{
			return "ch/eugster/colibri/local/database";
		}
		case DISPLAY_ERROR:
		{
			return "ch/eugster/colibri/display/error";
		}
		case PRINT_ERROR:
		{
			return "ch/eugster/colibri/printer/error";
		}
		case USER_LOGGED_IN:
		{
			return "ch/eugster/colibri/client/user/added";
		}
		case SALESPOINT_CLOSED:
		{
			return "ch/eugster/colibri/client/salespoint/closed";
		}
		case PAYMENT_ADDED:
		{
			return "ch/eugster/colibri/add/payment";
		}
		case POSITION_ADDED:
		{
			return "ch/eugster/colibri/add/position";
		}
		case CLIENT_STARTED:
		{
			return "ch/eugster/colibri/client/started";
		}
		case PROVIDER_QUERY:
		{
			return "ch/eugster/colibri/provider/query";
		}
		case DATABASE_COMPATIBILITY_ERROR:
		{
			return "ch/eugster/colibri/persistence/connection/compatibility/error";
		}
		case LOCK:
		{
			return "ch/eugster/colibri/local/database/lock";
		}
		case UNLOCK:
		{
			return "ch/eugster/colibri/local/database/unlock";
		}
		default:
		{
			return "";
		}
		}
	}

	public String ok()
	{
		switch(this)
		{
		case SCHEDULED:
		{
			return "Aktualisiert";
		}
		case SCHEDULED_PROVIDER_UPDATE:
		{
			return "Aktualisierung durchgeführt.";
		}
		case SCHEDULED_TRANSFER:
		{
			return "Transfer durchgeführt.";
		}
		case CUSTOMER_UPDATE:
		{
			return "Kunde aktualisiert.";
		}
		case STORE_RECEIPT:
		{
			return "Speichere Beleg";
		}
		case PRINT_SETTLEMENT:
		{
			return "Tagesabschluss wird gedruckt.";
		}
		case PRINT_RECEIPT:
		{
			return "Beleg wird gedruckt.";
		}
		case PRINT_VOUCHER:
		{
			return "Gutschein wird gedruckt.";
		}
		case SETTLE_PERFORMED:
		{
			return "Tagesabschluss durchgeführt.";
		}
		case LOCAL_DATABASE:
		{
			return "Verbindung zur lokalen Datenbank hergestellt.";
		}
		case DISPLAY_ERROR:
		{
			return "Kundendisplay kann nicht angesprochen werden.";
		}
		case PRINT_ERROR:
		{
			return "Belegdrucker kann nicht angesprochen werden.";
		}
		case USER_LOGGED_IN:
		{
			return "Benutzer hat sich angemeldet.";
		}
		case SALESPOINT_CLOSED:
		{
			return "Kasse wurde geschlossen.";
		}
		case PAYMENT_ADDED:
		{
			return "Zahlung hinzugefügt.";
		}
		case POSITION_ADDED:
		{
			return "Position hinzugefügt.";
		}
		case CLIENT_STARTED:
		{
			return "Kasse gestartet.";
		}
		case PROVIDER_QUERY:
		{
			return "Abfrage in externem System";
		}
		case DATABASE_COMPATIBILITY_ERROR:
		{
			return "Die Version der Datenbank ist aktueller als die Version der Anwendung. Bitte installieren Sie ein Programm, das kompatibel mit der aktuellen Datenbankstruktur ist. Um Inkonsistenzen in der Datenbank zu vermeiden, wird die Anwendung beendet.";
		}
		case LOCK:
		{
			return "Kasse gesperrt.";
		}
		case UNLOCK:
		{
			return "Kasse entsperrt.";
		}
		default:
		{
			return "";
		}
		}
	}

	public String error()
	{
		switch(this)
		{
		case SCHEDULED:
		{
			return "Aktualisierung fehlgeschlagen";
		}
		case SCHEDULED_PROVIDER_UPDATE:
		{
			return "Aktualisierung fehlgeschlagen.";
		}
		case SCHEDULED_TRANSFER:
		{
			return "Transfer fehlgeschlagen.";
		}
		case CUSTOMER_UPDATE:
		{
			return "Kundenaktualisierung fehlgeschlagen.";
		}
		case STORE_RECEIPT:
		{
			return "Beleg speichern fehlgeschlagen.";
		}
		case PRINT_SETTLEMENT:
		{
			return "Drucken Tagesabschluss fehlgeschlagen.";
		}
		case PRINT_RECEIPT:
		{
			return "Beleg drucken fehlgeschlagen.";
		}
		case PRINT_VOUCHER:
		{
			return "Gutschein drucken fehlgeschlagen.";
		}
		case SETTLE_PERFORMED:
		{
			return "Tagesabschluss fehlgeschlagen.";
		}
		case LOCAL_DATABASE:
		{
			return "Verbindung zur lokalen Datenbank fehlgeschlagen.";
		}
		case DISPLAY_ERROR:
		{
			return "Kundendisplay kann nicht angesprochen werden.";
		}
		case PRINT_ERROR:
		{
			return "Belegdrucker kann nicht angesprochen werden.";
		}
		case USER_LOGGED_IN:
		{
			return "Benutzerangemeldung fehlgeschlagen.";
		}
		case SALESPOINT_CLOSED:
		{
			return "Kasse schliessen fehlgeschlagen.";
		}
		case PAYMENT_ADDED:
		{
			return "Zahlung hinzufügen fehlgeschlagen.";
		}
		case POSITION_ADDED:
		{
			return "Position hinzufügen fehlgeschlagen.";
		}
		case CLIENT_STARTED:
		{
			return "Kasse starten fehlgeschlagen.";
		}
		case PROVIDER_QUERY:
		{
			return "Abfrage in externem System fehlgeschlagen";
		}
		case DATABASE_COMPATIBILITY_ERROR:
		{
			return "Die Version der Datenbank ist aktueller als die Version der Anwendung. Bitte installieren Sie ein Programm, das kompatibel mit der aktuellen Datenbankstruktur ist. Um Inkonsistenzen in der Datenbank zu vermeiden, wird die Anwendung beendet.";
		}
		case LOCK:
		{
			return "Kasse gesperrt.";
		}
		case UNLOCK:
		{
			return "Kasse entsperrt.";
		}
		default:
		{
			return "";
		}
		}
	}

	public String icon(IStatus status)
	{
		switch(status.getSeverity())
		{
		case IStatus.OK:
		{
			return "ok";
		}
		case IStatus.WARNING:
		{
			return "exclamation";
		}
		case IStatus.ERROR:
		{
			return "error";
		}
		default:
		{
			return null;
		}
		}
	}
}
