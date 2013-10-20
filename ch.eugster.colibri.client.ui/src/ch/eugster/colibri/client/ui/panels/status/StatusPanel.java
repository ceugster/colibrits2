package ch.eugster.colibri.client.ui.panels.status;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.panels.MainPanel;
import ch.eugster.colibri.persistence.model.Profile;

public class StatusPanel extends MainPanel implements LogListener 
{
	private JTextArea textArea;

	private ServiceTracker<LogReaderService, LogReaderService> tracker;
	
	private LinkedList<LogReaderService> readers = new LinkedList<LogReaderService>();
	
	public StatusPanel(Profile profile) 
	{
		super(profile);
		textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        this.setLayout(new BorderLayout());
		this.add(new JScrollPane(textArea));

	    //  We use a ServiceListener to dynamically keep track of all the LogReaderService service being
	    //  registered or unregistered
		tracker = new ServiceTracker<LogReaderService, LogReaderService>(Activator.getDefault().getBundle().getBundleContext(), LogReaderService.class, null)
		{
			@Override
			public LogReaderService addingService(ServiceReference<LogReaderService> reference) 
			{
				LogReaderService reader = super.addingService(reference);
				reader.addLogListener(StatusPanel.this);
				readers.add(reader);
				return reader;
			}

			@Override
			public void removedService(ServiceReference<LogReaderService> reference, LogReaderService reader) 
			{
				reader.removeLogListener(StatusPanel.this);
				readers.remove(reader);
				super.removedService(reference, reader);
			}
		};
		tracker.open();
	}

	@Override
	public void dispose() 
	{
		tracker.close();
	}

	@Override
	public void initFocus() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void update() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitle() 
	{
		return "Status";
	}

	@Override
	public void logged(LogEntry entry) 
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(entry.getTime());
		String time = SimpleDateFormat.getDateTimeInstance().format(calendar.getTime());
		String level = getLevelAsString(entry.getLevel());
		String message = entry.getMessage();
		String bundle = entry.getBundle().getSymbolicName();
		textArea.append(time + " " + level + " " + bundle + " " + message + "\n");
	}

	private String getLevelAsString(int level)
	{
		switch (level)
		{
		case LogService.LOG_DEBUG:
		{
			return "DEBUG";
		}
		case LogService.LOG_INFO:
		{
			return "INFO";
		}
		case LogService.LOG_WARNING:
		{
			return "WARNING";
		}
		case LogService.LOG_ERROR:
		{
			return "ERROR";
		}
		default:
		{
			return "UNKNOWN";
		}
		}
	}
}
