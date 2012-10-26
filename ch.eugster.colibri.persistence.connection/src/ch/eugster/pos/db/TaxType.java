package ch.eugster.pos.db;

import org.apache.ojb.broker.util.collections.RemovalAwareCollection;

public class TaxType extends Table
{
	
	public String code = ""; //$NON-NLS-1$
	public String name = ""; //$NON-NLS-1$
	@SuppressWarnings("unused")
	private RemovalAwareCollection taxes = new RemovalAwareCollection();
	
	public TaxType()
	{
		this(""); //$NON-NLS-1$ 
	}
	
	public TaxType(String code)
	{
		this(code, ""); //$NON-NLS-1$
	}
	
	public TaxType(String code, String name)
	{
		this.code = code;
		this.name = name;
	}
	
}
