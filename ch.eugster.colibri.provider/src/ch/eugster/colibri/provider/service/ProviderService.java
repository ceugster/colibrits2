package ch.eugster.colibri.provider.service;

import java.util.Map;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.provider.configuration.IProperty;

public interface ProviderService
{
	boolean canMap(CurrentTax currentTax);

	boolean canMap(Tax tax);

	String getImageName();

	String getName();

	Map<String, IProperty> getProperties();

	Map<String, IProperty> getDefaultProperties();

	String getProviderId();
}
