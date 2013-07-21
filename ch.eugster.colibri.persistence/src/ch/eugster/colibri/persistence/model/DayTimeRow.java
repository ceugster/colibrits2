package ch.eugster.colibri.persistence.model;

import java.util.HashMap;

public class DayTimeRow extends HashMap<Object, Object>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7085652736575375292L;

	public DayTimeRow(Long salespointId, String name, Integer hour, Double amount)
	{
		this.put("id", salespointId);
		this.put("name", name);
		this.put("h" + hour.toString(), amount);
		this.put("total", amount);
	}
	
	public Object get(String key)
	{
		return super.get(key);
	}
	
	public void add(Integer hour, Double amount)
	{
		System.out.println("h" + hour.toString());
		Double oldAmount = (Double) this.get("h" + hour.toString());
		Double newAmount = 0D;
		newAmount = oldAmount == null ? amount : new Double(oldAmount.doubleValue() + amount.doubleValue());
		this.put("h" + hour.toString(), newAmount);
		Double total = (Double) this.get("total");
		total = total == null ? newAmount : new Double(total.doubleValue() + newAmount.doubleValue());
		this.put("total", total);
	}
}
