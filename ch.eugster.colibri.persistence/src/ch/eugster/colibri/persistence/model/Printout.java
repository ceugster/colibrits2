package ch.eugster.colibri.persistence.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Convert;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "pn_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "pn_version")),
		@AttributeOverride(name = "update", column = @Column(name = "pn_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "pn_deleted")) })
@Table(name = "colibri_printout")
public class Printout extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "pn_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pn_id")
	@TableGenerator(name = "pn_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(cascade = CascadeType.ALL, optional = true)
	@JoinColumn(name = "pn_pn_id")
	private Printout printout;

	@ManyToOne(optional = true)
	@JoinColumn(name = "pn_sp_id")
	private Salespoint salespoint;

	@Basic
	@Column(name = "pn_automatic_print")
	@Convert("booleanConverter")
	private boolean automaticPrint;

	@OneToOne(optional = true)
	@JoinColumn(name = "pn_rp_id")
	private ReceiptPrinterSettings receiptPrinterSettings;

	/*
	 * Bereiche einer Druckvorlage. Im Falle eines Belegs z.B. Header,
	 * Positionen, Subtotal, Zahlungen, Total Zahlungen, Mehrwertsteuer, Fuss
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "printout")
	@MapKey(name = "printAreaType")
	private Map<Integer, PrintoutArea> printoutAreas = new HashMap<Integer, PrintoutArea>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "printout")
	@MapKey(name = "printout")
	private Collection<Printout> printouts = new Vector<Printout>();

	@Basic
	@Column(name = "pn_printout_type")
	private String printoutType;

	protected Printout()
	{
		super();
	}

	/**
	 * 
	 * @param printoutType
	 * @param receiptPrinterSettings
	 * 
	 *            this printout is a template devoted to a receipt printer
	 */
	protected Printout(final String printoutType, final ReceiptPrinterSettings receiptPrinterSettings)
	{
		super();
		this.setPrintoutType(printoutType);
		this.setReceiptPrinterSettings(receiptPrinterSettings);
	}

	/**
	 * 
	 * @param printoutType
	 * @param salespoint
	 * 
	 *            this printout is a printout for one salespoint
	 */
	protected Printout(final String printoutType, final Salespoint salespoint)
	{
		super();
		this.setPrintoutType(printoutType);
		this.setSalespoint(salespoint);
	}

	public void addPrintout(final Printout printout)
	{
		this.propertyChangeSupport.firePropertyChange("printouts", this.printouts, this.printouts.add(printout));
	}

	public void addPrintoutArea(final PrintoutArea printoutArea)
	{
		PrintoutArea pa = this.printoutAreas.get(Integer.valueOf(printoutArea.getPrintAreaType()));
		if (pa == null)
		{
			pa = printoutArea;
		}
		else if (pa.isDeleted())
		{
			pa.setDeleted(false);
		}
		this.propertyChangeSupport.firePropertyChange("printoutAreas", this.printoutAreas,
				this.printoutAreas.put(Integer.valueOf(printoutArea.getPrintAreaType()), pa));
	}

	public Collection<Printout> getChildren()
	{
		return this.printouts;
	}

	public int getColumns()
	{
		if (this.getSalespoint() == null)
		{
			return this.receiptPrinterSettings.getCols();
		}
		else if (this.getSalespoint().getReceiptPrinterSettings() == null)
		{
			return this.receiptPrinterSettings.getCols();
		}
		else
		{
			return this.getSalespoint().getReceiptPrinterSettings().getCols();
		}
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public Printout getParent()
	{
		return this.printout;
	}

	public PrintoutArea getPrintoutArea(final Integer printoutAreaType)
	{
		return this.printoutAreas.get(printoutAreaType);
	}

	public Map<Integer, PrintoutArea> getPrintoutAreas()
	{
		return this.printoutAreas;
	}

	public String getPrintoutType()
	{
		return this.printoutType;
	}

	public ReceiptPrinterSettings getReceiptPrinterSettings()
	{
		return this.receiptPrinterSettings;
	}

	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	public SalespointReceiptPrinterSettings getSalespointReceiptPrinterSettings()
	{
		return this.salespoint == null ? null : this.salespoint.getReceiptPrinterSettings();
	}

	public boolean hasParent()
	{
		return this.printout instanceof Printout;
	}

	public boolean isAutomaticPrint()
	{
		return this.automaticPrint;
	}

	public boolean isSalespointSpecific()
	{
		return this.salespoint instanceof Salespoint;
	}

	public void putPrintoutArea(final PrintoutArea printoutArea)
	{
		this.propertyChangeSupport.firePropertyChange("printoutAreas", this.printoutAreas,
				this.printoutAreas.put(printoutArea.getPrintAreaType(), printoutArea));
	}

	public void removePrintout(final Printout printout)
	{
		this.propertyChangeSupport.firePropertyChange("printouts", this.printouts, this.printouts.remove(printout));
	}

	public void removePrintoutArea(final PrintoutArea printoutArea)
	{
		final PrintoutArea pa = this.printoutAreas.get(printoutArea.getPrintAreaType());
		pa.setDeleted(true);
		this.propertyChangeSupport.firePropertyChange("printoutAreas", this.printoutAreas,
				this.printoutAreas.put(printoutArea.getPrintAreaType(), pa));
	}

	public void setAutomaticPrint(final boolean automaticPrint)
	{
		this.automaticPrint = automaticPrint;
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", id, this.id = id);
	}

	public void setParent(final Printout printout)
	{
		this.propertyChangeSupport.firePropertyChange("printout", printout, this.printout = printout);
		if (printout != null)
		{
			this.setPrintoutType(printout.getPrintoutType());
			this.setReceiptPrinterSettings(printout.getReceiptPrinterSettings());
		}
	}

	public void setPrintoutAreas(final Map<Integer, PrintoutArea> printoutAreas)
	{
		this.propertyChangeSupport.firePropertyChange("printoutAreas", printoutAreas, this.printoutAreas = printoutAreas);
	}

	public void setPrintouts(final Collection<Printout> printouts)
	{
		this.propertyChangeSupport.firePropertyChange("printouts", printouts, this.printouts = printouts);
	}

	public void setPrintoutType(final String printoutType)
	{
		this.propertyChangeSupport.firePropertyChange("printoutType", printoutType, this.printoutType = printoutType);
	}

	public void setReceiptPrinterSettings(final ReceiptPrinterSettings receiptPrinterSettings)
	{
		this.propertyChangeSupport.firePropertyChange("receiptPrinterSettings", receiptPrinterSettings,
				this.receiptPrinterSettings = receiptPrinterSettings);
	}

	public void setSalespoint(final Salespoint salespoint)
	{
		this.propertyChangeSupport.firePropertyChange("salespoint", salespoint, this.salespoint = salespoint);
		if (salespoint.getReceiptPrinterSettings() != null)
		{
			this.setReceiptPrinterSettings(salespoint.getReceiptPrinterSettings().getReceiptPrinterSettings());
		}
	}

	public static Printout newInstance(final String printoutType, final ReceiptPrinterSettings receiptPrinterSettings)
	{
		return (Printout) AbstractEntity.newInstance(new Printout(printoutType, receiptPrinterSettings));
	}

	public static Printout newInstance(final String printoutType, final Salespoint salespoint)
	{
		return (Printout) AbstractEntity.newInstance(new Printout(printoutType, salespoint));
	}
}
