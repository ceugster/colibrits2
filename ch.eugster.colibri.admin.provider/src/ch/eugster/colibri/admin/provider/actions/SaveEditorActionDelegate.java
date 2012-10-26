/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.provider.actions;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;

public class SaveEditorActionDelegate extends EditorActionDelegate
{
	@Override
	public void run(final IAction action)
	{
		if (editorPart.isDirty())
		{
			editorPart.doSave(new NullProgressMonitor());
		}
	}

}
