package ch.eugster.colibri.encryption.service;

public interface EncryptionService
{
	String decrypt(String encryptedMessage);

	String encrypt(String message);
}
