/*
 * Created on 13.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ch.eugster.pos.db;

import java.util.Hashtable;

/**
 * @author administrator
 */
public class ProductGroup extends Table
{

	/*
	 * Die Defaultgruppe wird eingesetzt, wenn keine Warengruppe gefunden wurde.
	 */
	public boolean isDefault = ProductGroup.DEFAULT_GROUP;

	/*
	 * GalileoId entspricht der Warengruppe in Galileo. Da ColibriTS neben den
	 * Galileo- auch eigene Warengruppen (auch Ausgaben und nicht
	 * umsatzrelevante Leistungen haben kann, wurde ProductGroup durch dieses
	 * Attribut zur Kenntzeichnung der Warengruppen, die aus Galileo kommen
	 * ergänzt
	 */
	public String galileoId = ProductGroup.GALILEO_ID_DEFAULT;

	/*
	 * Ein Kürzel, das automatisch bei der Verwendung in die Tastenbelegung
	 * übernommen wird
	 */
	public String shortname = ProductGroup.SHORT_NAME_DEFAULT;

	/*
	 * Die Bezeichnung der Warengruppe
	 */
	public String name = ProductGroup.NAME_DEFAULT;

	/*
	 * Mengenvorschlag
	 */
	public int quantityProposal = ProductGroup.QUANTITY_DEFAULT;

	/*
	 * Preisvorschlag
	 */
	public double priceProposal = ProductGroup.PRICE_DEFAULT;

	/*
	 * Optionscodevorschlag
	 */
	public String optCodeProposal = ProductGroup.OPT_CODE_DEFAULT;

	/*
	 * Das Fibukonto wird beim Fibutransfer benötigt. Wenn die Funktion
	 * Fibutransfer verwendet wird, müssen alle Warengruppen ein gültiges und
	 * der Fibu bekanntes Konto aufweisen
	 */
	public String account = ""; //$NON-NLS-1$

	/*
	 * WG: Bezahlte Rechnung
	 */
	public boolean paidInvoice = ProductGroup.PAID_INVOICE_DEFAULT;

	/*
	 * Ausgabe
	 */
	// public Boolean isExpense = IS_EXPENSE_DEFAULT;
	/*
	 * Wurde in Galileo verändert. Daher sollten die Warengruppeneigenschaften
	 * überprüft werden
	 */
	public boolean modified = ProductGroup.MODIFIED;

	public int type = ProductGroup.TYPE_INCOME;

	public String exportId = "";

	private Tax defaultTax;

	private Long defaultTaxId;

	@SuppressWarnings("unused")
	private ForeignCurrency foreignCurrency;

	private Long foreignCurrencyId;

	/**
	 * Neuer Typ ab 10231: Geldentnahme aus Kasse zwecks Eigeneinzahlung auf
	 * Bank
	 */
	public boolean withdraw = false;

	@SuppressWarnings("unused")
	private static ProductGroup defaultProductGroup;

	// public void setDefaultTax(Tax tax)
	// {
	// if (tax == null)
	// {
	// tax = Tax.getByCode("UR");
	// }
	// this.defaultTax = tax;
	// this.defaultTaxId = tax.getId();
	// }

	@SuppressWarnings("unused")
	private static ProductGroup paidInvoiceProductGroup;

	@SuppressWarnings({ "rawtypes", "unused" })
	private static Hashtable records = new Hashtable();

	@SuppressWarnings({ "rawtypes", "unused" })
	private static Hashtable galileoIdIndex = new Hashtable();

	public static final String NAME_DEFAULT = ""; //$NON-NLS-1$

	public static final String SHORT_NAME_DEFAULT = ""; //$NON-NLS-1$

	public static final String GALILEO_ID_DEFAULT = ""; //$NON-NLS-1$

	public static final int QUANTITY_DEFAULT = 0;

	public static final double PRICE_DEFAULT = 0.0d;

	public static final String OPT_CODE_DEFAULT = ""; //$NON-NLS-1$

	public static final boolean PAID_INVOICE_DEFAULT = false;

	// public static final Boolean IS_EXPENSE_DEFAULT = new Boolean(false);
	public static final boolean MODIFIED = false;

	public static final boolean DEFAULT_GROUP = false;

	// ProductGroup Types
	public static final int TYPE_INCOME = 0;

	public static final int TYPE_NOT_INCOME = 1;

	public static final int TYPE_EXPENSE = 2;

	public static final int TYPE_INPUT = 3;

	public static final int TYPE_WITHDRAW = 4;

	public ProductGroup()
	{
		// this.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
	}

	public Tax getDefaultTax()
	{
		return this.defaultTax;
	}

	public Long getDefaultTaxId()
	{
		return this.defaultTaxId;
	}

	public Long getForeignCurrencyId()
	{
		return this.foreignCurrencyId;
	}

	public void setForeignCurrency(final ForeignCurrency currency)
	{
		this.foreignCurrency = currency;
		this.foreignCurrencyId = currency == null ? null : currency.getId();
	}
}
