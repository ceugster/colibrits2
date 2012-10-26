/*
 * Created on 13.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ch.eugster.pos.db;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.ojb.broker.util.collections.RemovalAwareCollection;

/**
 * @author administrator
 */
public class User extends Table
{
	
	public String username = ""; //$NON-NLS-1$
	public String password = ""; //$NON-NLS-1$
	public Long posLogin = new Long(0L);
	public int status = User.USER_STATE_EMPLOYEE;
	public Boolean defaultUser = new Boolean(false);
	private boolean reverseReceipts;
	
	@SuppressWarnings("unused")
	private RemovalAwareCollection userAccesses = new RemovalAwareCollection();
	
	public User()
	{}
	
	public boolean getReverseReceipts()
	{
		return this.reverseReceipts;
	}
	
	public void setReverseReceipts(boolean reverse)
	{
		this.reverseReceipts = reverse;
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private static Hashtable records = new Hashtable();
	@SuppressWarnings({ "rawtypes", "unused" })
	private static Hashtable posLoginIndex = new Hashtable();
	@SuppressWarnings({ "rawtypes", "unused" })
	private static Hashtable usernameIndex = new Hashtable();
	
	@SuppressWarnings("unused")
	private static User defUser = null;
	@SuppressWarnings("unused")
	private static User currentUser = null;
	@SuppressWarnings({ "rawtypes", "unused" })
	private static ArrayList userChangeListeners = new ArrayList();
	
	public static final int USER_STATE_ADMINISTRATOR = 0;
	public static final int USER_STATE_MANAGER = 1;
	public static final int USER_STATE_EMPLOYEE = 2;
	
	public static final Integer[] USER_STATE_VALUE =
					{ new Integer(User.USER_STATE_ADMINISTRATOR), new Integer(User.USER_STATE_MANAGER),
					new Integer(User.USER_STATE_EMPLOYEE) };
	// public static final String[] USER_STATE_TEXT =
	// { Messages.getString("UserFieldEditorPage.Administrator_2"),
	// Messages.getString("UserFieldEditorPage.Manager_3"),
	// Messages.getString("UserFieldEditorPage.Benutzer_4") };
}
