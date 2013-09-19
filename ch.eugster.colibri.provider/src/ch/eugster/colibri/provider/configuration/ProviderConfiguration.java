package ch.eugster.colibri.provider.configuration;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public interface ProviderConfiguration
{
	boolean canMap(CurrentTax currentTax);

	boolean canMap(Tax tax);

	String getImageName();

	String getName();

	String getProviderId();

	boolean bookProvider(ProductGroupType productGroupType);

	/**
	 * 
	 * @return <code>true</code> if non provider items have to be updated to the
	 *         provider too
	 */
	boolean updateLocalItems();

}
