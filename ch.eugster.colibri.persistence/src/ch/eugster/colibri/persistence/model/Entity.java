package ch.eugster.colibri.persistence.model;

import java.util.Calendar;

public interface Entity
{
	Long getId();

	Calendar getTimestamp();

	int getUpdate();

	int getVersion();

	boolean isDeleted();

	void setDeleted(boolean deleted);

	void setId(Long id);

	void setTimestamp(Calendar timestamp);

	void setUpdate(int update);

	void setVersion(int version);
}
