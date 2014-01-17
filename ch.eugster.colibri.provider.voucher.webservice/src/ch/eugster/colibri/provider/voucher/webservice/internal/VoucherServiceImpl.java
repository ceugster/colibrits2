package ch.eugster.colibri.provider.voucher.webservice.internal;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.datacontract.schemas._2004._07.GCDService.CFaultEx;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.tempuri.IService;
import org.tempuri.IServiceProxy;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.AmountType;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.ProviderState;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Receipt.QuotationType;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.PaymentQuery;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IDirtyable;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;
import ch.eugster.colibri.provider.service.ProviderUpdater;
import ch.eugster.colibri.provider.voucher.VoucherService;

public class VoucherServiceImpl implements VoucherService, ProviderUpdater
{
	public static final String MINIMAL_VERSION_WS = "2013.6.0.5";

	public static final String MINIMAL_VERSION_DB = "1.71.15";

	private PersistenceService persistenceService;
	
	private LogService logService;

	private Map<String, IProperty> properties;
	
	private String company;
	
	private IService service;
	
	private ComponentContext context;

	public void setPersistenceService(PersistenceService service)
	{
		this.persistenceService = service;
	}
	
	public void unsetPersistenceService(PersistenceService service)
	{
		this.persistenceService = null;
	}
	
	public void setLogService(LogService service)
	{
		this.logService = service;
	}
	
	public void unsetLogService(LogService service)
	{
		this.logService = null;
	}
	
	public void activate(ComponentContext context)
	{
		this.context = context;
	}
	
	private IService startService()
	{
		IProperty url = this.properties.get(VoucherProperty.URL.key());
		IService service = new IServiceProxy(url.value());
		return service;
	}

	private void log(int level, String message)
	{
		if (logService != null)
		{
			logService.log(level, message);
		}
	}
	
	private Map<String, IProperty> loadProperties()
	{
		Map<String, IProperty> properties = new HashMap<String, IProperty>();
		for (VoucherProperty property : VoucherProperty.values())
		{
			properties.put(property.key(), property);
		}
		CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getCacheService().getQuery(CommonSettings.class);
		CommonSettings settings = query.findDefault();
		this.company = settings.getAddress();

		ProviderPropertyQuery providerPropertyQuery = (ProviderPropertyQuery) persistenceService.getServerService().getQuery(ProviderProperty.class);
		Collection<ProviderProperty> providerProperties = providerPropertyQuery.selectByProvider(this.getDiscriminator());
		for (ProviderProperty providerProperty : providerProperties)
		{
			IProperty property = properties.get(providerProperty.getKey());
			property.setPersistedProperty(providerProperty);
		}
		return properties;
	}
		
	public void deactivate(ComponentContext context)
	{
		this.context = null;
	}
	
	public String getDiscriminator()
	{
		return this.context.getBundleContext().getBundle().getSymbolicName();
	}
	
	public String getProviderId()
	{
		return this.context.getBundleContext().getBundle().getSymbolicName();
	}
	
	public Map<String, IProperty> getProperties()
	{
		return VoucherProperty.asMap();
	}
	
	@Override
	public Map<String, IProperty> getDefaultProperties() 
	{
		return VoucherProperty.asMap();
	}

	@Override
	public void testService() 
	{
		loadProperties();
		Short pin = new Short((short) 2927);
		String code = "GCD1000000000016";
		Integer mandator = Integer.valueOf(0);
		String company = "";
		Integer distribution = Integer.valueOf(0);
		Integer business = Integer.valueOf(0);
		Result result = this.doQuery(pin, code, 13.50, Command.RESERVE_AMOUNT, mandator, company, distribution, business);
		System.out.println(result.getAmount());
		System.out.println("Fehler " + result.getErrorCode() + ": " + result.getErrorMessage());
		System.out.println("");
		result = this.doQuery(pin, code, -2.123456, Command.QUERY_BALANCE, mandator, company, distribution, business);
		System.out.println(result.getAmount());
		System.out.println("Fehler " + result.getErrorCode() + ": " + result.getErrorMessage());
		System.out.println("");
	}

	@Override
	public Result getDatabaseVersion() 
	{
		Result result = doQuery(null, null, 0D, Command.VERSION_DB, Integer.valueOf(0), this.company, Integer.valueOf(0), Integer.valueOf(0));
		return result;
	}

	@Override
	public Result getWebserviceVersion() 
	{
		Result result = doQuery(null, null, 0D, Command.VERSION_WS, Integer.valueOf(0), this.company, Integer.valueOf(0), Integer.valueOf(0));
		return result;
	}

