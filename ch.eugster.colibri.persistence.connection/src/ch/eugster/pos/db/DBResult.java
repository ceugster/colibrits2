/*
 * Created on 02.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.db;

//import java.util.logging.LogManager;

import org.eclipse.swt.widgets.Shell;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DBResult
{
	
	/**
	 * 
	 */
	public DBResult()
	{
		this(0, ""); //$NON-NLS-1$
	}
	
	/**
	 * 
	 */
	public DBResult(int code, String text)
	{
		this(code, text, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * 
	 */
	public DBResult(int code, String text, String externalCode, String externalText)
	{
		super();
		this.errorCode = code;
		this.errorText = text;
		this.externalErrorCode = externalCode;
		this.externalErrorText = externalText;
	}
	
	public void setErrorCode(int code)
	{
		this.errorCode = code;
	}
	
	public int getErrorCode()
	{
		return this.errorCode;
	}
	
	public void setExternalErrorCode(String code)
	{
		this.externalErrorCode = code;
	}
	
	public String getExternalErrorCode()
	{
		return this.externalErrorCode;
	}
	
	public void setErrorText(String text)
	{
		this.errorText = text;
	}
	
	public String getErrorText()
	{
		return this.errorText;
	}
	
	public void setExternalErrorText(String text)
	{
		this.externalErrorText = text;
	}
	
	public String getExternalErrorText()
	{
		return this.externalErrorText;
	}
	
	public void setException(Exception e)
	{
		this.exception = e;
	}
	
	public Exception getException()
	{
		return this.exception;
	}
	
	public void setShell(Shell shell)
	{
//		this.shell = shell;
	}
	
	public void log()
	{
	//		if (getErrorCode() != 0) LogManager.getLogManager().getLogger("colibri").severe(getErrorCode() + ": " + getErrorText()); //$NON-NLS-1$ //$NON-NLS-2$
	//		if (!getExternalErrorCode().equals("")) LogManager.getLogManager().getLogger("colibri").severe(getExternalErrorCode() + ": " + getExternalErrorText()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	// public int showMessage()
	// {
	// String text = this.errorText;
	//		if (!this.externalErrorCode.equals("")) text = text.concat(System.getProperty("line.separator") + System.getProperty("line.separator") + this.externalErrorCode); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	//		if (!this.externalErrorText.equals("")) text = text.concat(": ".concat(this.externalErrorText)); //$NON-NLS-1$ //$NON-NLS-2$
	// // ErrorDialog dialog = new ErrorDialog(getShell(), "Fehler", text);
	// org.eclipse.jface.dialogs.MessageDialog dialog = new
	// org.eclipse.jface.dialogs.MessageDialog(
	// this.getShell(),
	//						"DBResult.title_13"), //$NON-NLS-1$
	// org.eclipse.jface.dialogs.MessageDialog
	// .getImage(org.eclipse.jface.dialogs.MessageDialog.DLG_IMG_ERROR), text,
	// MessageDialog.ERROR, new String[]
	//						{ "DBResult.Ok_14") }, //$NON-NLS-1$
	// 0);
	// return dialog.open();
	// }
	//	
	// public int showMessage(Shell shell)
	// {
	// this.shell = shell;
	// return this.showMessage();
	// }
	//	
	private int errorCode = 0;
	private String errorText = ""; //$NON-NLS-1$
	private String externalErrorCode = ""; //$NON-NLS-1$
	private String externalErrorText = ""; //$NON-NLS-1$
	
	private Exception exception;
	
	public static final int MUST_NOT_BE_DELETED = 1;
	public static final int STORE_FAILED = 2;
	public static final int REMOVAL_FAILED = 3;
	public static final int SQL_ERROR = 4;
	public static final int NO_CONNECTION_ERROR = 5;
	public static final int NO_STORING_NEEDED = 6;
	
	public static final String SQLSTATE_NO_CONNECTION_ERROR = "DBResult.08S01_18"; //$NON-NLS-1$
	
	public static final String STORE_FAILED_TEXT = "DBResult.Der_Datensatz_konnte_nicht_gespeichert_werden._19"; //$NON-NLS-1$
	public static final String REMOVAL_FAILED_TEXT = "DBResult.Der_Datensatz_konnte_nicht_gel_u00F6scht_werden._20";
	public static final String SQL_ERROR_TEXT = ""; //$NON-NLS-1$
	public static final String NO_CONNECTION_ERROR_TEXT = "DBResult.Zur_aktuellen_Datenbank_besteht_keine_Verbindung_oder_die_Verbindung_ist_unterbrochen._22";
}
