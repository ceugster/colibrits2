/*
 * Created on 13.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.swt.graphics.Image;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.connection.config.DatabaseConfigurator;
import ch.eugster.colibri.persistence.connection.config.DatabaseMigrator;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.replication.service.ReplicationService;
import ch.eugster.pos.db.Salespoint;

public class DatabaseWizard extends Wizard
{
	private Document document;

	private Element selectedConnection;

	public void setSelectedConnection(Element connection)
	{
		this.selectedConnection = connection;
	}
	
	public Element getSelectedConnection()
	{
		return selectedConnection;
	}
	
	public DatabaseWizard()
	{
		this.document = Activator.getDefault().getDocument();
		if (this.document == null)
		{
			this.document = this.initializeDocument();
		}
	}

	@Override
	public void addPages()
	{
		WizardDialog dialog = (WizardDialog) this.getContainer();
		if (Activator.getDefault().getFile().exists())
		{
			this.addPage(new DatabaseWizardSelectConnectionPage("select.connection.wizard.page"));
		}
		DatabaseWizardConnectionPage connectionPage = new DatabaseWizardConnectionPage("connection.wizard.page");
		dialog.addPageChangedListener(connectionPage);
		this.addPage(connectionPage);
		this.addPage(new DatabaseWizardMigrationPage("migration.wizard.page"));
		this.addPage(new DatabaseWizardCurrencyPage("currency.wizard.page"));
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	@Override
	public boolean canFinish()
	{
		IWizardPage page = this.getPage("select.connection.wizard.page");
		if (page instanceof DatabaseWizardSelectConnectionPage)
		{
			final DatabaseWizardSelectConnectionPage selectPage = (DatabaseWizardSelectConnectionPage) page;
			if (!selectPage.canFlipToNextPage())
			{
				return true;
			}
		}

		page = this.getPage("connection.wizard.page");
		if (page.isPageComplete())
		{
			if (page instanceof DatabaseWizardConnectionPage)
			{
				final DatabaseWizardConnectionPage connectionPage = (DatabaseWizardConnectionPage) page;
				if (connectionPage.migrate())
				{
					final IWizardPage migrationPage = this.getPage("migration.wizard.page");
					return migrationPage.isPageComplete();
				}
				else
				{
					final IWizardPage currencyPage = this.getPage("currency.wizard.page");
					return currencyPage.isPageComplete();
				}
			}
		}

		return false;
	}

	@Override
	public Image getDefaultPageImage()
	{
		return Activator.getDefault().getImageRegistry().get("DATABASE");
	}

	public Document getDocument()
	{
		return this.document;
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page)
	{
		if (page instanceof DatabaseWizardSelectConnectionPage)
		{
			final DatabaseWizardSelectConnectionPage selectWizardPage = (DatabaseWizardSelectConnectionPage) page;
			if (selectWizardPage.canFlipToNextPage())
			{
				return this.getPage("connection.wizard.page");
			}
			return null;
		}

		if (page instanceof DatabaseWizardConnectionPage)
		{
			if (((DatabaseWizardConnectionPage) page).migrate())
			{
				return this.getPage("migration.wizard.page");
			}
			return this.getPage("currency.wizard.page");
		}
		return null;
	}

	@Override
	public boolean performCancel()
	{
		return true;
	}

	@Override
	public boolean performFinish()
	{
		Element oldSelection = this.getOldSelection();
		Element newSelection = this.getNewSelection();

		if (oldSelection != newSelection)
		{
			if (newSelection != null)
			{
				newSelection = (Element) newSelection.detach();
				this.document.getRootElement().getChild("current").setContent(newSelection);
				this.document.getRootElement().getChild("current").setAttribute("log-level", this.getLogLevel());

				if (oldSelection != null)
				{
					oldSelection = (Element) oldSelection.detach();

					@SuppressWarnings("unchecked")
					final List<Element> connections = this.document.getRootElement().getChildren("connection");
					Element removableConnection = null;
					for (final Element connection : connections)
					{
						if (connection.getText().equals(oldSelection.getText()))
						{
							removableConnection = connection;
						}
					}
					if (removableConnection != null)
					{
						this.document.getRootElement().removeContent(removableConnection);
					}
					this.document.getRootElement().addContent(oldSelection);
				}
			}
		}
		Activator.getDefault().saveDocument(this.document);

		final DatabaseWizardSelectConnectionPage selectPage = (DatabaseWizardSelectConnectionPage) this
				.getPage("select.connection.wizard.page");
		if ((selectPage == null) || selectPage.addNewConnection())
		{
			User.setLoginUser(User.newInstance());
			Activator.getDefault().startPersistenceService();
			final DatabaseWizardConnectionPage connectionWizardPage = (DatabaseWizardConnectionPage) this
					.getPage("connection.wizard.page");

			
			if (connectionWizardPage.migrate())
			{
				final DatabaseWizardMigrationPage migrationWizardPage = (DatabaseWizardMigrationPage) this
						.getPage("migration.wizard.page");
				final Document oldDocument = migrationWizardPage.getDocument();
				saveColibriXml(oldDocument);
				final Salespoint[] salespoints = migrationWizardPage.getSalespoints();
				this.startMigration(newSelection, oldDocument, salespoints);
			}
			else
			{
				final DatabaseWizardCurrencyPage currencyWizardPage = (DatabaseWizardCurrencyPage) this
						.getPage("currency.wizard.page");
				final Long currencyId = currencyWizardPage.getSelectedCurrency();
				final Long startReceiptNumber = currencyWizardPage.getStartReceiptNumber();
				this.startConfiguration(newSelection, currencyId, startReceiptNumber);
			}
			ServiceTracker<ReplicationService, ReplicationService> tracker = new ServiceTracker<ReplicationService, ReplicationService>(Activator.getDefault().getBundle().getBundleContext(), ReplicationService.class, null);
			tracker.open();
			try
			{
				ReplicationService service = tracker.getService();
				if (service != null)
				{
					service.replicate(this.getShell(), true);
				}
			}
			finally
			{
				tracker.close();
			}
		}
		return true;
	}

	private void saveColibriXml(Document document)
	{
		String homeDir = System.getProperty("user.home");
		File migrationDir = new File(homeDir + File.separator + "Migration");
		migrationDir.mkdirs();
		File colibri = new File(migrationDir.getAbsolutePath() + File.separator + "colibri.xml");
		if (colibri.exists())
		{
			colibri.delete();
		}

		XMLOutputter out = new XMLOutputter();
        FileWriter writer;
		try 
		{
			writer = new FileWriter(colibri);
	        out.output(document, writer);
	        writer.flush();
	        writer.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private Element getNewSelection()
	{
		final DatabaseWizardConnectionPage connectionWizardPage = (DatabaseWizardConnectionPage) this
				.getPage("connection.wizard.page");
		return connectionWizardPage.updateElement();
	}

	private String getLogLevel()
	{
		final DatabaseWizardConnectionPage connectionWizardPage = (DatabaseWizardConnectionPage) this
					.getPage("connection.wizard.page");
		String logLevel = connectionWizardPage.getLogLevel();
		return logLevel == null ? SessionLog.INFO_LABEL : logLevel;
	}

	private Element getOldSelection()
	{
		return this.document.getRootElement().getChild("current").getChild("connection");
	}

	private Document initializeDocument()
	{
		final Element current = new Element("current");
		final Element root = new Element("database");
		root.setContent(current);

		final Document document = new Document();
		document.setDocType(new DocType("database", "database.dtd"));
		document.setRootElement(root);
		return document;
	}

	private void startConfiguration(final Element connection, final Long currencyId, Long startReceiptNumber)
	{
		final DatabaseConfigurator configurator = new DatabaseConfigurator(this.getShell(), connection, currencyId, startReceiptNumber);
		configurator.configureDatabase();
	}

	private void startMigration(final Element connection, final Document oldDocument, final Salespoint[] salespoints)
	{
		final DatabaseMigrator migrator = new DatabaseMigrator(this.getShell(), connection, oldDocument, salespoints);
		migrator.migrateDatabase();
	}
}
