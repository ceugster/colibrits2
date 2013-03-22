package ch.eugster.colibri.report.settlement.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import net.sf.jasperreports.engine.JRDataSource;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.ISelectionListener;

public interface ISettlementCompositeChild extends ISelectionListener, ISelectionProvider
{
	JRDataSource createDataSource();

	Hashtable<String, Object> getParameters();

	boolean validateSelection();

	InputStream getReport() throws IOException;

	String getReportName();
}
