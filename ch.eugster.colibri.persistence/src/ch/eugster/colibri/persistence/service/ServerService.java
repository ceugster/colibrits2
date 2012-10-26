package ch.eugster.colibri.persistence.service;

public interface ServerService extends ConnectionService
{
	public static final String EVENT_TOPIC = "ch/eugster/colibri/persistence/server/database";

	boolean isLocal();
}
