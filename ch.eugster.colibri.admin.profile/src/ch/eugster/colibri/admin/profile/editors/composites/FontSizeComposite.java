/*
 * Created on 22.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;

public class FontSizeComposite extends Composite
{
	private Label sizeLabel;
	
	private Scale sizeScale;
	
	int min = 8;
	
	int max = 72;
	
	int increment = 1;
	
	int pageIncrement = 8;
	
	public FontSizeComposite(Composite parent, int style)
	{
		super(parent, style);
		this.init();
	}
	
	public FontSizeComposite(Composite parent, int style, int min, int max, int increment,
					int pageIncrement)
	{
		super(parent, style);
		this.min = min;
		this.max = max;
		this.increment = increment;
		this.pageIncrement = pageIncrement;
		this.init();
	}
	
	private void init()
	{
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.setLayout(layout);
		
		Group group = new Group(this, SWT.NONE);
		group.setText("Schriftgrösse");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));
		
		this.sizeScale = new Scale(group, SWT.HORIZONTAL);
		this.sizeScale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.sizeScale.setMinimum(this.min);
		this.sizeScale.setMaximum(this.max);
		this.sizeScale.setIncrement(this.increment);
		this.sizeScale.setPageIncrement(this.pageIncrement);
		
		GridData gridData = new GridData();
		gridData.widthHint = 20;
		
		this.sizeLabel = new Label(group, SWT.NONE);
		this.sizeLabel.setLayoutData(gridData);
		this.sizeScale.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Scale scale = (Scale) e.widget;
				FontSizeComposite.this.sizeLabel.setText(new Integer(scale.getSelection())
								.toString());
			}
		});
	}
	
	public void setSize(float size)
	{
		this.sizeLabel.setText(new Float(size).toString());
		this.sizeScale.setSelection(Math.round(size));
	}
	
	public void addSelectionListener(SelectionListener listener)
	{
		if (listener != null) this.sizeScale.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener)
	{
		if (listener != null) this.sizeScale.removeSelectionListener(listener);
	}
}
