package ch.eugster.colibri.provider.configuration;

public interface IProperty
{
	String control();

	String[] filter();

	String key();

	String label();

	String value();
}