/*
 * Created on 22.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.composites;

import javax.swing.SwingConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class AlignComposite extends Composite
{
	private Button[] horizontals = new Button[3];
	
	private Button[] verticals = new Button[3];
	
	public AlignComposite(FormToolkit toolkit, Composite parent, int style)
	{
		this(toolkit, parent, style, false, false);
	}
	
	public AlignComposite(FormToolkit toolkit, Composite parent, int style, boolean bold,
					boolean italic)
	{
		super(parent, style);
		this.init(toolkit);
	}
	
	private void init(FormToolkit toolkit)
	{
		this.setLayout(new GridLayout());
		
		Group group = new Group(this, SWT.NONE);
		group.setText("Ausrichtung");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());
		
		Composite horizontal = new Composite(group, SWT.NONE);
		horizontal.setLayout(new GridLayout(3, true));
		horizontal.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.horizontals = new Button[3];
		
		this.horizontals[0] = toolkit.createButton(horizontal, "Links", SWT.RADIO);
		this.horizontals[0].setData(new Integer(SwingConstants.LEFT));
		this.horizontals[0].setLayoutData(new GridData());
		
		this.horizontals[1] = toolkit.createButton(horizontal, "Mitte", SWT.RADIO);
		this.horizontals[1].setData(new Integer(SwingConstants.CENTER));
		this.horizontals[1].setLayoutData(new GridData());
		
		this.horizontals[2] = toolkit.createButton(horizontal, "Rechts", SWT.RADIO);
		this.horizontals[2].setData(new Integer(SwingConstants.RIGHT));
		this.horizontals[2].setLayoutData(new GridData());
		
		Composite vertical = new Composite(group, SWT.NONE);
		vertical.setLayout(new GridLayout(3, true));
		vertical.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.verticals = new Button[3];
		
		this.verticals[0] = toolkit.createButton(vertical, "Oben", SWT.RADIO);
		this.verticals[0].setData(new Integer(SwingConstants.TOP));
		this.verticals[0].setLayoutData(new GridData());
		
		this.verticals[1] = toolkit.createButton(vertical, "Mitte", SWT.RADIO);
		this.verticals[1].setData(new Integer(SwingConstants.CENTER));
		this.verticals[1].setLayoutData(new GridData());
		
		this.verticals[2] = toolkit.createButton(vertical, "Unten", SWT.RADIO);
		this.verticals[2].setData(new Integer(SwingConstants.BOTTOM));
		this.verticals[2].setLayoutData(new GridData());
		
	}
	
	public void setHorizontalSelection(int value)
	{
		switch (value)
		{
			case SwingConstants.LEFT:
			{
				this.horizontals[0].setSelection(true);
				break;
			}
			case SwingConstants.CENTER:
			{
				this.horizontals[1].setSelection(true);
				break;
			}
			case SwingConstants.RIGHT:
			{
				this.horizontals[2].setSelection(true);
				break;
			}
		}
	}
	
	public void setVerticalSelection(int value)
	{
		switch (value)
		{
			case SwingConstants.TOP:
			{
				this.verticals[0].setSelection(true);
				break;
			}
			case SwingConstants.CENTER:
			{
				this.verticals[1].setSelection(true);
				break;
			}
			case SwingConstants.BOTTOM:
			{
				this.verticals[2].setSelection(true);
				break;
			}
		}
	}
	
	public void addHorizontalListener(Listener listener)
	{
		if (listener != null)
		{
			this.horizontals[0].addListener(SWT.Selection, listener);
			this.horizontals[1].addListener(SWT.Selection, listener);
			this.horizontals[2].addListener(SWT.Selection, listener);
		}
	}
	
	public void removeHorizontalListener(Listener listener)
	{
		if (listener != null)
		{
			this.horizontals[0].removeListener(SWT.Selection, listener);
			this.horizontals[1].removeListener(SWT.Selection, listener);
			this.horizontals[2].removeListener(SWT.Selection, listener);
		}
	}
	
	public void addVerticalListener(Listener listener)
	{
		if (listener != null)
		{
			this.verticals[0].addListener(SWT.Selection, listener);
			this.verticals[1].addListener(SWT.Selection, listener);
			this.verticals[2].addListener(SWT.Selection, listener);
		}
	}
	
	public void removeVerticalListener(Listener listener)
	{
		if (listener != null)
		{
			this.verticals[0].removeListener(SWT.Selection, listener);
			this.verticals[1].removeListener(SWT.Selection, listener);
			this.verticals[2].removeListener(SWT.Selection, listener);
		}
	}
}
