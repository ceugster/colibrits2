package ch.eugster.colibri.persistence.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "paa_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "paa_version")),
		@AttributeOverride(name = "update", column = @Column(name = "paa_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "paa_deleted")) })
@Table(name = "colibri_printout_area")
public class PrintoutArea extends AbstractEntity implements Comparable<PrintoutArea>, IReplicationRelevant
{
	@Id
	@Column(name = "paa_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "paa_id")
	@TableGenerator(name = "paa_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = true)
	@JoinColumn(name = "paa_pn_id")
	private Printout printout;

	@Basic
	@Column(name = "paa_print_area_type")
	protected int printAreaType;

	@Basic
	@Lob
	@Column(name = "paa_title_pattern")
	private String titlePattern;

	@Basic
	@Lob
	@Column(name = "paa_detail_pattern")
	private String detailPattern;

	@Basic
	@Lob
	@Column(name = "paa_total_pattern")
	private String totalPattern;

	@Basic
	@Column(name = "paa_print_title")
	@Enumerated(EnumType.ORDINAL)
	private PrintOption titlePrintOption;

	@Basic
	@Column(name = "paa_print_detail")
	@Enumerated(EnumType.ORDINAL)
	private PrintOption detailPrintOption;

	@Basic
	@Column(name = "paa_print_total")
	@Enumerated(EnumType.ORDINAL)
	private PrintOption totalPrintOption;

	protected PrintoutArea()
	{
		super();
	}

	protected PrintoutArea(final Printout printout, final int printAreaType)
	{
		this.setPrintout(printout);
		this.setPrintAreaType(printAreaType);
	}

	@Override
	public int compareTo(final PrintoutArea other)
	{
		return this.printAreaType - other.getPrintAreaType();
	}

	public String getDetailPattern()
	{
		return this.valueOf(this.detailPattern);
	}

	public PrintOption getDetailPrintOption()
	{
		return this.detailPrintOption;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getPattern(final int area)
	{
		switch (area)
		{
			case 0:
			{
				return this.getTitlePattern();
			}
			case 1:
			{
				return this.getDetailPattern();
			}
			case 2:
			{
				return this.getTotalPattern();
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	public int getPrintAreaType()
	{
		return this.printAreaType;
	}

	public PrintoutArea.PrintOption getPrintOption(final int area)
	{
		switch (area)
		{
			case 0:
			{
				return this.getTitlePrintOption();
			}
			case 1:
			{
				return this.getDetailPrintOption();
			}
			case 2:
			{
				return this.getTotalPrintOption();
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	public Printout getPrintout()
	{
		return this.printout;
	}

	public String getTitlePattern()
	{
		return this.valueOf(this.titlePattern);
	}

	public PrintOption getTitlePrintOption()
	{
		return this.titlePrintOption;
	}

	public String getTotalPattern()
	{
		return this.valueOf(this.totalPattern);
	}

	public PrintOption getTotalPrintOption()
	{
		return this.totalPrintOption;
	}

	public void setDetailPattern(final String pattern)
	{
		this.propertyChangeSupport.firePropertyChange("detailPattern", this.detailPattern, this.detailPattern = pattern);
	}

	public void setDetailPrintOption(final PrintOption printOption)
	{
		this.detailPrintOption = printOption;
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setPattern(final int area, final String pattern)
	{
		switch (area)
		{
			case 0:
			{
				this.setTitlePattern(pattern);
				break;
			}
			case 1:
			{
				this.setDetailPattern(pattern);
				break;
			}
			case 2:
			{
				this.setTotalPattern(pattern);
				break;
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	public void setPrintAreaType(final int printAreaType)
	{
		this.propertyChangeSupport.firePropertyChange("printAreaType", this.printAreaType, this.printAreaType = printAreaType);
	}

	public void setPrintOption(final int area, final PrintOption printOption)
	{
		switch (area)
		{
			case 0:
			{
				this.setTitlePrintOption(printOption);
				break;
			}
			case 1:
			{
				this.setDetailPrintOption(printOption);
				break;
			}
			case 2:
			{
				this.setTotalPrintOption(printOption);
				break;
			}
			default:
			{
				throw new RuntimeException("Invalid area");
			}
		}
	}

	public void setPrintOptionDetail(final PrintOption printOption)
	{
		this.detailPrintOption = printOption;
	}

	public void setPrintOptionTitle(final PrintOption printOption)
	{
		this.titlePrintOption = printOption;
	}

	public void setPrintout(final Printout printout)
	{
		this.propertyChangeSupport.firePropertyChange("printout", this.printout, this.printout = printout);
	}

	public void setTitlePattern(final String pattern)
	{
		this.propertyChangeSupport.firePropertyChange("titlePattern", this.titlePattern, this.titlePattern = pattern);
	}

	public void setTitlePrintOption(final PrintOption printOption)
	{
		this.titlePrintOption = printOption;
	}

	public void setTotalPattern(final String pattern)
	{
		this.propertyChangeSupport.firePropertyChange("totalPattern", this.totalPattern, this.totalPattern = pattern);
	}

	public void setTotalPrintOption(final PrintOption printOption)
	{
		this.totalPrintOption = printOption;
	}

	public static PrintoutArea newInstance(final Printout printout, final int printAreaType)
	{
		return (PrintoutArea) AbstractEntity.newInstance(new PrintoutArea(printout, printAreaType));
	}

	public enum PrintOption
	{
		ALWAYS, OPTIONALLY, NEVER;

		public String label()
		{
			switch (this)
			{
				case ALWAYS:
				{
					return "Immer drucken";
				}
				case OPTIONALLY:
				{
					return "Nur wenn vorhanden";
				}
				case NEVER:
				{
					return "Nicht drucken";
				}
				default:
				{
					throw new RuntimeException("Invalid print option");
				}
			}
		}
	}
}
