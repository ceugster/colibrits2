package ch.eugster.colibri.scheduler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.provider.configuration.IDirtyable;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;
import ch.eugster.colibri.scheduler.Activator;

public interface UpdateScheduler
{
	String getName();

	Section[] getSections();
	
	String getProviderId();
	
	public enum SchedulerSection implements Section
	{
		SECTION;
		
		public String title()
		{
			return "Aktualisierungsplanung";
		}

		@Override
		public int columns() 
		{
			return 3;
		}

		@Override
		public IProperty[] properties() 
		{
			return SchedulerProperty.properties(this);
		}
	}
	
	public enum SchedulerProperty implements IProperty
	{
		SCHEDULER_DELAY, SCHEDULER_PERIOD, SCHEDULER_COUNT, SCHEDULER_FAILOVER_MESSAGE_FREQUENCY;

		private ProviderProperty persistedProperty = null;
		
		public static Map<String, IProperty> asMap()
		{
			Map<String, IProperty> properties = new HashMap<String, IProperty>();
			for (IProperty property : SchedulerProperty.values())
			{
				properties.put(property.key(), property);
			}
			return properties;
		}
		
		public static IProperty[] properties(Section section)
		{
			List<IProperty> properties = new ArrayList<IProperty>();
			for (SchedulerProperty property : SchedulerProperty.values())
			{
				if (property.section().equals(section))
				{
					properties.add(property);
				}
			}
			return properties.toArray(new IProperty[0]);
		}
		
