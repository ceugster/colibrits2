package ch.eugster.colibri.persistence.model;

import java.util.HashMap;

public class ProductGroupEntry extends HashMap<String, Object> implements Comparable<ProductGroupEntry>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setSalespointId(Long id)
	{
		this.put("salespoint_id", id);
	}

	public Long getSalespointId()
	{
		return (Long) this.get("salespoint_id");
	}

	public void setSalespointName(String name)
	{
		this.put("salespoint_name", name);
	}

	public String getSalespointName()
	{
		return (String) this.get("salespoint_name");
	}

	public void setProductGroupId(Long id)
	{
		this.put("product_group_id", id);
	}

	public Long getProductGroupId()
	{
		return (Long) this.get("product_group_id");
	}

	public void setProviderId(String id)
	{
		this.put("provider_id", id);
	}

	public String getProviderId()
	{
		return (String) this.get("provider_id");
	}
	
	public void setProductGroupName(String name)
	{
		this.put("product_group_name", name);
	}

	public String getProductGroupName()
	{
		return (String) this.get("product_group_name");
	}
	
	public void setStockQuantity(Integer quantity)
	{
		this.put("l_quantity", quantity);
	}

	public int getStockQuantity()
	{
		Integer value = (Integer) this.get("l_quantity");
		return value == null ? 0 : value.intValue();
	}
	
	public void setStockAmount(Double amount)
	{
		this.put("l_amount", amount);
	}

	public double getStockAmount()
	{
		Double value = (Double) this.get("l_amount");
		return value == null ? 0D : value.doubleValue();
	}

	public void setOrderQuantity(Integer quantity)
	{
		this.put("b_quantity", quantity);
	}

	public int getOrderQuantity()
	{
		Integer value = (Integer) this.get("b_quantity");
		return value == null ? 0 : value.intValue();
	}
	
	public void setOrderAmount(Double amount)
	{
		this.put("b_amount", amount);
	}

	public double getOrderAmount()
	{
		Double value = (Double) this.get("b_amount");
		return value == null ? 0D : value.doubleValue();
	}

	public void setTotalQuantity(Integer quantity)
	{
		this.put("t_quantity", quantity);
	}

	public int getTotalQuantity()
	{
		Integer value = (Integer) this.get("t_quantity");
		return value == null ? 0 : value.intValue();
	}
	
	public void setTotalAmount(Double amount)
	{
		this.put("t_amount", amount);
	}

	public double getTotalAmount()
	{
		Double value = (Double) this.get("t_amount");
		return value == null ? 0D : value.doubleValue();
	}

	public void setStockQuantityPreviousYear(Integer quantity)
	{
		this.put("l_quantity_prev_year", quantity);
	}

	public int getStockQuantityPreviousYear()
	{
		Integer value = (Integer) this.get("l_quantity_prev_year");
		return value == null ? 0 : value.intValue();
	}
	
	public void setStockAmountPreviousYear(Double amount)
	{
		this.put("l_amount_prev_year", amount);
	}

	public double getStockAmountPreviousYear()
	{
		Double value = (Double) this.get("l_amount_prev_year");
		return value == null ? 0D : value.doubleValue();
	}

	public void setStockChangePercent(Double amount)
	{
		this.put("l_change_percents", amount);
	}

	public double getStockChangePercent()
	{
		Double value = (Double) this.get("l_change_percents");
		return value == null ? 0D : value.doubleValue();
	}

	public void setStockProportion(Double amount)
	{
		this.put("l_proportion", amount);
	}

	public double getStockProportion()
	{
		Double value = (Double) this.get("l_proportion");
		return value == null ? 0D : value.doubleValue();
	}

	public void setOrderQuantityPreviousYear(Integer quantity)
	{
		this.put("b_quantity_prev_year", quantity);
	}

	public int getOrderQuantityPreviousYear()
	{
		Integer value = (Integer) this.get("b_quantity_prev_year");
		return value == null ? 0 : value.intValue();
	}
	
	public void setOrderAmountPreviousYear(Double amount)
	{
		this.put("b_amount_prev_year", amount);
	}

	public double getOrderAmountPreviousYear()
	{
		Double value = (Double) this.get("b_amount_prev_year");
		return value == null ? 0D : value.doubleValue();
	}

	public void setOrderChangePercent(Double amount)
	{
		this.put("b_change_percents", amount);
	}

	public double getOrderChangePercent()
	{
		Double value = (Double) this.get("b_change_percents");
		return value == null ? 0D : value.doubleValue();
	}

	public void setOrderProportion(Double amount)
	{
		this.put("b_proportion", amount);
	}

	public double getOrderProportion()
	{
		Double value = (Double) this.get("b_proportion");
		return value == null ? 0D : value.doubleValue();
	}

	public void setTotalQuantityPreviousYear(Integer quantity)
	{
		this.put("t_quantity_prev_year", quantity);
	}

	public int getTotalQuantityPreviousYear()
	{
		Integer value = (Integer) this.get("t_quantity_prev_year");
		return value == null ? 0 : value.intValue();
	}
	
	public void setTotalAmountPreviousYear(Double amount)
	{
		this.put("t_amount_prev_year", amount);
	}

	public double getTotalAmountPreviousYear()
	{
		Double value = (Double) this.get("t_amount_prev_year");
		return value == null ? 0D : value.doubleValue();
	}

	public void setTotalChangePercent(Double amount)
	{
		this.put("t_change_percents", amount);
	}

	public double getTotalChangePercent()
	{
		Double value = (Double) this.get("t_change_percents");
		return value == null ? 0D : value.doubleValue();
	}

	public void setTotalProportion(Double amount)
	{
		this.put("t_proportion", amount);
	}

	public double getTotalProportion()
	{
		Double value = (Double) this.get("t_proportion");
		return value == null ? 0D : value.doubleValue();
	}

	public void setSectionPerItem(Double amount)
	{
		this.put("section_per_item", amount);
	}

	public double getSectionPerItem()
	{
		Double value = (Double) this.get("section_per_item");
		return value == null ? 0D : value.doubleValue();
	}

	public void setStockProportionGroup(Double group)
	{
		this.put("l_proportion_group", group);
	}

	public double getStockProportionGroup()
	{
		Double value = (Double) this.get("l_proportion_group");
		return value == null ? 0D : value.doubleValue();
	}

	public void setOrderProportionGroup(Double group)
	{
		this.put("b_proportion_group", group);
	}

	public double getOrderProportionGroup()
	{
		Double value = (Double) this.get("b_proportion_group");
		return value == null ? 0D : value.doubleValue();
	}

	@Override
	public int compareTo(ProductGroupEntry otherEntry) 
	{
		return 0;
	}

}
