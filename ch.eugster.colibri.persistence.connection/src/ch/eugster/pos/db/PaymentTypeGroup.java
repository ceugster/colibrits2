/*
 * Created on 14.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import org.apache.ojb.broker.util.collections.RemovalAwareCollection;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PaymentTypeGroup extends Table
{
	
	public String code = ""; //$NON-NLS-1$
	public String name = ""; //$NON-NLS-1$
	public String defaultAccount = ""; //$NON-NLS-1$
	public boolean visible = true;
	
	protected RemovalAwareCollection paymentTypes = new RemovalAwareCollection();
	
	/**
	 * 
	 */
	public PaymentTypeGroup()
	{
		super();
	}
	
}
