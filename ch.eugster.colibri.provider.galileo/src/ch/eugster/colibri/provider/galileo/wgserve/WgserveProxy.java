package ch.eugster.colibri.provider.galileo.wgserve;

import org.eclipse.core.runtime.IStatus;

public interface WgserveProxy {

	IStatus openDatabase(final String database);

	String[] readCodes();

	String[] selectAllCodes();

	String[] selectChangedCodes();
	
	boolean getWg(String code);
	
	GalileoProductGroup createGalileoProductGroup(final String code);

	void confirmChanges(final String code);
	
	void closeDatabase();
	
	void dispose();
}