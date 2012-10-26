/*
 * Created on 08.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.db;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Version
{

	/*
	 * Die folgenden Variablen sind nicht in der Datenbank!
	 */
	private static int major = 1;

	private static int minor = 6;

	private static int service = 0;

	private static int build = 337;

	private static String date = "14.08.2009";

	@SuppressWarnings("unused")
	private String connectionId = ""; //$NON-NLS-1$

	private static int runningProgram = -1;

	public static boolean isFrameVisible = false;

	// Version 16 ab Build 177
	private static int dataVersion = 34; // Datenversion Programm

	/*
	 * Die folgenden Variablen sind in der Datenbank
	 */
	private int data = 0; // Datenversion in Datenbank

	private Long transactionId = Table.ZERO_VALUE;

	public static final int COLIBRI = 0;

	public static final int ADMINISTRATOR = 1;

	public static final int STATISTICS = 2;

	public static final int RECEIPT_BROWSER = 3;

	public Version()
	{
		super();
	}

	public int getData()
	{
		return data;
	}

	public Long getTransactionId()
	{
		return transactionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.db.Table#isRemovable()
	 */
	public boolean isRemovable()
	{
		return false;
	}

	public void setData(final int data)
	{
		this.data = data;
	}

	public void setTransactionId(final Long id)
	{
		transactionId = id;
	}

	public static int getBuild()
	{
		return Version.build;
	}

	public static int getMyDataVersion()
	{
		return Version.dataVersion;
	}

	// public static void readDBRecords()
	// {
	// Version version = Version.select(Database.getCurrent());
	// Version.put(version);
	// }

	public static int getRunningProgram()
	{
		return Version.runningProgram;
	}

	public static String getVersionDate()
	{
		return Version.date;
	}

	public static void setRunningProgram(final int program)
	{
		Version.runningProgram = program;
	}

	public static String version()
	{
		return String.valueOf(Version.major) + "." //$NON-NLS-1$
				+ String.valueOf(Version.minor) + "." //$NON-NLS-1$
				+ String.valueOf(Version.service) + "-" //$NON-NLS-1$
				+ String.valueOf(Version.build);
	}
}
