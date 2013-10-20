package ch.eugster.colibri.persistence.transfer.services;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Receipt;

public interface TransferAgent
{
	Collection<Receipt> getReceipts(int max);
	
	IStatus transfer(Collection<Receipt> receipts, IProgressMonitor monitor);
	
	IStatus transfer(Receipt receipt);

	IStatus transfer(IProgressMonitor monitor, int count);
}
