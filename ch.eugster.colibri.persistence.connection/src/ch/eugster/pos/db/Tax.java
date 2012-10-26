package ch.eugster.pos.db;

import java.util.Hashtable;

import org.apache.ojb.broker.util.collections.RemovalAwareCollection;

public class Tax extends Table
{
	
	public String code = ""; //$NON-NLS-1$
	public String galileoId = ""; //$NON-NLS-1$
	public String code128Id = ""; //$NON-NLS-1$
	public String account = ""; //$NON-NLS-1$
	
	public Long taxTypeId;
	public TaxType taxType;
	public Long taxRateId;
	public TaxRate taxRate;
	public Long currentTaxId = null;
	
	public RemovalAwareCollection currentTaxes = new RemovalAwareCollection();
	public RemovalAwareCollection productGroups = new RemovalAwareCollection();
	
	public CurrentTax currentTax;
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private static Hashtable records = new Hashtable();
	@SuppressWarnings({ "rawtypes", "unused" })
	private static Hashtable galileoIdIndex = new Hashtable();
	@SuppressWarnings({ "rawtypes", "unused" })
	private static Hashtable code128IdIndex = new Hashtable();
	@SuppressWarnings({ "rawtypes", "unused" })
	private static Hashtable codeIndex = new Hashtable();
}