	public Result getAccountBalance(String code)
	{
		Result result = doQuery(null, code, 0D, Command.QUERY_BALANCE, Integer.valueOf(0), "", Integer.valueOf(0), Integer.valueOf(0));
		return result;
	}

	@Override
	public Result chargeAccount(String code, double amount)
	{
		Result result = doQuery(null, code, amount, Command.CHARGE_ACCOUNT, Integer.valueOf(0), this.company, Integer.valueOf(0), Integer.valueOf(0));
		return result;
	}

	@Override
	public Result creditAccount(String code, double amount) 
	{
		Result result = doQuery(null, code, amount, Command.CREDIT_ACCOUNT, Integer.valueOf(0), this.company, Integer.valueOf(0), Integer.valueOf(0));
		return result;
	}

	@Override
	public Result reserveAmount(String code, double amount) 
	{
		Result result = doQuery(null, code, amount, Command.RESERVE_AMOUNT, Integer.valueOf(0), this.company, Integer.valueOf(0), Integer.valueOf(0));
		return result;
	}

	@Override
	public Result confirmReservedAmount(String code, double amount) 
	{
		Result result = doQuery(null, code, amount, Command.CONFIRM_RESERVED_AMOUNT, Integer.valueOf(0), this.company, Integer.valueOf(0), Integer.valueOf(0));
		return result;
	}

	@Override
	public Result cancelReservedAmount(String code, double amount) 
	{
		Result result = doQuery(null, code, amount, Command.CANCEL_RESERVED_AMOUNT, Integer.valueOf(0), this.company, Integer.valueOf(0), Integer.valueOf(0));
		return result;
	}

	private Result doQuery(Short pin, String code, double amount, Command command, Integer mandator, String company, Integer distribution, Integer business)
	{
//		Short testPIN = new Short((short) 2927);
		Short testPIN = null;
		
		Result result = null;
		this.properties = loadProperties();
		IProperty prop = this.properties.get(VoucherProperty.URL.key());
		String url = prop.value();
		prop = this.properties.get(VoucherProperty.USERNAME.key());
		String username = prop.value();
		prop = this.properties.get(VoucherProperty.PASSWORD.key());
		String password = prop.value();

		try
		{
			log(LogService.LOG_INFO, "Stelle Verbindung zu " + url + " her.");
			if (this.service == null)
			{
				this.service = startService();
			}
			
			log(LogService.LOG_INFO, "Aufruf von GCD_V1: url=" + url + ";username=" + username + ";password=" + password + ";betrag=" + amount + ";command=" + command.command() + "(" + command.description() + ")" + ";beschreibung=" + command.description() + ";mandant=" + mandator + ";firma=" + company + ";vertrieb=" + distribution + ";geschäft=" + business + ";");
//			String value = service.GCD_V1(username, password, testPIN, code, Double.valueOf(amount).toString(), Command.VERSION_WS.command(), command.description(), mandator, company, distribution, business);
//			if (value.compareTo(MINIMAL_VERSION_WS) < 0)
//			{
//				result = new Result(command, -1, "Die Version des Webservices (" + value + " entspricht nicht der erforderlichen Version (" + MINIMAL_VERSION_WS + ").", context.getBundleContext().getBundle().getSymbolicName(), null);
//				
//			}
//			else
//			{
				log(LogService.LOG_INFO, "Aufruf von GCD_V1: url=" + url + ";username=" + username + ";password=" + password + ";betrag=" + amount + ";command=" + command.command() + "(" + command.description() + ")" + ";beschreibung=" + command.description() + ";mandant=" + mandator + ";firma=" + company + ";vertrieb=" + distribution + ";geschäft=" + business + ";");
				String value = service.GCD_V1(username, password, testPIN, code, Double.valueOf(amount).toString(), command.command(), command.description(), mandator, company, distribution, business);
				result = new Result(command, Double.valueOf(value).doubleValue());
//			}
		}
		catch(CFaultEx e)
		{
			result = new Result(command, Integer.valueOf(e.getGcdErrorCode()).intValue(), e.getGcdErrorMessage(), context.getBundleContext().getBundle().getSymbolicName(), e);
		}
		catch(RemoteException e)
		{
			result = new Result(command, Integer.MIN_VALUE, e.getLocalizedMessage(), context.getBundleContext().getBundle().getSymbolicName(), e);
		}
		catch(NumberFormatException e)
		{
			result = new Result(command, 0, e.getLocalizedMessage(), context.getBundleContext().getBundle().getSymbolicName(), e);
		}
		return result;
	}
	

