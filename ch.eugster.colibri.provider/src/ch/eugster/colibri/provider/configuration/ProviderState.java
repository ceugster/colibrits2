package ch.eugster.colibri.provider.configuration;

public interface ProviderState 
{
	public static final int OPEN = 0;
	
	public static final int RESERVED = 1;
	
	public static final int BOOKED = 2;
	
	public static final int ORDER_DELETED = 4;

	public static final int INVOICE_PAYED = 8;
	
	public static final int TRANSACTION_WRITTEN = 16;
	
	public static final int BOOK_ERROR = 32;
	
	public static final int DELETE_ORDER_ERROR = 64;
	
	public static final int PAY_INVOICE_ERROR = 128;
	
	public static final int WRITE_TRANSACTION_ERROR = 256;
}
