package ch.eugster.colibri.encryption.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.encryption.service.EncryptionService;
import ch.eugster.colibri.encryption.simple.Activator;

public class EncryptionTest 
{
	
	private static final String MESSAGE = "colibri";
	
	private static final String CORRECT_ENCRYPTED_MESSAGE = "qZ0a5tbxjMepadJO9s3e1Q==";
	
	private static final String WRONG_ENCRYPTED_MESSAGE = "Z+FdzgLolBNN7MgwjxfWKTxkT7oomDJg";
	
	private ServiceTracker<EncryptionService, EncryptionService> tracker;
	
	private EncryptionService service;

	@Before
	public void setUp()
	{
		tracker = new ServiceTracker<EncryptionService, EncryptionService>(Activator.getContext(), EncryptionService.class, null);
		tracker.open();
		service = tracker.getService();
	}
	
	@After
	public void tearDown()
	{
		tracker.close();
	}

	@Test
	public void testCorrectDecryption() 
	{
		Assert.assertNotNull(service);
		String decryptedMessage = service.decrypt(CORRECT_ENCRYPTED_MESSAGE);
		Assert.assertEquals("Decrypted message does not equal message.", decryptedMessage, MESSAGE);
	}

//	@Test
//	public void testCorrectEncryption() 
//	{
////		Assert.assertNotNull(service);
//		String encryptedMessage = service.encrypt(MESSAGE);
//		Assert.assertEquals("Encrypted message does not equal encryption.", encryptedMessage, CORRECT_ENCRYPTED_MESSAGE);
//	}

	@Test
	public void testEmptyDecryption() 
	{
		Assert.assertNotNull(service);
		String decryptedMessage = service.decrypt("");
		Assert.assertEquals("Decrypted message does not equal message.", decryptedMessage, "");
	}

	@Test
	public void testEmptyEncryption() 
	{
//		Assert.assertNotNull(service);
		String encryptedMessage = service.encrypt("");
		Assert.assertEquals("Encrypted message does not equal encryption.", encryptedMessage, "");
	}

	@Test
	public void testNullDecryption() 
	{
		Assert.assertNotNull(service);
		String decryptedMessage = service.decrypt(null);
		Assert.assertEquals("Decrypted message does not equal message.", decryptedMessage, "");
	}

	@Test
	public void testNullEncryption() 
	{
//		Assert.assertNotNull(service);
		String encryptedMessage = service.encrypt(null);
		Assert.assertEquals("Encrypted message does not equal encryption.", encryptedMessage, "");
	}

	@Test
	public void testWrongDecryption() 
	{
		Assert.assertNotNull(service);
		String decryptedMessage = service.decrypt(WRONG_ENCRYPTED_MESSAGE);
		Assert.assertNotSame("Decrypted message equals message.", decryptedMessage, MESSAGE);
	}

	@Test
	public void testWrongEncryption() 
	{
		Assert.assertNotNull(service);
		String encryptedMessage = service.encrypt(MESSAGE);
		Assert.assertNotSame("Encrypted message equals encryption.", encryptedMessage, WRONG_ENCRYPTED_MESSAGE);
	}

}
