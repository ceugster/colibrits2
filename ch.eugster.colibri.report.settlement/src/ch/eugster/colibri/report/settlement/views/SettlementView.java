package ch.eugster.colibri.report.settlement.views;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import ch.eugster.colibri.report.daterange.views.DateView;
import ch.eugster.colibri.report.destination.views.DestinationView;
import ch.eugster.colibri.report.destination.views.DestinationView.Destination;
import ch.eugster.colibri.report.destination.views.DestinationView.Format;
import ch.eugster.colibri.report.salespoint.views.SalespointView;
import ch.eugster.colibri.report.settlement.Activator;

public class SettlementView extends ViewPart implements IViewPart, ISelectionListener, ISelectionChangedListener
{
	public static final String ID = "ch.eugster.colibri.report.settlement.view";

	private IDialogSettings settings;

	private TabFolder tabFolder;

	private Destination selectedDestination;

	private Format selectedFormat;

	private Button start;

	private ISettlementCompositeChild selectedChild;

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
			settings.getInt("selected.tab");
		}
		catch (NumberFormatException e)
		{
			settings.put("selected.tab", 0);
		}
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DestinationView.ID, this);
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		tabFolder = new TabFolder(parent, SWT.TOP);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Nach Datumsbereich");
		SettlementDateRangeComposite dateRangeComposite = new SettlementDateRangeComposite(tabFolder, SWT.NONE);
		item.setControl(dateRangeComposite);

		item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Einzelner Abschluss");
		SettlementNumberComposite numberComposite = new SettlementNumberComposite(tabFolder, SWT.NONE);
		numberComposite.addSelectionChangedListener(this);
		item.setControl(numberComposite);

		// item = new TabItem(tabFolder, SWT.NONE);
		// item.setText("Abschlüsse über Periode");
		// SettlementRangeComposite rangeComposite = new
		// SettlementRangeComposite(tabFolder, SWT.NONE);
		// item.setControl(rangeComposite);

		tabFolder.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				TabItem tabItem = (TabItem) e.item;
				if (tabItem.getControl() instanceof ISettlementCompositeChild)
				{
					selectedChild = (ISettlementCompositeChild) tabItem.getControl();

					TabFolder folder = (TabFolder) e.widget;
					TabItem[] items = folder.getItems();
					for (TabItem item : items)
					{
						if (item.getControl() == selectedChild)
						{
							selectedChild.addSelectionChangedListener(SettlementView.this);
							start.setEnabled(selectedChild.validateSelection());
							settings.put("selected.tab", folder.getSelectionIndex());
						}
						else
						{
							if (item.getControl() instanceof ISettlementCompositeChild)
							{
								((ISettlementCompositeChild) item.getControl())
										.removeSelectionChangedListener(SettlementView.this);
							}
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});

		ISelectionService service = this.getSite().getWorkbenchWindow().getSelectionService();
		Control[] controls = tabFolder.getTabList();
		for (Control control : controls)
		{
			if (control instanceof ISettlementCompositeChild)
			{
				ISettlementCompositeChild child = (ISettlementCompositeChild) control;
				service.addPostSelectionListener(DateView.ID, child);
				service.addPostSelectionListener(SalespointView.ID, child);
			}
		}

		if (tabFolder.getItemCount() > settings.getInt("selected.tab"))
		{
			item = tabFolder.getItem(settings.getInt("selected.tab"));
			selectedChild = (ISettlementCompositeChild) item.getControl();
			tabFolder.setSelection(item);
		}

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonComposite.setLayout(new GridLayout(2, false));

		Label label = new Label(buttonComposite, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		start = new Button(buttonComposite, SWT.PUSH);
		start.setText("Auswertung starten");
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
				if (selectedChild != null)
				{
					try
					{
						final JRDataSource source = selectedChild.createDataSource();
						final Hashtable<String, Object> parameters = selectedChild.getParameters();
						final JasperReport report = selectedChild.getReport();
						final String reportName = selectedChild.getReportName();

						start.setEnabled(false);
						IRunnableWithProgress op = new IRunnableWithProgress()
						{
							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException,
									InterruptedException
							{
								try
								{
									monitor.beginTask("Die Auswertung wird gedruckt...", IProgressMonitor.UNKNOWN);
									try
									{
										if (report == null)
										{
											throw new JRException("Die Berichtsvorlage " + reportName
													+ " ist im Berichtsverzeichnis nicht vorhanden.");
										}
										IStatus status = printReport(report, parameters, source);
										if (status.getSeverity() == IStatus.CANCEL)
										{
											MessageDialog.openInformation(SettlementView.this.getSite().getShell(),
													"Auswahlfehler", status.getMessage());
										}
									}
									catch (JRException e)
									{
										IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e
												.getLocalizedMessage(), e);
										ErrorDialog.openError(SettlementView.this.getSite().getShell(), "Fehler",
												"Beim Verarbeiten des Berichts ist ein Fehler aufgetreten.", status);
									}
								}
								finally
								{
									monitor.done();
								}
							}
						};
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(SettlementView.this.getSite()
								.getShell());
						dialog.run(true, true, op);
					}
					catch (InvocationTargetException ex)
					{
					}
					catch (InterruptedException ex)
					{
					}
					catch (JRException ex)
					{

					}
					finally
					{
						start.setEnabled(true);
					}
				}
			}
		});
	}

	@Override
	public void dispose()
	{
		ISelectionService service = getSite().getWorkbenchWindow().getSelectionService();

		service.removePostSelectionListener(DestinationView.ID, this);

		if (!tabFolder.isDisposed())
		{
			Control[] controls = tabFolder.getTabList();
			for (Control control : controls)
			{
				if (control instanceof ISettlementCompositeChild)
				{
					ISettlementCompositeChild child = (ISettlementCompositeChild) control;
					service.removePostSelectionListener(DateView.ID, child);
					service.removePostSelectionListener(SalespointView.ID, child);
					child.removeSelectionChangedListener(this);
				}
			}
		}
		super.dispose();
	}

	@Override
	public void setFocus()
	{
		this.start.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if (part instanceof DestinationView)
		{
			DestinationView view = (DestinationView) part;
			selectedDestination = view.getSelectedDestination();
			if (selectedDestination.equals(Destination.FILE))
			{
				selectedFormat = view.getSelectedFormat();
			}
		}
		updateStartButton();
	}

	public boolean validateSelection()
	{
		if (this.selectedDestination == null)
		{
			return false;
		}
		if (this.selectedDestination.equals(Destination.FILE))
		{
			return this.selectedFormat != null;
		}
		else
		{
			return true;
		}
	}

	private void updateStartButton()
	{
		if (this.tabFolder.getSelection().length > 0)
		{
			TabItem tabItem = this.tabFolder.getSelection()[0];
			if (tabItem.getControl() instanceof ISettlementCompositeChild)
			{
				ISettlementCompositeChild child = (ISettlementCompositeChild) tabItem.getControl();
				this.start.setEnabled(child.validateSelection() && this.validateSelection());
			}
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		start.setEnabled(!event.getSelection().isEmpty());
	}

//	private void prepareReport()
//	{
//		JRDataSource source = selectedChild.createDataSource();
//		Hashtable<String, Object> parameters = selectedChild.getParameters();
//		try
//		{
//			JasperReport report = selectedChild.getReport();
//			if (report == null)
//			{
//				throw new JRException("Die Berichtsvorlage " + selectedChild.getReportName()
//						+ " ist im Berichtsverzeichnis nicht vorhanden.");
//			}
//			IStatus status = printReport(report, parameters, source);
//			if (status.getSeverity() == IStatus.CANCEL)
//			{
//				MessageDialog.openInformation(this.getSite().getShell(), "Auswahlfehler", status.getMessage());
//			}
//		}
//		catch (JRException e)
//		{
//			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e);
//			ErrorDialog.openError(this.getSite().getShell(), "Fehler",
//					"Beim Verarbeiten des Berichts ist ein Fehler aufgetreten.", status);
//		}
//	}

	protected IStatus printReport(JasperReport report, Hashtable<String, Object> parameters, final JRDataSource source)
			throws JRException
	{
		if (this.selectedDestination == null)
		{
			return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Sie haben kein Ausgabeziel ausgewählt.");
		}
		if (selectedDestination.equals(Destination.FILE))
		{
			if (this.selectedFormat == null)
			{
				return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Sie haben kein Dateiformat ausgewählt.");
			}
		}

		JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, source);

		switch (selectedDestination)
		{
			case FILE:
			{
				switch (selectedFormat)
				{
					case HTML:
					{
						JasperExportManager.exportReportToHtmlFile(jasperPrint, "C:\file.html");
						break;
					}
					case PDF:
					{
						JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\file.pdf");
						break;
					}
					case XML:
					{
						JasperExportManager.exportReportToXmlFile(jasperPrint, "C:\file.xml", false);
						break;
					}
					default:
					{
						MessageDialog.openInformation(this.getSite().getShell(), "Fehler",
								"Das gewählte Format wird zur Zeit nicht unterstützt.");
					}
				}
			}
			case PRINTER:
			{
				JasperPrintManager.printReport(jasperPrint, true);
				break;
			}
			case RECEIPT_PRINTER:
			{
			}
			case SCREEN:
			{
				JasperViewer.viewReport(jasperPrint, false);
				break;
			}
			default:
			{
				return new Status(IStatus.CANCEL, Activator.PLUGIN_ID,
						"Das ausgewählte Ausgabeziel ist nicht verfügbar.");
			}
		}
		return Status.OK_STATUS;
	}

}
