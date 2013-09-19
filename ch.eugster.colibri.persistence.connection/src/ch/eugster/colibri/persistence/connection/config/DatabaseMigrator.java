/*
 * Created on 13.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.config;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.swing.SwingConstants;

import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Document;
import org.jdom.Element;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.connection.wizard.SupportedDriver;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Profile.PanelType;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Role;
import ch.eugster.colibri.persistence.model.RoleProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.Version;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.pos.db.Block;
import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Setting;

public class DatabaseMigrator extends AbstractConfigurator
{
	public static final int COLIBRITS_VERSION_MIGRATABLE = 35;

	private final Document oldDocument;

	private final ch.eugster.pos.db.Salespoint[] salespoints;

	public DatabaseMigrator(final Shell shell, final Element connection, final Document oldDocument,
			final ch.eugster.pos.db.Salespoint[] salespoints)
	{
		super(shell);
		this.oldDocument = oldDocument;
		this.salespoints = salespoints;
	}

	public void migrateDatabase()
	{
		this.start();
	}

	@Override
	protected void start(final IProgressMonitor monitor)
	{
		try
		{
			Activator.getDefault().log("Start Migration.");
			monitor.beginTask("Die Daten aus der Vorgängerversion werden importiert...", 3);
			monitor.worked(1);
			if (this.getEntityManager() != null)
			{
				Activator.getDefault().log("Verbindung zur Datenbank herstellen.");
				final PersistenceBroker broker = DatabaseMigrator
						.createOjbPersistenceBroker(DatabaseMigrator.this.oldDocument);
				monitor.worked(1);
				DatabaseMigrator.this.startMigration(new SubProgressMonitor(monitor, 1), broker);
				monitor.worked(1);
				this.releaseEntityManager(this.getEntityManager());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			monitor.done();
		}
	}

	private long[] checkPosLogins(final Collection<ch.eugster.pos.db.User> users)
	{
		final ch.eugster.pos.db.User[] userArray = users.toArray(new ch.eugster.pos.db.User[0]);
		final long[] posLogins = new long[userArray.length];
		for (int i = 0; i < posLogins.length; i++)
		{
			posLogins[i] = userArray[i].posLogin == null ? 0L : userArray[i].posLogin.longValue();
		}
		for (int i = 0; i < posLogins.length; i++)
		{
			for (int j = i + 1; j < posLogins.length; j++)
			{
				while (posLogins[j] == posLogins[i])
				{
					posLogins[j]++;
				}
			}
		}
		return posLogins;
	}

	private Configurable createConfigurable(final IProgressMonitor monitor, final int count,
			final PersistenceBroker broker, final Profile profile, final Block block)
	{
		Configurable configurable = null;
		monitor.beginTask("Profile werden übernommen...", count);
		try
		{
			if (block.clazz.equals("ch.eugster.pos.client.gui.ProductGroupBlock"))
			{
				configurable = Configurable.newInstance(profile, Configurable.ConfigurableType.PRODUCT_GROUP);
			}
			else if (block.clazz.equals("ch.eugster.pos.client.gui.PaymentTypeBlock"))
			{
				configurable = Configurable.newInstance(profile, Configurable.ConfigurableType.PAYMENT_TYPE);
			}
			else if (block.clazz.equals("ch.eugster.pos.client.gui.ABlockFunction"))
			{
				configurable = Configurable.newInstance(profile, Configurable.ConfigurableType.FUNCTION);
			}
			if (configurable != null)
			{
				long maxId = 0L;
				configurable.setFg(new java.awt.Color(0, 255, 0).getRGB());
				configurable.setBg(new java.awt.Color(255, 255, 255).getRGB());
				configurable.setFgSelected(new java.awt.Color(255, 0, 0).getRGB());
				configurable.setFontSize(Double.valueOf(block.fontSize).floatValue());
				configurable.setFontStyle(block.fontStyle);

				for (final Object obj : block.tabs)
				{
					final ch.eugster.pos.db.Tab tab = (ch.eugster.pos.db.Tab) obj;
					final Tab newTab = this.createTab(new SubProgressMonitor(monitor, 1), 1, broker, configurable, tab);
					if (newTab.getId().longValue() > maxId)
					{
						maxId = newTab.getId().longValue();
					}
					configurable.addTab(newTab);
				}

				this.updateSequence("tab_id", Long.valueOf(maxId + 1L));
			}
			monitor.worked(1);
		}
		catch (final Exception e)
		{
			final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Blöcke ist ein Fehler aufgetreten (Block: " + block.name + ").", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return configurable;
	}

	private Key createKey(final IProgressMonitor monitor, final int count, final PersistenceBroker broker,
			final Tab tab, final CustomKey oldKey)
	{
		final Key newKey = Key.newInstance(tab);
		monitor.beginTask("Tasten werden übernommen...", count);
		try
		{
			newKey.setId(oldKey.getId());
			newKey.setDeleted(oldKey.deleted);
			newKey.setFailOverBg(new java.awt.Color(oldKey.bgRed2, oldKey.bgGreen2, oldKey.bgBlue2).getRGB());
			newKey.setFailOverFg(new java.awt.Color(oldKey.fgRed, oldKey.fgGreen, oldKey.fgBlue).getRGB());
			newKey.setFailOverFontSize(Double.valueOf(oldKey.fontSize).floatValue());
			newKey.setFailOverFontStyle(oldKey.fontStyle);
			newKey.setImageId(oldKey.imagepath);
			newKey.setLabel(oldKey.text);
			newKey.setNormalBg(new java.awt.Color(oldKey.bgRed, oldKey.bgGreen, oldKey.bgBlue).getRGB());
			newKey.setNormalFg(new java.awt.Color(oldKey.fgRed, oldKey.fgGreen, oldKey.fgBlue).getRGB());
			newKey.setNormalFontSize(Double.valueOf(oldKey.fontSize).floatValue());
			newKey.setNormalFontStyle(oldKey.fontStyle);
			newKey.setParentId(oldKey.parentId);
			newKey.setTabCol(oldKey.column);
			newKey.setTabRow(oldKey.row);
			newKey.setTextImageHorizontalPosition(oldKey.relHorizontalTextPos);
			newKey.setTextImageVerticalPosition(oldKey.relVerticalTextPos);
			newKey.setValue(oldKey.value.doubleValue());
			if (oldKey.className.equals("ch.eugster.pos.events.ExpressPaymentAction"))
			{
				newKey.setKeyType(KeyType.PAYMENT_TYPE);
				newKey.setParentId(oldKey.parentId);
				newKey.setValue(oldKey.value.doubleValue());
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.OptionAction"))
			{
				final Criteria criteria = new Criteria();
				criteria.addEqualTo("id", oldKey.parentId);
				final Query query = new QueryByCriteria(ch.eugster.pos.db.Option.class, criteria);
				final ch.eugster.pos.db.Option option = (ch.eugster.pos.db.Option) broker.getObjectByQuery(query);
				if (option != null)
				{
					final Option o = option.code.equals("L") ? Option.ARTICLE : Option.ORDERED;
					newKey.setKeyType(KeyType.OPTION);
					newKey.setParentId(Long.valueOf(o.ordinal()));
				}
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.ShowReceiptListAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_SHOW_CURRENT_RECEIPT_LIST);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.ParkAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_SHOW_PARKED_RECEIPT_LIST);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.LockAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_LOCK);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.LogoffAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_LOGOUT);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.TaxAction"))
			{
				newKey.setKeyType(KeyType.TAX_RATE);
				newKey.setParentId(oldKey.parentId);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.ShowCoinCounterAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_SHOW_COIN_COUNTER_PANEL);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.CashDrawerAction"))
			{
				final Criteria criteria = new Criteria();
				criteria.addEqualTo("id", oldKey.parentId);
				final Query query = new QueryByCriteria(ch.eugster.pos.db.Function.class, criteria);
				final ch.eugster.pos.db.Function function = (ch.eugster.pos.db.Function) broker.getObjectByQuery(query);
				if (function != null)
				{
					final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
					final CriteriaQuery<Currency> currencyQuery = cb.createQuery(Currency.class);
					Metamodel model = this.getEntityManager().getMetamodel();
					final EntityType<Currency> Currency_ = model.entity(Currency.class);
					final Root<Currency> cur = currencyQuery.from(Currency.class);

					newKey.setKeyType(KeyType.FUNCTION);
					newKey.setFunctionType(FunctionType.FUNCTION_OPEN_DRAWER);
					Currency currency = null;
					if (function.actionType == 800)
					{
						currencyQuery.where(cb.equal(cur.get(Currency_.getSingularAttribute("code")), "CHF"));
						final TypedQuery<Currency> cQuery = this.getEntityManager().createQuery(currencyQuery);
						currency = cQuery.getSingleResult();
					}
					if (function.actionType == 801)
					{
						currencyQuery.where(cb.equal(cur.get(Currency_.getSingularAttribute("code")), "EUR"));
						final TypedQuery<Currency> cQuery = this.getEntityManager().createQuery(currencyQuery);
						currency = cQuery.getSingleResult();
					}
					if (currency != null)
					{
						final CriteriaQuery<PaymentType> paymentTypeQuery = cb.createQuery(PaymentType.class);
						model = this.getEntityManager().getMetamodel();
						final EntityType<PaymentType> PaymentType_ = model.entity(PaymentType.class);
						final Root<PaymentType> pt = paymentTypeQuery.from(PaymentType.class);
						paymentTypeQuery.where(cb.and(cb.equal(
								pt.get(PaymentType_.getSingularAttribute("paymentTypeGroup")), PaymentTypeGroup.CASH)),
								cb.equal(pt.get(PaymentType_.getSingularAttribute("currency")), currency));
						final TypedQuery<PaymentType> ptQuery = this.getEntityManager().createQuery(paymentTypeQuery);
						final PaymentType paymentType = ptQuery.getSingleResult();
						newKey.setParentId(paymentType.getId());
					}
				}
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.ExitAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_SHUTDOWN);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.ExpressStoreReceiptAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION);
				newKey.setParentId(oldKey.paymentTypeId);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.ReturnAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_RESTITUTION);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.ProductGroupAction"))
			{
				newKey.setKeyType(KeyType.PRODUCT_GROUP);
				newKey.setParentId(oldKey.parentId);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.SalesPerSalespointAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_SALESPOINT_SALES);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.ChooseCustomerAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_SELECT_CUSTOMER);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.StoreReceiptVoucherAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION);
				newKey.setParentId(oldKey.paymentTypeId);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.SalesPerUserAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_USER_SALES);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.SalesAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_TOTAL_SALES);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.PrintLastReceiptAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_PRINT_LAST_RECEIPT);
			}
			else if (oldKey.className.equals("ch.eugster.pos.events.StoreReceiptAction"))
			{
				newKey.setKeyType(KeyType.FUNCTION);
				newKey.setFunctionType(FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION);
			}
			else
			{
				System.out.println();
			}
			monitor.worked(1);
		}
		catch (final Exception e)
		{
			final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Tastenbelegung ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return newKey;
	}

	private Tab createTab(final IProgressMonitor monitor, final int count, final PersistenceBroker broker,
			final Configurable configurable, final ch.eugster.pos.db.Tab tab)
	{
		final Tab newTab = Tab.newInstance(configurable);
		monitor.beginTask("Tabs werden übernommen...", count);
		try
		{
			newTab.setCols(tab.columns);
			newTab.setRows(tab.rows);
			newTab.setDeleted(tab.deleted);
			newTab.setId(tab.getId());
			newTab.setName(tab.title);
			newTab.setPos(tab.order);

			if (tab.defaultTabPayment && configurable.getType().equals(Configurable.ConfigurableType.PAYMENT_TYPE))
			{
				configurable.setPaymentDefaultTab(newTab);
			}
			if (tab.defaultTabPosition && configurable.getType().equals(Configurable.ConfigurableType.PRODUCT_GROUP))
			{
				configurable.setPositionDefaultTab(newTab);
			}

			long maxId = 0L;
			for (final Object obj : tab.keys)
			{
				final CustomKey key = (CustomKey) obj;
				final Key newKey = this.createKey(new SubProgressMonitor(monitor, 1), 1, broker, newTab, key);
				if (newKey.getId().longValue() > maxId)
				{
					maxId = newKey.getId().longValue();
				}
				newTab.addKey(newKey);
			}
			this.updateSequence("key_id", Long.valueOf(maxId + 1L));

			monitor.worked(1);
		}
		catch (final Exception e)
		{
			final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Tabs ist ein Fehler aufgetreten (Tab: " + tab.title + ").", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return newTab;
	}

	@SuppressWarnings("unchecked")
	private IStatus insertProfiles(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		IStatus status = Status.OK_STATUS;
		Query query = new QueryByCriteria(ch.eugster.pos.db.CustomKey.class);
		final int count = broker.getCount(query);
		monitor.beginTask("Profile werden übernommen...", count);
		try
		{
			Profile profile = this.getEntityManager().find(Profile.class, Long.valueOf(1l));
			if (profile == null)
			{
				final long id = 1L;
				profile = Profile.newInstance();
				profile.setName("Profile 1");
				profile.setId(Long.valueOf(id));

				profile.setTopLeft(PanelType.DISPLAY);
				profile.setTopRight(PanelType.SELECTION);
				profile.setBottomLeft(PanelType.NUMERIC);
				profile.setBottomRight(PanelType.FUNCTION);

				profile.setDisplayFontSize(20f);
				profile.setDisplayFontStyle(java.awt.Font.BOLD);
				profile.setDisplayFg(java.awt.Color.GREEN.getRGB());
				profile.setDisplayBg(java.awt.Color.BLACK.getRGB());

				query = new QueryByCriteria(ch.eugster.pos.db.Block.class);
				final Collection<ch.eugster.pos.db.Block> blocks = broker.getCollectionByQuery(query);
				for (final ch.eugster.pos.db.Block block : blocks)
				{
					final Configurable configurable = this.createConfigurable(
							new SubProgressMonitor(monitor, block.tabs.size()), block.tabs.size(), broker, profile,
							block);
					if (configurable != null)
					{
						if (!profile.getConfigurables().contains(configurable))
						{
							profile.addConfigurable(configurable);
						}
					}
				}
				profile.setTabbedPaneFontSize(18f);
				profile.setTabbedPaneFontStyle(java.awt.Font.PLAIN);
				profile.setTabbedPaneFgSelected(java.awt.Color.RED.getRGB());
				profile.setTabbedPaneFg(java.awt.Color.GREEN.getRGB());
				profile.setTabbedPaneBg(java.awt.Color.WHITE.getRGB());

				profile.setButtonNormalFontSize(12f);
				profile.setButtonNormalFontStyle(java.awt.Font.BOLD);
				profile.setButtonNormalHorizontalAlign(SwingConstants.CENTER);
				profile.setButtonNormalVerticalAlign(SwingConstants.CENTER);
				profile.setButtonNormalFg(java.awt.Color.BLACK.getRGB());
				profile.setButtonNormalBg(java.awt.Color.GREEN.getRGB());

				profile.setButtonFailOverFontSize(12f);
				profile.setButtonFailOverFontStyle(java.awt.Font.BOLD);
				profile.setButtonFailOverHorizontalAlign(SwingConstants.CENTER);
				profile.setButtonFailOverVerticalAlign(SwingConstants.CENTER);
				profile.setButtonFailOverFg(java.awt.Color.BLACK.getRGB());
				profile.setButtonFailOverBg(java.awt.Color.ORANGE.getRGB());
				/*
				 * Labels
				 */
				profile.setNameLabelFontSize(12f);
				profile.setNameLabelFontStyle(0);
				profile.setNameLabelFg(Color.BLACK.getRGB());
				profile.setNameLabelBg(Color.WHITE.getRGB());
				profile.setValueLabelFontSize(12f);
				profile.setValueLabelFontStyle(0);
				profile.setValueLabelFg(Color.BLACK.getRGB());
				profile.setValueLabelBg(Color.WHITE.getRGB());
				profile.setValueLabelBgSelected(Color.WHITE.getRGB());
				/*
				 * List
				 */
				profile.setListBg(Color.WHITE.getRGB());
				profile.setListFg(Color.BLACK.getRGB());
				profile.setListFontSize(12f);
				profile.setListFontStyle(Font.PLAIN);

				this.getEntityManager().getTransaction().begin();
				profile = this.getEntityManager().merge(profile);
				this.getEntityManager().getTransaction().commit();

				this.updateSequence("pt_id", Long.valueOf(id + 1L));
			}
			monitor.worked(1);
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Profile ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus insertRoles(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;
		final String[] name = { "Administrator", "Manager", "Benutzer" };
		final String[] values = { "true", "true", "false" };
		final String[] logins = { "true", "false", "false" };
		monitor.beginTask("Rollen werden eingefügt...", name.length);
		try
		{
			long maxId = 0L;
			for (int i = 0; i < name.length; i++)
			{
				Role role = this.getEntityManager().find(Role.class, Long.valueOf(i + 1));
				if (role == null)
				{
					role = Role.newInstance();
					role.setId(Long.valueOf(i + 1));
					role.setName(name[i]);

					RoleProperty prop = RoleProperty.newInstance(role);
					prop.setKey("login.admin");
					prop.setValue(logins[i]);
					role.addRoleProperty(prop);

					prop = RoleProperty.newInstance(role);
					prop.setKey("login.report");
					prop.setValue(values[i]);
					role.addRoleProperty(prop);

					final FunctionType[] functionTypes = FunctionType.values();
					for (final FunctionType functionType : functionTypes)
					{
						prop = RoleProperty.newInstance(role);
						prop.setKey(functionType.key());
						prop.setValue(values[i]);
						role.addRoleProperty(prop);
					}

					final KeyType[] keyTypes = KeyType.values();
					for (final KeyType keyType : keyTypes)
					{
						prop = RoleProperty.newInstance(role);
						prop.setKey(keyType.getActionCommand());
						prop.setValue(values[i]);
						role.addRoleProperty(prop);
					}

					this.getEntityManager().getTransaction().begin();
					role = this.getEntityManager().merge(role);
					this.getEntityManager().getTransaction().commit();

					if (role.getId().longValue() > maxId)
					{
						maxId = role.getId().longValue();
					}
				}
				monitor.worked(1);
			}
			this.updateSequence("ro_id", Long.valueOf(maxId + 1L));
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Beim Einfügen der Benutzerrollen ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus migrateCommonSettings(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		int numberlength = 0;
		try
		{
			numberlength = Integer.valueOf(
					this.oldDocument.getRootElement().getChild("receipt").getAttributeValue("number-length"))
					.intValue();
		}
		catch (final NumberFormatException e)
		{

		}
		String numberFormat = null;
		if (numberlength > 0)
		{
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < numberlength; i++)
			{
				builder = builder.append("0");
			}
			numberFormat = builder.toString();
		}

		IStatus status = Status.OK_STATUS;
		monitor.beginTask("Allgemeine Einstellungen werden übernommen...", 1);
		try
		{
			CommonSettings commonSettings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));
			if (commonSettings == null)
			{
				final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
				final CriteriaQuery<Currency> currencyQuery = cb.createQuery(Currency.class);
				final Metamodel model = this.getEntityManager().getMetamodel();
				final EntityType<Currency> Currency_ = model.entity(Currency.class);
				final Root<Currency> cur = currencyQuery.from(Currency.class);
				currencyQuery.where(cb.equal(cur.get(Currency_.getSingularAttribute("code")), "CHF"));
				final TypedQuery<Currency> cQuery = this.getEntityManager().createQuery(currencyQuery);
				final Currency currency = cQuery.getSingleResult();

				Criteria criteria = new Criteria();
				criteria.addEqualTo("isDefault", Boolean.valueOf(true));
				Query query = new QueryByCriteria(ch.eugster.pos.db.ProductGroup.class, criteria);

				final ch.eugster.pos.db.ProductGroup pg = (ch.eugster.pos.db.ProductGroup) broker
						.getObjectByQuery(query);

				final ProductGroup productGroup = this.getEntityManager().find(ProductGroup.class, pg.getId());

				commonSettings = CommonSettings.newInstance();
				commonSettings.setId(Long.valueOf(1l));
				commonSettings.setTaxInclusive(true);
				commonSettings.setHostnameResolver(CommonSettings.HostnameResolver.HOSTNAME);
				commonSettings.setMaxPaymentAmount(10000d);
				commonSettings.setMaxPaymentRange(1000d);
				commonSettings.setMaxPriceAmount(10000d);
				commonSettings.setMaxPriceRange(1000d);
				commonSettings.setMaxQuantityAmount(10000);
				commonSettings.setMaxQuantityRange(1000);
				commonSettings.setReferenceCurrency(currency);
				commonSettings.setDefaultProductGroup(productGroup);
				commonSettings.setReceiptNumberFormat(numberFormat);

				this.getEntityManager().getTransaction().begin();
				commonSettings = this.getEntityManager().merge(commonSettings);
				this.getEntityManager().getTransaction().commit();

				criteria = new Criteria();
				criteria.addEqualTo("id", Long.valueOf(1l));
				query = new QueryByCriteria(Setting.class, criteria);
				final ch.eugster.pos.db.Setting setting = (ch.eugster.pos.db.Setting) broker.getObjectByQuery(query);

				String path = getGalileoPath(setting.path);
				if (path != null)
				{
					ProviderProperty prop = ProviderProperty.newInstance();
					prop.setKey("galileo.database.path");
					prop.setProvider("ch.eugster.colibri.provider.galileo");
					prop.setValue(setting.path, setting.path);
					this.getEntityManager().getTransaction().begin();
					this.getEntityManager().merge(prop);
					this.getEntityManager().getTransaction().commit();
				}
				if (!setting.use)
				{
					ProviderProperty prop = ProviderProperty.newInstance();
					prop.setKey("galileo.connect");
					prop.setProvider("ch.eugster.colibri.provider.galileo");
					prop.setValue(Boolean.toString(setting.use), Boolean.toString(setting.use));
					this.getEntityManager().getTransaction().begin();
					this.getEntityManager().merge(prop);
					this.getEntityManager().getTransaction().commit();
				}
				if (setting.hold)
				{
					ProviderProperty prop = ProviderProperty.newInstance();
					prop.setKey("galileo.keep.connection");
					prop.setProvider("ch.eugster.colibri.provider.galileo");
					prop.setValue(Boolean.toString(setting.hold), Boolean.toString(setting.hold));
					this.getEntityManager().getTransaction().begin();
					this.getEntityManager().merge(prop);
					this.getEntityManager().getTransaction().commit();
				}
			}
			monitor.worked(1);
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Allgmeinen Einstellungen ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}

		return status;
	}
	
	private String getGalileoPath(String oldPath)
	{
		if (oldPath == null)
		{
			return null;
		}
		String newPath = null;
		String hostname = "C:";
		try 
		{
			InetAddress addr = InetAddress.getLocalHost();
			hostname = "//" + addr.getHostName();
		} 
		catch (UnknownHostException e) 
		{
			return newPath;
		}
		newPath = hostname + "/Comeliv/Galileo/Data/Galidata.dbc";
		return oldPath.equals(newPath) ? null : newPath;
	}

	@SuppressWarnings("rawtypes")
	private IStatus migrateCurrencies(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		IStatus status = Status.OK_STATUS;
		Query query = new QueryByCriteria(ch.eugster.pos.db.ForeignCurrency.class);
		final int count = broker.getCount(query);
		monitor.beginTask("Währungen werden übernommen...", count);
		try
		{
			long maxId = 0L;
			query = new QueryByCriteria(ForeignCurrency.class);
			final Collection currencies = broker.getCollectionByQuery(query);
			for (final Object obj : currencies)
			{
				final ForeignCurrency source = (ForeignCurrency) obj;
				Currency target = this.getEntityManager().find(Currency.class, source.getId());
				if (target == null)
				{
					target = Currency.newInstance();
					target.setCode(source.code);
					target.setDeleted(source.deleted);
					target.setId(source.getId());
					target.setName(source.name);
					target.setQuotation(source.quotation);
					target.setRegion(source.region);
					target.setRoundFactor(source.roundFactor);
					target.setUpdate(0);

					this.getEntityManager().getTransaction().begin();
					target = this.getEntityManager().merge(target);
					this.getEntityManager().getTransaction().commit();
				}
				if (target.getId().longValue() > maxId)
				{
					maxId = source.getId().longValue();
				}
				monitor.worked(1);
			}
			this.updateSequence("cu_id", Long.valueOf(maxId + 1L));
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Währungen ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IStatus migratePaymentTypes(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		IStatus status = Status.OK_STATUS;
		Query query = new QueryByCriteria(ch.eugster.pos.db.PaymentType.class);
		final int count = broker.getCount(query);
		monitor.beginTask("Zahlungsarten werden übernommen...", count);
		try
		{
			final String[] credits = new String[] { "visa", "master", "american", "express", "kredit", "credit" };
			final String[] debits = new String[] { "ec", "eft", "post", "maestro", "debit" };
			final String[] bons = new String[] { "gutschein", "bon" };

			Criteria criteria = new Criteria();
			criteria.addEqualTo("id", Long.valueOf(1L));
			query = new QueryByCriteria(ch.eugster.pos.db.PaymentType.class, criteria);
			final ch.eugster.pos.db.PaymentType paymentTypeCash = (ch.eugster.pos.db.PaymentType) broker
					.getObjectByQuery(query);

			criteria = new Criteria();
			criteria.addEqualTo("deleted", Boolean.FALSE);
			query = new QueryByCriteria(ch.eugster.pos.db.PaymentType.class, criteria);
			final Collection<ch.eugster.pos.db.PaymentType> paymentTypes = broker.getCollectionByQuery(query);

			final long maxId = 0L;
			for (final ch.eugster.pos.db.PaymentType source : paymentTypes)
			{
				PaymentType target = this.getEntityManager().find(PaymentType.class, source.getId());
				if (target == null)
				{
					if (source.cash)
					{
						target = PaymentType.newInstance(PaymentTypeGroup.CASH);
					}
					else if (!source.getCurrencyCode().equals("CHF"))
					{
						target = PaymentType.newInstance(PaymentTypeGroup.CASH);
					}
					else
					{
						for (final String credit : credits)
						{
							if (source.name.toLowerCase().contains(credit))
							{
								target = PaymentType.newInstance(PaymentTypeGroup.CREDIT);
							}
						}

						for (final String debit : debits)
						{
							if (source.name.toLowerCase().contains(debit))
							{
								target = PaymentType.newInstance(PaymentTypeGroup.DEBIT);
							}
						}

						for (final String bon : bons)
						{
							if (source.name.toLowerCase().contains(bon))
							{
								target = PaymentType.newInstance(PaymentTypeGroup.VOUCHER);
							}
						}
					}
					if (target == null)
					{
						target = PaymentType.newInstance(PaymentTypeGroup.CASH);
					}

					target.setAccount(source.account);
					target.setChange(source.back);
					target.setCode(source.code);
					final Currency cur = this.getEntityManager().find(Currency.class, source.getForeignCurrencyId());
					target.setCurrency(cur);
					target.setDeleted(source.deleted);
					target.setId(source.getId());
					target.setName(source.name);
					target.setOpenCashdrawer(source.openCashdrawer);
					target.setUndeletable(!source.removeable);
					target.setMappingId(source.exportId);

					if (target.getId().equals(paymentTypeCash.getId()))
					{
						long maxMoneyId = 0L;
						criteria = new Criteria();
						criteria.addEqualTo("foreignCurrencyId", source.getForeignCurrencyId());
						query = new QueryByCriteria(ch.eugster.pos.db.Coin.class, criteria);
						final Collection coins = broker.getCollectionByQuery(query);
						for (final Object o : coins)
						{
							final Coin coin = (Coin) o;
							maxMoneyId = coin.getId().longValue() > maxMoneyId ? coin.getId().longValue(): maxMoneyId;
						}

						for (final Object o : coins)
						{
							final Coin coin = (Coin) o;
							final Money money = Money.newInstance(target);
							money.setDeleted(coin.deleted);
							money.setId(coin.getId().equals(Long.valueOf(0L)) ? Long.valueOf(++maxMoneyId) : coin.getId());
							money.setValue(coin.value);
							target.addMoney(money);
						}
					}
					else if (source.getCurrencyCode().equals("EUR"))
					{
						long maxMoneyId = 0L;
						criteria = new Criteria();
						criteria.addEqualTo("foreignCurrencyId", source.getForeignCurrencyId());
						query = new QueryByCriteria(ch.eugster.pos.db.Coin.class, criteria);
						final Collection coins = broker.getCollectionByQuery(query);
						for (final Object o : coins)
						{
							final Coin coin = (Coin) o;
							maxMoneyId = coin.getId().longValue() > maxMoneyId ? coin.getId().longValue(): maxMoneyId;
						}

						for (final Object o : coins)
						{
							final Coin coin = (Coin) o;
							final Money money = Money.newInstance(target);
							money.setDeleted(coin.deleted);
							money.setId(coin.getId().equals(Long.valueOf(0L)) ? Long.valueOf(++maxMoneyId) : coin.getId());
							money.setValue(coin.value);
							target.addMoney(money);
						}
					}
					this.updateSequence("mo_id", Long.valueOf(maxId + 1L));

					this.getEntityManager().getTransaction().begin();
					this.getEntityManager().persist(target);
					this.getEntityManager().getTransaction().commit();
				}
				monitor.worked(1);
			}

			this.updateSequence("pt_id", Long.valueOf(maxId + 1L));
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Zahlungsarten ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	private IStatus migrateProductGroups(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		IStatus status = Status.OK_STATUS;

		ProductGroup defaultProductGroup = null;
		ProductGroup payedInvoice = null;

		Query query = new QueryByCriteria(ch.eugster.pos.db.ProductGroup.class);
		final int count = broker.getCount(query);
		monitor.beginTask("Warengruppen werden übernommen...", count);
		try
		{
			long maxId = 0L;

			CommonSettings settings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));

			query = new QueryByCriteria(ch.eugster.pos.db.ProductGroup.class);
			final Collection<ch.eugster.pos.db.ProductGroup> productGroups = broker.getCollectionByQuery(query);
			for (final ch.eugster.pos.db.ProductGroup source : productGroups)
			{
				ProductGroup target = this.getEntityManager().find(ProductGroup.class, source.getId());
				if (target == null)
				{
					Currency currency = null;
					if (source.getForeignCurrencyId() == null)
					{
						currency = this.getEntityManager()
								.find(Currency.class, settings.getReferenceCurrency().getId());
					}
					else
					{
						currency = this.getEntityManager().find(Currency.class, source.getForeignCurrencyId());
					}

					final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
					final CriteriaQuery<PaymentType> paymentTypeQuery = cb.createQuery(PaymentType.class);
					final Metamodel model = this.getEntityManager().getMetamodel();
					final EntityType<PaymentType> PaymentType_ = model.entity(PaymentType.class);
					final Root<PaymentType> pt = paymentTypeQuery.from(PaymentType.class);
					paymentTypeQuery.where(cb.and(cb.equal(
							pt.get(PaymentType_.getSingularAttribute("paymentTypeGroup")), PaymentTypeGroup.CASH)), cb
							.equal(pt.get(PaymentType_.getSingularAttribute("currency")), currency));
					final TypedQuery<PaymentType> ptQuery = this.getEntityManager().createQuery(paymentTypeQuery);
					final List<PaymentType> paymentTypes = ptQuery.getResultList();

					if (source.paidInvoice)
					{
						target = ProductGroup.newInstance(ProductGroupType.NON_SALES_RELATED, settings);
						target.setProposalOption(Option.PAYED_INVOICE);
						payedInvoice = target;
					}
					else if (source.type == ch.eugster.pos.db.ProductGroup.TYPE_INCOME)
					{
						target = ProductGroup.newInstance(ProductGroupType.SALES_RELATED, settings);
						if (source.optCodeProposal == null)
						{
							target.setProposalOption(Option.ARTICLE);
						}
						else if (source.optCodeProposal.isEmpty())
						{
							target.setProposalOption(Option.ARTICLE);
						}
						else if (source.optCodeProposal.equals("L"))
						{
							target.setProposalOption(Option.ARTICLE);
						}
						else if (source.optCodeProposal.equals("B"))
						{
							target.setProposalOption(Option.ORDERED);
						}
						else
						{
							target.setProposalOption(Option.ARTICLE);
						}
					}
					else if (source.type == ch.eugster.pos.db.ProductGroup.TYPE_NOT_INCOME)
					{
						target = ProductGroup.newInstance(ProductGroupType.NON_SALES_RELATED, settings);
					}
					else if (source.type == ch.eugster.pos.db.ProductGroup.TYPE_EXPENSE)
					{
						target = ProductGroup.newInstance(ProductGroupType.EXPENSES_MATERIAL, settings);
					}
					else if (source.type == ch.eugster.pos.db.ProductGroup.TYPE_INPUT)
					{
						target = ProductGroup.newInstance(ProductGroupType.ALLOCATION, settings);
					}
					else if (source.type == ch.eugster.pos.db.ProductGroup.TYPE_WITHDRAW)
					{
						target = ProductGroup.newInstance(ProductGroupType.WITHDRAWAL, settings);
					}

					// if (target.getProposalOption() == null)
					// {
					// target.setProposalOption(target.getProductGroupType().getOptions()[0]);
					// }

					target.setPaymentType(paymentTypes.iterator().next());
					target.setAccount(source.account);
					target.setCode(source.shortname);
					final Tax tax = this.getEntityManager().find(Tax.class, source.getDefaultTaxId());
					target.setDefaultTax(tax);
					target.setDeleted(source.deleted);
					target.setId(source.getId());
					target.setMappingId(source.exportId);
					target.setName(source.name);
					target.setPriceProposal(source.priceProposal);
					target.setQuantityProposal(source.quantityProposal);
					if ((source.galileoId != null) && (source.galileoId.length() > 0))
					{
						final ExternalProductGroup epg = ExternalProductGroup
								.newInstance("ch.eugster.colibri.provider.galileo");
						epg.setAccount(source.account);
						epg.setCode(source.galileoId);
						epg.setDeleted(source.deleted);
						epg.setText(source.name);
						final ProductGroupMapping mapping = ProductGroupMapping.newInstance(target, epg);
						mapping.setDeleted(source.deleted);
						epg.setProductGroupMapping(mapping);
						target.addProductGroupMapping(mapping);
					}

					if (target.getId().longValue() > maxId)
					{
						maxId = target.getId().longValue();
					}

					this.getEntityManager().getTransaction().begin();
					target = this.getEntityManager().merge(target);
					this.getEntityManager().getTransaction().commit();

					if (source.isDefault)
					{
						if (target.getId().equals(source.getId()))
						{
							defaultProductGroup = target;
						}
					}
				}
				monitor.worked(1);
			}
			maxId++;
			this.updateSequence("pg_id", Long.valueOf(maxId));

			if (payedInvoice == null)
			{
				final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
				final CriteriaQuery<PaymentType> paymentTypeQuery = cb.createQuery(PaymentType.class);
				final Metamodel model = this.getEntityManager().getMetamodel();
				final EntityType<PaymentType> PaymentType_ = model.entity(PaymentType.class);
				final Root<PaymentType> pt = paymentTypeQuery.from(PaymentType.class);
				paymentTypeQuery.where(cb.and(cb.equal(pt.get(PaymentType_.getSingularAttribute("paymentTypeGroup")),
						PaymentTypeGroup.CASH)), cb.equal(pt.get(PaymentType_.getSingularAttribute("currency")),
						settings.getReferenceCurrency()));
				final TypedQuery<PaymentType> ptQuery = this.getEntityManager().createQuery(paymentTypeQuery);
				final List<PaymentType> paymentTypes = ptQuery.getResultList();

				ProductGroup target = ProductGroup.newInstance(ProductGroupType.NON_SALES_RELATED, settings);
				target.setProposalOption(Option.PAYED_INVOICE);
				target.setPaymentType(paymentTypes.iterator().next());
				target.setAccount(null);
				target.setCode("BEZRG");
				final Tax tax = this.getEntityManager().find(Tax.class, Long.valueOf(0L));
				target.setDefaultTax(tax);
				target.setDeleted(false);
				target.setId(maxId++);
				target.setMappingId(null);
				target.setName("Bezahlte Rechnung");
				target.setPriceProposal(0);
				target.setQuantityProposal(1);

				this.getEntityManager().getTransaction().begin();
				target = this.getEntityManager().merge(target);
				this.getEntityManager().getTransaction().commit();

				payedInvoice = target;
				monitor.worked(1);
			}

			settings.setDefaultProductGroup(defaultProductGroup);
			settings.setPayedInvoice(payedInvoice);

			this.getEntityManager().getTransaction().begin();
			settings = this.getEntityManager().merge(settings);
			this.getEntityManager().getTransaction().commit();
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Warengruppen ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus migrateSalespoints(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		IStatus status = Status.OK_STATUS;
		final int count = this.salespoints.length;
		monitor.beginTask("Kassen werden übernommen...", count);
		try
		{
			long maxId = 0L;

			final CommonSettings settings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));

			final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
			final CriteriaQuery<PaymentType> paymentTypeQuery = cb.createQuery(PaymentType.class);
			final Metamodel model = this.getEntityManager().getMetamodel();
			final EntityType<PaymentType> PaymentType_ = model.entity(PaymentType.class);
			final Root<PaymentType> pt = paymentTypeQuery.from(PaymentType.class);

			for (final ch.eugster.pos.db.Salespoint source : this.salespoints)
			{
				Salespoint target = this.getEntityManager().find(Salespoint.class, source.getId());
				if (target == null)
				{
					target = Salespoint.newInstance(settings);
					target.setMapping(source.exportId);
					target.setSettlement(null);
					target.setId(source.getId());
					target.setCurrentReceiptNumber(source.currentReceiptId);
					target.setCurrentParkedReceiptNumber(0);
					target.setDeleted(source.deleted);
					target.setLocation(source.place);
					target.setHost(source.host);
					target.setName(source.name);
					target.setPaymentType(this.getEntityManager().find(PaymentType.class, Long.valueOf(1l)));
					target.setProfile(this.getEntityManager().find(Profile.class, Long.valueOf(1l)));
					// target.setProposalOption(Option.ARTICLE);
					target.setProposalPrice(0d);
					target.setProposalQuantity(1);
					target.setProposalTax(this.getEntityManager().find(Tax.class, Long.valueOf(4l)));

					for (final Object o : source.stocks)
					{
						final ch.eugster.pos.db.Stock stock = (ch.eugster.pos.db.Stock) o;
						final Currency currency = this.getEntityManager().find(Currency.class,
								stock.getForeignCurrencyId());

						paymentTypeQuery.where(cb.and(cb.equal(
								pt.get(PaymentType_.getSingularAttribute("paymentTypeGroup")), PaymentTypeGroup.CASH)),
								cb.equal(pt.get(PaymentType_.getSingularAttribute("currency")), currency));
						final TypedQuery<PaymentType> ptQuery = this.getEntityManager().createQuery(paymentTypeQuery);
						final PaymentType paymentType = ptQuery.getSingleResult();

						final Stock newStock = Stock.newInstance(target);
						newStock.setAmount(stock.getStock());
						newStock.setDeleted(stock.deleted);
						newStock.setPaymentType(paymentType);
						newStock.setVariable(stock.getSalespoint().variableStock);
						target.addStock(newStock);
					}

					if (target.getId().longValue() > maxId)
					{
						maxId = target.getId().longValue();
					}
					this.getEntityManager().getTransaction().begin();
					this.getEntityManager().persist(target);
					this.getEntityManager().getTransaction().commit();
				}
				monitor.worked(1);
			}
			this.updateSequence("sp_id", Long.valueOf(maxId + 1L));
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Kassen ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus migrateSettlements(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		IStatus status = Status.OK_STATUS;
		final Query query = new QueryByCriteria(ch.eugster.pos.db.Settlement.class);
		final int count = broker.getCount(query);
		monitor.beginTask("Abschlüsse werden übernommen...", count);
		try
		{
			long maxId = 0L;

			final CommonSettings settings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));

			final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
			final CriteriaQuery<PaymentType> paymentTypeQuery = cb.createQuery(PaymentType.class);
			final Metamodel model = this.getEntityManager().getMetamodel();
			final EntityType<PaymentType> PaymentType_ = model.entity(PaymentType.class);
			final Root<PaymentType> pt = paymentTypeQuery.from(PaymentType.class);

			for (final ch.eugster.pos.db.Salespoint source : this.salespoints)
			{
				Salespoint target = this.getEntityManager().find(Salespoint.class, source.getId());
				if (target == null)
				{
					target = Salespoint.newInstance(settings);
					target.setSettlement(null);
					target.setMapping(source.exportId);
					target.setId(source.getId());
					target.setCurrentReceiptNumber(source.currentReceiptId);
					target.setCurrentParkedReceiptNumber(0);
					target.setDeleted(source.deleted);
					target.setLocation(source.place);
					target.setHost(source.host);
					target.setName(source.name);
					target.setPaymentType(this.getEntityManager().find(PaymentType.class, Long.valueOf(1l)));
					target.setProfile(this.getEntityManager().find(Profile.class, Long.valueOf(1l)));
					// target.setProposalOption(Option.ARTICLE);
					target.setProposalPrice(0d);
					target.setProposalQuantity(1);
					target.setProposalTax(this.getEntityManager().find(Tax.class, Long.valueOf(4l)));

					for (final Object o : source.stocks)
					{
						final ch.eugster.pos.db.Stock stock = (ch.eugster.pos.db.Stock) o;
						final Currency currency = this.getEntityManager().find(Currency.class,
								stock.getForeignCurrencyId());

						paymentTypeQuery.where(cb.and(cb.equal(
								pt.get(PaymentType_.getSingularAttribute("paymentTypeGroup")), PaymentTypeGroup.CASH)),
								cb.equal(pt.get(PaymentType_.getSingularAttribute("currency")), currency));
						final TypedQuery<PaymentType> ptQuery = this.getEntityManager().createQuery(paymentTypeQuery);
						final PaymentType paymentType = ptQuery.getSingleResult();

						final Stock newStock = Stock.newInstance(target);
						newStock.setAmount(stock.getStock());
						newStock.setDeleted(stock.deleted);
						newStock.setPaymentType(paymentType);
						newStock.setVariable(stock.getSalespoint().variableStock);
						target.addStock(newStock);
					}

					if (target.getId().longValue() > maxId)
					{
						maxId = target.getId().longValue();
					}
					this.getEntityManager().getTransaction().begin();
					target = this.getEntityManager().merge(target);
					this.getEntityManager().getTransaction().commit();
				}
				monitor.worked(1);
			}
			this.updateSequence("sp_id", Long.valueOf(maxId + 1L));
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Kassen ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private IStatus migrateTaxes(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		IStatus status = Status.OK_STATUS;
		Query query = new QueryByCriteria(ch.eugster.pos.db.Tax.class);
		final int count = broker.getCount(query) + 6;
		monitor.beginTask("Mehrwertsteuern werden übernommen...", count);
		try
		{
			long maxId = 0L;
			query = new QueryByCriteria(ch.eugster.pos.db.TaxRate.class);
			final Collection<ch.eugster.pos.db.TaxRate> taxRates = broker.getCollectionByQuery(query);
			for (final ch.eugster.pos.db.TaxRate source : taxRates)
			{
				TaxRate target = this.getEntityManager().find(TaxRate.class, source.getId());
				if (target == null)
				{
					target = TaxRate.newInstance();
					target.setCode(source.code);
					target.setDeleted(source.deleted);
					target.setId(source.getId());
					target.setName(source.name);

					if (target.getId().longValue() > maxId)
					{
						maxId = target.getId().longValue();
					}

					this.getEntityManager().getTransaction().begin();
					target = this.getEntityManager().merge(target);
					this.getEntityManager().getTransaction().commit();
				}
				monitor.worked(1);
			}
			this.updateSequence("tr_id", Long.valueOf(maxId + 1L));

			maxId = 0L;
			query = new QueryByCriteria(ch.eugster.pos.db.TaxType.class);
			final Collection taxTypes = broker.getCollectionByQuery(query);
			for (final Object obj : taxTypes)
			{
				final ch.eugster.pos.db.TaxType source = (ch.eugster.pos.db.TaxType) obj;
				TaxType target = this.getEntityManager().find(TaxType.class, source.getId());
				if (target == null)
				{
					target = TaxType.newInstance();
					target.setCode(source.code);
					target.setDeleted(source.deleted);
					target.setId(source.getId());
					target.setName(source.name);

					if (target.getId().longValue() > maxId)
					{
						maxId = target.getId().longValue();
					}
					this.getEntityManager().getTransaction().begin();
					target = this.getEntityManager().merge(target);
					this.getEntityManager().getTransaction().commit();
				}
				monitor.worked(1);
			}
			this.updateSequence("tt_id", Long.valueOf(maxId + 1L));

			maxId = 0L;
			query = new QueryByCriteria(ch.eugster.pos.db.Tax.class);
			final Collection taxes = broker.getCollectionByQuery(query);
			for (final Object obj : taxes)
			{
				final ch.eugster.pos.db.Tax source = (ch.eugster.pos.db.Tax) obj;
				Tax tax = this.getEntityManager().find(Tax.class, source.getId());
				if (tax == null)
				{
					final TaxRate rate = this.getEntityManager().find(TaxRate.class, source.taxRateId);
					final TaxType type = this.getEntityManager().find(TaxType.class, source.taxTypeId);
					tax = Tax.newInstance(rate, type);
					rate.addTax(tax);
					type.addTax(tax);
					tax.setId(source.getId());
					tax.setAccount(source.account);
					tax.setDeleted(source.deleted);

					if (tax.getId().longValue() > maxId)
					{
						maxId = tax.getId().longValue();
					}
					this.getEntityManager().getTransaction().begin();
					this.getEntityManager().persist(tax);
					this.getEntityManager().getTransaction().commit();
					this.updateSequence("tx_id", Long.valueOf(tax.getId().longValue() + 1L));

					// CurrentTax currentTax = null;
					long maxCurrentTaxId = 0L;
					for (final Object ct : source.currentTaxes)
					{
						final ch.eugster.pos.db.CurrentTax old = (ch.eugster.pos.db.CurrentTax) ct;
						CurrentTax cur = this.getEntityManager().find(CurrentTax.class, old.getId());
						if (cur == null)
						{
							cur = CurrentTax.newInstance(tax);
							cur.setDeleted(old.deleted);
							cur.setId(old.getId());
							cur.setPercentage(BigDecimal.valueOf(old.percentage / 100).round(MathContext.DECIMAL32)
									.doubleValue());
							cur.setValidFrom(old.validationDate.getTime());
							tax.addCurrentTax(cur);
							if (cur.getId().equals(source.currentTaxId))
							{
								// currentTax = cur;
								tax.setCurrentTax(cur);
							}
							if (cur.getId().longValue() > maxCurrentTaxId)
							{
								maxCurrentTaxId = cur.getId().longValue();
							}
							if (tax.getId().longValue() > maxId)
							{
								maxId = tax.getId().longValue();
							}
							this.getEntityManager().getTransaction().begin();
							this.getEntityManager().persist(cur);
							this.getEntityManager().getTransaction().commit();

							this.updateSequence("ct_id", Long.valueOf(cur.getId().longValue() + 1L));
						}
					}

					if ((source.code128Id != null) && (source.code128Id.length() > 0))
					{
						TaxCodeMapping mapping = TaxCodeMapping.newInstance(tax);
						mapping.setAccount(tax.getAccount());
						mapping.setCode(source.code128Id);
						mapping.setProvider("ch.eugster.colibri.provider.code128");
						tax.addTaxCodeMapping(mapping);
						this.getEntityManager().getTransaction().begin();
						this.getEntityManager().persist(mapping);
						this.getEntityManager().getTransaction().commit();
					}
					
					if ((source.galileoId != null) && (source.galileoId.length() > 0))
					{
						TaxCodeMapping mapping = TaxCodeMapping.newInstance(tax);
						mapping.setAccount(tax.getAccount());
						mapping.setCode(source.galileoId);
						// mapping.setId(tax.getId());
						mapping.setProvider("ch.eugster.colibri.provider.galileo");
						tax.addTaxCodeMapping(mapping);
						this.getEntityManager().getTransaction().begin();
						this.getEntityManager().persist(mapping);
						this.getEntityManager().getTransaction().commit();
					}
//					if (tax.getId().longValue() > maxId)
//					{
//						maxId = tax.getId().longValue();
//					}
//					this.getEntityManager().getTransaction().begin();
//					tax = this.getEntityManager().merge(tax);
//					this.getEntityManager().getTransaction().commit();
				}
				monitor.worked(1);
			}
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme Mehrwertsteuern ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	private IStatus migrateUsers(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		IStatus status = Status.OK_STATUS;
		Query query = new QueryByCriteria(ch.eugster.pos.db.User.class);
		final int count = broker.getCount(query);
		monitor.beginTask("Benutzer werden übernommen...", count);
		try
		{
			long maxId = 0L;
			query = new QueryByCriteria(ch.eugster.pos.db.User.class);
			final Collection<ch.eugster.pos.db.User> users = broker.getCollectionByQuery(query);

			final long[] posLogins = this.checkPosLogins(users);
			int i = 0;
			for (final ch.eugster.pos.db.User source : users)
			{
				if (!source.deleted)
				{
					User target = this.getEntityManager().find(User.class, source.getId());
					if (target == null)
					{
						target = User.newInstance();
						target.setDefaultUser(source.defaultUser.booleanValue());
						target.setDeleted(source.deleted);
						target.setId(source.getId());
						target.setPassword(source.password);
						target.setPosLogin((int) posLogins[i]);
						final Role role = this.getEntityManager().find(Role.class, Long.valueOf(source.status + 1));
						target.setRole(role);
						target.setUsername(source.username.toLowerCase());

						if (target.getId().longValue() > maxId)
						{
							maxId = target.getId().longValue();
						}
						this.getEntityManager().getTransaction().begin();
						target = this.getEntityManager().merge(target);
						this.getEntityManager().getTransaction().commit();
					}
				}
				i++;
				monitor.worked(1);
			}
			this.updateSequence("us_id", Long.valueOf(maxId + 1L));
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Bei der Übernahme der Benutzer ist ein Fehler aufgetreten.", e);
			this.log(status);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus startMigration(final IProgressMonitor monitor, final PersistenceBroker broker)
	{
		IStatus status = Status.OK_STATUS;
		monitor.beginTask("Die Daten werden übernommen...", 10);

		try
		{
			status = this.migrateCurrencies(new SubProgressMonitor(monitor, 1), broker);
			if (status.equals(Status.OK_STATUS))
			{
				status = this.insertRoles(new SubProgressMonitor(monitor, 1));
				monitor.worked(1);
			}
			if (status.equals(Status.OK_STATUS))
			{
				status = this.migrateUsers(new SubProgressMonitor(monitor, 1), broker);
				monitor.worked(1);
			}
			if (status.equals(Status.OK_STATUS))
			{
				status = this.migrateTaxes(new SubProgressMonitor(monitor, 1), broker);
				monitor.worked(1);
			}
			if (status.equals(Status.OK_STATUS))
			{
				status = this.migratePaymentTypes(new SubProgressMonitor(monitor, 1), broker);
				monitor.worked(1);
			}
			if (status.equals(Status.OK_STATUS))
			{
				status = this.insertProfiles(new SubProgressMonitor(monitor, 1), broker);
				monitor.worked(1);
			}
			if (status.equals(Status.OK_STATUS))
			{
				status = this.migrateCommonSettings(new SubProgressMonitor(monitor, 1), broker);
				monitor.worked(1);
			}
			if (status.equals(Status.OK_STATUS))
			{
				status = this.migrateProductGroups(new SubProgressMonitor(monitor, 1), broker);
				monitor.worked(1);
			}
			if (status.equals(Status.OK_STATUS))
			{
				status = this.migrateSalespoints(new SubProgressMonitor(monitor, 1), broker);
				monitor.worked(1);
			}
			if (status.equals(Status.OK_STATUS))
			{
				status = this.migrateSettlements(new SubProgressMonitor(monitor, 1), broker);
				monitor.worked(1);
			}
			if (status.equals(Status.OK_STATUS))
			{
				status = this.updateVersion();
				monitor.worked(1);
			}
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus updateVersion()
	{
		final IStatus status = Status.OK_STATUS;
		Version version = this.getEntityManager().find(Version.class, Long.valueOf(1l));
		if (version == null)
		{
			version = Version.newInstance();
		}
		version.setMigrate(true);
		version.setReplicationValue(version.getReplicationValue() + 1);
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().merge(version);
		this.getEntityManager().getTransaction().commit();
		return status;
	}

	public static PersistenceBroker createOjbPersistenceBroker(final Document colibri)
	{
		File ojbProperties = null;
		PersistenceBroker broker = null;
		try
		{
			URL url = Activator.getDefault().getBundle().getEntry("/META-INF/OJB.properties");
			URI uri = url.toURI();
			String path = uri.getPath();

			final File file = FileLocator.getBundleFile(Activator.getDefault().getBundle());
			path = file.getAbsolutePath() + "/META-INF/OJB.properties";
			ojbProperties = new File(path);
			Activator.getDefault().log("Pfad zur Datei mit Datenbankeigenschaften: " + path + ".");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		if (ojbProperties.exists())
		{
			System.setProperty("OJB.properties", ojbProperties.getAbsolutePath());

			final Element element = colibri.getRootElement().getChild("database").getChild("standard")
					.getChild("connection");
			final String driver = element.getAttributeValue("driver");
			Activator.getDefault().log("Treiber: " + driver + ".");
			final String protocol = element.getAttributeValue("protocol");
			final String subprotocol = element.getAttributeValue("subprotocol");
			final String host = element.getAttributeValue("host");
			final String port = element.getAttributeValue("port");
			final String database = element.getAttributeValue("database");
			final String url = protocol + ":" + subprotocol + "://" + host + ":" + port + "/" + database;
			Activator.getDefault().log("URL: " + url + ".");

			final String username = element.getAttributeValue("username");
			Activator.getDefault().log("Benutzername: " + username + ".");
			final String password = element.getAttributeValue("password");
			Activator.getDefault().log("Passwort: " + password + ".");

			final ConnectionRepository cr = MetadataManager.getInstance().connectionRepository();
			final PBKey key = new PBKey("standard", username, password);

			JdbcConnectionDescriptor jcd = cr.getDescriptor(key);
			jcd = DatabaseMigrator.getOjbConnectionDescriptor(jcd, driver, url, username, password);
			if (cr.getDescriptor(key) == null)
			{
				cr.addDescriptor(jcd);
			}

			broker = PersistenceBrokerFactory.createPersistenceBroker(key);
			Activator.getDefault().log("Verbindung hergestellt: " + (broker == null ? "FEHLER" : "OK") + ".");
		}
		return broker;
	}

	public static JdbcConnectionDescriptor getOjbConnectionDescriptor(JdbcConnectionDescriptor jdbcConnection,
			final String driverName, final String url, final String username, final String password)
	{
		SupportedDriver driver = null;

		for (final SupportedDriver supportedDriver : SupportedDriver.values())
		{
			if (supportedDriver.getDriver().equals(driverName))
			{
				driver = supportedDriver;
				break;
			}
		}

		if (driver != null)
		{

			final String[] protocols = url.split("[:]");
			final String protocol = protocols.length > 0 ? protocols[0] : "jdbc";
			final String subprotocol = protocols.length > 1 ? protocols[1] : driver.getOjbSubprotocol();

			final StringBuilder dbalias = new StringBuilder();
			if (protocols.length == 3)
			{
				dbalias.append(protocols.length > 2 ? protocols[2] : "colibri");
			}
			else
			{
				for (int i = 2; i < protocols.length; i++)
				{
					dbalias.append(protocols[i]);
					if (i < protocols.length - 1)
					{
						dbalias.append(":");
					}
				}

			}

			if (jdbcConnection == null)
			{
				jdbcConnection = new JdbcConnectionDescriptor();
			}
			jdbcConnection.setJcdAlias("standard");
			jdbcConnection.setDbms(driver.getOjbPlatform());
			jdbcConnection.setDefaultConnection(true);
			jdbcConnection.setDriver(driver.getDriver());
			jdbcConnection.setProtocol(protocol);
			jdbcConnection.setSubProtocol(subprotocol);
			jdbcConnection.setDbAlias(dbalias.toString());
			jdbcConnection.setUserName(username);
			jdbcConnection.setPassWord(password);
		}
		return jdbcConnection;
	}

	public static void releasePersistenceBroker(final PersistenceBroker broker)
	{
		broker.close();
	}

}
