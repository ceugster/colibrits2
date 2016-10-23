/*
 * Created on 25.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import java.util.Collection;
import java.util.Map;

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;

public abstract class AbstractFindArticleServer extends AbstractGalileoServer
{
	public AbstractFindArticleServer(PersistenceService persistenceService, Map<String, IProperty> properties)
	{
		super(persistenceService, properties);
	}
	
	/*
	 * The failOverMode indicates the state of connection. If failOverMode == false 
	 * set the variable open to false and set this.status to ERROR. Use this method
	 * only in case where the program should stay responsible to user (i.e. findAndRead)
	 */
	public abstract boolean open(boolean failOverMode);
	
	protected ExternalProductGroup findExternalProductGroup(String code)
	{
		ExternalProductGroup externalProductGroup = null;
		if (persistenceService != null)
		{
			final ExternalProductGroupQuery query = (ExternalProductGroupQuery) persistenceService.getCacheService()
					.getQuery(ExternalProductGroup.class);
			externalProductGroup = query.selectByProviderAndCode(Activator.getDefault().getConfiguration().getProviderId(), code);
			if (externalProductGroup == null)
			{
				externalProductGroup = findDefaultExternalProductGroup();
			}
		}
		return externalProductGroup;
	}
	
	protected ExternalProductGroup findDefaultExternalProductGroup()
	{
		ExternalProductGroup externalProductGroup = null;
		if (persistenceService != null)
		{
			CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) persistenceService.getCacheService().getQuery(CommonSettings.class);
			CommonSettings commonSettings = commonSettingsQuery.findDefault();
			Collection<ProductGroupMapping> mappings = commonSettings.getDefaultProductGroup().getProductGroupMappings(Activator.getDefault().getConfiguration().getProviderId());
			if (!mappings.isEmpty())
			{
				externalProductGroup = mappings.iterator().next().getExternalProductGroup();
			}
		}
		return externalProductGroup;
	}
}
