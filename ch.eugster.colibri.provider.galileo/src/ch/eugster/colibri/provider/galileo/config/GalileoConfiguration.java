package ch.eugster.colibri.provider.galileo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.ProviderConfiguration;
import ch.eugster.colibri.provider.configuration.SchedulerProperty;
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
	public String[] getAllDefaultPropertyValues()
	{
		final Collection<String> values = new ArrayList<String>();
		IProperty[] properties = Property.values();
		for (int i = 0; i < properties.length; i++)
		{
			values.add(properties[i].value());
		}
		properties = SchedulerProperty.values();
		for (int i = 0; i < properties.length; i++)
		{
			values.add(properties[i].value());
		}
		return values.toArray(new String[0]);
	}

	@Override
	public Map<String, String> getDefaultPropertiesAsMap()
	{
		final Map<String, String> map = new HashMap<String, String>();
		final IProperty[] properties = Property.values();
		for (final IProperty property : properties)
		{
			map.put(property.key(), property.value());
		}
		return map;
	}

	@Override
	public String[] getDefaultPropertyValues()
	{
		final Property[] properties = Property.values();
		final String[] values = new String[properties.length];
		for (int i = 0; i < values.length; i++)
		{
			values[i] = properties[i].value();
		}
		return values;
	}

	@Override
	public String[] getDefaultSchedulerPropertyValues()
	{
		final IProperty[] properties = SchedulerProperty.values();
		final String[] values = new String[properties.length];
		for (int i = 0; i < values.length; i++)
		{
			values[i] = properties[i].value();
		}
		return values;
	}

	@Override
	public String getImageName()
	{
		return "galileo.png";
	}

	@Override
	public String[] getKeys()
	{
		final Property[] properties = Property.values();
		final String[] keys = new String[properties.length];
		for (int i = 0; i < keys.length; i++)
		{
			keys[i] = properties[i].key();
		}
		return keys;
	}

	@Override
	public String getName()
	{
		return "Galileo";
	}

	@Override
	public String getProviderId()
	{
		return Activator.PLUGIN_ID;
	}

	@Override
	public int getReceiptsPerSchedule()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSchedulerDelay()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSchedulerPeriod()
	{
		// TODO Auto-generated method stub
		return 0;
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

	public enum Property implements IProperty
	{
		DATABASE_PATH, CONNECT, KEEP_CONNECTION;

		public static Map<String, IProperty> asMap()
		{
			Map<String, IProperty> map = new HashMap<String, IProperty>();
			for (IProperty property : values())
			{
				map.put(property.key(), property);
			}
			return map;
		}

		@Override
		public String control()
		{
			if (this.equals(DATABASE_PATH))
			{
				return FileDialog.class.getName();
			}
			else if (this.equals(KEEP_CONNECTION))
			{
				return Button.class.getName();
			}
			else if (this.equals(CONNECT))
			{
				return Button.class.getName();
			}
			else
			{
				throw new RuntimeException("Invalid key");
			}
		}

		@Override
		public String[] filter()
		{
			if (this.equals(DATABASE_PATH))
			{
				return new String[] { "galidata.dbc", "*.dbc" };
			}
			else if (this.equals(KEEP_CONNECTION))
			{
				return null;
			}
			else if (this.equals(CONNECT))
			{
				return null;
			}
			else
			{
				throw new RuntimeException("Invalid key");
			}
		}

		@Override
		public String key()
		{
			if (this.equals(DATABASE_PATH))
			{
				return "galileo.database.path";
			}
			else if (this.equals(KEEP_CONNECTION))
			{
				return "galileo.keep.connection";
			}
			else if (this.equals(CONNECT))
			{
				return "galileo.connect";
			}
			else
			{
				throw new RuntimeException("Invalid key");
			}
		}

		@Override
		public String label()
		{
			if (this.equals(DATABASE_PATH))
			{
				return "Datenbankpfad";
			}
			else if (this.equals(KEEP_CONNECTION))
			{
				return "Verbindung aufrechterhalten";
			}
			else if (this.equals(CONNECT))
			{
				return "Verbindung verwenden";
			}
			else
			{
				throw new RuntimeException("Invalid key");
			}
		}

		@Override
		public String label2()
		{
			if (this.equals(DATABASE_PATH))
			{
				return "";
			}
			else if (this.equals(KEEP_CONNECTION))
			{
				return "(galserve, wgserve, kundenserver)";
			}
			else if (this.equals(CONNECT))
			{
				return "(galserve, wgserve, kundenserver)";
			}
			else
			{
				throw new RuntimeException("Invalid key");
			}
		}

		@Override
		public String value()
		{
			if (this.equals(DATABASE_PATH))
			{
				return "C:/Comeliv/Galileo/Data/Galidata.dbc";
			}
			else if (this.equals(KEEP_CONNECTION))
			{
				return Boolean.toString(false);
			}
			else if (this.equals(CONNECT))
			{
				return Boolean.toString(true);
			}
			else
			{
				throw new RuntimeException("Invalid key");
			}
		}
	}

}
