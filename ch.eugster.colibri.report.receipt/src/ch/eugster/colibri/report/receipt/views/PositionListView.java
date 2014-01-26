package ch.eugster.colibri.report.receipt.views;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class PositionListView extends ViewPart implements ISelectionListener, ISelectionChangedListener
{
	public static final String ID = "ch.eugster.colibri.report.receipt.positionview";

	private TableViewer viewer;

//	private SettlementViewerFilter settlementFilter;
//
//	private ReceiptStateViewerFilter stateFilter;
//
//	private UserViewerFilter userFilter;

	private final NumberFormat quantityFormatter = DecimalFormat.getIntegerInstance();

	private final NumberFormat currencyFormatter = DecimalFormat.getCurrencyInstance();

	private final NumberFormat percentFormatter = DecimalFormat.getPercentInstance();

	private final DateFormat datetimeFormatter = SimpleDateFormat.getDateTimeInstance();

	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
	}

	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
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

		ViewerFilter[] filters = new ViewerFilter[] { new DeletedEntityViewerFilter() };

		this.viewer = new TableViewer(table);
		this.viewer.setContentProvider(new PositionContentProvider());
		this.viewer.setFilters(filters);

		TableViewerColumn viewerColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		viewerColumn.getColumn().setText("Menge");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					cell.setText(PositionListView.this.quantityFormatter.format(position.getQuantity()).toString());
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		viewerColumn.getColumn().setText("Preis");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					final Currency currency = position.getForeignCurrency().getCurrency();
					PositionListView.this.currencyFormatter.setCurrency(currency);
					cell.setText(PositionListView.this.currencyFormatter.format(position.getPrice()));
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		viewerColumn.getColumn().setText("Rabatt");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					cell.setText(PositionListView.this.percentFormatter.format(position.getDiscount()));
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
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					final double amount = position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
							Position.AmountType.NETTO);
					cell.setText(PositionListView.this.currencyFormatter.format(amount));
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		viewerColumn.getColumn().setText("Mwst");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					cell.setText(PositionListView.this.percentFormatter
							.format(position.getCurrentTax().getPercentage()));
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.None);
		viewerColumn.getColumn().setText("Option");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					cell.setText(position.getOption() == null ? "" : position.getOption().toString());
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.None);
		viewerColumn.getColumn().setText("WG Warenbew.");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					if (position.getProduct() == null)
					{
						cell.setText(position.getProductGroup().getCode());
					}
					else
					{
						cell.setText(position.getProduct().getExternalProductGroup().format());
					}
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.None);
		viewerColumn.getColumn().setText("Autor");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					if (position.getProduct() != null)
					{
						cell.setText(position.getProduct().getAuthor());
					}
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.None);
		viewerColumn.getColumn().setText("Titel");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					if (position.getProduct() != null)
					{
						cell.setText(position.getProduct().getTitle());
					}
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.None);
		viewerColumn.getColumn().setText("Verlag");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					if (position.getProduct() != null)
					{
						cell.setText(position.getProduct().getPublisher());
					}
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.None);
		viewerColumn.getColumn().setText("Rechnungsnummer");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					if (position.getProduct() != null)
					{
						if (position.getProduct().getInvoiceNumber() != null)
						{
							cell.setText(position.getProduct().getInvoiceNumber());
						}
					}
					cell.setText(position.getOrder());
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.None);
		viewerColumn.getColumn().setText("Rechnungsdatum");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Position)
				{
					final Position position = (Position) cell.getElement();
					if (position.getProduct() != null)
					{
						if (position.getProduct().getInvoiceDate() != null)
						{
							cell.setText(PositionListView.this.datetimeFormatter.format(position.getProduct()
									.getInvoiceDate().getTime()));
						}
					}
					cell.setText(position.getOrder());
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
			this.viewer.setInput(null);
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

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		if (event.getSource() != null)
		{

		}
	}

}
