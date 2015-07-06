package ch.eugster.colibri.report.productgroup.views;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupEntry;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderIdService;
import ch.eugster.colibri.report.daterange.views.DateView;
import ch.eugster.colibri.report.destination.views.DestinationView;
import ch.eugster.colibri.report.engine.ReportService;
import ch.eugster.colibri.report.productgroup.Activator;
import ch.eugster.colibri.report.salespoint.views.SalespointView;

public class ProductGroupView extends ViewPart implements IViewPart, ISelectionListener, ISelectionChangedListener
{
	public static final String ID = "ch.eugster.colibri.report.productgroup.view";

	private ComboViewer typeViewer;
	
	private Button compareWithPreviousYear;

	private ComboViewer providerViewer;

	private Button printNotSalesToo;

	private Button printExpensesToo;

	private ComboViewer sortViewer;
	
	private IDialogSettings settings;

	private Button start;

	private ReportService.Destination selectedDestination;

	private ReportService.Format selectedFormat;
	
	private String[][] providers;
	
	private String selectedProviderId;
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);

		settings = Activator.getDefault().getDialogSettings().getSection(ProductGroupView.class.getName());
		if (settings == null)
		{
			settings = Activator.getDefault().getDialogSettings().addNewSection(ProductGroupView.class.getName());
		}
		try
		{
			settings.getInt("selected.type");
		}
		catch(NumberFormatException e)
		{
			settings.put("selected.type", Type.TOTAL.ordinal());
		}
		providers = getProviders();
		selectedProviderId = settings.get("provider.only.selection");
		if (providers != null && providers.length > 0)
		{
			if (selectedProviderId == null || selectedProviderId.isEmpty())
			{
				selectedProviderId = providers[0][1];
			}
		}
		try
		{
			settings.getInt("sort");
		}
		catch(NumberFormatException e)
		{
			settings.put("sort", 0);
		}
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("Optionen"); //$NON-NLS-1$

		Label label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Auswertung bezogen auf");

		Combo combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData());
		
		typeViewer = new ComboViewer(combo);
		typeViewer.setContentProvider(new ArrayContentProvider());
		typeViewer.setLabelProvider(new LabelProvider() 
		{
			@Override
			public String getText(Object element) 
			{
				return ((Type) element).label();
			}
		});
		typeViewer.setInput(Type.values());
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() 
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{
				IStructuredSelection ssel = (IStructuredSelection) typeViewer.getSelection();
				Type type = (Type) ssel.getFirstElement();
				settings.put("selected.type", type.ordinal());
				compareWithPreviousYear.setEnabled(!type.equals(Type.STOCK_ORDER));
			}
		});
		
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		
		compareWithPreviousYear = new Button(group, SWT.CHECK);
		compareWithPreviousYear.setLayoutData(gridData);
		compareWithPreviousYear.setText("Mit Vorjahresvergleich");
		compareWithPreviousYear.setSelection(settings.getBoolean("compare.with.previous.year"));
		compareWithPreviousYear.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				settings.put("compare.with.previous.year", compareWithPreviousYear.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		typeViewer.setSelection(new StructuredSelection( new Type[] { Type.values()[settings.getInt("selected.type")] }));

		if (providers != null && providers.length > 0)
		{
			label = new Label(group, SWT.None);
			label.setLayoutData(new GridData());
			label.setText("Nur Warengruppen von");
	
			combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
			combo.setLayoutData(new GridData());
			
			providerViewer = new ComboViewer(combo);
			providerViewer.setContentProvider(new ArrayContentProvider());
			providerViewer.setLabelProvider(new LabelProvider() 
			{
				@Override
				public String getText(Object element) 
				{
					String[] values = (String[]) element;
					return values[0];
				}
			});
			providerViewer.setInput(providers);
			
			String[] selectedProvider = null;
			for (String[] provider : providers)
			{
				if (provider[1].equals(selectedProviderId))
				{
					selectedProvider = provider;
				}
			}
			providerViewer.setSelection(new StructuredSelection( new String[][] { selectedProvider }));
			providerViewer.addSelectionChangedListener(new ISelectionChangedListener() 
			{
				@Override
				public void selectionChanged(SelectionChangedEvent event) 
				{
					IStructuredSelection ssel = (IStructuredSelection) typeViewer.getSelection();
					settings.put("provider.only.selection", ((String[]) ssel.getFirstElement())[1]);
				}
			});
		}
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		
		printNotSalesToo = new Button(group, SWT.CHECK);
		printNotSalesToo.setText("Umsatzneutrale Warengruppen berücksichtigen");
		printNotSalesToo.setLayoutData(gridData);
		printNotSalesToo.setSelection(settings.getBoolean("print.not.sales.too"));
		printNotSalesToo.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				settings.put("print.not.sales.too", ((Button) e.widget).getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});

		gridData = new GridData();
		gridData.horizontalSpan = 2;
		
		printExpensesToo = new Button(group, SWT.CHECK);
		printExpensesToo.setText("Ausgaben berücksichtigen");
		printExpensesToo.setLayoutData(gridData);
		printExpensesToo.setSelection(settings.getBoolean("print.expenses.too"));
		printExpensesToo.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				settings.put("print.expenses.too", ((Button) e.widget).getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Sortierung");

		combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData());
		
		sortViewer = new ComboViewer(combo);
		sortViewer.setContentProvider(new ArrayContentProvider());
		sortViewer.setLabelProvider(new LabelProvider() 
		{
			@Override
			public String getText(Object element) 
			{
				return ((String[]) element)[0];
			}
		});
		
		String[][] sorts = null;
		if (providers != null && providers.length > 0)
		{
			IStructuredSelection ssel = (IStructuredSelection) providerViewer.getSelection();
			String[] selectedProvider = (String[]) ssel.getFirstElement();
			sorts = new String[][] {{ "Warengruppen", "product.group" }, {selectedProvider[0], selectedProvider[1] }, { "Umsatz", "sales" }};
		}
		else
		{
			sorts = new String[][] {{ "Warengruppen", "product.group" }, { "Code", "code" }, { "Umsatz", "sales" }};
		}
		sortViewer.setInput(sorts);
		sortViewer.setSelection(new StructuredSelection(new String[][] { sorts[settings.getInt("sort")] }));
		sortViewer.addSelectionChangedListener(new ISelectionChangedListener() 
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{
				IStructuredSelection ssel = (IStructuredSelection) typeViewer.getSelection();
				settings.put("selected.type", ((Type) ssel.getFirstElement()).ordinal());
			}
		});

		if (providers != null && providers.length > 0)
		{
			providerViewer.addSelectionChangedListener(new ISelectionChangedListener() 
			{
				@Override
				public void selectionChanged(SelectionChangedEvent event) 
				{
					IStructuredSelection ssel = (IStructuredSelection) providerViewer.getSelection();
					String[] selectedProvider = (String[]) ssel.getFirstElement();
					String[][] sorts = new String[][] {{ selectedProvider[0], "Umsatz" }, { selectedProvider[1], "sales"}};
					sortViewer.setInput(sorts);
					sortViewer.setSelection(new StructuredSelection(new String[][] { sorts[settings.getInt("sort")] }));
				}
			});
		}
		
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		buttonComposite.setLayout(new GridLayout(2, false));

		label = new Label(buttonComposite, SWT.NONE);
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
				final JRDataSource dataSource = createDataSource();
				if (dataSource == null)
				{
					MessageDialog.openInformation(null, "Keine Daten vorhanden", "Für die gewählte Selektion sind keine Daten vorhanden.");
					return;
				}
				selectedDestination = getSelectedDestination();
				selectedFormat = getSelectedFormat();
				IStructuredSelection ssel =(StructuredSelection) typeViewer.getSelection();
				final Type selectedType = ((Type) ssel.getFirstElement());
				final boolean compare = compareWithPreviousYear.getSelection();
				final Hashtable<String, Object> parameters = getParameters();
				final URL url = Activator.getDefault().getBundle().getEntry("reports/" + selectedType.report(compare) + ".jrxml");
				UIJob job = new UIJob("Auswertung wird aufbereitet...")
				{
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						try
						{
//								start.setEnabled(false);
							monitor.beginTask("Auswertung wird aufbereitet. Bitte warten Sie...", IProgressMonitor.UNKNOWN);
							final InputStream report = url.openStream();
							printReport(new SubProgressMonitor(monitor, 1), report, dataSource, parameters);
						}
						catch (IOException e1)
						{
						}
						finally
						{
							monitor.done();
//								start.setEnabled(true);
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();
			}
		});
		
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(SalespointView.ID, this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DateView.ID, this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DestinationView.ID, this);

		updateStartButton();
	}

	@Override
	public void dispose()
	{
		ISelectionService service = getSite().getWorkbenchWindow().getSelectionService();
		service.removePostSelectionListener(SalespointView.ID, this);
		service.removePostSelectionListener(DateView.ID, this);
		service.removePostSelectionListener(DestinationView.ID, this);
		super.dispose();
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
	
	public ReportService.Destination getSelectedDestination()
	{
		IViewReference[] references = this.getSite().getPage().getViewReferences();
		for (IViewReference reference : references)
		{
			IViewPart part = reference.getView(true);
			if (part instanceof DestinationView)
			{
				DestinationView view = (DestinationView) part;
				return view.getSelectedDestination();
			}
		}
		return null;
	}

	public ReportService.Format getSelectedFormat()
	{
		IViewReference[] references = this.getSite().getPage().getViewReferences();
		for (IViewReference reference : references)
		{
			IViewPart part = reference.getView(true);
			if (part instanceof DestinationView)
			{
				DestinationView view = (DestinationView) part;
				return view.getSelectedFormat();
			}
		}
		return null;
	}

	private String[][] getProviders()
	{
		ServiceTracker<ProviderIdService, ProviderIdService> tracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle().getBundleContext(), ProviderIdService.class, null);
		tracker.open();
		ServiceReference<ProviderIdService>[] references = tracker.getServiceReferences();
		if (references != null && references.length > 0)
		{
			providers = new String[references.length][];
			for (int i = 0; i < references.length; i++)
			{
				String[] provider = new String[2];
				ProviderIdService service = tracker.getService(references[i]);
				provider[0] = service.getProviderLabel();
				provider[1] = service.getProviderId();
				providers[i] = provider;
			}
		}
		return providers;
	}

	public JRDataSource createDataSource()
	{
		JRDataSource dataSource = null;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = (PersistenceService) tracker.getService();
			if (service != null)
			{
				IStructuredSelection ssel = (IStructuredSelection) typeViewer.getSelection();
				Object object = ssel.getFirstElement();
				if (object instanceof Type)
				{
					String provider = null;
					if (providerViewer != null)
					{
						ssel = (IStructuredSelection) providerViewer.getSelection();
						provider = ((String[]) ssel.getFirstElement())[1];
					}
					
					List<ProductGroupEntry> dataSourceList = new ArrayList<ProductGroupEntry>();
					Type type = (Type) object;
					switch (type)
					{
					case TOTAL:
					{
						Calendar[] dateRange = getSelectedDateRange();
						PositionQuery query = (PositionQuery) service.getServerService().getQuery(Position.class);
						Map<String, Map<Long, ProductGroupEntry>> entries = query.selectProductGroupStatistics(getSelectedSalespoints(), dateRange, provider, printExpensesToo.getSelection(), printNotSalesToo.getSelection(), compareWithPreviousYear.getSelection());
						if (this.compareWithPreviousYear.getSelection())
						{
							dateRange[0].add(Calendar.YEAR, -1);
							dateRange[1].add(Calendar.YEAR, -1);
							Map<String, Map<Long, ProductGroupEntry>> previousYearEntries = query.selectProductGroupStatistics(getSelectedSalespoints(), dateRange, provider, printExpensesToo.getSelection(), printNotSalesToo.getSelection(), compareWithPreviousYear.getSelection());
							mergePreviousYearEntries(entries, previousYearEntries);
						}
						double totalStockAmount = getTotalStockAmount(entries);
						double totalOrderAmount = getTotalOrderAmount(entries);
						double totalTotalAmount = getTotalTotalAmount(entries);
						for (Map<Long, ProductGroupEntry> productGroups : entries.values())
						{
							for (ProductGroupEntry entry : productGroups.values())
							{
								entry.setProviderId(getProviderId(entry, service));
								entry.setStockQuantityPreviousYear(entry.getStockQuantityPreviousYear());
								entry.setOrderQuantityPreviousYear(entry.getOrderQuantityPreviousYear());
								entry.setTotalQuantityPreviousYear(entry.getTotalQuantityPreviousYear());
								entry.setStockAmountPreviousYear(entry.getStockAmountPreviousYear());
								entry.setOrderAmountPreviousYear(entry.getOrderAmountPreviousYear());
								entry.setTotalAmountPreviousYear(entry.getTotalAmountPreviousYear());
								Double value = Double.valueOf(entry.getOrderAmountPreviousYear()/ entry.getOrderAmount());
								entry.setOrderChangePercent(value.isNaN() ? Double.valueOf(0D) : value);
								value = Double.valueOf(entry.getStockAmountPreviousYear()/entry.getStockAmount());
								entry.setStockChangePercent(value.isNaN() ? Double.valueOf(0D) : value);
								entry.setTotalChangePercent(entry.getTotalChangePercent());
								entry.setOrderAmount(entry.getOrderAmount());
								entry.setOrderAmountPreviousYear(entry.getOrderAmountPreviousYear());
								entry.setOrderChangePercent(entry.getOrderChangePercent());
								entry.setOrderProportion(entry.getOrderProportion());
								entry.setOrderProportionGroup(entry.getOrderProportionGroup());
								entry.setOrderQuantity(entry.getOrderQuantity());
								entry.setOrderQuantityPreviousYear(entry.getOrderQuantityPreviousYear());
								entry.setStockAmount(entry.getStockAmount());
								entry.setStockAmountPreviousYear(entry.getStockAmountPreviousYear());
								entry.setStockChangePercent(entry.getStockChangePercent());
								entry.setStockProportion(entry.getStockProportion());
								entry.setStockProportionGroup(entry.getStockProportionGroup());
								entry.setStockQuantity(entry.getStockQuantity());
								entry.setStockQuantityPreviousYear(entry.getStockQuantityPreviousYear());
								entry.setTotalAmount(entry.getTotalAmount());
								entry.setTotalAmountPreviousYear(entry.getTotalAmountPreviousYear());
								entry.setTotalChangePercent(entry.getTotalChangePercent());
								value = Double.valueOf(entry.getTotalAmount()/totalTotalAmount);
								entry.setTotalProportion(value.isNaN() ? Double.valueOf(0D) : value);
								entry.setTotalQuantity(entry.getTotalQuantity());
								entry.setTotalQuantityPreviousYear(entry.getTotalQuantityPreviousYear());
								value = Double.valueOf(entry.getStockAmount()/totalStockAmount);
								entry.setStockProportionGroup(value.isNaN() ? Double.valueOf(0D) : value);
								value = Double.valueOf(entry.getStockAmount()/totalTotalAmount);
								entry.setStockProportion(value.isNaN() ? Double.valueOf(0D) : value);
								value = Double.valueOf(entry.getOrderAmount()/totalOrderAmount);
								entry.setOrderProportionGroup(value.isNaN() ? Double.valueOf(0D) : value);
								value = Double.valueOf(entry.getOrderAmount()/totalTotalAmount);
								entry.setOrderProportion(value.isNaN() ? Double.valueOf(0D) : value);
								value = Double.valueOf((entry.getOrderAmount() + entry.getStockAmount())/(entry.getOrderQuantity() + entry.getStockQuantity()));
								entry.setSectionPerItem(value.isNaN() ? Double.valueOf(0D) : value);
								dataSourceList.add(entry);
							}
						}
						String order = "sales";
						if (sortViewer != null)
						{
							ssel = (IStructuredSelection) sortViewer.getSelection();
							order = ((String[]) ssel.getFirstElement())[1];
						}
						if (order.equals("sales"))
						{
							Collections.sort(dataSourceList, new Comparator<ProductGroupEntry>() 
							{
								@Override
								public int compare(ProductGroupEntry entry1, ProductGroupEntry entry2) 
								{
									return Double.valueOf(entry2.getTotalAmount() - entry1.getTotalAmount()).intValue();
								}
							});
						}
						else if (order.equals("product.group"))
						{
							Collections.sort(dataSourceList, new Comparator<ProductGroupEntry>() 
							{
								@Override
								public int compare(ProductGroupEntry entry1, ProductGroupEntry entry2) 
								{
									return entry1.getProductGroupName().compareTo(entry2.getProductGroupName());
								}
							});
						}
						else
						{
							Collections.sort(dataSourceList, new Comparator<ProductGroupEntry>() 
							{
								@Override
								public int compare(ProductGroupEntry entry1, ProductGroupEntry entry2) 
								{
									if (entry1.getProviderId().isEmpty() || entry2.getProviderId().isEmpty())
									{
										return entry2.getProviderId().compareTo(entry1.getProviderId());
									}
									else
									{
										return entry1.getProviderId().compareTo(entry2.getProviderId());
									}
								}
							});
						}
						break;
					}
					case PRODUCT_GROUP:
					{
						Calendar[] dateRange = getSelectedDateRange();
						PositionQuery query = (PositionQuery) service.getServerService().getQuery(Position.class);
						Map<String, Map<Long, ProductGroupEntry>> entries = query.selectProductGroupStatistics(getSelectedSalespoints(), dateRange, provider, printExpensesToo.getSelection(), printNotSalesToo.getSelection(), compareWithPreviousYear.getSelection());
						if (this.compareWithPreviousYear.getSelection())
						{
							dateRange[0].add(Calendar.YEAR, -1);
							dateRange[1].add(Calendar.YEAR, -1);
							Map<String, Map<Long, ProductGroupEntry>> previousYearEntries = query.selectProductGroupStatistics(getSelectedSalespoints(), dateRange, provider, printExpensesToo.getSelection(), printNotSalesToo.getSelection(), compareWithPreviousYear.getSelection());
							mergePreviousYearEntries(entries, previousYearEntries);
						}
						double totalTotalAmount = getTotalTotalAmount(entries);
						for (Map<Long, ProductGroupEntry> productGroups : entries.values())
						{
							for (ProductGroupEntry entry : productGroups.values())
							{
								entry.setProviderId(getProviderId(entry, service));
								entry.setStockQuantityPreviousYear(entry.getStockQuantityPreviousYear());
								entry.setOrderQuantityPreviousYear(entry.getOrderQuantityPreviousYear());
								entry.setTotalQuantityPreviousYear(entry.getTotalQuantityPreviousYear());
								entry.setStockAmountPreviousYear(entry.getStockAmountPreviousYear());
								entry.setOrderAmountPreviousYear(entry.getOrderAmountPreviousYear());
								entry.setTotalAmountPreviousYear(entry.getTotalAmountPreviousYear());
								Double value = Double.valueOf(entry.getOrderAmountPreviousYear()/ entry.getOrderAmount());
								entry.setOrderChangePercent(value.isNaN() ? Double.valueOf(0D) : value);
								value = Double.valueOf(entry.getStockAmountPreviousYear()/entry.getStockAmount());
								entry.setStockChangePercent(value.isNaN() ? Double.valueOf(0D) : value);
								entry.setTotalChangePercent(entry.getTotalChangePercent());
								entry.setOrderAmount(entry.getOrderAmount());
								entry.setOrderAmountPreviousYear(entry.getOrderAmountPreviousYear());
								entry.setOrderChangePercent(entry.getOrderChangePercent());
								entry.setOrderProportion(entry.getOrderProportion());
								entry.setOrderProportionGroup(entry.getOrderProportionGroup());
								entry.setOrderQuantity(entry.getOrderQuantity());
								entry.setOrderQuantityPreviousYear(entry.getOrderQuantityPreviousYear());
								entry.setStockAmount(entry.getStockAmount());
								entry.setStockAmountPreviousYear(entry.getStockAmountPreviousYear());
								entry.setStockChangePercent(entry.getStockChangePercent());
								entry.setStockProportion(entry.getStockProportion());
								entry.setStockProportionGroup(entry.getStockProportionGroup());
								entry.setStockQuantity(entry.getStockQuantity());
								entry.setStockQuantityPreviousYear(entry.getStockQuantityPreviousYear());
								entry.setTotalAmount(entry.getTotalAmount());
								entry.setTotalAmountPreviousYear(entry.getTotalAmountPreviousYear());
								entry.setTotalChangePercent(entry.getTotalChangePercent());
								value = Double.valueOf(entry.getTotalAmount()/totalTotalAmount);
								entry.setTotalProportion(value.isNaN() ? Double.valueOf(0D) : value);
								entry.setTotalQuantity(entry.getTotalQuantity());
								entry.setTotalQuantityPreviousYear(entry.getTotalQuantityPreviousYear());
								value = Double.valueOf(entry.getStockAmount()/entry.getTotalAmount());
								entry.setStockProportionGroup(value.isNaN() ? Double.valueOf(0D) : value);
								entry.setStockProportion(null);
								value = Double.valueOf(entry.getOrderAmount()/entry.getTotalAmount());
								entry.setOrderProportionGroup(value.isNaN() ? Double.valueOf(0D) : value);
								entry.setOrderProportion(null);
								value = Double.valueOf((entry.getOrderAmount() + entry.getStockAmount())/(entry.getOrderQuantity() + entry.getStockQuantity()));
								entry.setSectionPerItem(value.isNaN() ? Double.valueOf(0D) : value);
								dataSourceList.add(entry);
							}
						}
						String order = "sales";
						if (sortViewer != null)
						{
							ssel = (IStructuredSelection) sortViewer.getSelection();
							order = ((String[]) ssel.getFirstElement())[1];
						}
						if (order.equals("sales"))
						{
							Collections.sort(dataSourceList, new Comparator<ProductGroupEntry>() 
							{
								@Override
								public int compare(ProductGroupEntry entry1, ProductGroupEntry entry2) 
								{
									return Double.valueOf(entry2.getTotalAmount() - entry1.getTotalAmount()).intValue();
								}
							});
						}
						else if (order.equals("product.group"))
						{
							Collections.sort(dataSourceList, new Comparator<ProductGroupEntry>() 
							{
								@Override
								public int compare(ProductGroupEntry entry1, ProductGroupEntry entry2) 
								{
									return entry1.getProductGroupName().compareTo(entry2.getProductGroupName());
								}
							});
						}
						else
						{
							Collections.sort(dataSourceList, new Comparator<ProductGroupEntry>() 
							{
								@Override
								public int compare(ProductGroupEntry entry1, ProductGroupEntry entry2) 
								{
									if (entry1.getProviderId().isEmpty() || entry2.getProviderId().isEmpty())
									{
										return entry2.getProviderId().compareTo(entry1.getProviderId());
									}
									else
									{
										return entry1.getProviderId().compareTo(entry2.getProviderId());
									}
								}
							});
						}
						break;
					}
					case STOCK_ORDER:
					{
						Calendar[] dateRange = getSelectedDateRange();
						PositionQuery query = (PositionQuery) service.getServerService().getQuery(Position.class);
						Map<Long, Map<String, Map<Long, ProductGroupEntry>>> entries = query.selectProductGroupStatisticsBySalespoint(getSelectedSalespoints(), dateRange, provider, printExpensesToo.getSelection(), printNotSalesToo.getSelection(), compareWithPreviousYear.getSelection());
						if (this.compareWithPreviousYear.getSelection())
						{
							dateRange[0].add(Calendar.YEAR, -1);
							dateRange[1].add(Calendar.YEAR, -1);
							Map<Long, Map<String, Map<Long, ProductGroupEntry>>> previousYearEntries = query.selectProductGroupStatisticsBySalespoint(getSelectedSalespoints(), dateRange, provider, printExpensesToo.getSelection(), printNotSalesToo.getSelection(), compareWithPreviousYear.getSelection());
							mergePreviousYearEntriesBySalespoint(entries, previousYearEntries);
						}
						for (Map<String, Map<Long, ProductGroupEntry>> providers : entries.values())
						{
							for (Map<Long, ProductGroupEntry> productGroups : providers.values())
							{
								for (ProductGroupEntry entry : productGroups.values())
								{
									entry.setProviderId(getProviderId(entry, service));
									entry.setStockQuantityPreviousYear(entry.getStockQuantityPreviousYear());
									entry.setOrderQuantityPreviousYear(entry.getOrderQuantityPreviousYear());
									entry.setTotalQuantityPreviousYear(entry.getTotalQuantityPreviousYear());
									entry.setStockAmountPreviousYear(entry.getStockAmountPreviousYear());
									entry.setOrderAmountPreviousYear(entry.getOrderAmountPreviousYear());
									entry.setTotalAmountPreviousYear(entry.getTotalAmountPreviousYear());
									Double value = Double.valueOf(entry.getOrderAmountPreviousYear()/ entry.getOrderAmount());
									entry.setOrderChangePercent(value.isNaN() ? Double.valueOf(0D) : value);
									value = Double.valueOf(entry.getStockAmountPreviousYear()/entry.getStockAmount());
									entry.setStockChangePercent(value.isNaN() ? Double.valueOf(0D) : value);
									entry.setTotalChangePercent(entry.getTotalChangePercent());
									entry.setOrderAmount(entry.getOrderAmount());
									entry.setOrderAmountPreviousYear(entry.getOrderAmountPreviousYear());
									entry.setOrderChangePercent(entry.getOrderChangePercent());
									entry.setOrderProportion(entry.getOrderProportion());
									entry.setOrderProportionGroup(entry.getOrderProportionGroup());
									entry.setOrderQuantity(entry.getOrderQuantity());
									entry.setOrderQuantityPreviousYear(entry.getOrderQuantityPreviousYear());
									entry.setStockAmount(entry.getStockAmount());
									entry.setStockAmountPreviousYear(entry.getStockAmountPreviousYear());
									entry.setStockChangePercent(entry.getStockChangePercent());
									entry.setStockProportion(entry.getStockProportion());
									entry.setStockProportionGroup(entry.getStockProportionGroup());
									entry.setStockQuantity(entry.getStockQuantity());
									entry.setStockQuantityPreviousYear(entry.getStockQuantityPreviousYear());
									entry.setTotalAmount(entry.getTotalAmount());
									entry.setTotalAmountPreviousYear(entry.getTotalAmountPreviousYear());
									entry.setTotalChangePercent(entry.getTotalChangePercent());
									entry.setTotalProportion(value.isNaN() ? Double.valueOf(0D) : value);
									entry.setTotalQuantity(entry.getTotalQuantity());
									entry.setTotalQuantityPreviousYear(entry.getTotalQuantityPreviousYear());
									value = Double.valueOf(entry.getStockAmount()/entry.getTotalAmount());
									entry.setStockProportionGroup(value.isNaN() ? Double.valueOf(0D) : value);
									entry.setStockProportion(null);
									value = Double.valueOf(entry.getOrderAmount()/entry.getTotalAmount());
									entry.setOrderProportionGroup(value.isNaN() ? Double.valueOf(0D) : value);
									entry.setOrderProportion(null);
									value = Double.valueOf((entry.getOrderAmount() + entry.getStockAmount())/(entry.getOrderQuantity() + entry.getStockQuantity()));
									entry.setSectionPerItem(value.isNaN() ? Double.valueOf(0D) : value);
									dataSourceList.add(entry);
								}
							}
						}
						String order = "sales";
						if (sortViewer != null)
						{
							ssel = (IStructuredSelection) sortViewer.getSelection();
							order = ((String[]) ssel.getFirstElement())[1];
						}
						if (order.equals("sales"))
						{
							Collections.sort(dataSourceList, new Comparator<ProductGroupEntry>() 
							{
								@Override
								public int compare(ProductGroupEntry entry1, ProductGroupEntry entry2) 
								{
									int compare = entry1.getSalespointId().compareTo(entry2.getSalespointId());
									if (compare == 0)
									{
										compare = entry1.getSalespointName().compareTo(entry2.getSalespointName());
										if (compare == 0)
										{
											compare = Double.valueOf(entry2.getTotalAmount() - entry1.getTotalAmount()).intValue();
										}
									}
									return compare;
								}
							});
						}
						else if (order.equals("product.group"))
						{
							Collections.sort(dataSourceList, new Comparator<ProductGroupEntry>() 
							{
								@Override
								public int compare(ProductGroupEntry entry1, ProductGroupEntry entry2) 
								{
									int compare = entry1.getSalespointId().compareTo(entry2.getSalespointId());
									if (compare == 0)
									{
										compare = entry1.getSalespointName().compareTo(entry2.getSalespointName());
										if (compare == 0)
										{
											compare = entry1.getProductGroupName().compareTo(entry2.getProductGroupName());
										}
									}
									return compare;
								}
							});
						}
						else
						{
							Collections.sort(dataSourceList, new Comparator<ProductGroupEntry>() 
							{
								@Override
								public int compare(ProductGroupEntry entry1, ProductGroupEntry entry2) 
								{
									int compare = entry1.getSalespointId().compareTo(entry2.getSalespointId());
									if (compare == 0)
									{
										compare = entry1.getSalespointName().compareTo(entry2.getSalespointName());
										if (compare == 0)
										{
											if (entry1.getProviderId().isEmpty() || entry2.getProviderId().isEmpty())
											{
												compare = entry2.getProviderId().compareTo(entry1.getProviderId());
											}
											else
											{
												compare = entry1.getProviderId().compareTo(entry2.getProviderId());
											}
										}
									}
									return compare;
								}
							});
						}
						break;
					}
					}
					dataSource = new JRMapArrayDataSource(dataSourceList.toArray(new ProductGroupEntry[0]));
				}
			}
		}
		finally
		{
			tracker.close();
		}

		return dataSource;
	}

	private String getProviderId(ProductGroupEntry entry, PersistenceService service)
	{
		if (entry.getProviderId() != null)
		{
			ProductGroup productGroup = (ProductGroup) service.getServerService().find(ProductGroup.class, entry.getProductGroupId());
			List<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(entry.getProviderId());
			if (mappings.isEmpty())
			{
				return "";
			}
			else
			{
				return mappings.iterator().next().getExternalProductGroup().getCode();
			}
		}
		return "";
	}

	private void mergePreviousYearEntries(Map<String, Map<Long, ProductGroupEntry>> entries, Map<String, Map<Long, ProductGroupEntry>> previousYearEntries)
	{
		Set<String> providerIds = previousYearEntries.keySet();
		for (String providerId : providerIds)
		{
			Map<Long, ProductGroupEntry> previousYearProductGroups = previousYearEntries.get(providerId);
			Set<Long> productGroupIds = previousYearProductGroups.keySet();
			for (Long productGroupId : productGroupIds)
			{
				ProductGroupEntry previousYearEntry = previousYearProductGroups.get(productGroupId); 
				Map<Long, ProductGroupEntry> productGroups = entries.get(previousYearEntry.getProviderId());
				if (productGroups == null)
				{
					productGroups = new HashMap<Long, ProductGroupEntry>();
					entries.put(providerId, productGroups);
				}
				ProductGroupEntry entry = productGroups.get(previousYearEntry.getProductGroupId());
				if (entry == null)
				{
					entry = new ProductGroupEntry();
					entry.setProductGroupId(previousYearEntry.getProductGroupId());
					entry.setProductGroupName(previousYearEntry.getProductGroupName());
					entry.setProviderId(previousYearEntry.getProviderId());
					productGroups.put(productGroupId, entry);
				}
				entry.setStockQuantityPreviousYear(previousYearEntry.getStockQuantity());
				entry.setOrderQuantityPreviousYear(previousYearEntry.getOrderQuantity());
				entry.setTotalQuantityPreviousYear(previousYearEntry.getTotalQuantity());
				entry.setStockAmountPreviousYear(previousYearEntry.getStockAmount());
				entry.setOrderAmountPreviousYear(previousYearEntry.getOrderAmount());
				entry.setTotalAmountPreviousYear(previousYearEntry.getTotalAmount());
				entry.setOrderChangePercent(previousYearEntry.getOrderAmountPreviousYear()/ previousYearEntry.getOrderAmount());
				entry.setStockChangePercent(previousYearEntry.getStockAmountPreviousYear()/previousYearEntry.getStockAmount());
				entry.setTotalChangePercent(previousYearEntry.getTotalAmountPreviousYear()/previousYearEntry.getTotalAmount());
			}
		}
	}
	
	private void mergePreviousYearEntriesBySalespoint(Map<Long, Map<String, Map<Long, ProductGroupEntry>>> entries, Map<Long, Map<String, Map<Long, ProductGroupEntry>>> previousYearEntries)
	{
		Set<Long> salespointIds = previousYearEntries.keySet();
		for (Long salespointId : salespointIds)
		{
			Map<String, Map<Long, ProductGroupEntry>> previousYearProviders = previousYearEntries.get(salespointId);
			Set<String> providerIds = previousYearProviders.keySet();
			for (String providerId : providerIds)
			{
				Map<Long, ProductGroupEntry> previousYearProductGroups = previousYearProviders.get(providerId);
				Set<Long> productGroupIds = previousYearProductGroups.keySet();
				for (Long productGroupId : productGroupIds)
				{
					ProductGroupEntry previousYearEntry = previousYearProductGroups.get(productGroupId);
					Map<String, Map<Long, ProductGroupEntry>> providers = entries.get(salespointId);
					if (providers == null)
					{
						providers = new HashMap<String, Map<Long, ProductGroupEntry>>();
						entries.put(salespointId, providers);
					}
					Map<Long, ProductGroupEntry> productGroups = providers.get(previousYearEntry.getProviderId());
					if (productGroups == null)
					{
						productGroups = new HashMap<Long, ProductGroupEntry>();
						providers.put(providerId, productGroups);
					}
					ProductGroupEntry entry = productGroups.get(previousYearEntry.getProductGroupId());
					if (entry == null)
					{
						entry = new ProductGroupEntry();
						entry.setSalespointId(previousYearEntry.getSalespointId());
						entry.setSalespointName(previousYearEntry.getSalespointName());
						entry.setProductGroupId(previousYearEntry.getProductGroupId());
						entry.setProductGroupName(previousYearEntry.getProductGroupName());
						entry.setProviderId(previousYearEntry.getProviderId());
						productGroups.put(productGroupId, entry);
					}
					entry.setStockQuantityPreviousYear(previousYearEntry.getStockQuantity());
					entry.setOrderQuantityPreviousYear(previousYearEntry.getOrderQuantity());
					entry.setTotalQuantityPreviousYear(previousYearEntry.getTotalQuantity());
					entry.setStockAmountPreviousYear(previousYearEntry.getStockAmount());
					entry.setOrderAmountPreviousYear(previousYearEntry.getOrderAmount());
					entry.setTotalAmountPreviousYear(previousYearEntry.getTotalAmount());
					entry.setOrderChangePercent(previousYearEntry.getOrderAmountPreviousYear()/ previousYearEntry.getOrderAmount());
					entry.setStockChangePercent(previousYearEntry.getStockAmountPreviousYear()/previousYearEntry.getStockAmount());
					entry.setTotalChangePercent(previousYearEntry.getTotalAmountPreviousYear()/previousYearEntry.getTotalAmount());
					
				}
			}
		}
	}
	
	private double getTotalStockAmount(Map<String, Map<Long, ProductGroupEntry>> entries)
	{
		double totalAmount = 0D;
		for (Map<Long, ProductGroupEntry> productGroups : entries.values())
		{
			for (ProductGroupEntry entry : productGroups.values())
			{
				totalAmount += entry.getStockAmount();
			}
		}
		return totalAmount;
	}
	
	private double getTotalOrderAmount(Map<String, Map<Long, ProductGroupEntry>> entries)
	{
		double totalAmount = 0D;
		for (Map<Long, ProductGroupEntry> productGroups : entries.values())
		{
			for (ProductGroupEntry entry : productGroups.values())
			{
				totalAmount += entry.getOrderAmount();
			}
		}
		return totalAmount;
	}
	
	private double getTotalTotalAmount(Map<String, Map<Long, ProductGroupEntry>> entries)
	{
		double totalAmount = 0D;
		for (Map<Long, ProductGroupEntry> productGroups : entries.values())
		{
			for (ProductGroupEntry entry : productGroups.values())
			{
				totalAmount += entry.getTotalAmount();
			}
		}
		return totalAmount;
	}
	
	@Override
	public void setFocus()
	{
		this.start.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		updateStartButton();
	}
	
	private boolean validateSelection()
	{
		return (getSelectedSalespoints() != null && getSelectedSalespoints().length > 0) && getSelectedDateRange() != null
				&& getSelectedDateRange().length == 2;	
	}

	private void updateStartButton()
	{
		this.start.setEnabled(this.validateSelection());
	}

	private String getSalespointList()
	{
		StringBuilder salespoints = new StringBuilder();
		if (getSelectedSalespoints() != null && getSelectedSalespoints().length > 0)
		{
			for (Salespoint selectedSalespoint : this.getSelectedSalespoints())
			{
				if (salespoints.length() > 0)
				{
					salespoints.append(", ");
				}
				salespoints.append(selectedSalespoint.getName());
			}
		}
		return salespoints.toString();
	}

	private String getDateRangeList()
	{
		if (getSelectedDateRange() != null && getSelectedDateRange().length == 2)
		{
			if (getSelectedDateRange()[0].get(Calendar.YEAR) == this.getSelectedDateRange()[1].get(Calendar.YEAR))
			{
				if (getSelectedDateRange()[0].get(Calendar.MONTH) == getSelectedDateRange()[1].get(Calendar.MONTH))
				{
					if (getSelectedDateRange()[0].get(Calendar.DATE) == getSelectedDateRange()[1].get(Calendar.DATE))
					{
						return SimpleDateFormat.getDateInstance().format(getSelectedDateRange()[0].getTime());
					}
				}
			}

			StringBuilder builder = new StringBuilder();
			builder = builder.append(SimpleDateFormat.getDateInstance().format(getSelectedDateRange()[0].getTime()));
			builder = builder.append(" bis ");
			builder = builder.append(SimpleDateFormat.getDateInstance().format(getSelectedDateRange()[1].getTime()));
			return builder.toString();
		}
		return "";
	}

	private Hashtable<String, Object> getParameters()
	{
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		final String header = "Header";
		parameters.put("header", header);
		parameters.put("printTime",
				SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance(Locale.getDefault()).getTime()));
		parameters.put("salespoints", getSalespointList());
		if (providerViewer != null)
		{
			IStructuredSelection ssel = (IStructuredSelection) providerViewer.getSelection();
			parameters.put("provider", ((String[]) ssel.getFirstElement()).clone()[0]);
		}
		else
		{
			parameters.put("provider", "");
		}
		parameters.put("dateRange", getDateRangeList());
		
		Calendar[] dateRange = getSelectedDateRange();
		parameters.put("previousYear", Integer.valueOf(dateRange[0].get(Calendar.YEAR) - 1).toString());
		parameters.put("currentYear", Integer.valueOf(dateRange[0].get(Calendar.YEAR)).toString());
		
		IStructuredSelection ssel = (IStructuredSelection) typeViewer.getSelection();
		URL entry = Activator.getDefault().getBundle().getEntry("/reports/" + ((Type) ssel.getFirstElement()).report(compareWithPreviousYear.getSelection()) + ".properties");
		try
		{
			InputStream stream = entry.openStream();
			parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, new PropertyResourceBundle(stream));
		}
		catch (Exception e)
		{
		}
		return parameters;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		this.updateStartButton();
	}

	protected IStatus printReport(IProgressMonitor monitor, final InputStream report, final JRDataSource dataSource, final Map<String, Object> parameters)
	{
		if (this.selectedDestination == null)
		{
			return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Sie haben kein Ausgabeziel ausgewählt.");
		}
		File exportFile = null;
		if (this.selectedDestination.equals(ReportService.Destination.EXPORT))
		{
			if (this.selectedFormat == null)
			{
				return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Sie haben kein Dateiformat ausgewählt.");
			}
			else
			{
				FileDialog dialog = new FileDialog(this.getSite().getShell());
				dialog.setFileName("*" + selectedFormat.extension());
				dialog.setFilterExtensions(new String[] { "*" + selectedFormat.extension() });
				dialog.setFilterIndex(0);
				dialog.setFilterNames(new String[] { selectedFormat.label() });
				dialog.setText("Datei");
				String path = dialog.open();
				if (path == null)
				{
					return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
				}
				exportFile = new File(path);
			}
		}

		ServiceTracker<ReportService, ReportService> tracker = new ServiceTracker<ReportService, ReportService>(Activator.getDefault().getBundle().getBundleContext(), ReportService.class, null);
		try
		{
			tracker.open();
			final ReportService service = tracker.getService();
			if (service != null)
			{
				monitor.beginTask("Vorschau", 1);
				switch (this.selectedDestination)
				{
					case EXPORT:
					{
						service.export(report, dataSource, parameters, selectedFormat, exportFile);
						break;
					}
					case PRINTER:
					{
						service.print(report, dataSource, parameters, false);
						break;
					}
					case RECEIPT_PRINTER:
					{
					}
					case PREVIEW:
					{
						service.view(monitor, report, dataSource, parameters);
						monitor.worked(1);
						monitor.done();
						break;
					}
					default:
					{
						return new Status(IStatus.CANCEL, Activator.PLUGIN_ID,
								"Das ausgewählte Ausgabeziel ist nicht verfügbar.");
					}
				}
				monitor.beginTask("Vorschau", 1);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			tracker.close();
			monitor.done();
		}
		return Status.OK_STATUS;
	}
	
	private enum Type
	{
		TOTAL, PRODUCT_GROUP, STOCK_ORDER;

		public String label()
		{
			switch(this)
			{
			case TOTAL:
			{
				return "Gesamtumsatz";
			}
			case PRODUCT_GROUP:
			{
				return "Warengruppenumsatz";
			}
			case STOCK_ORDER:
			{
				return "Vergleich Lager - Besorgung";
			}
			default:
			{
				return TOTAL.label();
			}
			}
		}

		public String report(boolean compareWithPreviousYear)
		{
			switch(this)
			{
			case TOTAL:
			{
				return compareWithPreviousYear ? "ProductGroupStatisticsWithPreviousYear" : "ProductGroupStatistics";
			}
			case PRODUCT_GROUP:
			{
				return compareWithPreviousYear ? "ProductGroupStatisticsEachWithPreviousYear" : "ProductGroupStatisticsEach";
			}
			case STOCK_ORDER:
			{
				return "ProductGroupStockOrderStatistics";
			}
			default:
			{
				return TOTAL.label();
			}
			}
		}
	}
}