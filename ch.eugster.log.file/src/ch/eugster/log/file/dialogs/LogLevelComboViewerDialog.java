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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.osgi.service.log.LogService;

import ch.eugster.log.file.Activator;

public class LogLevelComboViewerDialog extends IconAndMessageDialog
{
	private ComboViewer fileViewer;

	private Spinner deleteLogsOlderThanDays;
	
	private ComboViewer consoleViewer;

	private String fileLevel;

	private String consoleLevel;

	private String days;
	
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
	
	public String getDays()
	{
		return days;
	}

	public String getFileSelection()
	{
		return this.fileLevel;
	}

	public String getConsoleSelection()
	{
		return this.consoleLevel;
	}

	@Override
	protected Control createDialogArea(final Composite parent)
	{
		this.getShell().setText("Protokoll");
		
		parent.setLayout(new GridLayout());

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Level");
		label.setLayoutData(new GridData());

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		
		Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(gridData);

		this.fileViewer = new ComboViewer(combo);
		this.fileViewer.setContentProvider(new ArrayContentProvider());
		this.fileViewer.setLabelProvider(new LabelProvider());
		this.fileViewer.setInput(getAvailableLogLevels());
		this.fileViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{
				IStructuredSelection ssel = (IStructuredSelection) event.getSelection();
				if (!ssel.isEmpty())
				{
					fileLevel = (String) ssel.getFirstElement();
				}
			}
		});
		this.fileViewer.setSelection(new StructuredSelection(new String[] { Activator.getDefault().getLevelAsString(Activator.getDefault().getCurrentFileLogLevel()) }));

		label = new Label(composite, SWT.NONE);
		label.setText("Logdateien löschen, wenn älter als");
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 64;
		
		this.deleteLogsOlderThanDays = new Spinner(composite, SWT.BORDER);
		this.deleteLogsOlderThanDays.setLayoutData(gridData);
		this.deleteLogsOlderThanDays.setDigits(0);
		this.deleteLogsOlderThanDays.setIncrement(1);
		this.deleteLogsOlderThanDays.setPageIncrement(10);
		this.deleteLogsOlderThanDays.setMaximum(Integer.MAX_VALUE);
		this.deleteLogsOlderThanDays.setMinimum(0);
		this.deleteLogsOlderThanDays.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				days = deleteLogsOlderThanDays.getText();
			}
		});
		this.deleteLogsOlderThanDays.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				days = deleteLogsOlderThanDays.getText();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		this.deleteLogsOlderThanDays.setSelection(Activator.getDefault().getDeleteLogsAfterDays());
		
		label = new Label(composite, SWT.NONE);
		label.setText("Tage");
		label.setLayoutData(new GridData());

		label = new Label(composite, SWT.NONE);
		label.setText("Level Konsole");
		label.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		
		combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(gridData);

		this.consoleViewer = new ComboViewer(combo);
		this.consoleViewer.setContentProvider(new ArrayContentProvider());
		this.consoleViewer.setLabelProvider(new LabelProvider());
		this.consoleViewer.setInput(getAvailableLogLevels());
		this.consoleViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{
				IStructuredSelection ssel = (IStructuredSelection) event.getSelection();
				if (!ssel.isEmpty())
				{
					consoleLevel = (String) ssel.getFirstElement();
				}
			}
		});
		this.consoleViewer.setSelection(new StructuredSelection(new String[] { Activator.getDefault().getLevelAsString(Activator.getDefault().getCurrentConsoleLogLevel()) }));

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
