/*
 * Created on 11.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.numeric;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.JLabel;

import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.ClearAction;
import ch.eugster.colibri.client.ui.actions.DeleteAction;
import ch.eugster.colibri.client.ui.actions.EnterAction;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;

public class ValueDisplay extends JLabel implements ActionListener, PropertyChangeListener, StateChangeListener
{
	public static final long serialVersionUID = 0l;

	private String value = "";

	private UserPanel userPanel;
	
	private UserPanel.State state;

//	private ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
	
//	private EventAdmin eventAdmin;
	
	public ValueDisplay(final UserPanel userPanel, final String value)
	{
		super(value);
		this.userPanel = userPanel;
		this.userPanel.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(final KeyEvent e)
			{
				ValueDisplay.this.keyPressed(e);
			}
		});
		this.initDisplay();
		this.addPropertyChangeListener(this);
//		this.eventAdminTracker.open();
//		this.eventAdmin = eventAdminTracker.getService();
	}
	
	public void finalize()
	{
//		eventAdminTracker.close();
	}

	public void actionPerformed(final ActionEvent e)
	{
		if (e.getActionCommand().equals(EnterAction.ACTION_COMMAND))
		{
		}
		else if (e.getActionCommand().equals(ClearAction.ACTION_COMMAND))
		{
			this.initDisplay();
		}
		else if (e.getActionCommand().equals(DeleteAction.ACTION_COMMAND))
		{
			if (!this.value.isEmpty())
			{
				this.setValue(this.value.substring(0, this.value.length() - 1));
			}
		}
		else if (e.getActionCommand().equals(NumericPadPanel.TEXT_DOT))
		{
			this.addValue(e.getActionCommand());
		}
		else
		{
			try
			{
				Integer.parseInt(e.getActionCommand());
				this.addValue(e.getActionCommand());
			}
			catch (final NumberFormatException nfe)
			{
			}
		}
	}

	public double getAmount()
	{
		double amount = 0d;

		try
		{
			amount = Double.parseDouble(this.readAndInitDisplay());
		}
		catch (final NumberFormatException e)
		{
			amount = 0d;
		}
		return amount;
	}

	public double getDiscount()
	{
		double discount = 0d;

		try
		{
			discount = Double.parseDouble(this.readAndInitDisplay());
			discount = discount / 100d;
		}
		catch (final NumberFormatException e)
		{
			discount = 0d;
		}
		return discount;
	}

	public int getPosLogin()
	{
		int posLogin = 0;

		try
		{
			posLogin = Integer.parseInt(this.readAndInitDisplay());
		}
		catch (final NumberFormatException e)
		{
			posLogin = 0;
		}
		return posLogin;
	}

	public int getQuantity()
	{
		int quantity = 0;

		try
		{
			quantity = Integer.parseInt(this.readAndInitDisplay());
		}
		catch (final NumberFormatException e)
		{
			quantity = 0;
		}

		return quantity;
	}

	public String getValue()
	{
		return this.readAndInitDisplay();
	}

	public long getValueAsLong()
	{
		long value = 0l;
		try
		{
			value = Long.parseLong(this.readAndInitDisplay());
		}
		catch (final NumberFormatException e)
		{
		}
		return value;
	}

	public void keyPressed(final KeyEvent event)
	{
		if ((event.getKeyChar() == '.') && this.value.contains("."))
		{
			return;
		}

		if ("EeGgCcDd0123456789.".indexOf(event.getKeyChar()) > -1)
		{
			this.addChar(event.getKeyChar());
		}
		else if ((event.getKeyCode() == KeyEvent.VK_CLEAR) || (event.getKeyCode() == KeyEvent.VK_DELETE))
		{
			this.initDisplay();
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource().equals(this) && event.getPropertyName().equals("value"))
		{
			if (this.state.equals(UserPanel.State.LOCKED))
			{
				final char[] chars = new char[this.value.length()];
				Arrays.fill(chars, '*');
				this.setText(new String(chars));
			}
			else
			{
				this.setText(this.value);
			}
		}
	}

	public String readAndInitDisplay()
	{
		final String value = this.readDisplay();
		this.initDisplay();
		return value;
	}

	public String readDisplay()
	{
		return this.value;
	}

	public void setValue(final String value)
	{
		final String oldText = this.value;
		this.value = value;
		// setText(value);
		this.firePropertyChange("value", oldText, value);
	}

	@Override
	public void stateChange(final StateChangeEvent event)
	{
		this.state = event.getNewState();
	}

	public double testAmount()
	{
		Activator.getDefault().log(LogService.LOG_INFO, "Enter ValueDisplay.testAmount()");
		double amount = 0d;

		try
		{
			amount = Double.parseDouble(this.readDisplay());
		}
		catch (final NumberFormatException e)
		{
			amount = 0d;
		}
		Activator.getDefault().log(LogService.LOG_INFO, "Exit ValueDisplay.testAmount()");
		return amount;
	}

	public Barcode testBarcode()
	{
		ServiceTracker<BarcodeVerifier, BarcodeVerifier> tracker = new ServiceTracker<BarcodeVerifier, BarcodeVerifier>(Activator.getDefault().getBundle().getBundleContext(), BarcodeVerifier.class, null);
		tracker.open();
		try
		{
			BarcodeVerifier[] verifiers = tracker.getServices(new BarcodeVerifier[0]);
			if (verifiers != null)
			{
				for (BarcodeVerifier verifier : verifiers)
				{
					Barcode barcode = verifier.verify(this.readDisplay());
					if (barcode != null)
					{
						return barcode;
					}
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return null;
	}

	public double testDiscount()
	{
		double discount = 0d;

		try
		{
			discount = Double.parseDouble(this.readDisplay());
			discount = discount / 100d;
		}
		catch (final NumberFormatException e)
		{
			discount = 0d;
		}
		return discount;
	}

	public int testQuantity()
	{
		int quantity = 0;

		try
		{
			quantity = Integer.parseInt(this.readDisplay());
		}
		catch (final NumberFormatException e)
		{
			quantity = 0;
		}

		return quantity;
	}

	private void addChar(final char c)
	{
		final String oldText = this.value;
		this.value = this.value + c;
		// setText(value);
		this.firePropertyChange("value", oldText, this.value);
	}

	private void addValue(final String text)
	{
		if (text.contains("."))
		{
			if (this.getText().contains("."))
			{
				return;
			}
		}
		/*
		 * Important do not remove second "0" from String below it is essential for Button "00"!
		 */
		if ("00123456789.".contains(text))
		{
			final String oldText = this.value;
			this.value = this.value.concat(text);
			// setText(value);
			this.firePropertyChange("value", oldText, this.value);
		}
	}

	private void initDisplay()
	{
		final String oldValue = this.value.toString();
		this.value = "";
		this.firePropertyChange("value", oldValue, this.value);
	}
}
