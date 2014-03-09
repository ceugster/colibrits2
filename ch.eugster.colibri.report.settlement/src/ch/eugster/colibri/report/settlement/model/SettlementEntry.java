package ch.eugster.colibri.report.settlement.model;

import java.util.Calendar;
import java.util.HashMap;

public class SettlementEntry extends HashMap<String, Object> implements Comparable<SettlementEntry>
{
	private static final long serialVersionUID = 1L;

	public SettlementEntry(Section section)
	{
		super(8);
		setSection(section);
	}

	@Override
	public int compareTo(SettlementEntry other)
	{
		Section thisSection = this.getSection();
		Section otherSection = other.getSection();
		int comparison = thisSection.compareTo(otherSection);
		if (comparison == 0)
		{
			int thisGroup = getGroup();
			int otherGroup = other.getGroup();
			comparison = thisGroup - otherGroup;
			if (comparison == 0)
			{
				comparison = this.getCashtype() - other.getCashtype();
				if (comparison == 0)
				{
					if (getCode().isEmpty() || other.getCode().isEmpty())
					{
						comparison = -getCode().compareTo(other.getCode());
						if (comparison == 0)
						{
							comparison = this.getText().compareTo(other.getText());
						}
					}
					else
					{
						comparison = getCode().compareTo(other.getCode());
					}
				}
				else
				{
					
				}
			}
		}
		return comparison;
	}

	public Section getSection()
	{
		Object object = this.get("section");
		return object instanceof Integer ? Section.values()[((Integer) object).intValue()] : null;
	}

	public void setSection(Section section)
	{
		this.put("section", Integer.valueOf(section.ordinal()));
	}

	public int getCashtype()
	{
		Object object = get("cashtype");
		return object instanceof Integer ? ((Integer) object).intValue() : 0;
	}

	public void setCashtype(int cashtype)
	{
		this.put("cashtype", Integer.valueOf(cashtype));
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

	public Calendar getDate()
	{
		Object object = get("date");
		return (Calendar) object;
	}
	
	public void setDate(Calendar date)
	{
		this.put("date", date);
	}
	
	public int getGroup()
	{
		Object object = get("group");
		return object instanceof Integer ? ((Integer) object).intValue() : 0;
	}

	public void setGroup(int group)
	{
		this.put("group", group);
	}

	public String getText()
	{
		Object object = get("text");
		return object instanceof String ? (String) object : "";
	}

	public void setText(String text)
	{
		this.put("text", text);
	}

	public Integer getQuantity()
	{
		return (Integer) get("quantity");
	}

	public void setQuantity(Integer quantity)
	{
		this.put("quantity", quantity);
	}

	public Double getAmount1()
	{
		return (Double) get("amount1");
	}

	public void setAmount1(Double amount1)
	{
		this.put("amount1", amount1);
	}

	public Double getAmount2()
	{
		return (Double) get("amount2");
	}

	public void setAmount2(Double amount2)
	{
		this.put("amount2", amount2);
	}
	
}
