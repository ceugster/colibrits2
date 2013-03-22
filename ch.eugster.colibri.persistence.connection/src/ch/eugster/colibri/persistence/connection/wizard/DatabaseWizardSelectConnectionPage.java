package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jdom.Document;
import org.jdom.Element;

public class DatabaseWizardSelectConnectionPage extends WizardPage implements IWizardPage
{
	private TableViewer viewer;

	private Button newConnection;

	private Button deleteConnection;

	private Font bold;

	private Font normal;

	private Element selectedConnection;

	private Element currentConnection;

	public DatabaseWizardSelectConnectionPage(final String name)
	{
		super(name);
	}

	public boolean addNewConnection()
	{
		return this.viewer.getSelection().isEmpty();
	}

	@Override
	public boolean canFlipToNextPage()
	{
		final StructuredSelection ssel = (StructuredSelection) this.viewer.getSelection();
		if (ssel.isEmpty())
		{
			return true;
		}
		return false;
	}

	@Override
	public void createControl(final Composite parent)
	{
		this.setTitle("Auswahl Datenbankverbindungen");
		this.setMessage("Wählen oder erfassen Sie eine Datenbankverbindung.", IMessageProvider.INFORMATION);
		this.setDescription("Auswahl der Datenbankverbindungen.");

		this.normal = this.getFont();
		this.bold = new Font(this.normal.getDevice(), this.getDialogFontName(), this.normal.getFontData()[0].getHeight(), SWT.BOLD);

		final Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		final Table table = new Table(composite, SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		this.viewer = new TableViewer(table);
		this.viewer.setContentProvider(new ConnectionContentProvider());
		this.viewer.setSorter(new ConnectionSorter());
		this.viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(final DoubleClickEvent event)
			{
				final StructuredSelection ssel = (StructuredSelection) event.getSelection();
				if (!ssel.isEmpty())
				{
					DatabaseWizardSelectConnectionPage.this.selectedConnection = (Element) ssel.getFirstElement();
					DatabaseWizardSelectConnectionPage.this.setPageComplete(DatabaseWizardSelectConnectionPage.this.validatePage());
				}
			}
		});

		TableViewerColumn viewerColumn = new TableViewerColumn(this.viewer, SWT.LEFT);
		viewerColumn.getColumn().setText("Bezeichnung");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Element element = (Element) cell.getElement();
				cell.setText(element.getText());
				cell.setFont(element == DatabaseWizardSelectConnectionPage.this.selectedConnection ? DatabaseWizardSelectConnectionPage.this.bold
						: DatabaseWizardSelectConnectionPage.this.normal);
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.LEFT);
		viewerColumn.getColumn().setText("Datenbank");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Element element = (Element) cell.getElement();
				final String driverName = element.getAttributeValue(PersistenceUnitProperties.JDBC_DRIVER);
				final SupportedDriver driver = SupportedDriver.findDriver(driverName);
				cell.setText(driver.getPlatform());
				cell.setFont(element == DatabaseWizardSelectConnectionPage.this.selectedConnection ? DatabaseWizardSelectConnectionPage.this.bold
						: DatabaseWizardSelectConnectionPage.this.normal);
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.LEFT);
		viewerColumn.getColumn().setText("URL");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Element element = (Element) cell.getElement();
				cell.setText(element.getAttributeValue(PersistenceUnitProperties.JDBC_URL));
				cell.setFont(element == DatabaseWizardSelectConnectionPage.this.selectedConnection ? DatabaseWizardSelectConnectionPage.this.bold
						: DatabaseWizardSelectConnectionPage.this.normal);
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.LEFT);
		viewerColumn.getColumn().setText("Aktiv");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Element element = (Element) cell.getElement();
				final boolean active = element.getParentElement().getName().equals("current");
				cell.setText(active ? "Ja" : "");
				cell.setFont(element == DatabaseWizardSelectConnectionPage.this.selectedConnection ? DatabaseWizardSelectConnectionPage.this.bold
						: DatabaseWizardSelectConnectionPage.this.normal);
			}
		});

		if (this.getWizard() instanceof DatabaseWizard)
		{
			final Document document = ((DatabaseWizard) this.getWizard()).getDocument();
			this.viewer.setInput(document);
			for (final TableColumn column : this.viewer.getTable().getColumns())
			{
				column.pack();
			}
			this.currentConnection = document.getRootElement().getChild("current").getChild("connection");
			this.viewer.setSelection(new StructuredSelection(new Element[] { this.currentConnection }));
		}

		final Composite buttonComposite = new Composite(composite, SWT.None);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		buttonComposite.setLayout(new GridLayout());

		this.newConnection = new Button(buttonComposite, SWT.PUSH);
		this.newConnection.setText("Neue Verbindung");
		this.newConnection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.newConnection.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				DatabaseWizardSelectConnectionPage.this.viewer.setSelection(new StructuredSelection());
				DatabaseWizardSelectConnectionPage.this.setPageComplete(DatabaseWizardSelectConnectionPage.this.validatePage());
			}
		});

		this.deleteConnection = new Button(buttonComposite, SWT.PUSH);
		this.deleteConnection.setText("Verbindung entfernen");
		this.deleteConnection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.deleteConnection.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
			}
		});

		this.setControl(composite);
	}

	@Override
	public void dispose()
	{
		if (this.bold != null)
		{
			if (!this.bold.isDisposed())
			{
				this.bold.dispose();
			}
		}
	}

	public boolean exists(final String name)
	{
		final TableItem[] items = this.viewer.getTable().getItems();
		for (final TableItem item : items)
		{
			if (item.getText().equals(name))
			{
				return true;
			}
		}
		return false;
	}

	public Element getSelectedConnection()
	{
		final StructuredSelection ssel = (StructuredSelection) this.viewer.getSelection();
		return (Element) ssel.getFirstElement();
	}

	private boolean validatePage()
	{
		return true;
	}
}
