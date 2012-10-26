/*
 * Created on 23.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.db;

import org.apache.ojb.broker.util.collections.RemovalAwareCollection;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FixKeyGroup extends Table
{
	
	public String name = ""; //$NON-NLS-1$
	@SuppressWarnings("unused")
	private RemovalAwareCollection fixKeys = new RemovalAwareCollection();
	
	/**
	 * 
	 */
	public FixKeyGroup()
	{
		super();
	}
}
