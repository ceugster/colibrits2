package ch.eugster.colibri.display.area;

public interface ILayoutAreaType
{
	int getColumnCount();

	ILayoutArea getLayoutArea();

	int getRowCount();

	String getSectionTitle();

	int ordinal();

	void setColumnCount(final int columns);

	void setRowCount(final int rowCount);
}