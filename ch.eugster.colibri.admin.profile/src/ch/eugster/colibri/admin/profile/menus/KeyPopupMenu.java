/*
 * Created on 14.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.Font;
import java.awt.PopupMenu;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public abstract class KeyPopupMenu extends PopupMenu implements MouseListener
{
	protected TabEditor editor;
	
	protected TabEditorButton button;
	
	public static final long serialVersionUID = 0l;
	
	public KeyPopupMenu(TabEditorButton button, TabEditor editor)
	{
		this.editor = editor;
		this.button = button;
		this.setFont(this.button.getFont().deriveFont(Font.PLAIN, 11f));
		this.init();
		this.button.addMouseListener(this);
	}
	
	protected abstract void init();
	
	@Override
	public void mousePressed(MouseEvent event)
	{
		if (event.isPopupTrigger())
			KeyPopupMenu.this.show(event.getComponent(), event.getX(), event.getY());
	}
	
	@Override
	public void mouseReleased(MouseEvent event)
	{
		if (event.isPopupTrigger())
		{
			if (this.getParent() != null)
			{
				KeyPopupMenu.this.show(event.getComponent(), event.getX(), event.getY());
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent event)
	{
	}
	
	@Override
	public void mouseEntered(MouseEvent event)
	{
	}
	
	@Override
	public void mouseExited(MouseEvent event)
	{
	}
}
