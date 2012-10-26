/*
 * Created on 14.05.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SettlementAbstractSinglePosition extends SettlementAbstractPosition
{
	@ManyToOne(optional = false)
	@JoinColumn(name = "sepo_po_id", referencedColumnName = "po_id")
	protected Position position;

	@Basic
	@Column(name = "sepo_position_id")
	protected Long positionId;

	protected SettlementAbstractSinglePosition()
	{
		super();
	}

	protected SettlementAbstractSinglePosition(final Settlement settlement)
	{
		super(settlement, null, null);
	}

	protected SettlementAbstractSinglePosition(final Settlement settlement, final Position position)
	{
		super(settlement, position.getProductGroup(), position.getReceipt().getDefaultCurrency());
		this.setPosition(position);
	}

	public void setPosition(Position position)
	{
		this.position = position;
		this.positionId = position.getId();
		this.setDefaultCurrencyAmount(position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
				Position.AmountType.NETTO));
	}

	public Position getPosition()
	{
		return position;
	}

	public void setPositionId(Long positionId)
	{
		this.positionId = positionId;
	}

	public Long getPositionId()
	{
		return positionId;
	}
}
