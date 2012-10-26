package ch.eugster.colibri.display.simple.area;

import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.persistence.model.print.IPrintable;

public interface IKey
{
	String label();

	int ordinal();

	String replace(ILayoutArea layoutArea, IPrintable printable, String marker);

	String toString();
}
