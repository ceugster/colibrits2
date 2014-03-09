package ch.eugster.colibri.report.settlement.model;

import java.util.Date;
import java.util.HashMap;

public class DifferenceEntry extends HashMap<String, Object> implements Comparable<DifferenceEntry>
{
	private static final long serialVersionUID = 1L;

	public DifferenceEntry()
	{
		super(8);
	}

	@Override
	public int compareTo(DifferenceEntry other)
	{
		String thisSalespoint= this.getSalespoint();
		String otherSalespoint = other.getSalespoint();
		int comparison = thisSalespoint.compareTo(otherSalespoint);
		if (comparison == 0)
		{
			String thisCode = getCode();
			String otherCode = other.getCode();
			comparison = thisCode.compareTo(otherCode);
			if (comparison == 0)
			{
				comparison = this.getDate().compareTo(other.getDate());
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

	public String getCode()
	{
		String code = (String) get("code");
		return code == null ? "" : code;
	}

	public void setCode(String code)
	{
		this.put("code", code);
	}

	public String getSettlement()
	{
		return (String) get("settlement");
	}

	public void setSettlement(String settlement)
	{
		this.put("settlement", settlement);
	}

	public Date getDate()
	{
		Object object = get("date");
		return (Date) object;
	}
	
	public void setDate(Date date)
	{
		this.put("date", date);
	}
	
	public Double getAmount()
	{
		return (Double) get("amount");
	}

	public void setAmount(Double amount)
	{
		this.put("amount", amount);
	}

}
