/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.wizards;

import java.util.Collection;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.salespoint.Activator;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class StockWizardPage extends WizardPage implements Listener
{
	private StockWizard wizard;

	private ComboViewer currency;

	private FormattedText amount;

	private Button variable;

	public StockWizardPage(final StockWizard wizard)
	{
		super("stockWizardPage");
		this.wizard = wizard;
		this.setTitle(wizard.getStock().getId() == null ? "Neuer Kassenstock" : wizard.getStock().getSalespoint().getName() + ": "
				+ wizard.getStock().getPaymentType().getCurrency().format());
	}

	@Override
	public void createControl(final Composite parent)
	{
		final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NULL);
		label.setText("Währung");
		label.setLayoutData(new GridData());

		final Combo combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.currency = new ComboViewer(combo);
		this.currency.setContentProvider(new PaymentTypeContentProvider());
		this.currency.setLabelProvider(new PaymentTypeLabelProvider());
		this.currency.setSorter(new PaymentTypeSorter());
		this.currency.setFilters(new ViewerFilter[] { new DeletedPaymentTypeFilter() });

		final PersistenceService persistenceService = (PersistenceService) tracker.getService();
		if (persistenceService != null)
		{
			final PaymentTypeQuery queryService = (PaymentTypeQuery) persistenceService.getServerService().getQuery(PaymentType.class);
			if (queryService != null)
			{
				final Collection<PaymentType> pts = queryService.selectByGroup(PaymentTypeGroup.CASH);
				final Salespoint salespoint = this.wizard.getStock().getSalespoint();
				final Collection<Stock> stocks = salespoint.getStocks();
				for (final Stock stock : stocks)
				{
					if (!stock.isDeleted())
					{
						if (!stock.getPaymentType().equals(this.wizard.getStock().getPaymentType()))
						{
							if (pts.contains(stock.getPaymentType()))
							{
								pts.remove(stock.getPaymentType());
							}
						}
					}
				}
				final PaymentType[] paymentTypes = pts.toArray(new PaymentType[0]);
				this.currency.setInput(paymentTypes);
			}
		}

		this.currency.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final Event e = new Event();
				StockWizardPage.this.handleEvent(e);
			}
		});

		label = new Label(composite, SWT.NULL);
		label.setText("Betrag");
		label.setLayoutData(new GridData());

		this.amount = new FormattedText(composite, SWT.BORDER | SWT.RIGHT);
		this.amount.setFormatter(new NumberFormatter("#####0.00", "###,##0.00"));
		this.amount.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.amount.getControl().addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent event)
			{
				final Event e = new Event();
				StockWizardPage.this.handleEvent(e);
			}
		});

		label = new Label(composite, SWT.NULL);
		label.setText("");
		label.setLayoutData(new GridData());

		this.variable = new Button(composite, SWT.CHECK);
		this.variable.setText("Variabler Kassenstock");
		this.variable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.variable.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				this.widgetSelected(event);
			}

			public void widgetSelected(final SelectionEvent event)
			{
				final Event e = new Event();
				StockWizardPage.this.handleEvent(e);
			}
		});

		tracker.close();

		this.setControl(composite);

		this.refresh();
	}

	public void handleEvent(final Event event)
	{
		this.setPageComplete(this.validatePage());
	}

	@Override
	public boolean isPageComplete()
	{
		return this.validatePage();
	}

	public void update()
	{
		final StructuredSelection ssel = (StructuredSelection) this.currency.getSelection();
		final Stock stock = this.wizard.getStock();

		stock.setPaymentType((PaymentType) ssel.getFirstElement());

		final String value = this.amount.getControl().getText();
		stock.setAmount(Double.parseDouble(value));

		stock.setVariable(this.variable.getSelection());
	}

	protected void refresh()
	{
		if (this.wizard.getStock().getPaymentType() == null)
		{
			final PaymentType[] paymentTypes = (PaymentType[]) this.currency.getInput();
			if (paymentTypes.length == 0)
			{
				this.currency.setSelection(new StructuredSelection(new PaymentType[0]));
			}
			else
			{
				this.currency.setSelection(new StructuredSelection(paymentTypes[0]));
			}
		}
		else
		{
			this.currency.setSelection(new StructuredSelection(new PaymentType[] { this.wizard.getStock().getPaymentType() }));
		}

		this.amount.setValue(new Double(this.wizard.getStock().getAmount()));

		this.variable.setSelection(this.wizard.getStock().isVariable());
	}

	private boolean validatePage()
	{
		if (this.currency.getSelection().isEmpty())
		{
			return false;
		}
		else
		{
			final StructuredSelection ssel = (StructuredSelection) this.currency.getSelection();
			if (!(ssel.getFirstElement() instanceof PaymentType))
			{
				return false;
			}
		}

		return true;
	}
}
