package ch.eugster.colibri.persistence.model.report;

import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

@SuppressWarnings("rawtypes")
public class ReportSettlement implements Comparable
{
	private int group;

	private int subgroup;

	private String name;

	private int quantity;

	private double amount1;

	private double amount2;

	@Override
	public int compareTo(final Object o)
	{
		final ReportSettlement other = (ReportSettlement) o;
		if (group == other.group)
		{
			return subgroup - other.subgroup;
		}
		return group - other.group;
	}

	public double getAmount1()
	{
		return amount1;
	}

	public double getAmount2()
	{
		return amount2;
	}

	public int getGroup()
	{
		return group;
	}

	public String getGroupName()
	{
		return GroupName.values()[group].toString();
	}

	public String getName()
	{
		return name;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public int getSubgroup()
	{
		return subgroup;
	}

	public String getSubgroupName()
	{
		final GroupName groupName = GroupName.values()[group];

		switch (groupName)
		{
			case POSITION:
			{
				return ProductGroupType.values()[subgroup].toString();
			}
			case PAYMENT:
			{
				return PaymentTypeGroup.values()[subgroup].toString();
			}
			case TAX:
			{
				return "Mehrwertsteuerart";
			}
			case REVERSED_RECEIPT:
			{
				return "";
			}
			default:
			{
				return "";
			}
		}
	}

	public void setAmount1(final double amount)
	{
		amount1 = amount;
	}

	public void setAmount2(final double amount)
	{
		amount2 = amount;
	}

	public void setGroup(final int group)
	{
		this.group = group;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public void setQuantity(final int quantity)
	{
		this.quantity = quantity;
	}

	public void setSubgroup(final int subgroup)
	{
		this.subgroup = subgroup;
	}

	public enum GroupName
	{
		POSITION, PAYMENT, SUMMARY, TAX, INTERNAL, RESTITUTION, PAYED_INVOICES, REVERSED_RECEIPT, CASH_CHECK;

		@Override
		public String toString()
		{
			switch (this)
			{
				case POSITION:
				{
					return "Warengruppen";
				}
				case PAYMENT:
				{
					return "Zahlungsarten";
				}
				case SUMMARY:
				{
					return "Zusammenfassung";
				}
				case TAX:
				{
					return "Mehrwertsteuer";
				}
				case INTERNAL:
				{
					return "Einlagen/Entnahmen";
				}
				case RESTITUTION:
				{
					return "Rücknahmen";
				}
				case PAYED_INVOICES:
				{
					return "Bezahlte Rechnungen";
				}
				case REVERSED_RECEIPT:
				{
					return "Stornierte Belege";
				}
				case CASH_CHECK:
				{
					return "Kassensturz";
				}
				default:
				{
					return "";
				}
			}
		}
	}

}
