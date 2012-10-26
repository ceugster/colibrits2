/*
 * Created on 19.06.2003
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
public class Option extends Table
{
	
	public String code = ""; //$NON-NLS-1$
	public String name = ""; //$NON-NLS-1$
	
	/**
	 * 
	 */
	public Option()
	{
		super();
	}
	
	public Option(String code, String name)
	{
		super();
		this.code = code;
		this.name = name;
	}
}
