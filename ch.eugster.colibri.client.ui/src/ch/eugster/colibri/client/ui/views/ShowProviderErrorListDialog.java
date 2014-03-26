package ch.eugster.colibri.client.ui.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ShowProviderErrorListDialog extends MessageDialog 
{
	private TableViewer viewer;

	private PersistenceService persistenceService;
	
	public ShowProviderErrorListDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex, PersistenceService persistenceService, Salespoint salespoint) 
	{
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
		this.persistenceService = persistenceService; 
	}

	@Override
	protected Control createCustomArea(Composite parent)
	{
		parent.setLayout(new GridLayout());
		
		final Table table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.viewer = new TableViewer(table);
		this.viewer.setContentProvider(new ArrayContentProvider());

		TableViewerColumn viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Code");
		return table;
	}

}
