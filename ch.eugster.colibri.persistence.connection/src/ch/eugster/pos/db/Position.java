/*
 * Created on 04.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.util.Date;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Position extends Table
{
	
	// public Boolean expense = BOOLEAN_DEFAULT_FALSE;
	public int type = 0;
	public boolean galileoBook = Table.BOOLEAN_DEFAULT_FALSE;
	public boolean galileoBooked = Table.BOOLEAN_DEFAULT_FALSE;
	public String optCode = ""; //$NON-NLS-1$
	public String text = ""; //$NON-NLS-1$
	public String productId = ""; //$NON-NLS-1$
	public String author = ""; //$NON-NLS-1$
	public String title = ""; //$NON-NLS-1$
	public String publisher = ""; //$NON-NLS-1$
	public String isbn = ""; //$NON-NLS-1$
	public String bznr = ""; //$NON-NLS-1$
	public String productNumber = ""; //$NON-NLS-1$
	public boolean ordered = Table.BOOLEAN_DEFAULT_FALSE;
	public String orderId = ""; //$NON-NLS-1$
	// 10221
	public boolean payedInvoice = Table.BOOLEAN_DEFAULT_FALSE;
	public Integer invoice = new Integer(0);
	public Date invoiceDate = null;
	
	public Receipt receipt;
	public Long receiptId;
	// 10221
	// 10226
	// Der Mwst-Betrag (quantity * price * (1 - discount) / (100 + Satz) * Satz)
	public double tax;
	// 10226
	/*
	 * stock wird auf true gesetzt, wenn ein Titel von einem Kunden bestellt
	 * worden ist, der Titel dann aber vom Lager genommen wurde
	 * (Lagerabholfach).
	 */
	public boolean stock = Table.BOOLEAN_DEFAULT_FALSE;
	public boolean updateCustomerAccount = Table.BOOLEAN_DEFAULT_FALSE;
	/**
	 * Wird für die Geldentnahmen und -einlagen in Fremdwährung verwendet
	 */
	public double amountFC = 0d;
	
	/*
	 * 10224
	 * 
	 * Dieses Attribut dient lediglich als Gedächtnisstütze im Fall, wo nur
	 * Teilmengen aus einer Sammelbestellung verkauft werden Es dürfen nie mehr
	 * Exemplare getippt werden, als noch auf dem entsprechenden Titel im
	 * Abholfach angegeben sind (Totalmenge - bereits verkaufte Menge) Es wird
	 * NICHT in der Datenbank gespeichert, ist als TRANSIENT!
	 */
	public int orderedQuantity = 0;
	// 10224
	public int quantity = Table.INTEGER_DEFAULT_255;
	public double price = Table.DOUBLE_DEFAULT_ZERO;
	public double discount = Table.DOUBLE_DEFAULT_ZERO;
	public double amount = Table.DOUBLE_DEFAULT_ZERO;
	
	public Long productGroupId;
	public ProductGroup productGroup;
	public Long currentTaxId;
	public CurrentTax currentTax;
	
	public int positionState = Position.STATE_SALE;
	
	/**
	 * 
	 */
	public Position()
	{}
	
	// private Position(Receipt receipt)
	// {
	// super(receipt);
	// if (this.currentTax == null) this.currentTax =
	// ProductGroup.getDefaultGroup().getDefaultTax().getCurrentTax();
	// this.init(ProductGroup.getEmptyInstance(), this.currentTax);
	// }
	//	
	// private Position(Receipt receipt, CurrentTax currentTax, int quantity,
	// String optCode)
	// {
	// super(receipt);
	// this.init(ProductGroup.getEmptyInstance(), currentTax, quantity,
	// optCode);
	// }
	//	
	// public static Position getEmptyInstance()
	// {
	// Position position = new Position();
	// position.init(ProductGroup.getDefaultGroup(),
	// ProductGroup.getDefaultGroup().getDefaultTax().getCurrentTax());
	// return position;
	// }
	
	// public static Position getInstance()
	// {
	// return new Position(Receipt.getEmptyReceipt());
	// }
	//	
	// public static Position getInstance(Receipt receipt)
	// {
	// return new Position(receipt);
	// }
	//	
	// public static Position getInstance(Receipt receipt, CurrentTax
	// currentTax, int quantity, String optCode)
	// {
	// return new Position(receipt, currentTax, quantity, optCode);
	// }
	
	// private void init(ProductGroup pg, CurrentTax ct)
	// {
	//		this.init(pg, ct, 0, ""); //$NON-NLS-1$
	// }
	//	
	// private void init(ProductGroup pg, CurrentTax ct, int quantity, String
	// optCode)
	// {
	// this.optCode = optCode;
	// this.setProductGroup(pg);
	// this.setCurrentTax(ct);
	// this.setQuantity(quantity);
	// }
	//	
	// // 10224
	// public void setOrderedQuantity(int quantity)
	// {
	// this.orderedQuantity = quantity;
	// }
	//	
	// public int getOrderedQuantity()
	// {
	// return this.orderedQuantity;
	// }
	//	
	// // 10224
	//	
	public int compareTo(Object other)
	{
		if (other instanceof Position)
		{
			return this.getId().compareTo(((Position) other).getId());
		}
		return 0;
	}
	
	//	
	// public void setProductGroup(ProductGroup pg)
	// {
	// this.productGroup = pg;
	// this.productGroupId = pg.getId();
	// // expense = new Boolean(pg.type.equals(ProductGroup.TYPE_EXPENSE));
	// this.type = pg.type;
	// /*
	// * Testen, ob die Steuer (Umsatz/Vorsteuer) mit dem WG-Typ verträglich
	// * ist
	// */
	// // if (pg.getDefaultTax() == null || (expense.booleanValue() ==
	// // pg.getDefaultTax().getTaxTypeId().equals(new Long(1l)))) {
	// if (pg.getDefaultTax() == null || this.type == 2 &&
	// pg.getDefaultTax().getTaxType().code.equals("U"))
	// {
	// Tax tax = null;
	// if (pg.type == ProductGroup.TYPE_EXPENSE)
	// {
	// tax = Tax.getByTypeIdAndRateId(new Long(2l), new Long(3l), false);
	// }
	// else
	// {
	// tax = Tax.getByTypeIdAndRateId(new Long(1l), new Long(3l), false);
	// }
	// this.setCurrentTax(tax.getCurrentTax());
	// }
	// else
	// {
	// if (this.productId == null || this.productId.equals("") ||
	// this.productId.equals("0")
	// || this.currentTax == null)
	// {
	// this.setCurrentTax(pg.getDefaultTax().getCurrentTax());
	// }
	// }
	// // if (!pg.getDefaultTaxId().equals(ZERO_ID)) {
	// // if (!currentTax.getId().equals(ZERO_ID)) {
	// // this.setCurrentTax(pg.getDefaultTax().getCurrentTax());
	// // }
	// // }
	// if (pg.quantityProposal != 0)
	// {
	// if (pg.type == ProductGroup.TYPE_INPUT)
	// this.setQuantity(Math.abs(pg.quantityProposal));
	// else if (pg.type == ProductGroup.TYPE_WITHDRAW)
	// this.setQuantity(-Math.abs(pg.quantityProposal));
	// else if (this.quantity == 0)
	// this.setQuantity(pg.quantityProposal);
	// else if (this.positionState > 0 && this.quantity == 0)
	// this.setQuantity(pg.quantityProposal);
	//			
	// }
	// if (pg.priceProposal != 0d)
	// {
	// if (this.price == 0d)
	// {
	// this.setPrice(pg.priceProposal);
	// }
	// }
	//		//		if (!pg.optCodeProposal.equals("")) { //$NON-NLS-1$
	//		//			//			if (optCode.equals("") || optCode.equals("L")) { //$NON-NLS-1$
	// // this.optCode = pg.optCodeProposal;
	// // // }
	// // }
	// if (this.price != 0)
	// {
	// if (this.productGroup.type == ProductGroup.TYPE_EXPENSE)
	// this.price = -Math.abs(this.price);
	// else if (this.productGroup.type == ProductGroup.TYPE_WITHDRAW)
	// {
	// // this.price = new Double(-Math.abs(this.price));
	// }
	// else
	// this.price = Math.abs(this.price);
	// // this.price = NumberUtility.round(this.price,
	// ForeignCurrency.getDefaultCurrency().getCurrency()
	// // .getDefaultFractionDigits());
	// }
	// this.calculateAmount();
	// }
	
	// 10221
	public void setPayedInvoice(boolean payedInvoice)
	{
		this.payedInvoice = payedInvoice;
	}
	
	// 10221
	
	// 10221
	public boolean isPayedInvoice()
	{
		return this.payedInvoice;
	}
	
	// 10221
	
	// 10221
	public void setInvoiceNumber(Integer number)
	{
		this.invoice = number;
	}
	
	// 10221
	
	// 10221
	public Integer getInvoiceNumber()
	{
		return this.invoice;
	}
	
	// 10221
	
	// 10221
	public void setInvoiceDate(Date date)
	{
		this.invoiceDate = date;
	}
	
	// 10221
	
	// 10221
	public Date getInvoiceDate()
	{
		return this.invoiceDate;
	}
	
	public Long getProductGroupId()
	{
		return this.productGroupId;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
	
	public double getAmount()
	{
		return this.amount;
	}
	
	public static final int STATE_SALE = 1;
	public static final int STATE_TAKE_BACK = -1;
}
