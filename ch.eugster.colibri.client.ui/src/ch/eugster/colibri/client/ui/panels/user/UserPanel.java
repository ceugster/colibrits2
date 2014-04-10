/*
 * Created on 16.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import se.datadosen.component.buttonbar.PercentLayout;
import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.BackAction;
import ch.eugster.colibri.client.ui.actions.BackFromSettleAction;
import ch.eugster.colibri.client.ui.actions.ClearAction;
import ch.eugster.colibri.client.ui.actions.DiscountAction;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.DisposeEvent;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.ReceiptChangeEvent;
import ch.eugster.colibri.client.ui.events.ReceiptChangeMediator;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.events.StateChangeProvider;
import ch.eugster.colibri.client.ui.panels.MainPanel;
import ch.eugster.colibri.client.ui.panels.MainTabbedPane;
import ch.eugster.colibri.client.ui.panels.user.parking.ParkedReceiptListPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.function.FunctionPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.info.InfoPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.info.payment.PaymentPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.info.position.PositionDetailPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.info.position.PositionListPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.numeric.NumericPadPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.numeric.ValueDisplay;
import ch.eugster.colibri.client.ui.panels.user.pos.selection.SelectionPanel;
import ch.eugster.colibri.client.ui.panels.user.receipts.CurrentReceiptListPanel;
import ch.eugster.colibri.client.ui.panels.user.settlement.CoinCounterPanel;
import ch.eugster.colibri.client.ui.views.ClientView;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Profile.PanelType;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.ui.actions.BasicAction;

public class UserPanel extends MainPanel implements StateChangeProvider, StateChangeListener, PropertyChangeListener,
		ActionListener
{
	private final Collection<DisposeListener> disposeListeners = new Vector<DisposeListener>();

	public static final long serialVersionUID = 0l;

	private static final String PANEL_PARKED_RECEIPTS = "panel.parked.receipts";

	private static final String PANEL_CURRENT_RECEIPTS = "panel.current.receipts";

	private static final String PANEL_LEFT_POS = "panel.left.pos";

	private static final String PANEL_COIN_COUNTER = "panel.coin.counter";

	private static final String PANEL_RIGHT_POS = "panel.right.pos";

	private JPanel leftPanel;

	private JPanel rightPanel;

	private JPanel leftPosPanel;

	private JPanel rightPosPanel;

	private NumericPadPanel numericPadPanel;

	private SelectionPanel selectionPanel;

	private ValueDisplay valueDisplay;

	private PositionListPanel positionListPanel;

	private PositionDetailPanel positionDetailPanel;

	private PaymentPanel paymentPanel;

	private CurrentReceiptListPanel currentReceiptListPanel;

	private ParkedReceiptListPanel parkedReceiptListPanel;

	private CoinCounterPanel coinCounterPanel;

	private final User user;

	private ReceiptWrapper receiptWrapper;

	private PositionWrapper positionWrapper;

	private PaymentWrapper paymentWrapper;

	private State oldState;

	private State currentState;

	private int restitutionPrintCount;

	private MainTabbedPane mainTabbedPane;
	
	private final Collection<StateChangeListener> stateChangeListeners = new ArrayList<StateChangeListener>();

	private final Map<Class<? extends BasicAction>, Collection<ActionListener>> actionListeners = new HashMap<Class<? extends BasicAction>, Collection<ActionListener>>();

	public UserPanel(MainTabbedPane mainTabbedPane, final User user)
	{
		super(mainTabbedPane.getSalespoint().getProfile());
		this.mainTabbedPane = mainTabbedPane;
		this.user = user;
		this.init();
	}

	public MainTabbedPane getMainTabbedPane()
	{
		return mainTabbedPane;
	}
	
	public int getRestitutionPrintCount()
	{
		return restitutionPrintCount < 1 ? 1 : restitutionPrintCount;
	}

	public void setRestitutionPrintCount(int count)
	{
		this.restitutionPrintCount = count;
	}
	
	public void prepareReceipt()
	{
		Receipt oldReceipt = this.receiptWrapper.getReceipt();
		Receipt newReceipt = this.receiptWrapper.prepareReceipt();
		this.positionWrapper.preparePosition(newReceipt);
		this.paymentWrapper.preparePayment(newReceipt);
		this.receiptWrapper.fireReceiptChangeEvent(new ReceiptChangeEvent(oldReceipt, newReceipt));
	}
	
	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (event.getActionCommand().equals(ClearAction.ACTION_COMMAND))
		{
			this.getReceiptWrapper().getReceipt().setCustomer(null);
		}
		else if (event.getActionCommand().equals(BackAction.ACTION_COMMAND))
		{
			this.fireStateChange(new StateChangeEvent(this.getCurrentState(), this.oldState));
		}
		else if (event.getActionCommand().equals(BackFromSettleAction.ACTION_COMMAND))
		{
			if (this.getMainTabbedPane().settlementRequired())
			{
				this.fireStateChange(new StateChangeEvent(this.getCurrentState(), State.MUST_SETTLE));
			}
			else
			{
				this.fireStateChange(new StateChangeEvent(this.getCurrentState(), this.oldState));
			}
		}
		else if (event.getActionCommand().equals(DiscountAction.ACTION_COMMAND))
		{
			final double discount = this.getValueDisplay().getDiscount();
			if (this.getPositionWrapper().isPositionEmpty())
			{
				if (this.getReceiptWrapper().getReceipt().getPositions().size() > 0)
				{
					final String title = "Rabattvergabe";
					final String message = "Soll der gewählte Rabatt auf alle vorhandenen Positionen angewendet werden?";
					final int messageType = ch.eugster.colibri.client.ui.dialogs.MessageDialog.TYPE_QUESTION;
					if (MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.profile, title, message,
							messageType) == MessageDialog.BUTTON_YES)
					{
						this.getReceiptWrapper().setDiscount(discount);
						Position[] positions = this.getReceiptWrapper().getReceipt().getPositions().toArray(new Position[0]);
						if (positions.length > 0)
						{
							positions[positions.length - 1].setDiscount(discount);
						}
						return;
					}
				}
			}
			this.getPositionWrapper().getPosition().setDiscount(discount);
		}
	}

	public void addActionListener(final Class<? extends BasicAction> clazz, final ActionListener actionListener)
	{
		Collection<ActionListener> actionListeners = this.actionListeners.get(clazz);
		if (actionListeners == null)
		{
			actionListeners = new Vector<ActionListener>();
			this.actionListeners.put(clazz, actionListeners);
		}
		actionListeners.add(actionListener);
	}

	public void addDisposeListener(final DisposeListener listener)
	{
		this.disposeListeners.add(listener);
	}

	@Override
	public void addStateChangeListener(final StateChangeListener listener)
	{
		if (listener != null)
		{
			if (!this.stateChangeListeners.contains(listener))
			{
				this.stateChangeListeners.add(listener);
			}
		}
	}

	@Override
	public void dispose()
	{
		this.stateChangeListeners.clear();

	}

	@Override
	public void fireStateChange(final StateChangeEvent event)
	{
		this.oldState = event.getOldState();
		this.currentState = event.getNewState();

		final StateChangeListener[] listeners = this.stateChangeListeners.toArray(new StateChangeListener[0]);
		for (final StateChangeListener listener : listeners)
		{
			listener.stateChange(event);
		}
	}

	public UserPanel.State getCurrentState()
	{
		return this.currentState;
	}

	public NumericPadPanel getNumericPadPanel()
	{
		return this.numericPadPanel;
	}

	public UserPanel.State getOldState()
	{
		return this.oldState;
	}

	public PaymentPanel getPaymentPanel()
	{
		return this.paymentPanel;
	}

	public PaymentWrapper getPaymentWrapper()
	{
		return this.paymentWrapper;
	}

	public PositionDetailPanel getPositionDetailPanel()
	{
		return this.positionDetailPanel;
	}

	public PositionListPanel getPositionListPanel()
	{
		return this.positionListPanel;
	}

	public PositionWrapper getPositionWrapper()
	{
		return this.positionWrapper;
	}

	public ReceiptWrapper getReceiptWrapper()
	{
		return this.receiptWrapper;
	}

	public Salespoint getSalespoint()
	{
		return mainTabbedPane.getSalespoint();
	}

	public void setSalespoint(Salespoint salespoint)
	{
		ClientView.getClientView().updateSalespoint(salespoint);
		this.prepareReceipt();
	}

	@Override
	public String getTitle()
	{
		final StringBuilder title = new StringBuilder("Benutzer: ");
		title.append(this.user.getUsername());
		return title.toString();
	}

	public User getUser()
	{
		return this.user;
	}

	public ValueDisplay getValueDisplay()
	{
		return this.valueDisplay;
	}

	@Override
	public void initFocus()
	{
		this.numericPadPanel.getEnterButton().requestFocus();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getPropertyName().equals("customerCode"))
		{
			final UIJob uiJob = new UIJob("set text")
			{
				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor)
				{
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setText(UserPanel.this.getTitle());
					return Status.OK_STATUS;
				}
			};
			uiJob.schedule();
		}
	}

	public boolean readyToLogout()
	{
		return this.getReceiptWrapper().isReceiptEmpty();
	}

	public void removeActionListener(final Class<? extends BasicAction> clazz, final ActionListener actionListener)
	{
		final Collection<ActionListener> actionListeners = this.actionListeners.get(clazz);
		if (actionListeners != null)
		{
			actionListeners.remove(actionListener);
		}
	}

	public void removeDisposeListener(final DisposeListener listener)
	{
		this.disposeListeners.remove(listener);
	}

	@Override
	public void removeStateChangeListener(final StateChangeListener listener)
	{
		if (listener != null)
		{
			if (this.stateChangeListeners.contains(listener))
			{
				this.stateChangeListeners.remove(listener);
			}
		}
	}

	public void setPaymentPanel(final PaymentPanel panel)
	{
		this.paymentPanel = panel;
	}

	public void setPositionDetailPanel(final PositionDetailPanel panel)
	{
		this.positionDetailPanel = panel;
	}

	public void setPositionListPanel(final PositionListPanel panel)
	{
		this.positionListPanel = panel;
	}

	public void setUserPanelState(final State state)
	{
		if (this.currentState.equals(UserPanel.State.RECEIPTS_LIST))
		{
			((CardLayout) this.leftPanel.getLayout()).show(this.leftPanel, UserPanel.PANEL_CURRENT_RECEIPTS);
		}
		else if (this.currentState.equals(UserPanel.State.PARKED_RECEIPTS_LIST))
		{
			((CardLayout) this.leftPanel.getLayout()).show(this.leftPanel, UserPanel.PANEL_PARKED_RECEIPTS);
		}
		else if (this.currentState.equals(UserPanel.State.PAYMENT_INPUT)
				|| this.currentState.equals(UserPanel.State.POSITION_INPUT)
						|| this.currentState.equals(UserPanel.State.MUST_SETTLE))
		{
			((CardLayout) this.leftPanel.getLayout()).show(this.leftPanel, UserPanel.PANEL_LEFT_POS);
			((CardLayout) this.rightPanel.getLayout()).show(this.rightPanel, UserPanel.PANEL_RIGHT_POS);
		}
		else if (this.currentState.equals(UserPanel.State.COIN_COUNTER))
		{
			((CardLayout) this.leftPanel.getLayout()).show(this.rightPanel, UserPanel.PANEL_COIN_COUNTER);
		}
		else if (this.currentState.equals(UserPanel.State.LOCKED))
		{
			// TODO
			// ((CardLayout) this.leftPanel.getLayout()).show(this.leftPanel,
			// UserPanel.)
		}
	}

	public void showCoinCounterPanel()
	{
		((CardLayout) this.rightPanel.getLayout()).show(this.rightPanel, UserPanel.PANEL_COIN_COUNTER);
	}

	public void showCurrentReceiptListPanel()
	{
		this.currentReceiptListPanel.getModel().loadReceipts();
		((CardLayout) this.leftPanel.getLayout()).show(this.leftPanel, UserPanel.PANEL_CURRENT_RECEIPTS);
	}
	
	public void setToDefault()
	{
		if (this.currentState.equals(State.POSITION_INPUT))
		{
			
		}
	}

	public void showParkedReceiptsPanel()
	{
		this.parkedReceiptListPanel.getModel().loadReceipts();
		((CardLayout) this.leftPanel.getLayout()).show(this.leftPanel, UserPanel.PANEL_PARKED_RECEIPTS);
	}

	public void showPaymentPanels()
	{
		((CardLayout) this.leftPanel.getLayout()).show(this.leftPanel, UserPanel.PANEL_LEFT_POS);
		((CardLayout) this.rightPanel.getLayout()).show(this.rightPanel, UserPanel.PANEL_RIGHT_POS);

	}

	public void showPositionPanels()
	{
		((CardLayout) this.leftPanel.getLayout()).show(this.leftPanel, UserPanel.PANEL_LEFT_POS);
		((CardLayout) this.rightPanel.getLayout()).show(this.rightPanel, UserPanel.PANEL_RIGHT_POS);
	}

	@Override
	public void stateChange(final StateChangeEvent event)
	{
		final UserPanel.State newState = event.getNewState();

		if (newState != null)
		{
			if (newState.equals(UserPanel.State.COIN_COUNTER))
			{
				this.showCoinCounterPanel();
			}
			else if (newState.equals(UserPanel.State.MUST_SETTLE))
			{
				this.showPositionPanels();
			}
			else if (newState.equals(UserPanel.State.PARKED_RECEIPTS_LIST))
			{
				this.showParkedReceiptsPanel();
			}
			else if (newState.equals(UserPanel.State.RECEIPTS_LIST))
			{
				this.showCurrentReceiptListPanel();
			}
			else if (newState.equals(UserPanel.State.PAYMENT_INPUT))
			{
				this.showPaymentPanels();
			}
			else if (newState.equals(UserPanel.State.POSITION_INPUT))
			{
				this.showPositionPanels();
				if (event.getOldState() == null || event.getOldState().equals(UserPanel.State.COIN_COUNTER))
				{
					this.prepareReceipt();
				}
			}
		}
	}

	public void widgetDisposed(final DisposeEvent event)
	{
		final DisposeListener[] listeners = this.disposeListeners.toArray(new DisposeListener[0]);
		for (final DisposeListener listener : listeners)
		{
			listener.dispose();
		}
	}

	@Override
	protected void update()
	{
		this.leftPosPanel.removeAll();
		this.rightPosPanel.removeAll();

		if (this.profile.getTopLeft() != null)
		{
			final JPanel panel = this.getPanel(this.profile, this.profile.getTopLeft());
			if (panel != null)
			{
//				this.leftPosPanel.add(panel);
				int topPercent = this.profile.getTopPercent();
				if (topPercent == 0)
				{
					topPercent = 55;
				}
				this.leftPosPanel.add(panel, String.valueOf(topPercent) + "%");
			}
		}

		if (this.profile.getBottomLeft() != null)
		{
			final JPanel panel = this.getPanel(this.profile, this.profile.getBottomLeft());
			if (panel != null)
			{
//				this.leftPosPanel.add(panel);
				this.leftPosPanel.add(panel, "*");
			}
		}

		if (this.profile.getTopRight() != null)
		{
			final JPanel panel = this.getPanel(this.profile, this.profile.getTopRight());
			if (panel != null)
			{
				this.rightPosPanel.add(panel);
			}
		}

		if (this.profile.getBottomRight() != null)
		{
			final JPanel panel = this.getPanel(this.profile, this.profile.getBottomRight());
			if (panel != null)
			{
				this.rightPosPanel.add(panel);
			}
		}
	}

	private JPanel getPanel(final Profile profile, final PanelType panelType)
	{
		if (Profile.PanelType.DISPLAY.equals(panelType))
		{
			final InfoPanel infoPanel = new InfoPanel(this, profile);
			this.numericPadPanel.addActionListener(infoPanel);
			this.numericPadPanel.addActionListener(this);
			this.addStateChangeListener(infoPanel);
			return infoPanel;
		}
		else if (Profile.PanelType.NUMERIC.equals(panelType))
		{
			return this.numericPadPanel;
		}
		else if (Profile.PanelType.SELECTION.equals(panelType))
		{
			this.addStateChangeListener(this.selectionPanel);
			return this.selectionPanel;
		}
		else if (Profile.PanelType.FUNCTION.equals(panelType))
		{
			FunctionPanel functionPanel = null;
			final Collection<Configurable> configurables = profile.getConfigurables();
			for (final Configurable configurable : configurables)
			{
				if (!configurable.isDeleted())
				{
					if (configurable.getType().equals(Configurable.ConfigurableType.FUNCTION))
					{
						functionPanel = new FunctionPanel(this, configurable);
						this.addStateChangeListener(functionPanel);
					}
				}
			}
			return functionPanel;
		}

		return null;
	}

	private void init()
	{
//		this.setLayout(new GridLayout(1, 2));
		this.setLayout(new PercentLayout(PercentLayout.HORIZONTAL, 0));

		this.valueDisplay = new ValueDisplay(this, "");

		this.receiptWrapper = new ReceiptWrapper(this);
		this.positionWrapper = new PositionWrapper(this, this.valueDisplay);
		this.paymentWrapper = new PaymentWrapper(this);

		/*
		 * left side of the pos frame
		 */
		this.leftPanel = new JPanel();
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screen.width = screen.width / 2;
		this.leftPanel.setPreferredSize(screen);
		this.leftPanel.setLayout(new CardLayout());
