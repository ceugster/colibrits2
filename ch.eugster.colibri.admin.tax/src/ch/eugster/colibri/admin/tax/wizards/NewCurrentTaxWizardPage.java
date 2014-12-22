/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.wizards;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.nebula.widgets.calendarcombo.CalendarListenerAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.tax.TaxActivator;
import ch.eugster.colibri.admin.ui.wizards.AbstractEntityWizardPage;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.ServerService;

public class NewCurrentTaxWizardPage extends AbstractEntityWizardPage
{
	private final NewCurrentTax[] newCurrentTaxes;

	private Text[] newCurrentTaxPercentages;

	private CalendarCombo newCurrentTaxValidFrom;

	private final NumberFormat pf = NumberFormat.getPercentInstance();

	private final NumberFormat nf = NumberFormat.getNumberInstance();

	private final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

	public NewCurrentTaxWizardPage(final String name, final String title, TaxType[] taxTypes, TaxRate[] taxRates)
	{
		super(name, title);
		nf.setMinimumFractionDigits(0);
		nf.setMaximumFractionDigits(2);
		pf.setMinimumFractionDigits(0);
		pf.setMaximumFractionDigits(2);

		this.newCurrentTaxes = new NewCurrentTax[taxRates.length];
		for (int i = 0; i < taxRates.length; i++)
		{
			this.newCurrentTaxes[i] = new NewCurrentTax(taxTypes, taxRates[i]);
		}
	}

