package ch.eugster.colibri.barcode.code;

import ch.eugster.colibri.persistence.model.Position;

public interface Barcode
{
	String getCode();

	String getDescription();

	/**
	 * Gibt den jeweiligen Teil (oder den gesamten Code) zurück, der in SubType
	 * beschrieben ist.
	 * 
	 * @return the code part of the detail
	 */
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
