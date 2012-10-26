package ch.eugster.colibri.display.simple.area;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;

import ch.eugster.colibri.display.area.AbstractLayoutArea;
import ch.eugster.colibri.display.area.IKey;
import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.display.area.ILayoutAreaType;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;

public class PaymentAddedMessageArea extends AbstractLayoutArea implements ILayoutArea
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	public PaymentAddedMessageArea(final ILayoutAreaType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPattern()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("AAAAAAAAAA LLLLLLLLL\n");
		builder = builder.append("XXXXXX    OOOOOOOOOO");
		return builder.toString();
	}

	@Override
	public int getDefaultTimerDelay()
	{
		return 0;
	}

	@Override
	protected Collection<String> getItems(final IPrintable printable)
	{
		final Collection<String> items = new ArrayList<String>();

		if (printable instanceof Payment)
		{
			final Payment payment = (Payment) printable;

			String pattern = this.getPattern();
			final String[] markers = this.getMarkers(pattern);
			for (final String marker : markers)
			{
				pattern = this.replace(payment, marker, pattern);
			}
			items.add(pattern);
		}
		return items;
	}

	@Override
	protected IKey[] getKeys()
	{
		return Key.values();
	}

	public enum Key implements IKey
	{
		A, L, F, S, T, O, P, X;

		@Override
		public String label()
		{
			switch (this)
			{
				case A:
				{
					return "Zahlungsart";
				}
				case L:
				{
					return "Zahlungsbetrag LW";
				}
				case F:
				{
					return "Zahlungsbetrag FW";
				}
				case S:
				{
					return "Totalbetrag LW";
				}
				case T:
				{
					return "Totalbetrag FW";
				}
				case O:
				{
					return "Offen/Zurück Betrag LW";
				}
				case P:
				{
					return "Offen/Zurück Betrag FW";
				}
				case X:
				{
					return "Text Offen/Zurück";
				}
				default:
				{
					throw new RuntimeException("Invalid key");
				}
			}
		}

		private String format(Currency currency, double amount)
		{
			PaymentAddedMessageArea.amountFormatter.setMaximumFractionDigits(PaymentAddedMessageArea.amountFormatter.getCurrency().getDefaultFractionDigits());
			PaymentAddedMessageArea.amountFormatter.setMinimumFractionDigits(PaymentAddedMessageArea.amountFormatter.getCurrency().getDefaultFractionDigits());
			return PaymentAddedMessageArea.amountFormatter.format(amount);
		}
		
		@Override
		public String replace(final ILayoutArea layoutArea, final IPrintable printable, final String marker)
		{
			if (printable instanceof Payment)
			{
				final Payment payment = (Payment) printable;

				switch (this)
				{
					case A:
					{
						return layoutArea.replaceMarker(payment.getPaymentType().getName(), marker, true);
					}
					case L:
					{
						String value = format(payment.getReceipt().getDefaultCurrency().getCurrency(), payment.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY));
						return layoutArea.replaceMarker(value, marker, false);
					}
					case F:
					{
						String value = format(payment.getReceipt().getDefaultCurrency().getCurrency(), payment.getAmount(Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY));
						return layoutArea.replaceMarker(value, marker, false);
					}
					case S:
					{
						String value = format(payment.getReceipt().getDefaultCurrency().getCurrency(), payment.getReceipt().getPositionAmount(
								Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
						return layoutArea.replaceMarker(value, marker, false);
					}
					case T:
					{
						String value = format(payment.getReceipt().getDefaultCurrency().getCurrency(), payment.getReceipt().getPositionAmount(
								Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY, Position.AmountType.NETTO));
						return layoutArea.replaceMarker(value, marker, false);
					}
					case O:
					{
						String value = format(payment.getReceipt().getDefaultCurrency().getCurrency(), payment.getReceipt().getDifference());
						return layoutArea.replaceMarker(value, marker, false);
					}
					case P:
					{
						String value = format(payment.getReceipt().getDefaultCurrency().getCurrency(), payment.getReceipt().getFCDifference());
						return layoutArea.replaceMarker(value, marker, false);
					}
					case X:
					{
						String text = payment.getReceipt().getDifference() <= 0D ? "Offen" : "Zurück";
						return layoutArea.replaceMarker(text, marker, true);
					}
					default:
					{
						throw new RuntimeException("Invalid key");
					}
				}
			}
			return marker;
		}
	}

}
