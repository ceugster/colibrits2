package ch.eugster.colibri.provider.service;

import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.ProviderConfiguration;

public interface ProviderIdService
{
	ProviderConfiguration getConfiguration();

	IProperty[] getProperties();

	String getProviderId();

	String getProviderLabel();
}
