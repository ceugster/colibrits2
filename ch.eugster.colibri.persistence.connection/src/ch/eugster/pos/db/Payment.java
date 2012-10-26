/*
 * Created on 04.05.2003
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
public class Payment extends Table
{
	
	/*
	 * der auf den Rundungsfaktor der Landeswährung gerundete Betrag
	 */
	public double amount = Table.DOUBLE_DEFAULT_ZERO;
	/*
	 * der genaue eingegebene Betrag, nach dem Umrechnungsfaktor 'quotation' in
	 * Landeswährung umgerechnet
	 */
	public double amountFC = Table.DOUBLE_DEFAULT_ZERO;
	/*
	 * Der Umrechnungfaktor - der Kurs der Währung - von der Landes- währung aus
	 * gesehen, der zur Umrechnung benutzt wurde
	 */
	public double quotation = Table.DOUBLE_DEFAULT_ONE;
	/*
	 * Der Rundungsfaktor, der für die Landeswährung zum Zeitpunkt der Erfassung
	 * galt
	 */
	public double roundFactor = 0.01d;
	/*
	 * Der Rundungsfaktor, der für die Fremdwährung zum Zeitpunkt der Erfassung
	 * galt
	 */
	public double roundFactorFC = 0.01d;
	
	public boolean isInputOrWithdraw = false;
	
	public boolean back = false; // 10226
	public Long settlement = null;
	public Long salespointId = null;
	
	public Long paymentTypeId = null;
	public PaymentType paymentType;
	
	public Long foreignCurrencyId = null;
	public ForeignCurrency foreignCurrency;
	
	public Receipt receipt;
	public Long receiptId;
	
	/**
	 * 
	 */
	public Payment()
	{}
	
	/**
	 * 
	 */
	// protected Payment(Receipt receipt)
	// {
	// super(receipt);
	// this.init(PaymentType.getPaymentTypeCash());
	// }
	
	/**
	 * 
	 */
	// protected Payment(Receipt receipt, PaymentType type)
	// {
	// super(receipt);
	// this.init(type);
	// }
	
	// public static Payment getEmptyInstance()
	// {
	// Payment payment = new Payment();
	// payment.init(PaymentType.getPaymentTypeCash());
	// return payment;
	// }
	//	
	// public static Payment getInstance()
	// {
	// return new Payment(Receipt.getEmptyReceipt());
	// }
	//	
	// public static Payment getInstance(Receipt receipt)
	// {
	// return new Payment(receipt);
	// }
	
	// public static Payment getInstance(Receipt receipt, PaymentType
	// paymentType)
	// {
	// return new Payment(receipt, paymentType);
	// }
	
	// private void init(PaymentType paymentType)
	// {
	// this.setForeignCurrency(paymentType.getForeignCurrency());
	// this.salespointId = this.receipt.getSalespointId();
	// }
	
	public int compareTo(Object o)
	{
		Payment p = (Payment) o;
		return (int) p.getPaymentType().getId().longValue() - (int) this.getPaymentType().getId().longValue();
	}
	
	public double getAmount()
	{
		return this.amount;
	}
	
	public boolean isVoucher()
	{
		return this.paymentType.voucher;
	}
	
	public double getAmountFC()
	{
		return this.amountFC;
	}
	
	public void setQuotation(double q)
	{
		this.quotation = q;
	}
	
	public double getQuotation()
	{
		return this.quotation;
	}
	
	/**
	 * @return
	 */
	public double getRoundFactor()
	{
		return this.roundFactor;
	}
	
	/**
	 * @return
	 */
	public double getRoundFactorFC()
	{
		return this.roundFactorFC;
	}
	
	// 10226
	public void setBack(boolean isMoneyBack)
	{
		this.back = isMoneyBack;
	}
	
	public boolean isBack()
	{
		return this.back;
	}
	
	public void setSettlement(Long settlement)
	{
		this.settlement = settlement;
	}
	
	public Long getSettlement()
	{
		return this.settlement;
	}
	
	// 10226
	
	public PaymentType getPaymentType()
	{
		return this.paymentType;
	}
	
	public Long getPaymentTypeId()
	{
		return this.paymentTypeId;
	}
	
	public void setForeignCurrency(ForeignCurrency foreignCurrency)
	{
		this.foreignCurrency = foreignCurrency;
		this.foreignCurrencyId = foreignCurrency.getId();
		this.quotation = foreignCurrency.quotation;
		this.roundFactorFC = foreignCurrency.roundFactor;
	}
	
	public ForeignCurrency getForeignCurrency()
	{
		return this.foreignCurrency;
	}
	
	public Long getForeignCurrencyId()
	{
		return this.foreignCurrencyId;
	}
	
}
