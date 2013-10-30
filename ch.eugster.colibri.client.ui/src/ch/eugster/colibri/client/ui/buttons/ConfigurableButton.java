/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.buttons;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.ConfigurableAction;
import ch.eugster.colibri.client.ui.actions.LockAction;
import ch.eugster.colibri.client.ui.actions.LogoutAction;
import ch.eugster.colibri.client.ui.actions.OpenDrawerAction;
import ch.eugster.colibri.client.ui.actions.OptionAction;
import ch.eugster.colibri.client.ui.actions.PaymentTypeAction;
import ch.eugster.colibri.client.ui.actions.ProductGroupAction;
import ch.eugster.colibri.client.ui.actions.ReceiptParkingAction;
import ch.eugster.colibri.client.ui.actions.RestitutionAction;
import ch.eugster.colibri.client.ui.actions.SalespointSalesAction;
import ch.eugster.colibri.client.ui.actions.SelectCustomerAction;
import ch.eugster.colibri.client.ui.actions.ShowCoinCounterPanelAction;
import ch.eugster.colibri.client.ui.actions.ShowCurrentReceiptListAction;
import ch.eugster.colibri.client.ui.actions.ShutdownAction;
import ch.eugster.colibri.client.ui.actions.StoreReceiptExpressAction;
import ch.eugster.colibri.client.ui.actions.StoreReceiptShorthandAction;
import ch.eugster.colibri.client.ui.actions.TaxRateAction;
import ch.eugster.colibri.client.ui.actions.TotalSalesAction;
import ch.eugster.colibri.client.ui.actions.UserSalesAction;
import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.events.EventTopic;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.provider.service.ProviderService;
import ch.eugster.colibri.scheduler.service.UpdateScheduler;
import ch.eugster.colibri.ui.buttons.HTMLButton;

public class ConfigurableButton extends HTMLButton implements EntityListener, EventHandler
{
	public static final long serialVersionUID = 0l;

	protected Key key;

	private Point place;

	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;

	public ConfigurableButton(final ConfigurableAction action, final Key key)
	{
		super(action, key.getNormalFg(), key.getFailOverFg());
		this.key = key;
		this.setFocusable(false);

		EntityMediator.addListener(Key.class, this);

		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
//		final String[] topics = ProviderService.Topic.topics();
		List<String> topics = new ArrayList<String>();
		for (ProviderService.Topic topic : ProviderService.Topic.values())
		{
			topics.add(topic.topic());
		}
		topics.add(EventTopic.FAILOVER.topic());
		for(UpdateScheduler.SchedulerTopic topic : UpdateScheduler.SchedulerTopic.values())
		{
			topics.add(topic.topic());
		}
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);

