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
import ch.eugster.colibri.provider.scheduler.service.ProviderUpdateScheduler;
import ch.eugster.colibri.provider.service.ProviderUpdater;

public class ProviderPropertiesEditorInput implements IEditorInput
{
	private PersistenceService persistenceService;

	private Map<String, IProperty> properties;
	
	private ProviderUpdater providerUpdater;
	
	private ProviderUpdateScheduler providerUpdateScheduler;
	
	public ProviderPropertiesEditorInput(final PersistenceService persistenceService, ProviderUpdateScheduler providerUpdateScheduler)
	{
		this.persistenceService = persistenceService;
		this.providerUpdateScheduler = providerUpdateScheduler;
		this.properties = initializeProperties(ProviderUpdateScheduler.SchedulerProperty.asMap());
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
		return providerUpdateScheduler == null ? providerUpdater.getSections() : providerUpdateScheduler.getSections();
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
		return providerUpdateScheduler == null ? providerUpdater.getProviderId() : providerUpdateScheduler.getProviderId();
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
		return providerUpdateScheduler == null ? providerUpdater.getName() : providerUpdateScheduler.getName();
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
		return providerUpdateScheduler == null ? providerUpdater.canCheckConnection() : false;
	}

	public IStatus checkConnection(Map<String, ProviderProperty> properties)
	{
		return providerUpdateScheduler == null ? providerUpdater.checkConnection(properties) : Status.OK_STATUS;
	}
	
	public Map<String, IProperty> getDefaultProperties()
	{
		return providerUpdateScheduler == null ? providerUpdater.getDefaultProperties() : ProviderUpdateScheduler.SchedulerProperty.asMap();
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
