/*
 * Created on 20.05.2003
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
public class Tab extends Table
{
	
	public boolean defaultTabPosition = false;
	public boolean defaultTabPayment = false;
	public int order = Table.INTEGER_DEFAULT_ZERO;
	public int rows = Table.INTEGER_DEFAULT_ZERO;
	public int columns = Table.INTEGER_DEFAULT_ZERO;
	public boolean visible = false;
	public double fontSize = 0f;
	public int fontStyle = Table.INTEGER_DEFAULT_ZERO;
	public String title = ""; //$NON-NLS-1$
	
	private Long blockId = null;
	private Block block;
	public RemovalAwareCollection keys = new RemovalAwareCollection();
	
	/**
	 * 
	 */
	public Tab()
	{}
	
	public void setBlock(Block block)
	{
		this.block = block;
		this.blockId = block.getId();
	}
	
	public Long getBlockId()
	{
		return this.blockId;
	}
	
	public Block getBlock()
	{
		return this.block;
	}
	
}
