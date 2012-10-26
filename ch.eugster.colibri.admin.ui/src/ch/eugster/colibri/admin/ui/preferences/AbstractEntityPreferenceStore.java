package ch.eugster.colibri.admin.ui.preferences;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public abstract class AbstractEntityPreferenceStore extends ScopedPreferenceStore
{
	protected File prefs;
	
	protected Properties props;
	
	public AbstractEntityPreferenceStore(IScopeContext ctx, String scope)
	{
		super(ctx, scope);
	}
	
	public abstract void load();
	
	@Override
	public abstract void save();
}
