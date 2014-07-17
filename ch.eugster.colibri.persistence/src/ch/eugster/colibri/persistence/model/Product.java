/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import java.util.Calendar;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Index;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "pd_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "pd_version")),
		@AttributeOverride(name = "update", column = @Column(name = "pd_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "pd_deleted")) })
@Table(name = "colibri_product")
public class Product extends AbstractEntity
{
	@Id
	@Column(name = "pd_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pd_id")
	@TableGenerator(name = "pd_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@OneToOne(optional = false)
	@JoinColumn(name = "pd_po_id", referencedColumnName = "po_id")
	private Position position;

	@OneToOne(optional = false)
	@JoinColumn(name = "pd_epg_id", referencedColumnName = "epg_id")
	private ExternalProductGroup externalProductGroup;

	@Basic
	@Column(name = "pd_code")
	private String code;

	@Basic
	@Temporal(TemporalType.DATE)
	@Column(name = "pd_invoice_date")
	private Calendar invoiceDate;

	@Basic
	@Index
	@Column(name = "pd_invoice_number")
	private String invoiceNumber;

	@Basic
	@Column(name = "pd_author")
	private String author;

	@Basic
	@Column(name = "pd_title")
	private String title;

	@Basic
	@Column(name = "pd_publisher")
	private String publisher;

	protected Product()
	{
		super();
	}

	protected Product(final Position position)
	{
		this();
		this.setPosition(position);
	}

	public String getAuthor()
	{
		return this.valueOf(this.author);
	}

	public String getAuthorAndTitleShortForm()
	{
		StringBuilder title = new StringBuilder();
		if (this.getAuthor() != null && !this.getAuthor().isEmpty())
		{
			title = title.append(this.getAuthorShortform());
			if (this.getTitle() != null && !this.getTitle().isEmpty())
			{
				title = title.append(", ");
			}
		}
		if (this.getTitle() != null && !this.getTitle().isEmpty())
		{
			title = title.append(this.getTitle());
		}

		return title.toString().trim();
	}

	public String getTitleAndAuthorShortForm()
	{
		StringBuilder title = new StringBuilder();
		if (this.getTitle() != null && !this.getTitle().isEmpty())
		{
			title = title.append(this.getTitle());
			if (this.getTitle() != null && !this.getTitle().isEmpty())
			{
				title = title.append(", ");
			}
		}
		if (this.getAuthor() != null && !this.getAuthor().isEmpty())
		{
			title = title.append(this.getAuthorShortform());
		}
		if (title.toString().trim().isEmpty())
		{
			title = title.append(this.getCode().trim());
		}
		if (title.toString().trim().isEmpty())
		{
			if (this.getExternalProductGroup() == null)
			{
				title = title.append(this.getPosition().getProductGroup().getName().trim());
			}
			else
			{
				title = title.append(this.getExternalProductGroup().getProductGroupMapping().getProductGroup().getName().trim());
			}
		}
		return title.toString().trim();
	}

	public String getTitleAndAuthorShortFormNoCode()
	{
		StringBuilder title = new StringBuilder();
		if (this.getTitle() != null && !this.getTitle().isEmpty())
		{
			title = title.append(this.getTitle());
			if (this.getTitle() != null && !this.getTitle().isEmpty())
			{
				title = title.append(", ");
			}
		}
		if (this.getAuthor() != null && !this.getAuthor().isEmpty())
		{
			title = title.append(this.getAuthorShortform());
		}
		if (title.toString().trim().isEmpty())
		{
			if (this.getExternalProductGroup() == null)
			{
				title = title.append(this.getPosition().getProductGroup().getName().trim());
			}
			else
			{
				title = title.append(this.getExternalProductGroup().getProductGroupMapping().getProductGroup().getName().trim());
			}
		}
		return title.toString().trim();
	}

	public String getCode()
	{
		return this.valueOf(this.code);
	}

	public ExternalProductGroup getExternalProductGroup()
	{
		return this.externalProductGroup;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public Calendar getInvoiceDate()
	{
		return this.invoiceDate;
	}

	public String getInvoiceNumber()
	{
		return this.invoiceNumber;
	}

	public Position getPosition()
	{
		return this.position;
	}

	public String getPublisher()
	{
		return this.valueOf(this.publisher);
	}

	public String getTitle()
	{
		return this.valueOf(this.title);
	}

	public void setAuthor(final String author)
	{
		this.propertyChangeSupport.firePropertyChange("author", this.author, this.author = author);
	}

	public void setCode(final String code)
	{
		this.propertyChangeSupport.firePropertyChange("code", this.code, this.code = code);
	}

	public void setExternalProductGroup(final ExternalProductGroup externalProductGroup)
	{
		this.propertyChangeSupport.firePropertyChange("externalProductGroup", this.externalProductGroup,
				this.externalProductGroup = externalProductGroup);

//		if (this.externalProductGroup != null && this.getPosition().getProductGroup() == null)
		if (this.externalProductGroup != null)
		{
			if ((externalProductGroup.getProductGroupMapping() != null)
					&& !externalProductGroup.getProductGroupMapping().isDeleted())
			{
				final ProductGroup productGroup = this.externalProductGroup.getProductGroupMapping().getProductGroup();
				if ((productGroup != null) && !productGroup.isDeleted())
				{
					this.getPosition().setProductGroup(productGroup);
				}
			}
		}
		if (this.getPosition().getProductGroup() == null)
		{
			this.getPosition().setProductGroup(
					this.getPosition().getReceipt().getSettlement().getSalespoint().getCommonSettings()
							.getDefaultProductGroup());
		}
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setInvoiceDate(final Calendar invoiceDate)
	{
		this.propertyChangeSupport.firePropertyChange("invoiceDate", this.invoiceDate, this.invoiceDate = invoiceDate);
	}

	public void setInvoiceNumber(final String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	public void setPosition(final Position position)
	{
		this.position = position;
	}

	public void setPublisher(final String publisher)
	{
		this.propertyChangeSupport.firePropertyChange("publisher", this.publisher, this.publisher = publisher);
	}

	public void setTitle(final String title)
	{
		this.propertyChangeSupport.firePropertyChange("title", this.title, this.title = title);
	}

	private String getAuthorShortform()
	{
		final int index = this.getAuthor().indexOf(",");
		if (index > -1)
		{
			return this.getAuthor().substring(0, index).trim();
		}

		return this.getAuthor().trim();
	}

	public static Product newInstance()
	{
		final Product product = (Product) AbstractEntity.newInstance(new Product());
		return product;
	}

	public static Product newInstance(final Position position)
	{
		return (Product) AbstractEntity.newInstance(new Product(position));
	}
}
