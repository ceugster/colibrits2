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

import javax.swing.SwingConstants;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class SetAlignMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	private int align;

	public SetAlignMenuItem(TabEditor editor, TabEditorButton button, String label, String actionCommand, int align)
	{
		super();
		this.setLabel(label);
		this.setActionCommand(actionCommand);
		this.editor = editor;
		this.button = button;
		this.align = align;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		int command = Integer.parseInt(this.getActionCommand());
		switch (this.align)
		{
		case SwingConstants.HORIZONTAL:
			if (this.editor.getFailOverState())
				this.button.getKeys()[1].setFailOverHorizontalAlign(command);
			else
				this.button.getKeys()[1].setNormalHorizontalAlign(command);
			break;
		case SwingConstants.VERTICAL:
			if (this.editor.getFailOverState())
				this.button.getKeys()[1].setFailOverVerticalAlign(command);
			else
				this.button.getKeys()[1].setNormalVerticalAlign(command);
			break;
		}
		this.button.getKeys()[1].setDeleted(false);
		this.button.update(this.editor.getFailOverState());
	}
}
