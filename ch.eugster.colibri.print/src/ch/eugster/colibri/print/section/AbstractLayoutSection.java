package ch.eugster.colibri.print.section;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.print.Activator;

public abstract class AbstractLayoutSection implements ILayoutSection
{
	private static DateFormat dateFormatter = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);

	private static DateFormat timeFormatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);

	protected static NumberFormat simpleIntegerFormatter = DecimalFormat.getIntegerInstance();

	protected static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	private final ILayoutSectionType layoutSectionType;

	private String titlePattern;

	private String detailPattern;

	private String totalPattern;

	private PrintOption titlePrintOption;

	private PrintOption detailPrintOption;

	private PrintOption totalPrintOption;
	
	protected static String receiptNumberFormat;

	public AbstractLayoutSection(final ILayoutSectionType layoutSectionType)
	{
		this.layoutSectionType = layoutSectionType;
		simpleIntegerFormatter.setGroupingUsed(false);
		simpleIntegerFormatter.setMaximumFractionDigits(0);
		simpleIntegerFormatter.setMinimumFractionDigits(0);
		receiptNumberFormat = this.getCommonSettingsReceiptNumberFormat();
	}

	@Override
	public int getAreaHeight(final AreaType areaType)
	{
		return 48;
	}

	protected char[] getFontSize(ReceiptPrinterService.Size size) 
	{
		switch(size)
		{
		case NORMAL:
		{
			return new char[] { 29, 33, 0};
		}
		case DOUBLE_WIDTH:
		{
			return new char[] { 29, 33, 16};
		}
		case DOUBLE_HEIGHT:
		{
			return new char[] { 29, 33, 1};
		}
		case DOUBLE_WIDTH_AND_HEIGHT:
		{
			return new char[] { 29, 33, 17};
		}
		default:
		{
			return new char[] { 29, 33, 0};
		}
		}
	}

	@Override
	public String getDefaultPattern(final AreaType areaType)
	{
		switch (areaType)
		{
			case TITLE:
			{
				return this.getDefaultPatternTitle();
			}
			case DETAIL:
			{
				return this.getDefaultPatternDetail();
			}
			case TOTAL:
			{
				return this.getDefaultPatternTotal();
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	@Override
	public PrintOption getDefaultPrintOption(final AreaType areaType)
	{
		switch (areaType)
		{
			case TITLE:
			{
				return this.getDefaultPrintOptionTitle();
			}
			case DETAIL:
			{
				return this.getDefaultPrintOptionDetail();
			}
			case TOTAL:
			{
				return this.getDefaultPrintOptionTotal();
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	@Override
	public String getHelp(final AreaType areaType)
	{
		StringBuilder builder = new StringBuilder("Verwendbare Variablen:\n");
		final IKey[] keys = this.getKeys(areaType);
		for (final IKey key : keys)
		{
			builder = builder.append(key.toString() + " - " + key.label() + "\n");
		}
		return builder.toString();
	}

	@Override
	public ILayoutSectionType getLayoutSectionType()
	{
		return this.layoutSectionType;
	}

	@Override
	public boolean hasArea(final AreaType areaType)
	{
		switch (areaType)
		{
			case TITLE:
			{
				return this.hasTitleArea();
			}
			case DETAIL:
			{
				return this.hasDetailArea();
			}
			case TOTAL:
			{
				return this.hasTotalArea();
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	@Override
	public Collection<String> prepareSection()
	{
		final Collection<String> sections = new ArrayList<String>();
		if (this.hasTitleArea())
		{
			if (!this.getPrintOption(AreaType.TITLE).equals(PrintOption.NEVER))
			{
				sections.addAll(this.prepareArea(AreaType.TITLE));
			}
		}
		if (this.hasDetailArea())
		{
			if (!this.getPrintOption(AreaType.DETAIL).equals(PrintOption.NEVER))
			{
				sections.addAll(this.prepareArea(AreaType.DETAIL));
			}
		}
		if (this.hasTotalArea())
		{
			if (!this.getPrintOption(AreaType.TOTAL).equals(PrintOption.NEVER))
			{
				sections.addAll(this.prepareArea(AreaType.TOTAL));
			}
		}
		return sections;
	}

	@Override
	public Collection<String> prepareSection(final IPrintable printable)
	{
		final Collection<String> sections = new ArrayList<String>();
		if (this.hasTitleArea())
		{
			if (!this.getPrintOption(AreaType.TITLE).equals(PrintOption.NEVER))
			{
				sections.addAll(this.prepareArea(AreaType.TITLE, printable));
			}
		}
		if (this.hasDetailArea())
		{
			if (!this.getPrintOption(AreaType.DETAIL).equals(PrintOption.NEVER))
			{
				sections.addAll(this.prepareArea(AreaType.DETAIL, printable));
			}
		}
		if (this.hasTotalArea())
		{
			if (!this.getPrintOption(AreaType.TOTAL).equals(PrintOption.NEVER))
			{
				sections.addAll(this.prepareArea(AreaType.TOTAL, printable));
			}
		}
		return sections;
	}

	@Override
	public String replaceMarker(final Date source, final String marker)
	{
		if ((marker == null) || marker.isEmpty())
		{
			return "";
		}
		if (source == null)
		{
			return AbstractLayoutType.padRight("", marker.length());
		}
		final String result = (marker.charAt(0) == 'D') ? this.formatDate(source) : (marker.charAt(0) == 'T' ? this
				.formatTime(source) : "");
		return this.replaceMarker(result, marker, true);
	}

	@Override
	public String replaceMarker(final Long source, final String marker)
	{
		if ((marker == null) || marker.isEmpty())
		{
			return "";
		}
		if (source == null)
		{
			return AbstractLayoutType.padRight("", marker.length());
		}
		final String result = source.toString();
		return this.replaceMarker(result, marker, true);
	}

	@Override
	public String replaceMarker(final String source, final String target, final boolean padRight)
	{
		/*
		 * target: string to replace source: string to replace with
		 */
		if ((target == null) || target.isEmpty())
		{
			return "";
		}
		if (((source == null) || source.isEmpty()))
		{
			return AbstractLayoutType.padRight("", target.length());
		}
		if (source.length() > target.length())
		{
			return source.substring(0, target.length());
		}
		if (source.length() < target.length())
		{
			return padRight ? AbstractLayoutType.padRight(source, target.length()) : AbstractLayoutType.padLeft(source,
					target.length());
		}
		return source;
	}

	@Override
	public String replaceMarker(final User source, final String target)
	{
		if ((target == null) || target.isEmpty())
		{
			return "";
		}
		if (source == null)
		{
			return AbstractLayoutType.padRight("", target.length());
		}
		return this.replaceMarker(source.getUsername(), target, true);
	}

	@Override
	public void setPattern(final AreaType areaType, final String pattern)
	{
		switch (areaType)
		{
			case TITLE:
			{
				this.setPatternTitle(pattern);
				break;
			}
			case DETAIL:
			{
				this.setPatternDetail(pattern);
				break;
			}
			case TOTAL:
			{
				this.setPatternTotal(pattern);
				break;
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	@Override
	public void setPrintOption(final AreaType areaType, final PrintOption printOption)
	{
		switch (areaType)
		{
			case TITLE:
			{
				this.setPrintOptionTitle(printOption);
				break;
			}
			case DETAIL:
			{
				this.setPrintOptionDetail(printOption);
				break;
			}
			case TOTAL:
			{
				this.setPrintOptionTotal(printOption);
				break;
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	/**
	 * subclasses may override this method to adapt the pattern to their needs
	 * 
	 * @param segments
	 * @return
	 */
	protected Collection<String> adaptPatternDetail(final Collection<String> segments, IPrintable printable)
	{
		return segments;
	}

	/**
	 * subclasses may override this method to adapt the pattern to their needs
	 * 
	 * @param segments
	 * @return
	 */
	protected Collection<String> adaptPatternTitle(final Collection<String> segments, final IPrintable printable)
	{
		return segments;
	}

	/**
	 * subclasses may override this method to adapt the pattern to their needs
	 * 
	 * @param segments
	 * @return
	 */
	protected Collection<String> adaptPatternTotal(final Collection<String> segments, final IPrintable printable)
	{
		return segments;
	}

	protected String[] buildMarkers(final AreaType areaType, final Collection<String> lines)
	{
		final Collection<String> markers = new ArrayList<String>();
		for (final String line : lines)
		{
			final String[] items = line.split(" ");
			for (int i = 0; i < items.length; i++)
			{
				if (!items[i].isEmpty() && this.isKey(areaType, items[i].charAt(0)))
				{
					final String marker = this.getMarker(items[i].charAt(0), items[i].length());
					if (items[i].equals(marker))
					{
						markers.add(marker);
					}
				}
			}
		}
		return markers.toArray(new String[0]);
	}

	protected String[] buildMarkers(final AreaType areaType, final String line)
	{
		final Collection<String> markers = new ArrayList<String>();
		final String[] items = line.split(" ");
		for (int i = 0; i < items.length; i++)
		{
			if (!items[i].isEmpty() && this.isKey(areaType, items[i].charAt(0)))
			{
				final String marker = this.getMarker(items[i].charAt(0), items[i].length());
				if (items[i].equals(marker))
				{
					markers.add(marker);
				}
			}
		}
		return markers.toArray(new String[0]);
	}

	protected String correctLineSize(String line)
	{
		if (line.length() > this.getLayoutSectionType().getColumnCount())
		{
			line = line.substring(0, this.getLayoutSectionType().getColumnCount());
		}
		if (line.length() < this.getLayoutSectionType().getColumnCount())
		{
			line = AbstractLayoutType.padRight(line, this.getLayoutSectionType().getColumnCount());
		}
		return line;
	}

	protected Collection<String> correctLineSizes(final Collection<String> lines)
	{
		for (String line : lines)
		{
			line = this.correctLineSize(line);
		}
		return lines;
	}

	// @Override
	// public Collection<String> prepareSection()
	// {
	// final Collection<String> result = new ArrayList<String>();
	// if (this.pattern != null)
	// {
	// final String[] lines = this.pattern.split("\n");
	// for (String line : lines)
	// {
	// line = line.replace("\r", "");
	// System.out.println("Vorher:  '" + line + "'");
	// if (line.length() > this.layoutSectionType.getColumnCount())
	// {
	// line = line.substring(0, this.layoutSectionType.getColumnCount());
	// }
	// else if (line.length() < this.layoutSectionType.getColumnCount())
	// {
	// line = AbstractLayoutType.padRight(line,
	// this.layoutSectionType.getColumnCount());
	// }
	// System.out.println("Nachher: '" + line + "'");
	// result.add(line);
	// }
	// }
	// return result;
	// }

	protected String getCommonSettingsHeader()
	{
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundleContext(), PersistenceService.class, null);
		tracker.open();
		PersistenceService service = tracker.getService();
		if (service != null)
		{
			CommonSettingsQuery query = (CommonSettingsQuery) service.getCacheService().getQuery(CommonSettings.class);
			CommonSettings settings = query.findDefault();
			return settings.getAddress();
		}
		return null;
	}
	
	protected String getCommonSettingsTaxNumber()
	{
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundleContext(), PersistenceService.class, null);
		tracker.open();
		PersistenceService service = tracker.getService();
		if (service != null)
		{
			CommonSettingsQuery query = (CommonSettingsQuery) service.getCacheService().getQuery(CommonSettings.class);
			CommonSettings settings = query.findDefault();
			return settings.getTaxNumber();
		}
		return null;
	}
	
	protected String getCommonSettingsReceiptNumberFormat()
	{
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundleContext(), PersistenceService.class, null);
		tracker.open();
		PersistenceService service = tracker.getService();
		if (service != null)
		{
			CommonSettingsQuery query = (CommonSettingsQuery) service.getCacheService().getQuery(CommonSettings.class);
			CommonSettings settings = query.findDefault();
			return settings.getReceiptNumberFormat();
		}
		return null;
	}
	
	protected abstract String getDefaultPatternDetail();

	protected String getDefaultPatternTitle()
	{
		return "";
	}

	protected String getDefaultPatternTotal()
	{
		return "==========================================";
	}

	protected PrintOption getDefaultPrintOptionTitle()
	{
		return PrintOption.OPTIONALLY;
	}

	protected PrintOption getDefaultPrintOptionDetail()
	{
		return PrintOption.OPTIONALLY;
	}

	protected PrintOption getDefaultPrintOptionTotal()
	{
		return PrintOption.OPTIONALLY;
	}

	protected IKey[] getKeys(final AreaType areaType)
	{
		switch (areaType)
		{
			case TITLE:
			{
				return this.getKeysTitle();
			}
			case DETAIL:
			{
				return this.getKeysDetail();
			}
			case TOTAL:
			{
				return this.getKeysTotal();
			}
			default:
			{
				throw new RuntimeException("invalid area type");
			}
		}
	}

	protected abstract IKey[] getKeysDetail();

	protected abstract IKey[] getKeysTitle();

	protected abstract IKey[] getKeysTotal();

	protected Collection<String> getLines(final String[] segments)
	{
		final Collection<String> lines = new ArrayList<String>();
		for (final String segment : segments)
		{
			lines.add(segment);
		}
		return lines;
	}

	protected String getMarker(final char c, final int length)
	{
		final char[] array = new char[length];
		Arrays.fill(array, c);
		return String.valueOf(array);
	}

	protected String[] getMarkers(final AreaType areaType, final Collection<String> lines)
	{
		return this.buildMarkers(areaType, lines);
	}

	protected String[] getMarkers(final AreaType areaType, final String line)
	{
		return this.buildMarkers(areaType, line);
	}

	protected Collection<String> getPattern(final AreaType areaType)
	{
		switch (areaType)
		{
			case TITLE:
			{
				return this.getLines(this.getPatternTitle());
			}
			case DETAIL:
			{
				return this.getLines(this.getPatternDetail());
			}
			case TOTAL:
			{
				return this.getLines(this.getPatternTotal());
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	protected Collection<String> getPattern(final AreaType areaType, final IPrintable printable)
	{
		switch (areaType)
		{
			case TITLE:
			{
				return this.getPatternTitle(printable);
			}
			case DETAIL:
			{
				return this.getPatternDetail(printable);
			}
			case TOTAL:
			{
				return this.getPatternTotal(printable);
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	protected String[] getPatternDetail()
	{
		String[] segments = null;
		if (this.detailPattern == null)
		{
			if (this.getDefaultPatternDetail() == null)
			{
				segments = new String[0];
			}
			else
			{
				segments = this.getDefaultPatternDetail().split("\n");
			}
		}
		else
		{
			segments = this.detailPattern.split("\n");
		}
		return segments;
	}

	protected Collection<String> getPatternDetail(final IPrintable printable)
	{
		return this.adaptPatternDetail(this.getLines(this.getPatternDetail()), printable);
	}

	protected String[] getPatternTitle()
	{
		String[] segments = null;
		if (this.titlePattern == null)
		{
			if (this.getDefaultPatternTitle() == null)
			{
				segments = new String[0];
			}
			else
			{
				segments = this.getDefaultPatternTitle().split("\n");
			}
		}
		else
		{
			segments = this.titlePattern.split("\n");
		}
		return segments;
	}

	protected Collection<String> getPatternTitle(final IPrintable printable)
	{
		return this.adaptPatternTitle(this.getLines(this.getPatternTitle()), printable);
	}

	protected String[] getPatternTotal()
	{
		String[] segments = null;
		if (this.totalPattern == null)
		{
			if (this.getDefaultPatternTotal() == null)
			{
				segments = new String[0];
			}
			else
			{
				segments = this.getDefaultPatternTotal().split("\n");
			}
		}
		else
		{
			segments = this.totalPattern.split("\n");
		}
		return segments;
	}

	protected Collection<String> getPatternTotal(final IPrintable printable)
	{
		return this.adaptPatternTotal(this.getLines(this.getPatternTotal()), printable);
	}

	protected PrintOption getPrintOption(final AreaType areaType)
	{
		switch (areaType)
		{
			case TITLE:
			{
				return this.getPrintOptionTitle();
			}
			case DETAIL:
			{
				return this.getPrintOptionDetail();
			}
			case TOTAL:
			{
				return this.getPrintOptionTotal();
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	protected PrintOption getPrintOptionDetail()
	{
		if (this.detailPrintOption == null)
		{
			return this.getDefaultPrintOptionDetail();
		}
		return this.detailPrintOption;
	}

	protected PrintOption getPrintOptionTitle()
	{
		if (this.titlePrintOption == null)
		{
			return this.getDefaultPrintOptionTitle();
		}
		return this.titlePrintOption;
	}

	protected PrintOption getPrintOptionTotal()
	{
		if (this.totalPrintOption == null)
		{
			return this.getDefaultPrintOptionTotal();
		}
		return this.totalPrintOption;
	}

	protected abstract boolean hasDetailArea();

	protected abstract boolean hasTitleArea();

	protected abstract boolean hasTotalArea();

	protected boolean isKey(final AreaType areaType, final char c)
	{
		final IKey[] keys = this.getKeys(areaType);
		for (final IKey key : keys)
		{
			if (key.toString().charAt(0) == c)
			{
				return true;
			}
		}
		return false;
	}

	protected Collection<String> prepareArea(final AreaType areaType)
	{
		if (this.hasArea(areaType))
		{
			if (!this.getPrintOption(areaType).equals(PrintOption.NEVER))
			{
				return this.getPattern(areaType);
			}
		}
		return new ArrayList<String>();
	}

	protected Collection<String> prepareArea(final AreaType areaType, final IPrintable printable)
	{
		switch (areaType)
		{
			case TITLE:
			{
				return this.prepareAreaTitle(printable);
			}
			case DETAIL:
			{
				return this.prepareAreaDetail(printable);
			}
			case TOTAL:
			{
				return this.prepareAreaTotal(printable);
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	protected abstract boolean hasData(IPrintable printable);

	protected Collection<String> prepareAreaDetail(final IPrintable printable)
	{
		final Collection<String> lines = new ArrayList<String>();
		if (printIt(AreaType.DETAIL, printable))
		{
			Collection<String> patternLines = adaptPatternDetail(this.getPattern(AreaType.DETAIL), printable);
			final String[] markers = this.getMarkers(AreaType.DETAIL, patternLines);
			for (String patternLine : patternLines)
			{
				for (final String marker : markers)
				{
					patternLine = this.replace(AreaType.DETAIL, printable, marker, patternLine);
				}
				if (!patternLine.trim().isEmpty() || this.getPrintOption(AreaType.DETAIL).equals(PrintOption.ALWAYS))
				{
					lines.add(patternLine);
				}
			}
		}
		return lines;
	}

	protected Collection<String> prepareAreaTitle(final IPrintable printable)
	{
		final Collection<String> lines = new ArrayList<String>();
		if (printIt(AreaType.TITLE, printable))
		{
			final Collection<String> patternLines = adaptPatternTitle(this.getPattern(AreaType.TITLE), printable);
			final String[] markers = this.getMarkers(AreaType.TITLE, patternLines);
			for (String patternLine : patternLines)
			{
				for (final String marker : markers)
				{
					patternLine = this.replace(AreaType.TITLE, printable, marker, patternLine);
				}
				if (!patternLine.trim().isEmpty() || this.getPrintOption(AreaType.TITLE).equals(PrintOption.ALWAYS))
				{
					lines.add(patternLine);
				}
			}
		}
		return lines;
	}

	protected Collection<String> prepareAreaTotal(final IPrintable printable)
	{
		final Collection<String> lines = new ArrayList<String>();
		if (printIt(AreaType.TOTAL, printable))
		{
			Collection<String> patternLines = adaptPatternDetail(this.getPattern(AreaType.TOTAL), printable);
			final String[] markers = this.getMarkers(AreaType.TOTAL, patternLines);
			for (String patternLine : patternLines)
			{
				for (final String marker : markers)
				{
					patternLine = this.replace(AreaType.TOTAL, printable, marker, patternLine);
				}
				if (!patternLine.trim().isEmpty() || this.getPrintOption(AreaType.TOTAL).equals(PrintOption.ALWAYS))
				{
					lines.add(patternLine);
				}
			}
		}
		return lines;
	}

	protected boolean printIt(AreaType areaType, IPrintable printable)
	{
		if (!hasArea(areaType))
		{
			return false;
		}

		PrintOption printOption = this.getPrintOption(areaType);
		switch (printOption)
		{
			case ALWAYS:
			{
				return true;
			}
			case NEVER:
			{
				return false;
			}
			case OPTIONALLY:
			{
				return this.hasData(printable);
			}
			default:
			{
				return false;
			}
		}
	}

	protected String replace(final AreaType areaType, final IPrintable printable, final String marker, String line)
	{
		for (final IKey key : this.getKeys(areaType))
		{
			if (key.toString().charAt(0) == marker.charAt(0))
			{
				final String item = key.replace(this, printable, marker);
				line = line.replace(marker, item);
			}
		}
		return line;
	}

	protected void setPatternDetail(final String pattern)
	{
		this.detailPattern = pattern;
	}

	protected void setPatternTitle(final String pattern)
	{
		this.titlePattern = pattern;
	}

	protected void setPatternTotal(final String pattern)
	{
		this.totalPattern = pattern;
	}

	protected void setPrintOptionDetail(final PrintOption detailPrintOption)
	{
		this.detailPrintOption = detailPrintOption;
	}

	protected void setPrintOptionTitle(final PrintOption titlePrintOption)
	{
		this.titlePrintOption = titlePrintOption;
	}

	// protected Collection<String> prepareArea(final IPrintable printable)
	// {
	// final Collection<String> area = new ArrayList<String>();
	//
	// final Collection<String> lines = this.getPattern();
	// final String[] markers = this.getMarkers(lines);
	//
	// if ((markers.length > 0) ||
	// this.getPrintOption().equals(PrintOption.ALWAYS))
	// {
	// for (String line : lines)
	// {
	// for (final String marker : markers)
	// {
	// line = this.replace(printable, marker, line);
	// }
	// area.add(line);
	// }
	// }
	// return area;
	// }

	protected void setPrintOptionTotal(final PrintOption totalPrintOption)
	{
		this.totalPrintOption = totalPrintOption;
	}

	private String formatDate(final Date date)
	{
		return AbstractLayoutSection.dateFormatter.format(date);
	}

	private String formatTime(final Date date)
	{
		return AbstractLayoutSection.timeFormatter.format(date);
	}
}
