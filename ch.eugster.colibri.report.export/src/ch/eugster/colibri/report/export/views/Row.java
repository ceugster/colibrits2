package ch.eugster.colibri.report.export.views;

import java.util.HashMap;

@SuppressWarnings("serial")
public class Row extends HashMap<String, Object> implements Comparable<Row>
{
	public Row(Object[] object)
	{
		this.put("salespointId", object[0]);
		this.put("type", object[1]);
		this.put("subtype", object[2]);
		this.put("cashtype", object[3]);
		this.put("text", object[4]);
		this.put("code", object[5]);
		this.put("value", object[6] == null ? new Double(0d) : object[6]);
		this.put("receipts", object[7]);
		this.put("quantity", object[8]);
		this.put("amount1", object[9]);
		this.put("amount2", object[10]);
	}

	public Row(Row row)
	{
		this.put("salespointId", row.get("salespointId"));
		this.put("type", row.get("type"));
		this.put("subtype", row.get("subtype"));
		this.put("cashtype", row.get("cashtype"));
		this.put("text", row.get("text"));
		this.put("code", row.get("code"));
		this.put("value", row.get("value"));
		this.put("receipts", row.get("receipts"));
		this.put("quantity", row.get("quantity"));
		this.put("amount1", row.get("amount1"));
		this.put("amount2", row.get("amount2"));
	}

	@Override
	public int compareTo(Row other)
	{
		if (other instanceof Row)
		{
			Row row = other;
			if (row.get("type").equals(this.get("type")))
			{
				if (row.get("subtype").equals(this.get("subtype")))
				{
					if (row.get("cashtype").equals(this.get("cashtype")))
					{
						if (row.get("value").equals(this.get("value")))
						{
							return ((String) this.get("text")).compareTo((String) row.get("text"));
						}
						else
						{
							return Double.compare(((Double) row.get("value")).doubleValue(),
									((Double) this.get("value")).doubleValue());
						}
					}
					else
					{
						return ((Integer) this.get("cashtype")).intValue() - ((Integer) row.get("cashtype")).intValue();
					}
				}
				else
				{
					return ((Integer) this.get("subtype")).intValue() - ((Integer) row.get("subtype")).intValue();
				}
			}
			else
			{
				return ((Integer) this.get("type")).intValue() - ((Integer) row.get("type")).intValue();
			}
		}
		return 0;
	}

}
