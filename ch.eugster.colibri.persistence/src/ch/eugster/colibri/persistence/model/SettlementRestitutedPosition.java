/*
 * Created on 14.05.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "sepo_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "sepo_version")),
		@AttributeOverride(name = "update", column = @Column(name = "sepo_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "sepo_deleted")) })
@Table(name = "colibri_settlement_position")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "sepo_type", discriminatorType = DiscriminatorType.STRING, length = 1)
@DiscriminatorValue(value = "R")
public class SettlementRestitutedPosition extends SettlementAbstractSinglePosition implements
		Comparable<SettlementRestitutedPosition>
{
	@Basic
	@Column(name = "sepo_code")
	private String code;

	@Basic
	@Column(name = "sepo_text")
	private String text;

	protected SettlementRestitutedPosition()
	{
		super();
	}

	protected SettlementRestitutedPosition(final Settlement settlement, final Position position)
	{
		super(settlement, position);
		this.setCode(position.getProduct() == null ? position.getProductGroup().getCode() : position.getProduct()
				.getCode());
		this.setText(position.getProduct() == null ? position.getProductGroup().getName() : position.getProduct()
				.getAuthorAndTitleShortForm());
	}

	@Override
	public int compareTo(final SettlementRestitutedPosition other)
	{
		return this.getId().compareTo(other.getId());
	}

	public static SettlementRestitutedPosition newInstance(final Settlement settlement, final Position position)
	{
		return (SettlementRestitutedPosition) AbstractEntity.newInstance(new SettlementRestitutedPosition(settlement,
				position));
	}

	@Override
	public void setCode(String code)
	{
		this.code = code;
	}

	@Override
	public String getCode()
	{
		return valueOf(code);
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return valueOf(text);
	}

}
