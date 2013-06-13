package ch.eugster.colibri.persistence.model;

import java.util.Date;

public interface Entity
{
	Long getId();

	Date getTimestamp();

	int getUpdate();

	int getVersion();

	boolean isDeleted();

	void setDeleted(boolean deleted);

	void setId(Long id);

	void setTimestamp(Date timestamp);

	void setUpdate(int update);

	void setVersion(int version);
}
