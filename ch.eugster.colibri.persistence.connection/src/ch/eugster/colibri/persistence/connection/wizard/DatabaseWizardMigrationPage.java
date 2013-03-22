/*
 * Created on 13.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.connection.config.DatabaseMigrator;
import ch.eugster.colibri.persistence.model.CommonSettings.HostnameResolver;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Version;

public class DatabaseWizardMigrationPage extends WizardPage
{
	private Text colibriPropertiesPath;

	private Button selectColibriPropertiesPath;

	private CheckboxTableViewer salespointViewer;

	private Document document;

	private Version version;

	private final Collection<Listener> listeners = new ArrayList<Listener>();

	private static final String MESSAGE = "Übernahme der Daten aus einer Vorgängerversion.";

	private static final String STANDARD_COLIBRI_XML_PATH = "C:/Programme/ColibriTS/properties/colibri.xml";

	public DatabaseWizardMigrationPage(final String name)
	{
		super(name);
	}

	public void addListener(final Listener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public boolean canFlipToNextPage()
	{
		return false;
	}

	@Override
	public void createControl(final Composite parent)
	{
		this.setTitle("Daten übernehmen");
		this.setMessage(DatabaseWizardMigrationPage.MESSAGE);
		this.setDescription("Übernehmen der Daten aus der ColibriTS Vorgängerversion.");

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(3, false));

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Pfad zur Datei colibri.xml");

		this.colibriPropertiesPath = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.colibriPropertiesPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final File file = new File(DatabaseWizardMigrationPage.STANDARD_COLIBRI_XML_PATH);
		if (file.exists())
		{
			this.colibriPropertiesPath.setText(file.getAbsolutePath());
		}
		this.colibriPropertiesPath.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				final File file = new File(DatabaseWizardMigrationPage.this.colibriPropertiesPath.getText());
				if (file.exists())
				{
					DatabaseWizardMigrationPage.this.setPageComplete(DatabaseWizardMigrationPage.this.checkOldColibri(file));
				}
			}

		});

		this.selectColibriPropertiesPath = new Button(composite, SWT.PUSH);
		this.selectColibriPropertiesPath.setLayoutData(new GridData());
		this.selectColibriPropertiesPath.setText("...");
		this.selectColibriPropertiesPath.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				final FileDialog dialog = new FileDialog(DatabaseWizardMigrationPage.this.getShell());
				dialog.setFileName("colibri.xml");
				dialog.setFilterExtensions(new String[] { "*.xml" });
				final java.io.File properties = new java.io.File(DatabaseWizardMigrationPage.this.colibriPropertiesPath
						.getText());
				dialog.setFilterPath(!properties.getAbsolutePath().isEmpty() && properties.exists() ? properties
						.getAbsolutePath() : "");
				dialog.setText("Pfad zur Datei colibri.xml");
				final String value = dialog.open();
				if (value != null)
				{
					DatabaseWizardMigrationPage.this.colibriPropertiesPath.setText(value);
					final File file = new File(value);
					if (file.exists())
					{
						DatabaseWizardMigrationPage.this.setPageComplete(DatabaseWizardMigrationPage.this.checkOldColibri(file));
					}
				}
			}

		});

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;

		label = new Label(composite, SWT.WRAP);
		label.setText("Kassen mit aktivierter Checkbox werden übernommen. Ist die Spalte 'Host' einer Kasse ausgefüllt, so wird die Kasse der entsprechenden Station zugeordnet.");
		label.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;

		this.salespointViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		this.salespointViewer.setContentProvider(new SalespointContentProvider());
		this.salespointViewer.setCheckStateProvider(new ICheckStateProvider()
		{
			@Override
			public boolean isChecked(final Object element)
			{
				if (element instanceof Salespoint)
				{
					return ((Salespoint) element).migrate;
				}
				return false;
			}

			@Override
			public boolean isGrayed(final Object element)
			{
				return false;
			}
		});
		this.salespointViewer.setSorter(new SalespointSorter());
		this.salespointViewer.addCheckStateListener(new ICheckStateListener()
		{
			@Override
			public void checkStateChanged(final CheckStateChangedEvent event)
			{
				if (event.getElement() instanceof Salespoint)
				{
					((Salespoint) event.getElement()).migrate = event.getChecked();
				}
				DatabaseWizardMigrationPage.this.setPageComplete(DatabaseWizardMigrationPage.this.validatePage());
			}
		});
		this.salespointViewer.getTable().setLayoutData(gridData);
		this.salespointViewer.getTable().setHeaderVisible(true);
		this.salespointViewer.getTable().setLinesVisible(true);

		TableViewerColumn viewerColumn = new TableViewerColumn(this.salespointViewer, SWT.NONE);
		viewerColumn.getColumn().setText("Bezeichnung");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Salespoint)
				{
					final Salespoint salespoint = (Salespoint) cell.getElement();
					cell.setText(salespoint.name);
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.salespointViewer, SWT.NONE);
		viewerColumn.getColumn().setText("Standort");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Salespoint)
				{
					final Salespoint salespoint = (Salespoint) cell.getElement();
					cell.setText(salespoint.place);
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.salespointViewer, SWT.NONE);
		viewerColumn.getColumn().setText("Host");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Salespoint)
				{
					final Salespoint salespoint = (Salespoint) cell.getElement();
					cell.setText(salespoint.host);
				}
			}
		});
		viewerColumn.setEditingSupport(new HostEditingSupport(this.salespointViewer));

		this.setPageComplete(this.validatePage());

		this.setControl(composite);
	}
	
	public Document getColibriXmlDocument()
	{
		return this.document;
	}

	public Document getDocument()
	{
		return this.document;
	}

	public Salespoint[] getSalespoints()
	{
		return (Salespoint[]) this.salespointViewer.getInput();
	}

	public Version getVersion()
	{
		return this.version;
	}

	public boolean migrate()
	{
		return this.migrate();
	}

	public void notifyListeners(final Event event)
	{
		for (final Listener listener : this.listeners)
		{
			listener.handleEvent(event);
		}
	}

	public void removeListener(final Listener listener)
	{
		this.listeners.remove(listener);
	}

	private boolean checkOldColibri(final File colibriXML)
	{
		final IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			@Override
			public void run(final IProgressMonitor monitor)
			{
				try
				{
					Activator.getDefault().log(
							"Gewählte Datei mit Verbindungsdaten: " + colibriXML.getAbsolutePath() + ".");
					monitor.beginTask("Die Verbindung wird geprüft...", 4);
					if (DatabaseWizardMigrationPage.this.loadDocument(colibriXML))
					{
						monitor.worked(1);
						if (DatabaseWizardMigrationPage.this.document != null)
						{
							Activator.getDefault().log("Verbindung mit der Datenbank herstellen.");
							final PersistenceBroker broker = DatabaseWizardMigrationPage.this.connect();
							monitor.worked(1);
							if ((broker != null) && !broker.isClosed())
							{
								Activator.getDefault().log("Verbindung hergestellt.");
								DatabaseWizardMigrationPage.this.version = DatabaseWizardMigrationPage.this.getOldVersion(broker);
								monitor.worked(1);
								Activator.getDefault().log("Kassenstationen einlesen.");
								final Salespoint[] salespoints = DatabaseWizardMigrationPage.this.getOldSalespoints(broker);
								final String id = DatabaseWizardMigrationPage.this.document.getRootElement()
										.getChild("salespoint").getAttributeValue("id");
								final Long salespointId = Long.valueOf(id);
								for (final Salespoint salespoint : salespoints)
								{
									salespoint.migrate = true;
									if (salespoint.getId().equals(salespointId))
									{
										salespoint.host = HostnameResolver.HOSTNAME.getHostname();
									}
								}
								DatabaseWizardMigrationPage.this.salespointViewer.setInput(salespoints);
								final TableColumn[] tableColumns = DatabaseWizardMigrationPage.this.salespointViewer.getTable()
										.getColumns();
								for (final TableColumn tableColumn : tableColumns)
								{
									tableColumn.pack();
								}
								DatabaseWizardMigrationPage.this.salespointViewer.setAllChecked(true);
								monitor.worked(1);
								DatabaseWizardMigrationPage.this.disconnect(broker);
							}
						}
					}
				}
				finally
				{
					monitor.done();
				}
			}
		};
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(this.getShell());
		try
		{
			dialog.run(false, false, runnable);
		}
		catch (final InvocationTargetException e)
		{
		}
		catch (final InterruptedException e)
		{
		}
		finally
		{
			dialog.close();
		}
		return this.validatePage();
	}

	private PersistenceBroker connect()
	{
		return DatabaseMigrator.createOjbPersistenceBroker(this.document);
	}

	private void disconnect(final PersistenceBroker broker)
	{
		DatabaseMigrator.releasePersistenceBroker(broker);
	}

	@SuppressWarnings("unchecked")
	private Salespoint[] getOldSalespoints(final PersistenceBroker broker)
	{
		final Criteria criteria = new Criteria();
		criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		final Query query = QueryFactory.newQuery(Salespoint.class, criteria);
		return (Salespoint[]) broker.getCollectionByQuery(query).toArray(new Salespoint[0]);
	}

	@SuppressWarnings("unchecked")
	private Version getOldVersion(final PersistenceBroker broker)
	{
		try
		{
			final Query query = new QueryByCriteria(ch.eugster.pos.db.Version.class);
			final Collection<Version> versions = broker.getCollectionByQuery(query);
			return versions.iterator().next();
		}
		catch (final Exception e)
		{
			return null;
		}
	}

	private boolean loadDocument(final File file)
	{
		final SAXBuilder builder = new SAXBuilder();
		try
		{
			Activator.getDefault().log("Datei einlesen.");
			this.document = builder.build(DatabaseWizardMigrationPage.this.colibriPropertiesPath.getText());
			Activator.getDefault().log("Datei gelesen.");
			return true;
		}
		catch (final JDOMException e)
		{
			Activator.getDefault().log("Fehler beim Einlesen der Datei: " + e.getLocalizedMessage());
		}
		catch (final IOException e)
		{
			Activator.getDefault().log("Fehler beim Einlesen der Datei: " + e.getLocalizedMessage());
		}
		return false;
	}

	private boolean validatePage()
	{
		if (this.colibriPropertiesPath.getText().isEmpty())
		{
			this.setMessage("Geben Sie den Pfad zur Konfigurationsdatei (colibri.xml) der Vorgängerversion ein.",
					IMessageProvider.INFORMATION);
			return false;
		}
		else
		{
			if (this.document == null)
			{
				this.setMessage("Die ausgewählte Konfigurationsdatei ist ungültig.", IMessageProvider.ERROR);
			}
			if (this.version == null)
			{
				this.setMessage("Die Verbindung zur Datenbank der Vorgängerversion kann nicht hergestellt werden.",
						IMessageProvider.ERROR);
				return false;
			}
			else if (this.version.getData() != DatabaseMigrator.COLIBRITS_VERSION_MIGRATABLE)
			{
				this.setMessage("Die Vorgängerversion hat die Datenversion (" + this.version.getData()
						+ ") und kann nicht migriert werden (notwendig: "
						+ DatabaseMigrator.COLIBRITS_VERSION_MIGRATABLE + ").", IMessageProvider.ERROR);
				return false;
			}
			if (this.salespointViewer.getCheckedElements().length == 0)
			{
				this.setMessage("Wählen Sie für die Station, an der Sie sich gerade befinden, eine Kasse aus.",
						IMessageProvider.INFORMATION);
				return false;
			}
		}
		this.setMessage("Die Verbindung zur Datenbank der Vorgängerversion konnte erfolgreich hergestellt werden.",
				IMessageProvider.INFORMATION);
		return true;
	}

}
