package ch.eugster.colibri.report.receipt.views;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;

public class ReceiptSorter extends ViewerSorter
{
	private final int columnIndex = 0;

	private final boolean desc = false;

	private final String pattern;

	private NumberFormat inf;

	public ReceiptSorter(final String numberPattern)
	{
		this.pattern = numberPattern;
		if ((this.pattern == null) || (this.pattern.length() == 0))
		{
			this.inf = NumberFormat.getIntegerInstance();
		}
		else
		{
			this.inf = new DecimalFormat(this.pattern);
		}
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2)
	{
		if ((e1 instanceof Receipt) && (e2 instanceof Receipt))
		{
			final Receipt r1 = (Receipt) e1;
			final Receipt r2 = (Receipt) e2;

			switch (this.columnIndex)
			{
				case 0:
				{
					if (this.desc)
					{
						return this.inf.format(r2.getNumber()).compareTo(this.inf.format(r1.getNumber()));
					}
					else
					{
						return this.inf.format(r1.getNumber()).compareTo(this.inf.format(r2.getNumber()));
					}
				}
				case 1:
				{
					if (this.desc)
					{
						return r2.getTimestamp().compareTo(r1.getTimestamp());
					}
					else
					{
						return r1.getTimestamp().compareTo(r2.getTimestamp());
					}
				}
				case 2:
				{
					if (this.desc)
					{
						return r2.getState().toString().compareTo(r1.getState().toString());
					}
					else
					{
						return r1.getState().toString().compareTo(r2.getState().toString());
					}
				}
				case 3:
				{
					if (this.desc)
					{
						return Double.valueOf(r2.getPositionDefaultCurrencyAmount(Position.AmountType.NETTO))
								.compareTo(
										Double.valueOf(r1.getPositionDefaultCurrencyAmount(Position.AmountType.NETTO)));
					}
					else
					{
						return Double.valueOf(r1.getPositionDefaultCurrencyAmount(Position.AmountType.NETTO))
								.compareTo(
										Double.valueOf(r2.getPositionDefaultCurrencyAmount(Position.AmountType.NETTO)));
					}
				}
				case 4:
				{
					final Calendar s1 = r1.getSettlement().getSettled();
					final Calendar s2 = r2.getSettlement().getSettled();

					if ((s1 == null) && (s2 == null))
					{
						return 0;
					}
					else if (s1 == null)
					{
						if (this.desc)
						{
							return 1;
						}
						else
						{
							return -1;
						}
					}
					else if (s2 == null)
					{
						if (this.desc)
						{
							return -1;
						}
						else
						{
							return 1;
						}
					}
					else
					{
						if (this.desc)
						{
							return r2.getSettlement().getSettled().compareTo(r1.getSettlement().getSettled());
						}
						else
						{
							return r1.getTimestamp().compareTo(r2.getTimestamp());
						}
					}
				}
				case 5:
				{
					if (this.desc)
					{
						return r2.getUser().getUsername().compareTo(r1.getUser().getUsername());
					}
					else
					{
						return r1.getUser().getUsername().compareTo(r2.getUser().getUsername());
					}
				}
				default:
				{
					return 0;
				}
			}
		}
		return super.compare(viewer, e1, e2);
	}
}