	@Override
	public IStatus updateProvider(Position position) 
	{
		Result result = null;
		if (position.getReceipt().getState().equals(Receipt.State.SAVED))
		{
			if (position.isBookProvider() && !position.isProviderBooked())
			{
				if (position.getQuantity() > 0)
				{
					result = creditAccount(position.getProvider(), position.getAmount(QuotationType.DEFAULT_CURRENCY, AmountType.NETTO));
				}
				else if (position.getQuantity() < 0)
				{
					result = chargeAccount(position.getProvider(), position.getAmount(QuotationType.DEFAULT_CURRENCY, AmountType.NETTO));
				}
			}
			if (result.isOK())
			{
				position.setProviderBooked(true);
				position.setProviderState(ProviderState.BOOKED);
			}
		}
		else if (position.getReceipt().getState().equals(Receipt.State.REVERSED))
		{
			if (position.isBookProvider() && position.isProviderBooked())
			{
				if (position.getQuantity() > 0)
				{
					result = chargeAccount(position.getProvider(), position.getAmount(QuotationType.DEFAULT_CURRENCY, AmountType.NETTO));
				}
				else if (position.getQuantity() < 0)
				{
					result = creditAccount(position.getProvider(), position.getAmount(QuotationType.DEFAULT_CURRENCY, AmountType.NETTO));
				}
			}
			if (result.isOK())
			{
				position.setProviderBooked(false);
				position.setProviderState(ProviderState.OPEN);
			}
		}
		if (result.isOK())
		{
			try
			{
				position = (Position) persistenceService.getServerService().merge(position);
			}
			catch (Exception e)
			{
				return new Status(IStatus.ERROR, Activator.getDefault().getContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), e);
			}
		}
		return result.getStatus();
	}

	@Override
	public IStatus updateProvider(Payment payment) 
	{
		Result result = null;
		if (payment.getReceipt().getState().equals(Receipt.State.SAVED))
		{
			if (payment.isBookProvider() && !payment.isProviderBooked())
			{
				if (payment.getProviderState().equals(ProviderState.OPEN))
				{
					result = VoucherServiceImpl.this.chargeAccount(payment.getProviderId(), payment.getAmount(QuotationType.DEFAULT_CURRENCY));
				}
				else if (payment.getProviderState().equals(ProviderState.RESERVED))
				{
					result = VoucherServiceImpl.this.confirmReservedAmount(payment.getProviderId(), payment.getAmount(QuotationType.DEFAULT_CURRENCY));
				}
			}
			if (result.isOK())
			{
				payment.setProviderBooked(true);
				payment.setProviderState(ProviderState.BOOKED);
			}
		}
		else if (payment.getReceipt().getState().equals(Receipt.State.REVERSED))
		{
			if (payment.isBookProvider() && payment.isProviderBooked())
			{
				if (payment.getProviderState().equals(ProviderState.BOOKED))
				{
					result = VoucherServiceImpl.this.creditAccount(payment.getProviderId(), payment.getAmount(QuotationType.DEFAULT_CURRENCY));
				}
				else if (payment.getProviderState().equals(ProviderState.RESERVED))
				{
					result = VoucherServiceImpl.this.cancelReservedAmount(payment.getProviderId(), payment.getAmount(QuotationType.DEFAULT_CURRENCY));
				}
			}
			if (result.isOK())
			{
				payment.setProviderBooked(false);
				payment.setProviderState(ProviderState.OPEN);
				try
				{
					payment = (Payment) persistenceService.getServerService().merge(payment);
				}
				catch (Exception e)
				{
					return new Status(IStatus.ERROR, Activator.getDefault().getContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), e);
				}
			}
		}
		return result.getStatus();
	}

	@Override
	public String getName() 
	{
		return "eGutschein";
	}
	
	@Override
	public boolean doCheckFailover() 
	{
		return false;
	}

	@Override
	public boolean canTestConnection() 
	{
		return true;
	}
	
	public boolean isActive()
	{
		return true;
	}

	@Override
	public IStatus testConnection(Map<String, IProperty> properties) 
	{
		IStatus status = new Status(IStatus.OK, context.getBundleContext().getBundle().getSymbolicName(), "Die Verbindung zu " + this.getName() + " wurde erfolgreich hergestellt.");
		if (Desktop.isDesktopSupported())
		{
			IProperty urlProperty = properties.get(VoucherProperty.URL.key());
			String urlValue = urlProperty.value();
			try
			{
				URI uri = new URI(urlValue);
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(uri);
			}
			catch (IOException e)
			{
				status = new Status(IStatus.ERROR, context.getBundleContext().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
			} 
			catch (URISyntaxException e) 
			{
				status = new Status(IStatus.ERROR, context.getBundleContext().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
			}
		}
		else
		{
			status = new Status(IStatus.ERROR, context.getBundleContext().getBundle().getSymbolicName(), "Das Resultat des Verbindungs");
		}
		return status;
	}

	@Override
	public boolean isSalespointSpecificPossible() 
	{
		return true;
	}

	public enum VoucherSection implements Section
	{
		VOUCHER;
		
		public String title()
		{
			return "eGutschein";
		}

		@Override
		public int columns() 
		{
			return 3;
		}

		@Override
		public IProperty[] properties() 
		{
			return VoucherProperty.properties(this);
		}
	}

	@Override
	public Section[] getSections() 
	{
		return VoucherSection.values();
	}

	@Override
	public int compareTo(ProviderUpdater other) 
	{
		return other.getRanking().compareTo(this.getRanking());
	}

	@Override
	public Integer getRanking() 
	{
		return Integer.valueOf(1000);
	}

	@Override
	public Collection<Position> getPositions(ConnectionService service,
			int max)
	{
		SalespointQuery salespointQuery = (SalespointQuery) service.getQuery(Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint == null)
		{
			return new ArrayList<Position>();
		}
		PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectProviderUpdates(salespoint, this.getProviderId(), max);
	}

	@Override
	public IStatus updatePositions(PersistenceService persistenceService,
			Collection<Position> positions) 
	{
		IStatus status = Status.OK_STATUS;
		for (Position position : positions)
		{
			status = updateProvider(position);
			if (status.getSeverity() == IStatus.CANCEL)
			{
				return status;
			}
		}
		return status;
	}

	@Override
	public Collection<Payment> getPayments(ConnectionService service, int max) 
	{
		SalespointQuery salespointQuery = (SalespointQuery) service.getQuery(Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint == null)
		{
			return new ArrayList<Payment>();
		}
		PaymentQuery query = (PaymentQuery) service.getQuery(Payment.class);
		return query.selectProviderUpdates(salespoint, getProviderId(), max);
	}

	@Override
	public IStatus updatePayments(PersistenceService persistenceService,
			Collection<Payment> payments) 
	{
		IStatus status = Status.OK_STATUS;
		for (Payment payment : payments)
		{
			status = updateProvider(payment);
			if (status.getSeverity() == IStatus.CANCEL)
			{
				return status;
			}
		}
		return status;
	}

	@Override
	public boolean doUpdatePositions() 
	{
		return true;
	}

	@Override
	public boolean doUpdatePayments() 
	{
		return true;
	}
	
	public enum VoucherProperty implements IProperty
	{
		URL, USERNAME, PASSWORD;

		public static Map<String, IProperty> asMap()
		{
			Map<String, IProperty> map = new HashMap<String, IProperty>();
			for (IProperty property : values())
			{
				map.put(property.key(), property);
			}
			return map;
		}

		private ProviderProperty persistedProperty;
		
		public void setPersistedProperty(ProviderProperty persistedProperty)
		{
			if (this.persistedProperty == null || this.persistedProperty.isDeleted() || this.persistedProperty.getSalespoint() == null)
			{
				this.persistedProperty = persistedProperty;
			}
		}
		
		public static IProperty[] properties(Section section)
		{
			List<IProperty> properties = new ArrayList<IProperty>();
			for (VoucherProperty property : VoucherProperty.values())
			{
				if (property.section().equals(section))
				{
					properties.add(property);
				}
			}
			return properties.toArray(new IProperty[0]);
		}

		public String control()
		{
			switch(this)
			{
			case URL:
			{
				return AvailableControl.TEXT.controlName();
			}
			case USERNAME:
			{
				return AvailableControl.TEXT.controlName();
			}
			case PASSWORD:
			{
				return AvailableControl.TEXT.controlName();
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}
		
		public Class<?> valueType()
		{
			return String.class;
		}
		
		public Properties controlProperties()
		{
			return new Properties();
		}

		@Override
		public String[] filter()
		{
			return new String[0];
		}

		@Override
		public String label()
		{
			switch(this)
			{
			case URL:
			{
				return "URL";
			}
			case USERNAME:
			{
				return "Benutzername";
			}
			case PASSWORD:
			{
				return "Passwort";
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		@Override
		public String label2()
		{
			return "";
		}

		@Override
		public String key() 
		{
			switch(this)
			{
			case URL:
			{
				return "url";
			}
			case USERNAME:
			{
				return "username";
			}
			case PASSWORD:
			{
				return "password";
			}
			default:
			{
				throw new RuntimeException("Invalid property");
			}
			}
		}

		@Override
		public String value()
		{
			if (persistedProperty != null && !persistedProperty.isDeleted())
			{
				return persistedProperty.getValue(defaultValue());
			}
			return defaultValue();
		}

		@Override
		public String defaultValue() 
		{
			switch(this)
			{
			case URL:
			{
				return "http://comelivres201.dnsalias.net:9001/GCDService/";
			}
			case USERNAME:
			{
				return "bookhit";
			}
			case PASSWORD:
			{
				return "bookhit";
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}

		@Override
		public boolean isDefaultValue(String value) 
		{
			return defaultValue().equals(value);
		}

		@Override
		public ProviderProperty getPersistedProperty()
		{
			return persistedProperty;
		}

		@Override
		public Section section() 
		{
			return VoucherSection.VOUCHER;
		}

		@Override
		public String value(IProperty property, org.eclipse.swt.widgets.Control control) 
		{
			for (AvailableControl availableControl : AvailableControl.values())
			{
				if (availableControl.controlName().equals(property.control()))
				{
					return availableControl.value(control);
				}
			}
			return null;
		}

		@Override
		public org.eclipse.swt.widgets.Control createControl(Composite parent, FormToolkit formToolkit, IDirtyable dirtyable, int cols, int[] validValues) 
		{
			for (AvailableControl availableControl : AvailableControl.values())
			{
				if (availableControl.controlName().equals(this.control()))
				{
					return availableControl.create(parent, formToolkit, this, dirtyable, cols, validValues);
				}
			}
			return null;
		}

		@Override
		public void set(IProperty property, org.eclipse.swt.widgets.Control control, String value) 
		{
			for (AvailableControl availableControl : AvailableControl.values())
			{
				if (availableControl.controlName().equals(property.control()))
				{
					availableControl.value(control, value);
				}
			}
		}

		@Override
		public String providerId() 
		{
			return Activator.getDefault().getContext().getBundle().getSymbolicName();
		}

		@Override
		public int[] validValues() 
		{
			return null;
		}
	}

	public enum Command implements ICommand
	{
		VERSION_WS, VERSION_DB, QUERY_BALANCE, CHARGE_ACCOUNT, CREDIT_ACCOUNT, RESERVE_AMOUNT, CANCEL_RESERVED_AMOUNT, CONFIRM_RESERVED_AMOUNT;

		public String command()
		{
			switch(this)
			{
			case VERSION_WS:
			{
				return "3345010";
			}
			case VERSION_DB:
			{
				return "3345020";
			}
			case QUERY_BALANCE:
			{
				return "3345110";
			}
			case CHARGE_ACCOUNT:
			{
				return "3345120";
			}
			case CREDIT_ACCOUNT:
			{
				return "3345130";
			}
			case RESERVE_AMOUNT:
			{
				return "3345220";
			}
			case CANCEL_RESERVED_AMOUNT:
			{
				return "3345230";
			}
			case CONFIRM_RESERVED_AMOUNT:
			{
				return "3345320";
			}
			default:
			{
				throw new RuntimeException("Invalid command");
			}
			}
		}

		public String description()
		{
			switch(this)
			{
			case VERSION_WS:
			{
				return "Colibri: Abfrage Version Webservice";
			}
			case VERSION_DB:
			{
				return "Colibri: Abfrage Version Datenbank";
			}
			case QUERY_BALANCE:
			{
				return "Colibri: Abfrage Kontostand";
			}
			case CHARGE_ACCOUNT:
			{
				return "Colibri: Betrag belasten";
			}
			case CREDIT_ACCOUNT:
			{
				return "Colibri: Betrag gutschreiben";
			}
			case RESERVE_AMOUNT:
			{
				return "Colibri: Betrag reservieren";
			}
			case CANCEL_RESERVED_AMOUNT:
			{
				return "Colibri: Reservierten Betrag freigeben";
			}
			case CONFIRM_RESERVED_AMOUNT:
			{
				return "Colibri: Reservierten Betrag abbuchen";
			}
			default:
			{
				throw new RuntimeException("Invalid command");
			}
			}
		}
	}
}
