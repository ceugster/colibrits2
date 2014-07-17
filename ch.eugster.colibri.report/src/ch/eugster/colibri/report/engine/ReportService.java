package ch.eugster.colibri.report.engine;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import org.eclipse.core.runtime.IProgressMonitor;

public interface ReportService
{
	void export(final InputStream report, final JRDataSource dataSource, final Map<String, Object> parameters,
			Format format, File file) throws IllegalArgumentException;

	void export(final InputStream report, final Comparable<?>[] beanArray, final Map<String, Object> parameters,
			Format format, File file) throws IllegalArgumentException;

	void print(InputStream report, JRDataSource dataSource, Map<String, Object> parameters, boolean doNotShowPrintDialog)
			throws IllegalArgumentException;

	void print(InputStream report, Comparable<?>[] beanArray, Map<String, Object> parameters, boolean doNotShowPrintDialog)
			throws IllegalArgumentException;

	void view(IProgressMonitor monitor, final InputStream report, final JRDataSource dataSource, final Map<String, Object> parameters)
			throws IllegalArgumentException;

	void view(IProgressMonitor monitor, final InputStream report, final Comparable<?>[] beanArray, final Map<String, Object> parameters)
			throws IllegalArgumentException;

	public enum Destination
	{
		PREVIEW, PRINTER, RECEIPT_PRINTER, EXPORT;

		public String label()
		{
			switch (this)
			{
				case PREVIEW:
				{
					return "Vorschau";
				}
				case PRINTER:
				{
					return "Drucker";
				}
				case RECEIPT_PRINTER:
				{
					return "Belegdrucker";
				}
				case EXPORT:
				{
					return "Export";
				}
				default:
				{
					throw new RuntimeException("Invalid destination");
				}
			}
		}

		public String toString()
		{
			switch (this)
			{
				case PREVIEW:
				{
					return "Vorschau";
				}
				case PRINTER:
				{
					return "Drucker";
				}
				case RECEIPT_PRINTER:
				{
					return "Belegdrucker";
				}
				case EXPORT:
				{
					return "Export";
				}
				default:
				{
					throw new RuntimeException("Invalid destination");
				}
			}
		}
	}

	public enum Format
	{
		PDF, HTML, XML;

		public String extension()
		{
			switch (this)
			{
				case PDF:
				{
					return ".pdf";
				}
				case HTML:
				{
					return ".html";
				}
				case XML:
				{
					return ".xml";
				}
				default:
				{
					throw new RuntimeException("Invalid format");
				}
			}
		}

		public String label()
		{
			switch (this)
			{
				case PDF:
				{
					return "Portable Document Format (PDF)";
				}
				case HTML:
				{
					return "Hypertext Markup Language (HTML)";
				}
				case XML:
				{
					return "Extensible Markup Language (XML)";
				}
				default:
				{
					throw new RuntimeException("Invalid format");
				}
			}
		}

		public String toString()
		{
			switch (this)
			{
				case PDF:
				{
					return "Portable Document Format (PDF)";
				}
				case HTML:
				{
					return "Hypertext Markup Language (HTML)";
				}
				case XML:
				{
					return "Extensible Markup Language (XML)";
				}
				default:
				{
					throw new RuntimeException("Invalid format");
				}
			}
		}

		public static String[] extensions()
		{
			String[] extensions = new String[Format.values().length];
			for (int i = 0; i < Format.values().length; i++)
			{
				extensions[i] = "*" + Format.values()[i].extension();
			}
			return extensions;
		}

		public static Format format(final String filename)
		{
			Format[] formats = Format.values();
			for (Format format : formats)
			{
				if (filename.endsWith(format.extension()))
				{
					return format;
				}
			}
			return null;
		}
	}
}
