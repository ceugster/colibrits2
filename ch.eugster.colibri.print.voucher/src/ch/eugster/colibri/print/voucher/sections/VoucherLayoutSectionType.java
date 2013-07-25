package ch.eugster.colibri.print.voucher.sections;

import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public enum VoucherLayoutSectionType implements ILayoutSectionType
{
	HEADER, AMOUNT, CUSTOMER, FOOTER;

	private int columnCount = 42;

	private ILayoutSection layoutSection;

	@Override
	public int getColumnCount()
	{
		return this.columnCount;
	}

	public int getLayoutAreaHeight()
	{
		switch (this)
		{
			case HEADER:
			{
				return 128;
			}
			case AMOUNT:
			{
				return 128;
			}
			case CUSTOMER:
			{
				return 48;
			}
			case FOOTER:
			{
				return 64;
			}
			default:
			{
				throw new RuntimeException("Invalid print area type");
			}
		}
	}

	public ILayoutSection getLayoutSection()
	{
		switch (this)
		{
			case HEADER:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new VoucherLayoutHeaderSection(this);
				}
				return this.layoutSection;
			}
			case AMOUNT:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new VoucherLayoutAmountSection(this);
				}
				return this.layoutSection;
			}
			case CUSTOMER:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new VoucherLayoutCustomerSection(this);
				}
				return this.layoutSection;
			}
			case FOOTER:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new VoucherLayoutFooterSection(this);
				}
				return this.layoutSection;
			}
			default:
			{
				throw new RuntimeException("Invalid print area type");
			}
		}
	}

	@Override
	public String getSectionId()
	{
		switch (this)
		{
			case HEADER:
			{
				return "section.voucher.header";
			}
			case AMOUNT:
			{
				return "section.voucher.amount";
			}
			case CUSTOMER:
			{
				return "section.voucher.customer";
			}
			case FOOTER:
			{
				return "section.voucher.footer";
			}
			default:
			{
				throw new RuntimeException("Invalid print area type");
			}
		}
	}

	public String getSectionTitle()
	{
		switch (this)
		{
			case HEADER:
			{
				return "Kopfbereich";
			}
			case AMOUNT:
			{
				return "Betragsbereich";
			}
			case CUSTOMER:
			{
				return "Kundenangaben";
			}
			case FOOTER:
			{
				return "Fussbereich";
			}
			default:
			{
				throw new RuntimeException("Invalid print area type");
			}
		}
	}

	@Override
	public boolean isCustomerEditable() 
	{
		return false;
	}

	@Override
	public void setColumnCount(final int columns)
	{
		this.columnCount = columns;
	}

}
