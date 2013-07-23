package ch.eugster.colibri.display.simple.area;

import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.display.area.ILayoutAreaType;

public enum SimpleLayoutAreaType implements ILayoutAreaType
{
	SALESPOINT_CLOSED_MESSAGE, WELCOME_MESSAGE, POSITION_ADDED_MESSAGE, PAYMENT_ADDED_MESSAGE;

	private int columnCount = 20;

	private int rowCount = 2;

	private ILayoutArea layoutArea;

	/* (non-Javadoc)
	 * @see ch.eugster.colibri.display.area.ILayoutAreaType#getColumnCount()
	 */
	@Override
	public int getColumnCount()
	{
		return this.columnCount;
	}

	/* (non-Javadoc)
	 * @see ch.eugster.colibri.display.area.ILayoutAreaType#getLayoutArea()
	 */
	@Override
	public ILayoutArea getLayoutArea()
	{
		switch (this)
		{
			case SALESPOINT_CLOSED_MESSAGE:
			{
				if (this.layoutArea == null)
				{
					this.layoutArea = new SalespointClosedMessageArea(this);
				}
				return this.layoutArea;
			}
			case POSITION_ADDED_MESSAGE:
			{
				if (this.layoutArea == null)
				{
					this.layoutArea = new PositionAddedMessageArea(this);
				}
				return this.layoutArea;
			}
			case PAYMENT_ADDED_MESSAGE:
			{
				if (this.layoutArea == null)
				{
					this.layoutArea = new PaymentAddedMessageArea(this);
				}
				return this.layoutArea;
			}
			case WELCOME_MESSAGE:
			{
				if (this.layoutArea == null)
				{
					this.layoutArea = new WelcomeMessageArea(this);
				}
				return this.layoutArea;
			}
			default:
			{
				throw new RuntimeException("Invalid print area type");
			}
		}
	}

	/* (non-Javadoc)
	 * @see ch.eugster.colibri.display.area.ILayoutAreaType#getRowCount()
	 */
	@Override
	public int getRowCount()
	{
		return this.rowCount;
	}

	/* (non-Javadoc)
	 * @see ch.eugster.colibri.display.area.ILayoutAreaType#getSectionTitle()
	 */
	@Override
	public String getSectionTitle()
	{
		switch (this)
		{
			case SALESPOINT_CLOSED_MESSAGE:
			{
				return "Wenn Kasse geschlossen ist";
			}
			case WELCOME_MESSAGE:
			{
				return "Wenn keine Transaktion aktiv ist";
			}
			case POSITION_ADDED_MESSAGE:
			{
				return "Wenn ein Artikel erfasst wird";
			}
			case PAYMENT_ADDED_MESSAGE:
			{
				return "Wenn eine Zahlung erfasst wird";
			}
			default:
			{
				throw new RuntimeException("Invalid print area type");
			}
		}
	}

	/* (non-Javadoc)
	 * @see ch.eugster.colibri.display.area.ILayoutAreaType#setColumnCount(int)
	 */
	@Override
	public void setColumnCount(final int columns)
	{
		this.columnCount = columns;
	}

	/* (non-Javadoc)
	 * @see ch.eugster.colibri.display.area.ILayoutAreaType#setRowCount(int)
	 */
	@Override
	public void setRowCount(final int rowCount)
	{
		this.rowCount = rowCount;
	}

	@Override
	public boolean isCustomerEditable() 
	{
		switch (this)
		{
			case SALESPOINT_CLOSED_MESSAGE:
			{
				return true;
			}
			case POSITION_ADDED_MESSAGE:
			{
				return false;
			}
			case PAYMENT_ADDED_MESSAGE:
			{
				return false;
			}
			case WELCOME_MESSAGE:
			{
				return true;
			}
			default:
			{
				throw new RuntimeException("Invalid print area type");
			}
		}
	}
}
