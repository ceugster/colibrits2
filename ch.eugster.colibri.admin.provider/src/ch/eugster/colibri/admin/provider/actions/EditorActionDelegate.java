/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.provider.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import ch.eugster.colibri.admin.provider.editors.ProviderPropertiesEditor;

public abstract class EditorActionDelegate implements IEditorActionDelegate
{
	protected ProviderPropertiesEditor editorPart;

	protected IAction action;

	@Override
	public void selectionChanged(final IAction action, final ISelection selection)
	{
	}

	@Override
	public void setActiveEditor(final IAction action, final IEditorPart targetEditor)
	{
		this.action = action;
		this.editorPart = (ProviderPropertiesEditor) targetEditor;
	}

}
