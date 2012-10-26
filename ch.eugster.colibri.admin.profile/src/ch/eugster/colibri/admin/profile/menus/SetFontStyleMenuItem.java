/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class SetFontStyleMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	private int style;

	public SetFontStyleMenuItem(TabEditor editor, TabEditorButton button, String label, String actionCommand, int style)
	{
		super();
		this.setLabel(label);
		this.setActionCommand(actionCommand);
		this.editor = editor;
		this.button = button;
		this.style = style;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (this.editor.getFailOverState())
		{
			int style = this.button.getKeys()[1].getFailOverFontStyle();
			style = this.style == Font.PLAIN ? Font.PLAIN : style | this.style;
			this.button.getKeys()[1].setFailOverFontStyle(style);
		}
		else
		{
			int style = this.button.getKeys()[1].getNormalFontStyle();
			style = this.style == Font.PLAIN ? Font.PLAIN : style | this.style;
			this.button.getKeys()[1].setNormalFontStyle(style);
		}
		this.button.getKeys()[1].setDeleted(false);
		this.button.update(this.editor.getFailOverState());
	}

}
