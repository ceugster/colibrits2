package ch.eugster.colibri.provider.galileo.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.provider.configuration.IDirtyable;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.AvailableControl;
import ch.eugster.colibri.provider.configuration.IProperty.Section;
import ch.eugster.colibri.provider.configuration.ProviderConfiguration;
import ch.eugster.colibri.provider.galileo.Activator;

public class GalileoConfiguration implements ProviderConfiguration
{
	@Override
	public boolean canMap(final CurrentTax currentTax)
	{
		return false;
	}

	@Override
	public boolean canMap(final Tax tax)
	{
		return true;
	}

	@Override
	public String getImageName()
	{
		return "galileo.png";
	}

	@Override
	public String getName()
	{
		return "Galileo";
	}

	@Override
	public String getProviderId()
	{
		return Activator.getDefault().getBundle().getSymbolicName();
	}

	@Override
	public boolean updateLocalItems()
	{
		return true;
	}

	@Override
	public boolean bookProvider(ProductGroupType productGroupType)
	{
		if (updateLocalItems())
		{
			return productGroupType.equals(ProductGroupType.SALES_RELATED);
		}
		return false;
	}

	public enum GalileoProperty implements IProperty
	{
		DATABASE_PATH, CONNECT, KEEP_CONNECTION;

		private ProviderProperty persistedProperty;
		
		public static Map<String, IProperty> asMap()
		{
			Map<String, IProperty> map = new HashMap<String, IProperty>();
			for (IProperty property : GalileoProperty.values())
			{
				map.put(property.key(), property);
			}
			return map;
		}

		public static IProperty[] properties(Section section)
		{
			List<IProperty> properties = new ArrayList<IProperty>();
			for (GalileoProperty property : GalileoProperty.values())
			{
				if (property.section().equals(section))
				{
					properties.add(property);
				}
			}
			return properties.toArray(new IProperty[0]);
		}
		
