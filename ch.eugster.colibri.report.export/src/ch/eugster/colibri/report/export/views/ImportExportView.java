package ch.eugster.colibri.report.export.views;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.jdom.Attribute;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.CurrentTaxQuery;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.queries.SettlementQuery;
import ch.eugster.colibri.persistence.queries.TaxQuery;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.queries.UserQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.ProviderState;
import ch.eugster.colibri.report.daterange.views.DateView;
import ch.eugster.colibri.report.export.Activator;
import ch.eugster.colibri.report.salespoint.views.SalespointView;

public class ImportExportView extends ViewPart implements IViewPart, ISelectionListener
{
	public static final String ID = "ch.eugster.colibri.report.export.view";

	private static final String TRANSFER_DTD = "transfer.dtd";

	private static final String EXPORT_FILE_EXTENSION = ".xml";

	private IDialogSettings settings;

	private Mode currentMode;

	private Composite panelContainer;
	
	private Button[] importExportButtons;
	
	private StackLayout layout;

	private static Group importPanel;

	private Text importPath;
	
	private Button importPathSelector;
	
	private Button deleteAfterImport;
	
	private Text savePath;
	
	private Button savePathSelector;
	
	private Text logPath;
	
	private Button logPathSelector;
	
	private static Group exportPanel;
	
	private ComboViewer exportSettlementViewer;
	
	private Text exportPath;
	
	private Button exportPathSelector;
	
	private Button start;
	
	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<BarcodeVerifier, BarcodeVerifier> barcodeVerifierTracker;

	private File importDirectory;
	
	private File saveDirectory;
	
	private File logDirectory;

	private Settlement exportSettlement;
	
	private boolean deleteFile;

