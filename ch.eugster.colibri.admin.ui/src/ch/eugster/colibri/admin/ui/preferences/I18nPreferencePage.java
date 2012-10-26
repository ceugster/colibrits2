/*
 * Created on 12.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.ui.preferences;

import java.util.Locale;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.eugster.colibri.admin.ui.Activator;

public class I18nPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private ComboFieldEditor languageEditor;

	public I18nPreferencePage()
	{
		super(FieldEditorPreferencePage.GRID);

	}

	public I18nPreferencePage(final int style)
	{
		super(style);
	}

	public I18nPreferencePage(final String title, final ImageDescriptor image, final int style)
	{
		super(title, image, style);
	}

	public I18nPreferencePage(final String title, final int style)
	{
		super(title, style);
	}

	@Override
	public void init(final IWorkbench workbench)
	{
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors()
	{
		final Locale[] locales = IPreferenceConstants.AVAILABLE_LOCALES;
		final String[][] list = new String[locales.length][2];
		for (int i = 0; i < locales.length; i++)
		{
			list[i][0] = locales[i].getDisplayLanguage(locales[i]);
			list[i][1] = locales[i].getLanguage() + (locales[i].getCountry().equals("") ? "" : "|" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ locales[i].getCountry());
		}

		languageEditor = new ComboFieldEditor(IPreferenceConstants.KEY_LANGUAGE, "Sprache", list, getFieldEditorParent());

		addField(languageEditor);

		performDefaults();
	}

	// @Override
	// protected void performDefaults()
	// {
	// this.languageEditor.loadDefault();
	// super.performDefaults();
	// }

	// @Override
	// public boolean performOk()
	// {
	// this.languageEditor.store();
	// return super.performOk();
	// }
}
