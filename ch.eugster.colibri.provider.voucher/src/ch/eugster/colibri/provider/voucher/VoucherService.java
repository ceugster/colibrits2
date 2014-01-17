package ch.eugster.colibri.provider.voucher;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.eugster.colibri.provider.configuration.IProperty;

public interface VoucherService
{
	void testService();
	
	Result getAccountBalance(String code);

	Result getDatabaseVersion();

	Result getWebserviceVersion();

	Result creditAccount(String code, double amount);
	
	Result chargeAccount(String code, double amount);

	Result reserveAmount(String code, double amount);

	Result confirmReservedAmount(String code, double amount);

	Result cancelReservedAmount(String code, double amount);

	Map<String, IProperty> getProperties();
	
	String getDiscriminator();

	String getProviderId();
	
	public interface ICommand
	{
		String command();
		
		String description();
	}
	
	public class Result
	{
		private ICommand command;
		
		private String version;
		
		private double amount;
		
		private IStatus status;
		
		public Result(ICommand command, double amount)
		{
			this.command = command;
			this.amount = amount;
			this.status = Status.OK_STATUS;
		}

		public Result(ICommand command, String version)
		{
			this.command = command;
			this.version = version;
			this.status = Status.OK_STATUS;
		}

		public Result(ICommand command, int code, String message, String pluginId, Throwable e)
		{
			this.command = command;
			this.amount = 0D;
			this.status = new Status(IStatus.ERROR, pluginId, code, message, e);
		}

		public ICommand getCommand()
		{
			return command;
		}
		
		public String getVersion()
		{
			return version;
		}
		
		public int getSeverity()
		{
			return status.getSeverity();
		}
		
		public int getErrorCode()
		{
			return status.getCode();
		}
		
		public String getErrorMessage()
		{
			return status.getMessage();
		}
		
		public double getAmount()
		{
			return amount;
		}
		
		public boolean isOK()
		{
			return status.isOK();
		}
		
		public Throwable getThrowable()
		{
			return status.getException();
		}
		
		public IStatus getStatus()
		{
			return this.status;
		}
	}

	IStatus testConnection(Map<String, IProperty> properties);

}
