/*
 * Created on 19.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.util.Currency;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ForeignCurrency extends Table
{
	
	public String code = "CHF"; //$NON-NLS-1$
	public String name = ""; //$NON-NLS-1$
	public String region = ""; //$NON-NLS-1$
	public double quotation = 1d;
	public String account = ""; //$NON-NLS-1$
	public double roundFactor = .01d;
	
	// private Collection coins = new ArrayList();
	/*
	 * Only for internal use (set image in CurrencyViewer of Administrator
	 */
	public boolean isUsed = false;
	
	/**
	 * 
	 */
	public ForeignCurrency()
	{
		super();
	}
	
	public ForeignCurrency(String code, String name, String region)
	{
		this(Currency.getInstance(code), name, region);
	}
	
	public ForeignCurrency(Currency currency, String name, String region)
	{
		super();
		this.name = name;
		this.region = region;
	}
	
	// public Collection getCoins()
	// {
	// return coins;
	// }
	
	public Currency getCurrency()
	{
		return Currency.getInstance(this.code);
	}
	
	@SuppressWarnings("unused")
	private static ForeignCurrency defaultCurrency;
}
