package ch.eugster.colibri.print.section;

import ch.eugster.colibri.persistence.model.print.IPrintable;

public interface IKey
{
	String label();

	int ordinal();

	String replace(ILayoutSection layoutSection, IPrintable printable, String marker);

	String toString();
}
