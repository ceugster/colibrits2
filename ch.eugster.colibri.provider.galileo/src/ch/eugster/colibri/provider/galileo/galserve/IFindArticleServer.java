package ch.eugster.colibri.provider.galileo.galserve;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.product.Customer;

public interface IFindArticleServer extends IServer
{
	IStatus findAndRead(final Barcode barcode, final Position position);

	boolean open();
	
	boolean isConnect();
	
	Customer getCustomer(int customerId);
}