		public String control()
		{
			switch (this)
			{
			case SCHEDULER_DELAY:
			{
				return AvailableControl.SPINNER.controlName();
			}
			case SCHEDULER_PERIOD:
			{
				return AvailableControl.SPINNER.controlName();
			}
			case SCHEDULER_COUNT:
			{
				return AvailableControl.SPINNER.controlName();
			}
			case SCHEDULER_FAILOVER_MESSAGE_FREQUENCY:
			{
				return AvailableControl.SPINNER.controlName();
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}
		
		public Class<?> valueType()
		{
			return Long.class;
		}
		
		public boolean isPath()
		{
			return false;
		}
		
		public String[] filter()
		{
			switch(this)
			{
			case SCHEDULER_DELAY:
			{
				return null;
			}
			case SCHEDULER_PERIOD:
			{
				return null;
			}
			case SCHEDULER_COUNT:
			{
				return null;
			}
			case SCHEDULER_FAILOVER_MESSAGE_FREQUENCY:
			{
				return null;
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		public String key()
		{
			switch(this)
			{
			case SCHEDULER_DELAY:
			{
				return "scheduler.delay";
			}
			case SCHEDULER_PERIOD:
			{
				return "scheduler.period";
			}
			case SCHEDULER_COUNT:
			{
				return "scheduler.receipt.count";
			}
			case SCHEDULER_FAILOVER_MESSAGE_FREQUENCY:
			{
				return "scheduler.failover.message.frequency";
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		public String label()
		{
			switch(this)
			{
			case SCHEDULER_DELAY:
			{
				return "Startverzögerung";
			}
			case SCHEDULER_PERIOD:
			{
				return "Periodische Wiederholung";
			}
			case SCHEDULER_COUNT:
			{
				return "Pro Aktualisierungslauf";
			}
			case SCHEDULER_FAILOVER_MESSAGE_FREQUENCY:
			{
				return "Failover-Meldung Frequenz";
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		public String defaultValue()
		{
			switch(this)
			{
			case SCHEDULER_DELAY:
			{
				return "30";
			}
			case SCHEDULER_PERIOD:
			{
				return "15";
			}
			case SCHEDULER_COUNT:
			{
				return "10";
			}
			case SCHEDULER_FAILOVER_MESSAGE_FREQUENCY:
			{
				return "60";
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		public String value()
		{
			if (this.persistedProperty != null)
			{
				return this.persistedProperty.getValue(defaultValue());
			}
			return defaultValue();
		}

		@Override
		public String label2()
		{
			switch(this)
			{
			case SCHEDULER_DELAY:
			{
				return "Sekunden";
			}
			case SCHEDULER_PERIOD:
			{
				return "Sekunden";
			}
			case SCHEDULER_COUNT:
			{
				return "verbuchen";
			}
			case SCHEDULER_FAILOVER_MESSAGE_FREQUENCY:
			{
				return "Minuten";
			}
			default:
			{
				return "";
			}
			}
		}

		public Properties controlProperties()
		{
			switch (this)
			{
			case SCHEDULER_DELAY:
			{
				return getProperties(45, 1, 3600, 0, 1, 10, 32);
			}
			case SCHEDULER_PERIOD:
			{
				return getProperties(30, 1, 3600, 0, 1, 10, 32);
			}
			case SCHEDULER_COUNT:
			{
				return getProperties(5, 0, 1000, 0, 1, 10, 32);
			}
			case SCHEDULER_FAILOVER_MESSAGE_FREQUENCY:
			{
				return getProperties(60, 1, 60 * 24, 0, 1, 10, 32);
			}
			default:
			{
				Properties properties = new Properties();
				return properties;
			}
			}
		}

		private Properties getProperties(int selection, int minimum, int maximum, int digits, int increment, int pageIncrement, int width)
		{
			Properties properties = new Properties();
			properties.setProperty(SpinnerPropertyKey.SELECTION.key(), Integer.valueOf(selection).toString());
			properties.setProperty(SpinnerPropertyKey.MINIMUM.key(), Integer.valueOf(minimum).toString());
			properties.setProperty(SpinnerPropertyKey.MAXIMUM.key(), Integer.valueOf(maximum).toString());
			properties.setProperty(SpinnerPropertyKey.DIGITS.key(), Integer.valueOf(digits).toString());
			properties.setProperty(SpinnerPropertyKey.INCREMENT.key(), Integer.valueOf(increment).toString());
			properties.setProperty(SpinnerPropertyKey.PAGE_INCREMENT.key(), Integer.valueOf(pageIncrement).toString());
			properties.setProperty(SpinnerPropertyKey.WIDTH.key(), Integer.valueOf(width).toString());
			return properties;
		}
		
		@Override
		public void setPersistedProperty(ProviderProperty persistedProperty) 
		{
			if (this.persistedProperty == null || this.persistedProperty.isDeleted() || this.persistedProperty.getSalespoint() == null)
			{
				this.persistedProperty = persistedProperty;
			}
		}

		@Override
		public boolean isDefaultValue(String value)
		{
			return defaultValue().equals(value);
		}

		@Override
		public ProviderProperty getPersistedProperty() 
		{
			return persistedProperty;
		}

		@Override
		public String value(org.eclipse.swt.widgets.Control control) 
		{
			for (AvailableControl availableControl : AvailableControl.values())
			{
				if (availableControl.controlName().equals(control.getClass().getName()))
				{
					return availableControl.value(control);
				}
			}
			return null;
		}

		@Override
		public Section section() 
		{
			return SchedulerSection.SECTION;
		}

		@Override
		public org.eclipse.swt.widgets.Control createControl(Composite parent, FormToolkit formToolkit, IDirtyable dirtyable, int cols) 
		{
			for (AvailableControl availableControl : AvailableControl.values())
			{
				if (availableControl.controlName().equals(this.control()))
				{
					return availableControl.create(parent, formToolkit, this, dirtyable, cols);
				}
			}
			return null;
		}

		@Override
		public void set(org.eclipse.swt.widgets.Control control, String value) 
		{
			for (AvailableControl availableControl : AvailableControl.values())
			{
				if (availableControl.controlName().equals(control.getClass().getName()))
				{
					availableControl.value(control, value);
				}
			}
		}

		@Override
		public String providerId() 
		{
			return Activator.getDefault().getBundleContext().getBundle().getSymbolicName();
		}

	}
}
