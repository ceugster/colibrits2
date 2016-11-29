/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eugster.colibri.client.ui.panels.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.buttons.ProfileButton;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.panels.MainPanel;
import ch.eugster.colibri.client.ui.panels.TitleProvider;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.queries.UserQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.actions.ProfileAction;

/**
 * 
 * @author ceugster
 */
public class LoginPanel extends MainPanel implements ActionListener, TitleProvider
{
	public static final long serialVersionUID = 0l;

	Collection<LoginButton> buttons = new ArrayList<LoginButton>();

	private LoginButton clear;

	private LoginButton enter;

	private LoginButton exit;

	private JLabel label;

	private JPasswordField display = new JPasswordField();

	private JLabel imageArea;

	private JLabel runningMessage;

	private JPanel displayArea;

	private Icon runningIcon;

	private Icon notRunningIcon;

	private final Collection<ILoginListener> loginListeners = new ArrayList<ILoginListener>();

	private final Collection<IWorkingListener> workingListeners = new ArrayList<IWorkingListener>();

	public static final String EMPTY_STRING = "";

	public static final String LABEL_TEXT = "Anmeldung"; //$NON-NLS-1$

	public static final String TEXT_EXIT = "Beenden"; //$NON-NLS-1$

	public static final String TEXT_CLEAR = "Leeren"; //$NON-NLS-1$

	public static final String TEXT_ENTER = "Anmelden"; //$NON-NLS-1$

	public static final String ACTION_COMMAND_EXIT = "exit"; //$NON-NLS-1$

	public static final String ACTION_COMMAND_CLEAR = "clear"; //$NON-NLS-1$

	public static final String ACTION_COMMAND_ENTER = "enter"; //$NON-NLS-1$

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private boolean isFailOver;
	
