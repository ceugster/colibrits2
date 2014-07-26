package ch.eugster.colibri.provider.service;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Tax;
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
	boolean checkBarcode(Barcode barcode);
	
	IStatus findAndRead(Barcode barcode, Position position);

	IStatus selectCustomer(Position position, ProductGroup productGroup);

	ProviderConfiguration getConfiguration();
	
	boolean canMap(CurrentTax currentTax);

	boolean canMap(Tax tax);

	String getImageName();

	boolean isConnect();
	
	String getName();
	
	void updateCustomer(Receipt receipt);
	
	void setStatus(IStatus status);
	
	IStatus checkTaxCodes(PersistenceService service);
}
