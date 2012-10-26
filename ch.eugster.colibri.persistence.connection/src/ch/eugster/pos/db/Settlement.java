/*
 * Created on 14.05.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.db;

import java.text.DateFormat;
import java.util.Hashtable;

@SuppressWarnings("rawtypes")
public class Settlement extends Table implements Comparable
{
	@SuppressWarnings("unused")
	private static DateFormat dateTimeFormat = DateFormat
					.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
	
	@SuppressWarnings("unused")
	private static DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
	
	@SuppressWarnings("unused")
	private static DateFormat mediumDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
	
	private static Hashtable records = new Hashtable();
	
	private Long salespointId;
	private Salespoint salespoint;
	private Long settlement;
	
	private int lineNumber;
	private int type; // z.B. Bewegungen, Zahlungsart, Zusammenfassung, MWST,
	// Abschluss Stornierte
	private int subtype; // z.B. bei Bewegungen: WG, Sonstiges, Ausgaben
	private int cashtype;
	private String referenceClassName;
	private Long referenceObjectId;
	
	private String shortText; // für Coupon
	private String longText; // für Drucker
	
	private double value; // für Münzzählung
	
	private int quantity;
	private double amount1;
	private double amount2;
	
	private String code;
	
	private int receipts;
	
	public Settlement()
	{

	}
	
	// public Settlement(Salespoint salespoint, Long settlement, Position
	// position, int receipts)
	// {
	// this(position, receipts);
	// this.setSalespoint(salespoint);
	// this.setSettlement(settlement);
	// }
	//	
	// public Settlement(Salespoint salespoint, Long settlement, Position
	// position, CurrentTax currentTax, int receipts)
	// {
	// this(position, currentTax, receipts);
	// this.setSalespoint(salespoint);
	// this.setSettlement(settlement);
	// }
	//	
	// public Settlement(Salespoint salespoint, Long settlement, Payment
	// payment, int receipts)
	// {
	// // this(payment, receipts);
	// this.setSalespoint(salespoint);
	// this.setSettlement(settlement);
	// }
	//	
	// public Settlement(Salespoint salespoint, Long settlement, int type,
	// String text, int quantity, double amount,
	// int receipts)
	// {
	// this(type, text, quantity, amount, receipts);
	// this.setSalespoint(salespoint);
	// this.setSettlement(settlement);
	// }
	//	
	// public Settlement(Salespoint salespoint, Long settlement, Position
	// position, int settlementType, int receipts)
	// {
	// this(position, settlementType, receipts);
	// this.setSalespoint(salespoint);
	// this.setSettlement(settlement);
	// }
	
	// public Settlement(Salespoint salespoint, Long settlement, Receipt
	// receipt, int receipts)
	// {
	// this(receipt, receipts);
	// this.setSalespoint(salespoint);
	// this.setSettlement(settlement);
	// }
	//	
	// public Settlement(Salespoint salespoint, Long settlement, int subtype,
	// int cashtype, String code, double amount,
	// String text, int receipts)
	// {
	// this(subtype, cashtype, code, amount, text, receipts);
	// this.setSalespoint(salespoint);
	// this.setSettlement(settlement);
	// }
	//	
	// public Settlement(Salespoint salespoint, Long settlement, int subtype,
	// int cashtype, String code, int quantity,
	// double amount, String text, int receipts)
	// {
	// this(subtype, cashtype, code, quantity, amount, text, receipts);
	// this.setSalespoint(salespoint);
	// this.setSettlement(settlement);
	// }
	//	
	// public Settlement(Salespoint salespoint, Long settlement, int subtype,
	// int cashtype, String code, double value,
	// int quantity, double amount, String text, int receipts)
	// {
	// this(subtype, cashtype, code, value, quantity, amount, text, receipts);
	// this.setSalespoint(salespoint);
	// this.setSettlement(settlement);
	// }
	//	
	// private Settlement(Position position, int receipts)
	// {
	// if (position.getProductGroup().type == ProductGroup.TYPE_INPUT)
	// {
	// this.type = Settlement.TYPE_INPUT_WITHDRAWAL;
	// this.subtype = Settlement.SUBTYPE_INPUT_WITHDRAWAL_INPUT;
	// this.cashtype =
	// position.getProductGroup().getForeignCurrency().getId().intValue();
	// }
	// else if (position.getProductGroup().type == ProductGroup.TYPE_WITHDRAW)
	// {
	// this.type = Settlement.TYPE_INPUT_WITHDRAWAL;
	// this.subtype = Settlement.SUBTYPE_INPUT_WITHDRAWAL_WITHDRAWAL;
	// this.cashtype =
	// position.getProductGroup().getForeignCurrency().getId().intValue();
	// }
	// else
	// {
	// this.type = Settlement.TYPE_POSITION;
	// this.subtype = position.getProductGroup().type;
	// }
	// this.referenceClassName =
	// position.getProductGroup().getClass().getName();
	// this.referenceObjectId = position.getProductGroupId();
	// this.shortText = position.getProductGroup().shortname;
	// this.longText = position.getProductGroup().name;
	// this.code = position.getProductGroup().galileoId;
	// this.receipts = receipts;
	// this.setData(position);
	//		
	// }
	//	
	// private Settlement(Position position, int type, int receipts)
	// {
	// this.type = type;
	// this.subtype = position.getProductGroup().type;
	// this.referenceClassName =
	// position.getProductGroup().getClass().getName();
	// this.referenceObjectId = position.getProductGroupId();
	// if (this.type == Settlement.TYPE_PAYED_INVOICES)
	// {
	// this.shortText = position.getProductGroup().shortname + " " +
	// position.getInvoiceNumber() + " "
	// + Settlement.shortDateFormat.format(position.getInvoiceDate());
	// this.longText = position.getProductGroup().name + " " +
	// position.getInvoiceNumber() + " "
	// + Settlement.mediumDateFormat.format(position.getInvoiceDate());
	// }
	// else
	// {
	// this.shortText = position.getProductGroup().shortname;
	// this.longText = position.getProductGroup().name;
	// }
	// this.code = "";
	// this.receipts = receipts;
	// this.setData(position);
	//		
	// }
	//	
	// /**
	// * Konstruktur für CurrentTax
	// */
	// private Settlement(Position position, CurrentTax tax, int receipts)
	// {
	// this.type = Settlement.TYPE_TAX;
	// this.cashtype = tax.getTaxId().intValue();
	// this.referenceClassName = tax.getClass().getName();
	// this.referenceObjectId = tax.getId();
	//		this.shortText = tax.getTax().getTaxType().name + " " + Double.toString(tax.percentage); //$NON-NLS-1$
	//		this.longText = tax.getTax().getTaxType().name + " " + Double.toString(tax.percentage); //$NON-NLS-1$
	// this.receipts = receipts;
	// this.setData(position, tax);
	// }
	//	
	// private Settlement(int type, String text, int quantity, double amount,
	// int receipts)
	// {
	// this.type = type;
	// this.quantity = quantity;
	// this.amount1 = amount;
	// this.shortText = text;
	// this.longText = text;
	// this.receipts = receipts;
	// }
	
	/**
	 * Konstruktur für Payment
	 */
	// private Settlement(Payment payment, int receipts)
	// {
	// this.type = Settlement.TYPE_PAYMENT;
	// if
	// (payment.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
	// {
	// this.subtype = Settlement.SUBTYPE_PAYMENT_PAYMENT_TYPE;
	// }
	// else
	// {
	// this.subtype = Settlement.SUBTYPE_PAYMENT_FOREIGN_CURRENCY;
	// }
	// this.cashtype = payment.getPaymentType().sort;
	// if (payment.isBack())
	// {
	// if (this.subtype == Settlement.SUBTYPE_PAYMENT_PAYMENT_TYPE)
	// if (payment.getPaymentType().cash)
	// {
	// this.shortText = payment.getPaymentType().code;
	// this.longText = payment.getPaymentType().name;
	// }
	// else
	// {
	// this.shortText = "Rückgeld " + payment.getPaymentType().code;
	// this.longText = "Rückgeld " + payment.getPaymentType().name;
	// }
	// else if (this.subtype == Settlement.SUBTYPE_PAYMENT_FOREIGN_CURRENCY)
	// {
	// this.shortText = "Rückgeld " + payment.getForeignCurrency().code;
	// this.longText = "Rückgeld " + payment.getForeignCurrency().name;
	// }
	// else
	// {
	// this.shortText = "";
	// this.longText = "";
	// }
	// }
	// else
	// {
	// if (this.subtype == Settlement.SUBTYPE_PAYMENT_PAYMENT_TYPE)
	// {
	// this.shortText = payment.getPaymentType().code;
	// this.longText = payment.getPaymentType().name;
	// }
	// else if (this.subtype == Settlement.SUBTYPE_PAYMENT_FOREIGN_CURRENCY)
	// {
	// this.shortText = payment.getForeignCurrency().code;
	// this.longText = payment.getForeignCurrency().name;
	// }
	// else
	// {
	// this.shortText = "";
	// this.longText = "";
	// }
	// }
	// this.referenceClassName = PaymentType.class.getName();
	// this.referenceObjectId = payment.getPaymentTypeId();
	// this.receipts = receipts;
	// this.setData(payment);
	// }
	//	
	// private Settlement(Receipt receipt, int receipts)
	// {
	// this.type = Settlement.TYPE_REVERSED;
	// this.referenceClassName = receipt.getClass().getName();
	// this.referenceObjectId = receipt.getId();
	// String date = Settlement.dateTimeFormat.format(receipt.timestamp);
	// this.shortText = receipt.getFormattedNumber() + " " + date;
	// this.longText = receipt.getFormattedNumber() + " " + date;
	// this.setAmount1(receipt.getAmount());
	// this.receipts = receipts;
	// }
	//	
	// private Settlement(int subtype, int cashtype, String code, double amount,
	// String text, int receipts)
	// {
	// this.type = Settlement.TYPE_CASH_CHECK;
	// this.subtype = subtype;
	// this.cashtype = cashtype;
	// this.code = code;
	// this.amount1 = amount;
	// this.shortText = text;
	// this.longText = text;
	// this.receipts = receipts;
	// }
	//	
	// private Settlement(int subtype, int cashtype, String code, int quantity,
	// double amount, String text, int receipts)
	// {
	// this.type = Settlement.TYPE_CASH_CHECK;
	// this.subtype = subtype;
	// this.cashtype = cashtype;
	// this.code = code;
	// this.quantity = quantity;
	// this.amount1 = amount;
	// this.shortText = text;
	// this.longText = text;
	// this.receipts = receipts;
	// }
	//	
	// private Settlement(int subtype, int cashtype, String code, double value,
	// int quantity, double amount, String text,
	// int receipts)
	// {
	// this.type = Settlement.TYPE_CASH_CHECK;
	// this.subtype = subtype;
	// this.cashtype = cashtype;
	// this.code = code;
	// this.value = value;
	// this.quantity = quantity;
	// this.amount1 = amount;
	// this.shortText = code + " " + text;
	// this.longText = code + " " + text;
	// this.receipts = receipts;
	// }
	//	
	// public void setData(Position position)
	// {
	// if (position.type == ProductGroup.TYPE_INPUT)
	// {
	// ForeignCurrency currency =
	// position.getProductGroup().getForeignCurrency();
	// if
	// (!currency.getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
	// this.amount1 += position.getAmountFC();
	//			
	// this.quantity += Math.abs(position.getQuantity());
	// this.amount2 += position.getAmount();
	// }
	// else if (position.type == ProductGroup.TYPE_WITHDRAW)
	// {
	// ForeignCurrency currency =
	// position.getProductGroup().getForeignCurrency();
	// if
	// (!currency.getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
	// this.amount1 += position.amountFC;
	//			
	// this.quantity += Math.abs(position.getQuantity());
	// this.amount2 += position.getAmount();
	// }
	// else
	// {
	// this.quantity += position.getQuantity();
	// this.amount1 += position.getAmount();
	// this.amount2 += position.getTaxAmount();
	// }
	// }
	//	
	// public void setData(Position position, CurrentTax currentTax)
	// {
	// this.quantity += position.getQuantity();
	// this.amount1 += position.getAmount();
	// this.amount2 += position.getTaxAmount();
	// }
	//	
	// public void setData(Payment payment)
	// {
	// if (!payment.isBack()) if (payment.getAmount() != 0d) this.quantity++;
	// this.amount1 += payment.getAmountFC();
	// this.amount2 += payment.getAmount();
	// }
	
	public Long getSalespointId()
	{
		return this.salespointId;
	}
	
	public void setSalespointId(Long salespointId)
	{
		this.salespointId = salespointId;
	}
	
	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}
	
	public void setSalespoint(Salespoint salespoint)
	{
		this.salespoint = salespoint;
		this.salespointId = this.salespoint.getId();
	}
	
	public Long getSettlement()
	{
		return this.settlement;
	}
	
	public void setSettlement(Long settlement)
	{
		this.settlement = settlement;
	}
	
	public int getLineNumber()
	{
		return this.lineNumber;
	}
	
	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public int getSubtype()
	{
		return this.subtype;
	}
	
	public void setSubtype(int subtype)
	{
		this.subtype = subtype;
	}
	
	public String getReferenceClassName()
	{
		return this.referenceClassName;
	}
	
	public void setReferenceClassName(String referenceClassName)
	{
		this.referenceClassName = referenceClassName;
	}
	
	public Long getReferenceObjectId()
	{
		return this.referenceObjectId;
	}
	
	public void setReferenceObjectId(Long referenceObjectId)
	{
		this.referenceObjectId = referenceObjectId;
	}
	
	public String getShortText()
	{
		return this.shortText;
	}
	
	public void setShortText(String shortText)
	{
		this.shortText = shortText;
	}
	
	public String getLongText()
	{
		return this.longText;
	}
	
	public void setLongText(String longText)
	{
		this.longText = longText;
	}
	
	public double getValue()
	{
		return this.value;
	}
	
	public void setValue(double value)
	{
		this.value = value;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
	
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}
	
	public double getAmount1()
	{
		return this.amount1;
	}
	
	public void setAmount1(double amount1)
	{
		this.amount1 = amount1;
	}
	
	public double getAmount2()
	{
		return this.amount2;
	}
	
	public void setAmount2(double amount2)
	{
		this.amount2 = amount2;
	}
	
	public String getCode()
	{
		return this.code;
	}
	
	public void setCode(String code)
	{
		this.code = code;
	}
	
	public boolean isRemovable()
	{
		return false;
	}
	
	public int getCashtype()
	{
		return this.cashtype;
	}
	
	public void setCashtype(int cashtype)
	{
		this.cashtype = cashtype;
	}
	
	public int getReceipts()
	{
		return this.receipts;
	}
	
	public void setReceipts(int receipts)
	{
		this.receipts = receipts;
	}
	
	public int compareTo(Object other)
	{
		if (other instanceof Settlement)
		{
			Settlement settlement = (Settlement) other;
			if (settlement.getType() == this.getType())
			{
				if (settlement.getSubtype() == this.getSubtype())
				{
					if (settlement.getCashtype() == this.getCashtype())
					{
						if (settlement.getValue() == this.getValue())
						{
							return this.getShortText().compareTo(settlement.getShortText());
						}
						else
						{
							return Double.compare(settlement.getValue(), this.getValue());
						}
					}
					else
					{
						return this.getCashtype() - settlement.getCashtype();
					}
				}
				else
				{
					return this.getSubtype() - settlement.getSubtype();
				}
			}
			else
			{
				return this.getType() - settlement.getType();
			}
		}
		return 0;
	}
	
	// public static Element writeXMLRecords(Element root)
	// {
	//		Element table = Database.getTemporary().getTable("settlement"); //$NON-NLS-1$
	// if (table == null)
	// {
	//			table = new Element("table"); //$NON-NLS-1$
	//			table.setAttribute("name", "settlement"); //$NON-NLS-1$ //$NON-NLS-2$
	// root.addContent(table);
	// }
	//		
	// Enumeration entries = Settlement.records.elements();
	// while (entries.hasMoreElements())
	// {
	// Settlement rec = (Settlement) entries.nextElement();
	//			
	//			Element record = new Element("record"); //$NON-NLS-1$
	//			record.setAttribute("id", rec.getId().toString()); //$NON-NLS-1$
	//			record.setAttribute("timestamp", new Long(rec.timestamp.getTime()).toString()); //$NON-NLS-1$
	//			record.setAttribute("deleted", new Boolean(rec.deleted).toString()); //$NON-NLS-1$
	//			
	//			Element field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "salespoint-id"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.salespointId.toString()); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "settlement"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.settlement.toString()); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "line-number"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", Integer.toString(rec.lineNumber)); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "type"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", Integer.toString(rec.type)); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "subtype"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", Integer.toString(rec.subtype)); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "cashtype"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", Integer.toString(rec.cashtype)); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "reference-class-name"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.referenceClassName); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "reference-object-id"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.referenceObjectId.toString()); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "short-text"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.shortText); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "long-text"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.longText); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "value"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", Double.toString(rec.value)); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "quantity"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", Integer.toString(rec.quantity)); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "amount1"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", Double.toString(rec.amount1)); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "amount2"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", Double.toString(rec.amount2)); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "code"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.code); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "receipts"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", Integer.toString(rec.receipts)); //$NON-NLS-1$
	// record.addContent(field);
	//			
	// table.addContent(record);
	// }
	// return root;
	// }
	//	
	// private static void clearData()
	// {
	// Settlement.records.clear();
	// }
	//	
	// public static void readXML()
	// {
	// Settlement.clearData();
	//		Element[] elements = Database.getTemporary().getRecords("settlement"); //$NON-NLS-1$
	// for (int i = 0; i < elements.length; i++)
	// {
	// Settlement record = new Settlement();
	//			record.setId(new Long(XMLLoader.getLong(elements[i].getAttributeValue("id")))); //$NON-NLS-1$
	//			record.timestamp = XMLLoader.getTimestampFromLong(elements[i].getAttributeValue("timestamp")); //$NON-NLS-1$
	//			record.deleted = new Boolean(elements[i].getAttributeValue("deleted")).booleanValue(); //$NON-NLS-1$
	//			
	//			List fields = elements[i].getChildren("field"); //$NON-NLS-1$
	// Iterator iter = fields.iterator();
	// while (iter.hasNext())
	// {
	// Element field = (Element) iter.next();
	//				
	//				if (field.getAttributeValue("name").equals("salespoint-id")) { //$NON-NLS-1$ //$NON-NLS-2$
	// record.setSalespoint(Salespoint.getById(new Long(XMLLoader
	//									.getLong(field.getAttributeValue("value"))))); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("settlement")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.settlement = new Long(XMLLoader.getLong(field.getAttributeValue("value"))); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("line-number")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.lineNumber = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("type")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.type = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("subtype")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.subtype = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("cashtype")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.cashtype = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("reference-class-name")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.referenceClassName = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("reference-object-id")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.referenceObjectId = new Long(XMLLoader.getLong(field.getAttributeValue("value"))); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("short-text")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.shortText = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("long-text")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.longText = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("value")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.value = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("quantity")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.quantity = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("amount1")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.amount1 = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("amount2")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.amount2 = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("code")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.code = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("receipts")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.receipts = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	// }
	// Settlement.put(record);
	// }
	// }
	
	@SuppressWarnings({ "unused", "unchecked" })
	private static void put(Settlement settlement)
	{
		Settlement.records.put(settlement.getId(), settlement);
	}
	
	public static final int TYPE_POSITION = 1;
	public static final int TYPE_PAYMENT = 2;
	public static final int TYPE_SUMMARY = 3;
	public static final int TYPE_TAX = 4;
	public static final int TYPE_INPUT_WITHDRAWAL = 5;
	public static final int TYPE_TOOK_BACK = 6;
	public static final int TYPE_PAYED_INVOICES = 7;
	public static final int TYPE_REVERSED = 8;
	public static final int TYPE_CASH_CHECK = 9;
	
	public static final int SUBTYPE_PAYMENT_PAYMENT_TYPE = 1;
	public static final int SUBTYPE_PAYMENT_FOREIGN_CURRENCY = 2;
	
	public static final int SUBTYPE_INPUT_WITHDRAWAL_INPUT = 1;
	public static final int SUBTYPE_INPUT_WITHDRAWAL_WITHDRAWAL = 2;
	
	public static final int CASH_CHECK_START = 1;
	public static final int CASH_CHECK_BESTAND_SOLL = 2;
	public static final int CASH_CHECK_BESTAND_IST = 3;
	public static final int CASH_CHECK_CHANGES_SOLL = 4;
	public static final int CASH_CHECK_CHANGES_IST = 5;
	public static final int CASH_CHECK_INPUTS = 6;
	public static final int CASH_CHECK_WITHDRAWS = 7;
	public static final int CASH_CHECK_DIFFERENCE = 8;
	
	public static final int CASH_CHECK_MONEY = 9;
	
}
