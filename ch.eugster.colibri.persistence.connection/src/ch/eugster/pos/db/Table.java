/*
 * Created on 07.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Table
{
	
	private Long id;
	
	public Timestamp timestamp = new Timestamp(new Date().getTime());
	
	public boolean deleted = false;
	
	public Table()
	{
		this.id = null;
		this.timestamp = new Timestamp(new Date(0l).getTime());
	}
	
	public void setId(Long id)
	{
		this.id = id;
	}
	
	public Long getId()
	{
		return this.id;
	}
	
	protected DBResult describeError(Exception e)
	{
		//		LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
		DBResult result = new DBResult();
		if (e.getCause() instanceof SQLException)
		{
			result.setErrorCode(DBResult.SQL_ERROR);
			result.setExternalErrorCode(((SQLException) e.getCause()).getSQLState());
			result.setExternalErrorText(((SQLException) e.getCause()).getLocalizedMessage());
			// int vendorCode = ((SQLException) e.getCause()).getErrorCode();
			result.log();
		}
		else
		{
			result.setErrorCode(-1);
			e.printStackTrace();
			result.setErrorText(e.getMessage());
		}
		return result;
	}
	
	public FieldDescriptor[] getAttributeNames()
	{
		return Table.fieldDescriptors;
	}
	
	@SuppressWarnings("unused")
	private static ClassDescriptor classDescriptor;
	private static FieldDescriptor[] fieldDescriptors;
	
	public static final int OK = 0;
	public static final int IS_REFERENCED = -1;
	public static final int SERVER_ERROR = -2;
	public static final String IS_REFERENCED_TEXT = "Der_Datensatz_wird_referenziert_und_darf_nicht_gel_u00F6scht_werden._87";
	
	public static final Long ZERO_VALUE = new Long(0L);
	// public static final Long NULL_ID = null;
	public static final int INTEGER_DEFAULT_ZERO = 0;
	public static final int INTEGER_DEFAULT_255 = 255;
	public static final double DOUBLE_DEFAULT_ZERO = 0d;
	public static final double DOUBLE_DEFAULT_ONE = 1d;
	public static final boolean BOOLEAN_DEFAULT_TRUE = true;
	public static final boolean BOOLEAN_DEFAULT_FALSE = false;
	
	@SuppressWarnings({ "unused", "rawtypes" })
	private static ArrayList databaseErrorListeners = new ArrayList();
	@SuppressWarnings({ "unused", "rawtypes" })
	private static ArrayList modeChangeListeners = new ArrayList();
	
}
