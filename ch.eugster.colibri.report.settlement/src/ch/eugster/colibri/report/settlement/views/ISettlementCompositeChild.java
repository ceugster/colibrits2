package ch.eugster.colibri.report.settlement.views;

import java.util.Hashtable;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.ISelectionListener;

public interface ISettlementCompositeChild extends ISelectionListener, ISelectionProvider
{
	JRDataSource createDataSource();

	Hashtable<String, Object> getParameters();

	boolean validateSelection();

	JasperReport getReport() throws JRException;

	String getReportName();
}
