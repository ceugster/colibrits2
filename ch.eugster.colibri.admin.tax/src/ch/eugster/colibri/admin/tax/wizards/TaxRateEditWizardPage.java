/*
 * Created on 18.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.wizards;

import java.util.Calendar;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.nebula.widgets.calendarcombo.CalendarListenerAdapter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.PercentFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.tax.TaxActivator;
import ch.eugster.colibri.admin.ui.wizards.EditWizardPage;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TaxRateEditWizardPage extends EditWizardPage<TaxRate>
{
	private Text code;

	private Text name;

	private FormattedText percentage;

	private CalendarCombo validFrom;

	private Text[] accounts;

	private TaxType[] taxTypes;

	private String message = "Bearbeiten Sie die Daten und schliessen Sie mit 'Speichern' oder 'Abbrechen' ab.";

	private String errorMessageCodeNotEmpty = "Der Code ist noch nicht definiert.";

	private String errorMessageCodeExists = "Der Code wird bereits verwendet.";

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public TaxRateEditWizardPage(final String name, final String title, final ImageDescriptor image)
	{
		super(name, title, image);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(TaxActivator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		super.dispose();
	}

	@Override
	public Composite doCreateControl(final Composite parent)
	{
		this.setMessage(this.message);
		this.setDescription("Description");

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final TaxTypeQuery query = (TaxTypeQuery) persistenceService.getServerService().getQuery(TaxType.class);
			this.taxTypes = query.selectAll(false).toArray(new TaxType[0]);
		}

		this.accounts = new Text[this.taxTypes.length];

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Code");

		final GC gc = new GC(composite.getDisplay());
		final Point p = gc.textExtent("00");
		gc.dispose();

		final GridData layoutData = new GridData();
		layoutData.widthHint = p.x;

		this.code = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.code.setLayoutData(layoutData);
		this.code.addModifyListener(this);
		this.code.addVerifyListener(new VerifyListener()
		{
			public void verifyText(final VerifyEvent e)
			{
				e.text = e.text.toUpperCase();
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setText("Bezeichnung");

		this.name = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.name.addModifyListener(this);

		label = new Label(composite, SWT.LEFT);
		label.setText("Prozentsatz");
		label.setLayoutData(new GridData());

		this.percentage = new FormattedText(composite, SWT.BORDER | SWT.RIGHT);
		this.percentage.setFormatter(new PercentFormatter(TaxActivator.PATTERN_PERCENTAGE));
		this.percentage.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.percentage.getControl().addModifyListener(this);

		label = new Label(composite, SWT.RIGHT);
		label.setText("Gültig ab");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.HOUR, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		startDate.add(Calendar.DATE, 1);

		this.validFrom = new CalendarCombo(composite, SWT.BORDER | SWT.RIGHT);
		this.validFrom.setDate(startDate.getTime());
		this.validFrom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.validFrom.addCalendarListener(new CalendarListenerAdapter()
		{
			@Override
			public void dateChanged(final Calendar calendar)
			{
				final Event e = new Event();
				e.widget = TaxRateEditWizardPage.this.validFrom;
				final SelectionEvent event = new SelectionEvent(e);
				TaxRateEditWizardPage.this.widgetSelected(event);
			}
		});
		this.validFrom.addModifyListener(this);

		for (int i = 0; i < this.accounts.length; i++)
		{
			label = new Label(composite, SWT.NONE);
			label.setText("Konto " + this.taxTypes[i].getName());

			this.accounts[i] = new Text(composite, SWT.BORDER | SWT.SINGLE);
			this.accounts[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.accounts[i].addModifyListener(this);
		}

		return composite;
	}

	@Override
	public void getFieldValues()
	{
		final TaxRate taxRate = this.entity;
		taxRate.setCode(this.code.getText());
		taxRate.setName(this.name.getText());
		if (this.getEntity().getId() == null)
		{
			// for (int i = 0; i < this.taxTypes.length; i++)
			// {
			// Tax tax = new Tax(taxRate, this.taxTypes[i]);
			// tax.setAccount(this.accounts[i].getText());
			// tax.set
			// }
			//
			// taxRatepercentage.setValue(this.)
		}
	}

	@Override
	public void setFieldValues()
	{
		this.code.setText((this.entity).getCode());
		this.name.setText((this.entity).getName());
	}

	@Override
	protected boolean checkInput()
	{
		final String code = this.code.getText();
		if (code.equals(""))
		{
			this.setErrorMessage(this.errorMessageCodeNotEmpty);
			return false;
		}

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final TaxRateQuery query = (TaxRateQuery) persistenceService.getServerService().getQuery(TaxRate.class);
			if (!query.isCodeUnique(code, this.entity.getId()))
			{
				this.setErrorMessage(this.errorMessageCodeExists);
				return false;
			}
		}
		else
		{
			return false;
		}

		if (this.validFrom.getDate() == null)
		{
			this.setErrorMessage("Das eingegebene Datum ist ungültig.");
			return false;
		}

		return true;
	}

	@Override
	protected TaxRate getNewEntity()
	{
		return TaxRate.newInstance();
	}
}
