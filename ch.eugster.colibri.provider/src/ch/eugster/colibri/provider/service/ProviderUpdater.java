package ch.eugster.colibri.provider.service;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;

public interface ProviderUpdater 
{
	IStatus updateProvider(Position position);

	IStatus updateProvider(Payment payment);
	
	String getName();
	
	String getProviderId();
	
	Map<String, IProperty> getProperties();

	Map<String, IProperty> getDefaultProperties();
	
	boolean canCheckConnection();

	IStatus checkConnection(Map<String, ProviderProperty> properties);
	
	boolean isSalespointSpecificPossible();
	
	Section[] getSections();
}
