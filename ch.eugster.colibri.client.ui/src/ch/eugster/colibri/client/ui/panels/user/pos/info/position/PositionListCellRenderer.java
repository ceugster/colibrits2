/*
 * Created on 14.03.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.position;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Position;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PositionListCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 0l;

	private final UserPanel userPanel;

	private final Color normal;

	private final Color back;

	private final Color expense;

	public PositionListCellRenderer(final UserPanel userPanel, final Color normal, final Color back, final Color expense)
	{
		super();
		this.userPanel = userPanel;
		this.normal = normal;
		this.back = back;
		this.expense = expense;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column)
	{
		final Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (isNegativePriceValue(row))
		{
			result.setForeground(isSelected ? Color.GREEN : expense);
		}
		else if (isNegativeQuantityValue(row))
		{
			result.setForeground(isSelected ? Color.MAGENTA : back);
		}
		else
		{
			result.setForeground(isSelected ? Color.DARK_GRAY : normal);
		}

		return result;
	}

	private boolean isNegativePriceValue(final int row)
	{
		final Position[] positions = userPanel.getReceiptWrapper().getReceipt().getPositions().toArray(new Position[0]);
		if ((row >= 0) && (row < positions.length))
		{
			return positions[row].getPrice() < 0d;
		}
		else
		{
			return false;
		}
	}

	private boolean isNegativeQuantityValue(final int row)
	{
		final Position[] positions = userPanel.getReceiptWrapper().getReceipt().getPositions().toArray(new Position[0]);
		if ((row >= 0) && (row < positions.length))
		{
			return positions[row].getQuantity() < 0;
		}
		else
		{
			return false;
		}
	}
}
