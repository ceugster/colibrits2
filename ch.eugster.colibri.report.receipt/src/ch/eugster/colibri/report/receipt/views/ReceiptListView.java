package ch.eugster.colibri.report.receipt.views;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.report.receipt.Activator;
import ch.eugster.colibri.report.receipt.views.ReceiptFilterView.ReceiptStateSelector;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class ReceiptListView extends ViewPart implements ISelectionListener
{
	public static final String ID = "ch.eugster.colibri.report.receipt.receiptview";

	private CommonSettings commonSettings;

	private String pattern;

	private TableViewer viewer;

	private SettlementViewerFilter settlementFilter;

	private ReceiptStateViewerFilter stateFilter;

	private UserViewerFilter userFilter;
	
	private PaymentTypeViewerFilter paymentTypeFilter;
	
	private AmountViewerFilter amountFilter;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private Label receiptCountLabel;

	private Label totalAmountLabel;

	private NumberFormat inf;

	private final NumberFormat dnf = DecimalFormat.getCurrencyInstance();

	private final DateFormat df = SimpleDateFormat.getDateTimeInstance();

	private void setSummary(Object[] items)
	{
		this.receiptCountLabel.setText("Anzahl Belege: " + items.length);
		double total = 0d;
		for (int i = 0; i < items.length; i++)
		{
			Receipt receipt = null;
			if (items[i] instanceof TableItem)
			{
				TableItem item = (TableItem) items[i];
				Object object = item.getData();
				if (object instanceof Receipt)
				{
					receipt = (Receipt) object;
				}
			}
			else if (items[i] instanceof Receipt)
			{
				receipt = (Receipt) items[i];
			}
			if (receipt != null)
			{
				if (i == 0)
				{
					dnf.setCurrency(receipt.getDefaultCurrency().getCurrency());
				}
				total += receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO);
			}
		}

		this.totalAmountLabel.setText("Gesamtbetrag: " + dnf.format(total));
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());

		final Table table = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);

		settlementFilter = new SettlementViewerFilter();
		userFilter = new UserViewerFilter();
		stateFilter = new ReceiptStateViewerFilter();
		paymentTypeFilter = new PaymentTypeViewerFilter();
		amountFilter = new AmountViewerFilter();
		
		ViewerFilter[] filters = new ViewerFilter[] { settlementFilter, userFilter, stateFilter, paymentTypeFilter, amountFilter,
				new DeletedEntityViewerFilter() };

		this.viewer = new TableViewer(table);
		this.viewer.setContentProvider(new ReceiptContentProvider());
		this.viewer.setSorter(new ReceiptSorter(this.pattern));
		this.viewer.setFilters(filters);

		TableViewerColumn viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Nummer");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Receipt)
				{
					final Receipt receipt = (Receipt) cell.getElement();
					cell.setText(ReceiptListView.this.inf.format(receipt.getNumber()));
					if (receipt.getState().equals(Receipt.State.REVERSED))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("receipt-reversed"));
					}
					else
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("receipt-ok"));
					}
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Datum Zeit");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Receipt)
				{
					final Receipt receipt = (Receipt) cell.getElement();
					cell.setText(ReceiptListView.this.df.format(receipt.getTimestamp().getTime()));
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Status");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Receipt)
				{
					final Receipt receipt = (Receipt) cell.getElement();
					cell.setText(receipt.getState().toString());
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		viewerColumn.getColumn().setText("Betrag");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Receipt)
				{
					final Receipt receipt = (Receipt) cell.getElement();
					dnf.setGroupingUsed(true);
					dnf.setCurrency(receipt.getDefaultCurrency().getCurrency());
					cell.setText(ReceiptListView.this.dnf.format(receipt
							.getPositionDefaultCurrencyAmount(Position.AmountType.NETTO)));
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		viewerColumn.getColumn().setText("Bezahlt");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Receipt)
				{
					final Receipt receipt = (Receipt) cell.getElement();
					dnf.setGroupingUsed(true);
					dnf.setCurrency(receipt.getDefaultCurrency().getCurrency());
					cell.setText(ReceiptListView.this.dnf.format(receipt.getPaymentDefaultCurrencyAmount()));
					double difference = receipt.getDifference();
					if (difference == 0D)
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("receipt-ok"));
					}
					else
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("receipt-reversed"));
					}
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Abgeschlossen");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Receipt)
				{
					final Receipt receipt = (Receipt) cell.getElement();
					final Settlement settlement = receipt.getSettlement();
					if (settlement.getSettled() == null)
					{
						cell.setText("");
					}
					else
					{
						Calendar calendar = Calendar.getInstance();
						calendar = settlement.getSettled();
						cell.setText(ReceiptListView.this.df.format(calendar.getTime()));
					}
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Benutzer");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Receipt)
				{
					final Receipt receipt = (Receipt) cell.getElement();
					cell.setText(receipt.getUser().getUsername());
				}
			}
		});

		Composite info = new Composite(composite, SWT.NONE);
		info.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		info.setLayout(new GridLayout(2, true));

		this.receiptCountLabel = new Label(info, SWT.NONE);
		this.receiptCountLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.receiptCountLabel.setText("");

		this.totalAmountLabel = new Label(info, SWT.NONE);
		this.totalAmountLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.totalAmountLabel.setText("");

		final TableColumn[] columns = this.viewer.getTable().getColumns();
		for (final TableColumn column : columns)
		{
			column.pack();
		}

		this.getSite().setSelectionProvider(viewer);
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(this);
		super.dispose();
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService().getQuery(
					CommonSettings.class);
			this.commonSettings = query.findDefault();
			if (this.commonSettings != null)
			{
				this.pattern = this.commonSettings.getReceiptNumberFormat();
				if ((this.pattern != null) && (this.pattern.length() > 0))
				{
					this.inf = new DecimalFormat(this.pattern);
				}
				else
				{
					this.inf = NumberFormat.getIntegerInstance();
				}
			}
		}

		site.getWorkbenchWindow().getSelectionService().addPostSelectionListener(this);
	}

	@Override
	public void setFocus()
	{
		this.viewer.getTable().setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if (part instanceof ReceiptFilterView)
		{
			if (selection instanceof StructuredSelection)
			{
				StructuredSelection ssel = (StructuredSelection) selection;
				if (ssel.getFirstElement() instanceof Settlement)
				{
					settlementFilter.setSettlement((Settlement) ssel.getFirstElement());
					this.viewer.refresh();
					TableItem[] objects = this.viewer.getTable().getItems();
					setSummary(objects);
					viewer.setSelection(objects.length == 0 ? new StructuredSelection() : new StructuredSelection(
							new Object[] { objects[0] }));
				}
				else if (ssel.getFirstElement() instanceof ReceiptStateSelector)
				{
					stateFilter.setReceiptState((ReceiptStateSelector) ssel.getFirstElement());
					this.viewer.refresh();
					TableItem[] objects = this.viewer.getTable().getItems();
					setSummary(objects);
					viewer.setSelection(objects.length == 0 ? new StructuredSelection() : new StructuredSelection(
							new Object[] { objects[0] }));
				}
				else if (ssel.getFirstElement() instanceof User)
				{
					userFilter.setUser((User) ssel.getFirstElement());
					this.viewer.refresh();
					TableItem[] objects = this.viewer.getTable().getItems();
					setSummary(objects);
					viewer.setSelection(objects.length == 0 ? new StructuredSelection() : new StructuredSelection(
							new Object[] { objects[0] }));
				}
				else if (ssel.getFirstElement() instanceof PaymentType)
				{
					paymentTypeFilter.setPaymentType((PaymentType) ssel.getFirstElement());
					this.viewer.refresh();
					TableItem[] objects = this.viewer.getTable().getItems();
					setSummary(objects);
					viewer.setSelection(objects.length == 0 ? new StructuredSelection() : new StructuredSelection(
							new Object[] { objects[0] }));
				}
				else if (ssel.getFirstElement() instanceof Double)
				{
					amountFilter.setAmount((Double) ssel.getFirstElement());
					this.viewer.refresh();
					TableItem[] objects = this.viewer.getTable().getItems();
					setSummary(objects);
					viewer.setSelection(objects.length == 0 ? new StructuredSelection() : new StructuredSelection(
							new Object[] { objects[0] }));
				}
				else
				{
					Object[] objects = ssel.toArray();
					this.viewer.setInput(objects);
					setSummary(objects);
					TableColumn[] columns = this.viewer.getTable().getColumns();
					for (TableColumn column : columns)
					{
						column.pack();
					}
				}
			}
		}
	}

}
