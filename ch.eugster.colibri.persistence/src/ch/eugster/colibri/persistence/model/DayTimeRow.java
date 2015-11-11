package ch.eugster.colibri.persistence.model;

import java.util.HashMap;

public class DayTimeRow extends HashMap<Object, Object>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7085652736575375292L;

	public DayTimeRow(Long salespointId, String name, Integer hour, Double amount, Integer count)
	{
		if (amount == null)
		{
			amount = new Double(0D);
		}
		if (count == null)
		{
			count = new Integer(0);
		}
		this.put("id", salespointId);
		this.put("name", name);
		this.put("amount@" + hour.toString(), amount);
		this.put("amount", amount);
		this.put("count@" + hour.toString(), count);
		this.put("count", count);
	}
	
	public Object get(String key)
	{
		return super.get(key);
	}
	
	public void add(Integer hour, Double amount, Integer count)
	{
		amount = getAmount(amount);
		Double oldAmount = getAmount((Double) this.get("amount@" + hour.toString()));
		Double newAmount = new Double(oldAmount.doubleValue() + amount.doubleValue());
		this.put("amount@" + hour.toString(), newAmount);
		oldAmount = getAmount((Double) this.get("amount"));
		this.put("amount", new Double(oldAmount.doubleValue() + amount.doubleValue()));
		
		count = getCount(count);
		Integer oldCount = getCount((Integer) this.get("count@" + hour.toString()));
		Integer newCount = new Integer(oldCount.intValue() + count.intValue());
		this.put("count@" + hour.toString(), newCount);
		oldCount = getCount((Integer) this.get("count"));
		this.put("count", oldCount + count);
	}
	
	private Double getAmount(Double amount)
	{
		return amount == null ? new Double(0D) : amount;
	}

	private Integer getCount(Integer count)
	{
		return count == null ? new Integer(0) : count;
	}
}
