package ch.eugster.colibri.print.section;

import java.util.Collection;
import java.util.Date;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.print.IPrintable;

public interface ILayoutSection
{
	int getAreaHeight(AreaType areaType);

	String getDefaultPattern(AreaType areaType);

	PrintOption getDefaultPrintOption(AreaType areaType);

	String getHelp(AreaType areaType);

	ILayoutSectionType getLayoutSectionType();

	boolean hasArea(AreaType areaType);

	Collection<String> prepareSection();

	Collection<String> prepareSection(IPrintable printable);

	String replaceMarker(Date date, String marker);

	String replaceMarker(final Long value, final String marker);

	String replaceMarker(String text, String marker, boolean padRight);

	String replaceMarker(User user, String marker);

	void setPattern(AreaType areaType, final String pattern);

	void setPrintOption(AreaType areaType, final PrintOption printOption);

	public enum AreaType
	{
		TITLE, DETAIL, TOTAL;

		public String label()
		{
			switch (this)
			{
				case TITLE:
				{
					return "Titelbereich";
				}
				case DETAIL:
				{
					return "Detailbereich";
				}
				case TOTAL:
				{
					return "Totalbereich";
				}
				default:
				{
					throw new RuntimeException("Invalid area type");
				}
			}
		}
	}
}
