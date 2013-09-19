package ch.eugster.colibri.provider.service;

import java.util.Map;

import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.ProviderConfiguration;

public interface ProviderPropertySelector
{
	ProviderConfiguration getConfiguration();

	IProperty[] getProperties();

	String getProviderId();

	Map<String, IProperty> getProviderProperties();

	Map<String, IProperty> getProviderProperties(Salespoint salespoint);
}
