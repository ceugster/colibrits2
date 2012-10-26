package ch.eugster.colibri.print.section;

public interface ILayoutSectionType
{
	int getColumnCount();

	int getLayoutAreaHeight();

	ILayoutSection getLayoutSection();

	String getSectionId();

	String getSectionTitle();

	int ordinal();

	void setColumnCount(int columns);
}
