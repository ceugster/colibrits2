/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ch.eugster.colibri.persistence.connection.Activator;

public class DatabaseWizardCurrencyPage extends WizardPage implements Listener
{
	private ComboViewer currencyViewer;

	private String[] selectedCurrency;
	
	private Text startReceiptNumber;

	public DatabaseWizardCurrencyPage(final String name)
	{
		super(name);
		this.setTitle("Referenzwährung definieren");
	}

	@Override
	public boolean canFlipToNextPage()
	{
		return false;
	}

	public Long getStartReceiptNumber()
	{
		long receiptNumber = 1L;
		try
		{
			receiptNumber = Long.valueOf(startReceiptNumber.getText()).longValue();
		}
		catch (NumberFormatException e)
		{
			receiptNumber = Long.valueOf(1L);
		}
		return receiptNumber;
	}
	
	@Override
	public void createControl(final Composite parent)
	{
		this.setTitle("Auswahl Referenzwährung");
		this.setMessage("Wählen Sie die gewünschte Referenzwährung, die verwendet werden soll.", IMessageProvider.INFORMATION);

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.widthHint = 400;

		Label label = new Label(composite, SWT.WRAP);
		label.setText("Als Referenzwährung wird die Währung bezeichnet, welche als Basis in ColibrTS verwendet wird. Diese Währung muss als Umrechnungsfaktor den Wert 1 haben. Alle anderen verwendeten Währungen müssen in Beziehung zu diese Währung gesetzt werden. D.h. ihr Umrechnungsfaktor bezieht sich auf die Referenzwährung. Die Referenzwährung sollte nie geändert werden, da sonst die Umrechnungen falsche Resultate zeitigen können.");
		label.setLayoutData(gridData);

		label = new Label(composite, SWT.NULL);
		label.setText("Referenzwährung");
		label.setLayoutData(new GridData());

		final Collection<String[]> currencies = this.loadCurrencies();

		final Combo combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.currencyViewer = new ComboViewer(combo);
		this.currencyViewer.setContentProvider(new CurrencyContentProvider());
		this.currencyViewer.setLabelProvider(new CurrencyLabelProvider());
		this.currencyViewer.setSorter(new CurrencySorter());
		this.currencyViewer.setInput(currencies.toArray(new Object[0]));
		this.currencyViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final Event e = new Event();
				DatabaseWizardCurrencyPage.this.handleEvent(e);
			}
		});
		this.currencyViewer.setSelection(new StructuredSelection(new Object[] { this.selectedCurrency }));

		label = new Label(composite, SWT.None);
		label.setText("Start Belegnummer");
		label.setLayoutData(new GridData());
		
		startReceiptNumber = new Text(composite, SWT.BORDER);
		startReceiptNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		startReceiptNumber.setText("1");
		
		this.setPageComplete(this.validatePage());

		this.setControl(composite);
	}

	public Long getSelectedCurrency()
	{
		if (this.selectedCurrency != null)
		{
			return Long.valueOf(this.selectedCurrency[0]);
		}
		return new Long(22L);
	}

	public void handleEvent(final Event event)
	{
		this.selectedCurrency = (String[]) ((StructuredSelection) this.currencyViewer.getSelection()).getFirstElement();
		this.setPageComplete(this.validatePage());
	}

	@Override
	public boolean isPageComplete()
	{
		return this.validatePage();
	}

	private Collection<String[]> loadCurrencies()
	{
		final Locale locale = Locale.getDefault();
		final Currency currency = java.util.Currency.getInstance(locale);

		BufferedReader reader = null;
		final Collection<String[]> currencies = new ArrayList<String[]>();
		try
		{
			final URL url = Activator.getDefault().getBundle().getResource("META-INF/currencies.csv");
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = reader.readLine();
			while (line != null)
			{
				final String[] cur = (line.split(";"));
				if (cur[5].equals(currency.getCurrencyCode()))
				{
					this.selectedCurrency = cur;
				}
				currencies.add(cur);
				line = reader.readLine();
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}
		return currencies;
	}

	private boolean validatePage()
	{
		try
		{
			Long.valueOf(startReceiptNumber.getText());
		}
		catch (Exception e)
		{
			return false;
		}
		return this.currencyViewer.getSelection().isEmpty() ? false : true;
	}
}
