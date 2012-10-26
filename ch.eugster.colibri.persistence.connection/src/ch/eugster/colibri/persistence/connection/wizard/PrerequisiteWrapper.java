package ch.eugster.colibri.persistence.connection.wizard;

import org.jdom.Document;

import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Version;

public class PrerequisiteWrapper
{
	private Document newDocument;

	private boolean migrate;

	private Version version;

	private Salespoint[] salespoints;

	private Document oldDocument;

	private Long currencyId;

	public Long getCurrencyId()
	{
		return this.currencyId;
	}

	public Document getNewDocument()
	{
		return this.newDocument;
	}

	public Document getOldDocument()
	{
		return this.oldDocument;
	}

	public Salespoint[] getSalespoints()
	{
		return this.salespoints;
	}

	public Version getVersion()
	{
		return this.version;
	}

	public boolean isMigrate()
	{
		return this.migrate;
	}

	public void setCurrencyId(final Long id)
	{
		this.currencyId = id;
	}

	public void setMigrate(final boolean migrate)
	{
		this.migrate = migrate;
	}

	public void setNewDocument(final Document document)
	{
		this.newDocument = document;
	}

	public void setOldDocument(final Document oldDocument)
	{
		this.oldDocument = oldDocument;
	}

	public void setSalespoints(final Salespoint[] salespoints)
	{
		this.salespoints = salespoints;
	}

	public void setVersion(final Version version)
	{
		this.version = version;
	}

}
