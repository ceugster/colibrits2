package ch.eugster.colibri.provider.service;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.service.ConnectionService;
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
	
	boolean canCheckConnection();

	boolean doCheckFailover();
	
	IStatus checkConnection();
	
	IStatus checkConnection(Map<String, IProperty> properties);
	
	boolean isSalespointSpecificPossible();
	
	Section[] getSections();
	
	Integer getRanking();

	Collection<Position> getPositions(ConnectionService service, int max, ConnectionService.ConnectionType connectionType);

	IStatus updatePositions(ConnectionService connectionService, Collection<Position> positions, IProgressMonitor monitor);

	Collection<Payment> getPayments(ConnectionService service, int max, ConnectionService.ConnectionType connectionType);

	IStatus updatePayments(ConnectionService connectionService, Collection<Payment> payments, IProgressMonitor monitor);

	public boolean doUpdatePositions(ConnectionService.ConnectionType connectionType);

	public boolean doUpdatePayments(ConnectionService.ConnectionType connectionType);
}
