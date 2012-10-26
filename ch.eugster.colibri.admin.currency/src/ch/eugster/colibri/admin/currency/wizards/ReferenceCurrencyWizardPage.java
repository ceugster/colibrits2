/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.currency.wizards;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.currency.Activator;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.queries.CurrencyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class ReferenceCurrencyWizardPage extends WizardPage implements Listener
{
	private ReferenceCurrencyWizard wizard;

	private ComboViewer currency;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public ReferenceCurrencyWizardPage(final ReferenceCurrencyWizard wizard)
	{
		super("referenceCurrencyWizardPage"); //$NON-NLS-1$
		this.wizard = wizard;
		this.setTitle("Referenzwährung bestimmen");

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void createControl(final Composite parent)
	{
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NULL);
		label.setText("Referenzwährung");
		label.setLayoutData(new GridData());

		final Combo combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.currency = new ComboViewer(combo);
		this.currency.setContentProvider(new CurrencyContentProvider());
		this.currency.setLabelProvider(new CurrencyLabelProvider());
		this.currency.setSorter(new CurrencySorter());
		this.currency.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.currency.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final Event e = new Event();
				ReferenceCurrencyWizardPage.this.handleEvent(e);
			}
		});
		final PersistenceService persistenceService = this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final CurrencyQuery query = (CurrencyQuery) persistenceService.getServerService().getQuery(Currency.class);
			final Collection<Currency> currencies = query.selectAll(true);
			this.currency.setInput(currencies.toArray(new Currency[0]));
		}

		final GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(layoutData);
		label.setText("Bitte beachten Sie: das Ändern der Referenzwährung führt, wenn bereits Belege erfasst sind, zu unerwarteten Resultaten!");

		this.setControl(composite);

		this.refresh();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
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
		this.wizard.getSettings().setReferenceCurrency((Currency) ssel.getFirstElement());
	}

	protected void refresh()
	{
		if (this.wizard.getSettings().getReferenceCurrency() == null)
		{
			final java.util.Currency currency = java.util.Currency.getInstance(Locale.getDefault());
			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
			if (persistenceService != null)
			{
				final CurrencyQuery query = (CurrencyQuery) persistenceService.getServerService().getQuery(Currency.class);
				final Currency curr = query.selectByCode(currency.getCurrencyCode());
				this.wizard.getSettings().setReferenceCurrency(curr);
			}
		}
		this.currency.setSelection(new StructuredSelection(this.wizard.getSettings().getReferenceCurrency()));
	}

	private boolean validatePage()
	{
		if (this.currency.getSelection().isEmpty())
		{
			return false;
		}

		return true;
	}
}
