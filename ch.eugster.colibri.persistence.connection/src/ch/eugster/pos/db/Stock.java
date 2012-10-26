/*
 * Created on 22.05.2008
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
public class Stock extends Table
{
	private Salespoint salespoint;
	private Long salespointId;
	
	private ForeignCurrency foreignCurrency;
	private Long foreignCurrencyId;
	
	private double stock;
	
	public Stock()
	{

	}
	
	public Stock(Salespoint salespoint, ForeignCurrency currency)
	{
		this.setSalespoint(salespoint);
		this.setForeignCurrency(currency);
		this.setStock(0d);
	}
	
	public void setSalespoint(Salespoint salespoint)
	{
		this.salespoint = salespoint;
		this.salespointId = salespoint == null ? null : salespoint.getId();
	}
	
	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}
	
	public Long getSalespointId()
	{
		return this.salespointId;
	}
	
	public void setForeignCurrency(ForeignCurrency foreignCurrency)
	{
		this.foreignCurrency = foreignCurrency;
		this.foreignCurrencyId = foreignCurrency == null ? null : foreignCurrency.getId();
	}
	
	public ForeignCurrency getForeignCurrency()
	{
		return this.foreignCurrency;
	}
	
	public Long getForeignCurrencyId()
	{
		return this.foreignCurrencyId;
	}
	
	public void setStock(double stock)
	{
		this.stock = stock;
	}
	
	public double getStock()
	{
		return this.stock;
	}
	
}
