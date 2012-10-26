package ch.eugster.colibri.encryption.simple.service;

import org.jasypt.util.text.BasicTextEncryptor;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.encryption.service.EncryptionService;

public class SimpleEncryptionService implements EncryptionService
{
	private LogService logService;

	private BasicTextEncryptor encryptor;

	/**
	 * 
	 * @param message
	 * @return empty string if no encryptor or null or empty message else
	 *         returns decrypted message
	 */
	@Override
	public String decrypt(final String encryptedMessage)
	{
		if ((this.getEncryptor() == null) || (encryptedMessage == null) || encryptedMessage.isEmpty())
		{
			return "";
		}
		try
		{
			return this.getEncryptor().decrypt(encryptedMessage);
		}
		catch (Exception e)
		{
			return encryptedMessage;
		}
	}

	/**
	 * 
	 * @param message
	 * @return empty string if no encryptor or null or empty message else
	 *         returns encrypted message
	 */
	@Override
	public String encrypt(final String message)
	{
		if ((this.getEncryptor() == null) || (message == null) || message.isEmpty())
		{
			return "";
		}
		return this.getEncryptor().encrypt(message);
	}

	protected void activate(final ComponentContext context)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + context.getProperties().get("component.name") + " aktiviert.");
		}
	}

	protected void deactivate(final ComponentContext context)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + context.getProperties().get("component.name") + " deaktiviert.");
		}
	}

	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	private BasicTextEncryptor getEncryptor()
	{
		if (this.encryptor == null)
		{
			this.encryptor = new BasicTextEncryptor();
			this.encryptor.setPassword("_5 zY<!kqcx9-3");
		}
		return this.encryptor;
	}
}
