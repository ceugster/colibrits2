package ch.eugster.colibri.barcode.code;

import ch.eugster.colibri.persistence.model.Position;

public interface Barcode
{
	public static final int EAN13_LENGTH = 13;
	
	public static final int EAN13_EBOOK_LENGTH = 14;

	public static final String PREFIX_ISBN = "978";

	public static final String PREFIX_ORDER = "989";

	public static final String PREFIX_CUSTOMER = "992";
	
	public static final String PREFIX_EBOOK = "E";
	
	public static final String PREFIX_VOUCHER = "GCD";

	String getCode();

	String getDescription();

	/**
	 * Gibt den jeweiligen Teil (oder den gesamten Code) zurück, der in SubType
	 * beschrieben ist.
	 * 
	 * @return the code part of the detail
	 */
	boolean isEbook();
	
	String getDetail();

	String getName();

	String getProductCode();

	Type getType();

	int length();

	void updatePosition(final Position position);

	public enum Type
	{
		ARTICLE, ORDER, INVOICE, CUSTOMER;

		public String getArticle()
		{
			switch (this)
			{
				case ARTICLE:
				{
					return "Der";
				}
				case ORDER:
				{
					return "Die";
				}
				case INVOICE:
				{
					return "Die";
				}
				case CUSTOMER:
				{
					return "Der";
				}
				default:
				{
					return "";
				}
			}
		}

		@Override
		public String toString()
		{
			switch (this)
			{
				case ARTICLE:
				{
					return "Artikel";
				}
				case ORDER:
				{
					return "Bestellung";
				}
				case INVOICE:
				{
					return "Rechnung";
				}
				case CUSTOMER:
				{
					return "Kunde";
				}
				default:
				{
					return "";
				}
			}
		}
	}
}
