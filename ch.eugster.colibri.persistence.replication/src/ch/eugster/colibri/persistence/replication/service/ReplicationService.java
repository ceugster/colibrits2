package ch.eugster.colibri.persistence.replication.service;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Shell;

public interface ReplicationService
{
	boolean isLocalService();
	
	IStatus replicate(Shell shell, boolean force);
}
