package ch.eugster.colibri.persistence.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "colibri_sequence")
public class Sequence
{
	@Id
	@Column(name = "sq_key")
	private String key;

	@Basic
	@Column(name = "sq_val")
	private long value;

	public String getKey()
	{
		return this.key;
	}

	public long getValue()
	{
		return this.value;
	}

	public void setKey(final String key)
	{
		this.key = key;
	}

	public void setValue(final long value)
	{
		this.value = value;
	}
}
