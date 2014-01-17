/*
 * Created on 18.07.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.kundenserver;

import java.util.Map;

import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.galileo.kundenserver.old.CustomerServerOld;
import ch.eugster.colibri.provider.galileo.kundenserver.sql.CustomerServerSql;


public class CustomerServerFactory
{
	public static ICustomerServer createCustomerServer(Map<String, IProperty> properties)
	{
		IProperty property = properties.get(GalileoProperty.CONNECT.key());
		int connect = Integer.valueOf(property.value()).intValue();
		if (connect == 1)
		{
			return new CustomerServerOld(properties);
		}
		else if (connect == 2)
		{
			return new CustomerServerSql(properties);
		}
		return null;
	}
}
