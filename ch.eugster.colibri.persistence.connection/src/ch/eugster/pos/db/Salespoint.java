/*
 * Created on 04.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Salespoint extends Table
{

	public String name = ""; //$NON-NLS-1$

	public String place = ""; //$NON-NLS-1$

	public double stock = 0d;

	public boolean variableStock = false; // 10183

	public Long currentReceiptId = null;

	public Timestamp currentDate = new Timestamp(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis());

	public boolean active = false;

	public String exportId = "";

	public String host;

	public boolean migrate;

	@SuppressWarnings("rawtypes")
	public Collection stocks = new ArrayList();

	@SuppressWarnings("unused")
	private Long currentReceiptNumber;

	/**
	 * Beleglaufnummer im Failover-Modus
	 * 
	 */
	private static long currentTemporaryReceiptNumber = 0l;

	@SuppressWarnings("unused")
	private static Salespoint current = null;

	// 10193
	// public Long getNextReceiptNumber()
	// {
	// if (Database.getCurrent().equals(Database.getTemporary()))
	// {
	// return this.getNextTemporaryReceiptNumber();
	// }
	// else
	// {
	// if (this.currentReceiptId == null || this.currentReceiptId.longValue() <
	// 1l)
	// {
	// this.currentReceiptNumber = new Long(1l);
	// }
	// else
	// {
	// this.currentReceiptNumber = new Long(this.currentReceiptId.longValue() +
	// 1);
	// }
	// return this.currentReceiptNumber;
	// }
	// }

	// 10193

	/**
	 * 
	 */
	public Salespoint()
	{
		super();
	}

	// 10193

	public Long getNextTemporaryReceiptNumber()
	{
		Salespoint.currentTemporaryReceiptNumber++;
		return new Long(Salespoint.currentTemporaryReceiptNumber);
	}

	// 10226
	@SuppressWarnings("rawtypes")
	public Collection getStocks()
	{
		return this.stocks;
	}
}
