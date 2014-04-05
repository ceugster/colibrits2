package ch.eugster.colibri.print.settlement.sections;

import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public enum SettlementLayoutSectionType implements ILayoutSectionType
{
	HEADER, PRODUCT_GROUP, OTHER_SALES, EXPENSES, PRODUCT_GROUP_SUMMARY, PAYMENT, SUMMARY, TAX, PAYED_INVOICE, RESTITUTION, INTERNAL, REVERSED_RECEIPT, SETTLEMENT, MONEY, FOOTER;

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
			case PRODUCT_GROUP:
			{
				return 128;
			}
			case OTHER_SALES:
			{
				return 128;
			}
			case EXPENSES:
			{
				return 128;
			}
			case PRODUCT_GROUP_SUMMARY:
			{
				return 48;
			}
			case PAYMENT:
			{
				return 48;
			}
			case SUMMARY:
			{
				return 48;
			}
			case TAX:
			{
				return 48;
			}
			case PAYED_INVOICE:
			{
				return 48;
			}
			case RESTITUTION:
			{
				return 48;
			}
			case INTERNAL:
			{
				return 48;
			}
			case REVERSED_RECEIPT:
			{
				return 48;
			}
			case SETTLEMENT:
			{
				return 48;
			}
			case MONEY:
			{
				return 24;
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
					this.layoutSection = new SettlementLayoutHeaderSection(this);
				}
				return this.layoutSection;
			}
			case PRODUCT_GROUP:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutProductGroupSection(this);
				}
				return this.layoutSection;
			}
			case OTHER_SALES:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutOtherSaleSection(this);
				}
				return this.layoutSection;
			}
			case EXPENSES:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutExpensesSection(this);
				}
				return this.layoutSection;
			}
			case PRODUCT_GROUP_SUMMARY:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutProductGroupSummarySection(this);
				}
				return this.layoutSection;
			}
			case PAYMENT:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutPaymentSection(this);
				}
				return this.layoutSection;
			}
			case SUMMARY:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutSummarySection(this);
				}
				return this.layoutSection;
			}
			case TAX:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutTaxSection(this);
				}
				return this.layoutSection;
			}
			case PAYED_INVOICE:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutPayedInvoiceSection(this);
				}
				return this.layoutSection;
			}
			case RESTITUTION:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutRestitutionSection(this);
				}
				return this.layoutSection;
			}
			case INTERNAL:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutInternalSection(this);
				}
				return this.layoutSection;
			}
			case REVERSED_RECEIPT:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutReceiptSection(this);
				}
				return this.layoutSection;
			}
			case SETTLEMENT:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutSettlementSection(this);
				}
				return this.layoutSection;
			}
			case MONEY:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutMoneySection(this);
				}
				return this.layoutSection;
			}
			case FOOTER:
			{
				if (this.layoutSection == null)
				{
					this.layoutSection = new SettlementLayoutFooterSection(this);
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
				return "section.settlement.header";
			}
			case PRODUCT_GROUP:
			{
				return "section.settlement.product.group";
			}
			case OTHER_SALES:
			{
				return "section.settlement.other.sales";
			}
			case EXPENSES:
			{
				return "section.settlement.expenses";
			}
			case PRODUCT_GROUP_SUMMARY:
			{
				return "section.settlement.productgroup.summary";
			}
			case PAYMENT:
			{
				return "section.settlement.payment";
			}
			case SUMMARY:
			{
				return "section.settlement.summary";
			}
			case TAX:
			{
				return "section.settlement.tax";
			}
			case PAYED_INVOICE:
			{
				return "section.settlement.payed.invoice";
			}
			case RESTITUTION:
			{
				return "section.settlement.restitution";
			}
			case INTERNAL:
			{
				return "section.settlement.internal";
			}
			case REVERSED_RECEIPT:
			{
				return "section.settlement.reversed.receipt";
			}
			case SETTLEMENT:
			{
				return "section.settlement.settlement";
			}
			case MONEY:
			{
				return "section.settlement.money";
			}
			case FOOTER:
			{
				return "section.settlement.footer";
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
				return "Kopfbereich";
			}
			case PRODUCT_GROUP:
			{
				return "Warengruppen";
			}
			case OTHER_SALES:
			{
				return "Andere Verkäufe";
			}
			case EXPENSES:
			{
				return "Ausgaben";
			}
			case PRODUCT_GROUP_SUMMARY:
			{
				return "Total Warengruppen";
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
			case PAYED_INVOICE:
			{
				return "Bezahlte Rechnungen";
			}
			case RESTITUTION:
			{
				return "Rücknahmen";
			}
			case INTERNAL:
			{
				return "Einlagen/Entnahmen";
			}
			case REVERSED_RECEIPT:
			{
				return "Stornierte Belege";
			}
			case SETTLEMENT:
			{
				return "Tagesabschluss";
			}
			case MONEY:
			{
				return "Münzzählung";
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
