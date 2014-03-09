package ch.eugster.colibri.report.receipt.views;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Receipt.QuotationType;

public class Entry extends HashMap<String, Object> implements Comparable<Entry>
{
	private static final long serialVersionUID = 1L;

	public Entry(Position position)
	{
		super();
		boolean plus = position.getPrice() > 0;
		this.put("receiptId", position.getReceipt().getId());
		this.put("section", new Integer(0));
		this.put("receiptNumber", position.getReceipt().getNumber().toString());
		this.put("receiptDate", SimpleDateFormat.getDateTimeInstance().format(position.getReceipt().getTimestamp().getTime()));
		this.put("receiptUser", position.getReceipt().getUser() == null ? null : position.getReceipt().getUser().getUsername());
		this.put("receiptState", position.getReceipt().getState().toString());
		this.put("text", getText(position));
		this.put("price", position.getPrice());
		this.put("quantity", position.getQuantity());
		this.put("discount", DecimalFormat.getPercentInstance().format(Math.abs(position.getDiscount())));
		this.put("amount1", position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
		this.put("taxCode", DecimalFormat.getPercentInstance().format(position.getCurrentTax().getPercentage()) + " " + position.getCurrentTax().getTax().getTaxType().getCode());
		double amount = position.getTaxAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
		this.put("amount2", plus ? -Math.abs(amount) : Math.abs(amount));
	}

	public Entry(Payment payment)
	{
		super();
		this.put("receiptId", payment.getReceipt().getId());
		this.put("section", new Integer(1));
		this.put("receiptNumber", payment.getReceipt().getNumber().toString());
		this.put("receiptDate", SimpleDateFormat.getDateTimeInstance().format(payment.getReceipt().getTimestamp().getTime()));
		this.put("receiptUser", payment.getReceipt().getUser() == null ? null : payment.getReceipt().getUser().getUsername());
		this.put("receiptState", payment.getReceipt().getState().toString());
		this.put("text", getText(payment));
		this.put("price", null);
		this.put("quantity", null);
		this.put("discount", null);
		this.put("amount1", payment.getAmount(QuotationType.DEFAULT_FOREIGN_CURRENCY));
		double c = payment.getForeignCurrencyQuotation();
		this.put("taxCode", c == 1D ? null : Double.valueOf(payment.getForeignCurrencyQuotation()).toString());
		double a1 = payment.getAmount(QuotationType.DEFAULT_CURRENCY);
		double a2 = payment.getAmount(QuotationType.FOREIGN_CURRENCY);
		this.put("amount2", a1 == a2 ? null : Double.valueOf(a2));
	}

	private String getText(Position position)
	{
		StringBuilder builder = new StringBuilder(position.getProductGroup().getName());
		if (position.getProduct() != null)
		{
			builder = builder.insert(0, position.getProduct().getAuthorAndTitleShortForm() + " (");
			builder = builder.append(")");
			builder = builder.insert(0, position.getProduct().getCode() + " ");
		}
		return builder.toString();
	}
	
	private String getText(Payment payment)
	{
		if (payment.isBack())
		{
			return "Rückgeld " + payment.getPaymentType().getCode();
		}
		else
		{
			return payment.getPaymentType().getName();
		}
	}
	
	@Override
	public int compareTo(Entry other)
	{
		return this.getSection().compareTo(other.getSection());
	}

	public Integer getSection()
	{
		return (Integer) this.get("section");
	}

	
	public String getReceiptNumber()
	{
		return (String) get("receiptNumber");
	}

	public String getReceiptDate()
	{
		return (String) get("receiptDate");
	}

	public String getReceiptUser()
	{
		return (String) get("receiptUser");
	}

	public String getText()
	{
		return (String) get("text");
	}

	public Double getPrice()
	{
		return (Double) this.get("price");
	}

	public Integer getQuantity()
	{
		return (Integer) get("quantity");
	}

	public String getDiscount()
	{
		return (String) get("discount");
	}
	
	public Double getAmount1()
	{
		return (Double) get("amount1");
	}

	public String getTaxCode()
	{
		return (String) get("taxCode");
	}
	
	public Double getAmount2()
	{
		return (Double) get("amount2");
	}
}
