package ch.eugster.colibri.report.destination.views;

import java.util.Iterator;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import ch.eugster.colibri.report.destination.Activator;
import ch.eugster.colibri.report.engine.ReportService;

public class DestinationView extends ViewPart implements ISelectionProvider
{
	public static final String ID = "ch.eugster.colibri.report.destination.view";

	private Composite composite;

	private ComboViewer formats;

	private ComboViewer destinations;

	private IDialogSettings settings;

	private final ListenerList listeners = new ListenerList();

	@Override
	public void createPartControl(final Composite parent)
	{
		this.composite = new Composite(parent, SWT.NONE);
		this.composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.composite.setLayout(new GridLayout(2, false));

		Label label = new Label(this.composite, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Ausgabe");

		Combo combo = new Combo(this.composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.destinations = new ComboViewer(combo);
		this.destinations.setContentProvider(new ArrayContentProvider());
		this.destinations.setInput(ReportService.Destination.values());
		this.destinations.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final StructuredSelection ssel = (StructuredSelection) event.getSelection();
				if (!ssel.isEmpty())
				{
					final ReportService.Destination destination = (ReportService.Destination) ssel.getFirstElement();
					DestinationView.this.settings.put("selected.destination", destination.ordinal());
					DestinationView.this.formats.getCombo().setEnabled(destination.equals(ReportService.Destination.EXPORT));
					fireSelectionChanged();
				}
			}
		});

		label = new Label(this.composite, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Format");

		combo = new Combo(this.composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.formats = new ComboViewer(combo);
		this.formats.setContentProvider(new ArrayContentProvider());
		this.formats.setInput(ReportService.Format.values());
		this.formats.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final StructuredSelection ssel = (StructuredSelection) event.getSelection();
				if (!ssel.isEmpty())
				{
					final ReportService.Format format = (ReportService.Format) ssel.getFirstElement();
					DestinationView.this.settings.put("selected.format", format.ordinal());
					fireSelectionChanged();
				}
			}

		});

		getSite().setSelectionProvider(this);

		ReportService.Destination destination = ReportService.Destination.values()[this.settings.getInt("selected.destination")];
		ReportService.Format format = ReportService.Format.values()[this.settings.getInt("selected.format")];
		StructuredSelection ssel = new StructuredSelection(new Object[] { destination, format });
		setSelection(ssel);
	}

	private void fireSelectionChanged()
	{
		final SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		Object[] listeners = this.listeners.getListeners();
		for (int i = 0; i < listeners.length; ++i)
		{
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable()
			{
				@Override
				public void run()
				{
					l.selectionChanged(event);
				}
			});
		}
	}

	public ReportService.Destination getSelectedDestination()
	{
		final StructuredSelection ssel = (StructuredSelection) this.destinations.getSelection();
		return (ReportService.Destination) ssel.getFirstElement();
	}

	public ReportService.Format getSelectedFormat()
	{
		final StructuredSelection ssel = (StructuredSelection) this.formats.getSelection();
		return (ReportService.Format) ssel.getFirstElement();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public ISelection getSelection()
	{
		Object[] selected = new Object[0];
		StructuredSelection ssel = (StructuredSelection) this.destinations.getSelection();
		ReportService.Destination destination = (ReportService.Destination) ssel.getFirstElement();
		if (destination != null)
		{
			ReportService.Format format = null;
			if (destination.equals(ReportService.Destination.EXPORT))
			{
				ssel = (StructuredSelection) this.formats.getSelection();
				format = (ReportService.Format) ssel.getFirstElement();
			}
			if (format == null)
			{
				selected = new Object[] { destination };
			}
			else
			{
				selected = new Object[] { destination, format };
			}
		}
		return new StructuredSelection(selected);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection)
	{
		if (selection instanceof StructuredSelection)
		{
			StructuredSelection ssel = (StructuredSelection) selection;
			Iterator<?> iterator = ssel.iterator();
			while (iterator.hasNext())
			{
				Object object = iterator.next();
				if (object != null)
				{
					StructuredSelection sel = new StructuredSelection(new Object[] { object });
					if (object instanceof ReportService.Destination)
					{
						this.destinations.setSelection(sel);
					}
					else if (object instanceof ReportService.Format)
					{
						this.formats.setSelection(sel);
					}
				}
			}
		}
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);

		this.settings = Activator.getDefault().getDialogSettings().getSection("destination.view");
		if (this.settings == null)
		{
			this.settings = Activator.getDefault().getDialogSettings().addNewSection("destination.view");
		}
		try
		{
			int value = this.settings.getInt("selected.destination");
			if (value >= ReportService.Destination.values().length)
			{
				this.settings.put("selected.destination", 0);
			}
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("selected.destination", 0);
		}
		try
		{
			int value = this.settings.getInt("selected.format");
			if (value >= ReportService.Destination.values().length)
			{
				this.settings.put("selected.format", 0);
			}
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("selected.format", 0);
		}
	}

	@Override
	public void setFocus()
	{
		this.destinations.getCombo().setFocus();
	}

//	public enum Destination
//	{
//		SCREEN, PRINTER, RECEIPT_PRINTER, FILE;
//
//		@Override
//		public String toString()
//		{
//			switch (this)
//			{
//				case SCREEN:
//				{
//					return "Bildschirm";
//				}
//				case PRINTER:
//				{
//					return "Drucker";
//				}
//				case RECEIPT_PRINTER:
//				{
//					return "Belegdrucker";
//				}
//				case FILE:
//				{
//					return "Datei";
//				}
//				default:
//					throw new RuntimeException("Invalid destination selected");
//			}
//		}
//	}

//	public enum Format
//	{
//		PDF, EXCEL, HTML, XML;
//
//		@Override
//		public String toString()
//		{
//			switch (this)
//			{
//				case PDF:
//				{
//					return "Portable Document Format (*.pdf)";
//				}
//				case EXCEL:
//				{
//					return "Excel-Arbeitsmappe (*.xls)";
//				}
//				case HTML:
//				{
//					return "HTML-Datei (*.html)";
//				}
//				case XML:
//				{
//					return "XML- Datei (*.xml)";
//				}
//				default:
//					throw new RuntimeException("Invalid destination selected");
//			}
//		}
//	}
}
