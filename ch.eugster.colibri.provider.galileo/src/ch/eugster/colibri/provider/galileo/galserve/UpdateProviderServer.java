package ch.eugster.colibri.provider.galileo.galserve;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.product.Customer;

public interface UpdateProviderServer
{
	IStatus checkConnection(String path);

	IStatus deleteOrdered(final Position position);

	IStatus findAndRead(final Barcode barcode, final Position position);

	String getBibIniPath() throws Exception;

	Customer selectCustomer(final Barcode barcode) throws Exception;

	void stop();

	IStatus updateProvider(final Position position);

}