//		this.add(this.leftPanel);
		int leftPercent = this.profile.getLeftPercent();
		if (leftPercent == 0)
		{
			leftPercent = 55;
		}
		this.add(this.leftPanel, String.valueOf(leftPercent) + "%");
		/*
		 * parked receipts
		 */
		this.parkedReceiptListPanel = new ParkedReceiptListPanel(this);
		this.leftPanel.add(UserPanel.PANEL_PARKED_RECEIPTS, this.parkedReceiptListPanel);
		/*
		 * current receipts
		 */
		this.currentReceiptListPanel = new CurrentReceiptListPanel(this, this.profile);
		this.leftPanel.add(UserPanel.PANEL_CURRENT_RECEIPTS, this.currentReceiptListPanel);
		/*
		 * right side of the pos frame
		 */
		this.rightPanel = new JPanel();
		this.rightPanel.setLayout(new CardLayout());
//		this.add(this.rightPanel);
		this.add(this.rightPanel, "*");
		/*
		 * coin counter
		 */
		this.coinCounterPanel = new CoinCounterPanel(this);
		this.rightPanel.add(UserPanel.PANEL_COIN_COUNTER, this.coinCounterPanel);
		/*
		 * left side
		 */
		this.leftPosPanel = new JPanel();
//		this.leftPosPanel.setLayout(new GridLayout(2, 1));
		this.leftPosPanel.setLayout(new PercentLayout(PercentLayout.VERTICAL, 0));
		this.leftPanel.add(UserPanel.PANEL_LEFT_POS, this.leftPosPanel);
		/*
		 * right side
		 */
		this.rightPosPanel = new JPanel();
		this.rightPosPanel.setLayout(new GridLayout(2, 1));
		this.rightPanel.add(UserPanel.PANEL_RIGHT_POS, this.rightPosPanel);

		this.numericPadPanel = new NumericPadPanel(this, this.valueDisplay, this.profile);
		this.selectionPanel = new SelectionPanel(this, this.profile);

		this.update();

		new ReceiptChangeMediator(this, this, new String[] { "customerCode" });

		this.prepareReceipt();

		this.addStateChangeListener(this);
		this.addStateChangeListener(this.valueDisplay);

		this.fireStateChange(new StateChangeEvent(UserPanel.State.POSITION_INPUT, UserPanel.State.POSITION_INPUT));
		if (this.mainTabbedPane.settlementRequired())
		{
			this.fireStateChange(new StateChangeEvent(UserPanel.State.POSITION_INPUT, UserPanel.State.COIN_COUNTER));
		}
		else
		{
			this.fireStateChange(new StateChangeEvent(UserPanel.State.POSITION_INPUT, UserPanel.State.POSITION_INPUT));
		}
	}
	
	public enum State
	{
		POSITION_INPUT, PAYMENT_INPUT, RECEIPTS_LIST, PARKED_RECEIPTS_LIST, COIN_COUNTER, LOCKED, MUST_SETTLE;

		public boolean configurableActionState()
		{
			switch (this)
			{
			case POSITION_INPUT:
			{
				return true;
			}
			case PAYMENT_INPUT:
			{
				return true;
			}
			default:
			{
				return false;
			}
			}
		}
	}

}
