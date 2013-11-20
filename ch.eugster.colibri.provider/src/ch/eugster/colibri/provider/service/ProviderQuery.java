package ch.eugster.colibri.provider.service;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.ProviderConfiguration;

public interface ProviderQuery extends ProviderService
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

	ProviderConfiguration getConfiguration();
	
	String getProviderId();
	
	boolean isConnect();
	
	String getName();
	
	void setStatus(IStatus status);
	
	IStatus checkTaxCodes(PersistenceService service);
}
