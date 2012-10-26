package ch.eugster.colibri.admin.ui.preferences;

import java.util.Properties;

import ch.eugster.colibri.admin.ui.Activator;

public class LanguagePreferenceStore extends AbstractPreferenceStore
{
	public static final String PREFERENCE_FILE_NAME = "language.prefs";

	public static final String KEY_SELECTED_LANGUAGE = "selected.language";

	public LanguagePreferenceStore()
	{
		super(Activator.PLUGIN_ID);
	}

	@Override
	public String getPreferenceFilename()
	{
		return LanguagePreferenceStore.PREFERENCE_FILE_NAME;
	}

	@Override
	protected Properties getDefaultProperties()
	{
		final Properties properties = new Properties();
		properties.setProperty(LanguagePreferenceStore.KEY_SELECTED_LANGUAGE, "de_CH");
		return properties;
	}

}
