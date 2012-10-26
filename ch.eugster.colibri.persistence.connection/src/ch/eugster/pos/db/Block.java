/*
 * Created on 08.07.2003
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
public class Block extends Table
{
	
	public boolean visible = Table.BOOLEAN_DEFAULT_FALSE;
	public String name = ""; //$NON-NLS-1$
	public String clazz = ""; //$NON-NLS-1$
	public double fontSize = 14f;
	public int fontStyle = 0;
	
	public RemovalAwareCollection tabs = new RemovalAwareCollection();
	
	/**
	 * 
	 */
	public Block()
	{
		super();
	}
	
}
