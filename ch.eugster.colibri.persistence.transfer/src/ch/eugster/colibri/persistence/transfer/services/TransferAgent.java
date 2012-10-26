package ch.eugster.colibri.persistence.transfer.services;

import org.eclipse.core.runtime.IStatus;

import ch.eugster.colibri.persistence.model.Receipt;

public interface TransferAgent
{
	IStatus transfer(int maxResult);
	
	IStatus transfer(Receipt receipt);
}
