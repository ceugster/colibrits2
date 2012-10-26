package ch.eugster.colibri.admin.provider.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderConfigurator;

public class ProviderPropertiesEditorInput implements IEditorInput
{
	private PersistenceService persistenceService;

	private ProviderConfigurator providerConfigurator;

	public ProviderPropertiesEditorInput(final PersistenceService persistenceService, final ProviderConfigurator configurator)
	{
		this.persistenceService = persistenceService;
		this.providerConfigurator = configurator;
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

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "";
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

	public ProviderConfigurator getProviderConfigurator()
	{
		return this.providerConfigurator;
	}

	@Override
	public String getToolTipText()
	{
		return "";
	}

}
