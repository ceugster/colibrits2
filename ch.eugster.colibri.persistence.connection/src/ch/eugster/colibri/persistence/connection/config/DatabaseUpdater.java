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

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.model.Version;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

public abstract class DatabaseUpdater extends AbstractInitializer
{
	public static DatabaseUpdater newInstance(Properties properties)
	{
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
		return null;
	}
	
	public DatabaseUpdater(final Properties properties)
	{
		super(properties);
	}

	public IStatus updateDatabase()
	{
		IStatus status = Status.OK_STATUS;
		final Connection connection = this.createConnection();
		if (connection != null)
		{
			status = this.updateStructure(connection);
			this.releaseConnection(connection);
		}
		return status;
	}

	private IStatus tableExists(final Connection connection, final String tableName)
	{
		IStatus status = null;
		final String[] types = new String[] { "TABLE" };
		StringBuilder msg = new StringBuilder("Prüfe, ob Tabelle " + tableName + " in "
				+ properties.getProperty(PersistenceUnitProperties.JDBC_URL) + " vorhanden ist: ");
		try
		{
			final ResultSet tables = connection.getMetaData().getTables(null, null, tableName.toUpperCase(), types);
			status = tables.next() ? new Status(IStatus.OK, Activator.PLUGIN_ID, msg.append("OK").toString())
					: new Status(IStatus.CANCEL, Activator.PLUGIN_ID, msg.append("FEHLT").toString());
		}
		catch (final SQLException e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
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
					status = new Status(IStatus.OK, Activator.PLUGIN_ID, "Die Spalte " + columnName + " in Tabelle "
							+ tableName + " ist vorhanden.");
					break;
				}
			}
			if (status == null)
			{
				status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Die Spalte " + columnName + " in Tabelle "
						+ tableName + " ist nicht vorhanden.");
			}
		}
		catch (final SQLException e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
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
//				status = new Status(IStatus.OK, Activator.PLUGIN_ID, "Die Zeile mit dem Wert = " + primaryKeyValue
//						+ " in der Spalte " + primaryKeyColumn + " in Tabelle " + table + " ist vorhanden.");
//			}
//			else
//			{
//				status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Die Zeile mit dem Wert = " + primaryKeyValue
//						+ " in der Spalte " + primaryKeyColumn + " in Tabelle " + table + " ist nicht vorhanden.");
//			}
//		}
//		catch (SQLException e)
//		{
//			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
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
	private IStatus updateStructure(final Connection connection)
	{
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
							Activator.PLUGIN_ID,
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
									this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", "NULL", true);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}

								tableName = "colibri_payment_type";
								columnName = "pt_pg_id";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "BIGINT", "NULL", true);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}

//								columnName = "pt_charge";
//								status = this.columnExists(connection, tableName, columnName);
//								if (status.getSeverity() == IStatus.CANCEL)
//								{
//									this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
//									sql = getAddColumnStatement(tableName, columnName, getDoubleTypeName(), "0", false);
//									this.log("SQL: " + sql);
//									result = stm.executeUpdate(sql);
//									this.log("SQL STATE:" + result + " OK)");
//								}

								columnName = "pt_charge_type";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "NULL", true);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
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
									this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, getDoubleTypeName(), "0", false);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}

								columnName = "pt_fix_charge";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "NULL", true);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}

//								columnName = "pt_charge";
//								status = this.columnExists(connection, tableName, columnName);
//								if (status.getSeverity() == IStatus.OK)
//								{
//									this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
//									sql = getDropColumnStatement(tableName, columnName);
//									this.log("SQL: " + sql);
//									result = stm.executeUpdate(sql);
//									this.log("SQL STATE:" + result + " OK)");
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
									this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
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
									this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}

							else if (structureVersion == 4)
							{
								this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings";
								String columnName = "cs_transfer_delay";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "60000", false);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}
								columnName = "cs_transfer_repeat_delay";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "15000", false);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}
								columnName = "cs_transfer_receipt_count";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "5", false);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}

								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}

							else if (structureVersion == 5)
							{
								this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_common_settings";
								String columnName = "cs_maximized_client_window";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "SMALLINT", "0", false);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}
								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}

							else if (structureVersion == 6)
							{
								this.log("Aktualisiere Datenbank auf Version " + (structureVersion + 1) + "...");
								tableName = "colibri_version";
								String columnName = "v_replication_value";
								status = this.columnExists(connection, tableName, columnName);
								if (status.getSeverity() == IStatus.CANCEL)
								{
									sql = getAddColumnStatement(tableName, columnName, "INTEGER", "0", false);
									this.log("SQL: " + sql);
									result = stm.executeUpdate(sql);
									this.log("SQL STATE:" + result + " OK)");
								}
								structureVersion = structureVersion < Version.STRUCTURE ? ++structureVersion
										: structureVersion;
							}

							this.log("Aktualisiere die Version der Datenbankstruktur auf Version " + structureVersion
									+ ".");
							sql = "UPDATE colibri_version SET v_structure = " + structureVersion;
							this.log("SQL: " + sql);
							result = stm.executeUpdate(sql);
							this.log("SQL STATE: " + result + " updated rows");
						}
						status = new Status(IStatus.OK, Activator.PLUGIN_ID,
								"Die Datenbankstruktur wurde auf die Version " + structureVersion
										+ " aktualisiert. Die Änderungen wurden protokolliert.");
					}
				}
			}
			catch (final SQLException e)
			{
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
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
}
