package ch.eugster.colibri.persistence.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "colibri_receipt_printer_settings")
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "rp_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "rp_version")),
		@AttributeOverride(name = "update", column = @Column(name = "rp_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "rp_deleted")) })
public class ReceiptPrinterSettings extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "rp_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "rp_id")
	@TableGenerator(name = "rp_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@Basic
	@Column(name = "rp_name")
	private String name;

	@Basic
	@Column(name = "rp_port")
	private String port;

	@Basic
	@Lob
	@Column(name = "rp_converter")
	private String converter;

	@Basic
	@Column(name = "rp_cols")
	private int cols;

	@Basic
	@Column(name = "rp_lines_before_cut")
	private int linesBeforeCut;

	@Basic
	@Column(name = "rp_component_name")
	private String componentName;

	@OneToMany
	@MapKey(name = "printoutType")
	private Map<String, Printout> printouts = new HashMap<String, Printout>();

	@OneToMany(mappedBy = "receiptPrinterSettings")
	private List<SalespointReceiptPrinterSettings> salespointReceiptPrinters = new Vector<SalespointReceiptPrinterSettings>();

	protected ReceiptPrinterSettings()
	{
		super();
	}

	public int getCols()
	{
		return this.cols;
	}

	public String getComponentName()
	{
		return this.componentName;
	}

	public String getConverter()
	{
		if (this.converter instanceof String)
		{
			return this.converter;
		}
		return "";
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public int getLinesBeforeCut()
	{
		return this.linesBeforeCut;
	}

	public String getName()
	{
		return this.name;
	}

	public String getPort()
	{
		return this.port;
	}

	public Printout getPrintout(final String printoutType)
	{
		return this.printouts.get(printoutType);
	}

	public SalespointReceiptPrinterSettings getSalespointReceiptPrinter(final Salespoint salespoint)
	{
		for (SalespointReceiptPrinterSettings setting : this.salespointReceiptPrinters)
		{
			if (setting.getSalespoint().getId().equals(salespoint.getId()))
			{
				return setting;
			}
		}
		return null;
	}

	public List<SalespointReceiptPrinterSettings> getSalespointReceiptPrinters()
	{
		return this.salespointReceiptPrinters;
	}

	public void putPrintout(final String printoutType, final Printout printout)
	{
		this.propertyChangeSupport.firePropertyChange("printouts", this.printouts,
				this.printouts.put(printoutType, printout));
	}

	public void setCols(final int cols)
	{
		this.propertyChangeSupport.firePropertyChange("cols", cols, this.cols = cols);
	}

	public void setComponentName(final String componentName)
	{
		this.propertyChangeSupport.firePropertyChange("componentName", componentName,
				this.componentName = componentName);
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
		this.linesBeforeCut = linesBeforeCut;
	}

	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	public void setPort(final String port)
	{
		this.propertyChangeSupport.firePropertyChange("port", port, this.port = port);
	}

	public static ReceiptPrinterSettings newInstance()
	{
		return (ReceiptPrinterSettings) AbstractEntity.newInstance(new ReceiptPrinterSettings());
	}
}