	public LoginPanel(final Profile profile, boolean isFailOver)
	{
		super(profile);

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(), LogService.class, null);
		this.logServiceTracker.open();

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.init(isFailOver);
	}

	public void actionPerformed(final ActionEvent e)
	{
		final LogService log = (LogService) this.logServiceTracker.getService();

		if (e.getSource() instanceof LoginButton)
		{
			if (e.getActionCommand().equals(LoginPanel.ACTION_COMMAND_ENTER))
			{
				if (log != null)
				{
					log.log(LogService.LOG_INFO, "Neue Benutzeranmeldung...");
				}

				this.runningMessage.setIcon(this.runningIcon);

				LoginPanel.this.fireWorkingStarted();

				try
				{
					final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
					if (persistenceService != null)
					{
						persistenceService.getCacheService().clearCache();
						final UserQuery query = (UserQuery) persistenceService.getCacheService().getQuery(User.class);
						User user = query.findByPosLogin(new Integer(String.valueOf(this.display.getPassword())));
						if (user == null)
						{
							MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.profile, "Login ungültig",
									"Es konnte kein Benutzer mit dem eingegebenen Login gefunden werden.", MessageDialog.TYPE_INFORMATION, isFailOver);
							if (log != null)
							{
								log.log(LogService.LOG_INFO, "Anmeldung fehlgeschlagen: POS Login " + new String(this.display.getPassword()) + " ungültig.");
							}
						}
						else
						{
							final LoginEvent event = new LoginEvent(user);
							LoginPanel.this.fireLoginEvent(event);
							if (log != null)
							{
								log.log(LogService.LOG_INFO, "Benutzer \"" + user.getUsername() + "\" erfolgreich angemeldet.");
							}
						}
					}
				}
				catch (final NumberFormatException nfe)
				{

				}
				finally
				{
					this.runningMessage.setIcon(this.notRunningIcon);
					LoginPanel.this.initPosLogin();
					LoginPanel.this.fireWorkingEnded();
				}
			}
			else if (e.getActionCommand().equals(LoginPanel.ACTION_COMMAND_CLEAR))
			{
				this.initPosLogin();
			}
			else if (e.getActionCommand().equals(LoginPanel.ACTION_COMMAND_EXIT))
			{
				if (log != null)
				{
					log.log(LogService.LOG_INFO, "Anwendung wird durch Benutzer beendet...");
				}
				final UIJob uiJob = new UIJob("close window")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().close();
						return Status.OK_STATUS;
					}
				};
				uiJob.schedule();
			}
			else
			{
				this.display(e.getActionCommand());
			}
		}
	}

	public boolean addLoginListener(final ILoginListener listener)
	{
		return this.loginListeners.add(listener);
	}

	public void display(final char c)
	{
		this.display(String.valueOf(c));
	}

	public void display(final String text)
	{
		this.display.setText(String.valueOf(this.display.getPassword()).concat(text));
	}

	public void dispose()
	{
		this.workingListeners.clear();
		this.persistenceServiceTracker.close();
		this.logServiceTracker.close();
	}

	public void fireLoginEvent(final LoginEvent e)
	{
		for (final ILoginListener loginListener : this.loginListeners)
		{
			loginListener.login(e);
		}
	}

	public String getLogoutQuestion()
	{
		return null;
	}

	public Long getPosLogin()
	{
		Long posLogin = null;
		try
		{
			posLogin = new Long(this.readPosLogin());
		}
		catch (final NumberFormatException e)
		{
		}
		return posLogin;
	}

	public String getShutdownQuestion()
	{
		return null;
	}

	@Override
	public String getTitle()
	{
		return "Login";
	}

	@Override
	public void initFocus()
	{
		this.enter.requestFocus();
	}

	public void keyPressed(final KeyEvent e)
	{
		if ("0123456789".indexOf(String.valueOf(e.getKeyChar())) < 0)
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				this.enter.doClick();
			}
			else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			{
				this.clear.doClick();
			}
			else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.exit.doClick();
			}
		}
		else
		{
			this.display(e.getKeyChar());
		}
	}

	public String movePosLogin()
	{
		final String s = this.readPosLogin();
		this.initPosLogin();
		return s;
	}

	public String readPosLogin()
	{
		return this.display.getPassword().toString();
	}

	public boolean readyToShutdown()
	{
		return true;
	}

	public boolean removeLoginListener(final ILoginListener listener)
	{
		return this.loginListeners.remove(listener);
	}

	@Override
	protected void update()
	{
		this.label.setForeground(new java.awt.Color(this.profile.getNameLabelFg()));
		this.label.setFont(this.label.getFont().deriveFont(this.profile.getNameLabelFontStyle(), this.profile.getNameLabelFontSize()));

		this.display.setForeground(new java.awt.Color(this.profile.getValueLabelFg()));
		this.display.setBackground(new java.awt.Color(this.profile.getValueLabelBg()));
		this.display.setFont(this.display.getFont().deriveFont(this.profile.getValueLabelFontStyle(), this.profile.getValueLabelFontSize()));

		// for (final LoginButton button : buttons)
		// {
		// this.update(button);
		// }
		//
		// this.update(clear);
		// this.update(enter);
		// this.update(exit);
	}

	// protected void update(final LoginButton button)
	// {
	// if (FailOverManager.getInstance().isOk())
	// {
	// button.setFont(button.getFont().deriveFont(profile.getButtonNormalFontStyle(),
	// profile.getButtonNormalFontSize()));
	// button.setForeground(new Color(profile.getButtonNormalFg()));
	// button.setBackground(new Color(profile.getButtonNormalBg()));
	// button.setHorizontalAlignment(profile.getButtonNormalHorizontalAlign());
	// button.setVerticalAlignment(profile.getButtonNormalVerticalAlign());
	// }
	// else
	// {
	// button.setFont(button.getFont().deriveFont(profile.getButtonFailOverFontStyle(),
	// profile.getButtonFailOverFontSize()));
	// button.setForeground(new Color(profile.getButtonFailOverFg()));
	// button.setBackground(new Color(profile.getButtonFailOverBg()));
	// button.setHorizontalAlignment(profile.getButtonFailOverHorizontalAlign());
	// button.setVerticalAlignment(profile.getButtonFailOverVerticalAlign());
	// }
	// }

	private void addWorkingListener(final IWorkingListener listener)
	{
		if (listener != null)
		{
			if (!this.workingListeners.contains(listener))
			{
				this.workingListeners.add(listener);
			}
		}
	}

	private LoginButton createButton(final String text, final Profile profile, boolean isFailOver)
	{
		return this.createButton(text, text, profile, isFailOver);
	}

	private LoginButton createButton(final String text, final String actionCommand, final Profile profile, boolean isFailOver)
	{
		final LoginButton button = new LoginButton(new LoginAction(text, actionCommand, profile), text, profile, isFailOver);
		button.setActionCommand(actionCommand);
		button.addActionListener(this);
		button.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(final KeyEvent e)
			{
				LoginPanel.this.keyPressed(e);
			}
		});
		this.addWorkingListener(button);
		return button;
	}

	private void fireWorkingEnded()
	{
		for (final IWorkingListener listener : this.workingListeners)
		{
			listener.workingEnded();
		}
	}

	private void fireWorkingStarted()
	{
		for (final IWorkingListener listener : this.workingListeners)
		{
			listener.workingStarted();
		}
	}

	private void init(boolean isFailOver)
	{
		this.isFailOver = isFailOver;
		
		this.setBackground(Color.WHITE);
		/*
		 * Display Area
		 */
		this.label = new JLabel(LoginPanel.LABEL_TEXT);
		this.label.setOpaque(true);
		this.label.setBorder(new EmptyBorder(2, 2, 2, 2));
		this.label.setBackground(java.awt.Color.WHITE);
		/*
		 * Das Display
		 */
		this.display = new JPasswordField();
		this.display.setEnabled(false);
		this.display.setText("");
		this.display.setBorder(BorderUIResource.getEtchedBorderUIResource());

		Image swtImage = Activator.getDefault().getImageRegistry().get("wait.gif");
		BufferedImage awtImage = Activator.getDefault().convertToAWT(swtImage.getImageData());
		this.runningIcon = new ImageIcon(awtImage);

		swtImage = Activator.getDefault().getImageRegistry().get("nowait.gif");
		awtImage = Activator.getDefault().convertToAWT(swtImage.getImageData());
		this.notRunningIcon = new ImageIcon(awtImage);

		this.runningMessage = new JLabel();
		this.runningMessage.setPreferredSize(new Dimension(32, 32));
		this.runningMessage.setOpaque(false);
		this.runningMessage.setIcon(this.notRunningIcon);

		this.displayArea = new JPanel();
		this.displayArea.setLayout(new BorderLayout());
		this.displayArea.add(this.label, BorderLayout.WEST);
		this.displayArea.add(this.display, BorderLayout.CENTER);
		this.displayArea.add(this.runningMessage, BorderLayout.EAST);

		final JPanel cifferArea = new JPanel();
		cifferArea.setLayout(new GridLayout(4, 3));

		final String[] labels = new String[] { "7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "00" };
		for (final String label : labels)
		{
			final LoginButton button = this.createButton(label, this.profile, isFailOver);
			cifferArea.add(button);
		}

		this.clear = this.createButton(LoginPanel.TEXT_CLEAR, LoginPanel.ACTION_COMMAND_CLEAR, this.profile, isFailOver);
		cifferArea.add(this.clear);

		swtImage = Activator.getDefault().getImageRegistry().get("login.png");
		awtImage = Activator.getDefault().convertToAWT(swtImage.getImageData());
		final Icon icon = new ImageIcon(awtImage);

		this.imageArea = new JLabel();
		this.imageArea.setIcon(icon);

		final JPanel inputArea = new JPanel();
		inputArea.setLayout(new GridLayout(1, 2));
		inputArea.add(cifferArea);
		inputArea.add(this.imageArea);

		final JPanel confirmArea = new JPanel();
		confirmArea.setMinimumSize(new Dimension(200, 80));
		confirmArea.setPreferredSize(new Dimension(2 * 276, 80));
		confirmArea.setLayout(new GridLayout(1, 2));
		this.enter = this.createButton(LoginPanel.TEXT_ENTER, LoginPanel.ACTION_COMMAND_ENTER, this.profile, isFailOver);
		confirmArea.add(this.enter);
		this.exit = this.createButton(LoginPanel.TEXT_EXIT, LoginPanel.ACTION_COMMAND_EXIT, this.profile, isFailOver);
		confirmArea.add(this.exit);

		final JPanel visibleArea = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		visibleArea.add(this.displayArea, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 4;
		gbc.gridwidth = 1;
		visibleArea.add(inputArea, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		visibleArea.add(confirmArea, gbc);
		visibleArea.setBackground(Color.WHITE);

		this.setLayout(new BorderLayout());
		this.add(visibleArea, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.add(panel, BorderLayout.NORTH);

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.add(panel, BorderLayout.SOUTH);

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.add(panel, BorderLayout.WEST);

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.add(panel, BorderLayout.EAST);

		this.update();
	}

	private void initPosLogin()
	{
		this.display.setText(LoginPanel.EMPTY_STRING);
	}

	private interface IWorkingListener
	{
		public void workingEnded();

		public void workingStarted();
	}

	private class LoginAction extends ProfileAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LoginAction(final String text, final String actionCommand, final Profile profile)
		{
			super(text, actionCommand, profile);
		}
	}

	private class LoginButton extends ProfileButton implements IWorkingListener
	{
		public static final long serialVersionUID = 0l;

		public LoginButton(final LoginAction action, final String text, final Profile profile, boolean isFailOver)
		{
			super(profile, isFailOver);
			this.setText(text);
		}

		public void workingEnded()
		{
			this.setEnabled(true);
		}

		public void workingStarted()
		{
			this.setEnabled(false);
		}
	}
}
