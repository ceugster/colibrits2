package ch.eugster.colibri.display.area;

import java.util.Collection;
import java.util.Date;

import ch.eugster.colibri.persistence.model.print.IPrintable;

public interface ILayoutArea
{
	String getDefaultPattern();

	int getDefaultTimerDelay();

	String getHelp();

	ILayoutAreaType getLayoutAreaType();

	String getPattern();

	int getTimerDelay();

	Collection<String> prepareDisplay();

	Collection<String> prepareDisplay(IPrintable printable);

	String replaceMarker(final Date source, final String marker);

	String replaceMarker(final Long source, final String marker);

	String replaceMarker(final String source, final String target, final boolean padRight);

	void setPattern(final String pattern);

	void setTimerDelay(final int timerDelay);
}