	private File exportDirectory;
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);

		settings = Activator.getDefault().getDialogSettings().getSection(this.getClass().getName());
		if (settings == null)
		{
			settings = Activator.getDefault().getDialogSettings().addNewSection(this.getClass().getName());
		}
		try
		{
			int value = settings.getInt("import.export");
			currentMode = Mode.values()[value];
		}
		catch (NumberFormatException e)
		{
			settings.put("import.export", 0);
			currentMode = Mode.values()[0];
		}

		persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
		persistenceServiceTracker.open();
		
		barcodeVerifierTracker = new ServiceTracker<BarcodeVerifier, BarcodeVerifier>(Activator.getDefault().getBundle().getBundleContext(), BarcodeVerifier.class, null);
		barcodeVerifierTracker.open();
		
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(SalespointView.ID, this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DateView.ID, this);
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());
		
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());

		Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(Mode.values().length, true));
		group.setText("Belege");

		this.layout = new StackLayout();

		importExportButtons = new Button[Mode.values().length];
		
		for (int i = 0; i < importExportButtons.length; i++)
		{
			importExportButtons[i] = new Button(group, SWT.RADIO);
			importExportButtons[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			importExportButtons[i].setText(Mode.values()[i].action());
			importExportButtons[i].setData(new Integer(i));
			importExportButtons[i].addSelectionListener(new SelectionListener() 
			{
				@Override
				public void widgetSelected(SelectionEvent e) 
				{
					Button button = (Button) e.getSource();
					Integer value = (Integer) button.getData();
					currentMode = Mode.values()[value.intValue()];
					layout.topControl = currentMode.panel();
					panelContainer.layout();
					updateStartButton();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) 
				{
					widgetSelected(e);
				}
			});
		}

		panelContainer = new Composite(composite, SWT.NONE);
		panelContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		panelContainer.setLayout(layout);
		
		importPanel	= new Group(panelContainer, SWT.SHADOW_ETCHED_IN);
		importPanel.setLayout(new GridLayout(3, false));
		importPanel.setText(Mode.IMPORT.label());
		
		Label label = new Label(importPanel, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Import-Verzeichnis");
		
		importPath = new Text(importPanel, SWT.SINGLE | SWT.BORDER);
		importPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		String path = settings.get("import.path");
		importPath.setText(path == null ? System.getProperty("user.home") : path);
		importPath.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e) 
			{
				settings.put("import.path", importPath.getText());
			}
		});
		
		importPathSelector = new Button(importPanel, SWT.PUSH);
		importPathSelector.setLayoutData(new GridData());
		importPathSelector.setText("...");
		importPathSelector.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				DirectoryDialog dialog = new DirectoryDialog(getSite().getShell());
				File file = new File(importPath.getText());
				if (file.isDirectory())
				{
					dialog.setFilterPath(file.getAbsolutePath());
				}
				else
				{
					dialog.setFilterPath(System.getProperty("user.home"));
				}
				String path = dialog.open();
				if (path != null)
				{
					File importDirectory = new File(path);
					
					if (importDirectory.isDirectory())
					{
						importPath.setText(importDirectory.getAbsolutePath());
						settings.put("import.path", importDirectory.getAbsolutePath());
						updateStartButton();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});

		label = new Label(importPanel, SWT.None);
		label.setLayoutData(new GridData());
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		
		deleteAfterImport = new Button(importPanel, SWT.CHECK);
		deleteAfterImport.setLayoutData(gridData);
		deleteAfterImport.setText("Quelldateien nach erfolgreichem Import löschen");
		deleteAfterImport.setSelection(settings.getBoolean("delete.after.import"));
		deleteAfterImport.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				savePath.setEnabled(!deleteAfterImport.getSelection());
				savePathSelector.setEnabled(!deleteAfterImport.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});

		label = new Label(importPanel, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Sicherungs-Verzeichnis");
		
		savePath = new Text(importPanel, SWT.SINGLE | SWT.BORDER);
		savePath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		path = settings.get("save.path");
		savePath.setText(path == null ? System.getProperty("user.home") : path);
		savePath.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e) 
			{
				settings.put("save.path", savePath.getText());
			}
		});
		savePath.setEnabled(!deleteAfterImport.getSelection());

		savePathSelector = new Button(importPanel, SWT.PUSH);
		savePathSelector.setLayoutData(new GridData());
		savePathSelector.setText("...");
		savePathSelector.setEnabled(!deleteAfterImport.getSelection());
		savePathSelector.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				DirectoryDialog dialog = new DirectoryDialog(getSite().getShell());
				File file = new File(savePath.getText());
				if (file.isDirectory())
				{
					dialog.setFilterPath(file.getAbsolutePath());
				}
				else
				{
					dialog.setFilterPath(System.getProperty("user.home"));
				}
				String path = dialog.open();
				if (path != null)
				{
					File saveDirectory= new File(path);
					if (!saveDirectory.exists())
					{
						saveDirectory.mkdirs();
					}
					if (saveDirectory.isDirectory())
					{
						savePath.setText(saveDirectory.getAbsolutePath());
						settings.put("save.path", saveDirectory.getAbsolutePath());
						updateStartButton();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});

		label = new Label(importPanel, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Protokoll-Verzeichnis");
		
		logPath = new Text(importPanel, SWT.SINGLE | SWT.BORDER);
		logPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		path = settings.get("log.path");
		logPath.setText(path == null ? System.getProperty("user.home") : path);
		logPath.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e) 
			{
				settings.put("log.path", logPath.getText());
			}
		});

		logPathSelector = new Button(importPanel, SWT.PUSH);
		logPathSelector.setLayoutData(new GridData());
		logPathSelector.setText("...");
		logPathSelector.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				DirectoryDialog dialog = new DirectoryDialog(getSite().getShell());
				File file = new File(logPath.getText());
				if (file.isDirectory())
				{
					dialog.setFilterPath(file.getAbsolutePath());
				}
				else
				{
					dialog.setFilterPath(System.getProperty("user.home"));
				}
				String path = dialog.open();
				if (path != null)
				{
					File logDirectory= new File(path);
					if (!logDirectory.exists())
					{
						logDirectory.mkdirs();
					}
					if (logDirectory.isDirectory())
					{
						logPath.setText(logDirectory.getAbsolutePath());
						settings.put("log.path", logDirectory.getAbsolutePath());
						updateStartButton();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});

		exportPanel	= new Group(panelContainer, SWT.SHADOW_ETCHED_IN);
		exportPanel.setLayout(new GridLayout(3, false));
		exportPanel.setText(Mode.EXPORT.label());

		label = new Label(exportPanel, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Abschluss");
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		
		Combo combo = new Combo(exportPanel, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(gridData);
		
		exportSettlementViewer = new ComboViewer(combo);
		exportSettlementViewer.setContentProvider(new SettlementViewerContentProvider());
		exportSettlementViewer.setLabelProvider(new SettlementViewerLabelProvider());
		exportSettlementViewer.addSelectionChangedListener(new ISelectionChangedListener() 
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{
				updateStartButton();
			}
		});
		
		label = new Label(exportPanel, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Zielverzeichnis");
		
		exportPath = new Text(exportPanel, SWT.SINGLE | SWT.BORDER);
		exportPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		PersistenceService service = persistenceServiceTracker.getService();
		path = null;
		if (service != null)
		{
			SalespointQuery query = (SalespointQuery) service.getServerService().getQuery(Salespoint.class);
			Salespoint current = query.getCurrentSalespoint();
			if (current != null)
			{
				path = current.getExportPath();
			}
		}
		if (path == null || path.isEmpty())
		{
			path = settings.get("export.path");
		}
		exportPath.setText(path == null ? System.getProperty("user.home") : path);
		exportPath.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e) 
			{
				settings.put("export.path", exportPath.getText());
			}
		});
		
		exportPathSelector = new Button(exportPanel, SWT.PUSH);
		exportPathSelector.setLayoutData(new GridData());
		exportPathSelector.setText("...");
		exportPathSelector.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				DirectoryDialog dialog = new DirectoryDialog(getSite().getShell());
				File file = new File(exportPath.getText());
				if (file.isDirectory())
				{
					dialog.setFilterPath(file.getAbsolutePath());
				}
				else
				{
					dialog.setFilterPath(System.getProperty("user.home"));
				}
				String path = dialog.open();
				if (path != null)
				{
					File exportDirectory= new File(path);
					if (exportDirectory.isDirectory())
					{
						exportPath.setText(exportDirectory.getAbsolutePath());
						updateStartButton();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});

		Composite buttonComposite = new Composite(composite, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonComposite.setLayout(new GridLayout(2, false));

		label = new Label(buttonComposite, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		start = new Button(buttonComposite, SWT.PUSH);
		start.setText(currentMode.equals(Mode.IMPORT) ? "Importieren" : "Exportieren");
		start.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				if (currentMode != null)
				{
					try
					{
						start.setEnabled(false);
						if (currentMode.equals(Mode.IMPORT))
						{
							importDirectory = new File(importPath.getText());
							saveDirectory = new File(savePath.getText());
							logDirectory = new File(logPath.getText());
							deleteFile = deleteAfterImport.getSelection();
							if (importDirectory.isDirectory())
							{
								final File[] files = importDirectory.listFiles(new FileFilter() 
								{
									@Override
									public boolean accept(File file) 
									{
										return file.getName().endsWith(".xml");
									}
								});
								if (files.length > 0)
								{
									doImport(files);
								}
								else
								{
									MessageDialog.openInformation(ImportExportView.this.getSite().getShell(), "Keine Dateien vorhanden", "Es sind keine Dateien zum Verarbeiten vorhanden.");
								}
							}
							else
							{
								MessageDialog.openError(ImportExportView.this.getSite().getShell(), "Ungültiges Verzeichnis", "Das gewählte Verzeichnis ist ungültig.");
							}
						}
						else if (currentMode.equals(Mode.EXPORT))
						{
							IStructuredSelection ssel = (IStructuredSelection) exportSettlementViewer.getSelection();
							exportSettlement = (Settlement) ssel.getFirstElement();
							exportDirectory = new File(exportPath.getText());
							Job job = new Job(currentMode.action()) 
							{
								@Override
								protected IStatus run(IProgressMonitor monitor) 
								{
									doExport(monitor);
									return Status.OK_STATUS;
								}
							};
							job.addJobChangeListener(new JobChangeAdapter() 
							{
								@Override
								public void done(IJobChangeEvent event) 
								{
									Display.getDefault().syncExec(new Runnable()
									{
										@Override
										public void run() 
										{
											start.setEnabled(true);
										}
									});
								}

							});
							job.setUser(true);
							job.schedule();
						}
					}
					finally
					{
						start.setEnabled(true);
					}
				}
			}
		});
		importExportButtons[currentMode.ordinal()].setSelection(true);
		layout.topControl = currentMode.panel();
		panelContainer.layout();
	}

	private void doImport(final File[] files)
	{
		start.setEnabled(false);
		final Result result = new Result();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(this.getSite().getShell());
		try 
		{
			dialog.run(true, true, new IRunnableWithProgress() 
			{
				@Override
				public void run(IProgressMonitor iProgressMonitor)
						throws InvocationTargetException, InterruptedException 
				{
					SubMonitor monitor = SubMonitor.convert(iProgressMonitor);
					try
					{
						monitor.beginTask("Importiere...", files.length);
						for (File file : files)
						{
							Document document = load(file);
							@SuppressWarnings("unchecked")
							List<Element> elements = document.getRootElement().getChildren("receipt");
							doImport(monitor.newChild(elements.size()), elements, file, result);
						}
					}
					finally
					{
						monitor.done();
					}
					
				}
			});
		} 
		catch (InvocationTargetException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			StringBuilder message = new StringBuilder("Es wurden " + result.files + " Dateien eingelesen:\n");
			message = message.append(result.read + " Datensätze wurden gelesen, " + result.write + " Datensätze wurden geschrieben, " + result.exist + " Datensätze waren bereits vorhanden, " + result.error + " Datensätze waren fehlerhaft.");
			MessageDialog.openInformation(getSite().getShell(), "Zusammenfassung", message.toString());
			start.setEnabled(true);
		}
//		UIJob job = new UIJob("Daten werden importiert...") 
//		{
//			@Override
//			public IStatus runInUIThread(IProgressMonitor monitor) 
//			{
//				try
//				{
//					monitor.beginTask("Importieren", files.length);
//					for (File file : files)
//					{
//						Document document = load(file);
//						if (document != null)
//						{
//							result.files += 1;
//							List<?> elements = document.getRootElement().getChildren("receipt");
//							if (!doImport(new SubProgressMonitor(monitor, elements.size()), elements, file, result))
//							{
//								monitor.setCanceled(true);
//							}
//						}
//					}
//				}
//				finally
//				{
//					monitor.done();
//				}
//				return Status.OK_STATUS;
//			}
//		};
//		job.addJobChangeListener(new JobChangeAdapter() 
//		{
//			@Override
//			public void done(IJobChangeEvent event) 
//			{
//				start.setEnabled(true);
//				StringBuilder message = new StringBuilder("Es wurden " + result.files + " Dateien eingelesen:\n");
//				message = message.append(result.read + " Datensätze wurden gelesen, " + result.write + " Datensätze wurden geschrieben, " + result.exist + " Datensätze waren bereits vorhanden, " + result.error + " Datensätze waren fehlerhaft.");
//				MessageDialog.openInformation(getSite().getShell(), "Zusammenfassung", message.toString());
//			}
//		});
//		job.setUser(true);
//		job.schedule();
	}

	public Salespoint[] getSelectedSalespoints()
	{
		IViewReference[] references = this.getSite().getPage().getViewReferences();
		for (IViewReference reference : references)
		{
			IViewPart part = reference.getView(true);
			if (part instanceof SalespointView)
			{
				SalespointView view = (SalespointView) part;
				return view.getSelectedSalespoints();
			}
		}
		return null;
	}
	
	public Calendar[] getSelectedDateRange()
	{
		IViewReference[] references = this.getSite().getPage().getViewReferences();
		for (IViewReference reference : references)
		{
			IViewPart part = reference.getView(true);
			if (part instanceof DateView)
			{
				DateView view = (DateView) part;
				return view.getDates();
			}
		}
		return null;
	}
	
	private PrintWriter createLog(File file)
	{
		if (!logDirectory.exists())
		{
			logDirectory.mkdirs();
		}
		String path = logDirectory.getAbsolutePath();
		if (!path.endsWith(File.separator))
		{
			path = path + File.separator;
		}
		File log = new File(path + file.getName() + ".log");
		PrintWriter printer = null;
		try 
		{
			log.createNewFile();
			printer = new PrintWriter(log);
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		return printer;
	}
	
	private void log(PrintWriter printer, String message)
	{
		if (printer != null)
		{
			printer.println(message);
		}
	}

	private void closeLog(PrintWriter printer)
	{
		if (printer != null)
		{
			printer.flush();
			printer.close();
		}
	}
	private boolean doImport(IProgressMonitor subMonitor, List<Element> elements, File file, Result result)
	{
		if (!saveDirectory.exists())
		{
			saveDirectory.mkdirs();
		}

		PrintWriter printer = createLog(file);
		try
		{
			log(printer, "Import: " + file.getName()+ " (" + elements.size() + " Belege.");

			PersistenceService service = persistenceServiceTracker.getService();
			if (service != null)
			{
				SubMonitor monitor = SubMonitor.convert(subMonitor);
				SalespointQuery salespointQuery = (SalespointQuery) service.getServerService().getQuery(Salespoint.class);
//				monitor.subTask(file.getName() + " wird importiert...");
				monitor.beginTask(file.getName(), elements.size());
				try
				{
					result.files++;
					Salespoint salespoint = null;
					Settlement settlement = null;
					for (Element element : elements)
					{
						if (monitor.isCanceled())
						{
							return false;
						}
						result.read++;
						try
						{
							String mappingId = element.getAttributeValue("salespoint-id");
							if (salespoint == null || !salespoint.getMapping().equals(mappingId))
							{
								Collection<Salespoint> salespoints = salespointQuery.selectByMapping(mappingId);
								if (salespoints.isEmpty())
								{
									result.error++;
									log(printer, "Warnung: Kasse mit ExportId " + mappingId + " ist nicht vorhanden.");
									continue;
								}
								else
								{
									salespoint = salespoints.iterator().next();
								}
								
							}
							Long settled = getSettlementTimeInMillis(element.getAttributeValue("settlement"));
							if (settled == null)
							{
								result.error++;
								log(printer, "Warnung: Abschluss hat keine Abschlussdatum ist nicht vorhanden.");
								continue;
							}
							Calendar calendar = GregorianCalendar.getInstance();
							calendar.setTimeInMillis(settled.longValue());
							if (settlement == null || !settlement.getSettled().equals(calendar))
							{
								settlement = getSettlement(service, salespoint, settled.longValue());
							}
							Long otherId = getOtherId(element);
							if (!receiptExists(service, otherId))
							{
								Receipt receipt = this.convertToReceipt(element, settlement);
								service.getServerService().persist(receipt, false);
								result.write++;
							}
							else
							{
								result.exist++;
								if (printer != null)
								{
									String id = element.getAttributeValue("id");
									log(printer, "Warnung: Beleg " + id + " ist bereits vorhanden.");
								}
								Activator.getDefault().log(LogService.LOG_ERROR, "Beleg mit der Id " + element.getAttributeValue("id") + ": Beleg existiert bereits.");
							}
						}
						catch (Exception e)
						{
							Activator.getDefault().log(LogService.LOG_ERROR, "Fehler beim verarbeiten von Beleg mit der Id " + element.getAttributeValue("id") + ": " + e.getMessage());
							result.error++;
							String id = element.getAttributeValue("id");
							log(printer, "Fehler in Beleg " + id + ": " + e.getLocalizedMessage());
						}
						monitor.worked(1);
					}
				}
				finally
				{
					monitor.done();
				}
			}
			if (printer != null)
			{
				log(printer, "");
				log(printer, "Gelesen: " + DecimalFormat.getIntegerInstance().format(result.read));
				log(printer, "Eingefügt: " + DecimalFormat.getIntegerInstance().format(result.write));
				log(printer, "Vorhanden: " + DecimalFormat.getIntegerInstance().format(result.exist));
				log(printer, "Fehler: " + DecimalFormat.getIntegerInstance().format(result.error));
			}
			if (deleteFile)
			{
				file.delete();
			}
			else
			{
				StringBuilder save = new StringBuilder(saveDirectory.getAbsolutePath());
				if (!save.toString().endsWith(File.separator))
				{
					save = save.append(File.separator);
				}
				save = save.append(file.getName());
				if (file.renameTo(new File(save.toString())))
				{
					if (printer != null)
					{
						log(printer, "");
						log(printer, "Datei gesichert in: " + save.toString());
					}
				}
				else
				{
					if (printer != null)
					{
						log(printer, "");
						log(printer, "Sicherung der Datei konnte nicht durchgeführt werden.");
					}
				}
			}
		}
		finally
		{
			closeLog(printer);
		}
		return true;
	}
	
	private Long getSettlementTimeInMillis(String settlement)
	{
		Long value = null;
		try
		{
			value = Long.valueOf(settlement);
		}
		catch(NumberFormatException e)
		{
			
		}
		return value;
	}
	
	private void doExport(final IProgressMonitor monitor)
	{
		PersistenceService service = persistenceServiceTracker.getService();
		if (service != null)
		{
			ReceiptQuery query = (ReceiptQuery) service.getServerService().getQuery(Receipt.class);
			Collection<Receipt> receipts = query.selectBySettlement(exportSettlement, null);
			if (receipts.size() > 0)
			{
				try
				{
					Iterator<Receipt> iterator = receipts.iterator();
					monitor.beginTask("Belege werden exportiert...", receipts.size());
					Document document = initialize(exportSettlement.getSalespoint(), exportSettlement.getSettled(), exportSettlement.getReceiptCount());
					for (int i = 0; iterator.hasNext(); i++)
					{
						if (monitor.isCanceled())
						{
							return;
						}
						add(document, iterator.next());
						monitor.worked(i);
					}
					save(document);
				}
				finally
				{
					monitor.done();
					Display.getDefault().syncExec(new Runnable()
					{
						@Override
						public void run() 
						{
							if (monitor.isCanceled())
							{
								MessageDialog.openWarning(getSite().getShell(), "Export abgebrochen", "Der Export wurde abgebrochen.");
							}
							else
							{
								MessageDialog.openInformation(getSite().getShell(), "Export abgeschlossen", "Der Export wurde durchgeführt.");
							}
						}
					});
				}
			}
			else
			{
				Display.getDefault().syncExec(new Runnable()
				{
					@Override
					public void run() 
					{
						MessageDialog.openInformation(getSite().getShell(), "Keine Belege", "Für die gewählten Kriterien sind keine Belege vorhanden.");
					}
				});
			}
		}
	}
	
	private Settlement getSettlement(PersistenceService service, Salespoint salespoint, long date)
	{
		Settlement settlement = null;
		SettlementQuery settlementQuery = (SettlementQuery) service.getServerService().getQuery(Settlement.class);
		Collection<Settlement> settlements = settlementQuery.selectBySalespointsAndSettled(new Salespoint[] { salespoint }, date, date);
		if (settlements.isEmpty())
		{
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTimeInMillis(date);
			settlement = Settlement.newInstance(salespoint);
			settlement.setSettled(calendar);
			settlement.setDeleted(false);
			try
			{
				service.getServerService().persist(settlement);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			settlement = settlements.iterator().next();
		}
		return settlement;
	}
	
	private boolean receiptExists(PersistenceService service, Long id)
	{
		ReceiptQuery query = (ReceiptQuery) service.getServerService().getQuery(Receipt.class);
		Receipt receipt = query.findWithOtherId(id);
		return receipt != null;
	}
	
	@Override
	public void dispose()
	{
		this.getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(SalespointView.ID, this);
		this.getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(DateView.ID, this);

		persistenceServiceTracker.close();
		
		super.dispose();
	}

	@Override
	public void setFocus()
	{
		this.importExportButtons[this.currentMode.ordinal()].setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		this.setSettlementViewerInput();
		updateStartButton();
	}
	
	private void setSettlementViewerInput()
	{
		if (getSelectedSalespoints() != null && getSelectedSalespoints().length > 0 && getSelectedDateRange() != null && getSelectedDateRange().length == 2)
		{
			ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
			try
			{
				tracker.open();
				PersistenceService service = tracker.getService();
				if (service != null)
				{
					SettlementQuery query = (SettlementQuery) service.getServerService().getQuery(Settlement.class);
					Collection<Settlement> settlements = query.selectBySalespointsAndSettled(getSelectedSalespoints(), Long.valueOf(getSelectedDateRange()[0].getTimeInMillis()), Long.valueOf(getSelectedDateRange()[1].getTimeInMillis()));
					this.exportSettlementViewer.setInput(settlements.toArray(new Settlement[0]));
				}
			}
			finally
			{
				tracker.close();
				start.setEnabled(true);
			}
		}
	}

	private void updateStartButton()
	{
		boolean enabled = false;
		if (currentMode.equals(Mode.IMPORT))
		{
			File importFile = new File(importPath.getText());
			enabled = importFile.isDirectory();
			if (enabled)
			{
				if (!deleteAfterImport.getSelection())
				{
					File saveFile = new File(savePath.getText());
					enabled = saveFile.isDirectory() && !saveFile.getAbsolutePath().equals(importFile.getAbsolutePath());
				}
			}
			if (enabled)
			{
				enabled = new File(logPath.getText()).isDirectory();
			}
		}
		else if (currentMode.equals(Mode.EXPORT))
		{
			File file = new File(exportPath.getText());
			enabled = file.isDirectory();
			enabled = enabled && !this.exportSettlementViewer.getSelection().isEmpty();
		}
		this.start.setText(currentMode.label());
		this.start.setEnabled(enabled);
	}

	private Document load(File file)
	{
		loadDTD(file.getParent());
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(true);

		Document document = null;
		try 
		{
			document = builder.build(file);
			Activator.getDefault().log(LogService.LOG_INFO, "Importdokument geladen.");
		} 
		catch (Exception e) 
		{
			String path = logPath.getText();
			path = path.endsWith(File.separator) ? path : path + File.separator;
			File log = new File(path + file.getName() + ".log");
			PrintWriter printer = null;
			try 
			{
				log.createNewFile();
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
			try 
			{
				printer = new PrintWriter(log);
				printer.println("Fehler beim Lesen der Datei: " + file.getName());
				e.printStackTrace(printer);
				printer.close();
			} 
			catch (FileNotFoundException e1) 
			{
				e1.printStackTrace();
			}
		} 
		return document;
	}
	
	private void loadDTD(String targetDirectory)
	{
		String dtd = targetDirectory + File.separator + "transfer.dtd";
		File target = new File(dtd);
		if (!target.exists())
		{
			OutputStream out = null;
			InputStream in = null;
			try {
				out = new FileOutputStream(target);
				in = Activator.getDefault().getBundle().getEntry("/transfer.dtd").openStream();
				byte[] buffer = new byte[1024];
			    int bytesRead = 0;
			    while ((bytesRead = in.read(buffer)) != -1)
			    {
			        out.write(buffer, 0, bytesRead);
			    }
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			finally
			{
				try 
				{
					if (in != null)
					{
						in.close();
					}
					if (out != null)
					{
						out.close();
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Document initialize(Salespoint salespoint, Calendar calendar, long count)
	{
		Document document = new Document();
		document.setDocType(new DocType("transfer", TRANSFER_DTD));
		document.setRootElement(new Element("transfer"));
		document.getRootElement().setAttribute(new Attribute("salespoint", salespoint.getMapping()));
		document.getRootElement().setAttribute(new Attribute("date", Long.valueOf(calendar.getTimeInMillis()).toString()));
		document.getRootElement().setAttribute(new Attribute("count", Long.valueOf(count).toString()));
		Activator.getDefault().log(LogService.LOG_INFO, "Exportdokument initialisiert.");
		return document;
	}
	
	private String getExportDirectory()
	{
		String path = exportDirectory.getAbsolutePath();
		if (!path.endsWith(File.separator))
		{
			path = path + File.separator;
		}
		File dtdFile = new File(path + TRANSFER_DTD);
		if (!dtdFile.exists())
		{
			createDTDFile(dtdFile);
		}
		return path;
	}
	
	private void save(Document document)
	{
		String salespoint = document.getRootElement().getAttributeValue("salespoint");
		String date = document.getRootElement().getAttributeValue("date");
		String filename = salespoint + date + EXPORT_FILE_EXTENSION;
		File file = new File(getExportDirectory() + filename);
		try 
		{
			final Format format = Format.getPrettyFormat();
			final XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(format);
			FileOutputStream out = new FileOutputStream(file);
			outputter.output(document, out);
			out.close();
			Activator.getDefault().log(LogService.LOG_INFO, "Export ausgeführt.");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void createDTDFile(File dtdFile)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dtdFile)));
			URL url = Activator.getDefault().getBundle().getEntry("/" + TRANSFER_DTD);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = reader.readLine();
			while (line != null)
			{
				writer.write(line + "\n");
				line = reader.readLine();
			}
			reader.close();
			writer.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	private void add(Document document, Receipt receipt) 
	{
		Element root = document.getRootElement();
		Element element = new Element("receipt");
		convertToJdomElement(receipt, element);
		root.addContent(element);
	}

	private Element convertToJdomElement(Receipt receipt, Element element)
	{
		element.setAttribute("id", receipt.getId().toString()); //$NON-NLS-2$
		element.setAttribute("timestamp", Long.valueOf(receipt.getTimestamp().getTimeInMillis()).toString());
		element.setAttribute("number", receipt.getNumber().toString());
		element.setAttribute("transaction-id", "0");
		element.setAttribute("booking-id", "0");
		element.setAttribute("salespoint-id", receipt.getSettlement().getSalespoint().getMapping());
		element.setAttribute("user-id", receipt.getUser().getUsername());
		element.setAttribute("foreign-currency-id", receipt.getDefaultCurrency().getCode());
		element.setAttribute("status", receipt.getState().compatibleState());
		element.setAttribute("settlement", receipt.getSettlement().getId().toString());
		element.setAttribute("amount", Double.toString(receipt.getPaymentAmount(Receipt.QuotationType.REFERENCE_CURRENCY)));
		element.setAttribute("customer-id", receipt.getCustomerCode());
		element.setAttribute("transferred", Boolean.valueOf(receipt.isTransferred()).toString());

		updatePositionElements(receipt, element);
		updatePaymentElements(receipt, element);
		return element;
	}
	
	private User getUser(String username) throws IllegalArgumentException
	{
		if (username == null || username.isEmpty())
		{
			return null;
		}
		PersistenceService service = persistenceServiceTracker.getService();
		if (service != null)
		{
			UserQuery query = (UserQuery) service.getServerService().getQuery(User.class);
			return query.findByUsername(username);
		}
		throw new IllegalArgumentException("Der Benutzer mit Benutzernamen " + username + " ist nicht vorhanden.");
	}
	
	private ProductGroup getProductGroup(String code) throws IllegalArgumentException
	{
		PersistenceService service = persistenceServiceTracker.getService();
		if (service != null)
		{
			ProductGroupQuery query = (ProductGroupQuery) service.getServerService().getQuery(ProductGroup.class);
			return query.findByMappingId(code);
		}
		throw new IllegalArgumentException("Die Warengruppe mit der ExportId " + code + " ist nicht vorhanden.");
	}
	
	private PaymentType getPaymentType(String code) throws IllegalArgumentException
	{
		PersistenceService service = persistenceServiceTracker.getService();
		if (service != null)
		{
			PaymentTypeQuery query = (PaymentTypeQuery) service.getServerService().getQuery(PaymentType.class);
			PaymentType paymentType = query.findByMappingId(code);
			if (paymentType != null)
			{
				return paymentType;
			}
		}
		throw new IllegalArgumentException("Die Zahlungsart mit der ExportId " + code + " ist nicht vorhanden.");
	}
	
	private CurrentTax getCurrentTax(Tax tax, String code) throws IllegalArgumentException
	{
		Long validFrom = Long.valueOf(code);
		PersistenceService service = persistenceServiceTracker.getService();
		if (service != null)
		{
			CurrentTaxQuery query = (CurrentTaxQuery) service.getServerService().getQuery(CurrentTax.class);
			Collection<CurrentTax> currentTaxes = query.selectByValidFrom(tax, validFrom.longValue());
			if (currentTaxes.size() > 0)
			{
				return currentTaxes.iterator().next();
			}
		}
		throw new IllegalArgumentException("Die Mehrwertsteuer mit der ExportId " + code + " ist nicht vorhanden.");
	}
	
	private Tax getTax(String code) throws IllegalArgumentException
	{
		if (code != null && code.length() == 2)
		{
			PersistenceService service = persistenceServiceTracker.getService();
			if (service != null)
			{
				TaxTypeQuery typeQuery = (TaxTypeQuery) service.getServerService().getQuery(TaxType.class);
				TaxType type = typeQuery.selectByCode(code.substring(0, 1));
				if (type != null)
				{
					TaxRateQuery rateQuery = (TaxRateQuery) service.getServerService().getQuery(TaxRate.class);
					TaxRate rate = rateQuery.selectByCode(code.substring(1));
					if (rate != null)
					{
						TaxQuery query = (TaxQuery) service.getServerService().getQuery(Tax.class);
						Collection<Tax> taxes = query.selectByTaxTypeAndTaxRate(type, rate);
						if (taxes.size() > 0)
						{
							return taxes.iterator().next();
						}
					}
				}
			}
		}
		throw new IllegalArgumentException("Die Mehrwertsteuer mit dem Code " + code + " ist nicht vorhanden.");
	}

	private Customer getCustomer(String code)
	{
		if (code == null || code.isEmpty())
		{
			return null;
		}
		Integer customerCode = Integer.valueOf(code);
		Customer customer = new Customer();
		customer.setId(customerCode);
		return customer;
	}
	
	private Receipt.State getState(String compatibleState)
	{
		for (Receipt.State state : Receipt.State.values())
		{
			if (state.compatibleState().equals(compatibleState))
			{
				return state;
			}
		}
		throw new IllegalArgumentException("Der Belegstatus " + compatibleState + " ist ungültig");
	}

	private Receipt convertToReceipt(Element element, Settlement settlement) throws IllegalArgumentException
	{
		Receipt receipt = Receipt.newInstance(settlement, getUser(element.getAttributeValue("user-id")));
		receipt.setState(getState(element.getAttributeValue("status")));
		receipt.setBookkeepingTransaction(Long.valueOf(element.getAttributeValue("booking-id")).longValue());
		receipt.setCustomer(getCustomer(element.getAttributeValue("customer-id")));
		receipt.setDefaultCurrency(settlement.getSalespoint().getPaymentType().getCurrency());
		receipt.setDeleted(false);
		receipt.setForeignCurrency(settlement.getSalespoint().getPaymentType().getCurrency());
		receipt.setTimestamp(getTimestamp(element));
		receipt.setHour(getHour(receipt.getTimestamp()));
		receipt.setNumber(getNumber(receipt, element));
		receipt.setOtherId(getOtherId(element));
		receipt.setProviderUpdated(true);
		receipt.setReferenceCurrency(settlement.getSalespoint().getCommonSettings().getReferenceCurrency());
		receipt.setSettlement(settlement);
		receipt.setTransaction(Long.valueOf(element.getAttributeValue("transaction-id")));
		receipt.setTransferred(Boolean.valueOf(element.getAttributeValue("transferred")));
		receipt.setUser(getUser(element.getAttributeValue("user-id")));

		updatePositions(element, receipt);
		updatePayments(element, receipt);
		return receipt;
	}
	
	private Long getOtherId(Element element) throws IllegalArgumentException
	{
		String id = element.getAttributeValue("id");
		try
		{
			return Long.valueOf(id);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Die Id " + id + " ist nicht numerisch.");
		}
	}
	
	private Calendar getTimestamp(Element element)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(Long.valueOf(element.getAttributeValue("timestamp")).longValue());
		return calendar;
	}

	private Long getNumber(Receipt receipt, Element element)
	{
		if (receipt.getState().equals(Receipt.State.REVERSED))
		{
			return Long.valueOf(element.getAttributeValue("number").substring(1));
		}
		else
		{
			return Long.valueOf(element.getAttributeValue("number"));
		}
	}
	
	private int getHour(Calendar timestamp)
	{
		return timestamp.get(Calendar.HOUR_OF_DAY);
	}

	private void updatePositions(Element receiptElement, Receipt receipt) throws IllegalArgumentException
	{
		@SuppressWarnings("unchecked")
		List<Element> positionElements = receiptElement.getChildren("position");
		for (Element positionElement : positionElements)
		{
			receipt.addPosition(convertToPosition(positionElement, receipt));
			receipt.setProviderUpdated(isGalileoUpdated(receipt));
		}
	}
	
	private boolean isGalileoUpdated(Receipt receipt)
	{
		Collection<Position> positions = receipt.getPositions();
		for (Position position : positions)
		{
			if (position.isBookProvider() && !position.isProviderBooked())
			{
				return false;
			}
		}
		return true;
	}

	private void updatePayments(Element receiptElement, Receipt receipt)
	{
		@SuppressWarnings("unchecked")
		List<Element> paymentElements = receiptElement.getChildren("payment");
		for (Element paymentElement : paymentElements)
		{
			receipt.addPayment(convertToPayment(paymentElement, receipt));
		}
	}
	
	private Option getOption(String optCode)
	{
		for (Position.Option option : Position.Option.values())
		{
			if (option.toCode().equals(optCode))
			{
				return option;
			}
		}
		return Position.Option.ARTICLE;
	}
	
	private Position convertToPosition(Element element, Receipt receipt) throws IllegalArgumentException
	{
		Position position = Position.newInstance(receipt);
		position.setBookProvider(Boolean.valueOf(element.getAttributeValue("galileo-book")));
		position.setDeleted(false);
		position.setDiscount(Double.valueOf(element.getAttributeValue("discount")));
		position.setEbook(false);
//        double lw = Double.valueOf(element.getAttributeValue("amount"));
        /*
         * ACHTUNG amount-fc ist der Brutto-Betrag, nicht etwa, wie der Name vermuten lässt, der Betrag in Fremdwährung
         * daher soll die Währung immer Landeswährung sein.
         */
//        double fw = Double.valueOf(element.getAttributeValue("amount-fc"));
		Currency currency = receipt.getReferenceCurrency();
		position.setForeignCurrency(currency);
//		if (!currency.getId().equals(receipt.getSettlement().getSalespoint().getCommonSettings().getReferenceCurrency().getId()))
//		{
//			double quotation = fw / lw;
//			position.setForeignCurrencyQuotation(quotation);
//			receipt.setForeignCurrencyQuotation(quotation);
//		}
		position.setFromStock(Boolean.valueOf(element.getAttributeValue("stock")));
		position.setOption(getOption(element.getAttributeValue("opt-code")));
		String order = element.getAttributeValue("order-id");
		position.setOrder(order == null || order.isEmpty() ? null : order);
		position.setOrdered(Boolean.valueOf(element.getAttributeValue("ordered")));
		position.setOtherId(null);
		position.setProduct(null);
		position.setProductGroup(getProductGroup(element.getAttributeValue("product-group-id")));
		position.setProvider("ch.eugster.colibri.provider.galileo");
		position.setProviderBooked(Boolean.valueOf(element.getAttributeValue("galileo-booked")));
		position.setProviderState(position.isProviderBooked() ? ProviderState.BOOKED : ProviderState.OPEN);
		int quantity = Integer.valueOf(element.getAttributeValue("quantity")).intValue();
		if (quantity < 0)
		{
			if (position.getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL))
			{
				quantity = Math.abs(quantity);
			}
			else if (position.getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.EXPENSES))
			{
				quantity = Math.abs(quantity);
			}
		}
		position.setQuantity(quantity);
		double price = Math.abs(Double.valueOf(element.getAttributeValue("price")).doubleValue());
		if (position.getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.EXPENSES) || position.getProductGroup().getProductGroupType().equals(ProductGroupType.WITHDRAWAL))
		{
			price = -price;
		}
		position.setPrice(price);
		position.setSearchValue(element.getAttributeValue("product-number"));
		String code = element.getAttributeValue("tax-id");
		Tax tax = getTax(code);
		CurrentTax currentTax = getCurrentTax(tax, element.getAttributeValue("current-tax-id"));
		position.setCurrentTax(currentTax);
		position.setTaxPercents(position.getCurrentTax().getPercentage());
		position.setTimestamp(receipt.getTimestamp());
		if (element.getAttributeValue("payed-invoice").equals(Boolean.TRUE.toString()))
		{
			position.setOption(Position.Option.PAYED_INVOICE);
		}
		else if (position.isOrdered())
		{
			position.setOption(Position.Option.ORDERED);
		}
		return position;
	}

	public Payment convertToPayment(Element element, Receipt receipt)
	{
		Payment payment = Payment.newInstance(receipt);
		payment.setAmount(Double.valueOf(element.getAttributeValue("amount-fc")));
		payment.setBack(Boolean.valueOf(element.getAttributeValue("back")));
		payment.setDeleted(false);
		payment.setPaymentType(getPaymentType(element.getAttributeValue("payment-type-id")));
		payment.setForeignCurrencyQuotation(Double.valueOf(element.getAttributeValue("quotation")));
		payment.setForeignCurrencyRoundFactor(Double.valueOf(element.getAttributeValue("round-factor-fc")));
		payment.setTimestamp(receipt.getTimestamp());
		payment.setBookProvider(false);
		payment.setCode(null);
		payment.setProviderBooked(false);
		payment.setProviderState(ProviderState.BOOKED);
		return payment;
	}
	
	private void updatePositionElements(Receipt receipt, Element receiptElement)
	{
		Position[] positions = receipt.getPositions().toArray(new Position[0]);
		for (Position position : positions)
		{
			boolean found = false;
			@SuppressWarnings("unchecked")
			Collection<Element> positionElements = receiptElement.getChildren("position");
			for (Element positionElement : positionElements)
			{
				if (positionElement.getAttributeValue("id").equals(position.getId().toString()))
				{
					convertToJdomElement(position, positionElement);
					found = true;
				}
			}
			if (!found)
			{
				Element positionElement = new Element("position");
				convertToJdomElement(position, positionElement);
				receiptElement.addContent(positionElement);
			}
		}
		/**
		 * remove deleted positions
		 */
		@SuppressWarnings("unchecked")
		Collection<Element> positionElements = receiptElement.getChildren("position");
		for (Element positionElement : positionElements)
		{
			boolean found = false;
			positions = receipt.getPositions().toArray(new Position[0]);
			for (Position position : positions)
			{
				if (position.getId().toString().equals(positionElement.getAttributeValue("id")))
				{
					found = true;
				}
			}
			if (!found)
			{
				positionElement.detach();
			}
		}
	}

	private void updatePaymentElements(Receipt receipt, Element receiptElement)
	{
		Payment[] payments = receipt.getPayments().toArray(new Payment[0]);
		for (Payment payment : payments)
		{
			boolean found = false;
			@SuppressWarnings("unchecked")
			Collection<Element> paymentElements = receiptElement.getChildren("payment");
			for (Element paymentElement : paymentElements)
			{
				if (paymentElement.getAttributeValue("id").equals(payment.getId().toString()))
				{
					convertToJdomElement(payment, paymentElement);
					found = true;
				}
			}
			if (!found)
			{
				Element paymentElement = new Element("payment");
				convertToJdomElement(payment, paymentElement);
				receiptElement.addContent(paymentElement);
			}
		}
		/**
		 * remove deleted payments
		 */
		@SuppressWarnings("unchecked")
		Collection<Element> paymentElements = receiptElement.getChildren("payment");
		for (Element paymentElement : paymentElements)
		{
			boolean found = false;
			payments = receipt.getPayments().toArray(new Payment[0]);
			for (Payment payment : payments)
			{
				if (payment.getId().toString().equals(paymentElement.getAttributeValue("id")))
				{
					found = true;
				}
			}
			if (!found)
			{
				paymentElement.detach();
			}
		}
	}

	private void convertToJdomElement(Position position, Element element)
	{
		element.setAttribute("id", position.getId().toString());
		element.setAttribute("receipt-id", position.getReceipt().getId().toString());
		element.setAttribute("product-group-id", position.getProductGroup().getMappingId());
		element.setAttribute("tax-id", position.getCurrentTax().getTax().getCode().toString());
		element.setAttribute("current-tax-id", position.getCurrentTax().getValidFrom().toString());
		element.setAttribute("quantity", Integer.toString(position.getQuantity()));
		element.setAttribute("price", Double.toString(position.getPrice()));
		element.setAttribute("discount", Double.toString(position.getDiscount()));
		element.setAttribute("galileo-book", Boolean.toString(position.isBookProvider()));
		element.setAttribute("galileo-booked", Boolean.toString(position.isProviderBooked()));
		element.setAttribute("opt-code", position.getOption() == null ? "" : position.getOption().toCode());
		element.setAttribute("ordered", Boolean.toString(position.isOrdered()));
		element.setAttribute("order-id", position.valueOf(position.getOrder()));
		element.setAttribute("stock", Boolean.toString(position.isFromStock()));
		Customer customer = position.getReceipt().getCustomer();
		boolean updateAccount = customer != null && customer.getHasAccount();
		element.setAttribute("update-customer-account", Boolean.toString(updateAccount));
		element.setAttribute("payed-invoice", Boolean.toString(position.getOption() == null ? false : position.getOption().equals(Option.PAYED_INVOICE)));
		String invoiceNumber = "";
		String invoiceDate = "";
		if (position.getProduct() == null)
		{
			element.setAttribute("author", "");
			element.setAttribute("title", "");
			element.setAttribute("publisher", "");
			element.setAttribute("isbn", "");
			element.setAttribute("bznr", "");
			element.setAttribute("product-number", "");
			element.setAttribute("product-id", "");
		}
		else
		{
			element.setAttribute("author", position.getProduct().getAuthor());
			element.setAttribute("title", position.getProduct().getTitle());
			element.setAttribute("publisher", position.getProduct().getPublisher());
			element.setAttribute("isbn", getIsbn(position.getProduct().getCode()));
			element.setAttribute("bznr", getBz(position.getProduct().getCode()));
			element.setAttribute("product-number", position.getProduct().getCode());
			element.setAttribute("product-id", position.getProduct().getCode());
			invoiceNumber = position.valueOf(position.getProduct().getInvoiceNumber());
			invoiceDate = position.getProduct().getInvoiceDate() == null ? "" : Long.valueOf(position.getProduct().getInvoiceDate().getTimeInMillis()).toString();
		}
		element.setAttribute("invoice", invoiceNumber);
		element.setAttribute("invoice-date", invoiceDate);
		element.setAttribute("tax", Double.toString(position.getTaxAmount(Receipt.QuotationType.REFERENCE_CURRENCY)));
		element.setAttribute("type", position.getProductGroup().compatibleState());
		element.setAttribute("amount-fc", Double.valueOf(position.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.NETTO)).toString());
		element.setAttribute("amount", Double.valueOf(position.getAmount(Receipt.QuotationType.REFERENCE_CURRENCY, Position.AmountType.NETTO)).toString());
	}

	public void convertToJdomElement(Payment payment, Element element)
	{
		element.setAttribute("id", payment.getId().toString());
		element.setAttribute("receipt-id", payment.getReceipt().getId().toString());
		element.setAttribute("payment-type-id", payment.getPaymentType().getMappingId());
		element.setAttribute("foreign-currency-id", payment.getPaymentType().getCurrency().getCode());
		element.setAttribute("quotation", Double.toString(payment.getForeignCurrencyQuotation()));
		element.setAttribute("amount", Double.toString(payment.getAmount()));
		element.setAttribute("amount-fc", Double.toString(payment.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY)));
		element.setAttribute("round-factor", Double.toString(payment.getForeignCurrencyRoundFactor()));
		element.setAttribute("round-factor-fc", Double.toString(payment.getPaymentType().getCurrency().getRoundFactor()));
		element.setAttribute("back", new Boolean(payment.isBack()).toString());
		String settlement = payment.getReceipt().getSettlement().getSettled() == null ? "" : Long.valueOf(payment.getReceipt().getSettlement().getSettled().getTimeInMillis()).toString();
		element.setAttribute("settlement", settlement);
		Salespoint salespoint = payment.getReceipt().getSettlement().getSalespoint();
		element.setAttribute("salespoint-id", salespoint.getMapping() == null ? salespoint.getHost() : salespoint.getMapping());
		boolean inputOrWithdraw = payment.getReceipt().getPositions().size() > 0 && payment.getReceipt().getPositions().iterator().next().getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL);
		element.setAttribute("is-input-or-withdraw", Boolean.toString(inputOrWithdraw));
	}
	
	private String getIsbn(String code)
	{
		BarcodeVerifier[] barcodeVerifiers = (BarcodeVerifier[]) barcodeVerifierTracker.getServices();
		for (BarcodeVerifier barcodeVerifier : barcodeVerifiers)
		{
			Barcode barcode = barcodeVerifier.verify(code);
			if (barcode.getName().toLowerCase().contains("isbn"))
			{
				return code;
			}
		}
		return "";
	}

	private String getBz(String code)
	{
		BarcodeVerifier[] barcodeVerifiers = (BarcodeVerifier[]) barcodeVerifierTracker.getServices();
		for (BarcodeVerifier barcodeVerifier : barcodeVerifiers)
		{
			Barcode barcode = barcodeVerifier.verify(code);
			if (barcode.getName().toLowerCase().contains("bz"))
			{
				return code;
			}
		}
		return "";
	}
	public enum Mode
	{
		IMPORT, EXPORT;
		
		public String label()
		{
			switch (this)
			{
			case IMPORT:
			{
				return "Import";
			}
			case EXPORT:
			{
				return "Export";
			}
			default:
			{
				throw new RuntimeException("Invalid Selection");
			}
			}
		}

		public String action()
		{
			switch (this)
			{
			case IMPORT:
			{
				return "Importieren...";
			}
			case EXPORT:
			{
				return "Exportieren...";
			}
			default:
			{
				throw new RuntimeException("Invalid Selection");
			}
			}
		}

		public Group panel()
		{
			switch (this)
			{
			case IMPORT:
			{
				return importPanel;
			}
			case EXPORT:
			{
				return exportPanel;
			}
			default:
			{
				throw new RuntimeException("Invalid Selection");
			}
			}
		}
	}
	
	private class Result
	{
		public int files = 0;
		public int read = 0;
		public int write = 0;
		public int exist = 0;
		public int error = 0;
	}
}