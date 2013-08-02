/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "key_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "key_version")),
		@AttributeOverride(name = "update", column = @Column(name = "key_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "key_deleted")) })
@Table(name = "colibri_key")
public class Key extends AbstractEntity implements Comparable<Key>, IReplicatable
{
	@Transient
	public PaymentType paymentType;
	
	@Id
	@Column(name = "key_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "key_id")
	@TableGenerator(name = "key_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "key_tab_id", referencedColumnName = "tab_id")
	private Tab tab;

	@Basic
	@Column(name = "key_tab_row")
	private int tabRow;

	@Basic
	@Column(name = "key_tab_col")
	private int tabCol;

	@Basic
	@Column(name = "key_label")
	private String label;

	@Basic
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "key_function_type")
	private FunctionType functionType;

	@Basic
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "key_key_type")
	private KeyType keyType;

	@Basic
	@Column(name = "key_parent_id")
	private Long parentId;

	@Basic
	@Column(name = "key_value")
	private double value;

	@Basic
	@Column(name = "key_count")
	private int count;

	@Basic
	@Column(name = "key_product_code")
	private String productCode;

	@Basic
	@Column(name = "key_img_id")
	private String imageId;

	@Basic
	@Column(name = "key_img_h_pos")
	private int textImageHorizontalPosition;

	@Basic
	@Column(name = "key_img_v_pos")
	private int textImageVerticalPosition;

	@Basic
	@Column(name = "key_normal_font_size", nullable = true)
	private Float normalFontSize;

	@Basic
	@Column(name = "key_normal_font_style", nullable = true)
	private Integer normalFontStyle;

	@Basic
	@Column(name = "key_normal_h_align", nullable = true)
	private Integer normalHorizontalAlign;

	@Basic
	@Column(name = "key_normal_v_align", nullable = true)
	private Integer normalVerticalAlign;

	@Basic
	@Column(name = "key_normal_fg", nullable = true)
	private Integer normalFg;

	@Basic
	@Column(name = "key_normal_bg", nullable = true)
	private Integer normalBg;

	@Basic
	@Column(name = "key_fail_over_font_size", nullable = true)
	private Float failOverFontSize;

	@Basic
	@Column(name = "key_fail_over_font_style", nullable = true)
	private Integer failOverFontStyle;

	@Basic
	@Column(name = "key_fail_over_h_align", nullable = true)
	private Integer failOverHorizontalAlign;

	@Basic
	@Column(name = "key_fail_over_v_align", nullable = true)
	private Integer failOverVerticalAlign;

	@Basic
	@Column(name = "key_fail_over_fg", nullable = true)
	private Integer failOverFg;

	@Basic
	@Column(name = "key_fail_over_bg", nullable = true)
	private Integer failOverBg;

	private Key()
	{
		super();
	}

	private Key(final Tab tab)
	{
		this();
		this.setTab(tab);
	}

	@Override
	public int compareTo(final Key key)
	{
		if (this.getTabRow() == key.getTabRow())
		{
			return key.getTabCol() - this.getTabCol();
		}
		else
		{
			return key.getTabRow() - this.getTabRow();
		}

	}

	public Key copy()
	{
		final Key target = (Key) super.copy(Key.newInstance(this.getTab()));
		return this.update(target);
	}

	public int getFailOverBg()
	{
		return this.failOverBg == null ? this.getTab().getConfigurable().getProfile().getButtonFailOverBg()
				: this.failOverBg;
	}

	public int getFailOverFg()
	{
		return this.failOverFg == null ? this.getTab().getConfigurable().getProfile().getButtonFailOverFg()
				: this.failOverFg.intValue();
	}

	public float getFailOverFontSize()
	{
		return this.failOverFontSize == null ? this.getTab().getConfigurable().getProfile().getButtonFailOverFontSize()
				: this.failOverFontSize.floatValue();
	}

	public int getFailOverFontStyle()
	{
		return this.failOverFontStyle == null ? this.getTab().getConfigurable().getProfile()
				.getButtonFailOverFontStyle() : this.failOverFontStyle.intValue();
	}

	public int getFailOverHorizontalAlign()
	{
		return this.failOverHorizontalAlign == null ? this.getTab().getConfigurable().getProfile()
				.getButtonFailOverHorizontalAlign() : this.failOverHorizontalAlign.intValue();
	}

	public int getFailOverVerticalAlign()
	{
		return this.failOverVerticalAlign == null ? this.getTab().getConfigurable().getProfile()
				.getButtonFailOverVerticalAlign() : this.failOverVerticalAlign.intValue();
	}

	public FunctionType getFunctionType()
	{
		return this.functionType;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getImageId()
	{
		return this.imageId;
	}

	public KeyType getKeyType()
	{
		return this.keyType;
	}

	public String getLabel()
	{
		return this.valueOf(this.label);
	}

	public Integer getNormalBg()
	{
		return this.normalBg == null ? this.getTab().getConfigurable().getProfile().getButtonNormalBg() : this.normalBg;
	}

	public int getNormalFg()
	{
		return this.normalFg == null ? this.getTab().getConfigurable().getProfile().getButtonNormalFg() : this.normalFg
				.intValue();
	}

	public float getNormalFontSize()
	{
		return this.normalFontSize == null ? this.getTab().getConfigurable().getProfile().getButtonNormalFontSize()
				: this.normalFontSize.floatValue();
	}

	public int getNormalFontStyle()
	{
		return this.normalFontStyle == null ? this.getTab().getConfigurable().getProfile().getButtonNormalFontStyle()
				: this.normalFontStyle.intValue();
	}

	public int getNormalHorizontalAlign()
	{
		return this.normalHorizontalAlign == null ? this.getTab().getConfigurable().getProfile()
				.getButtonNormalHorizontalAlign() : this.normalHorizontalAlign.intValue();
	}

	public int getNormalVerticalAlign()
	{
		return this.normalVerticalAlign == null ? this.getTab().getConfigurable().getProfile()
				.getButtonNormalVerticalAlign() : this.normalVerticalAlign.intValue();
	}

	public Long getParentId()
	{
		return this.parentId;
	}

	public String getProductCode()
	{
		return this.productCode;
	}

	public Tab getTab()
	{
		return this.tab;
	}

	public int getTabCol()
	{
		return this.tabCol;
	}

	public int getTabRow()
	{
		return this.tabRow;
	}

	public int getTextImageHorizontalPosition()
	{
		return this.textImageHorizontalPosition;
	}

	public int getTextImageVerticalPosition()
	{
		return this.textImageVerticalPosition;
	}

	public double getValue()
	{
		return this.value;
	}

	@Override
	public void setDeleted(final boolean deleted)
	{
		super.setDeleted(deleted);
		if (deleted)
		{
			this.setParentId(null);
			this.setProductCode(null);
			this.setFunctionType(FunctionType.values()[0]);
			this.setImageId(null);
			this.setKeyType(KeyType.values()[0]);
			this.setLabel(null);
			this.setValue(0d);
		}
	}

	public void setFailOverBg(final int failOverBg)
	{
		final Integer value = failOverBg == this.getTab().getConfigurable().getProfile().getButtonFailOverBg() ? null
				: Integer.valueOf(failOverBg);
		this.propertyChangeSupport.firePropertyChange("failOverBg", this.failOverBg, this.failOverBg = value);
	}

	public void setFailOverFg(final int failOverFg)
	{
		final Integer value = failOverFg == this.getTab().getConfigurable().getProfile().getButtonFailOverFg() ? null
				: Integer.valueOf(failOverFg);
		this.propertyChangeSupport.firePropertyChange("failOverFg", this.failOverFg, this.failOverFg = value);
	}

	public void setFailOverFontSize(final float failOverFontSize)
	{
		final Float value = failOverFontSize == this.getTab().getConfigurable().getProfile()
				.getButtonFailOverFontSize() ? null : Float.valueOf(failOverFontSize);
		this.propertyChangeSupport.firePropertyChange("failOverFontSize", this.failOverFontSize,
				this.failOverFontSize = value);
	}

	public void setFailOverFontStyle(final int failOverFontStyle)
	{
		final Integer value = failOverFontStyle == this.getTab().getConfigurable().getProfile()
				.getButtonFailOverFontStyle() ? null : Integer.valueOf(failOverFontStyle);
		this.propertyChangeSupport.firePropertyChange("failOverFontStyle", this.failOverFontStyle,
				this.failOverFontStyle = value);
	}

	public void setFailOverHorizontalAlign(final int failOverHorizontalAlign)
	{
		final Integer value = failOverHorizontalAlign == this.getTab().getConfigurable().getProfile()
				.getButtonFailOverHorizontalAlign() ? null : Integer.valueOf(failOverHorizontalAlign);
		this.propertyChangeSupport.firePropertyChange("failOverHorizontalAlign", this.failOverHorizontalAlign,
				this.failOverHorizontalAlign = value);
	}

	public void setFailOverVerticalAlign(final int failOverVerticalAlign)
	{
		final Integer value = failOverVerticalAlign == this.getTab().getConfigurable().getProfile()
				.getButtonFailOverVerticalAlign() ? null : Integer.valueOf(failOverVerticalAlign);
		this.propertyChangeSupport.firePropertyChange("failOverVerticalAlign", this.failOverVerticalAlign,
				this.failOverVerticalAlign = value);
	}

	public void setFunctionType(final FunctionType functionType)
	{
		this.propertyChangeSupport.firePropertyChange("functionType", this.functionType,
				this.functionType = functionType);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setImageId(final String imageId)
	{
		this.propertyChangeSupport.firePropertyChange("imageId", this.imageId, this.imageId = imageId);
	}

	public void setKeyType(final KeyType keyType)
	{
		this.propertyChangeSupport.firePropertyChange("keyType", this.keyType, this.keyType = keyType);
	}

	public void setLabel(final String label)
	{
		this.propertyChangeSupport.firePropertyChange("label", this.label, this.label = label);
	}

	public void setNormalBg(final int normalBg)
	{
		final Integer value = normalBg == this.getTab().getConfigurable().getProfile().getButtonNormalBg() ? null
				: Integer.valueOf(normalBg);
		this.propertyChangeSupport.firePropertyChange("normalBg", this.normalBg, this.normalBg = value);
	}

	public void setNormalFg(final int normalFg)
	{
		final Integer value = normalFg == this.getTab().getConfigurable().getProfile().getButtonNormalFg() ? null
				: Integer.valueOf(normalFg);
		this.propertyChangeSupport.firePropertyChange("normalFg", this.normalFg, this.normalFg = value);
	}

	public void setNormalFontSize(final float normalFontSize)
	{
		final Float value = normalFontSize == this.getTab().getConfigurable().getProfile().getButtonNormalFontSize() ? null
				: Float.valueOf(normalFontSize);
		this.propertyChangeSupport.firePropertyChange("normalFontSize", this.normalFontSize,
				this.normalFontSize = value);
	}

	public void setNormalFontStyle(final int normalFontStyle)
	{
		final Integer value = normalFontStyle == this.getTab().getConfigurable().getProfile()
				.getButtonNormalFontStyle() ? null : Integer.valueOf(normalFontStyle);
		this.propertyChangeSupport.firePropertyChange("normalFontStyle", this.normalFontStyle,
				this.normalFontStyle = value);
	}

	public void setNormalHorizontalAlign(final int normalHorizontalAlign)
	{
		final Integer value = normalHorizontalAlign == this.getTab().getConfigurable().getProfile()
				.getButtonNormalHorizontalAlign() ? null : Integer.valueOf(normalHorizontalAlign);
		this.propertyChangeSupport.firePropertyChange("normalHorizontalAlign", this.normalHorizontalAlign,
				this.normalHorizontalAlign = value);
	}

	public void setNormalVerticalAlign(final int normalVerticalAlign)
	{
		final Integer value = normalVerticalAlign == this.getTab().getConfigurable().getProfile()
				.getButtonNormalVerticalAlign() ? null : Integer.valueOf(normalVerticalAlign);
		this.propertyChangeSupport.firePropertyChange("normalVerticalAlign", this.normalVerticalAlign,
				this.normalVerticalAlign = value);
	}

	public void setParentId(final Long parentId)
	{
		this.propertyChangeSupport.firePropertyChange("parentId", this.parentId, this.parentId = parentId);
	}

	public void setProductCode(final String productCode)
	{
		this.propertyChangeSupport.firePropertyChange("productCode", this.productCode, this.productCode = productCode);
	}

	public void setTab(final Tab tab)
	{
		this.propertyChangeSupport.firePropertyChange("tab", this.tab, this.tab = tab);
	}

	public void setTabCol(final int tabCol)
	{
		this.propertyChangeSupport.firePropertyChange("tabCol", this.tabCol, this.tabCol = tabCol);
	}

	public void setTabRow(final int tabRow)
	{
		this.propertyChangeSupport.firePropertyChange("tabRow", this.tabRow, this.tabRow = tabRow);
	}

	public void setTextImageHorizontalPosition(final int textImageHorizontalPosition)
	{
		this.propertyChangeSupport.firePropertyChange("textImageHorizontalPosition", this.textImageHorizontalPosition,
				this.textImageHorizontalPosition = textImageHorizontalPosition);
	}

	public void setTextImageVerticalPosition(final int textImageVerticalPosition)
	{
		this.propertyChangeSupport.firePropertyChange("textImageVerticalPosition", this.textImageVerticalPosition,
				this.textImageVerticalPosition = textImageVerticalPosition);
	}

	public void setValue(final double value)
	{
		this.propertyChangeSupport.firePropertyChange("value", this.value, this.value = value);
	}

	public Key update(final Key target)
	{
		target.setTab(this.getTab());
		target.setTabRow(this.getTabRow());
		target.setTabCol(this.getTabCol());
		target.setLabel(this.getLabel());
		target.setFunctionType(this.getFunctionType());
		target.setValue(this.getValue());
		target.setImageId(this.getImageId());
		target.setTextImageHorizontalPosition(this.getTextImageHorizontalPosition());
		target.setTextImageVerticalPosition(this.getTextImageVerticalPosition());
		target.setNormalFontSize(this.getNormalFontSize());
		target.setNormalFontStyle(this.getNormalFontStyle());
		target.setNormalHorizontalAlign(this.getNormalHorizontalAlign());
		target.setNormalVerticalAlign(this.getNormalVerticalAlign());
		target.setNormalFg(this.getNormalFg());
		target.setNormalBg(this.getNormalBg());
		target.setFailOverFontSize(this.getFailOverFontSize());
		target.setFailOverFontStyle(this.getFailOverFontStyle());
		target.setFailOverHorizontalAlign(this.getFailOverHorizontalAlign());
		target.setFailOverVerticalAlign(this.getFailOverVerticalAlign());
		target.setFailOverFg(this.getFailOverFg());
		target.setFailOverBg(this.getFailOverBg());
		target.setProductCode(this.getProductCode());
		target.setKeyType(this.getKeyType());
		target.setCount(this.getCount());
		target.setParentId(this.getParentId());
		return target;
	}

	public static Key newInstance(final Tab tab)
	{
		final Key key = (Key) AbstractEntity.newInstance(new Key(tab));
		key.setFailOverBg(tab.getConfigurable().getProfile().getButtonFailOverBg());
		key.setFailOverFg(tab.getConfigurable().getProfile().getButtonFailOverFg());
		key.setFailOverFontSize(tab.getConfigurable().getProfile().getButtonFailOverFontSize());
		key.setFailOverFontStyle(tab.getConfigurable().getProfile().getButtonFailOverFontStyle());
		key.setFailOverHorizontalAlign(tab.getConfigurable().getProfile().getButtonFailOverHorizontalAlign());
		key.setFailOverVerticalAlign(tab.getConfigurable().getProfile().getButtonFailOverVerticalAlign());
		key.setNormalBg(tab.getConfigurable().getProfile().getButtonNormalBg());
		key.setNormalFg(tab.getConfigurable().getProfile().getButtonNormalFg());
		key.setNormalFontSize(tab.getConfigurable().getProfile().getButtonNormalFontSize());
		key.setNormalFontStyle(tab.getConfigurable().getProfile().getButtonNormalFontStyle());
		key.setNormalHorizontalAlign(tab.getConfigurable().getProfile().getButtonNormalHorizontalAlign());
		key.setNormalVerticalAlign(tab.getConfigurable().getProfile().getButtonNormalVerticalAlign());
		return key;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.propertyChangeSupport.firePropertyChange("count", this.count, this.count = count);
	}
}