		@Override
		public String control()
		{
			switch(this)
			{
			case DATABASE_PATH:
			{
				return AvailableControl.FILE_DIALOG.controlName();
			}
			case KEEP_CONNECTION:
			{
				return AvailableControl.BUTTON.controlName();
			}
			case CONNECT:
			{
				return AvailableControl.BUTTON.controlName();
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		@Override
		public String[] filter()
		{
			switch(this)
			{
			case DATABASE_PATH:
			{
				return new String[] { "galidata.dbc", "*.dbc" };
			}
			default:
			{
				return  null;
			}
			}
		}

		@Override
		public String key()
		{
			switch(this)
			{
			case DATABASE_PATH:
			{
				return "galileo.database.path";
			}
			case KEEP_CONNECTION:
			{
				return "galileo.keep.connection";
			}
			case CONNECT:
			{
				return "galileo.connect";
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		@Override
		public String label()
		{
			switch(this)
			{
			case DATABASE_PATH:
			{
				return "Datenbankpfad";
			}
			case KEEP_CONNECTION:
			{
				return "Verbindung aufrechterhalten";
			}
			case CONNECT:
			{
				return "Verbindung verwenden";
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		@Override
		public String label2()
		{
			switch(this)
			{
			case KEEP_CONNECTION:
			{
				return "";
			}
			case CONNECT:
			{
				/*
				 * Für jeden Wert in validValues 1 labeltext, getrennt durch |
				 */
				return "Keine|Galileo VFP (galserve, wgserve, kundenserver)|Galileo SQL (galserve2g, wgserve2g, kundenserver2g)";
			}
			default:
			{
				return "";
			}
			}
		}

		@Override
		public String value()
		{
			if (this.persistedProperty != null && !this.persistedProperty.isDeleted())
			{
				return this.persistedProperty.getValue(defaultValue());
			}
			return defaultValue();
		}

		public String defaultValue()
		{
			switch(this)
			{
			case DATABASE_PATH:
			{
				String hostname = "C:";
				try 
				{
					InetAddress addr = InetAddress.getLocalHost();
					hostname = "//" + addr.getHostName();
				} 
				catch (UnknownHostException e) 
				{
				}
				return hostname + "/Comeliv/Galileo/Data/Galidata.dbc";
			}
			case KEEP_CONNECTION:
			{
				return Integer.toString(0);
			}
			case CONNECT:
			{
				return Integer.toString(0);
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		public int[] validValues()
		{
			if (this.equals(CONNECT))
			{
				return new int[] { 0, 1, 2 };
			}
			if (this.equals(KEEP_CONNECTION))
			{
				return new int[] { 0, 1 };
			}
			return null;
		}

		@Override
		public Class<?> valueType() 
		{
			switch(this)
			{
			case DATABASE_PATH:
			{
				return String.class;
			}
			case KEEP_CONNECTION:
			{
				return Integer.class;
			}
			case CONNECT:
			{
				return Integer.class;
			}
			default:
			{
				throw new RuntimeException("Invalid property");
			}
			}
		}

		@Override
		public Properties controlProperties() 
		{
			return new Properties();
		}

		@Override
		public void setPersistedProperty(
				ProviderProperty persistedProperty) 
		{
			if (this.persistedProperty == null || this.persistedProperty.isDeleted() || this.persistedProperty.getSalespoint() == null)
			{
				this.persistedProperty = persistedProperty;
			}
		}

		@Override
		public boolean isDefaultValue(String value) 
		{
			return this.defaultValue().equals(value);
		}

		@Override
		public ProviderProperty getPersistedProperty() 
		{
			return persistedProperty;
		}

		@Override
		public String value(IProperty property, org.eclipse.swt.widgets.Control control) 
		{
			for (AvailableControl availableControl : AvailableControl.values())
			{
				if (availableControl.controlName().equals(property.control()))
				{
					return availableControl.value(control);
				}
			}
			return null;
		}

		@Override
		public Section section() 
		{
			return GalileoSection.GALILEO;
		}

		@Override
		public org.eclipse.swt.widgets.Control createControl(Composite parent, FormToolkit formToolkit, IDirtyable dirtyable, int cols, int[] validValues) 
		{
			for (AvailableControl availableControl : AvailableControl.values())
			{
				if (availableControl.controlName().equals(this.control()))
				{
					return availableControl.create(parent, formToolkit, this, dirtyable, cols, validValues);
				}
			}
			return null;
		}

//		@Override
//		public void set(String value) 
//		{
//			if (this.persistedProperty == null || this.persistedProperty)
//			{
//				persistedProperty = ProviderProperty.newInstance(new GalileoConfiguration().getProviderId());
//			}
//			if (this.persistedProperty.getSalespoint() == null)
//			persistedProperty.setKey(this.key());
//			persistedProperty.setValue(value);
//		}
//
//		@Override
//		public void set(String value, Salespoint salespoint) 
//		{
//			if (this.persistedProperty == null)
//			{
//				persistedProperty = ProviderProperty.newInstance(new GalileoConfiguration().getProviderId(), salespoint);
//			}
//			persistedProperty.setKey(this.key());
//			persistedProperty.setValue(value);
//		}

		@Override
		public void set(IProperty property, org.eclipse.swt.widgets.Control control, String value) 
		{
			for (AvailableControl availableControl : AvailableControl.values())
			{
				if (availableControl.controlName().equals(property.control()))
				{
					availableControl.value(control, value);
					break;
				}
			}
		}

		@Override
		public String providerId() 
		{
			return Activator.getDefault().getBundle().getSymbolicName();
		}

//		@Override
//		public void update(String value) 
//		{
//			if (this.persistedProperty == null)
//			{
//				this.persistedProperty = ProviderProperty.newInstance(new GalileoConfiguration().getProviderId());
//				this.persistedProperty.setKey(this.key());
//				this.persistedProperty.setValue(value);
//			}
//		}
	}

	public enum GalileoSection implements Section
	{
		GALILEO;
		
		public String title()
		{
			return "Galileo";
		}
		
		public int columns()
		{
			int cols = 0;
			for (IProperty property : properties())
			{
				for (AvailableControl control : AvailableControl.values())
				{
					if (control.controlName().equals(property.control()))
					{
						cols = Math.max(cols, control.columns(property));
					}
				}
			}
			return cols;
		}

		@Override
		public IProperty[] properties() 
		{
			return GalileoProperty.properties(this);
		}
	}
}
