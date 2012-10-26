package ch.eugster.colibri.provider.service;

import java.util.Map;

import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.ProviderConfiguration;

public interface ProviderPropertySelector
{
	ProviderConfiguration getConfiguration();

	IProperty[] getProperties();

	String getProviderId();

	Map<String, ProviderProperty> getProviderProperties();

	Map<String, ProviderProperty> getProviderProperties(Salespoint salespoint);
}
