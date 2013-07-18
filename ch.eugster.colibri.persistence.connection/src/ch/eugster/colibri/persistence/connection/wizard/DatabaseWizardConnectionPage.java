/*
 * Created on 13.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.jdom.Attribute;
import org.jdom.Element;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.model.Version;
import ch.eugster.colibri.persistence.service.ConnectionService;

public class DatabaseWizardConnectionPage extends WizardPage
{
	private Text connectionName;

	private ComboViewer drivers;

	private Button embedded;

	private Text protocol;
	
	private Text host;
	
	private Text instance;
	
	private Text port;
	
	private Text database;
	
	private TableViewer propertyViewer;

	private Button addPropertyButton;
	
	private Button removePropertyButton;
	
	private Text url;

	private Label exampleUrl;

	private Button activateUrl;
	
	private Text user;

	private Text password;

	private Label helpLabel;

	private Button migrate;

	private Version version;

	private Button checkConnection;
	
	private ComboViewer logViewer;
	
	private IStatus status;

	private Element newConnection;

	public DatabaseWizardConnectionPage(final String name)
	{
		super(name);
	}

	@Override
	public boolean canFlipToNextPage()
	{
		return this.status.isOK();
	}

	@Override
	public void createControl(final Composite parent)
	{
		final int cols = 3;
		this.setTitle("Aktive Datenbankverbindung");
		this.setMessage("Legen Sie die Angaben für die Datenbankverbindung fest.", IMessageProvider.INFORMATION);
		this.setDescription("Bestimmen Sie die Eigenschaften für die Datenbankverbindung.");

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(cols, false));

		Label label = new Label(composite, SWT.None);
		label.setText("Bezeichnung");
		label.setLayoutData(new GridData());

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols -1;
		
		this.connectionName = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.connectionName.setLayoutData(gridData);
		this.connectionName.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				final DatabaseWizardSelectConnectionPage selectWizardPage = (DatabaseWizardSelectConnectionPage) DatabaseWizardConnectionPage.this.getWizard().getPage(
						"select.connection.wizard.page");
				if (selectWizardPage != null)
				{
					if (selectWizardPage.exists(DatabaseWizardConnectionPage.this.connectionName.getText()))
					{
						DatabaseWizardConnectionPage.this.setMessage("Die Bezeichnung wird bereits verwendet.", IMessageProvider.WARNING);
						DatabaseWizardConnectionPage.this.setPageComplete(DatabaseWizardConnectionPage.this.validatePage(Status.CANCEL_STATUS));
					}
				}
				if (DatabaseWizardConnectionPage.this.embedded.getSelection())
				{
					final StructuredSelection ssel = (StructuredSelection) DatabaseWizardConnectionPage.this.drivers
							.getSelection();
					final SupportedDriver driver = (SupportedDriver) ssel.getFirstElement();
					final String url = driver.getBaseProtocol() + DatabaseWizardConnectionPage.this.connectionName.getText();
					DatabaseWizardConnectionPage.this.url.setText(url);
				}
			}
		});

		gridData = new GridData();
		gridData.horizontalSpan = cols;

		this.embedded = new Button(composite, SWT.CHECK);
		this.embedded.setText("Lokale Datenbank verwenden");
		this.embedded.setLayoutData(gridData);
		this.embedded.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				DatabaseWizardConnectionPage.this.drivers.refresh();
				SupportedDriver selectedDriver = null;
				if (DatabaseWizardConnectionPage.this.embedded.getSelection())
				{
					selectedDriver = SupportedDriver.DERBY_EMBEDDED;
					status = Status.OK_STATUS;
				}
				else
				{
					selectedDriver = SupportedDriver.MSSQLSERVER_2008;
					status = Status.CANCEL_STATUS;
				}
				DatabaseWizardConnectionPage.this.drivers.setSelection(new StructuredSelection(
						new SupportedDriver[] { selectedDriver }));
				DatabaseWizardConnectionPage.this.setPageComplete(DatabaseWizardConnectionPage.this.validatePage(status));
			}

		});

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols;

		label = new Label(composite, SWT.WRAP);
		label.setLayoutData(gridData);
		label.setText("Deaktivieren Sie die Checkbox, wenn Sie ein externes Datenbanksystem verwenden wollen.");

		gridData = new GridData();
		gridData.horizontalSpan = cols;

		label = new Label(composite, SWT.LEFT);
		label.setText("");
		label.setLayoutData(gridData);

		label = new Label(composite, SWT.LEFT);
		label.setText("Datenbanksystem");
		label.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols - 1;

		Combo combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData(gridData);

		this.drivers = new ComboViewer(combo);
		this.drivers.setContentProvider(new ArrayContentProvider());
		this.drivers.setLabelProvider(new DriverLabelProvider());
		this.drivers.setSorter(new ViewerSorter());
		this.drivers.setFilters(new ViewerFilter[] { new DriverViewerFilter(this.embedded) });
		this.drivers.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final StructuredSelection ssel = (StructuredSelection) event.getSelection();
				if (ssel.getFirstElement() instanceof SupportedDriver)
				{
					final SupportedDriver supportedDriver = (SupportedDriver) ssel.getFirstElement();
					DatabaseWizardConnectionPage.this.protocol.setText(supportedDriver.getBaseProtocol());
					DatabaseWizardConnectionPage.this.setDescription(supportedDriver.getDescription());
					DatabaseWizardConnectionPage.this.instance.setEnabled(supportedDriver.hasInstance());
					DatabaseWizardConnectionPage.this.helpLabel.setText(DatabaseWizardConnectionPage.this.getDescription());
					DatabaseWizardConnectionPage.this.exampleUrl.setText(supportedDriver.getExampleURL());
					DatabaseWizardConnectionPage.this.port.setText(supportedDriver.getDefaultPort());
					DatabaseWizardConnectionPage.this.getShell().layout();
				}
				else
				{
					DatabaseWizardConnectionPage.this.url.setText("");
					DatabaseWizardConnectionPage.this.setDescription("");
					DatabaseWizardConnectionPage.this.helpLabel.setText(DatabaseWizardConnectionPage.this.getDescription());
					DatabaseWizardConnectionPage.this.exampleUrl.setText("");
				}
				DatabaseWizardConnectionPage.this.setPageComplete(DatabaseWizardConnectionPage.this.validatePage(DatabaseWizardConnectionPage.this.embedded.getSelection() ? Status.OK_STATUS : Status.CANCEL_STATUS));
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Protokoll");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols - 1;
		
		this.protocol = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.protocol.setLayoutData(gridData);
		this.protocol.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				updateUrl();
			}
		});	

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Host");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols - 1;
		
		this.host = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.host.setLayoutData(gridData);
		this.host.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				updateUrl();
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Instanz");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols - 1;
		
		this.instance = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.instance.setLayoutData(gridData);
		this.instance.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				updateUrl();
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Port");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols - 1;
		gridData.widthHint = 48;
		
		this.port = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.port.setLayoutData(gridData);
		this.port.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				updateUrl();
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Datenbank");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols - 1;
		
		this.database = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.database.setLayoutData(gridData);
		this.database.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				updateUrl();
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Parameter");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 300;
		gridData.heightHint = 96;
		
		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayoutData(gridData);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		
		propertyViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		propertyViewer.setContentProvider(new PropertyContentProvider());
		propertyViewer.getTable().setHeaderVisible(true);
		propertyViewer.getTable().setLinesVisible(true);
		
		final MenuManager menuManager = new MenuManager("#ConnectionWizardPagePopupMenu"); //$NON-NLS-1$
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener()
		{
			@Override
			public void menuAboutToShow(final IMenuManager manager)
			{
				StructuredSelection ssel = (StructuredSelection) DatabaseWizardConnectionPage.this.propertyViewer
						.getSelection();
				int size = ssel.size();

				if (size > 0)
				{
					if (ssel.size() == 1)
					{
						DatabaseWizardConnectionPage.this.addPropertyAction(manager);
						manager.add(new Separator());
					}
					DatabaseWizardConnectionPage.this.removePropertyAction(manager);
				}
			}
		});

		Menu menu = menuManager.createContextMenu(this.propertyViewer.getTable());
		this.propertyViewer.getTable().setMenu(menu);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(this.propertyViewer, SWT.LEFT);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				Object element = cell.getElement();
				if (element instanceof Property)
				{
					Property property = (Property) element;
					cell.setText(property.getKey());
				}
			}
		});
		tableViewerColumn.setEditingSupport(new EditingSupport(this.propertyViewer)
		{

			@Override
			protected boolean canEdit(final Object element)
			{
				return true;
			}

			@Override
			protected CellEditor getCellEditor(final Object element)
			{
				return new TextCellEditor(DatabaseWizardConnectionPage.this.propertyViewer.getTable());
			}

			@Override
			protected Object getValue(final Object element)
			{
				Property property = (Property) element;
				return property.getKey();
			}

			@Override
			protected void setValue(final Object element, final Object value)
			{
				Property property = (Property) element;
				property.setKey((String) value);
				DatabaseWizardConnectionPage.this.propertyViewer.refresh(element);
			}

		});
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setText("Schlüssel");
		tableColumn.setResizable(true);
		tableColumnLayout.setColumnData(tableColumn, new ColumnWeightData(50, 100, true));
		
		tableViewerColumn = new TableViewerColumn(this.propertyViewer, SWT.LEFT);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				Object element = cell.getElement();
				if (element instanceof Property)
				{
					Property property = (Property) element;
					cell.setText(property.getValue());
				}
			}
		});
		tableViewerColumn.setEditingSupport(new EditingSupport(this.propertyViewer)
		{

			@Override
			protected boolean canEdit(final Object element)
			{
				return true;
			}

			@Override
			protected CellEditor getCellEditor(final Object element)
			{
				return new TextCellEditor(DatabaseWizardConnectionPage.this.propertyViewer.getTable());
			}

			@Override
			protected Object getValue(final Object element)
			{
				Property property = (Property) element;
				return property.getValue();
			}

			@Override
			protected void setValue(final Object element, final Object value)
			{
				Property property = (Property) element;
				property.setValue((String) value);
				DatabaseWizardConnectionPage.this.propertyViewer.refresh(element);
			}

		});
		propertyViewer.getColumnViewerEditor().addEditorActivationListener(new ColumnViewerEditorActivationListener() 
		{
			@Override
			public void beforeEditorActivated(ColumnViewerEditorActivationEvent event)
			{
			}

			@Override
			public void afterEditorActivated(ColumnViewerEditorActivationEvent event)
			{
			}

			@Override
			public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event)
			{
			}

			@Override
			public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event)
			{
				updateUrl();
			}
		});
		tableColumn = tableViewerColumn.getColumn();
		tableColumn.setText("Wert");
		tableColumn.setResizable(true);
		tableColumnLayout.setColumnData(tableColumn, new ColumnWeightData(50, 100, true));
		
		Composite selectorComposite = new Composite(composite, SWT.None);
		selectorComposite.setLayoutData(new GridData());
		selectorComposite.setLayout(new GridLayout());
		
		addPropertyButton = new Button(selectorComposite, SWT.PUSH);
		addPropertyButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addPropertyButton.setImage(Activator.getDefault().getImageRegistry().get("ADD"));
		addPropertyButton.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				@SuppressWarnings("unchecked")
				List<Property> properties = (List<Property>) propertyViewer.getInput();
				properties.add(new Property());
				propertyViewer.refresh();
				updateUrl();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		removePropertyButton = new Button(selectorComposite, SWT.PUSH);
		removePropertyButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removePropertyButton.setImage(Activator.getDefault().getImageRegistry().get("REMOVE"));
		removePropertyButton.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				StructuredSelection ssel = (StructuredSelection) propertyViewer.getSelection();
				Object[] objects = ssel.toArray();
				Object inputs = propertyViewer.getInput();
				if (inputs instanceof List)
				{
					@SuppressWarnings("unchecked")
					List<Object> properties = (List<Object>) inputs;
					for (Object object : objects)
					{
						properties.remove(object);
					}
					propertyViewer.refresh();
				}
				updateUrl();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		
		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("URL");
		
		this.url = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.url.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.activateUrl = new Button(composite, SWT.CHECK);
		this.activateUrl.setLayoutData(new GridData());
		this.activateUrl.setText("manuell");
		this.activateUrl.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				url.setEnabled(activateUrl.getSelection() && !embedded.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		
		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Benutzername");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols - 1;
		
		this.user = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.user.setLayoutData(gridData);
		this.user.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				DatabaseWizardConnectionPage.this.setPageComplete(DatabaseWizardConnectionPage.this.validatePage(DatabaseWizardConnectionPage.this.embedded.getSelection() ? Status.OK_STATUS : Status.CANCEL_STATUS));
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Passwort");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols - 1;
		
		this.password = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.password.setEchoChar('*');
		this.password.setLayoutData(gridData);
		this.password.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				DatabaseWizardConnectionPage.this.setPageComplete(DatabaseWizardConnectionPage.this.validatePage(DatabaseWizardConnectionPage.this.embedded.getSelection() ? Status.OK_STATUS : Status.CANCEL_STATUS));
			}
		});

		gridData = new GridData();
		gridData.horizontalSpan = cols;

		this.checkConnection = new Button(composite, SWT.PUSH);
		this.checkConnection.setText("Verbindung prüfen");
		this.checkConnection.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DatabaseWizardConnectionPage.this.setPageComplete(DatabaseWizardConnectionPage.this.validatePage(DatabaseWizardConnectionPage.this.checkConnection()));
				if (status.isOK())
				{
					MessageDialog.openConfirm(DatabaseWizardConnectionPage.this.getShell(), "Verbindung hergestellt.", "Die Verbindung wurde erfolgreich hergestellt.");
				}
				else
				{
					ErrorDialog.openError(DatabaseWizardConnectionPage.this.getShell(), "Verbindungsproblem", "Es konnte keine Verbindung zur Datenbank hergestellt werden.", status);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
				
			}
		});
		this.checkConnection.setLayoutData(gridData);

		label = new Label(composite, SWT.LEFT);
		label.setText("Protokollierung Datenbankzugriffe");
		label.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols - 1;
		
		combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData(gridData);

		String[] levels = new String[] { SessionLog.ALL_LABEL, SessionLog.FINEST_LABEL, SessionLog.FINER_LABEL, SessionLog.FINE_LABEL, SessionLog.CONFIG_LABEL, SessionLog.INFO_LABEL, SessionLog.WARNING_LABEL, SessionLog.SEVERE_LABEL };
		this.logViewer = new ComboViewer(combo);
		this.logViewer.setContentProvider(new ArrayContentProvider());
		this.logViewer.setLabelProvider(new LabelProvider());
		this.logViewer.setInput(levels);

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Beispiel-URL");
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = cols -1;
		
		this.exampleUrl = new Label(composite, SWT.None);
		this.exampleUrl.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = cols;

		this.helpLabel = new Label(composite, SWT.WRAP);
		this.helpLabel.setLayoutData(gridData);

		gridData = new GridData();
		gridData.horizontalSpan = cols;

		this.migrate = new Button(composite, SWT.CHECK);
		this.migrate.setLayoutData(gridData);
		this.migrate.setText("Daten aus Vorgängerversion migrieren");

		this.loadValues();

		this.setControl(composite);
	}
	
	private void updateUrl()
	{
		if (this.drivers.getSelection() instanceof StructuredSelection)
		{
			StructuredSelection ssel = (StructuredSelection) this.drivers.getSelection();
			if (ssel.getFirstElement() instanceof SupportedDriver)
			{
				Object object = propertyViewer.getInput();
				SupportedDriver driver = (SupportedDriver) ssel.getFirstElement();
				this.url.setText(driver.getUrl(protocol.getText(), host.getText(), driver.getInstanceDelimiter(), instance.getText(), port.getText(), database.getText(), object));
			}
		}
		
	}

	public boolean migrate()
	{
		return this.migrate == null ? false : this.migrate.getSelection();
	}

	public Element updateElement()
	{
		final boolean embedded = this.embedded.getSelection();
		final String text = this.connectionName.getText();
		String driver = null;
		String url = null;
		if (embedded)
		{
			driver = EmbeddedDriver.class.getName();
			url = "jdbc:derby:" + text;
		}
		else
		{
			final StructuredSelection ssel = (StructuredSelection) this.drivers.getSelection();
			final SupportedDriver selectedDriver = (SupportedDriver) ssel.getFirstElement();
			driver = selectedDriver.getDriver();
			url = this.url.getText();
		}
		final String username = this.user.getText();
		String password = this.password.getText();
		password = password.isEmpty() ? "" : Activator.getDefault().encrypt(password);

		this.newConnection = new Element("connection");
		this.newConnection.setText(text);
		this.newConnection.setAttribute(new Attribute(ConnectionService.KEY_USE_EMBEDDED_DATABASE, Boolean
				.toString(embedded)));
		this.newConnection.setAttribute(new Attribute(PersistenceUnitProperties.JDBC_DRIVER, driver));
		this.newConnection.setAttribute(new Attribute(PersistenceUnitProperties.JDBC_URL, url));
		this.newConnection.setAttribute(new Attribute(PersistenceUnitProperties.JDBC_USER, username));
		this.newConnection.setAttribute(new Attribute(PersistenceUnitProperties.JDBC_PASSWORD, password));

		return this.newConnection;
	}

	public boolean useEmbeddedDatabase()
	{
		return this.embedded.getSelection();
	}

	private IStatus checkConnection()
	{
		IStatus status = Status.OK_STATUS;
		final StructuredSelection ssel = (StructuredSelection) DatabaseWizardConnectionPage.this.drivers.getSelection();
		final SupportedDriver selectedDriver = (SupportedDriver) ssel.getFirstElement();
		final String driverName = selectedDriver.getDriver();
		final String url = DatabaseWizardConnectionPage.this.url.getText();
		final String username = DatabaseWizardConnectionPage.this.user.getText();
		final String password = DatabaseWizardConnectionPage.this.password.getText();

		try
		{
			Class.forName(driverName);
			Connection connection = null;
			if (user.getText().isEmpty() && DatabaseWizardConnectionPage.this.password.getText().isEmpty())
			{
				connection = DriverManager.getConnection(url);
			}
			else
			{
				connection = DriverManager.getConnection(url, username, password);
			}
			if (connection != null)
			{
				this.version = DatabaseWizardConnectionPage.this.getVersion(connection);
				connection.close();
			}
		}
		catch (final ClassNotFoundException e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unerwarteter Fehler", e);
		}
		catch (final SQLException e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Mit den eingegebenen Daten kann keine Datenbankverbindung hergestellt werden.", e);
		}
		if (status.getSeverity() == IStatus.OK)
		{
			if (this.version != null && this.version.getStructure() > Version.STRUCTURE)
			{
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Die Datenversion der gewählten Datenbank ist aktueller als die Programmversion. Bitte aktualisieren Sie das Programm auf die aktuelle Version " + Version.STRUCTURE);
			}
		}
		return status;
	}

	private boolean validatePage(IStatus status)
	{
		this.status = status;
		this.setEnabled(!this.embedded.getSelection());
		return this.status.isOK();
	}

	private Element createDefaultValues()
	{
		final boolean noSelectPage = this.getWizard().getPage("select.connection.wizard.page") == null;

		final Element connection = new Element("connection");
		connection.setText(noSelectPage ? "colibri" : "");
		connection.setAttribute(new Attribute(ConnectionService.KEY_USE_EMBEDDED_DATABASE, "true"));
		connection.setAttribute(new Attribute(PersistenceUnitProperties.JDBC_DRIVER, SupportedDriver.DERBY_EMBEDDED
				.getDriver()));
		connection.setAttribute(new Attribute(PersistenceUnitProperties.JDBC_URL, SupportedDriver.DERBY_EMBEDDED
				.getBaseProtocol()));
		connection.setAttribute(new Attribute(PersistenceUnitProperties.JDBC_USER, "colibri"));
		connection.setAttribute(new Attribute(PersistenceUnitProperties.JDBC_PASSWORD, Activator.getDefault().encrypt(
				"colibri")));
		return connection;
	}
	
	public String getLogLevel()
	{
		return (String) ((StructuredSelection) this.logViewer.getSelection()).getFirstElement();
	}

	private Version getVersion(final Connection connection)
	{
		Version version = null;
		Statement stm = null;
		DatabaseMetaData dmd;
		ResultSet rst = null;

		try
		{
			stm = connection.createStatement();
			dmd = connection.getMetaData();
			rst = dmd.getTables(null, null, "colibri_version", new String[] { "TABLE" });
			if (rst.next())
			{
				rst = stm.executeQuery("SELECT * FROM colibri_version");
				if (rst.next())
				{
					version = Version.newInstance();
					version.setId(Long.valueOf(rst.getLong("v_id")));
					version.setDeleted((rst.getInt("v_deleted") == 0 ? false : true));
					version.setUpdate(rst.getInt("v_update"));
					version.setStructure(rst.getInt("v_structure"));
					version.setData(rst.getInt("v_data"));
				}
			}
		}
		catch (final Exception e)
		{

		}
		finally
		{
			try
			{
				if (rst != null)
				{
					rst.close();
				}
				if (stm != null)
				{
					stm.close();
				}
			}
			catch (final SQLException e)
			{
				e.printStackTrace();
			}
		}
		return version;
	}

	private void loadElement(final Element element)
	{
		this.connectionName.setText(element.getText());
		final Boolean embedded = Boolean
				.valueOf(element.getAttributeValue(ConnectionService.KEY_USE_EMBEDDED_DATABASE));
		DatabaseWizardConnectionPage.this.embedded.setSelection(embedded == null ? true : embedded);
		this.drivers.setInput(SupportedDriver.values());
		final String driverName = element.getAttributeValue(PersistenceUnitProperties.JDBC_DRIVER);
		final SupportedDriver driver = SupportedDriver.findDriver(driverName);
		this.drivers.setSelection(new StructuredSelection(new SupportedDriver[] { driver }));
		final String url = element.getAttributeValue(PersistenceUnitProperties.JDBC_URL);
		setProperties(url);
		this.setDescription(driver.getDescription());
		final String username = element.getAttributeValue(PersistenceUnitProperties.JDBC_USER);
		this.user.setText(username == null ? "colibri" : username);
		String password = element.getAttributeValue(PersistenceUnitProperties.JDBC_PASSWORD);
		password = password == null ? "" : password.isEmpty() ? "" : Activator.getDefault().decrypt(password);
		this.password.setText(password);
		this.helpLabel.setText(DatabaseWizardConnectionPage.this.getDescription());
		StructuredSelection ssel = new StructuredSelection(SessionLog.INFO_LABEL);
		this.logViewer.setSelection(ssel);
		this.getShell().layout();
	}
	
	private void setProperties(String url)
	{
		String[] as = url.split("[;]");
		if (as.length > 0)
		{
			String[] bs = as[0].split("[:]");
			if (bs.length > 1)
			{
				protocol.setText(bs[0] + ":" + bs[1] + ":");
			}
			if (bs.length > 2)
			{
				if (bs[3].startsWith("//"))
				{
					String host = bs[3].substring(2);
					if (host.indexOf("/", 3) == 0)
					{
						this.host.setText(host);
					}
					else
					{
						String[] database = host.split("[/]");
						this.host.setText(database[0]);
						this.database.setText(database[1]);
					}
				}
				else
				{
					this.database.setText(bs[3]);
				}
			}
		}
		List<Property> properties = new ArrayList<Property>();
		if (as.length > 1)
		{
			for (int i = 1; i < as.length; i++)
			{
				Property property = new Property();
				if (as[i].contains("="))
				{
					String[] keyValue = as[i].split("[=]");
					property.setKey(keyValue[0]);
					property.setValue(keyValue[1]);
				}
				else
				{
					property.setKey(as[i]);
				}
				properties.add(property);
			}
		}
		this.propertyViewer.setInput(properties);
	}

	private void loadValues()
	{
		this.newConnection = this.createDefaultValues();
		this.loadElement(this.newConnection);
		this.setEnabled(!this.embedded.getSelection());
		this.setPageComplete(this.embedded.getSelection());
	}

	private void setEnabled(final boolean enabled)
	{
		this.checkConnection.setEnabled(enabled);
		this.drivers.getCombo().setEnabled(enabled);
		this.protocol.setEnabled(enabled);
		this.host.setEnabled(enabled);
		this.port.setEnabled(enabled);
		this.database.setEnabled(enabled);
		this.propertyViewer.getTable().setEnabled(enabled);
		this.addPropertyButton.setEnabled(enabled);
		this.removePropertyButton.setEnabled(enabled);
		this.url.setEnabled(enabled && activateUrl.getSelection());
		this.user.setEnabled(enabled);
		this.password.setEnabled(enabled);
	}
	
	private void addPropertyAction(final IMenuManager manager)
	{
		Action action = new Action()
		{
			@Override
			public void run()
			{
				propertyViewer.add(new Property());
			}
		};
		action.setText("Hinzufügen");
		action.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("ADD"));
		action.setEnabled(true);
		manager.add(action);
	}

	private void removePropertyAction(final IMenuManager manager)
	{
		Action action = new Action()
		{
			@Override
			public void run()
			{
				StructuredSelection ssel = (StructuredSelection) propertyViewer.getSelection();
				Object[] objects = ssel.toArray();
				for (Object object : objects)
				{
					propertyViewer.remove(object);
				}
			}
		};
		action.setText("Entfernen");
		action.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("REMOVE"));
		action.setEnabled(true);
		manager.add(action);
	}

	public class Property
	{
		private String key = "";
		
		private String value = "";

		private PropertyChangeSupport changes = new PropertyChangeSupport(this);
		
		public Property()
		{
		}
		
		public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
		{
			this.changes.addPropertyChangeListener(propertyName, listener);
		}

		public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
		{
			this.changes.removePropertyChangeListener(propertyName, listener);
		}

		public Property(String key, String value)
		{
			setKey(key);
			setValue(value);
		}
		
		public String getKey()
		{
			return this.key;
		}
		
		public String getValue()
		{
			return this.value;
		}
		
		public void setKey(String key)
		{
			changes.firePropertyChange("key", this.key, this.key = key);
		}
		
		public void setValue(String value)
		{
			changes.firePropertyChange("value", this.value, this.value = value);
		}
	}

	private class PropertyContentProvider implements IStructuredContentProvider
	{

		@Override
		public void dispose()
		{
		}

		@Override
		public Object[] getElements(final Object inputElement)
		{
			if (inputElement instanceof List)
			{
				@SuppressWarnings("unchecked")
				List<Property> properties = (List<Property>) inputElement;
				return properties.toArray(new Property[0]);
			}
			return new Property[0];
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
		{
		}

	}

}
