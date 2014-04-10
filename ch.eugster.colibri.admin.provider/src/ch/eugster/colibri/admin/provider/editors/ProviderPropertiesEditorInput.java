package ch.eugster.colibri.admin.provider.editors;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.service.ProviderUpdater;
import ch.eugster.colibri.scheduler.service.UpdateScheduler;

public class ProviderPropertiesEditorInput implements IEditorInput
{
	private PersistenceService persistenceService;

	private Map<String, IProperty> properties;
	
	private ProviderUpdater providerUpdater;
	
	private UpdateScheduler updateScheduler;
	
	public ProviderPropertiesEditorInput(final PersistenceService persistenceService, UpdateScheduler updateScheduler)
	{
		this.persistenceService = persistenceService;
		this.updateScheduler = updateScheduler;
		this.properties = initializeProperties(UpdateScheduler.SchedulerProperty.asMap());
	}

	public ProviderPropertiesEditorInput(final PersistenceService persistenceService, ProviderUpdater providerUpdater)
	{
		this.persistenceService = persistenceService;
		this.providerUpdater = providerUpdater;
		this.properties = initializeProperties(providerUpdater.getDefaultProperties());
	}

	private Map<String, IProperty> initializeProperties(Map<String, IProperty> properties)
	{
		ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getServerService().getQuery(ProviderProperty.class);
		Collection<ProviderProperty> providerProperties = query.selectByProvider(this.getProviderId());
		for (ProviderProperty providerProperty : providerProperties)
		{
			IProperty property = properties.get(providerProperty.getKey());
			if (property != null)
			{
				property.setPersistedProperty(providerProperty);
			}
		}
		return properties;
	}
	
	public IProperty.Section[] getSections()
	{
		return updateScheduler == null ? providerUpdater.getSections() : updateScheduler.getSections();
	}
	
	@Override
	public boolean exists()
	{
		return false;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object getAdapter(final Class adapter)
	{
		return null;
	}

	public String getProviderId()
	{
		return updateScheduler == null ? providerUpdater.getProviderId() : updateScheduler.getProviderId();
	}
	
	public Map<String, IProperty> getProperties()
	{
		return properties;
	}
	
	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return updateScheduler == null ? providerUpdater.getName() : updateScheduler.getName();
	}

	@Override
	public IPersistableElement getPersistable()
	{
		return null;
	}

	public PersistenceService getPersistenceService()
	{
		return this.persistenceService;
	}

	@Override
	public String getToolTipText()
	{
		return "";
	}

	public boolean canCheckConnection()
	{
		return updateScheduler == null ? providerUpdater.canTestConnection() : false;
	}

	public IStatus checkConnection(Map<String, IProperty> properties)
	{
//		return updateScheduler == null ? providerUpdater.testConnection(properties) : Status.OK_STATUS;
		return providerUpdater.testConnection(properties);
	}
	
	public Map<String, IProperty> getDefaultProperties()
	{
		return updateScheduler == null ? providerUpdater.getDefaultProperties() : UpdateScheduler.SchedulerProperty.asMap();
	}

	public boolean equals(Object other)
	{
		if (other == null)
		{
			return false;
		}
		if (other.getClass().equals(this.getClass()))
		{
			ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) other;
			return this.getProviderId().equals(input.getProviderId());
		}
		return false;
	}
}