		this.silentUpdate(false);
	}

	@Override
	public void finalize()
	{
		this.eventHandlerServiceRegistration.unregister();
		EntityMediator.removeListener(Key.class, this);
	}

	public Key getKey()
	{
		return this.key;
	}

	public Point getPlace()
	{
		if (this.key == null)
		{
			return this.place;
		}
		else
		{
			return new Point(this.key.getTabRow(), this.key.getTabCol());
		}
	}

	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals(ProviderService.Topic.PROVIDER_FAILOVER.topic()) || event.getTopic().equals(EventTopic.FAILOVER.topic()) || event.getTopic().equals(UpdateScheduler.SchedulerTopic.FAILOVER.topic()))
		{
			this.failOver = event.getProperty(EventConstants.EXCEPTION) != null;
			this.update(this.failOver);
		}
		else if (event.getTopic().equals(UpdateScheduler.SchedulerTopic.OK.topic()))
		{
			this.update(false);
		}
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void postLoad(final AbstractEntity entity)
	{
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
	}

	@Override
	public void postRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		if (entity instanceof Key)
		{
			if (entity.getId().equals(this.key.getId()))
			{
				this.key = (Key) entity;
				this.update(this.failOver);
			}
		}
	}

	@Override
	public void preDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void prePersist(final AbstractEntity entity)
	{
	}

	@Override
	public void preRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void preUpdate(final AbstractEntity entity)
	{
	}

	public void setKey(final Key key)
	{
		this.key = key;
	}

	public void silentUpdate(final boolean failOver)
	{
		this.update();
		if (failOver)
		{
			this.createLabelFailOver(this.getText());
			this.updateFailOver();
		}
		else
		{
			this.createLabelNormal(this.getText());
			this.updateNormal();
		}
	}

	public void update()
	{
		if ((this.key.getImageId() != null) && (this.key.getImageId().length() > 0))
		{
			this.setIcon(new ImageIcon(this.key.getImageId()));
		}
		this.setHorizontalTextPosition(this.key.getTextImageHorizontalPosition());
		this.setVerticalTextPosition(this.key.getTextImageVerticalPosition());
	}

	@Override
	public void update(final boolean failOver)
	{
		this.silentUpdate(failOver);
		this.firePropertyChange("dirty", null, null);
	}

	public void update(final boolean silent, final boolean failOver)
	{
		if (silent)
		{
			this.silentUpdate(failOver);
		}
		else
		{
			this.update(failOver);
		}
	}

	public void update(final Point place)
	{
		this.place = place;
		this.updateDeleted();
	}

	private void updateDeleted()
	{
		this.setText("");
		this.setIcon(null);
		this.setBackground(Color.LIGHT_GRAY);
		this.setForeground(Color.LIGHT_GRAY);
	}

	private void updateFailOver()
	{
		this.update(new Color(this.key.getFailOverFg()), new Color(this.key.getFailOverBg()), this.key.getFailOverFontStyle(),
				this.key.getFailOverFontSize(), this.key.getFailOverHorizontalAlign(), this.key.getFailOverVerticalAlign());
	}

	private void updateNormal()
	{
		this.update(new Color(this.key.getNormalFg()), new Color(this.key.getNormalBg()), this.key.getNormalFontStyle(),
				this.key.getNormalFontSize(), this.key.getNormalHorizontalAlign(), this.key.getNormalVerticalAlign());
	}

	public static Class<? extends ConfigurableAction> getActionClass(final FunctionType functionType)
	{
		switch (functionType)
		{
			case FUNCTION_LOCK:
			{
				return LockAction.class;
			}
			case FUNCTION_LOGOUT:
			{
				return LogoutAction.class;
			}
			case FUNCTION_SHUTDOWN:
			{
				return ShutdownAction.class;
			}
			case FUNCTION_SELECT_CUSTOMER:
			{
				return SelectCustomerAction.class;
			}
//			case FUNCTION_STORE_RECEIPT:
//			{
//				return StoreReceiptAction.class;
//			}
			case FUNCTION_SHOW_CURRENT_RECEIPT_LIST:
			{
				return ShowCurrentReceiptListAction.class;
			}
			case FUNCTION_SHOW_PARKED_RECEIPT_LIST:
			{
				return ReceiptParkingAction.class;
			}
			case FUNCTION_SHOW_COIN_COUNTER_PANEL:
			{
				return ShowCoinCounterPanelAction.class;
			}
			case FUNCTION_RESTITUTION:
			{
				return RestitutionAction.class;
			}
			case FUNCTION_TOTAL_SALES:
			{
				return TotalSalesAction.class;
			}
			case FUNCTION_SALESPOINT_SALES:
			{
				return SalespointSalesAction.class;
			}
			case FUNCTION_USER_SALES:
			{
				return UserSalesAction.class;
			}
			case FUNCTION_OPEN_DRAWER:
			{
				return OpenDrawerAction.class;
			}
			case FUNCTION_STORE_RECEIPT_EXPRESS_ACTION:
			{
				return StoreReceiptExpressAction.class;
			}
			case FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION:
			{
				return StoreReceiptShorthandAction.class;
			}
			default:
			{
				throw new RuntimeException("No such function type");
			}
		}
	}

	public static Class<? extends ConfigurableAction> getActionClass(final KeyType keyType)
	{
		switch (keyType)
		{
			case PRODUCT_GROUP:
			{
				return ProductGroupAction.class;
			}
			case PAYMENT_TYPE:
			{
				return PaymentTypeAction.class;
			}
			case TAX_RATE:
			{
				return TaxRateAction.class;
			}
			case OPTION:
			{
				return OptionAction.class;
			}
			case FUNCTION:
			{
				return null;
			}
			default:
			{
				throw new RuntimeException("No such class, try to get the class by using getClassName()");
			}
		}

	}

	public static Class<? extends ConfigurableButton> getButtonClass(final FunctionType functionType)
	{
		switch (functionType)
		{
			case FUNCTION_LOCK:
			{
				return LockButton.class;
			}
			case FUNCTION_SHOW_PARKED_RECEIPT_LIST:
			{
				return ReceiptParkingButton.class;
			}
			default:
			{
				return ConfigurableButton.class;
			}
		}
	}

	public static Class<ConfigurableButton> getButtonClass(final KeyType keyType)
	{
		if (keyType.equals(KeyType.FUNCTION))
		{
			return null;
		}
		else
		{
			return ConfigurableButton.class;
		}
	}

}
