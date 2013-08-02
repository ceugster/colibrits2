package ch.eugster.colibri.provider.service;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;

public interface ProviderInterface extends ProviderService
{
	/**
	 * 
	 * @param code
	 *            String containing eventually a barcode sequence
	 * @return <code>true</code> if found, <code>false</code> otherwise
	 * 
	 * @throws IOException
	 */
	IStatus findAndRead(Barcode barcode, Position position);

	IStatus selectCustomer(Position position, ProductGroup productGroup);

	IStatus updateProvider(Position position);

	IStatus checkConnection(Map<String, IProperty> properties);

	IStatus checkTaxCodes(PersistenceService service);
	
	boolean isConnect();
	
	enum Topic
	{
		ARTICLE_UPDATE, CUSTOMER_UPDATE, PROVIDER_TAX_NOT_SPECIFIED, PROVIDER_FAILOVER, PROVIDER_UPDATE_ERROR;

		public static String[] topics()
		{
			String[] topics = new String[Topic.values().length];
			for (int i = 0; i < Topic.values().length; i++)
			{
				topics[i] = Topic.values()[i].topic();
			}
			return topics;
		}
		
		public String topic()
		{
			switch(this)
			{
			case ARTICLE_UPDATE:
			{
				return "ch/eugster/colibri/provider/article/update";
			}
			case CUSTOMER_UPDATE:
			{
				return "ch/eugster/colibri/provider/customer/update";
			}
			case PROVIDER_TAX_NOT_SPECIFIED:
			{
				return "ch/eugster/colibri/provider/tax/not/specified";
			}
			case PROVIDER_FAILOVER:
			{
				return "ch/eugster/colibri/provider/failover";
			}
			case PROVIDER_UPDATE_ERROR:
			{
				return "ch/eugster/colibri/provider/update/error";
			}
			default:
			{
				return "";
			}
			}
		}

		public String message()
		{
			switch(this)
			{
			case ARTICLE_UPDATE:
			{
				return "Zu verbuchen";
			}
			case CUSTOMER_UPDATE:
			{
				return "";
			}
			case PROVIDER_TAX_NOT_SPECIFIED:
			{
				return "Keine Mwst";
			}
			case PROVIDER_FAILOVER:
			{
				return "Keine Verbindung.";
			}
			case PROVIDER_UPDATE_ERROR:
			{
				return "Beim Aktualisieren der Daten f�r die Warenbewirtschaftung ist ein Fehler aufgetreten.";
			}
			default:
			{
				return "";
			}
			}
		}

		public String icon()
		{
			switch(this)
			{
			case ARTICLE_UPDATE:
			{
				return "ok";
			}
			case CUSTOMER_UPDATE:
			{
				return "";
			}
			case PROVIDER_TAX_NOT_SPECIFIED:
			{
				return "error";
			}
			case PROVIDER_FAILOVER:
			{
				return "error";
			}
			case PROVIDER_UPDATE_ERROR:
			{
				return "error";
			}
			default:
			{
				return "";
			}
			}
		}
	}
}
