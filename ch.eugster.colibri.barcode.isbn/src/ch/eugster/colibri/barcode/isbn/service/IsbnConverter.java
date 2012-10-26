package ch.eugster.colibri.barcode.isbn.service;

import ch.eugster.colibri.barcode.ean13.code.Ean13;
import ch.eugster.colibri.barcode.isbn.code.Isbn;

public interface IsbnConverter
{
	Ean13 convert(Isbn isbn);
}
