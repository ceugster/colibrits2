package ch.eugster.colibri.persistence.connection.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.model.RoleProperty;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.queries.UserQuery;

public class LoginDialog extends TitleAreaDialog implements EventHandler
{
	public static final String TOPIC_LOGIN = "ch/eugster/colibri/admin/login";

	public static final String TOPIC_CANCEL = "ch/eugster/colibri/admin/cancel";

	private Text username;

	private Text password;

	private static final String TITLE = "Anmelden";

	private static final String MESSAGE = "Bitte authentifizieren Sie sich mit Benutzernamen und Passwort.";

	private static final String USERNAME = "Benutzername";

	private static final String PASSWORD = "Passwort";

	private ServiceRegistration<EventHandler> eventHandlerRegistration;

	private ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker;

	private UserQuery query;

	public LoginDialog(final Shell shell, UserQuery query)
	{
		super(shell);
		this.query = query;
		final Collection<String> t = new ArrayList<String>();
		t.add(TOPIC_LOGIN);
		t.add(TOPIC_CANCEL);
		final String[] topics = t.toArray(new String[t.size()]);

		this.eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventAdminTracker.open();

		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, this, properties);
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent)
	{
		this.createButton(parent, IDialogConstants.YES_ID, "Anmelden", true).addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(final SelectionEvent e)
					{
						this.widgetSelected(e);
					}

					@Override
					public void widgetSelected(final SelectionEvent e)
					{
						login();
					}
				});
		this.createButton(parent, IDialogConstants.CLOSE_ID, "Beenden", false).addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(final SelectionEvent e)
					{
						this.widgetSelected(e);
					}

					@Override
					public void widgetSelected(final SelectionEvent e)
					{
						shutdown();
					}
				});

		this.createButton(parent, IDialogConstants.CANCEL_ID, "Abbrechen", false).addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(final SelectionEvent e)
					{
						this.widgetSelected(e);
					}

					@Override
					public void widgetSelected(final SelectionEvent e)
					{
						cancel();
					}
				});
	}

	private void shutdown()
	{
		if (PlatformUI.isWorkbenchRunning())
		{
			PlatformUI.getWorkbench().close();
		}
		else
		{
			System.exit(0);
		}
	}

	private void login()
	{
		User.setLoginUser(User.newInstance());
		final User user = query.findByUsernameAndPassword(this.username.getText(), this.password.getText());
		if (user == null)
		{
			User.setLoginUser(null);
			this.setErrorMessage("Benutzername oder Passwort inkorrekt.");
		}
		else
		{
			boolean valid = false;
			String application = System.getProperty("eclipse.application");
			if (application.contains("admin"))
			{
				if (user.getRole().getId().equals(Long.valueOf(1L)))
				{
					valid = true;
				}
				else
				{
					RoleProperty property = user.getRole().getRoleProperty("login.admin");
					valid = property != null && property.getValue().equals("true");
				}
			}
			else if (application.contains("report"))
			{
				RoleProperty role = user.getRole().getRoleProperty("login.report");
				valid = role != null && role.getValue().equals("true");
			}
			if (valid)
			{
				User.setLoginUser(user);
				close();
			}
			else
			{
				this.setErrorMessage("Ihnen fehlen die notwendigen Berechtigungen.");
				User.setLoginUser(null);
			}
		}
	}

	private void cancel()
	{
		if (User.getLoginUser() == null)
		{
			close();
			shutdown();
		}
		else
		{
			close();
		}
	}

//	private Event getEvent(String topic, final String username, String password)
//	{
//		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
//		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
//		properties.put(EventConstants.BUNDLE_ID,
//				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
//		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
//		properties.put(EventConstants.SERVICE, this.eventAdminTracker.getServiceReference());
//		properties.put(EventConstants.SERVICE_ID, this.eventAdminTracker.getServiceReference()
//				.getProperty("service.id"));
//		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
//		properties.put("property.key", "login.admin");
//		properties.put("username", username);
//		properties.put("password", password);
//		properties.put("message", "Sie haben keine Administratorenrechte.");
//		properties.put("status", Status.OK_STATUS);
//		return new Event(topic, properties);
//	}

//	private void sendEvent(String topic, final String username, String password)
//	{
//		final EventAdmin eventAdmin = (EventAdmin) this.eventAdminTracker.getService();
//		if (eventAdmin != null)
//		{
//			eventAdmin.sendEvent(this.getEvent(topic, username, password));
//		}
//	}

	public void dispose()
	{
		this.eventHandlerRegistration.unregister();
		this.eventAdminTracker.close();
	}

	@Override
	protected Control createDialogArea(final Composite parent)
	{
		this.setBlockOnOpen(true);

		this.setTitle(LoginDialog.TITLE);
		this.setTitleImage(Activator.getDefault().getImageRegistry().get("login"));
		this.setMessage(LoginDialog.MESSAGE);

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText(LoginDialog.USERNAME);
		label.setLayoutData(new GridData());

		this.username = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.username.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(composite, SWT.NONE);
		label.setText(LoginDialog.PASSWORD);
		label.setLayoutData(new GridData());

		this.password = new Text(composite, SWT.BORDER | SWT.SINGLE);
		this.password.setEchoChar('*');
		this.password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return composite;
	}

	@Override
	public void handleEvent(Event event)
	{
		String topic = event.getTopic();
		if (topic.equals(LoginDialog.TOPIC_LOGIN))
		{
			IStatus status = (IStatus) event.getProperty("status");
			if (status.isOK())
			{
				this.close();
			}
			else
			{
				this.setErrorMessage(status.getMessage());
			}
		}
		else if (topic.equals(LoginDialog.TOPIC_CANCEL))
		{
			Boolean user = (Boolean) event.getProperty("has.current.user");
			if (user == null || user.booleanValue() == false)
			{
				this.shutdown();
			}
			else
			{
				this.close();
			}
		}
	}
}
