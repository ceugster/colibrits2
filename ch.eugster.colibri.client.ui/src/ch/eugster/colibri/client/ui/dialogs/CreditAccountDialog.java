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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.buttons.DialogButton;
import ch.eugster.colibri.client.ui.buttons.ProfileButton;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Profile;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CreditAccountDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private Currency currency;
	
	private String input = "";

	private double returnValue;
	
	private JLabel imageLabel;

	private JPanel messagePanel;

	private JTextField balanceField;

	private JTextField creditField;

	private ProfileButton creditButton;
	
	private ProfileButton cancelButton;

	public static final int BUTTON_CREDIT = 0;

	public static final int BUTTON_CANCEL = 1;

	private static final String BUTTON_CMD_CREDIT = "credit";

	private static final String BUTTON_CMD_CANCEL = "cancel";

	public static final int TYPE_INFORMATION = 0;

	private static final String ICON_INFORMATION = "metal-inform.gif";

	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public CreditAccountDialog(final Frame owner, final Profile profile, final String title, final Currency currency, final double balance, final double credit, boolean isFailOver)
			throws HeadlessException
	{
		super(owner, title, true);
		init(profile, currency, balance, credit, isFailOver);
	}

	public void setImage(final String path)
	{
		final Icon icon = new ImageIcon(path);
		imageLabel.setIcon(icon);
	}

