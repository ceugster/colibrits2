/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.ui.menus;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;

@SuppressWarnings("rawtypes")
public abstract class EditorActionDelegate<T extends AbstractEntityEditor> implements IEditorActionDelegate
{
	protected T editorPart;

	protected IAction action;

	@Override
	public void selectionChanged(final IAction action, final ISelection selection)
	{
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setActiveEditor(final IAction action, final IEditorPart targetEditor)
	{
		this.action = action;
		this.editorPart = (T) targetEditor;
	}

}
