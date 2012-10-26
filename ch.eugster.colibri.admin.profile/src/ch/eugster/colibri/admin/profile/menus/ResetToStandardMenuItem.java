/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class ResetToStandardMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	public ResetToStandardMenuItem(TabEditor editor, TabEditorButton button)
	{
		super();
		this.setLabel("Auf Standard zurücksetzen");
		this.editor = editor;
		this.button = button;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		this.button.reset();
		this.button.getKeys()[1].setDeleted(false);
		this.button.update(ResetToStandardMenuItem.this.editor.getFailOverState());
	}
}
