package ch.eugster.colibri.admin.ui.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public abstract class AbstractPreferenceStore extends ScopedPreferenceStore
{
	protected File prefs;
	
	protected Properties props;
	
	@SuppressWarnings("deprecation")
	public AbstractPreferenceStore(String scope)
	{
		super(new InstanceScope(), scope);
	}
	
	public abstract String getPreferenceFilename();
	
	protected abstract Properties getDefaultProperties();
	
	public void load()
	{
		// try
		// {
		// File dir =
		// LocalActivator.getDefault().getLocalPreferencesDirectory();
		// this.prefs = new
		// File(dir.getAbsolutePath().concat(File.separator.concat(this.getPreferenceFilename())));
		// this.props = new Properties(this.getDefaultProperties());
		// this.props.load(new FileInputStream(this.prefs));
		// Set<Object> objects = this.props.keySet();
		// for (Object object : objects)
		// {
		// String key = (String) object;
		// this.setValue(key, this.props.getProperty(key));
		// }
		// }
		// catch (FileNotFoundException e)
		// {
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// // TODO
		// }
	}
	
	@Override
	public void save()
	{
		if (this.needsSaving())
		{
			Set<Object> objects = this.props.keySet();
			for (Object object : objects)
			{
				String key = (String) object;
				this.props.setProperty(key, this.getString(key));
			}
			try
			{
				String comment = "Änderungen an der Datei führen zu nicht voraussehbarem Verhalten des Programms!";
				this.props.store(new FileOutputStream(this.prefs), comment);
			}
			catch (FileNotFoundException e)
			{
				// TODO
			}
			catch (IOException e)
			{
				// TODO
			}
		}
	}
}
