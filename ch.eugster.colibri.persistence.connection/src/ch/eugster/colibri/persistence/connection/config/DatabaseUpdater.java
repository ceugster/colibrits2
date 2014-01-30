package ch.eugster.colibri.persistence.connection.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Properties;

import javax.persistence.Table;

import org.apache.derby.jdbc.ClientDriver;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.model.ProviderState;
import ch.eugster.colibri.persistence.model.Version;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

public abstract class DatabaseUpdater extends AbstractInitializer
{
	public static DatabaseUpdater newInstance(Properties properties)
	{
		Activator.getDefault().log(LogService.LOG_INFO, "Enter DatabaseUpdater.newInstance()");
		String driverName = properties.getProperty(PersistenceUnitProperties.JDBC_DRIVER);
		if (SQLServerDriver.class.getName().equals(driverName)) 
		{
			return new SqlserverDatabaseUpdater(properties);
		}
		if (com.mysql.jdbc.Driver.class.getName().equals(driverName)) 
		{
			return new MysqlDatabaseUpdater(properties);
		}
		if (ClientDriver.class.getName().equals(driverName)) 
		{
			return new DerbyDatabaseUpdater(properties);
		}
		if (EmbeddedDriver.class.getName().equals(driverName)) 
		{
			return new DerbyDatabaseUpdater(properties);
		}
		if (org.postgresql.Driver.class.getName().equals(driverName)) 
		{
			return new PostgresqlDatabaseUpdater(properties);
		}
		Activator.getDefault().log(LogService.LOG_INFO, "Exit DatabaseUpdater.newInstance()");
		return null;
	}
	
	public DatabaseUpdater(final Properties properties)
	{
		super(properties);
	}
	
	public IStatus updateDatabase()
	{
		return updateDatabase(true);
	}

	public IStatus updateDatabase(boolean server)
	{
		Activator.getDefault().log(LogService.LOG_INFO, "Enter DatabaseUpdater.updateDatabase()");
		IStatus status = Status.OK_STATUS;
		final Connection connection = this.createConnection();
		if (connection != null)
		{
			status = this.updateStructure(connection, server);
			this.releaseConnection(connection);
		}
		Activator.getDefault().log(LogService.LOG_INFO, "Exit DatabaseUpdater.updateDatabase()");
		return status;
	}
	
	protected abstract String getDatePart(DatePart datePart, String dateField);

	private IStatus tableExists(final Connection connection, final String tableName)
	{
		IStatus status = null;
		final String[] types = new String[] { "TABLE" };
		StringBuilder msg = new StringBuilder("Prüfe, ob Tabelle " + tableName + " in "
				+ properties.getProperty(PersistenceUnitProperties.JDBC_URL) + " vorhanden ist: ");
		try
		{
			final ResultSet tables = connection.getMetaData().getTables(null, null, tableName.toUpperCase(), types);
			status = tables.next() ? new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), msg.append("OK").toString())
					: new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), msg.append("FEHLT").toString());
		}
		catch (final SQLException e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Ermitteln der Versionstabelle ist ein Fehler aufgetreten.", e);
		}
		return status;
	}

	private IStatus columnExists(java.sql.Connection connection, String tableName, String columnName)
	{
		tableName = tableName.toUpperCase();
		columnName = columnName.toUpperCase();
		IStatus status = null;
		try
		{
			ResultSet rs = connection.getMetaData().getColumns(null, null, tableName, columnName);
			while (rs.next())
			{
				if (rs.getString(rs.findColumn("COLUMN_NAME")).toUpperCase().equals(columnName))
				{
					status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), "Die Spalte " + columnName + " in Tabelle "
							+ tableName + " ist vorhanden.");
					break;
				}
			}
			if (status == null)
			{
				status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), "Die Spalte " + columnName + " in Tabelle "
						+ tableName + " ist nicht vorhanden.");
			}
		}
		catch (final SQLException e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Ermitteln der Versionstabelle ist ein Fehler aufgetreten.", e);
		}
		return status;
	}

