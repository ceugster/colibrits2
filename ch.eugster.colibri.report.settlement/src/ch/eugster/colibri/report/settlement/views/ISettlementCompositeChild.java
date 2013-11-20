package ch.eugster.colibri.report.settlement.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import net.sf.jasperreports.engine.JRDataSource;

import org.eclipse.jface.viewers.ISelectionProvider;

public interface ISettlementCompositeChild extends ISelectionProvider
{
	/**
	 * 
	 * @return JRDataSource or <code>null</code> if no records are found
	 */
	JRDataSource createDataSource();

	Hashtable<String, Object> getParameters();

	boolean validateSelection();

	InputStream getReport() throws IOException;

	String getReportName();
	
	public void setInput();
}
