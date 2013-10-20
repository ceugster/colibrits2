package ch.eugster.colibri.client.ui.panels;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Vector;

import ch.eugster.colibri.client.ui.Disposable;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.panels.ProfilePanel;

public abstract class MainPanel extends ProfilePanel implements TitleProvider, KeyEventDispatcher, Disposable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Collection<KeyListener> keyListeners = new Vector<KeyListener>();

	// protected Collection<ReceiptChangeListener> receiptChangeListeners = new
	// Vector<ReceiptChangeListener>();

	public MainPanel(final Profile profile)
	{
		super(profile);
	}

	@Override
	public void addKeyListener(final KeyListener listener)
	{
		keyListeners.add(listener);
	}

	@Override
	public boolean dispatchKeyEvent(final KeyEvent event)
	{
		if (event.getID() == KeyEvent.KEY_PRESSED)
		{
			fireKeyEvent(event);
			return true;
		}
		return false;
	}

	public void fireKeyEvent(final KeyEvent event)
	{
		final KeyListener[] listeners = keyListeners.toArray(new KeyListener[0]);
		for (final KeyListener listener : listeners)
		{
			listener.keyPressed(event);
		}
	}

//	@Override
//	public abstract String getTitle();

	public abstract void initFocus();

	@Override
	public void removeKeyListener(final KeyListener listener)
	{
		keyListeners.remove(listener);
	}
}
