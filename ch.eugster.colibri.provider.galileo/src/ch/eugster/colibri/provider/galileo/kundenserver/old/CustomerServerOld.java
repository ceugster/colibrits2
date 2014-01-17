/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.kundenserver.old;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.galileo.kundenserver.CustomerServer;

public class CustomerServerOld extends CustomerServer
{
	private Ikundenserver kserver;

	public CustomerServerOld(Map<String, IProperty> properties)
	{
		super(properties);
	}
	
	public IStatus selectCustomer(final Position position, final ProductGroup productGroup)
	{
		IProperty property = properties.get(GalileoProperty.DATABASE_PATH.key());
		String database = property.value();
		boolean wasopen = this.open;
		if (!this.open)
		{
			this.open = CustomerServerOld.this.kserver.db_open(database);
		}
		if (this.open)
		{
			Customer customer = null;
			final int result = CustomerServerOld.this.kserver.getkundennr();
			if ((result != 0))
			{
				customer = new Customer();
				customer.setId(result);
				customer.setSalutation(CustomerServerOld.this.kserver.canrede().toString());
				customer.setEmail(CustomerServerOld.this.kserver.cemail().toString());
				customer.setCountry(CustomerServerOld.this.kserver.cland().toString());
				customer.setLastname(CustomerServerOld.this.kserver.cnamE1().toString());
				customer.setLastname2(CustomerServerOld.this.kserver.cnamE2().toString());
				customer.setLastname3(CustomerServerOld.this.kserver.cnamE3().toString());
				customer.setMobile(CustomerServerOld.this.kserver.cnatel().toString());
				customer.setCity(CustomerServerOld.this.kserver.cort().toString());
				customer.setZip(CustomerServerOld.this.kserver.cplz().toString());
				customer.setAddress(CustomerServerOld.this.kserver.cstrasse().toString());
				customer.setFax(CustomerServerOld.this.kserver.ctelefax().toString());
				customer.setPhone(CustomerServerOld.this.kserver.ctelefon().toString());
				customer.setPhone2(CustomerServerOld.this.kserver.ctelefoN2().toString());
				customer.setPersonalTitle(CustomerServerOld.this.kserver.ctitel().toString());
				customer.setFirstname(CustomerServerOld.this.kserver.cvorname().toString());
				customer.setHasAccount((Boolean) CustomerServerOld.this.kserver.lkundkarte());
				customer.setAccount((Double) CustomerServerOld.this.kserver.nkundkonto());
				final double nachlass = Math.abs((Double) CustomerServerOld.this.kserver.nnachlass());
				final double discount = BigDecimal.valueOf(nachlass / 100).round(new MathContext(2)).doubleValue();
				customer.setDiscount(discount);
				Object object = CustomerServerOld.this.kserver.lrggewaehlt();
				if (object instanceof Boolean && ((Boolean) object).booleanValue())
				{
					final Product product = Product.newInstance(position);
					Integer value = (Integer) CustomerServerOld.this.kserver.nrgnummer();
					product.setInvoiceNumber(value.toString());
					position.setProduct(product);
					position.setQuantity(1);
					position.setPrice((Double) CustomerServerOld.this.kserver.nrgbetrag());
					position.setDiscount(discount);
					position.setSearchValue(product.getInvoiceNumber());
					position.setProductGroup(productGroup);
					position.setOption(Option.PAYED_INVOICE);
				}
				this.status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
						Topic.CUSTOMER_UPDATE.topic());
			}
			else
			{
				CustomerServerOld.this.status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(),
						Topic.CUSTOMER_UPDATE.topic());
			}
			position.getReceipt().setCustomer(customer);

			property = properties.get(GalileoProperty.KEEP_CONNECTION.key());
			int keepConnection = Integer.valueOf(property.value()).intValue();
			if (keepConnection == 0)
			{
				if (!wasopen)
				{
					this.open = !((Boolean) CustomerServerOld.this.kserver.db_close()).booleanValue();
				}
			}
		}
		else
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.CUSTOMER_UPDATE.topic(), new Exception("Keine Verbindung"));
		}
		return this.status;
	}
	
	public IStatus start()
	{
		this.status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		try
		{
			kserver = ClassFactory.createkundenserver();
		}
		catch(Throwable e)
		{
			this.status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Die Verbindung zu Warenbewirtschaftung kann nicht hergestellt werden.", e);
		}
		return this.status;
	}

	public void stop()
	{
		if (this.kserver != null)
		{
			this.kserver = null;
		}
	}

}