//	private IStatus rowExists(java.sql.Connection con, String table, String primaryKeyColumn, Long primaryKeyValue)
//	{
//		IStatus status = null;
//		try
//		{
//			PreparedStatement stm = con.prepareStatement("SELECT * FROM " + table + " WHERE " + primaryKeyColumn
//					+ " = " + primaryKeyValue);
//			ResultSet rs = stm.executeQuery();
//			if (rs.first())
//			{
//				status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), "Die Zeile mit dem Wert = " + primaryKeyValue
//						+ " in der Spalte " + primaryKeyColumn + " in Tabelle " + table + " ist vorhanden.");
//			}
//			else
//			{
//				status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), "Die Zeile mit dem Wert = " + primaryKeyValue
//						+ " in der Spalte " + primaryKeyColumn + " in Tabelle " + table + " ist nicht vorhanden.");
//			}
//		}
//		catch (SQLException e)
//		{
//			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
//					"Beim Ermitteln der Versionstabelle ist ein Fehler aufgetreten.", e);
//		}
//		return status;
//	}

	/**
	 * 
	 * @param connection
	 *            The established connection to the database
	 * @return IStatus.OK if database has the current version or has been
	 *         updated to the current version IStatus.CANCEL if database
	 *         contains no version table IStatus.ERROR if an error has occurred
	 *         while updating the database or if the database version is newer
	 *         than the program version
	 */
	private IStatus updateStructure(final Connection connection, boolean server)
	{
		Activator.getDefault().log(LogService.LOG_INFO, "Enter DatabaseUpdater.updateStructure()");
		final Class<?> clazz = Version.class;
		final Table table = clazz.getAnnotation(Table.class);
		String tableName = "colibri_version";
		if (table != null)
		{
			tableName = table.name();
		}
		IStatus status = this.tableExists(connection, tableName);
		if (status.getSeverity() == IStatus.OK)
		{
			Statement stm = null;
			ResultSet rst = null;
			try
			{
				stm = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
				rst = stm.executeQuery("SELECT * FROM " + tableName);
				if (!rst.next())
				{
					rst.moveToInsertRow();
					rst.updateLong("v_id", 1L);
					rst.updateInt("v_data", 0);
					rst.updateInt("v_structure", 0);
					rst.updateInt("v_migrate", 0);
					rst.updateInt("v_version", 0);
					rst.updateInt("v_update", 0);
					rst.updateInt("v_deleted", 0);
					rst.updateDate("v_timestamp", new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
					rst.insertRow();
					rst = stm.executeQuery("SELECT * FROM " + tableName);
					rst.next();
				}
				int structureVersion = rst.getInt("v_structure");
				if (structureVersion > Version.STRUCTURE)
				{
					status = new Status(
							IStatus.ERROR,
							Activator.getDefault().getBundle().getSymbolicName(),
							"Die Version der Datenbank ist aktueller als die Version der Anwendung. Um Inkonsistenzen in der Datenbank zu vermeiden, wird die Anwendung beendet. Bitte installieren Sie ein Programm, das kompatibel mit der aktuellen Datenbankstruktur ist. Die Datenbankstruktur hat die Version "
									+ Version.STRUCTURE + ".");
				}
				else
				{
					if (structureVersion < Version.STRUCTURE)
					{
						while (structureVersion < Version.STRUCTURE)
						{
							int result = 0;
							String sql = null;
							if (structureVersion == 0)
							{
								tableName = "colibri_stock";
								String columnName = "st_se_id";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", "NULL", true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								tableName = "colibri_payment_type";
								columnName = "pt_pg_id";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", "NULL", true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

//								columnName = "pt_charge";
//								status = this.columnExists(connection, tableName, columnName);
//								if (status.getSeverity() == IStatus.CANCEL)
//								{
//									this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
//									sql = getAddColumnStatement(tableName, columnName, getDoubleTypeName(), "0", false);
//									this.log(LogService.LOG_INFO, "SQL: " + sql);
//									result = stm.executeUpdate(sql);
//									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
//								}

								columnName = "pt_charge_type";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "NULL", true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 1)
							{
								tableName = "colibri_payment_type";
								String columnName = "pt_percentual_charge";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, getDoubleTypeName(), "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								columnName = "pt_fix_charge";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "NULL", true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

//								columnName = "pt_charge";
//								status = this.columnExists(connection, tableName, columnName);
//								if (status.getSeverity() == IStatus.OK)
//								{
//									this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
//									sql = getDropColumnStatement(tableName, columnName);
//									this.log(LogService.LOG_INFO, "SQL: " + sql);
//									result = stm.executeUpdate(sql);
//									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
//								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 2)
							{
								tableName = "colibri_common_settings";
								String columnName = "cs_allow_test_settlement";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 3)
							{
								tableName = "colibri_key";
								String columnName = "key_count";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}

							else if (structureVersion == 4)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings";
								String columnName = "cs_transfer_delay";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "60000", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "cs_transfer_repeat_delay";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "15000", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "cs_transfer_receipt_count";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "5", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}

							else if (structureVersion == 5)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings";
								String columnName = "cs_maximized_client_window";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}

							else if (structureVersion == 6)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_version";
								String columnName = "v_replication_value";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 7)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings";
								String columnName = "cs_force_settlement";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
									if (server)
									{
										sql = "UPDATE " + tableName + " set CS_VERSION = CS_VERSION + 1";
										this.log(LogService.LOG_INFO, "SQL: " + sql);
										result = stm.executeUpdate(sql);
										this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
									}
								}
								tableName = "colibri_salespoint";
								columnName = "sp_force_settlement";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", null, true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");

									if (server)
									{
										sql = "UPDATE " + tableName + " set SP_VERSION = SP_VERSION + 1";
										this.log(LogService.LOG_INFO, "SQL: " + sql);
										result = stm.executeUpdate(sql);
										this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
									}
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 8)
							{
								if (server)
								{
									sql = "UPDATE COLIBRI_PRODUCT_GROUP SET PG_TX_ID = 1, PG_VERSION = PG_VERSION + 1 WHERE PG_PRODUCT_GROUP_TYPE = 1";
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								
								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 9)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_position";
								String columnName = "po_ebook";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 10)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings";
								String columnName = "cs_export";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "cs_export_path";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "VARCHAR(255)", "", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								tableName = "colibri_salespoint";
								columnName = "sp_export";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "sp_export_path";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "VARCHAR(255)", "", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "sp_use_individual_export";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 11)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings_property";
								status = this.tableExists(connection, tableName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = "CREATE TABLE " + tableName + " (csp_id BIGINT, csp_timestamp DATETIME, csp_version INTEGER, csp_update INTEGER, csp_deleted SMALLINT, csp_discriminator VARCHAR(255), csp_cs_id BIGINT, csp_sp_id BIGINT, csp_key VARCHAR(255), csp_value VARCHAR(255))";
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");

									if (this.rowExists(stm, "colibri_sequence", "sq_key", "csp_id").equals(IStatus.CANCEL))
									{
										sql = "INSERT INTO colibri_sequence (sq_key, sq_val) VALUES ('csp_id', 0)";
										this.log(LogService.LOG_INFO, "SQL: " + sql);
										result = stm.executeUpdate(sql);
										this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
									}
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 12)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_receipt";
								String columnName = "re_hour";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								sql = "UPDATE " + tableName + " SET " + columnName + " = " + getDatePart(DatePart.HOUR, "re_timestamp");
								this.log(LogService.LOG_INFO, "SQL: " + sql);
								result = stm.executeUpdate(sql);
								this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 13)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings";
								String columnName = "cs_force_cash_check";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								tableName = "colibri_salespoint";
								columnName = "sp_allow_test_settlement";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								tableName = "colibri_salespoint";
								columnName = "sp_force_cash_check";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 14)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings";
								String columnName = "cs_vpg_id";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", null, true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "cs_vpt_id";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", null, true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 15)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_payment";
								String columnName = "pa_book_provider";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "pa_provider_booked";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "pa_provider_id";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "VARCHAR(255)", null, true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 16)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_payment";
								String columnName = "pa_code";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "VARCHAR(255)", null, true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 17)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_position";
								String columnName = "po_provider_state";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
									sql = "UPDATE colibri_position (po_provider_state) VALUES (" + ProviderState.BOOKED.ordinal() + ")";
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								tableName = "colibri_payment";
								columnName = "pa_provider_state";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
									sql = "UPDATE colibri_payment (pa_provider_state) VALUES (" + ProviderState.BOOKED.ordinal() + ")";
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 18)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_profile";
								String columnName = "pr_input_name_lbl_font_size";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "FLOAT", "0.0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "pr_input_name_lbl_font_style";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "pr_input_name_lbl_fg";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								columnName = "pr_input_name_lbl_bg";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 19)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_receipt";
								String columnName = "re_internal";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 20)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_settlement";
								String columnName = "se_transferred";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 21)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_payment";
								String columnName = "pa_other_id";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", null , true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}
							else if (structureVersion == 22)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_payment";
								String columnName = "pa_server_updated";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", null , true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								tableName = "colibri_position";
								columnName = "po_server_updated";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", null , true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}

							else if (structureVersion == 23)
							{
								this.log(LogService.LOG_INFO, "Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings";
								String columnName = "cs_pg_ebook_id";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", null, true);
									this.log(LogService.LOG_INFO, "SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log(LogService.LOG_INFO, "SQL STATE:" + result + " OK)");
								}
								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}

							this.log(LogService.LOG_INFO, "Aktualisiere die Version der Datenbankstruktur auf Version " + structureVersion
									+ ".");
							sql = "UPDATE colibri_version SET v_structure = " + structureVersion;
							this.log(LogService.LOG_INFO, "SQL: " + sql);
							result = stm.executeUpdate(sql);
							this.log(LogService.LOG_INFO, "SQL STATE: " + result + " updated rows");
						}
						status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
								"Die Datenbankstruktur wurde auf die Version " + structureVersion
										+ " aktualisiert. Die Änderungen wurden protokolliert.");
					}
				}
			}
			catch (final SQLException e)
			{
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
						"Beim Aktualisieren der Datenbankstruktur ist ein Fehler aufgetreten.", e);
			}
			finally
			{
				try
				{
					if (rst != null)
					{
						rst.close();
					}
					if (stm != null)
					{
						stm.close();
					}
				}
				catch (final SQLException e)
				{
					// do nothing
				}
			}
			this.log(status);
		}
		Activator.getDefault().log(LogService.LOG_INFO, "Exit DatabaseUpdater.updateStructure()");
		return status;
	}
	
	protected IStatus rowExists(Statement stm, String tableName, String columnName, String value)
	{
		IStatus status = null;
		try
		{
			StringBuilder msg = new StringBuilder("Prüfe, ob Tabelle " + tableName + " eine Zeile mit "
					+ columnName + " = " + value + "enthält: ");
			String sql = "SELECT COUNT(*) AS COUNT FROM " + tableName + " WHERE " + columnName + " = '" + value + "'";
			this.log(LogService.LOG_INFO, "SQL: " + sql);
			ResultSet result = stm.executeQuery(sql);
			if (result.next())
			{
				status = result.getInt("COUNT") > 0 ? new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), msg.append("OK").toString())
				: new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), msg.append("FEHLT").toString());
			}
			else
			{
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
						"Beim Ermitteln der Versionstabelle ist ein Fehler aufgetreten.");
			}
		}
		catch (final SQLException e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Während der Abfrage ist ein Fehler aufgetreten.", e);
		}
		return status;
	}
	
	protected String getAddColumnStatement(String tableName, String columnName, String type, String defaultValue, boolean nullAllowed)
	{
		StringBuilder sql = new StringBuilder("ALTER TABLE " + tableName.toUpperCase())
							.append(" ADD COLUMN " + columnName.toUpperCase())
							.append(type == null ? " VARCHAR(255)" : " " + type)
							.append(defaultValue == null ? "" : " DEFAULT " + defaultValue)
							.append(nullAllowed ? " NULL" : " NOT NULL");
		return sql.toString();
	}
	
	protected String getDropColumnStatement(String tableName, String columnName)
	{
		return "ALTER TABLE " + tableName.toUpperCase() + " DROP COLUMN "
				+ columnName.toUpperCase();
	}
	
	protected String getClobTypeName()
	{
		return "CLOB";
	}
	
	protected abstract String getDoubleTypeName();

	public enum DatePart
	{
		SECOND, MINUTE, HOUR, DAY, MONTH, YEAR;
	}
}
