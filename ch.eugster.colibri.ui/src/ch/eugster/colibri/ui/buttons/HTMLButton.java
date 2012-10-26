/*
 * Created on 23.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.ui.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.UIManager;

import ch.eugster.colibri.ui.actions.BasicAction;

public abstract class HTMLButton extends JButton implements PropertyChangeListener
{
	public static final long serialVersionUID = 0l;

	public static final String FONT_REGEX = "<font>";

	public static final String COLOR_REGEX = "( color=\"#)[0-9abcdef]{6}(\")";

	public static final String SIZE_REGEX = " size=\\\"\\d{0,2}(.)?\\d{0,2}+(em)?\\\"";

	public static final String HTML_PATTERN = "<html>";

	public static final String COLOR_PATTERN = "color=\"#";

	public static final String SIZE_PATTERN_START = "size=\"";

	public static final String SIZE_PATTERN_END = "em\"";

	public static final String ATTRIBUTE_END = "\"";

	public static final String FONT_PATTERN_START = "<font ";

	public static final String FONT_PATTERN_END = ">";

	public static final String LINE_BREAK_CHAR = "|";

	public static final Dimension MIN_BUTTON_SIZE = new Dimension(60, 40);

	public static final Dimension PREFERRED_BUTTON_SIZE = new Dimension(80, 60);

	public static final Dimension MAX_BUTTON_SIZE = new Dimension(480, 360);

//	private int fgNormal;

	private int fgFailOver;

	protected boolean failOver;

	public HTMLButton()
	{
		super();
	}

	public HTMLButton(final BasicAction action, final int fgNormal, final int fgFailOver)
	{
		super(action);
		setActionCommand(action.getActionCommand());
		this.init(fgNormal, fgFailOver);
		setFocusable(false);
		action.addPropertyChangeListener(this);
	}

	public HTMLButton(final int fgNormal, final int fgFailOver)
	{
		super();
		this.init(fgNormal, fgFailOver);
	}

	public String extractText(final String labelText)
	{
		String text = labelText.replaceAll(HTMLButton.SIZE_REGEX, "");
		text = text.replaceAll(HTMLButton.COLOR_REGEX, "");
		text = text.replaceAll(HTMLButton.FONT_REGEX, "");
		text = text.replaceAll(HTMLButton.HTML_PATTERN, "");
		System.out.println(labelText + "         " + text);
		return text;
	}

	public int extractColor(final String labelText)
	{
		int rgb = this.getForeground().getRGB();
		String color = " color=\"#" + Integer.toHexString(rgb).substring(2) + "\"";
		String text = labelText.replaceAll(COLOR_REGEX, color);
		System.out.println(text);
		return rgb;
	}

	@Override
	public boolean isFocusTraversable()
	{
		return false;
	}

	@Override
	public boolean isRequestFocusEnabled()
	{
		return false;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getPropertyName().equals("fail.over"))
		{
			this.update(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	@Override
	public void setBackground(final Color bg)
	{
		super.setBackground(getBackgroundColor(bg));
	}

	protected Color getBackgroundColor(Color bg)
	{
		return bg.getRGB() == UIManager.getColor("Button.background").getRGB() ? UIManager
				.getColor("Button.background") : bg;
	}

	@Override
	protected void paintComponent(Graphics gra)
	{
		Graphics2D g = (Graphics2D) gra;
		super.paintComponent(g);
		int w = this.getWidth();
		int h = this.getHeight();

		GradientPaint gradientPaint = new GradientPaint(w, 0F, Color.WHITE, w, h, this.getBackground());
		g.setPaint(gradientPaint);
		g.fillRect(0, 0, w, h);

		getUI().paint(g, this);
	}

	@Override
	public void setEnabled(final boolean enabled)
	{
		super.setEnabled(enabled);
		if (getText().toLowerCase().startsWith(HTMLButton.HTML_PATTERN))
		{
			// TODO
			super.setText(updateLabel(!true));
		}
	}

	@Override
	public void setFont(final java.awt.Font font)
	{
		super.setFont(font);
		if (super.getText().toLowerCase().startsWith(HTMLButton.HTML_PATTERN))
		{
			// TODO
			super.setText(createLabel(getText(), !true));
		}
	}

	@Override
	public void setForeground(final java.awt.Color fg)
	{
		super.setForeground(fg);
		if (getText().toLowerCase().startsWith(HTMLButton.HTML_PATTERN))
		{
			super.setText(createLabel(this.getText(), !true));
		}
	}

	public abstract void update(boolean failOver);

	public String updateLabel(final boolean failOver)
	{
		return createLabel(getText(), failOver);
	}

	protected String createLabel(final String labelText, final boolean failOver)
	{
		if (failOver)
		{
			return createLabelFailOver(labelText);
		}
		else
		{
			return createLabelNormal(labelText);
		}
	}

	protected String createLabelFailOver(final String labelText)
	{
		String text = null;
		if (labelText.toLowerCase().startsWith(HTMLButton.HTML_PATTERN) || labelText.toLowerCase().contains("<br>")
				|| labelText.toLowerCase().contains("|"))
		{
			text = extractText(labelText);
			int fg = 0;
			if (isEnabled())
			{
				fg = fgFailOver;
			}
			else
			{
				fg = java.awt.Color.GRAY.getRGB();
			}

			if (text.contains("|"))
			{
				text = text.replace("|", "<br>");
			}
			final String fontTag = getFontTag(getFont(), fg);
			text = HTMLButton.HTML_PATTERN + fontTag + text;
		}
		else
		{
			text = labelText;
		}
		return text;
	}

	protected String createLabelNormal(final String labelText)
	{
		String text = null;
		if (labelText.toLowerCase().startsWith(HTMLButton.HTML_PATTERN) || labelText.toLowerCase().contains("<br>")
				|| labelText.toLowerCase().contains("|"))
		{
			text = extractText(labelText);
			int fg = this.getForeground().getRGB();
			if (!isEnabled())
			{
				// fg = fgNormal;
				// }
				// else
				// {
				fg = java.awt.Color.GRAY.getRGB();
			}

			if (text.contains("|"))
			{
				text = text.replace("|", "<br>");
			}
			final String fontTag = getFontTag(getFont(), fg);
			text = HTMLButton.HTML_PATTERN + fontTag + text;
		}
		else
		{
			text = labelText;
		}
		return text;
	}

	protected void update(final Color fg, final Color bg, final int fontStyle, final float fontSize,
			final int horzAlign, final int vertAlign)
	{
		setBackground(bg);
		setForeground(fg);
		setFont(getFont().deriveFont(fontStyle, fontSize));
		setHorizontalAlignment(horzAlign);
		setVerticalAlignment(vertAlign);
	}

	private String getColorAttribute(final int fg)
	{
		final java.awt.Color color = new java.awt.Color(fg);
		final String rgb = Integer.toHexString(color.getRGB());
		return HTMLButton.COLOR_PATTERN + rgb.substring(2, rgb.length()) + HTMLButton.ATTRIBUTE_END;
	}

	private String getFontSizeAttribute(final java.awt.Font font)
	{
		final float size = Math.round(font.getSize() / 12 * 100) / 100;
		return HTMLButton.SIZE_PATTERN_START + size + HTMLButton.SIZE_PATTERN_END;
	}

	private String getFontTag(final java.awt.Font font, final int fg)
	{
		final String colorAttribute = getColorAttribute(fg);
		final String fontSizeAttribute = getFontSizeAttribute(font);
		return HTMLButton.FONT_PATTERN_START + colorAttribute + " " + fontSizeAttribute + HTMLButton.FONT_PATTERN_END;
	}

	private void init(final int fgNormal, final int fgFailOver)
	{
//		this.fgNormal = fgNormal;
		this.fgFailOver = fgFailOver;

		setFocusable(true);

		setMinimumSize(HTMLButton.MIN_BUTTON_SIZE);
		setPreferredSize(HTMLButton.PREFERRED_BUTTON_SIZE);
		setMaximumSize(HTMLButton.MAX_BUTTON_SIZE);
	}

}
