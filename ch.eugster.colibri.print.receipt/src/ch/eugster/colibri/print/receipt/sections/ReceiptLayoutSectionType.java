package ch.eugster.colibri.print.receipt.sections;

import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public enum ReceiptLayoutSectionType implements ILayoutSectionType
{
	HEADER, POSITION, PAYMENT, TAX, CUSTOMER, FOOTER;

	private int columnCount = 42;

	private ILayoutSection layoutSection;

	@Override
	public int getColumnCount()
	{
		return this.columnCount;
	}

	@Override
	public int getLayoutAreaHeight()
	{
		switch (this)
		{
			case HEADER:
			{
				return 128;
			}
			case POSITION:
			{
				return 128;
			}
			case PAYMENT:
			{
				return 48;
			}
			case TAX:
			{
				return 48;
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

	@Override
	public ILayoutSection getLayoutSection()
	{
		switch (this)
		{
			case HEADER:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new ReceiptLayoutHeaderSection(this);
				}
				return this.layoutSection;
			}
			case POSITION:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new ReceiptLayoutPositionSection(this);
				}
				return this.layoutSection;
			}
			case PAYMENT:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new ReceiptLayoutPaymentSection(this);
				}
				return this.layoutSection;
			}
			case TAX:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new ReceiptLayoutTaxSection(this);
				}
				return this.layoutSection;
			}
			case CUSTOMER:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new ReceiptLayoutCustomerSection(this);
				}
				return this.layoutSection;
			}
			case FOOTER:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new ReceiptLayoutFooterSection(this);
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
				return "section.receipt.header";
			}
			case POSITION:
			{
				return "section.receipt.position";
			}
			case PAYMENT:
			{
				return "section.receipt.payment";
			}
			case TAX:
			{
				return "section.receipt.tax";
			}
			case CUSTOMER:
			{
				return "section.receipt.customer";
			}
			case FOOTER:
			{
				return "section.receipt.footer";
			}
			default:
			{
				throw new RuntimeException("Invalid print area type");
			}
		}
	}

	@Override
	public String getSectionTitle()
	{
		switch (this)
		{
			case HEADER:
			{
				return "Abschnitt Kopf";
			}
			case POSITION:
			{
				return "Abschnitt Positionen";
			}
			case PAYMENT:
			{
				return "Abschnitt Zahlungen";
			}
			case TAX:
			{
				return "Abschnitt Mehrwertsteuer";
			}
			case CUSTOMER:
			{
				return "Abschnitt Kunden";
			}
			case FOOTER:
			{
				return "Abschnitt Fuss";
			}
			default:
			{
				throw new RuntimeException("Invalid print area type");
			}
		}
	}

	@Override
	public void setColumnCount(final int columns)
	{
		this.columnCount = columns;
	}
}
