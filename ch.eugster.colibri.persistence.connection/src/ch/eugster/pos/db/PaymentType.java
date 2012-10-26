/*
 * Created on 30.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PaymentType extends Table
{
	
	public boolean removeable = true;
	public String name = ""; //$NON-NLS-1$
	public String code = ""; //$NON-NLS-1$
	public String account = ""; //$NON-NLS-1$
	public boolean voucher = false;
	public boolean cash = false;
	public boolean back = false; // Data Version 21 @2008-05-21 Build 201
	public String exportId = ""; // Data Version 6 @2005-11-15 Build 76
	public boolean openCashdrawer = true; // Data Version 19 @2008-03-14 Build
	// 196
	public int sort;
	
	public Long foreignCurrencyId = null;
	public ForeignCurrency foreignCurrency = new ForeignCurrency();
	
	public Long paymentTypeGroupId = null;
	public PaymentTypeGroup paymentTypeGroup = new PaymentTypeGroup();
	
	public PaymentType()
	{
		super();
	}
	
	public void setPaymentTypeGroup(PaymentTypeGroup group)
	{
		if (group == null)
		{
			group = new PaymentTypeGroup();
		}
		this.paymentTypeGroup = group;
		this.paymentTypeGroupId = group.getId();
	}
	
	public Long getPaymentTypeGroupId()
	{
		return this.paymentTypeGroupId;
	}
	
	public PaymentTypeGroup getPaymentTypeGroup()
	{
		return this.paymentTypeGroup;
	}
	
	public void setForeignCurrency(ForeignCurrency currency)
	{
		this.foreignCurrency = currency;
		// this.foreignCurrencyId = currency.getId();
	}
	
	// public void setForeignCurrency(Long id)
	// {
	// this.setForeignCurrency(ForeignCurrency.getById(id));
	// }
	//	
	// public ForeignCurrency getForeignCurrency()
	// {
	// if (this.foreignCurrency == null)
	// {
	// this.foreignCurrency = ForeignCurrency.getById(this.foreignCurrencyId);
	// }
	// return this.foreignCurrency;
	// }
	//	
	public Long getForeignCurrencyId()
	{
		return this.foreignCurrencyId;
	}
	
	// private String getDefaultCurrency() {
	// return
	// NumberFormat.getCurrencyInstance(Config.getInstance().getDefaultLocale()).getCurrency().getCurrencyCode();
	// }
	//	
	public String getCurrencySymbol()
	{
		return this.foreignCurrency.getCurrency().getSymbol();
	}
	
	public String getCurrencyCode()
	{
		return this.foreignCurrency.getCurrency().getCurrencyCode();
	}
	
	public int getCurrencyDefaultFractionDigits()
	{
		return this.foreignCurrency.getCurrency().getDefaultFractionDigits();
	}
	
}
