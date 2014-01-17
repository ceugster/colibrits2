/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.kundenserver;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;

public interface ICustomerServer
{
	IStatus selectCustomer(final Position position);

	boolean isConnect();

	IStatus selectCustomer(final Position position, final ProductGroup productGroup);

	IStatus start();

	void stop();
}
