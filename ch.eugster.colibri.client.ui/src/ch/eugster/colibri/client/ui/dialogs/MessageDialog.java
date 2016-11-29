/*
 * Created on 23.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.buttons.DialogButton;
import ch.eugster.colibri.persistence.model.Profile;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MessageDialog extends JDialog
{
	private static final long serialVersionUID = 0l;

	private int returnValue = -1;

	private JLabel imageLabel;

	private JLabel messageLabel;

	private JButton[] buttons;

	public static final int BUTTON_OK = 0;

	public static final int BUTTON_CANCEL = 1;

	public static final int BUTTON_YES = 2;

	public static final int BUTTON_NO = 3;

	private static final String BUTTON_CMD_OK = "ok";

	private static final String BUTTON_CMD_CANCEL = "cancel";

	private static final String BUTTON_CMD_YES = "yes";

	private static final String BUTTON_CMD_NO = "no";

	public static final int TYPE_INFORMATION = 0;

	public static final int TYPE_QUESTION = 1;

	public static final int TYPE_WARN = 2;

	public static final int TYPE_ERROR = 3;

	private static final String ICON_INFORMATION = "metal-inform.gif";

	private static final String ICON_QUESTION = "metal-question.gif";

	private static final String ICON_WARN = "metal-warn.gif";

	private static final String ICON_ERROR = "metal-error.gif";

	private static final int MAX_MESSAGE_LENGTH = 55;

	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(final Frame owner, final Profile profile, final String title, final int[] buttons, final int defaultButton, boolean isFailOver)
			throws HeadlessException
	{
		super(owner, title, true);
		init(profile, buttons, defaultButton, isFailOver);
	}

	public void setImage(final String path)
	{
		final Icon icon = new ImageIcon(path);
		imageLabel.setIcon(icon);
	}

	public void setMessage(final String message)
	{
		messageLabel.setText(message);
	}

	private DialogButton createButton(final Profile profile, final int type, boolean isFailOver)
	{
		String text = "";
		String cmd = "";
		switch (type)
		{
			case BUTTON_OK:
			{
				text = "OK";
				cmd = MessageDialog.BUTTON_CMD_OK;
				break;
			}
			case BUTTON_CANCEL:
			{
				text = "Abbrechen";
				cmd = MessageDialog.BUTTON_CMD_CANCEL;
				break;
			}
			case BUTTON_YES:
			{
				text = "Ja";
				cmd = MessageDialog.BUTTON_CMD_YES;
				break;
			}
			case BUTTON_NO:
			{
				text = "Nein";
				cmd = MessageDialog.BUTTON_CMD_NO;
				break;
			}
		}
		final DialogButton button = new DialogButton(text, profile, isFailOver);
		button.setActionCommand(cmd);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				if (e.getActionCommand().equals(MessageDialog.BUTTON_CMD_OK))
				{
					returnValue = MessageDialog.BUTTON_OK;
				}
				else if (e.getActionCommand().equals(MessageDialog.BUTTON_CMD_CANCEL))
				{
					returnValue = MessageDialog.BUTTON_CANCEL;
				}
				else if (e.getActionCommand().equals(MessageDialog.BUTTON_CMD_YES))
				{
					returnValue = MessageDialog.BUTTON_YES;
				}
				else if (e.getActionCommand().equals(MessageDialog.BUTTON_CMD_NO))
				{
					returnValue = MessageDialog.BUTTON_NO;
				}
				MessageDialog.this.setVisible(false);
			}
		});
		button.setMinimumSize(new Dimension(80, 80));
		button.setPreferredSize(new Dimension(80, 80));
		return button;
	}

	private void init(final Profile profile, final int[] b, final int defaultButton, boolean isFailOver)
	{
		buttons = new DialogButton[b.length];
		for (int i = 0; i < b.length; i++)
		{
			buttons[i] = createButton(profile, b[i], isFailOver);
			buttons[i].setDefaultCapable(b[i] == defaultButton);
		}

		final Container main = new Container();
		final BorderLayout layout = new BorderLayout();
		layout.setHgap(50);
		layout.setVgap(50);
		main.setLayout(layout);

		main.add(new JLabel(" "), BorderLayout.NORTH);

		imageLabel = new JLabel();
		main.add(imageLabel, BorderLayout.WEST);
		main.add(new JLabel(), BorderLayout.EAST);

		messageLabel = new JLabel();
		main.add(messageLabel, BorderLayout.CENTER);

		final Container ctrl = new Container();
		ctrl.setLayout(new BorderLayout());

		final Container btnctrl = new Container();
		btnctrl.setLayout(new GridLayout(1, buttons.length));

		for (final JButton button : buttons)
		{
			btnctrl.add(button);
		}

		ctrl.add(btnctrl, BorderLayout.CENTER);

		main.add(ctrl, BorderLayout.SOUTH);

		getContentPane().add(main);

		setResizable(false);
	}

	public static void showInformation(final Frame owner, final Profile profile, final String title, final String message, final int messageType, boolean isFailOver)
	{
		Toolkit.getDefaultToolkit().beep();

		final MessageDialog dialog = new MessageDialog(owner, profile, title, new int[] { MessageDialog.BUTTON_OK }, -1, isFailOver);
		dialog.setIconImage(MessageDialog.getImage(messageType));
		dialog.setModal(true);
		dialog.setMessage(MessageDialog.prepareMessage(message));
		dialog.pack();
		dialog.setResizable(false);
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension c = dialog.getSize();
		final Point p = new Point();
		p.x = (d.width - c.width) / 2;
		p.y = (d.height - c.height) / 2;
		dialog.setLocation(p);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	public static int showQuestion(final Frame owner, final Profile profile, final String title, final String message, final int messageType,
			final int[] buttons, final int defaultButton, boolean isFailOver)
	{
		Toolkit.getDefaultToolkit().beep();

		final MessageDialog dialog = new MessageDialog(owner, profile, title, buttons, defaultButton, isFailOver);
		dialog.setIconImage(MessageDialog.getImage(messageType));
		dialog.setModal(true);
		dialog.setMessage(MessageDialog.prepareMessage(message));
		dialog.pack();
		dialog.setResizable(false);
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension c = dialog.getSize();
		final Point p = new Point();
		p.x = (d.width - c.width) / 2;
		p.y = (d.height - c.height) / 2;
		dialog.setLocation(p);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		return dialog.returnValue;

	}

	public static int showSimpleDialog(final Frame owner, final Profile profile, final String title, final String message, final int messageType, final boolean isFailOver)
	{
		switch (messageType)
		{
			case 0:
			{
				MessageDialog.showInformation(owner, profile, title, message, messageType, isFailOver);
				return 0;
			}
			case 1:
			{
				return MessageDialog.showQuestion(owner, profile, title, message, messageType, new int[] { MessageDialog.BUTTON_YES, MessageDialog.BUTTON_NO }, 0, isFailOver);
			}
			default:
				return 0;
		}
	}

	private static java.awt.Image getImage(final int messageType)
	{
		Image swtImage = null;
		switch (messageType)
		{
			case 0:
			{
				swtImage = Activator.getDefault().getImageRegistry().get(MessageDialog.ICON_INFORMATION);
				break;
			}
			case 1:
			{
				swtImage = Activator.getDefault().getImageRegistry().get(MessageDialog.ICON_QUESTION);
				break;
			}
			case 2:
			{
				swtImage = Activator.getDefault().getImageRegistry().get(MessageDialog.ICON_WARN);
				break;
			}
			case 3:
			{
				swtImage = Activator.getDefault().getImageRegistry().get(MessageDialog.ICON_ERROR);
				break;
			}
		}
		return Activator.getDefault().convertToAWT(swtImage.getImageData());
	}

	private static String prepareMessage(final String message)
	{
		String msg = message;
		final Collection<String> messageParts = new ArrayList<String>();

		while (msg.length() > 0)
		{
			final int index = msg.indexOf(' ', MessageDialog.MAX_MESSAGE_LENGTH);
			if (index == -1)
			{
				messageParts.add(msg);
				msg = "";
			}
			else
			{
				messageParts.add(msg.substring(0, index));
				msg = msg.substring(index + 1);
			}
		}

		final String[] parts = messageParts.toArray(new String[0]);
		msg = "<html>";
		for (int i = 0; i < parts.length; i++)
		{
			msg = msg + parts[i];
			if (i < parts.length - 1)
			{
				msg = msg + "<br>";
			}
		}
		return msg;
	}

	public void centerInScreen()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point pos = new Point(screenSize.width /2 - this.getWidth() / 2, screenSize.height / 2 - this.getHeight() / 2);
		this.setLocation(pos.x, pos.y);
	}
}
