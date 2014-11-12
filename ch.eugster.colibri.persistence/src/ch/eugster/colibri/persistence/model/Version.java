package ch.eugster.colibri.persistence.model;

import java.util.Calendar;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Convert;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "v_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "v_version")),
		@AttributeOverride(name = "update", column = @Column(name = "v_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "v_deleted")) })
@Table(name = "colibri_version")
public class Version extends AbstractEntity
{
	private static final String TITLE = "ColibriTS II";

	/*
	 * Achtung, falls eine neue Tabelle eingeführt wird, muss diese in der
	 * statischen Methode getTableNames aufgeführt werden.
	 */
	public static final int STRUCTURE = 27;

	public static final int DATA = 0;

	public static final String DATE = "27.03.2014";

	@Basic
	@Column(name = "v_data")
	private int data;

	@Id
	@Column(name = "v_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "v_id")
	@TableGenerator(name = "v_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	private Long id;

	@Basic
	@Column(name = "v_structure")
	private int structure;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "v_migrate")
	private boolean migrate;

	/**
	 * Current Version of Server Data
	 */
	@Basic
	@Column(name = "v_replication_value")
	private int replicationValue;

	public int getData()
	{
		return this.data;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public int getStructure()
	{
		return this.structure;
	}

	public boolean isMigrate()
	{
		return this.migrate;
	}

	public void setData(final int data)
	{
		this.data = data;
	}

	@Override
	public void setId(final Long id)
	{
		this.id = id;
	}

	public void setMigrate(final boolean migrate)
	{
		this.migrate = migrate;
	}

	public void setStructure(final int structure)
	{
		this.structure = structure;
	}

	public int getReplicationValue() {
		return replicationValue;
	}

	public void setReplicationValue(int replicationValue) {
		this.replicationValue = replicationValue;
	}

	public static String getInsertSQLQuery()
	{
		final StringBuilder sql = new StringBuilder("INSERT INTO colibri_version (");
		sql.append("v_id, v_timestamp, v_version, v_update, v_deleted, v_data, v_structure");
		sql.append(") VALUES (");
		sql.append("1, " + Calendar.getInstance().getTimeInMillis() + ", 0, 0, 0, 0, 1");
		sql.append(")");
		return sql.toString();
	}

	public static String getTitle()
	{
		return Version.TITLE;
	}
	
	public static String getStructureVersion()
	{
		return " (Datenversion: " + Version.STRUCTURE + ")";
	}

	public static Version newInstance()
	{
		return (Version) AbstractEntity.newInstance(new Version());
	}

}
