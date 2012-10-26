/*
 * Created on 01.07.2003
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
@SuppressWarnings("rawtypes")
public abstract class ReceiptChild extends Table implements Comparable
{
	
	public ReceiptChild(Receipt receipt)
	{
		this.init(receipt);
	}
	
	private void init(Receipt receipt)
	{
		this.setReceipt(receipt);
	}
	
	public void setReceipt(Receipt receipt)
	{
		this.receipt = receipt;
	}
	
	public Receipt getReceipt()
	{
		return this.receipt;
	}
	
	public Long getReceiptId()
	{
		return this.receiptId;
	}
	
	protected Receipt receipt;
	protected Long receiptId;
}
