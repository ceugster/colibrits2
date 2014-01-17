package ch.eugster.colibri.provider.galileo.wgserve.sql;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.wgserve.GalileoProductGroup;
import ch.eugster.colibri.provider.galileo.wgserve.WgserveProxy;

public class WgserveSqlProxy implements WgserveProxy
{
	private Iwgserve2g wgserve;
	
	private boolean open;
	
	public WgserveSqlProxy()
	{
		this.wgserve = ClassFactory.createwgserve2g();
	}
	
	public IStatus openDatabase(final String database)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), "Verbindung zu " + database + " hergestellt.");
		if (!this.open)
		{
			this.open = this.wgserve.do_open(database);
			if (!this.open)
			{
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), "Verbindung zu " + database + " konnte nicht hergestellt werden.");
			}
		}
		return status;
	}

	public void closeDatabase()
	{
		if (this.open)
		{
			this.open = !((Boolean) this.wgserve.do_Close()).booleanValue();
		}
	}

	public String[] readCodes()
	{
		final String codeList = (String) this.wgserve.wglist();
		if (codeList.length() > 0)
		{
			return codeList.split("[|]");
		}
		else
		{
			return new String[0];
		}
	}

	public String[] selectAllCodes()
	{
		if (((Boolean) this.wgserve.do_getwglist()).booleanValue())
		{
			return this.readCodes();
		}
		return new String[0];
	}

	public String[] selectChangedCodes()
	{
		if (((Boolean) this.wgserve.do_getchangedwglist()).booleanValue())
		{
			return this.readCodes();
		}
		return new String[0];
	}

	public boolean getWg(String code)
	{
		return ((Boolean)wgserve.do_getwg(code)).booleanValue();
	}

	public GalileoProductGroup createGalileoProductGroup(final String code)
	{
		final GalileoProductGroup group = new GalileoProductGroup();
		group.setCode(code);
		group.setText((String) this.wgserve.wgtext());
		group.setAccount((String) this.wgserve.konto());
		group.setBox1("");
		group.setBox2("");
		group.setDescBox1("");
		group.setDescBox2("");
		return group;
	}

	public void confirmChanges(final String code)
	{
		this.wgserve.do_setbestaetigt(code);
	}

	public void dispose()
	{
		this.wgserve.destroy();
	}
}
