package ch.eugster.colibri.persistence.connection.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.CommonSettings.HostnameResolver;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Configurable.ConfigurableType;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Role;
import ch.eugster.colibri.persistence.model.RoleProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.Version;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public class DatabaseConfigurator extends AbstractConfigurator
{
	private final Long currencyId;

	private Long payedInvoiceProductGroupId = null;

	public DatabaseConfigurator(final Shell shell, final Element connection, final Long currencyId)
	{
		super(shell);
		this.currencyId = currencyId;
	}

	public void configureDatabase()
	{
		this.start();
	}

	@Override
	protected void start(final IProgressMonitor monitor)
	{
		try
		{
			monitor.beginTask("Datenbank wird konfiguriert...", 3);
			monitor.worked(1);
			if (this.getEntityManager() != null)
			{
				Version version = this.getEntityManager().find(Version.class, Long.valueOf(1L));
				if (version == null)
				{
					this.prepareConfiguration(new SubProgressMonitor(monitor, 1), this.currencyId);
				}
				monitor.worked(1);

				version = this.getEntityManager().find(Version.class, Long.valueOf(1L));
				int dataVersion = version.getData();
				if (dataVersion < Version.DATA)
				{
					this.update(new SubProgressMonitor(monitor, Version.DATA - dataVersion), dataVersion);

					version.setData(dataVersion);
					this.getEntityManager().getTransaction().begin();
					version = this.getEntityManager().merge(version);
					this.getEntityManager().getTransaction().commit();
				}
				if (this.getEntityManager() != Activator.getDefault().getCacheEntityManager())
				{
					this.releaseEntityManager(this.getEntityManager());
				}
				monitor.worked(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			monitor.done();
		}
	}

	private IStatus configure(final IProgressMonitor monitor, final Long currencyId)
	{
		IStatus status = Status.OK_STATUS;

		monitor.beginTask("Die Datenbank wird konfiguriert...", 9);
		try
		{
			try
			{
				this.log(LogService.LOG_INFO, "Währungen werden eingefügt...");
				status = this.createCurrencies(new SubProgressMonitor(monitor, 1));
				monitor.worked(1);
			}
			catch (final IOException e)
			{
				final String msg = "Beim Auslesen der Währungen aus der lokalen Datenbank (currency.csv) ist ein Fehler aufgetreten.";
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), msg, e);
				this.log(status);
				return status;
			}

			this.log(LogService.LOG_INFO, "Allgemeine Einstellungen werden eingefügt...");
			status = this.createCommonSettings(new SubProgressMonitor(monitor, 1), currencyId);
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Voreingestellte Zahlungsarten werden eingefügt...");
			status = this.createPaymentTypeCash(new SubProgressMonitor(monitor, 1));
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Mehrwertsteuern werden eingefügt...");
			status = this.createTaxes(new SubProgressMonitor(monitor, 1));
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Voreingestellte Warengruppe wird eingefügt...");
			status = this.createDefaultProductGroup(new SubProgressMonitor(monitor, 1));
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Warengruppe Bezahlte Rechnungen wird eingefügt...");
			status = this.createPayedInvoiceProductGroup(new SubProgressMonitor(monitor, 1));
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Warengruppe Kreditkartengebühr wird eingefügt...");
			status = this.createChargeProductGroup(new SubProgressMonitor(monitor, 1));
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Voreingestelltes Kassenprofil wird eingefügt...");
			status = this.createDefaultProfile(new SubProgressMonitor(monitor, 1), payedInvoiceProductGroupId);
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Die Administratorenrolle wird eingefügt...");
			status = this.createRole(new SubProgressMonitor(monitor, 1));
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Benutzer Administrator wird eingefügt...");
			status = this.createUser(new SubProgressMonitor(monitor, 1));
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Erste Kasse wird eingefügt...");
			status = this.createSalespoint(new SubProgressMonitor(monitor, 1));
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

			this.log(LogService.LOG_INFO, "Die Version wird eingefügt...");
			status = this.createVersion(new SubProgressMonitor(monitor, 1));
			if (monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}
			if (!status.equals(Status.OK_STATUS))
			{
				return status;
			}

		}
		finally
		{
			monitor.done();
		}

		return status;
	}

	private int countCurrencies() throws IOException
	{
		int lineCount = 0;
		final URL url = Activator.getDefault().getBundle().getResource("META-INF/currencies.csv");
		final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = reader.readLine();
		while (line != null)
		{
			lineCount++;
			line = reader.readLine();
		}
		reader.close();
		return lineCount;
	}

	private IStatus createCommonSettings(final IProgressMonitor monitor, final Long currencyId)
	{
		IStatus status = Status.OK_STATUS;

		try
		{
			monitor.beginTask("Die allgemeinen Einstellungen werden initialisiert...", 1);

			CommonSettings commonSettings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));
			final Currency currency = this.getEntityManager().find(Currency.class, currencyId);
			if (commonSettings == null)
			{
				commonSettings = CommonSettings.newInstance();
				commonSettings.setId(Long.valueOf(1l));
				commonSettings.setHostnameResolver(HostnameResolver.HOSTNAME);
				commonSettings.setTaxInclusive(true);
				commonSettings.setMaxPaymentAmount(10000d);
				commonSettings.setMaxPaymentRange(1000d);
				commonSettings.setMaxPriceAmount(10000d);
				commonSettings.setMaxPriceRange(1000d);
				commonSettings.setMaxQuantityAmount(10000);
				commonSettings.setMaxQuantityRange(1000);
				commonSettings.setTransferDelay(60000);
				commonSettings.setTransferRepeatDelay(15000);
				commonSettings.setTransferReceiptCount(5);
				commonSettings.setAllowTestSettlement(false);
				commonSettings.setForceSettlement(true);
				commonSettings.setMaximizedClientWindow(true);
				commonSettings.setReceiptNumberFormat("000000");
			}
			commonSettings.setReferenceCurrency(currency);

			this.getEntityManager().getTransaction().begin();
			commonSettings = this.getEntityManager().merge(commonSettings);
			this.getEntityManager().getTransaction().commit();

			monitor.worked(1);
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Einfügen der allgemeinen Einstellungen ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus createCurrencies(final IProgressMonitor monitor) throws IOException
	{
		IStatus status = Status.OK_STATUS;
		BufferedReader reader = null;
		final int counter = this.countCurrencies();
		monitor.beginTask("Die Währungen werden generiert...", counter);
		try
		{
			final URL url = Activator.getDefault().getBundle().getResource("META-INF/currencies.csv");
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = reader.readLine();
			while (line != null)
			{
				final String[] cols = line.split(";");
				if (this.getEntityManager().find(Currency.class, Long.valueOf(cols[0])) == null)
				{
					Currency currency = Currency.newInstance();
					currency.setId(Long.valueOf(cols[0]));
					currency.setRegion(cols[1]);
					currency.setName(cols[4]);
					currency.setCode(cols[5]);
					currency.setQuotation(getQuotation(cols[2]));
					currency.setRoundFactor(getRoundFactor(cols[3]));

					this.getEntityManager().getTransaction().begin();
					currency = this.getEntityManager().merge(currency);
					this.getEntityManager().getTransaction().commit();

					this.updateSequence("cu_id", Long.valueOf(currency.getId().longValue()));
				}
				monitor.worked(1);
				line = reader.readLine();
			}
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Einfügen der Währungen ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			reader.close();
			monitor.done();
		}
		return status;
	}
	
	private double getQuotation(String value)
	{
		double quotation = 1d;
		try
		{
			quotation = Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
		}
		return quotation;
	}

	private double getRoundFactor(String value)
	{
		double roundFactor = 0.01;
		try
		{
			roundFactor = Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
		}
		return roundFactor;
	}

	private IStatus createDefaultProductGroup(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;

		CommonSettings settings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));
		final Tax tax = this.getEntityManager().find(Tax.class, Long.valueOf(4L));

		monitor.beginTask("Die erste Warengruppe wird eingerichtet...", 1);
		try
		{
			ProductGroup productGroup = ProductGroup.newInstance(ProductGroupType.SALES_RELATED, settings);
			productGroup.setAccount(null);
			productGroup.setCode("999");
			productGroup.setDefaultTax(tax);
			productGroup.setMappingId(null);
			productGroup.setName("Default WG");
			productGroup.setPriceProposal(0D);
			productGroup.setProposalOption(Option.ARTICLE);
			productGroup.setQuantityProposal(1);

			this.getEntityManager().getTransaction().begin();
			productGroup = this.getEntityManager().merge(productGroup);
			this.getEntityManager().getTransaction().commit();
			monitor.worked(1);

			settings.setDefaultProductGroup(productGroup);

			this.getEntityManager().getTransaction().begin();
			settings = this.getEntityManager().merge(settings);
			this.getEntityManager().getTransaction().commit();

			this.updateSequence("pt_id", Long.valueOf(productGroup.getId().longValue() + 1L));
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Einfügen der Warengruppe ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus createPayedInvoiceProductGroup(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;

		CommonSettings settings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));
		final Tax tax = this.getEntityManager().find(Tax.class, Long.valueOf(1L));

		monitor.beginTask("Die Warengruppe für bezahlte Rechnungen wird eingerichtet...", 1);
		try
		{
			ProductGroup productGroup = ProductGroup.newInstance(ProductGroupType.NON_SALES_RELATED, settings);
			productGroup.setAccount(null);
			productGroup.setCode("BEZRG");
			productGroup.setDefaultTax(tax);
			productGroup.setMappingId(null);
			productGroup.setName("Bezahlte Rechnungen");
			productGroup.setPriceProposal(0D);
			productGroup.setProposalOption(Option.PAYED_INVOICE);
			productGroup.setQuantityProposal(1);

			this.getEntityManager().getTransaction().begin();
			productGroup = this.getEntityManager().merge(productGroup);
			this.getEntityManager().getTransaction().commit();
			monitor.worked(1);

			settings.setPayedInvoice(productGroup);

			this.getEntityManager().getTransaction().begin();
			settings = this.getEntityManager().merge(settings);
			this.getEntityManager().getTransaction().commit();
			this.payedInvoiceProductGroupId = productGroup.getId();

			this.updateSequence("pt_id", Long.valueOf(productGroup.getId().longValue() + 1L));
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Einfügen der Warengruppe für Bezahlte Rechnungen ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus createChargeProductGroup(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;

		CommonSettings settings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));
		final Tax tax = this.getEntityManager().find(Tax.class, Long.valueOf(1L));

		monitor.beginTask("Die Warengruppe für bezahlte Rechnungen wird eingerichtet...", 1);
		try
		{
			ProductGroup productGroup = ProductGroup.newInstance(ProductGroupType.NON_SALES_RELATED, settings);
			productGroup.setAccount(null);
			productGroup.setCode("Kreditkartengebühr");
			productGroup.setDefaultTax(tax);
			productGroup.setMappingId(null);
			productGroup.setName("Kreditkartengebühr");
			productGroup.setPriceProposal(0D);
			productGroup.setProposalOption(Position.Option.NONE);
			productGroup.setQuantityProposal(1);

			this.getEntityManager().getTransaction().begin();
			productGroup = this.getEntityManager().merge(productGroup);
			this.getEntityManager().getTransaction().commit();
			monitor.worked(1);

			settings.setPayedInvoice(productGroup);

			this.getEntityManager().getTransaction().begin();
			settings = this.getEntityManager().merge(settings);
			this.getEntityManager().getTransaction().commit();

			this.updateSequence("pt_id", Long.valueOf(productGroup.getId().longValue() + 1L));
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Einfügen der Warengruppe für Bezahlte Rechnungen ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus createDefaultProfile(final IProgressMonitor monitor, Long payedInvoiceProductGroupId)
	{
		final IStatus status = Status.OK_STATUS;
		try
		{
			monitor.beginTask("Ein voreingestelltes Kassenprofil wird erstellt...", 1);

			if (this.getEntityManager().find(Profile.class, Long.valueOf(1L)) == null)
			{
				Profile profile = Profile.newInstance();
				profile.setId(Long.valueOf(1L));
				profile.initialize();
				profile.setName("Standard");

				addTabs(profile, payedInvoiceProductGroupId);

				this.getEntityManager().getTransaction().begin();
				profile = this.getEntityManager().merge(profile);
				this.getEntityManager().getTransaction().commit();

				this.updateSequence("pr_id", Long.valueOf(profile.getId().longValue() + 1L));
			}

			monitor.worked(1);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}
	
	private void addTabs(Profile profile, Long payedInvoiceProductGroupId)
	{
		for (Configurable configurable : profile.getConfigurables())
		{
			if (configurable.getConfigurableType().equals(ConfigurableType.PRODUCT_GROUP))
			{
				Tab tab = Tab.newInstance(configurable);
				tab.setCols(3);
				tab.setRows(3);
				tab.setName("WG 1");
				tab.setPos(1);
				configurable.addTab(tab);
			}
			else if (configurable.getConfigurableType().equals(ConfigurableType.FUNCTION))
			{
				Tab tab = Tab.newInstance(configurable);
				tab.setCols(3);
				tab.setRows(3);
				tab.setName("Fun 1");
				tab.setPos(1);
				configurable.addTab(tab);
				
				Key key = Key.newInstance(tab);
				key.setKeyType(KeyType.OPTION);
				key.setParentId(Long.valueOf(Option.ORDERED.ordinal()));
				key.setLabel("Besorgung");
				key.setTabCol(0);
				key.setTabRow(0);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_TOTAL_SALES);
				key.setLabel("Umsatz");
				key.setTabCol(1);
				key.setTabRow(0);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_LOCK);
				key.setLabel("Sperren");
				key.setTabCol(2);
				key.setTabRow(0);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_RESTITUTION);
				key.setLabel("Rücknahme");
				key.setTabCol(0);
				key.setTabRow(1);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_SHOW_PARKED_RECEIPT_LIST);
				key.setLabel("Parkieren");
				key.setTabCol(1);
				key.setTabRow(1);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_LOGOUT);
				key.setLabel("Abmelden");
				key.setTabCol(2);
				key.setTabRow(1);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_SHOW_CURRENT_RECEIPT_LIST);
				key.setLabel("Belegliste");
				key.setTabCol(0);
				key.setTabRow(2);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_PRINT_LAST_RECEIPT);
				key.setLabel("<html>Letzen<br>Beleg");
				key.setTabCol(1);
				key.setTabRow(2);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_SELECT_CUSTOMER);
				key.setParentId(this.payedInvoiceProductGroupId);
				key.setLabel("Kunde");
				key.setTabCol(2);
				key.setTabRow(2);
				tab.addKey(key);

				tab = Tab.newInstance(configurable);
				tab.setCols(3);
				tab.setRows(3);
				tab.setName("Fun 2");
				tab.setPos(2);
				configurable.addTab(tab);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.OPTION);
				key.setParentId(Long.valueOf(Option.ARTICLE.ordinal()));
				key.setLabel("Lager");
				key.setTabCol(0);
				key.setTabRow(0);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.TAX_RATE);
				key.setParentId(Long.valueOf(1L));
				key.setLabel("Steuerfrei");
				key.setTabCol(1);
				key.setTabRow(0);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_SHOW_COIN_COUNTER_PANEL);
				key.setLabel("Abschluss");
				key.setTabCol(2);
				key.setTabRow(0);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.TAX_RATE);
				key.setParentId(Long.valueOf(2L));
				key.setLabel("<html>Red.<br>Steuersatz");
				key.setTabCol(1);
				key.setTabRow(1);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_OPEN_DRAWER);
				key.setParentId(Long.valueOf(22));
				key.setLabel("<html>Schublade<br>öffnen");
				key.setTabCol(2);
				key.setTabRow(1);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.TAX_RATE);
				key.setParentId(Long.valueOf(3L));
				key.setLabel("<html>Normaler.<br>Steuersatz");
				key.setTabCol(1);
				key.setTabRow(2);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_SHUTDOWN);
				key.setLabel("Beenden");
				key.setTabCol(2);
				key.setTabRow(2);
				tab.addKey(key);

				tab = Tab.newInstance(configurable);
				tab.setCols(1);
				tab.setRows(3);
				tab.setName("Coupon");
				tab.setPos(3);
				configurable.addTab(tab);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION);
				key.setParentId(Long.valueOf(1L));
				key.setLabel("Beleg abschliessen");
				key.setTabCol(0);
				key.setTabRow(0);
				tab.addKey(key);

				key = Key.newInstance(tab);
				key.setKeyType(KeyType.FUNCTION);
				key.setFunctionType(FunctionType.FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION);
				key.setParentId(Long.valueOf(1L));
				key.setLabel("Express abschliessen");
				key.setTabCol(0);
				key.setTabRow(1);
				tab.addKey(key);

