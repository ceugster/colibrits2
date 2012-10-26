/*
 * Created on 18.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import ch.eugster.colibri.persistence.model.Profile;

public abstract class DisplayPanel extends ProfilePanel
{
	public static final long serialVersionUID = 0l;

	protected NumberFormat defaultCurrencyFormat = NumberFormat.getNumberInstance();

	protected NumberFormat foreignCurrencyFormat = NumberFormat.getNumberInstance();

	protected double defaultCurrencyAmount;

	protected double foreignCurrencyAmount;

	protected JPanel amountPanel;

	protected JPanel foreignCurrencyPanel;

	protected JPanel defaultCurrencyPanel;

	protected JLabel textLabel;

	protected JLabel foreignCurrencyLabel;

	protected JLabel foreignCurrencyAmountLabel;

	protected JLabel defaultCurrencyLabel;

	protected JLabel defaultCurrencyAmountLabel;

	protected static final String MEASURE_TEXT = "MMMMMMMMMM";

	protected static final float FONT_SIZE = 18f;

	protected static final Color DEFAULT_BACKGROUND = Color.BLACK;

	protected static final Color DEFAULT_FOREGROUND = Color.GREEN;

	public DisplayPanel(final Profile profile)
	{
		super(profile);
		init();
	}

	public double getDefaultCurrencyAmount()
	{
		return defaultCurrencyAmount;
	}

	public Color getDisplayBackground()
	{
		return textLabel.getBackground();
	}

	public Font getDisplayFont()
	{
		return textLabel.getFont();
	}

	public Color getDisplayForeground()
	{
		return textLabel.getForeground();
	}

	public double getForeignCurrencyAmount()
	{
		return foreignCurrencyAmount;
	}

	public void setDisplayBackground(final Color color)
	{
		textLabel.setBackground(color);
		foreignCurrencyLabel.setBackground(color);
		foreignCurrencyAmountLabel.setBackground(color);
		defaultCurrencyLabel.setBackground(color);
		defaultCurrencyAmountLabel.setBackground(color);
	}

	public void setDisplayFont(final Font font)
	{
		textLabel.setFont(font);
		final FontMetrics metrics = textLabel.getFontMetrics(textLabel.getFont());
		textLabel.setPreferredSize(new Dimension(metrics.stringWidth(DisplayPanel.MEASURE_TEXT), metrics.getHeight()));

		foreignCurrencyLabel.setFont(font);
		foreignCurrencyLabel.setSize(new Dimension(metrics.stringWidth(foreignCurrencyLabel.getText()), metrics.getHeight()));
		foreignCurrencyAmountLabel.setFont(font);
		foreignCurrencyAmountLabel.setSize(new Dimension(metrics.stringWidth(foreignCurrencyAmountLabel.getText()), metrics.getHeight()));
		foreignCurrencyPanel.setSize(new Dimension(foreignCurrencyLabel.getSize().width + foreignCurrencyAmountLabel.getSize().width, metrics
				.getHeight()));

		defaultCurrencyLabel.setFont(font);
		defaultCurrencyLabel.setSize(new Dimension(metrics.stringWidth(defaultCurrencyLabel.getText()), metrics.getHeight()));
		defaultCurrencyAmountLabel.setFont(font);
		defaultCurrencyAmountLabel.setSize(new Dimension(metrics.stringWidth(defaultCurrencyAmountLabel.getText()), metrics.getHeight()));
		defaultCurrencyPanel.setSize(new Dimension(defaultCurrencyLabel.getSize().width + defaultCurrencyAmountLabel.getSize().width, metrics
				.getHeight()));

		amountPanel.setSize(new Dimension(foreignCurrencyPanel.getSize().width + defaultCurrencyPanel.getSize().width, metrics.getHeight()));

		textLabel.validate();
		foreignCurrencyPanel.validate();
		defaultCurrencyPanel.validate();
		amountPanel.validate();
	}

	public void setDisplayForeground(final Color color)
	{
		textLabel.setForeground(color);
		foreignCurrencyLabel.setForeground(color);
		foreignCurrencyAmountLabel.setForeground(color);
		defaultCurrencyLabel.setForeground(color);
		defaultCurrencyAmountLabel.setForeground(color);
	}

	protected JLabel createLabel(final Profile profile, final String text)
	{
		final JLabel label = new JLabel(text);
		label.setOpaque(true);
		return label;
	}

	protected abstract String getText();

	protected void init()
	{
		setLayout(new BorderLayout());

		textLabel = createLabel(profile, getText());
		textLabel.setMinimumSize(new Dimension(textLabel.getFontMetrics(textLabel.getFont()).stringWidth(DisplayPanel.MEASURE_TEXT), textLabel
				.getMinimumSize().height));
		textLabel.setPreferredSize(new Dimension(textLabel.getFontMetrics(textLabel.getFont()).stringWidth(DisplayPanel.MEASURE_TEXT), textLabel
				.getMinimumSize().height));
		this.add(textLabel, BorderLayout.WEST);

		amountPanel = new JPanel(new GridLayout(1, 2));

		final Border border = textLabel.getBorder();
		Border margin = new EmptyBorder(0, 0, 0, 10);

		foreignCurrencyPanel = new JPanel(new BorderLayout());
		foreignCurrencyLabel = createLabel(profile, "");
		foreignCurrencyLabel.setBorder(new CompoundBorder(border, margin));
		foreignCurrencyPanel.add(foreignCurrencyLabel, BorderLayout.WEST);

		foreignCurrencyAmountLabel = createLabel(profile, "");
		foreignCurrencyAmountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		foreignCurrencyPanel.add(foreignCurrencyAmountLabel, BorderLayout.CENTER);
		amountPanel.add(foreignCurrencyPanel);

		margin = new EmptyBorder(0, 10, 0, 10);

		defaultCurrencyPanel = new JPanel(new BorderLayout());
		defaultCurrencyLabel = createLabel(profile, "");
		defaultCurrencyLabel.setBorder(new CompoundBorder(border, margin));
		defaultCurrencyPanel.add(defaultCurrencyLabel, BorderLayout.WEST);

		defaultCurrencyAmountLabel = createLabel(profile, "");
		defaultCurrencyAmountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		defaultCurrencyPanel.add(defaultCurrencyAmountLabel, BorderLayout.CENTER);
		amountPanel.add(defaultCurrencyPanel);

		this.update();

		this.add(amountPanel);
	}

	@Override
	protected void update()
	{
		textLabel.setFont(textLabel.getFont().deriveFont(profile.getDisplayFontStyle(), profile.getDisplayFontSize()));
		textLabel.setForeground(new java.awt.Color(profile.getDisplayFg()));
		textLabel.setBackground(new java.awt.Color(profile.getDisplayBg()));

		foreignCurrencyLabel.setFont(foreignCurrencyLabel.getFont().deriveFont(profile.getDisplayFontStyle(), profile.getDisplayFontSize()));
		foreignCurrencyLabel.setForeground(new java.awt.Color(profile.getDisplayFg()));
		foreignCurrencyLabel.setBackground(new java.awt.Color(profile.getDisplayBg()));

		foreignCurrencyAmountLabel.setFont(foreignCurrencyAmountLabel.getFont().deriveFont(profile.getDisplayFontStyle(),
				profile.getDisplayFontSize()));
		foreignCurrencyAmountLabel.setForeground(new java.awt.Color(profile.getDisplayFg()));
		foreignCurrencyAmountLabel.setBackground(new java.awt.Color(profile.getDisplayBg()));

		defaultCurrencyLabel.setFont(defaultCurrencyLabel.getFont().deriveFont(profile.getDisplayFontStyle(), profile.getDisplayFontSize()));
		defaultCurrencyLabel.setForeground(new java.awt.Color(profile.getDisplayFg()));
		defaultCurrencyLabel.setBackground(new java.awt.Color(profile.getDisplayBg()));

		defaultCurrencyAmountLabel.setFont(defaultCurrencyAmountLabel.getFont().deriveFont(profile.getDisplayFontStyle(),
				profile.getDisplayFontSize()));
		defaultCurrencyAmountLabel.setForeground(new java.awt.Color(profile.getDisplayFg()));
		defaultCurrencyAmountLabel.setBackground(new java.awt.Color(profile.getDisplayBg()));
	}

}
