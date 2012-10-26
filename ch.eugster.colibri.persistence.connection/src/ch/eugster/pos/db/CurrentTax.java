/*
 * Created on 07.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ch.eugster.pos.db;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author administrator
 */
public class CurrentTax extends Table
{
	
	public String fibuId = ""; //$NON-NLS-1$
	public double percentage = Table.DOUBLE_DEFAULT_ZERO;
	public Timestamp validationDate = new Timestamp(new Date(0l).getTime());
	
	private Long taxId;
	@SuppressWarnings("unused")
	private Tax tax;
	
	public CurrentTax()
	{
		this.setTax(new Tax());
	}
	
	public void setTax(Tax tax)
	{
		this.tax = tax;
		this.taxId = tax.getId();
	}
	
	public Long getTaxId()
	{
		return this.taxId;
	}
	
	public Double calculateTax(Double amount)
	{
		return new Double(this.calculateTax(amount.doubleValue()));
	}
	
	// public double calculateTax(double amount)
	// {
	// return Table.round(amount / 100 * this.percentage,
	// ForeignCurrency.getDefaultCurrency().getCurrency()
	// .getDefaultFractionDigits());
	// }
	//	
	public void setValidationDate(Date date)
	{
		this.validationDate = new Timestamp(date.getTime());
	}
	
	public Date getValidationDate()
	{
		return new Date(this.validationDate.getTime());
	}
}
