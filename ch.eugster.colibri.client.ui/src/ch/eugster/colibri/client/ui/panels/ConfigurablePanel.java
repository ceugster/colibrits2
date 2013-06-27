/*
 * Created on 24.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.ActionResolver;
import ch.eugster.colibri.client.ui.actions.ConfigurableAction;
import ch.eugster.colibri.client.ui.buttons.ConfigurableButton;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class ConfigurablePanel extends JPanel implements IConfigurable, StateChangeListener, DisposeListener
{
	public static final long serialVersionUID = 0l;

	protected UserPanel userPanel;

	private Configurable configurable;

	private JTabbedPane tabbedPane;

	private Color fgSelected;

	private Color fg;

	private Color bg;

	private Tab positionDefaultTab;

	private Tab paymentDefaultTab;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public ConfigurablePanel(final UserPanel userPanel, final Configurable configurable)
	{
		this.userPanel = userPanel;
		this.configurable = configurable;

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.init();
	}

	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	public Configurable getConfigurable()
	{
		return this.configurable;
	}

	@Override
	public abstract String getName();

	public void setConfigurable(final Configurable configurable)
	{
		this.configurable = configurable;
	}

	public void stateChange(final StateChangeEvent event)
	{
		if (this.tabbedPane != null)
		{
			if (event.getNewState().equals(UserPanel.State.POSITION_INPUT))
			{
				if (this.positionDefaultTab != null)
				{
					for (int i = 0; i < this.tabbedPane.getTabCount(); i++)
					{
						if (this.tabbedPane.getTitleAt(i).equals(this.positionDefaultTab.getName()))
						{
							this.tabbedPane.setSelectedIndex(i);
						}
					}
				}
			}
			else if (event.getNewState().equals(UserPanel.State.PAYMENT_INPUT))
			{
				if (this.paymentDefaultTab != null)
				{
					for (int i = 0; i < this.tabbedPane.getTabCount(); i++)
					{
						if (this.tabbedPane.getTitleAt(i).equals(this.paymentDefaultTab.getName()))
						{
							this.tabbedPane.setSelectedIndex(i);
						}
					}
				}
			}
		}
	}

	protected Component createButton(final Key key)
	{
		ConfigurableButton button = null;
		try
		{
			button = this.createButton(this.createAction(key), key);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return button == null ? new JPanel() : button;
	}

	private boolean checkParent(final Key key)
	{
		final Long parentId = key.getParentId();
		if (parentId != null)
		{
			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();

			if (key.getKeyType().equals(KeyType.FUNCTION))
			{
				if (key.getFunctionType().equals(FunctionType.FUNCTION_OPEN_DRAWER))
				{
				}
				else if (key.getFunctionType().equals(FunctionType.FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION))
				{
				}
				else if (key.getFunctionType().equals(FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION))
				{
				}
				else if (key.getFunctionType().equals(FunctionType.FUNCTION_STORE_RECEIPT))
				{
				}
			}
			else if (key.getKeyType().equals(KeyType.OPTION))
			{

			}
			else if (key.getKeyType().equals(KeyType.PAYMENT_TYPE))
			{
				if (persistenceService != null)
				{
					PaymentType paymentType = (PaymentType) persistenceService.getCacheService().find(PaymentType.class, parentId);
					return paymentType == null ? false : !paymentType.isDeleted();
				}
				return false;
			}
			else if (key.getKeyType().equals(KeyType.PRODUCT_GROUP))
			{
				if (persistenceService != null)
				{
					ProductGroup productGroup = (ProductGroup) persistenceService.getCacheService().find(ProductGroup.class, parentId);
					return productGroup == null ? false : !productGroup.isDeleted();
				}
				return false;
			}
			else if (key.getKeyType().equals(KeyType.TAX_RATE))
			{
				if (persistenceService != null)
				{
					TaxRate taxRate = (TaxRate) persistenceService.getCacheService().find(TaxRate.class, parentId);
					return taxRate == null ? false : !taxRate.isDeleted();
				}
				return false;
			}
		}
		return true;
	}

	private ConfigurableAction createAction(final Key key) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, InstantiationException
	{
		Class<? extends ConfigurableAction> actionClass = ActionResolver.getActionClass(key.getKeyType());
		if (actionClass == null)
		{
			actionClass = ActionResolver.getActionClass(key.getFunctionType());
		}

		final Constructor<?> constructor = actionClass.getConstructor(UserPanel.class, Key.class);
		final Object[] parameters = new Object[2];
		parameters[0] = this.userPanel;
		parameters[1] = key;

		return (ConfigurableAction) constructor.newInstance(parameters);
	}

	private ConfigurableButton createButton(final ConfigurableAction action, final Key key) throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, InstantiationException
	{
		Class<? extends ConfigurableButton> buttonClass = ActionResolver.getButtonClass(key.getKeyType());
		if (buttonClass == null)
		{
			buttonClass = ActionResolver.getButtonClass(key.getFunctionType());
		}

		final Constructor<?> constructor = buttonClass.getConstructor(ConfigurableAction.class, Key.class);
		final Object[] parameters = new Object[2];
		parameters[0] = action;
		parameters[1] = key;

		return (ConfigurableButton) constructor.newInstance(parameters);
	}

	private JPanel fillPanel(final JPanel panel, final Tab tab)
	{
		panel.setLayout(new GridLayout(tab.getRows(), tab.getCols()));

		final Key[] keys = tab.getKeys().toArray(new Key[0]);
		Arrays.sort(keys);

		final Map<Integer, HashMap<Integer, Key>> rows = new HashMap<Integer, HashMap<Integer, Key>>();

		for (final Key key : keys)
		{
			HashMap<Integer, Key> cols = rows.get(key.getTabRow());
			if (cols == null)
			{
				cols = new HashMap<Integer, Key>();
			}

			cols.put(key.getTabCol(), key);
			rows.put(key.getTabRow(), cols);
			
			if (key.getFunctionType() != null && key.getFunctionType().equals(FunctionType.FUNCTION_RESTITUTION))
			{
				this.userPanel.setRestitutionPrintCount(key.getCount());
			}
		}

		for (int i = 0; i < tab.getRows(); i++)
		{
			final HashMap<Integer, Key> cols = rows.get(new Integer(i));
			if (cols == null)
			{
				for (int j = 0; j < tab.getCols(); j++)
				{
					panel.add(new JPanel());
				}
			}
			else
			{
				for (int j = 0; j < tab.getCols(); j++)
				{
					final Key key = cols.get(new Integer(j));
					if ((key == null) || key.isDeleted())
					{
						panel.add(new JPanel());
					}
					else if (this.checkParent(key))
					{

						panel.add(this.createButton(key));
					}
					else
					{
						panel.add(new JPanel());
					}
				}
			}
		}
		return panel;
	}

	private void init()
	{
		
		final Tab[] tabs = this.configurable.getActiveTabs().toArray(new Tab[0]);
		if ((tabs == null) || (tabs.length == 0))
		{
			return;
		}

		if (tabs.length == 1)
		{
			if (!tabs[0].isDeleted())
			{
				this.fillPanel(this, tabs[0]);
			}
		}
		else
		{
			this.setLayout(new BorderLayout());

			this.fgSelected = new java.awt.Color(this.configurable.getFgSelected());
			this.fg = new java.awt.Color(this.configurable.getFg());
			this.bg = new java.awt.Color(this.configurable.getBg());

			this.tabbedPane = new JTabbedPane();
			this.tabbedPane.setFont(this.tabbedPane.getFont().deriveFont(this.configurable.getFontStyle(), this.configurable.getFontSize()));
			this.tabbedPane.addChangeListener(new ChangeListener()
			{
				public void stateChanged(final ChangeEvent event)
				{
					for (int i = 0; i < ConfigurablePanel.this.tabbedPane.getTabCount(); i++)
					{
						if (ConfigurablePanel.this.tabbedPane.getSelectedIndex() == i)
						{
							ConfigurablePanel.this.tabbedPane.setForegroundAt(i, ConfigurablePanel.this.fgSelected);
						}
						else
						{
							ConfigurablePanel.this.tabbedPane.setForegroundAt(i, ConfigurablePanel.this.fg);
							ConfigurablePanel.this.tabbedPane.setBackgroundAt(i, ConfigurablePanel.this.bg);
						}
					}
				}
			});
			this.add(this.tabbedPane, BorderLayout.CENTER);

			Arrays.sort(tabs);
			for (final Tab tab : tabs)
			{
				this.tabbedPane.addTab(tab.getName(), this.fillPanel(new JPanel(), tab));
			}

			if ((this.configurable.getPositionDefaultTab() != null) && !this.configurable.getPositionDefaultTab().isDeleted())
			{
				this.positionDefaultTab = this.configurable.getPositionDefaultTab();
			}
			if ((this.configurable.getPaymentDefaultTab() != null) && !this.configurable.getPaymentDefaultTab().isDeleted())
			{
				this.paymentDefaultTab = this.configurable.getPaymentDefaultTab();
			}
		}
	}
}
