package ch.eugster.colibri.provider.service;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public interface ProviderConfigurator extends ProviderService
{
	IStatus importProductGroups(IProgressMonitor monitor);

	IStatus synchronizeProductGroups(IProgressMonitor monitor);

	IStatus setTaxCodes(IProgressMonitor monitor);
	
	boolean isConnect();
}
