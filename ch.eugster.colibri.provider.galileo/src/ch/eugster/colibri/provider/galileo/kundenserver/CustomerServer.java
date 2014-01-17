/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.kundenserver;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;

public abstract class CustomerServer implements ICustomerServer
{
	protected Map<String, IProperty> properties;

	protected boolean open;

	protected IStatus status;

	public boolean isConnect()
	{
		IProperty property = properties.get(GalileoProperty.CONNECT.key());
		return Integer.valueOf(property.value()).intValue() > 0;
	}
	
	public CustomerServer(Map<String, IProperty> properties)
	{
		this.properties = properties;
	}
	
	public IStatus selectCustomer(final Position position)
	{
		return selectCustomer(position, null);
	}
	
	public abstract IStatus selectCustomer(Position position, ProductGroup productGroup);
	
}
