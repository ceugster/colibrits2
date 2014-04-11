/*
 * Created on 30.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.log.file.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.log.LogService;

import ch.eugster.log.file.Activator;

public class LogLevelComboViewerDialog extends IconAndMessageDialog
{
	private ComboViewer viewer;

	private String selection;

	public LogLevelComboViewerDialog(final Shell shell)
	{
		super(shell);
	}

	@Override
	public Control createButtonBar(final Composite parent)
	{
		final Composite composite = (Composite) super.createButtonBar(parent);
		this.getButton(Window.OK).setText("OK");
		this.getButton(Window.CANCEL).setText("Abbrechen");
		return composite;
	}

	@Override
	public Image getImage()
	{
		return null;
	}

	public String getSelection()
	{
		return this.selection;
	}

	@Override
	protected Control createDialogArea(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label label = new Label(composite, SWT.NONE);
		label.setText("Protokollierungsstufe");
		label.setLayoutData(new GridData());

		final Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.viewer = new ComboViewer(combo);
		this.viewer.setContentProvider(new ArrayContentProvider());
		this.viewer.setLabelProvider(new LabelProvider());
		this.viewer.setInput(getAvailableLogLevels());
		this.viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{
				IStructuredSelection ssel = (IStructuredSelection) event.getSelection();
				if (!ssel.isEmpty())
				{
					selection = (String) ssel.getFirstElement();
				}
			}
		});
		this.viewer.setSelection(new StructuredSelection(new String[] { Activator.getDefault().getLevelAsString(Activator.getDefault().getCurrentLogLevel()) }));

		return composite;
	}

	private String[] getAvailableLogLevels()
	{
		List<String> levels = new ArrayList<String>();
		levels.add(Activator.getDefault().getLevelAsString(LogService.LOG_ERROR));
		levels.add(Activator.getDefault().getLevelAsString(LogService.LOG_WARNING));
		levels.add(Activator.getDefault().getLevelAsString(LogService.LOG_INFO));
		levels.add(Activator.getDefault().getLevelAsString(LogService.LOG_DEBUG));
		return levels.toArray(new String[0]);
	}
}
