/*
 * Created on 16.09.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.db;

import java.text.NumberFormat;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Customer
{
	
	private String id;
	private String name;
	private boolean hasCard;
	private Double account;
	
	public Customer()
	{
		this.id = "";
		this.name = "";
		this.hasCard = false;
		this.account = new Double(Table.DOUBLE_DEFAULT_ZERO);
	}
	
	public Customer(String id, String name, boolean card, Double account)
	{
		this.setId(id);
		this.name = name;
		this.hasCard = card;
		this.account = account;
	}
	
	/**
	 * @param string
	 */
	public void setId(String customerId)
	{
		try
		{
			Integer cid = new Integer(customerId);
			if (cid.intValue() == 0)
			{
				this.id = "";
			}
			else
			{
				this.id = cid.toString();
			}
			
		}
		catch (NumberFormatException e)
		{
			this.id = customerId;
		}
		finally
		{}
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public boolean hasCard()
	{
		return this.hasCard;
	}
	
	public Double getAccount()
	{
		return this.account;
	}
	
	public String getFormattedAccount()
	{
		String formattedAccount = "";
		// 10172
		NumberFormat nf = NumberFormat.getNumberInstance();
		// 10174
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		// 10174
		// 10172
		if (this.hasCard)
		{
			formattedAccount = nf.format(this.getAccount());
		}
		return formattedAccount;
	}
	
	/**
	 * @param double1
	 */
	public void setAccount(Double account)
	{
		this.account = account;
	}
	
	/**
	 * @param boolean1
	 */
	public void setHasCard(boolean card)
	{
		this.hasCard = card;
	}
	
	/**
	 * @param string
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	public static Double getAccount(Object account)
	{
		Double amount = new Double(0d);
		if (account instanceof Integer)
		{
			Integer acc = (Integer) account;
			amount = new Double(acc.doubleValue());
		}
		else if (account instanceof Double)
		{
			amount = (Double) account;
		}
		return amount;
	}
	
	public static Customer getCustomer(String customerId)
	{
		Customer customer = null;
		// if (GalileoServer.isUsed() && GalileoServer.getInstance().isActive())
		// {
		// try
		// {
		// Integer id = new Integer(customerId);
		// GalileoServer.getInstance().getCustomer(id);
		// customer = GalileoServer.getInstance().getCustomerObject();
		// customer.setId(customerId);
		// }
		// catch (NumberFormatException e)
		// {
		// customer = new Customer();
		// }
		// finally
		// {
		// }
		// }
		// else
		// {
		// customer = new Customer();
		// }
		return customer;
	}
}
