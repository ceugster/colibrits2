/*
 * Created on 20.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FixKey extends Key
{
	
	public String block = ""; //$NON-NLS-1$
	
	private FixKeyGroup fixKeyGroup;
	private Long fixKeyGroupId;
	// 10052
	// Hintergrundfarbe für Failover
	@SuppressWarnings("unused")
	private int bgRedFailover = 255;
	@SuppressWarnings("unused")
	private int bgGreenFailover = 222;
	@SuppressWarnings("unused")
	private int bgBlueFailover = 222;
	
	/**
	 * 
	 */
	public FixKey()
	{
		super();
	}
	
	public void setFixKeyGroup(FixKeyGroup group)
	{
		this.fixKeyGroup = group;
		this.fixKeyGroupId = group.getId();
	}
	
	public FixKeyGroup getFixKeyGroup()
	{
		return this.fixKeyGroup;
	}
	
	public Long getFixKeyGroupId()
	{
		return this.fixKeyGroupId;
	}
}
