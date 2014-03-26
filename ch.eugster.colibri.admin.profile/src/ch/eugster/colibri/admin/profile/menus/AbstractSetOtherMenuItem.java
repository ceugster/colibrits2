/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.Component;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public abstract class AbstractSetOtherMenuItem extends MenuItem implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1792917208840862292L;

	protected TabEditor editor;

	protected TabEditorButton button;

	public AbstractSetOtherMenuItem(final TabEditor editor, final TabEditorButton button, final String label, final String actionCommand)
	{
		super();
		this.setLabel(label);
		this.setActionCommand(actionCommand);
		this.editor = editor;
		this.button = button;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (this.editor.getFailOverState())
		{
			updateFailOverButtons();
		}
		else
		{
			updateNormalButtons();
		}
	}

	private void updateNormalButtons()
	{
		Frame frame = this.editor.getFrame();
		Component[] components = frame.getComponents();
		for (Component component : components)
		{
			updateNormalButtons(component);
		}
	}

	private void updateFailOverButtons()
	{
		Frame frame = this.editor.getFrame();
		Component[] components = frame.getComponents();
		for (Component component : components)
		{
			updateFailOverButtons(component);
		}
	}

	private void updateNormalButtons(Component component)
	{
		if (component instanceof JPanel)
		{
			JPanel panel = (JPanel) component;
			Component[] children = panel.getComponents();
			for (Component child : children)
			{
				updateNormalButtons(child);
			}
		}
		else if (component instanceof TabEditorButton)
		{
			TabEditorButton button = (TabEditorButton) component;
			if (button.getKeys() != null)
			{
				updateNormalButton(button);
				button.update(this.editor.getFailOverState());
			}
		}
	}

	private void updateFailOverButtons(Component component)
	{
		if (component instanceof JPanel)
		{
			JPanel panel = (JPanel) component;
			Component[] children = panel.getComponents();
			for (Component child : children)
			{
				updateFailOverButtons(child);
			}
		}
		else if (component instanceof TabEditorButton)
		{
			TabEditorButton button = (TabEditorButton) component;
			if (button.getKeys() != null)
			{
				updateFailOverButton(button);
				button.update(this.editor.getFailOverState());
			}
		}
	}
	
	protected abstract void updateNormalButton(TabEditorButton button);

	protected abstract void updateFailOverButton(TabEditorButton button);
}
