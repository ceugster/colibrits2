/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eugster.colibri.client.ui.panels.user.pos.numeric;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;

import ch.eugster.colibri.client.ui.actions.CifferAction;
import ch.eugster.colibri.client.ui.actions.ClearAction;
import ch.eugster.colibri.client.ui.actions.DeleteAction;
import ch.eugster.colibri.client.ui.actions.EnterAction;
import ch.eugster.colibri.client.ui.actions.NumericPadAction;
import ch.eugster.colibri.client.ui.buttons.EnterButton;
import ch.eugster.colibri.client.ui.buttons.NumericPadButton;
import ch.eugster.colibri.client.ui.events.PositionChangeMediator;
import ch.eugster.colibri.client.ui.panels.user.PositionWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Profile;

/**
 * 
 * @author ceugster
 */
public class NumericPadPanel extends JPanel implements PropertyChangeListener
{
	public static final long serialVersionUID = 0l;

	public static final String ID = "ch.eugster.colibri.ui.user.swing.number.pad.panel";

	private static final String[] positionProperties = new String[] 
			{ PositionWrapper.KEY_POSITION, PositionWrapper.KEY_PROPERTY_SEARCH_VALUE }; 

	private final Collection<ActionListener> actionListeners = new ArrayList<ActionListener>();

	private NumericPadButton clear;

	private NumericPadButton delete;

	private NumericPadButton enter;

	private JLabel displayLabel;
	
	private final ValueDisplay display;

	private final UserPanel userPanel;

	public static final String PROPERTY_VALUE_KEY = "value";

	public static final String TEXT_1 = "1";

	public static final String TEXT_2 = "2";

	public static final String TEXT_3 = "3";

	public static final String TEXT_4 = "4";

	public static final String TEXT_5 = "5";

	public static final String TEXT_6 = "6";

	public static final String TEXT_7 = "7";

	public static final String TEXT_8 = "8";

	public static final String TEXT_9 = "9";

	public static final String TEXT_0 = "0";

	public static final String TEXT_00 = "00";

	public static final String TEXT_DOT = ".";

	private PositionChangeMediator positionChangeMediator;

	public NumericPadPanel(final UserPanel userPanel, final ValueDisplay display, final Profile profile)
	{
		this.userPanel = userPanel;
		this.display = display;
		this.init(profile);
	}

	public void addActionListener(final ActionListener listener)
	{
		if (listener != null)
		{
			if (!this.actionListeners.contains(listener))
			{
				this.actionListeners.add(listener);
			}
		}
	}

	public NumericPadButton getEnterButton()
	{
		return this.enter;
	}

	public void removeActionListener(final ActionListener listener)
	{
		if (listener != null)
		{
			if (this.actionListeners.contains(listener))
			{
				this.actionListeners.remove(listener);
			}
		}
	}

	private void configureDisplay(final Profile profile, final int textAlign, final String text)
	{
		this.display.setFont(getFont().deriveFont(userPanel.getProfile().getInputNameLabelFontStyle(), userPanel.getProfile().getInputNameLabelFontSize()));
		this.display.setOpaque(true);
		this.display.setForeground(new java.awt.Color(userPanel.getProfile().getInputNameLabelFg()));
		this.display.setBackground(new java.awt.Color(userPanel.getProfile().getInputNameLabelBg()));
//		this.display.setBorder(new EmptyBorder(2, 2, 2, 2));
		this.display.setBorder(BorderUIResource.getEtchedBorderUIResource());
		this.display.setHorizontalAlignment(textAlign);
	}

	private NumericPadButton createCifferButton(final String actionCommand, final Profile profile)
	{
		final CifferAction action = new CifferAction(actionCommand, this.userPanel);
		final NumericPadButton button = new NumericPadButton(action, this.userPanel, profile);
		button.setPreferredSize(new Dimension(100, 80));
		return button;
	}

	private NumericPadButton createEnterButton(final Profile profile)
	{
		final EnterAction action = new EnterAction(this.userPanel, profile);
		final EnterButton button = new EnterButton(action, this.userPanel, profile);
		button.setPreferredSize(new Dimension(100, 160));
		this.display.addPropertyChangeListener(NumericPadPanel.PROPERTY_VALUE_KEY, button);
		action.addPropertyChangeListener(button);
		return button;
	}

