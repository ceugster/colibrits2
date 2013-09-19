package ch.eugster.colibri.persistence.model;

import java.util.Properties;

import ch.eugster.colibri.persistence.model.ProviderProperty;

public interface IProperty
{
	String defaultValue();
	
	Class<?> valueType();
	
	String key();
	
	String value();
	
	String[] filter();
	
	String control();
	
	String label();
	
	Properties controlProperties();
	
	void setPersistedProperty(ProviderProperty persistedProperty);

	String label2();
	
	ProviderProperty getPersistedProperty();

	boolean isDefaultValue(String value);
	
	public interface IPropertyKey
	{
		String key();
	}

	public enum NoPropertyKey implements IPropertyKey
	{
		;
		public String key()
		{
			return "";
		}
	}

	public enum SpinnerPropertyKey implements IPropertyKey
	{
		SELECTION, MINIMUM, MAXIMUM, DIGITS, INCREMENT, PAGE_INCREMENT, WIDTH;
		
		public String key()
		{
			switch(this)
			{
			case SELECTION:
			{
				return "selection";
			}
			case MINIMUM:
			{
				return "minimum";
			}
			case MAXIMUM:
			{
				return "maximum";
			}
			case DIGITS:
			{
				return "digits";
			}
			case INCREMENT:
			{
				return "increment";
			}
			case PAGE_INCREMENT:
			{
				return "pageIncrement";
			}
			case WIDTH:
			{
				return "width";
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}
	}
}