	public boolean store()
	{
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(TaxActivator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		PersistenceService service = (PersistenceService) tracker.getService();
		if (service == null)
		{
			MessageDialog
					.openInformation(this.getShell(), "Datenbankverbindung",
							"Die Änderungen können nicht gespeichert werden. Die Datenbankverbindung kann nicht hergestellt werden.");
			return false;
		}
		else
		{
			ServerService serverService = service.getServerService();
			for (Text newCurrentTaxPercentage : newCurrentTaxPercentages)
			{
				NewCurrentTax newCurrentTax = (NewCurrentTax) newCurrentTaxPercentage.getData();
				newCurrentTax.update(serverService);
			}
			return true;
		}
	}

	@Override
	public void createControl(final Composite parent)
	{
		final int columnCount = 3;

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(columnCount, false));

		this.nf.setMaximumFractionDigits(3);
		this.nf.setMinimumFractionDigits(1);

		this.newCurrentTaxPercentages = new Text[this.newCurrentTaxes.length];

		Label label = new Label(composite, SWT.LEFT);
		label.setText("Aktuelle Steuersätze");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(composite, SWT.RIGHT);
		label.setText("Gültig seit");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(composite, SWT.RIGHT);
		label.setText(this.df.format(this.newCurrentTaxes[0].getOldCurrentTax().getValidFrom()));
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(composite, SWT.LEFT);
		label.setText("Neue Steuersätze");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(composite, SWT.RIGHT);
		label.setText("Gültig ab");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.HOUR, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		startDate.add(Calendar.DATE, 1);

		this.newCurrentTaxValidFrom = new CalendarCombo(composite, SWT.BORDER | SWT.RIGHT);
		this.newCurrentTaxValidFrom.setDisallowBeforeDate(startDate);
		this.newCurrentTaxValidFrom.setDate(startDate.getTime());
		this.newCurrentTaxValidFrom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.newCurrentTaxValidFrom.addCalendarListener(new CalendarListenerAdapter()
		{
			@Override
			public void dateChanged(final Calendar calendar)
			{
				final Event e = new Event();
				NewCurrentTaxWizardPage.this.handleEvent(e);
			}
		});
		this.newCurrentTaxValidFrom.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent event)
			{
				final Event e = new Event();
				NewCurrentTaxWizardPage.this.handleEvent(e);
			}
		});

		final GridData layoutData = new GridData();
		layoutData.horizontalSpan = columnCount;

		label = new Label(composite, SWT.LEFT);
		label.setLayoutData(layoutData);

		label = new Label(composite, SWT.LEFT);
		label.setText("Mehrwertsteuersatzart");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(composite, SWT.RIGHT);
		label.setText("Aktueller Satz");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(composite, SWT.RIGHT);
		label.setText("Neuer Satz");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (int i = 0; i < this.newCurrentTaxes.length; i++)
		{
			label = new Label(composite, SWT.LEFT);
			label.setText(this.newCurrentTaxes[i].getTaxRate().getName());
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			label = new Label(composite, SWT.RIGHT);
			label.setText(this.pf.format(this.newCurrentTaxes[i].getOldCurrentTax().getPercentage()));
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.newCurrentTaxPercentages[i] = new Text(composite, SWT.BORDER | SWT.RIGHT);
			this.newCurrentTaxPercentages[i].setData(this.newCurrentTaxes[i]);
			this.newCurrentTaxPercentages[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.newCurrentTaxPercentages[i].addFocusListener(new FocusListener() 
			{
				@Override
				public void focusGained(FocusEvent e) 
				{
					Text text = (Text) e.getSource();
					if (!text.getText().isEmpty())
					{
						String s = text.getText().replaceAll("%", "");
						double value = 0d;
						try
						{
							value = Double.parseDouble(s);
							text.setText(nf.format(new Double(value).doubleValue()));
						}
						catch (NumberFormatException nfe)
						{
						nfe.printStackTrace();	
						}
					}
				}

				@Override
				public void focusLost(FocusEvent e) 
				{
					Text text = (Text) e.getSource();
					if (!text.getText().isEmpty())
					{
						try
						{
							double value = Double.parseDouble(text.getText());
							if (value != 0d)
							{
								value = value / 100;
							}
							text.setText(pf.format(value));
						}
						catch (NumberFormatException nfe)
						{
							
						}
					}
				}});
			this.newCurrentTaxPercentages[i].addModifyListener(new ModifyListener()
			{
				@Override
				public void modifyText(final ModifyEvent event)
				{
					final Text text = (Text) event.getSource();
					try
					{
						double value = Double.parseDouble(text.getText());
						if (value != 0d)
						{
							value = value / 100;
						}
						((NewCurrentTax) text.getData()).setNewPercentage(value);
						final Event e = new Event();
						NewCurrentTaxWizardPage.this.handleEvent(e);
					}
					catch (NumberFormatException e)
					{
						NewCurrentTaxWizardPage.this.setPageComplete(false);
					}
				}
			});
		}

		this.setControl(composite);
	}

	@Override
	protected boolean validatePage()
	{
		if (this.newCurrentTaxValidFrom.getDate() == null)
		{
			return false;
		}

		return true;
	}

	@Override
	protected void refresh()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub

	}

	public class NewCurrentTax
	{
		private final CurrentTax[] newCurrentTaxes;

		private final TaxRate taxRate;

		private double newPercentage = 0d;

		public NewCurrentTax(TaxType[] taxTypes, final TaxRate taxRate)
		{
			this.taxRate = taxRate;

			Collection<CurrentTax> currentTaxes = new ArrayList<CurrentTax>();
			Collection<Tax> taxes = taxRate.getTaxes();
			for (Tax tax : taxes)
			{
				for (TaxType taxType : taxTypes)
				{
					if (taxType.getId().equals(tax.getTaxType().getId()))
					{
						CurrentTax currentTax = CurrentTax.newInstance(tax);
						currentTaxes.add(currentTax);
					}
				}
			}
			newCurrentTaxes = currentTaxes.toArray(new CurrentTax[0]);
		}

		public void update(ConnectionService service)
		{
			for (CurrentTax currentTax : newCurrentTaxes)
			{
				currentTax.setPercentage(newPercentage);
				currentTax.setValidFrom(Long.valueOf(newCurrentTaxValidFrom.getDate().getTimeInMillis()));
				currentTax.getTax().addCurrentTax(currentTax);
				Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
				calendar.setTimeInMillis(currentTax.getValidFrom().longValue());
				if (calendar.getTime().before(GregorianCalendar.getInstance(Locale.getDefault()).getTime()))
				{
					currentTax.getTax().setCurrentTax(currentTax);
				}
				try
				{
					currentTax.setTax((Tax) service.merge(currentTax.getTax()));
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					IStatus status = new Status(IStatus.ERROR, TaxActivator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
					ErrorDialog.openError(NewCurrentTaxWizardPage.this.getShell(), "Fehler", "Der neue Mehrwertsteuersatz konnte nicht gespeichert werden.", status);
				}
			}
		}

		public void setNewPercentage(final double newPercentage)
		{
			this.newPercentage = newPercentage;
		}

		public TaxRate getTaxRate()
		{
			return this.taxRate;
		}

		public CurrentTax getOldCurrentTax()
		{
			return newCurrentTaxes[0].getTax().getCurrentTax();
		}
	}

}
