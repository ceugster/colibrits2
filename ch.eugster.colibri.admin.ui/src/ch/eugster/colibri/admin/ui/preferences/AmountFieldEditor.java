package ch.eugster.colibri.admin.ui.preferences;

import java.text.NumberFormat;
import java.util.Currency;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

public class AmountFieldEditor extends DoubleFieldEditor implements FocusListener
{
	private NumberFormat formatter = NumberFormat.getNumberInstance();

	/**
	 * Creates an double field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public AmountFieldEditor(String name, String labelText, Currency currency, Composite parent)
	{
		this(name, labelText, currency, DoubleFieldEditor.DEFAULT_TEXT_LIMIT, parent);
	}

	/**
	 * Creates an integer field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 * @param textLimit
	 *            the maximum number of characters in the text.
	 */
	public AmountFieldEditor(String name, String labelText, Currency currency, int textLimit, Composite parent)
	{
		super(name, labelText, textLimit, parent);
		this.formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
		this.formatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
		this.getTextControl().addFocusListener(this);
	}

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoad() {
        if (textField != null) {
            currentValue = Double.valueOf(getPreferenceStore().getDouble(getPreferenceName()));
            textField.setText(formatter.format(currentValue.doubleValue()));
            oldValue = currentValue;
        }
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoadDefault() {
        if (textField != null) {
            currentValue = Double.valueOf(getPreferenceStore().getDefaultDouble(getPreferenceName()));
            textField.setText(formatter.format(currentValue.doubleValue()));
        }
        valueChanged();
    }

	@Override
	public void focusGained(FocusEvent e)
	{
		try
		{
			this.getTextControl().setText(currentValue.toString());
		}
		catch (Exception ex)
		{
			this.getTextControl().setText(Double.valueOf(0d).toString());
		}
	}

	public void focusLost(FocusEvent event)
	{
		try
		{
			double value = currentValue.doubleValue();
			this.getTextControl().setText(this.formatter.format(value));
		}
		catch (Exception e)
		{
			this.getTextControl().setText(this.formatter.format(0d));
		}
	}

}
