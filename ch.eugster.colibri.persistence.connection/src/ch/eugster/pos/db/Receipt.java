/*
 * Created on 04.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.ojb.broker.util.collections.RemovalAwareCollection;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Receipt extends Table
{
	
	public int status = Receipt.RECEIPT_STATE_NEW;
	
	/**
	 * Nur noch aus Kompatibilitätsgründen anwesend. Wird nicht mehr benutzt.
	 */
	public double amount = Table.DOUBLE_DEFAULT_ZERO;
	
	public String number = ""; //$NON-NLS-1$
	public Long settlement;
	public Long transactionId;
	public Long bookingId;
	public Salespoint salespoint;
	public Long salespointId;
	public User user = new User();
	public Long userId = this.user.getId();
	public boolean transferred = false;
	
	public ForeignCurrency foreignCurrency;
	// = ForeignCurrency.getDefaultCurrency();
	public Long foreignCurrencyId;
	
	public RemovalAwareCollection positions = new RemovalAwareCollection();
	public RemovalAwareCollection payments = new RemovalAwareCollection();
	
	public String customerId = ""; //$NON-NLS-1$
	public Customer customer = new Customer();
	
	/**
	 * 
	 */
	public Receipt()
	{}
	
	private Receipt(Salespoint salespoint, User user, ForeignCurrency foreignCurrency)
	{
		this.init(salespoint, user, foreignCurrency);
	}
	
	public static Receipt getEmptyReceipt()
	{
		return new Receipt();
	}
	
	public static Receipt getReceipt(Salespoint salespoint, User user, ForeignCurrency foreignCurrency)
	{
		return new Receipt(salespoint, user, foreignCurrency);
	}
	
	/**
	 * inits a new Receipt
	 * 
	 * @param salespoint
	 *            the current salespoint object
	 * @param user
	 *            the current user
	 */
	private void init(Salespoint salespoint, User user, ForeignCurrency foreignCurrency)
	{
		this.setSalespoint(salespoint);
		this.setUser(user);
		this.setNumber();
		// this.setCustomer();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setId(Long id)
	{
		super.setId(id);
		Position[] pos = (Position[]) this.positions.toArray(new Position[0]);
		for (int i = 0; i < pos.length; i++)
		{
			pos[i].setId(null);
			pos[i].receiptId = id.longValue() > 0l ? id : null;
		}
		Payment[] pay = (Payment[]) this.payments.toArray(new Payment[0]);
		for (int i = 0; i < pay.length; i++)
		{
			pay[i].setId(null);
			pay[i].receiptId = id.longValue() > 0l ? id : null;
		}
	}
	
	public Date getDate()
	{
		return new Date(this.timestamp.getTime());
	}
	
	public Time getTime()
	{
		return new Time(this.timestamp.getTime());
	}
	
	/**
	 * sets the current salespoint
	 * 
	 * @param salespoint
	 */
	public void setSalespoint(Salespoint salespoint)
	{
		this.salespoint = salespoint;
		this.salespointId = salespoint.getId();
	}
	
	/**
	 * returns the current salespoint
	 * 
	 * @return the current salespoint
	 */
	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}
	
	/**
	 * returns the id of the current salespoint
	 * 
	 * @return the current salespoint id
	 */
	public Long getSalespointId()
	{
		return this.salespointId;
	}
	
	/**
	 * sets the current user
	 * 
	 * @param user
	 *            the current user
	 */
	public void setUser(User user)
	{
		this.user = user;
		this.userId = user.getId();
	}
	
	/**
	 * returns the current user
	 * 
	 * @return the current user
	 */
	public User getUser()
	{
		return this.user;
	}
	
	/**
	 * returns the id of current user
	 * 
	 * @return the id of current user
	 */
	public Long getUserId()
	{
		return this.userId;
	}
	
	// public void setDefaultCurrency(ForeignCurrency currency) {
	// this.defaultCurrency = currency;
	// this.defaultCurrencyId = currency.getId();
	// }
	//	
	// public Long getDefaultCurrencyId() {
	// return defaultCurrencyId;
	// }
	//	
	// public ForeignCurrency getDefaultCurrency() {
	// return defaultCurrency;
	// }
	//	
	public void setDefaultCurrency(ForeignCurrency foreignCurrency)
	{
		this.foreignCurrency = foreignCurrency;
		this.foreignCurrencyId = foreignCurrency.getId();
	}
	
	public Long getDefaultCurrencyId()
	{
		return this.foreignCurrencyId;
	}
	
	public ForeignCurrency getDefaultCurrency()
	{
		return this.foreignCurrency;
	}
	
	/**
	 * sets the current receiptnumber. The receipt number is a String value,
	 * computed from the current time
	 * 
	 */
	public void setNumber()
	{
		this.number = "";
	}
	
	/**
	 * sets the receiptnumber. The receipt number is a String value
	 * 
	 */
	public void setNumber(String number)
	{
		this.number = number;
	}
	
	/**
	 * returns the receipt's number
	 * 
	 * @return the receipts number
	 * @see ch.eugster.pos.db.Receipt#setNumber
	 */
	public String getNumber()
	{
		return this.number;
	}
	
	// public String getFormattedNumber()
	// {
	// int length =
	// Integer.parseInt(Config.getInstance().getReceiptHeaderNumberLength());
	// if (length <= 0)
	// return this.getNumber();
	// else
	// return this.getNumber().substring(this.getNumber().length() - length);
	// }
	
	/**
	 * sets the settlement
	 * 
	 * @param settlement
	 *            the settlement that is set
	 */
	public void setSettlement(Long settlement)
	{
		this.settlement = settlement;
	}
	
	/**
	 * returns the settlement
	 * 
	 * @return the settlement
	 */
	public Long getSettlement()
	{
		return this.settlement;
	}
	
	public void setTransactionId(Long transactionId)
	{
		this.transactionId = transactionId;
	}
	
	public Long getTransactionId()
	{
		return this.transactionId;
	}
	
	public void setBookingId(Long bookingId)
	{
		this.bookingId = bookingId;
	}
	
	public Long getBookingId()
	{
		return this.bookingId;
	}
	
	/**
	 * returns the amount of the receipt
	 * 
	 * @return the amount of the receipt
	 */
	// public double getAmount()
	// {
	// double a = 0d;
	// Position[] p = (Position[]) this.positions.toArray(new Position[0]);
	// for (int i = 0; i < p.length; i++)
	// {
	// a += p[i].getAmount();
	// }
	// return NumberUtility.round(a,
	// ForeignCurrency.getDefaultCurrency().roundFactor);
	// }
	
	/**
	 * returns the sum of values of position.amountFC
	 * 
	 * @return the sum of values of position.amountFC
	 */
	// private double getAmountFC()
	// {
	// double amount = 0d;
	// Position[] p = (Position[]) this.positions.toArray(new Position[0]);
	// for (int i = 0; i < p.length; i++)
	// {
	// if (p[i].getProductGroup().type == ProductGroup.TYPE_INPUT)
	// {
	// amount += p[i].amountFC * p[i].getQuantity();
	// }
	// else if (p[i].getProductGroup().type == ProductGroup.TYPE_WITHDRAW)
	// {
	// amount += p[i].amountFC * p[i].getQuantity();
	// }
	// else
	// {
	// amount += 0d;
	// }
	// }
	// return amount;
	// }
	
	/**
	 * returns the amount of the receipt in foreign currency value
	 * 
	 * @return the amount of the receipt
	 */
	// public double getAmountFC(ForeignCurrency currency)
	// {
	// double amount = this.getAmountFC();
	// if (amount == 0d)
	// return NumberUtility.round(this.getAmount() / currency.quotation,
	// currency.roundFactor);
	// else
	// return amount;
	// }
	
	/**
	 * gibt das Total der Zahlungen in der Landeswährung zurück
	 * 
	 * @return the total of payments without any backmoney
	 */
	public double getPayment()
	{
		double d = 0d;
		@SuppressWarnings("rawtypes")
		Iterator i = this.payments.iterator();
		while (i.hasNext())
		{
			Payment p = (Payment) i.next();
			d = d + p.getAmount();
		}
		return d;
	}
	
	/**
	 * gibt das Total der Zahlungen in der Landeswährung zurück
	 * 
	 * @return the total of payments without any backmoney
	 */
	// public Double getPaymentDiff() {
	// //TODO
	// double d = 0d;
	// Iterator i = payments.iterator();
	// while (i.hasNext()) {
	// Payment p = (Payment)i.next();
	// d = d + p.getAmountFC() * p.getQuotation();
	// }
	// return new Double(d);
	// }
	/**
	 * gibt das Total der Zahlungen - ohne Rueckgeld - basierend auf der
	 * übergebenen Fremdwaehrung, aber mit den Umrechnungsfaktoren der
	 * Fremdwährung in die Landeswährung konvertiert
	 * 
	 * @return the total of payments without any backmoney
	 */
	// 10230 public Double getPaymentAmountFC(ForeignCurrency foreignCurrency) {
	public double getPaymentAmountFC()
	{
		double d = 0d;
		@SuppressWarnings("rawtypes")
		Iterator i = this.payments.iterator();
		while (i.hasNext())
		{
			Payment p = (Payment) i.next();
			d += p.getAmountFC();
		}
		return d;
	}
	
	// public double getPaymentAmountFC(ForeignCurrency currentCurrency)
	// {
	// double d = 0d;
	// Iterator i = this.payments.iterator();
	// while (i.hasNext())
	// {
	// Payment p = (Payment) i.next();
	// if
	// (!currentCurrency.getId().equals(ForeignCurrency.getDefaultCurrency()))
	// {
	// if
	// (p.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
	// d += p.getAmountFC() / currentCurrency.quotation;
	// else
	// d += p.getAmountFC();
	// }
	// }
	// return d;
	// }
	
	/**
	 * returns the total of backmoney
	 * 
	 * @return the total of backmoney
	 */
	public double getBack()
	{
		double d = 0d;
		@SuppressWarnings("rawtypes")
		Iterator i = this.payments.iterator();
		while (i.hasNext())
		{
			Payment p = (Payment) i.next();
			if (p.isBack())
			{
				d = d + p.getAmount();
			}
		}
		return d;
	}
	
	/**
	 * returns the total of backmoney
	 * 
	 * @return the total of backmoney
	 */
	public double getBackFC()
	{
		double d = 0d;
		@SuppressWarnings("rawtypes")
		Iterator i = this.payments.iterator();
		while (i.hasNext())
		{
			Payment p = (Payment) i.next();
			d = d + p.getAmountFC();
		}
		return d;
	}
	
	/**
	 * @return
	 */
	public boolean getTransferred()
	{
		return this.transferred;
	}
	
	/**
	 * examines the subtype of <code>ReceiptChild</code> and adds it to the
	 * list. There are currently two subtypes <code>position</code> and
	 * <code>payment</code>.
	 * 
	 * @param child
	 *            the receipt's child to add
	 * @return <code>true</code> if successfull added
	 * @see ch.eugster.pos.db.Receipt#addPosition
	 * @see ch.eugster.pos.db.Receipt@addPayment
	 */
	// public boolean addChild(ReceiptChild child)
	// {
	// boolean added = false;
	// if (child instanceof Position)
	// {
	// this.addPosition((Position) child);
	// added = true;
	// }
	// else if (child instanceof Payment)
	// {
	// added = this.addPayment((Payment) child);
	// }
	// return added;
	// }
	
	/**
	 * adds a <code>position</code> to the list
	 * 
	 * @param position
	 *            the <code>position</code> to add
	 * @return <code>true</code> if successfull added
	 */
	// public int addPosition(Position position)
	// {
	// int pos = 0;
	// int i = this.exists(position);
	// if (i > -1)
	// {
	// int qty0 = 0;
	// if (position.orderId != null && !position.orderId.equals(""))
	// {
	// qty0 = Position.countOrderedItemsUsed(position.orderId);
	// qty0 += position.getOrderedQuantity();
	// }
	// int qty1 = ((Position) this.positions.get(i)).getQuantity();
	// qty1 += position.getQuantity();
	// if (position.orderId != null && !position.orderId.equals(""))
	// if (qty1 > qty0)
	// ((Position) this.positions.get(i)).setQuantity(qty0);
	// else
	// ((Position) this.positions.get(i)).setQuantity(qty1);
	// else
	// ((Position) this.positions.get(i)).setQuantity(qty1);
	// }
	// else
	// {
	// this.positions.add(pos, position);
	// }
	// // amount = computeAmount();
	// return pos;
	// }
	//	
	// private int exists(Position p)
	// {
	// for (int i = 0; i < this.positions.size(); i++)
	// {
	// Position tmp = (Position) this.positions.get(i);
	// if (tmp.productId.equals(p.productId))
	// {
	// if (tmp.getProductGroupId().equals(p.getProductGroupId()))
	// {
	// if (tmp.getCurrentTaxId().equals(p.getCurrentTaxId()))
	// {
	// if (tmp.ordered == p.ordered)
	// {
	// if (tmp.orderId.equals(p.orderId))
	// {
	// if (tmp.getPrice() == p.getPrice())
	// {
	// if (tmp.getDiscount() == p.getDiscount())
	// {
	// if (tmp.getQuantity() + p.getQuantity() == 0)
	// return -1;
	// else
	// {
	//											
	// return i;
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// return -1;
	// }
	
	/**
	 * adds a <code>payment</code> to the list
	 * 
	 * @param payment
	 *            the <code>payment</code> to add
	 * @return <code>true</code> if successfull added
	 */
	public boolean addPayment(Payment p)
	{
		@SuppressWarnings("unchecked")
		boolean added = this.payments.add(p);
		// if (added) {
		// payment = computePayment();
		// paymentFC = computePaymentFC();
		// }
		return added;
	}
	
	/**
	 * replaces an existing Position with a new one.
	 * 
	 * @param index
	 *            the index in the list where to find the existing position
	 * @param p
	 *            the new position
	 * @return the exchanged position in the list
	 */
	public Position setPosition(int index, Position p)
	{
		Position old = (Position) this.positions.get(index);
		if (old != null)
		{
			p.setId(old.getId());
		}
		@SuppressWarnings("unchecked")
		Position q = (Position) this.positions.set(index, p);
		// amount = computeAmount();
		return q;
	}
	
	/**
	 * replaces an existing Payment with a new one.
	 * 
	 * @param index
	 *            the index in the list where to find the existing payment
	 * @param p
	 *            the new payment
	 * @return the exchanged payment in the list
	 */
	public Payment setPayment(int index, Payment p)
	{
		@SuppressWarnings("unchecked")
		Payment q = (Payment) this.payments.set(index, p);
		// payment = computePayment();
		// paymentFC = computePaymentFC();
		return q;
	}
	
	/**
	 * removes all existing children. The children can be one of the following
	 * subtypes of <code>ReceiptChild</code>: <code>Position</code> or
	 * <code>Payment</code>.
	 * 
	 * @param cls
	 *            the subclass
	 * @return boolean success
	 */
	@SuppressWarnings("rawtypes")
	public void removeChildren(Class cls)
	{
		if (cls == Position.class)
		{
			this.removePositions();
		}
		else if (cls == Payment.class)
		{
			this.removePayments();
		}
	}
	
	/**
	 * removes all existing positions.
	 */
	public void removePositions()
	{
		this.positions.removeAllElements();
		// amount = computeAmount();
		
	}
	
	/**
	 * removes all existing payments.
	 */
	public void removePayments()
	{
		this.payments.removeAllElements();
		// payment = computePayment();
	}
	
	/**
	 * removes an existing position at a given index.
	 * 
	 * @param index
	 *            the index in the list where to find the existing position
	 * @return the removed position in the list
	 */
	public Position removePosition(int index)
	{
		Position p = null;
		if (index < this.positions.size())
		{
			p = (Position) this.positions.remove(index);
			if (!(p == null))
			{
				// amount = computeAmount();
			}
		}
		return p;
	}
	
	/**
	 * removes an existing payment at a given index.
	 * 
	 * @param index
	 *            the index in the list where to find the existing payment
	 * @return the removed payment in the list
	 */
	public Payment removePayment(int index)
	{
		Payment p = null;
		if (index < this.payments.size())
		{
			p = (Payment) this.payments.remove(index);
			// if (!(p == null)) {
			// payment = computePayment();
			// paymentFC = computePaymentFC();
			// }
		}
		return p;
	}
	
	public Position getPositionAt(int index)
	{
		return (Position) this.positions.get(index);
	}
	
	public Payment getPaymentAt(int index)
	{
		return (Payment) this.payments.get(index);
	}
	
	// public Double computeAmount()
	// {
	// double d = 0d;
	// Iterator i = this.positions.iterator();
	// while (i.hasNext())
	// {
	// d += ((Position) i.next()).getAmount();
	// }
	// return new Double(NumberUtility.round(d,
	// ForeignCurrency.getDefaultCurrency().getCurrency()
	// .getDefaultFractionDigits()));
	// }
	
	// private Double computePayment()
	// {
	// double d = 0d;
	// Iterator i = this.payments.iterator();
	// while (i.hasNext())
	// {
	// d += ((Payment) i.next()).getAmount();
	// }
	// return new Double(NumberUtility.round(d, ForeignCurrency
	// .getDefaultCurrency().getCurrency().getDefaultFractionDigits()));
	// }
	
	public RemovalAwareCollection getChildren(@SuppressWarnings("rawtypes") Class cls)
	{
		RemovalAwareCollection rac = null;
		if (cls == Position.class)
		{
			rac = this.getPositions();
		}
		else if (cls == Payment.class)
		{
			rac = this.getPayments();
		}
		return rac;
	}
	
	public RemovalAwareCollection getPositions()
	{
		return this.positions;
	}
	
	public RemovalAwareCollection getPayments()
	{
		return this.payments;
	}
	
	@SuppressWarnings("unchecked")
	public Position[] getPositionsAsArray()
	{
		return (Position[]) this.positions.toArray(new Position[0]);
	}
	
	@SuppressWarnings("unchecked")
	public Payment[] getPaymentsAsArray()
	{
		return (Payment[]) this.payments.toArray(new Payment[0]);
	}
	
	// public boolean isBalanced()
	// {
	// return this.getAmount() == this.getPayment();
	// }
	//	
	public int getChildrenCount(@SuppressWarnings("rawtypes") Class cls)
	{
		int size = 0;
		if (cls == Position.class)
		{
			size = this.getPositionCount();
		}
		else if (cls == Payment.class)
		{
			size = this.getPaymentCount();
		}
		return size;
	}
	
	public int getPositionCount()
	{
		return this.positions.size();
	}
	
	public int getPaymentCount()
	{
		return this.payments.size();
	}
	
	public void setTransferred(boolean transferred)
	{
		this.transferred = transferred;
	}
	
	public boolean isTransferred()
	{
		return this.transferred;
	}
	
	public boolean openCashdrawer()
	{
		@SuppressWarnings("rawtypes")
		Iterator iterator = this.getPayments().iterator();
		while (iterator.hasNext())
		{
			Payment p = (Payment) iterator.next();
			if (p.getPaymentType().openCashdrawer)
			{
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private static final String PRE_POSITION_CONSTANT = "1"; // 10193
	@SuppressWarnings("unused")
	private static final String PRE_POSITION_CONSTANT_NEW = "N"; // 10193
	@SuppressWarnings("unused")
	private static final String PRE_POSITION_CONSTANT_PARKED = "P"; // 10193
	@SuppressWarnings("unused")
	private static final String PRE_POSITION_CONSTANT_REVERSED = "S"; // 10193
	@SuppressWarnings("unused")
	private static final String PRE_POSITION_CONSTANT_FAILOVER = "F"; // 10193
	@SuppressWarnings("unused")
	private static final NumberFormat salespointNumberFormat = new DecimalFormat("00000"); // 10193
	@SuppressWarnings("unused")
	private static final NumberFormat receiptNumberFormat = new DecimalFormat("0000000000"); // 10193
	
	public static final int RECEIPT_STATE_NO_CHANGE = 0;
	public static final int RECEIPT_STATE_NEW = 1;
	public static final int RECEIPT_STATE_PARKED = 2;
	public static final int RECEIPT_STATE_REVERSED = 3;
	public static final int RECEIPT_STATE_SERIALIZED = 4;
	
	// public static final String[] STATE_TEXT = new String[]
	//	{ Messages.getString("Receipt.Keine__u00C4nderung_24"), //$NON-NLS-1$
	//					Messages.getString("Receipt.Neu_25"), //$NON-NLS-1$
	//					Messages.getString("Receipt.Parkiert_26"), //$NON-NLS-1$
	//					Messages.getString("Receipt.Storniert_27"), //$NON-NLS-1$
	//					Messages.getString("Receipt.G_u00FCltig_28") }; //$NON-NLS-1$
}