/*
 * Created on 13.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.swt.graphics.Image;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.osgi.framework.BundleException;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.connection.config.DatabaseConfigurator;
import ch.eugster.colibri.persistence.connection.config.DatabaseMigrator;
import ch.eugster.pos.db.Salespoint;

public class DatabaseWizard extends Wizard
{
	private Document document;

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
		if (Activator.getDefault().getFile().exists())
		{
			this.addPage(new SelectConnectionWizardPage("select.connection.wizard.page"));
		}
		this.addPage(new ConnectionWizardPage("connection.wizard.page"));
		this.addPage(new MigrationWizardPage("migration.wizard.page"));
		this.addPage(new CurrencyWizardPage("currency.wizard.page"));
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	@Override
	public boolean canFinish()
	{
		IWizardPage page = this.getPage("select.connection.wizard.page");
		if (page instanceof SelectConnectionWizardPage)
		{
			final SelectConnectionWizardPage selectPage = (SelectConnectionWizardPage) page;
			if (!selectPage.canFlipToNextPage())
			{
				return true;
			}
		}

		page = this.getPage("connection.wizard.page");
		if (page.isPageComplete())
		{
			if (page instanceof ConnectionWizardPage)
			{
				final ConnectionWizardPage connectionPage = (ConnectionWizardPage) page;
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
		if (page instanceof SelectConnectionWizardPage)
		{
			final SelectConnectionWizardPage selectWizardPage = (SelectConnectionWizardPage) page;
			if (selectWizardPage.canFlipToNextPage())
			{
				return this.getPage("connection.wizard.page");
			}
			return null;
		}

		if (page instanceof ConnectionWizardPage)
		{
			if (((ConnectionWizardPage) page).migrate())
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
			Activator.getDefault().saveDocument(this.document);

			final SelectConnectionWizardPage selectPage = (SelectConnectionWizardPage) this
					.getPage("select.connection.wizard.page");
			if ((selectPage == null) || selectPage.addNewConnection())
			{
				final ConnectionWizardPage connectionWizardPage = (ConnectionWizardPage) this
						.getPage("connection.wizard.page");
				if (connectionWizardPage.migrate())
				{
					final MigrationWizardPage migrationWizardPage = (MigrationWizardPage) this
							.getPage("migration.wizard.page");
					final Document oldDocument = migrationWizardPage.getDocument();
					final Salespoint[] salespoints = migrationWizardPage.getSalespoints();
					this.startMigration(newSelection, oldDocument, salespoints);
				}
				else
				{
					final CurrencyWizardPage currencyWizardPage = (CurrencyWizardPage) this
							.getPage("currency.wizard.page");
					final Long currencyId = currencyWizardPage.getSelectedCurrency();
					this.startConfiguration(newSelection, currencyId);
				}
			}

			try
			{
				Platform.getBundle(Activator.PLUGIN_ID).stop();
				Platform.getBundle(Activator.PLUGIN_ID).start();
			}
			catch (final BundleException e)
			{
			}
		}

		return true;
	}

	private Element getNewSelection()
	{
		final SelectConnectionWizardPage selectConnectionWizardPage = (SelectConnectionWizardPage) this
				.getPage("select.connection.wizard.page");
		if ((selectConnectionWizardPage == null) || selectConnectionWizardPage.addNewConnection())
		{
			final ConnectionWizardPage connectionWizardPage = (ConnectionWizardPage) this
					.getPage("connection.wizard.page");
			return connectionWizardPage.updateElement();
		}
		else
		{
			return selectConnectionWizardPage.getSelectedConnection();
		}
	}

	private String getLogLevel()
	{
		final ConnectionWizardPage connectionWizardPage = (ConnectionWizardPage) this
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

	private void startConfiguration(final Element connection, final Long currencyId)
	{
		final DatabaseConfigurator configurator = new DatabaseConfigurator(this.getShell(), connection, currencyId);
		configurator.configureDatabase();
	}

	private void startMigration(final Element connection, final Document oldDocument, final Salespoint[] salespoints)
	{
		final DatabaseMigrator migrator = new DatabaseMigrator(this.getShell(), connection, oldDocument, salespoints);
		migrator.migrateDatabase();
	}
}
