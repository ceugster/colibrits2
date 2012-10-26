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

public class SetFontSizeMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	private float size;

	public SetFontSizeMenuItem(TabEditor editor, TabEditorButton button, String label, String actionCommand, float size)
	{
		super();
		this.setLabel(label);
		this.setActionCommand(actionCommand);
		this.editor = editor;
		this.button = button;
		this.size = size;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (this.editor.getFailOverState())
		{
			this.button.getKeys()[1].setFailOverFontSize(this.size);
		}
		else
		{
			this.button.getKeys()[1].setNormalFontSize(this.size);
		}
		this.button.getKeys()[1].setDeleted(false);
		this.button.update(this.editor.getFailOverState());
	}

}
