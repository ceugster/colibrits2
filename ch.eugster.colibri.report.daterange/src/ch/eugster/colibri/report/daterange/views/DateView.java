package ch.eugster.colibri.report.daterange.views;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import ch.eugster.colibri.report.daterange.Activator;

public class DateView extends ViewPart implements ISelectionProvider
{
	public static final String ID = "ch.eugster.colibri.report.daterange.view";

	private final ListenerList listeners = new ListenerList();

	private DateTime start;

	private DateTime end;

	private IDialogSettings settings;

	@Override
	public void createPartControl(final Composite parent)
	{
		final Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Startdatum");

		this.start = new DateTime(composite, SWT.DATE | SWT.MEDIUM | SWT.BORDER);
		this.start.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.start.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				settings.put("date.start", getStartDate().getTimeInMillis());
				updateEndDate();
				fireSelectionChanged();
			}
		});

		label = new Label(composite, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Enddatum");

		this.end = new DateTime(composite, SWT.DATE | SWT.MEDIUM | SWT.BORDER);
		this.end.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.end.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				settings.put("date.end", getEndDate().getTimeInMillis());
				updateStartDate();
				fireSelectionChanged();
			}
		});

		getSite().setSelectionProvider(this);
		initDates();
	}

	private void initDates()
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(settings.getLong("date.start"));
		setStartDate(calendar);
		calendar.setTimeInMillis(settings.getLong("date.end"));
		setEndDate(calendar);
	}

	public Calendar[] getDates()
	{
		Calendar[] dates = new Calendar[2];
		dates[0] = getStartDate();
		dates[1] = getEndDate();
		return dates;
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

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
		this.settings = Activator.getDefault().getDialogSettings().getSection("report.daterange");
		if (this.settings == null)
		{
			this.settings = Activator.getDefault().getDialogSettings().addNewSection("report.daterange");
		}
		try
		{
			this.settings.getLong("date.start");
		}
		catch (final NumberFormatException e)
		{
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			this.settings.put("date.start", calendar.getTimeInMillis());
		}
		try
		{
			this.settings.getLong("date.end");
		}
		catch (final NumberFormatException e)
		{
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			this.settings.put("date.end", Calendar.getInstance().getTimeInMillis());
		}
	}

	@Override
	public void setFocus()
	{
		this.start.setFocus();
	}

	private void setStartDate(Calendar calendar)
	{
		start.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
		this.start.setHours(0);
		this.start.setMinutes(0);
		this.start.setSeconds(0);
		System.out.println(SimpleDateFormat.getInstance().format(calendar.getTime()));
	}

	private void setEndDate(Calendar calendar)
	{
		end.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
		this.end.setHours(23);
		this.end.setMinutes(59);
		this.end.setSeconds(59);
	}

	private Calendar getStartDate()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, DateView.this.start.getYear());
		calendar.set(Calendar.MONTH, DateView.this.start.getMonth());
		calendar.set(Calendar.DATE, DateView.this.start.getDay());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	private Calendar getEndDate()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, DateView.this.end.getYear());
		calendar.set(Calendar.MONTH, DateView.this.end.getMonth());
		calendar.set(Calendar.DATE, DateView.this.end.getDay());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar;
	}

	private void updateStartDate()
	{
		if (!getStartDate().before(getEndDate()))
		{
			this.start.setYear(DateView.this.end.getYear());
			this.start.setMonth(DateView.this.end.getMonth());
			this.start.setDay(DateView.this.end.getDay());
			this.start.setHours(0);
			this.start.setMinutes(0);
			this.start.setSeconds(0);
			this.settings.put("date.start", getStartDate().getTimeInMillis());
		}
	}

	private void updateEndDate()
	{
		if (!getEndDate().after(getStartDate()))
		{
			this.end.setYear(DateView.this.start.getYear());
			this.end.setMonth(DateView.this.start.getMonth());
			this.end.setDay(DateView.this.start.getDay());
			this.end.setHours(23);
			this.end.setMinutes(59);
			this.end.setSeconds(59);
			this.settings.put("date.end", getEndDate().getTimeInMillis());
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public ISelection getSelection()
	{
		Calendar[] dates = new Calendar[] { getStartDate(), getEndDate() };
		return new StructuredSelection(dates);
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
			if (iterator.hasNext())
			{
				Object object = iterator.next();
				if (object instanceof Calendar)
				{
					Calendar calendar = (Calendar) object;
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					start.setYear(calendar.get(Calendar.YEAR));
					start.setMonth(calendar.get(Calendar.MONTH));
					start.setDay(calendar.get(Calendar.DATE));
					settings.put("date.start", calendar.getTimeInMillis());
				}
			}
			if (iterator.hasNext())
			{
				Object object = iterator.next();
				if (object instanceof Calendar)
				{
					Calendar calendar = (Calendar) object;
					calendar.set(Calendar.HOUR_OF_DAY, 23);
					calendar.set(Calendar.MINUTE, 59);
					calendar.set(Calendar.SECOND, 59);
					calendar.set(Calendar.MILLISECOND, 999);
					end.setYear(calendar.get(Calendar.YEAR));
					end.setMonth(calendar.get(Calendar.MONTH));
					end.setDay(calendar.get(Calendar.DATE));
					settings.put("date.end", calendar.getTimeInMillis());
				}
			}
		}
	}

}
