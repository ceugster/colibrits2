/*
 * Created on 25.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import java.util.Map;

import ch.eugster.colibri.persistence.model.ProviderProperty;

public abstract class AbstractServer implements IServer
{
	protected Map<String, ProviderProperty> properties;

	protected boolean isOpen = false;

	protected String database;

	protected boolean keepConnection;

	public boolean start()
	{
		openTrackers();
		boolean result = initializeCOMServer();
		if (result)
		{
			result = configureCOMServer();
		}
		return result;
	}

	public void stop()
	{
		closeTrackers();
		releaseCOMServer();
	}

	protected void close()
	{
		if (isOpen)
		{
			isOpen = doClose(!keepConnection);
		}
	}

	protected abstract void closeTrackers();

	protected abstract boolean configureCOMServer();

	protected abstract boolean doClose(boolean force);

	protected abstract boolean doOpen(String database);

	protected abstract String getProgId();

	protected abstract boolean initializeCOMServer();

	protected boolean keepConnection()
	{
		return keepConnection;
	}

	protected void open()
	{
		if (!isOpen)
		{
			isOpen = doOpen(database);
		}
	}

	protected abstract void openTrackers();

	protected abstract void releaseCOMServer();

}
