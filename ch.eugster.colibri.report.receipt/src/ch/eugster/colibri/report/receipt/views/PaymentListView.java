package ch.eugster.colibri.report.receipt.views;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class PaymentListView extends ViewPart implements ISelectionListener
{
	public static final String ID = "ch.eugster.colibri.report.receipt.paymentview";

	private TableViewer viewer;

//	private SettlementViewerFilter settlementFilter;
//
//	private ReceiptStateViewerFilter stateFilter;
//
//	private UserViewerFilter userFilter;

	private PaymentTypeViewerFilter paymentTypeFilter;
	
	private AmountViewerFilter amountFilter;
	
	private final NumberFormat currencyFormatter = DecimalFormat.getCurrencyInstance();

	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
	}

	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(this);
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		final Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());

		final Table table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);

//		settlementFilter = new SettlementViewerFilter();
//		userFilter = new UserViewerFilter();
//		stateFilter = new ReceiptStateViewerFilter();
		paymentTypeFilter = new PaymentTypeViewerFilter();
		amountFilter = new AmountViewerFilter();

		ViewerFilter[] filters = new ViewerFilter[] { paymentTypeFilter, amountFilter,
				new DeletedEntityViewerFilter() };

		this.viewer = new TableViewer(table);
		this.viewer.setContentProvider(new PaymentContentProvider());
		this.viewer.setFilters(filters);
		TableViewerColumn viewerColumn = new TableViewerColumn(this.viewer, SWT.None);
		viewerColumn.getColumn().setText("Zahlungsart");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Payment)
				{
					final Payment payment = (Payment) cell.getElement();
					PaymentType paymentType = payment.getPaymentType();
					cell.setText((payment.isBack() ? "Rückgeld " : "") + paymentType.getName());
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		viewerColumn.getColumn().setText("Betrag LW");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Payment)
				{
					final Payment payment = (Payment) cell.getElement();
					PaymentListView.this.currencyFormatter.setCurrency(payment.getReceipt().getDefaultCurrency()
							.getCurrency());
					cell.setText(PaymentListView.this.currencyFormatter.format(payment
							.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY)));
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		viewerColumn.getColumn().setText("Betrag FW");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Payment)
				{
					final Payment payment = (Payment) cell.getElement();
					PaymentListView.this.currencyFormatter.setCurrency(payment.getPaymentType().getCurrency()
							.getCurrency());
					cell.setText(PaymentListView.this.currencyFormatter.format(payment
							.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY)));
				}
			}
		});

		this.getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(this);

		IViewPart view = this.getSite().getWorkbenchWindow().getActivePage().findView("ch.eugster.colibri.report.receipt.receiptview");
		if (view instanceof ReceiptListView)
		{
			ReceiptListView receiptView = (ReceiptListView) view;
			ISelection selection = receiptView.getSelection();
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection ssel = (IStructuredSelection) selection;
				if (ssel.isEmpty())
				{
					this.viewer.setInput(null);
				}
				else if (ssel.getFirstElement() instanceof Receipt)
				{
					Receipt receipt = (Receipt) ssel.getFirstElement();
					this.viewer.setInput(receipt);
					final TableColumn[] columns = this.viewer.getTable().getColumns();
					for (final TableColumn column : columns)
					{
						column.pack();
					}
				}
			}
		}

		final TableColumn[] columns = this.viewer.getTable().getColumns();
		for (final TableColumn column : columns)
		{
			column.pack();
		}

	}

	@Override
	public void selectionChanged(final IWorkbenchPart part, final ISelection selection)
	{
		if (part instanceof ReceiptFilterView)
		{
//			IStructuredSelection ssel = (IStructuredSelection) selection;
//			if (ssel.getFirstElement() instanceof PaymentType)
//			{
//				paymentTypeFilter.setPaymentType((PaymentType) ssel.getFirstElement());
//				this.viewer.refresh();
//				TableItem[] objects = this.viewer.getTable().getItems();
//				viewer.setSelection(objects.length == 0 ? new StructuredSelection() : new StructuredSelection(
//						new Object[] { objects[0] }));
//			}
//			else if (ssel.getFirstElement() instanceof Double)
//			{
//				Double amount = (Double) ssel.getFirstElement();
//				amountFilter.setAmount(amount);
//				this.viewer.refresh();
//				TableItem[] objects = this.viewer.getTable().getItems();
//				viewer.setSelection(objects.length == 0 ? new StructuredSelection() : new StructuredSelection(
//						new Object[] { objects[0] }));
//			}
		}
		else if (part instanceof ReceiptListView)
		{
			StructuredSelection ssel = (StructuredSelection) selection;
			if (ssel.isEmpty())
			{
				this.viewer.setInput(null);
			}
			else if (ssel.getFirstElement() instanceof Receipt)
			{
				Receipt receipt = (Receipt) ssel.getFirstElement();
				this.viewer.setInput(receipt);
				final TableColumn[] columns = this.viewer.getTable().getColumns();
				for (final TableColumn column : columns)
				{
					column.pack();
				}
			}
		}
	}

	@Override
	public void setFocus()
	{
		this.viewer.getTable().setFocus();
	}

}
