package ch.eugster.colibri.client.ui.views;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class TimeDisplayContribution extends ControlContribution 
{
	protected TimeDisplayContribution(String id) 
	{
		super(id);
	}

	private Label time;

	private Timer timer;
	
	@Override
	protected Control createControl(Composite parent) 
	{
		time = new Label(parent, SWT.BORDER);
		GC gc = new GC(time);
		time.setSize(gc.textExtent("00.00.0000 00:00:00"));
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() 
			{
				time.getDisplay().syncExec(new Runnable() 
				{
					@Override
					public void run() 
					{
						time.setText(SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance().getTime()));
					}
				});
			}
		}, GregorianCalendar.getInstance().getTime());
		return time;
	}
}
