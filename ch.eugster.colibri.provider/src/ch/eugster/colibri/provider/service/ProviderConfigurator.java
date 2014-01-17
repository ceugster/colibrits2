package ch.eugster.colibri.provider.service;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;

public interface ProviderConfigurator extends ProviderService
{
	IStatus importProductGroups(IProgressMonitor monitor);

	IStatus synchronizeProductGroups(IProgressMonitor monitor);

	IStatus setTaxCodes(IProgressMonitor monitor);
	
	boolean isConnect();

	boolean canMap(CurrentTax currentTax);

	boolean canMap(Tax tax);

	String getImageName();
}
