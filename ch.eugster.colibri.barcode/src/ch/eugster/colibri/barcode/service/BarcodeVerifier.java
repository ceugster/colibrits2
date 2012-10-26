package ch.eugster.colibri.barcode.service;

import ch.eugster.colibri.barcode.code.Barcode;

public interface BarcodeVerifier
{
	String getBarcodeDescription();

	String getProperty(String key);

	Barcode verify(String code);
}
