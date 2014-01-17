/*
 * Created on 18.07.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import java.util.Map;

import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.galileo.galserve.old.FindArticleServerOldCom4j;
import ch.eugster.colibri.provider.galileo.galserve.old.UpdateProviderServerOldCom4j;
import ch.eugster.colibri.provider.galileo.galserve.sql.FindArticleServerSqlCom4j;
import ch.eugster.colibri.provider.galileo.galserve.sql.UpdateProviderServerSqlCom4j;


public class GalserverFactory
{
	public static IFindArticleServer createFindArticleServer(PersistenceService persistenceService, Map<String, IProperty> properties)
	{
		IProperty property = properties.get(GalileoProperty.CONNECT.key());
		int connect = Integer.valueOf(property.value()).intValue();
		if (connect == 1)
		{
			return new FindArticleServerOldCom4j(persistenceService, properties);
		}
		else if (connect == 2)
		{
			return new FindArticleServerSqlCom4j(persistenceService, properties);
		}
		return null;
	}

	public static IUpdateProviderServer createUpdateProviderServer(PersistenceService persistenceService, Map<String, IProperty> properties)
	{
		IProperty property = properties.get(GalileoProperty.CONNECT.key());
		int connect = Integer.valueOf(property.value()).intValue();
		if (connect == 1)
		{
			return new UpdateProviderServerOldCom4j(persistenceService, properties);
		}
		else if (connect == 2)
		{
			return new UpdateProviderServerSqlCom4j(persistenceService, properties);
		}
		return null;
	}

}
