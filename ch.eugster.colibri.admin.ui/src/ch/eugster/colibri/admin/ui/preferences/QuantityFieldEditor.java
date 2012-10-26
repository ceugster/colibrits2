package ch.eugster.colibri.admin.ui.preferences;

import java.text.NumberFormat;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

public class QuantityFieldEditor extends IntegerFieldEditor implements FocusListener
{
	private NumberFormat formatter = NumberFormat.getIntegerInstance();

	/**
	 * Creates an quantity field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public QuantityFieldEditor(String name, String labelText, Composite parent)
	{
		this(name, labelText, DoubleFieldEditor.DEFAULT_TEXT_LIMIT, parent);
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
	public QuantityFieldEditor(String name, String labelText, int textLimit, Composite parent)
	{
		super(name, labelText, textLimit, parent);
		this.formatter.setMaximumFractionDigits(0);
		this.formatter.setMinimumFractionDigits(0);
		this.getTextControl().addFocusListener(this);
	}

	   /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoad() {
        if (textField != null) {
            currentValue = Integer.valueOf(getPreferenceStore().getInt(getPreferenceName()));
            textField.setText(formatter.format(currentValue.intValue()));
            oldValue = currentValue;
        }
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoadDefault() {
        if (textField != null) {
            currentValue = Integer.valueOf(getPreferenceStore().getDefaultInt(getPreferenceName()));
            textField.setText(formatter.format(currentValue.intValue()));
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
			this.getTextControl().setText(Integer.valueOf(0).toString());
		}
	}

	public void focusLost(FocusEvent event)
	{
		try
		{
			int value = currentValue.intValue();
			this.getTextControl().setText(this.formatter.format(value));
		}
		catch (Exception e)
		{
			this.getTextControl().setText(this.formatter.format(0));
		}
	}

}
