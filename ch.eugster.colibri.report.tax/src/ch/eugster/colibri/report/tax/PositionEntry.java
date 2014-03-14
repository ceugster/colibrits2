package ch.eugster.colibri.report.tax;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;

public class PositionEntry extends HashMap<String, Object> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4687721122861375982L;

	public PositionEntry(Position position)
	{
		
		this.put("text", getText(position));
		this.put("price", position.getPrice());
		this.put("quantity", position.getQuantity());
		this.put("discount", position.getDiscount());
		this.put("amount", position.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.NETTO));
		this.put("percentage", position.getCurrentTax().getPercentage());
		this.put("taxAmount", position.getTaxAmount(Receipt.QuotationType.FOREIGN_CURRENCY));
		this.put("receipt",  position.getReceipt().getNumber().toString());
		this.put("date", SimpleDateFormat.getDateTimeInstance().format(position.getReceipt().getTimestamp().getTime()));
	}
	
	private String getText(Position position)
	{
		if (position.getProduct() == null)
		{
			return position.getProductGroup().getName() + (position.getSearchValue() == null ? "" : "  " + position.getSearchValue());
		}
		else
		{
			return position.getProduct().getAuthorAndTitleShortForm();
		}
	}
}