	private void init(final Profile profile)
	{
		this.setLayout(new BorderLayout());
		/*
		 * Display Area
		 */
		displayLabel = new JLabel("WG / Barcode");
		displayLabel.setFont(displayLabel.getFont().deriveFont(profile.getInputNameLabelFontStyle(), profile.getInputNameLabelFontSize()));
		displayLabel.setOpaque(true);
		displayLabel.setForeground(new java.awt.Color(profile.getInputNameLabelFg()));
		displayLabel.setBackground(new java.awt.Color(profile.getInputNameLabelBg()));
		displayLabel.setBorder(new EmptyBorder(2, 2, 2, 2));

		this.configureDisplay(profile, SwingConstants.LEADING, "");

		final JPanel displayArea = new JPanel();
		displayArea.setLayout(new BorderLayout());
		displayArea.add(displayLabel, BorderLayout.WEST);
		displayArea.add(this.display, BorderLayout.CENTER);

		this.add(displayArea, BorderLayout.NORTH);

		this.enter = this.createEnterButton(profile);

		final JPanel cifferArea = new JPanel();
		cifferArea.setLayout(new GridLayout(2, 2));

		final JPanel topLeftArea = new JPanel();
		topLeftArea.setLayout(new GridLayout(2, 2));
		topLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_7, profile));
		topLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_8, profile));
		topLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_4, profile));
		topLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_5, profile));
		cifferArea.add(topLeftArea);

		final JPanel topRightArea = new JPanel();
		topRightArea.setLayout(new GridLayout(2, 2));
		topRightArea.add(this.createCifferButton(NumericPadPanel.TEXT_9, profile));

		final NumericPadAction deleteAction = new DeleteAction(profile);
		this.delete = new NumericPadButton(deleteAction, this.userPanel, profile);
		this.delete.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				final ActionListener[] actionListeners = NumericPadPanel.this.actionListeners.toArray(new ActionListener[0]);
				for (final ActionListener actionListener : actionListeners)
				{
					actionListener.actionPerformed(event);
				}
			}
		});
		topRightArea.add(this.delete);

		topRightArea.add(this.createCifferButton(NumericPadPanel.TEXT_6, profile));

		final ClearAction clearAction = new ClearAction(profile);
		this.clear = new NumericPadButton(clearAction, this.userPanel, profile);
		this.clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				final ActionListener[] actionListeners = NumericPadPanel.this.actionListeners.toArray(new ActionListener[0]);
				for (final ActionListener actionListener : actionListeners)
				{
					actionListener.actionPerformed(event);
				}
			}
		});
		topRightArea.add(this.clear);
		cifferArea.add(topRightArea);

		final JPanel bottomLeftArea = new JPanel();
		bottomLeftArea.setLayout(new GridLayout(2, 2));
		bottomLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_1, profile));
		bottomLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_2, profile));
		bottomLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_0, profile));
		bottomLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_00, profile));
		cifferArea.add(bottomLeftArea);

		final JPanel bottomRightArea = new JPanel();
		bottomRightArea.setLayout(new GridLayout(1, 2));

		final JPanel bottomRightLeftArea = new JPanel();
		bottomRightLeftArea.setLayout(new GridLayout(2, 1));
		bottomRightLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_3, profile));
		bottomRightLeftArea.add(this.createCifferButton(NumericPadPanel.TEXT_DOT, profile));
		bottomRightArea.add(bottomRightLeftArea);
		bottomRightArea.add(this.enter);
		cifferArea.add(bottomRightArea);

		this.add(cifferArea, BorderLayout.CENTER);
		
		this.userPanel.getPositionWrapper().addPropertyChangeListener(this);
		this.positionChangeMediator = new PositionChangeMediator(userPanel, this, NumericPadPanel.positionProperties);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource() instanceof Position)
		{
			StringBuilder label = new StringBuilder();
			Position position = (Position) event.getSource();
			if (position.getProductGroup() == null)
			{
				label = label.append("WG");
			}
			if (position.getSearchValue() == null || position.getSearchValue().length() == 0)
			{
				label = label.append(label.length() > 0 ? " / Barcode" : "Barcode");
			}
			if (position.getQuantity() == 0)
			{
				label = label.append(label.length() > 0 ? " / Menge" : "Menge");
			}
			else if (position.getPrice() == 0D)
			{
				label = label.append(label.length() > 0 ? " / Preis" : "Preis");
			}
			else if (position.getCurrentTax() == null)
			{
				label = label.append(label.length() > 0 ? " / MWST" : "MWST");
			}
			this.displayLabel.setText(label.toString());
			this.displayLabel.setForeground(Color.red);
		}
		else if (event.getSource() instanceof PositionChangeMediator)
		{
			if (event.getNewValue() instanceof Position)
			{
				this.displayLabel.setText("WG / Barcode");
				this.displayLabel.setForeground(new java.awt.Color(userPanel.getProfile().getValueLabelFg()));
			}
		}
	}

}
