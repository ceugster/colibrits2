package ch.eugster.colibri.persistence.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "colibri_salespoint_receipt_printer_settings")
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "srp_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "srp_version")),
		@AttributeOverride(name = "update", column = @Column(name = "srp_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "srp_deleted")) })
public class SalespointReceiptPrinterSettings extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "srp_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "srp_id")
	@TableGenerator(name = "srp_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = true)
	@JoinColumn(name = "srp_rp_id", referencedColumnName = "rp_id")
	private ReceiptPrinterSettings receiptPrinterSettings;

	@ManyToOne(optional = true)
	@JoinColumn(name = "srp_sp_id", referencedColumnName = "sp_id")
	private Salespoint salespoint;

	@Basic
	@Column(name = "srp_lines_before_cut")
	private Integer linesBeforeCut;

	@Basic
	@Column(name = "srp_port")
	private String port;

	@Basic
	@Lob
	@Column(name = "srp_converter")
	private String converter;

	@Basic
	@Column(name = "srp_cols")
	private Integer cols;

	protected SalespointReceiptPrinterSettings()
	{
		super();
	}

	protected SalespointReceiptPrinterSettings(final ReceiptPrinterSettings receiptPrinterSettings, final Salespoint salespoint)
	{
		this.receiptPrinterSettings = receiptPrinterSettings;
		this.salespoint = salespoint;
	}

	public int getCols()
	{
		if (this.cols == null)
		{
			return this.receiptPrinterSettings.getCols();
		}
		return this.cols.intValue();
	}

	public String getComponentName()
	{
		return this.receiptPrinterSettings.getComponentName();
	}

	public String getConverter()
	{
		if (this.converter instanceof String)
		{
			return this.converter;
		}
		return this.receiptPrinterSettings.getConverter();
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public int getLinesBeforeCut()
	{
		if (this.linesBeforeCut == null)
		{
			return this.getReceiptPrinterSettings().getLinesBeforeCut();
		}
		return this.linesBeforeCut.intValue();
	}

	public String getName()
	{
		return this.receiptPrinterSettings.getName();
	}

	public String getPort()
	{
		if (this.port == null)
		{
			return this.getReceiptPrinterSettings().getPort();
		}
		return this.port;
	}

	public ReceiptPrinterSettings getReceiptPrinterSettings()
	{
		return this.receiptPrinterSettings;
	}

	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	public void setCols(final int cols)
	{
		final Integer columns = cols == this.getReceiptPrinterSettings().getCols() ? null : Integer.valueOf(cols);
		this.propertyChangeSupport.firePropertyChange("cols", this.cols, this.cols = columns);
	}

	public void setConverter(final String converter)
	{
		this.propertyChangeSupport.firePropertyChange("converter", converter, this.converter = converter);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setLinesBeforeCut(final int linesBeforeCut)
	{
		final Integer beforeCut = linesBeforeCut == this.getReceiptPrinterSettings().getLinesBeforeCut() ? null : Integer.valueOf(linesBeforeCut);
		this.propertyChangeSupport.firePropertyChange("linesBeforeCut", this.linesBeforeCut, this.linesBeforeCut = beforeCut);
	}

	public void setPort(String port)
	{
		if (port == this.getReceiptPrinterSettings().getPort())
		{
			port = null;
		}
		this.propertyChangeSupport.firePropertyChange("port", port, this.port = port);
	}

	public void setReceiptPrinterSettings(final ReceiptPrinterSettings receiptPrinterSettings)
	{
		this.propertyChangeSupport.firePropertyChange("receiptPrinterSettings", this.receiptPrinterSettings,
				this.receiptPrinterSettings = receiptPrinterSettings);
	}

	public void setSalespoint(final Salespoint salespoint)
	{
		this.propertyChangeSupport.firePropertyChange("salespoint", this.salespoint, this.salespoint = salespoint);
	}

	public static SalespointReceiptPrinterSettings newInstance()
	{
		return (SalespointReceiptPrinterSettings) AbstractEntity.newInstance(new SalespointReceiptPrinterSettings());
	}

	public static SalespointReceiptPrinterSettings newInstance(final ReceiptPrinterSettings receiptPrinterSettings, final Salespoint salespoint)
	{
		return (SalespointReceiptPrinterSettings) AbstractEntity.newInstance(new SalespointReceiptPrinterSettings(receiptPrinterSettings,
				salespoint));
	}

}
