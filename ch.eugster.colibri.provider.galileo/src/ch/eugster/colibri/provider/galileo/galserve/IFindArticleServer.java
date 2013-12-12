package ch.eugster.colibri.provider.galileo.galserve;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.model.Position;

public interface IFindArticleServer extends IServer
{
	IStatus checkConnection(String path);

	IStatus findAndRead(final Barcode barcode, final Position position);

	boolean isConnect();
//	Customer selectCustomer(final Barcode barcode) throws Exception;
}