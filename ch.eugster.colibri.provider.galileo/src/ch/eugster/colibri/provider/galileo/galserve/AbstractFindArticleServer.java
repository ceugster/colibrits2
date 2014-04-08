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
	
//	protected class GalserveReadObject
//	{
//		private String autor;
//		private String bestand;
//		private boolean bestellt;
//		private String bestnummer;
//		private String bznr;
//		private String canrede;
//		private String cemail;
//		private String cland;
//		private String cnamE1;
//		private String cnamE2;
//		private String cnamE3;
//		private String cnatel;
//		private String cort;
//		private String cplz;
//		private String cstrasse;
//		private String ctelefax;
//		private String ctelefon;
//		private String ctelefoN2;
//		private String ctitel;
//		private String cversion;
//		private String cversionsnr;
//		private String cvorname;
//		private String galversion;
//		private String gefunden;
//		private String isbn;
//		private String keinrabatt;
//		private int kundennr;
//		private boolean lagerabholfach;
//		private String lastvkdat;
//		private boolean lkundkarte;
//		private int menge;
//		private String mwst;
//		private boolean nichtbuchen;
//		private double nkundkonto;
//		private double nnachlass;
//		private double preis;
//		private String titel;
//		private String verlag;
//		private String wgruppe;
//		public String getAutor() {
//			return autor;
//		}
//
//		public String getBestand() {
//			return bestand;
//		}
//		public boolean isBestellt() {
//			return bestellt;
//		}
//		public String getBestnummer() {
//			return bestnummer;
//		}
//		public String getBznr() {
//			return bznr;
//		}
//		public String getCanrede() {
//			return canrede;
//		}
//		public String getCemail() {
//			return cemail;
//		}
//		public String getCland() {
//			return cland;
//		}
//		public String getCnamE1() {
//			return cnamE1;
//		}
//		public String getCnamE2() {
//			return cnamE2;
//		}
//		public String getCnamE3() {
//			return cnamE3;
//		}
//		public String getCnatel() {
//			return cnatel;
//		}
//		public String getCort() {
//			return cort;
//		}
//		public String getCplz() {
//			return cplz;
//		}
//		public String getCstrasse() {
//			return cstrasse;
//		}
//		public String getCtelefax() {
//			return ctelefax;
//		}
//		public String getCtelefon() {
//			return ctelefon;
//		}
//		public String getCtelefoN2() {
//			return ctelefoN2;
//		}
//		public String getCtitel() {
//			return ctitel;
//		}
//		public String getCversion() {
//			return cversion;
//		}
//		public String getCversionsnr() {
//			return cversionsnr;
//		}
//		public String getCvorname() {
//			return cvorname;
//		}
//		public String getGalversion() {
//			return galversion;
//		}
//		public String getGefunden() {
//			return gefunden;
//		}
//		public String getIsbn() {
//			return isbn;
//		}
//		public String getKeinrabatt() {
//			return keinrabatt;
//		}
//		public int getKundennr() {
//			return kundennr;
//		}
//		public boolean isLagerabholfach() {
//			return lagerabholfach;
//		}
//		public String getLastvkdat() {
//			return lastvkdat;
//		}
//		public boolean isLkundkarte() {
//			return lkundkarte;
//		}
//		public int getMenge() {
//			return menge;
//		}
//		public String getMwst() {
//			return mwst;
//		}
//		public boolean isNichtbuchen() {
//			return nichtbuchen;
//		}
//		public double getNkundkonto() {
//			return nkundkonto;
//		}
//		public double getNnachlass() {
//			return nnachlass;
//		}
//		public double getPreis() {
//			return preis;
//		}
//		public String getTitel() {
//			return titel;
//		}
//		public String getVerlag() {
//			return verlag;
//		}
//		public String getWgruppe() {
//			return wgruppe;
//		}
//	}
//
}