//	public void setMessage(final String message)
//	{
//		messagePanel.setText(message);
//	}

	private DialogButton createButton(final Profile profile, final int type, boolean isFailOver)
	{
		String text = "";
		String cmd = "";
		switch (type)
		{
			case BUTTON_CREDIT:
			{
				text = "Aufladen";
				cmd = CreditAccountDialog.BUTTON_CMD_CREDIT;
				break;
			}
			case BUTTON_CANCEL:
			{
				text = "Abbrechen";
				cmd = CreditAccountDialog.BUTTON_CMD_CANCEL;
				break;
			}
//			case BUTTON_YES:
//			{
//				text = "Ja";
//				cmd = CreditAccountDialog.BUTTON_CMD_YES;
//				break;
//			}
//			case BUTTON_NO:
//			{
//				text = "Nein";
//				cmd = CreditAccountDialog.BUTTON_CMD_NO;
//				break;
//			}
		}
		final DialogButton button = new DialogButton(text, profile, isFailOver);
		button.setActionCommand(cmd);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				if (e.getActionCommand().equals(CreditAccountDialog.BUTTON_CMD_CREDIT))
				{
					returnValue = convert(creditField.getText());
				}
				else if (e.getActionCommand().equals(CreditAccountDialog.BUTTON_CMD_CANCEL))
				{
					returnValue = 0D;
				}
//				else if (e.getActionCommand().equals(CreditAccountDialog.BUTTON_CMD_YES))
//				{
//					returnValue = CreditAccountDialog.BUTTON_YES;
//				}
//				else if (e.getActionCommand().equals(CreditAccountDialog.BUTTON_CMD_NO))
//				{
//					returnValue = CreditAccountDialog.BUTTON_NO;
//				}
				CreditAccountDialog.this.setVisible(false);
			}
		});
		button.setMinimumSize(new Dimension(80, 80));
		button.setPreferredSize(new Dimension(80, 80));
		return button;
	}

	private void init(final Profile profile, final Currency currency, final double balance, final double credit, boolean isFailOver)
	{
		this.currency = currency;
		
		creditButton = createButton(profile, CreditAccountDialog.BUTTON_CREDIT, isFailOver);
		creditButton.setDefaultCapable(true);
		cancelButton = createButton(profile, CreditAccountDialog.BUTTON_CANCEL, isFailOver);
		cancelButton.setDefaultCapable(true);

		final Container main = new Container();
		final BorderLayout layout = new BorderLayout();
		layout.setHgap(50);
		layout.setVgap(50);
		main.setLayout(layout);

		main.add(new JLabel(" "), BorderLayout.NORTH);

		imageLabel = new JLabel();
		main.add(imageLabel, BorderLayout.WEST);
		main.add(new JLabel(), BorderLayout.EAST);

		messagePanel = createMessagePanel(profile, balance, credit);
		main.add(messagePanel, BorderLayout.CENTER);

		final Container ctrl = new Container();
		ctrl.setLayout(new BorderLayout());

		final Container btnctrl = new Container();
		btnctrl.setLayout(new GridLayout(1, 2));

		btnctrl.add(creditButton);
		btnctrl.add(cancelButton);

		ctrl.add(btnctrl, BorderLayout.CENTER);

		main.add(ctrl, BorderLayout.SOUTH);

		getContentPane().add(main);

		setResizable(false);
		
		setButtonMode();
	}

	private JPanel createMessagePanel(Profile profile, double balance, double credit) 
	{
		BorderLayout layout = new BorderLayout();
		JPanel panel = new JPanel();
		panel.setLayout(layout);
		panel.add(createDisplay(profile, balance, credit), BorderLayout.WEST);
		JPanel fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(80, 80));
		panel.add(fillPanel, BorderLayout.CENTER);
		panel.add(createKeyboard(profile), BorderLayout.EAST);
		return panel;
	}
	
	private JPanel createDisplay(Profile profile, double balance, double credit)
	{
		JPanel balancePanel = new JPanel();
		balancePanel.setLayout(new BorderLayout());
		balancePanel.add(createLabel(profile, "Aktueller Stand"), BorderLayout.WEST);
		JPanel fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(10, 10));
		balancePanel.add(fillPanel, BorderLayout.CENTER);
		balanceField = createTextField(profile, balance);
		balancePanel.add(balanceField, BorderLayout.EAST);

		JPanel creditPanel = new JPanel();
		creditPanel.setLayout(new BorderLayout());
		creditPanel.add(createLabel(profile, "Aufladebetrag"), BorderLayout.WEST);
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(10, 10));
		creditPanel.add(fillPanel, BorderLayout.CENTER);
		creditField = createTextField(profile, credit);
		creditPanel.add(creditField, BorderLayout.EAST);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(balancePanel, BorderLayout.NORTH);
		panel.add(creditPanel, BorderLayout.SOUTH);
		return panel;
	}
	
	private JTextField createTextField(Profile profile, double value)
	{
		NumberFormat formatter = DecimalFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
		formatter.setMinimumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
		JTextField textField = new JTextField(formatter.format(value));
		textField.setEditable(false);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		textField.setFont(textField.getFont().deriveFont(profile.getInputNameLabelFontStyle(), profile.getInputNameLabelFontSize()));
		textField.setForeground(new  java.awt.Color(profile.getInputNameLabelFg()));
		textField.setBackground(new java.awt.Color(profile.getInputNameLabelBg()));
		textField.setPreferredSize(new Dimension(200, textField.getFont().getSize() + 10));
		return textField;
	}

	private JLabel createLabel(Profile profile, String text)
	{
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(JTextField.RIGHT);
		label.setFont(label.getFont().deriveFont(profile.getInputNameLabelFontStyle(), profile.getInputNameLabelFontSize()));
		label.setOpaque(true);
		label.setForeground(new java.awt.Color(profile.getInputNameLabelFg()));
		label.setBackground(new java.awt.Color(profile.getInputNameLabelBg()));
		label.setBorder(new EmptyBorder(2, 2, 2, 2));
		return label;
	}

	private ProfileButton createButton(Profile profile, String actionCommand, String label)
	{
		ProfileButton button = new ProfileButton(profile);
		button.setText(label);
		button.addActionListener(this);
		button.setActionCommand(actionCommand);
		button.setPreferredSize(new Dimension(80, 80));
		button.setForeground(new java.awt.Color(profile.getButtonNormalFg()));
		button.setBackground(new java.awt.Color(profile.getButtonNormalBg()));
		button.setFont(button.getFont().deriveFont(profile.getButtonNormalFontStyle(), profile.getButtonNormalFontSize()));
		return button;
	}
	
	private JPanel createKeyboard(Profile profile)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 3));
		panel.add(createButton(profile, "1", "1"));
		panel.add(createButton(profile, "2", "2"));
		panel.add(createButton(profile, "3", "3"));
		panel.add(createButton(profile, "4", "4"));
		panel.add(createButton(profile, "5", "5"));
		panel.add(createButton(profile, "6", "6"));
		panel.add(createButton(profile, "7", "7"));
		panel.add(createButton(profile, "8", "8"));
		panel.add(createButton(profile, "9", "9"));
		panel.add(createButton(profile, "0", "0"));
		panel.add(createButton(profile, ".", "."));
		panel.add(createButton(profile, "clear", "C"));
		return panel;
	}
	
	public static double showInformation(final Frame owner, final Profile profile, final String title, final Currency currency, final double balance, final double credit, final int messageType, boolean isFailOver)
	{
		Toolkit.getDefaultToolkit().beep();

		final CreditAccountDialog dialog = new CreditAccountDialog(owner, profile, title, currency, balance, credit, isFailOver);
		dialog.setIconImage(CreditAccountDialog.getImage(messageType));
		dialog.setModal(true);
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

//	public static int showQuestion(final Frame owner, final Profile profile, final String title, final String message, final int messageType,
//			final int[] buttons, final int defaultButton)
//	{
//		Toolkit.getDefaultToolkit().beep();
//
//		final CreditAccountDialog dialog = new CreditAccountDialog(owner, profile, title, buttons, defaultButton);
//		dialog.setIconImage(CreditAccountDialog.getImage(messageType));
//		dialog.setModal(true);
////		dialog.setMessage(CreditAccountDialog.prepareMessage(message));
//		dialog.pack();
//		dialog.setResizable(false);
//		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//		final Dimension c = dialog.getSize();
//		final Point p = new Point();
//		p.x = (d.width - c.width) / 2;
//		p.y = (d.height - c.height) / 2;
//		dialog.setLocation(p);
//		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//		dialog.setVisible(true);
//		return dialog.returnValue;
//
//	}

//	public static int showSimpleDialog(final Frame owner, final Profile profile, final String title, final String message, final int messageType)
//	{
//		switch (messageType)
//		{
//			case 0:
//			{
//				CreditAccountDialog.showInformation(owner, profile, title, message, messageType);
//				return 0;
//			}
////			case 1:
////			{
////				return CreditAccountDialog.showQuestion(owner, profile, title, message, messageType, new int[] { CreditAccountDialog.BUTTON_YES,
////						CreditAccountDialog.BUTTON_NO }, 0);
////			}
//			default:
//				return 0;
//		}
//	}

	private static java.awt.Image getImage(final int messageType)
	{
		Image swtImage = null;
		switch (messageType)
		{
			case 0:
			{
				swtImage = Activator.getDefault().getImageRegistry().get(CreditAccountDialog.ICON_INFORMATION);
				break;
			}
//			case 1:
//			{
//				swtImage = Activator.getDefault().getImageRegistry().get(CreditAccountDialog.ICON_QUESTION);
//				break;
//			}
//			case 2:
//			{
//				swtImage = Activator.getDefault().getImageRegistry().get(CreditAccountDialog.ICON_WARN);
//				break;
//			}
//			case 3:
//			{
//				swtImage = Activator.getDefault().getImageRegistry().get(CreditAccountDialog.ICON_ERROR);
//				break;
//			}
		}
		return Activator.getDefault().convertToAWT(swtImage.getImageData());
	}

//	private static String prepareMessage(final String message)
//	{
//		String msg = message;
//		final Collection<String> messageParts = new ArrayList<String>();
//
//		while (msg.length() > 0)
//		{
//			final int index = msg.indexOf(' ', CreditAccountDialog.MAX_MESSAGE_LENGTH);
//			if (index == -1)
//			{
//				messageParts.add(msg);
//				msg = "";
//			}
//			else
//			{
//				messageParts.add(msg.substring(0, index));
//				msg = msg.substring(index + 1);
//			}
//		}
//
//		final String[] parts = messageParts.toArray(new String[0]);
//		msg = "<html>";
//		for (int i = 0; i < parts.length; i++)
//		{
//			msg = msg + parts[i];
//			if (i < parts.length - 1)
//			{
//				msg = msg + "<br>";
//			}
//		}
//		return msg;
//	}

	public void centerInScreen()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point pos = new Point(screenSize.width /2 - this.getWidth() / 2, screenSize.height / 2 - this.getHeight() / 2);
		this.setLocation(pos.x, pos.y);
	}
	
	private double convert(String text)
	{
		double value = 0D;
		try
		{
			value = Double.valueOf(creditField.getText()).doubleValue();
		}
		catch(NumberFormatException e)
		{
			value = 0D;
		}
		return value;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent)
	{
		String command = actionEvent.getActionCommand();
		if (command.equals("clear"))
		{
			input = "";
			returnValue = 0D;
		}
		else
		{
			if (!command.equals(".") || !input.contains(command))
			{
				input = input + command;
				returnValue = convertToNumber(input);
			}
		}
		this.creditField.setText(formatNumber(returnValue));
		setButtonMode();
	}

	private String formatNumber(double value)
	{
		NumberFormat formatter = DecimalFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
		formatter.setMinimumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
		return formatter.format(value);
	}
	
	private double convertToNumber(String stringValue)
	{
		double doubleValue = 0D;
		try
		{
			doubleValue = Double.valueOf(stringValue).doubleValue();
		}
		catch(NumberFormatException e)
		{
		}
		return doubleValue;
	}
	
	private void setButtonMode()
	{
		if (returnValue == 0D)
		{
			this.getRootPane().setDefaultButton(cancelButton);
			creditButton.setEnabled(false);
		}
		else
		{
			creditButton.setEnabled(true);
			this.getRootPane().setDefaultButton(creditButton);
		}
	}
}
