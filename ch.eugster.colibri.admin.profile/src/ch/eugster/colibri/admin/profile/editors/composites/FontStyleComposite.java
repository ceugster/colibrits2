/*
 * Created on 22.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class FontStyleComposite extends Composite
{
	private Button bold;
	
	private Button italic;
	
	public FontStyleComposite(FormToolkit toolkit, Composite parent, int style)
	{
		this(toolkit, parent, style, false, false);
	}
	
	public FontStyleComposite(FormToolkit toolkit, Composite parent, int style, boolean bold,
					boolean italic)
	{
		super(parent, style);
		this.init(toolkit);
	}
	
	private void init(FormToolkit toolkit)
	{
		this.setLayout(new GridLayout());
		
		Group group = new Group(this, SWT.NONE);
		group.setText("Schriftstil");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());
		
		this.bold = toolkit.createButton(group, "Fett", SWT.CHECK);
		this.bold.setLayoutData(new GridData());
		
		this.italic = toolkit.createButton(group, "Kursiv", SWT.CHECK);
		this.italic.setLayoutData(new GridData());
	}
	
	public void setStyle(int value)
	{
		boolean selection = (value & java.awt.Font.BOLD) == java.awt.Font.BOLD;
		this.bold.setSelection(selection);
		selection = (value & java.awt.Font.ITALIC) == java.awt.Font.ITALIC;
		this.italic.setSelection(selection);
	}
	
	public void setBold(boolean bold)
	{
		this.bold.setSelection(bold);
	}
	
	public void setItalic(boolean italic)
	{
		this.italic.setSelection(italic);
	}
	
	public void addBoldListener(Listener listener)
	{
		if (listener != null) this.bold.addListener(SWT.Selection, listener);
	}
	
	public void removeBoldListener(Listener listener)
	{
		if (listener != null) this.bold.removeListener(SWT.Selection, listener);
	}
	
	public void addItalicListener(Listener listener)
	{
		if (listener != null) this.italic.addListener(SWT.Selection, listener);
	}
	
	public void removeItalicListener(Listener listener)
	{
		if (listener != null) this.italic.removeListener(SWT.Selection, listener);
	}
}
