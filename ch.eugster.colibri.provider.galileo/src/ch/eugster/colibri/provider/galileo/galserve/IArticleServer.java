package ch.eugster.colibri.provider.galileo.galserve;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.product.Customer;

public interface IArticleServer
{
	IStatus checkConnection(String path);

	IStatus findAndRead(final Barcode barcode, final Position position);

	IStatus updateProvider(final Position position);

	IStatus start();
	
	void stop();

}