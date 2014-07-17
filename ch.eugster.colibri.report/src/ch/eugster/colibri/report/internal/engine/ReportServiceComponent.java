package ch.eugster.colibri.report.internal.engine;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.report.engine.ReportService;
import ch.eugster.colibri.report.internal.viewer.ViewerApp;

public class ReportServiceComponent implements ReportService
{

	@Override
	public void export(final InputStream report, final Comparable<?>[] beanArray, final Map<String, Object> parameters,
			final Format format, final File file) throws IllegalArgumentException
	{
		Arrays.sort(beanArray);
		JRDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		export(report, dataSource, parameters, format, file);
	}

	@Override
	public void print(final InputStream report, final Comparable<?>[] beanArray, final Map<String, Object> parameters,
			final boolean doNotShowPrintDialog) throws IllegalArgumentException
	{
		Arrays.sort(beanArray);
		JRDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		print(report, dataSource, parameters, doNotShowPrintDialog);
	}

	@Override
	public void view(IProgressMonitor monitor, final InputStream report, final Comparable<?>[] beanArray, final Map<String, Object> parameters)
			throws IllegalArgumentException
	{
		Arrays.sort(beanArray);
		JRDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		view(monitor, report, dataSource, parameters);
	}

	@Override
	public void view(IProgressMonitor monitor, final InputStream report, final JRDataSource dataSource,
			final Map<String, Object> parameters) throws IllegalArgumentException 
	{
		try
		{
			monitor.beginTask("", 1);
			JasperReport jasperReport = JasperCompileManager.compileReport(report);
			final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			ViewerApp app = new ViewerApp();
			app.getReportViewer().setDocument(jasperPrint);
			app.open();
			monitor.worked(1);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(e);
		}
		finally
		{
			monitor.done();
		}
	}

	@Override
	public void export(InputStream report, JRDataSource dataSource,
			Map<String, Object> parameters, Format format, File file)
			throws IllegalArgumentException {
		try
		{
			JasperReport jasperReport = JasperCompileManager.compileReport(report);
			final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			switch (format)
			{
				case PDF:
				{
					JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
					break;
				}
				case HTML:
				{
					JasperExportManager.exportReportToHtmlFile(jasperPrint, file.getAbsolutePath());
					break;
				}
				case XML:
				{
					JasperExportManager.exportReportToXmlFile(jasperPrint, file.getAbsolutePath(), true);
					break;
				}
			}
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void print(InputStream report, JRDataSource dataSource,
			Map<String, Object> parameters, boolean doNotShowPrintDialog)
			throws IllegalArgumentException {
		try
		{
			JasperReport jasperReport = JasperCompileManager.compileReport(report);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			JasperPrintManager.printReport(jasperPrint, doNotShowPrintDialog);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(e);
		}
	}
}
