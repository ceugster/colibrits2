package ch.eugster.colibri.provider.service;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;

public interface ProviderUpdater extends Comparable<ProviderUpdater>
{
	IStatus updateProvider(Position position);

	IStatus updateProvider(Payment payment);
	
	String getName();
	
	String getProviderId();
	
	Map<String, IProperty> getProperties();

	Map<String, IProperty> getDefaultProperties();
	
	boolean canTestConnection();

	boolean doCheckFailover();
	
	IStatus checkConnection();

	IStatus testConnection(Map<String, IProperty> properties);
	
	boolean isSalespointSpecificPossible();
	
	Section[] getSections();
	
	Integer getRanking();

	List<Position> getPositions(PersistenceService service, int max);

	IStatus updatePositions(PersistenceService persistenceService, List<Position> positions);

	List<Payment> getPayments(PersistenceService service, int max);

	IStatus updatePayments(PersistenceService persistenceService, List<Payment> payments);

	boolean doUpdatePositions();

	boolean doUpdatePayments();
	
	boolean isActive();
	
	long countPositions(PersistenceService service);
	
	long countPayments(PersistenceService service);
}
