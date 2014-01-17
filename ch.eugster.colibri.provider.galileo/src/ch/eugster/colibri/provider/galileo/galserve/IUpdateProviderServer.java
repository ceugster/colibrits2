package ch.eugster.colibri.provider.galileo.galserve;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Position;

public interface IUpdateProviderServer extends IServer
{
	IStatus updateProvider(final Position position);
	
	boolean isConnect();
	
	void close(boolean force);
}