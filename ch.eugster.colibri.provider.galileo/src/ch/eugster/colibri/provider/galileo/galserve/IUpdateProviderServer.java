package ch.eugster.colibri.provider.galileo.galserve;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;

public interface IUpdateProviderServer extends IServer
{
	IStatus updateProvider(final Position position);

	boolean isConnect();
	
	GalileoConfiguration getConfiguration();
}