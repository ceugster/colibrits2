package ch.eugster.colibri.persistence.model;

import java.util.HashMap;

public class DiscountEntry extends HashMap<String, Object> implements Comparable<DiscountEntry>
{
	private static final long serialVersionUID = 1L;

	public DiscountEntry()
	{
		super(8);
	}

	@Override
	public int compareTo(DiscountEntry other)
	{
		String thisSalespoint= this.getSalespoint();
		String otherSalespoint = other.getSalespoint();
		int comparison = thisSalespoint.compareTo(otherSalespoint);
		if (comparison == 0)
		{
			Integer thisYear = this.getYear();
			Integer otherYear = other.getYear();
			comparison = thisYear.compareTo(otherYear);
			if (comparison == 0)
			{
				Integer thisMonth = this.getMonth();
				Integer otherMonth = other.getMonth();
				comparison = thisMonth.compareTo(otherMonth);
				if (comparison == 0)
				{
					comparison = this.getDay().compareTo(other.getDay());
				}
			}
		}
		return comparison;
	}

	public String getSalespoint()
	{
		return (String) get("salespoint");
	}

	public void setSalespoint(String salespoint)
	{
		this.put("salespoint", salespoint);
	}

	public Integer getYear()
	{
		return (Integer) get("year");
	}

	public void setYear(Integer year)
	{
		this.put("year", year);
	}

	public Integer getMonth()
	{
		return (Integer) get("month");
	}

	public void setMonth(Integer month)
	{
		this.put("month", month);
	}

	public Integer getDay()
	{
		return (Integer) get("day");
	}

	public void setDay(Integer day)
	{
		this.put("day", day);
	}

	public Double getDiscount()
	{
		return (Double) get("discount");
	}

	public void setDiscount(Double discount)
	{
		this.put("discount", discount);
	}

	public Double getPercent()
	{
		Object object = get("percent");
		return (Double) object;
	}
	
	public void setPercent(Double percent)
	{
		this.put("percent", percent);
	}
	
	public Double getAmount()
	{
		return (Double) get("amount");
	}

	public void setAmount(Double amount)
	{
		this.put("amount", amount);
	}

	public Double getFullAmount()
	{
		return (Double) get("fullAmount");
	}

	public void setFullAmount(Double fullAmount)
	{
		this.put("fullAmount", fullAmount);
	}

}
