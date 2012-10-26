/*
 * Created on 05.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.db;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Coin extends Table
{
	
	public double value = 0d;
	public int quantity = 0;
	@SuppressWarnings("unused")
	private Long foreignCurrencyId;
	@SuppressWarnings("unused")
	private ForeignCurrency foreignCurrency;
	public double amount = 0d;
	
	/**
	 * 
	 */
	public Coin()
	{
		super();
	}
	
	public void setForeignCurrency(ForeignCurrency currency)
	{
		this.foreignCurrency = currency;
		this.foreignCurrencyId = currency.getId();
	}
	
}
