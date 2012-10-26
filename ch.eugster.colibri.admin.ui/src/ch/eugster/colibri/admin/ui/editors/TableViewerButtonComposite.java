/*
 * Created on 19.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.ui.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class TableViewerButtonComposite extends Composite implements ISelectionChangedListener
{

	private Button addButton;

	private Button editButton;

	private Button removeButton;

	private static final String TYPE = "type"; //$NON-NLS-1$

	private Collection<Listener> listeners = new ArrayList<Listener>();

	public static final int EVENT_TYPE_ADD = 0;

	public static final int EVENT_TYPE_EDIT = 1;

	public static final int EVENT_TYPE_REMOVE = 2;

	public TableViewerButtonComposite(final Composite composite, final int style)
	{
		this(composite, new int[] { TableViewerButtonComposite.EVENT_TYPE_ADD, TableViewerButtonComposite.EVENT_TYPE_EDIT,
				TableViewerButtonComposite.EVENT_TYPE_REMOVE }, style);
	}

	public TableViewerButtonComposite(final Composite composite, final int[] buttons, final int style)
	{
		super(composite, style);
		createControls(buttons);
	}

	public void addListener(final Listener listener)
	{
		if (!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}

	public Button getAddButton()
	{
		return addButton;
	}

	public Button getEditButton()
	{
		return editButton;
	}

	public Button getRemoveButton()
	{
		return removeButton;
	}

	public void removeListener(final Listener listener)
	{
		if (listeners.contains(listener))
		{
			listeners.remove(listener);
		}
	}

	public void selectionChanged(final SelectionChangedEvent event)
	{
		final StructuredSelection ssel = (StructuredSelection) event.getSelection();
		if (editButton != null)
		{
			editButton.setEnabled(ssel.size() > 0);
		}
		if (removeButton != null)
		{
			removeButton.setEnabled(ssel.size() > 0);
		}
	}

	private void addAddButton(final Composite composite)
	{
		addButton = new Button(composite, SWT.PUSH);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setText("Hinzufügen");
		addButton.setData(TableViewerButtonComposite.TYPE, Integer.valueOf(TableViewerButtonComposite.EVENT_TYPE_ADD));
		addButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				widgetSelected(event);
			}

			public void widgetSelected(final SelectionEvent event)
			{
				TableViewerButtonComposite.this.fireEvent(addButton);
				// Stock stock = new
				// Stock(StockTableViewerComposite.this.salespoint);
				// StockTableViewerComposite.this.salespoint.addStock(stock);
				// StockTableViewerComposite.this.stockList.addWrapper(new
				// StockWrapper(stock));
			}
		});
		// this.addButton.setEnabled((StockTableViewerComposite.this.viewerComposite.getCurrencyEntries().length
		// > 0));
	}

	private void addEditButton(final Composite composite)
	{
		editButton = new Button(composite, SWT.PUSH);
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editButton.setText("Bearbeiten");
		editButton.setData(TableViewerButtonComposite.TYPE, Integer.valueOf(TableViewerButtonComposite.EVENT_TYPE_EDIT));
		editButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				widgetSelected(event);
			}

			public void widgetSelected(final SelectionEvent event)
			{
				TableViewerButtonComposite.this.fireEvent(editButton);
				// Stock stock = new
				// Stock(StockTableViewerComposite.this.salespoint);
				// StockTableViewerComposite.this.salespoint.addStock(stock);
				// StockTableViewerComposite.this.stockList.addWrapper(new
				// StockWrapper(stock));
			}
		});
		// this.editButton.setEnabled((StockTableViewerComposite.this.viewerComposite.getCurrencyEntries().length
		// > 0));
	}

	private void addRemoveButton(final Composite composite)
	{
		removeButton = new Button(composite, SWT.PUSH);
		removeButton.setText("Entfernen");
		removeButton.setData(TableViewerButtonComposite.TYPE, Integer.valueOf(TableViewerButtonComposite.EVENT_TYPE_REMOVE));
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				widgetSelected(event);
			}

			public void widgetSelected(final SelectionEvent event)
			{
				TableViewerButtonComposite.this.fireEvent(removeButton);
			}
		});
		// this.removeButton.setEnabled(false);
	}

	private void createControls(final int[] buttons)
	{
		setLayout(new GridLayout());
		setLayoutData(new TableWrapData());
		for (final int button : buttons)
		{
			if (button == TableViewerButtonComposite.EVENT_TYPE_ADD)
			{
				addAddButton(this);
			}
			else if (button == TableViewerButtonComposite.EVENT_TYPE_EDIT)
			{
				addEditButton(this);
			}
			else if (button == TableViewerButtonComposite.EVENT_TYPE_REMOVE)
			{
				addRemoveButton(this);
			}
		}
	}

	private void fireEvent(final Button button)
	{
		final Event event = new Event();
		event.button = ((Integer) button.getData(TableViewerButtonComposite.TYPE)).intValue();
		event.data = button.getData();
		event.detail = event.button;
		event.item = button;
		event.type = event.button;
		event.widget = event.item;

		for (final Listener listener : listeners)
		{
			listener.handleEvent(event);
		}
	}
}
