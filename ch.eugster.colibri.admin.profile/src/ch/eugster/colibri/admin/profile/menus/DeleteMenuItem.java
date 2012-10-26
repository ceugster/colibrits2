/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class DeleteMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditorButton button;

	public DeleteMenuItem(final TabEditorButton button, final String label, final String actionCommand)
	{
		super();
		this.setLabel(label);
		this.setActionCommand(actionCommand);
		this.button = button;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		this.button.getKeys()[1].setDeleted(true);
		this.button.remove(this);
		this.button.update(new Point(this.button.getKeys()[1].getTabRow(), this.button.getKeys()[1].getTabCol()));
	}
}
