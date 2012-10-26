package ch.eugster.colibri.admin.ui.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public abstract class TableWizardPage<T extends AbstractEntity> extends ItemsWizardPage<T> implements ISelectionChangedListener,
		IDoubleClickListener
{
	private TableViewer viewer;

	public TableWizardPage(final String name, final String title, final ImageDescriptor image)
	{
		super(name, title, image);
	}

	public void createControl(final Composite parent)
	{
		setTitle(getTitle());

		this.setMessage(this.getPageMessage());

		final GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(layout);

		final GridData layoutData = new GridData(GridData.FILL_BOTH);

		final Table table = new Table(composite, this.getTableStyle());
		table.setLayoutData(layoutData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		for (int i = 0; i < this.getColumnNames().length; i++)
		{
			final TableColumn column = new TableColumn(table, this.getColumnAlignments()[i]);
			column.setText(this.getColumnNames()[i]);
			column.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(final SelectionEvent event)
				{
					final TableColumn column = (TableColumn) event.getSource();
					TableWizardPage.this.columnSelected(column);
				}
			});
		}

		this.viewer = new TableViewer(table);
		this.viewer.setColumnProperties(this.getColumnNames());
		this.viewer.setContentProvider(this.getContentProvider());
		this.viewer.setLabelProvider(this.getLabelProvider());
		this.viewer.setSorter(this.getSorter());
		this.viewer.setFilters(this.getInitializedFilters());
		this.viewer.setInput(getInput());
		this.viewer.addSelectionChangedListener(this);
		this.packColumns();

		/*
		 * Drag and Drop Support
		 */
		final int dropSupport = this.getDropSupport();
		if ((dropSupport > 0) && (this.getViewerDropAdapter(this.viewer) != null))
		{
			this.viewer
					.addDropSupport(dropSupport, new Transfer[] { LocalSelectionTransfer.getTransfer() }, this.getViewerDropAdapter(this.viewer));
		}

		final int dragSupport = this.getDragSupport();
		if ((dragSupport > 0) && (this.getViewerDragListener(this.viewer) != null))
		{
			this.viewer.addDragSupport(dragSupport, new Transfer[] { LocalSelectionTransfer.getTransfer() }, this
					.getViewerDragListener(this.viewer));
		}

		if (this.hasOrderComposite())
		{
			this.createOrderComposite(parent);
		}

		setPageComplete(!this.viewer.getSelection().isEmpty());

		setControl(composite);
	}

	public void doubleClick(final DoubleClickEvent event)
	{
		if (event.getSource().equals(this.viewer))
		{
			final StructuredSelection selection = (StructuredSelection) event.getSelection();
			if (selection.isEmpty())
			{
				setPageComplete(false);
			}
			else
			{
				setPageComplete(true);
			}

		}
		fireDoubleClickEvent(event);
	}

	@Override
	public ISelection getSelection()
	{
		return this.viewer.getSelection();
	}

	public TableViewer getViewer()
	{
		return this.viewer;
	}

	@Override
	public boolean isEmptySelection()
	{
		return this.viewer.getSelection().isEmpty();
	}

	public void packColumns()
	{
		final TableColumn[] columns = this.viewer.getTable().getColumns();
		for (final TableColumn column : columns)
		{
			column.pack();
		}
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		this.viewer.refresh(entity);
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		this.viewer.add(entity);
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		this.viewer.update(entity, null);
	}

	public void selectionChanged(final SelectionChangedEvent event)
	{
		if (event.getSelectionProvider().equals(this.viewer))
		{
			final StructuredSelection selection = (StructuredSelection) event.getSelection();
			if (selection.isEmpty())
			{
				setPageComplete(false);
			}
			else
			{
				setPageComplete(true);
			}

		}
		fireSelectionChangedEvent(event);
	}

	@Override
	public void setSelection(final ISelection selection)
	{
		this.viewer.setSelection(selection);
	}

	protected void columnSelected(final TableColumn column)
	{
	}

	protected abstract int[] getColumnAlignments();

	protected abstract String[] getColumnNames();

	protected IContentProvider getContentProvider()
	{
		return new TableContentProvider();
	}

	protected int getDragSupport()
	{
		return 0;
	}

	protected int getDropSupport()
	{
		return 0;
	}

	protected ViewerFilter[] getInitializedFilters()
	{
		return new ViewerFilter[] { new DeletedEntityViewerFilter() };
	}

	protected abstract ILabelProvider getLabelProvider();

	protected abstract String getPageMessage();

	protected abstract ViewerSorter getSorter();

	protected int getTableStyle()
	{
		return SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
	}

	protected DragSourceListener getViewerDragListener(final TableViewer viewer)
	{
		return null;
	}

	protected ViewerDropAdapter getViewerDropAdapter(final TableViewer viewer)
	{
		return null;
	}

	protected boolean hasOrderComposite()
	{
		return false;
	}

	/**
	 * Implement by subclasses if order feature is wished
	 * 
	 * @param up
	 */
	protected void reorderItems(final boolean up)
	{
	}

	private void createOrderComposite(final Composite parent)
	{
		final OrderComposite orderComposite = new OrderComposite(parent, SWT.NONE);
		this.viewer.addSelectionChangedListener(orderComposite);
	}

	protected class TableContentProvider implements IStructuredContentProvider
	{
		public void dispose()
		{
		}

		public Object[] getElements(final Object entity)
		{
			if (entity instanceof AbstractEntity[])
			{
				return (AbstractEntity[]) entity;
			}
			else
			{
				return new AbstractEntity[0];
			}
		}

		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
		{
		}
	}

	private class OrderComposite extends Composite implements ISelectionChangedListener
	{
		private Button up;

		private Button down;

		public OrderComposite(final Composite parent, final int style)
		{
			super(parent, style);
			this.createControls(this);
		}

		public void selectionChanged(final SelectionChangedEvent event)
		{
			if (event.getSelection().isEmpty())
			{
				this.up.setEnabled(!event.getSelection().isEmpty());
				this.down.setEnabled(!event.getSelection().isEmpty());
			}
			else
			{
				final Table table = ((TableViewer) event.getSource()).getTable();
				final Object object = ((StructuredSelection) event.getSelection()).getFirstElement();
				this.up.setEnabled(!object.equals(table.getItem(0).getData()));
				this.down.setEnabled(!object.equals(table.getItem(table.getItemCount() - 1).getData()));
			}
		}

		protected void createControls(final Composite parent)
		{
			final GridLayout layout = new GridLayout();
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.marginTop = 0;
			layout.marginBottom = 0;
			layout.marginWidth = 0;
			layout.marginHeight = 0;

			setLayout(layout);

			final Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout());
			composite.setLayoutData(new GridData());

			this.up = new Button(composite, SWT.PUSH);
			this.up.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false));
			this.up.setText("Nach oben");
			this.up.setEnabled(!TableWizardPage.this.viewer.getSelection().isEmpty());
			this.up.addSelectionListener(new SelectionListener()
			{
				public void widgetDefaultSelected(final SelectionEvent event)
				{
					this.widgetSelected(event);
				}

				public void widgetSelected(final SelectionEvent event)
				{
					if (!TableWizardPage.this.viewer.getSelection().isEmpty())
					{
						TableWizardPage.this.reorderItems(true);
					}
				}
			});

			this.down = new Button(composite, SWT.PUSH);
			this.down.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			this.down.setText("Nach unten");
			this.down.setEnabled(!TableWizardPage.this.viewer.getSelection().isEmpty());
			this.down.addSelectionListener(new SelectionListener()
			{
				public void widgetDefaultSelected(final SelectionEvent event)
				{
					this.widgetSelected(event);
				}

				public void widgetSelected(final SelectionEvent event)
				{
					if (!TableWizardPage.this.viewer.getSelection().isEmpty())
					{
						TableWizardPage.this.reorderItems(false);
					}
				}
			});
		}
	}
}