//				key = Key.newInstance(tab);
//				key.setKeyType(KeyType.FUNCTION);
//				key.setParentId(Long.valueOf(1L));
//				key.setFunctionType(FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION);
//				key.setLabel("<html><font color=\"#000000\" size=\"1.0em\">Beleg abschliessen<br>Gutschein CHF");
//				key.setTabCol(0);
//				key.setTabRow(2);
//				tab.addKey(key);

			}
		}
	}

	private IStatus createPaymentTypeCash(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;
		String[] codes = null;

		final CommonSettings settings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));
		Currency currency = settings.getReferenceCurrency();
		if (currency.getCode().equals("CHF") || currency.getCode().equals("EUR"))
		{
			codes = new String[] { "CHF", "EUR" };
		}
		else
		{
			codes = new String[] { currency.getCode(), "CHF", "EUR" };
		}

		monitor.beginTask("Die voreingestellten Zahlungsarten werden eingerichtet...", codes.length);
		try
		{
			long counter = 1l;
			for (final String code : codes)
			{
				final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
				final CriteriaQuery<Currency> currencyQuery = cb.createQuery(Currency.class);
				Metamodel model = this.getEntityManager().getMetamodel();
				final EntityType<Currency> Currency_ = model.entity(Currency.class);
				final Root<Currency> cur = currencyQuery.from(Currency.class);
				currencyQuery.where(cb.equal(cur.get(Currency_.getSingularAttribute("code")), code));
				final TypedQuery<Currency> query = this.getEntityManager().createQuery(currencyQuery);
				currency = query.getSingleResult();

				final CriteriaQuery<PaymentType> paymentTypeQuery = cb.createQuery(PaymentType.class);
				model = this.getEntityManager().getMetamodel();
				final EntityType<PaymentType> PaymentType_ = model.entity(PaymentType.class);
				final Root<PaymentType> pt = paymentTypeQuery.from(PaymentType.class);
				paymentTypeQuery.where(cb.and(cb.equal(pt.get(PaymentType_.getSingularAttribute("paymentTypeGroup")),
						PaymentTypeGroup.CASH)), cb.equal(pt.get(PaymentType_.getSingularAttribute("currency")),
						currency));
				final TypedQuery<PaymentType> ptQuery = this.getEntityManager().createQuery(paymentTypeQuery);
				PaymentType paymentType = null;
				try
				{
					paymentType = ptQuery.getSingleResult();
				}
				catch (final NoResultException e)
				{
					paymentType = PaymentType.newInstance(PaymentTypeGroup.CASH);
					paymentType.setId(Long.valueOf(counter));
					paymentType.setChange(true);
					paymentType.setCurrency(currency);
					paymentType.setCode("BAR " + currency.getCode());
					paymentType.setName("Bargeld" + currency.getCode());
					paymentType.setOpenCashdrawer(true);
					paymentType.setUndeletable(true);

					if (paymentType.getCurrency().getCode().equals("CHF"))
					{
						final double[] values = new double[] { .05d, .1d, .2d, .5d, 1d, 2d, 5d, 10d, 20d, 50d, 100d, 200d };
						for (final double value : values)
						{
							final Money money = Money.newInstance(paymentType);
							money.setValue(value);
							paymentType.addMoney(money);
						}
					}
					else if (paymentType.getCurrency().getCode().equals("EUR"))
					{
						final double[] values = new double[] { .01d, .02d, .05d, .1d, .2d, .5d, 1d, 2d, 5d, 10d, 20d,
								50d, 100d, 200d };
						for (final double value : values)
						{
							final Money money = Money.newInstance(paymentType);
							money.setValue(value);
							paymentType.addMoney(money);
						}
					}
					this.getEntityManager().getTransaction().begin();
					paymentType = this.getEntityManager().merge(paymentType);
					this.getEntityManager().getTransaction().commit();
				}
				counter++;
				monitor.worked(1);
			}
			this.updateSequence("pt_id", Long.valueOf(counter + 1L));
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Einfügen der Zahlungsarten ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus createRole(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;
		monitor.beginTask("Die Rolle mit sämtlichen Berechtigungen wird eingerichtet...", 1);
		try
		{
			long maxRoleId = 1L;
			long maxRolePropertyId = 1L;
			
			Role role = this.getEntityManager().find(Role.class, Long.valueOf(1l));
			if (role == null)
			{
				role = Role.newInstance();
				role.setId(Long.valueOf(maxRoleId));
				role.setName("Administrator");

				RoleProperty prop = RoleProperty.newInstance(role);
				prop.setKey("login.admin");
				prop.setValue("true");
				role.addRoleProperty(prop);

				prop = RoleProperty.newInstance(role);
				prop.setKey("login.report");
				prop.setValue("true");
				role.addRoleProperty(prop);

				final FunctionType[] functionTypes = FunctionType.values();
				for (final FunctionType functionType : functionTypes)
				{
					final RoleProperty property = RoleProperty.newInstance(role);
					property.setKey(functionType.key());
					property.setValue(Boolean.toString(true));
					role.addRoleProperty(property);
				}

				final KeyType[] keyTypes = KeyType.values();
				for (final KeyType keyType : keyTypes)
				{
					final RoleProperty property = RoleProperty.newInstance(role);
					property.setKey(keyType.key());
					property.setValue(Boolean.toString(true));
					role.addRoleProperty(property);
				}

				this.getEntityManager().getTransaction().begin();
				role = this.getEntityManager().merge(role);
				this.getEntityManager().getTransaction().commit();

				maxRoleId = role.getId().longValue();
				for (RoleProperty property : role.getRoleProperties())
				{
					if (property.getId().longValue() > maxRolePropertyId)
					{
						maxRolePropertyId = property.getId().longValue();
					}
				}
				maxRoleId = role.getId().longValue();
				
				monitor.worked(1);
			}
			else
			{
				if (role.getId().longValue() > maxRoleId)
				{
					maxRoleId = role.getId().longValue();
				}
				for (RoleProperty property : role.getRoleProperties())
				{
					if (property.getId().longValue() > maxRolePropertyId)
					{
						maxRolePropertyId = property.getId().longValue();
					}
				}
			}
			this.updateSequence("ro_id", maxRoleId + 1L);
			this.updateSequence("r_id", maxRolePropertyId + 1L);

		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Einfügen der Administratorenrolle ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus createSalespoint(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;

		final CommonSettings commonSettings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));

		final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		final CriteriaQuery<Salespoint> salespointQuery = cb.createQuery(Salespoint.class);
		final TypedQuery<Salespoint> query = this.getEntityManager().createQuery(salespointQuery);
		final List<Salespoint> salespoints = query.getResultList();

		long maxSalespointId = 0L;
		long maxStockId = 0L;


		final String host = commonSettings.getHostnameResolver().getHostname();
		if ((salespoints.size() == 0) && (host != null))
		{
			monitor.beginTask("Die Kasse " + host + " wird eingerichtet...", 1);
			try
			{
				Salespoint salespoint = Salespoint.newInstance(commonSettings);
				salespoint.setId(Long.valueOf(1L));
				salespoint.setCurrentParkedReceiptNumber(0L);
				salespoint.setCurrentReceiptNumber(Long.valueOf(0L));
				salespoint.setHost(host);
				salespoint.setName(host);
				salespoint.setProfile(this.getEntityManager().find(Profile.class, Long.valueOf(1l)));
				salespoint.setPaymentType(this.getEntityManager().find(PaymentType.class, Long.valueOf(1l)));
				salespoint.setProposalTax(this.getEntityManager().find(Tax.class, Long.valueOf(4l)));
				salespoint.setProposalQuantity(1);
				final Stock stock = Stock.newInstance(salespoint);
				stock.setPaymentType(salespoint.getPaymentType());
				salespoint.addStock(stock);

				this.getEntityManager().getTransaction().begin();
				salespoint = this.getEntityManager().merge(salespoint);
				this.getEntityManager().getTransaction().commit();

				maxSalespointId = salespoint.getId().longValue();
				for (Stock st : salespoint.getStocks())
				{
					if (st.getId().longValue() > maxStockId)
					{
						maxStockId = st.getId().longValue();
					}
				}

				monitor.worked(1);
			}
			catch (final Exception e)
			{
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
						"Beim Einfügen der Kasse ist ein Fehler aufgetreten.", e);
			}
			finally
			{
				monitor.done();
			}
		}
		else
		{
			for (final Salespoint salespoint : salespoints)
			{
				if (salespoint.getId().longValue() > maxSalespointId)
				{
					maxSalespointId = salespoint.getId().longValue();
				}
				for (Stock st : salespoint.getStocks())
				{
					if (st.getId().longValue() > maxStockId)
					{
						maxStockId = st.getId().longValue();
					}
				}
			}
		}
		this.updateSequence("sp_id", maxSalespointId + 1L);
		this.updateSequence("st_id", maxStockId + 1L);
		return status;
	}

	private IStatus createTaxes(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;

		final CommonSettings commonSettings = this.getEntityManager().find(CommonSettings.class, Long.valueOf(1L));
		if (commonSettings.getReferenceCurrency().getCode().equals("CHF"))
		{
			monitor.beginTask("Die Mehrwertsteuern werden eingefügt...", 15);
			try
			{
				long ttId = 0L;
				long trId = 0L;

				String[] codes = new String[] { "U", "M", "I" };
				String[] names = new String[] { "Umsatzsteuer", "Vorsteuer Mat/DL", "Vorsteuer Betr/Inv" };
				for (int i = 0; i < 3; i++)
				{
					final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
					final CriteriaQuery<TaxType> taxTypeQuery = cb.createQuery(TaxType.class);
					final Metamodel model = this.getEntityManager().getMetamodel();
					final EntityType<TaxType> TaxType_ = model.entity(TaxType.class);
					final Root<TaxType> tt = taxTypeQuery.from(TaxType.class);
					taxTypeQuery.where(cb.equal(tt.get(TaxType_.getSingularAttribute("code")), codes[i]));
					final TypedQuery<TaxType> query = this.getEntityManager().createQuery(taxTypeQuery);
					TaxType taxType = null;
					try
					{
						taxType = query.getSingleResult();
					}
					catch (final NoResultException e)
					{
						taxType = TaxType.newInstance();
						taxType.setId(Long.valueOf(i + 1));
						taxType.setCode(codes[i]);
						taxType.setName(names[i]);

						this.getEntityManager().getTransaction().begin();
						taxType = this.getEntityManager().merge(taxType);
						this.getEntityManager().getTransaction().commit();
					}
					monitor.worked(1);
					ttId = i;
				}
				this.updateSequence("tt_id", ttId + 1L);

				codes = new String[] { "F", "R", "N" };
				names = new String[] { "Steuerfrei", "Reduzierter Steuersatz", "Normaler Steuersatz" };
				for (int i = 0; i < 3; i++)
				{
					final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
					final CriteriaQuery<TaxRate> taxRateQuery = cb.createQuery(TaxRate.class);
					final Metamodel model = this.getEntityManager().getMetamodel();
					final EntityType<TaxRate> TaxRate_ = model.entity(TaxRate.class);
					final Root<TaxRate> tr = taxRateQuery.from(TaxRate.class);
					taxRateQuery.where(cb.equal(tr.get(TaxRate_.getSingularAttribute("code")), codes[i]));
					final TypedQuery<TaxRate> query = this.getEntityManager().createQuery(taxRateQuery);
					TaxRate taxRate = null;
					try
					{
						taxRate = query.getSingleResult();
					}
					catch (final NoResultException e)
					{
						taxRate = TaxRate.newInstance();
						taxRate.setId(Long.valueOf(i + 1));
						taxRate.setCode(codes[i]);
						taxRate.setName(names[i]);

						this.getEntityManager().getTransaction().begin();
						taxRate = this.getEntityManager().merge(taxRate);
						this.getEntityManager().getTransaction().commit();
					}
					monitor.worked(1);
					trId = i;
				}
				this.updateSequence("tr_id", trId + 1L);

				CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

				final CriteriaQuery<TaxRate> taxRateQuery = cb.createQuery(TaxRate.class);
				final TypedQuery<TaxRate> trQuery = this.getEntityManager().createQuery(taxRateQuery);
				final List<TaxRate> taxRates = trQuery.getResultList();

				final CriteriaQuery<TaxType> taxTypeQuery = cb.createQuery(TaxType.class);
				final TypedQuery<TaxType> ttQuery = this.getEntityManager().createQuery(taxTypeQuery);
				final List<TaxType> taxTypes = ttQuery.getResultList();

				long taxId = 1L;
				long currentTaxId = 1L;
				int[] years = new int[] { 1995, 2011 };
				for (final TaxRate taxRate : taxRates)
				{
					double[] percentage = new double[] { 0d };
					if (taxRate.getCode().equals("R"))
					{
						percentage = new double[] { .024, .025 };
					}
					else if (taxRate.getCode().equals("N"))
					{
						percentage = new double[] { .076, 0.08 };
					}

					for (final TaxType taxType : taxTypes)
					{
						cb = this.getEntityManager().getCriteriaBuilder();
						final CriteriaQuery<Tax> taxQuery = cb.createQuery(Tax.class);
						final Metamodel model = this.getEntityManager().getMetamodel();
						final EntityType<Tax> Tax_ = model.entity(Tax.class);
						final Root<Tax> tx = taxQuery.from(Tax.class);
						taxQuery.where(cb.and(cb.equal(tx.get(Tax_.getSingularAttribute("taxType")), taxType),
								cb.equal(tx.get(Tax_.getSingularAttribute("taxRate")), taxRate)));
						final TypedQuery<Tax> query = this.getEntityManager().createQuery(taxQuery);
						Tax tax = null;
						try
						{
							tax = query.getSingleResult();
						}
						catch (final NoResultException e)
						{
							tax = Tax.newInstance(taxRate, taxType);
							tax.setId(taxId++);
							for (int i = 0; i < percentage.length; i++)
							{
								final CurrentTax currentTax = CurrentTax.newInstance(tax);
								currentTax.setId(currentTaxId++);
								currentTax.setPercentage(percentage[i]);
								final Calendar validationDate = Calendar.getInstance();
								validationDate.set(Calendar.DATE, 1);
								validationDate.set(Calendar.MONTH, 0);
								validationDate.set(Calendar.YEAR, years[i]);
								validationDate.set(Calendar.HOUR_OF_DAY, 0);
								validationDate.set(Calendar.MINUTE, 0);
								validationDate.set(Calendar.SECOND, 0);
								validationDate.set(Calendar.MILLISECOND, 0);
								currentTax.setValidFrom(Long.valueOf(validationDate.getTimeInMillis()));
								tax.addCurrentTax(currentTax);
							}
							taxRate.addTax(tax);
							taxType.addTax(tax);

							this.getEntityManager().getTransaction().begin();
							this.getEntityManager().persist(tax);
							this.getEntityManager().getTransaction().commit();
							TaxUpdater.updateTaxes(this.getEntityManager(), tax);
						}
						monitor.worked(1);
					}
				}
				this.updateSequence("tx_id", taxId);
				this.updateSequence("ct_id", currentTaxId);
			}
			catch (final Exception e)
			{
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
						"Beim Einfügen der Mehrwertsteuersätze ist ein Fehler aufgetreten.", e);
			}
			finally
			{
				monitor.done();
			}
		}
		return status;
	}
	
	private IStatus createUser(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;
		monitor.beginTask("Der Benutzer Administrator wird eingerichtet...", 1);
		try
		{
			long maxId = 1L;
			final Role role = this.getEntityManager().find(Role.class, Long.valueOf(1l));

			final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
			final CriteriaQuery<User> userQuery = cb.createQuery(User.class);
			final Metamodel model = this.getEntityManager().getMetamodel();
			final EntityType<User> User_ = model.entity(User.class);
			final Root<User> u = userQuery.from(User.class);
			userQuery.where(cb.equal(u.get(User_.getSingularAttribute("role")), role));
			final TypedQuery<User> uQuery = this.getEntityManager().createQuery(userQuery);
			final List<User> users = uQuery.getResultList();

			if (users.size() == 0)
			{
				User user = User.newInstance();
				user.setId(Long.valueOf(maxId));
				user.setDefaultUser(false);
				user.setPassword("1234");
				user.setPosLogin(1234);
				user.setUsername("Admin".toLowerCase());
				user.setRole(role);

				this.getEntityManager().getTransaction().begin();
				user = this.getEntityManager().merge(user);
				this.getEntityManager().getTransaction().commit();
				User.setLoginUser(user);
				monitor.worked(1);
			}
			else
			{
				for (final User user : users)
				{
					if (user.getId().longValue() > maxId)
					{
						maxId = user.getId().longValue();
					}
				}
			}
			this.updateSequence("us_id", maxId + 1L);
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Einfügen des Benutzers Administrator ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private IStatus createVersion(final IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;

		try
		{
			monitor.beginTask("Die Version wird eingefügt...", 1);
			Version version = this.getEntityManager().find(Version.class, Long.valueOf(1L));
			if (version == null)
			{
				version = Version.newInstance();
				version.setData(Version.DATA);
				version.setStructure(Version.STRUCTURE);
				version.setReplicationValue(version.getReplicationValue() + 1);

				this.getEntityManager().getTransaction().begin();
				version = this.getEntityManager().merge(version);
				this.getEntityManager().getTransaction().commit();
			}
			monitor.worked(1);
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Einfügen der Version ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private void prepareConfiguration(final IProgressMonitor monitor, final Long currencyId)
	{
		monitor.beginTask("Die Datenbank wird konfiguriert...", 1);
		DatabaseConfigurator.this.configure(new SubProgressMonitor(monitor, 1), currencyId);
		monitor.worked(1);
	}

	private void update(final IProgressMonitor monitor, final int version)
	{
		try
		{
			monitor.beginTask("Datenbank wird aktualisiert...", Version.DATA - version);
			for (int i = version; i < Version.DATA; i++)
			{
				monitor.worked(1);
			}
		}
		finally
		{
			monitor.done();
		}
	}

}
