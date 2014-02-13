package ch.eugster.colibri.display.simple.area;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.display.area.AbstractLayoutArea;
import ch.eugster.colibri.display.area.IKey;
import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.display.area.ILayoutAreaType;
import ch.eugster.colibri.persistence.model.Currency;
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
		builder = builder.append("YYYYYYYYYYYYYYYYYYYY\n");
		builder = builder.append("ZZZZZZZZZZZZZZZZZZZZ");
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
		A, L, F, S, T, O, P, R, W, X, Y, Z;

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
					return "Offen/Rückgeld Betrag LW";
				}
				case P:
				{
					return "Offen/Rückgeld Betrag FW";
				}
				case R:
				{
					return "Rückgeldwährung";
				}
				case W:
				{
					return "Zahlungswährung";
				}
				case X:
				{
					return "Text Offen/Rückgeld";
				}
				case Y:
				{
					return "1. Zeile (fix definiert)";
				}
				case Z:
				{
					return "2. Zeile (fix definiert)";
				}
				default:
				{
					throw new RuntimeException("Invalid key");
				}
			}
		}

		private String format(Currency currency, Receipt receipt, double amount)
		{
			if (!currency.equals(receipt.getDefaultCurrency()))
			{
				PaymentAddedMessageArea.amountFormatter.setCurrency(currency.getCurrency());
			}
			else
			{
				PaymentAddedMessageArea.amountFormatter.setMaximumFractionDigits(PaymentAddedMessageArea.amountFormatter.getCurrency().getDefaultFractionDigits());
				PaymentAddedMessageArea.amountFormatter.setMinimumFractionDigits(PaymentAddedMessageArea.amountFormatter.getCurrency().getDefaultFractionDigits());
			}
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
						String value = format(payment.getReceipt().getDefaultCurrency(), payment.getReceipt(), payment.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY));
						return layoutArea.replaceMarker(value, marker, false);
					}
					case F:
					{
						if (payment.getReceipt().getForeignCurrency().getId().equals(payment.getReceipt().getDefaultCurrency().getId()))
						{
							String value = format(payment.getReceipt().getDefaultCurrency(), payment.getReceipt(), payment.getAmount(Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY));
							return layoutArea.replaceMarker(value, marker, false);
						}
						else
						{
							String value = format(payment.getReceipt().getForeignCurrency(), payment.getReceipt(), payment.getAmount(Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY));
							return layoutArea.replaceMarker(value, marker, false);
						}
					}
					case S:
					{
						String value = format(payment.getReceipt().getDefaultCurrency(), payment.getReceipt(), payment.getReceipt().getPositionAmount(
								Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
						return layoutArea.replaceMarker(value, marker, false);
					}
					case T:
					{
						String value = format(payment.getReceipt().getDefaultCurrency(), payment.getReceipt(), payment.getReceipt().getPositionAmount(
								Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY, Position.AmountType.NETTO));
						return layoutArea.replaceMarker(value, marker, false);
					}
					case O:
					{
						String value = format(payment.getReceipt().getDefaultCurrency(), payment.getReceipt(), payment.getReceipt().getDifference());
						return layoutArea.replaceMarker(value, marker, false);
					}
					case P:
					{
						double positions = payment.getReceipt().getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO);
						double payments = payment.getReceipt().getPaymentAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
						double difference = Math.abs(positions - payments);
						double newDifference = Currency.change(payment.getReceipt().getDefaultCurrency(), payment.getPaymentType().getCurrency(), difference);
						String value = format(payment.getReceipt().getDefaultCurrency(), payment.getReceipt(), newDifference);
						return layoutArea.replaceMarker(value, marker, false);
					}
					case R:
					{
						double positions = payment.getReceipt().getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO);
						double payments = payment.getReceipt().getPaymentAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
						double difference = positions - payments;
						String value = difference > 0D ? payment.getPaymentType().getCurrency().getCode() : payment.getPaymentType().getCurrency().getCode();
						return layoutArea.replaceMarker(value, marker, false);
					}
					case W:
					{
						String value = payment.getReceipt().getForeignCurrency().getCode();
						return layoutArea.replaceMarker(value, marker, false);
					}
					case X:
					{
						double positions = payment.getReceipt().getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO);
						double payments = payment.getReceipt().getPaymentAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
						double difference = positions - payments;
						String text = difference > 0D ? "Offen" : "Rückgeld";
						return layoutArea.replaceMarker(text, marker, true);
					}
					case Y:
					{
						amountFormatter.setCurrency(payment.getPaymentType().getCurrency().getCurrency());
						amountFormatter.setMaximumFractionDigits(payment.getPaymentType().getCurrency().getCurrency().getDefaultFractionDigits());
						amountFormatter.setMinimumFractionDigits(payment.getPaymentType().getCurrency().getCurrency().getDefaultFractionDigits());
						double amount = payment.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY);
						String amountText = amountFormatter.format(amount);
						if (!payment.getPaymentType().getCurrency().getId().equals(payment.getReceipt().getDefaultCurrency().getId()))
						{
							amountText = payment.getPaymentType().getCurrency().getCode() + " " + amountText;
						}
						int articleTextLen = marker.length() - amountText.length();
						String articleText = getText(payment, articleTextLen);
						String value = articleText + amountText;
						value = layoutArea.replaceMarker(value, marker, true);
						return value;
					}
					case Z:
					{
						double positions = payment.getReceipt().getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO);
						double payments = payment.getReceipt().getPaymentAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
						double difference = positions - payments;
						Currency currency = difference <= 0D ? payment.getReceipt().getDefaultCurrency() : payment.getPaymentType().getCurrency();
						amountFormatter.setCurrency(currency.getCurrency());
						amountFormatter.setMaximumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
						amountFormatter.setMinimumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
						if (difference > 0D)
						{
							// Offen in Zahlwährung
							difference = Currency.change(payment.getReceipt().getDefaultCurrency(), payment.getPaymentType().getCurrency(), difference);
						}
						String amountText = amountFormatter.format(Math.abs(difference));
						amountText = currency.getCode() + " " + amountText;
						int articleTextLen = marker.length() - amountText.length();
						String text = difference > 0D ? pad("Offen", articleTextLen) : pad("Rückgeld", articleTextLen);
						text = layoutArea.replaceMarker(text + amountText, marker, false);
						return text;
					}
					default:
					{
						throw new RuntimeException("Invalid key");
					}
				}
			}
			return marker;
		}
		private String getText(Payment payment, int minLength)
		{
			String text = pad(payment.getPaymentType().getName(), minLength);
			return text;
		}
		
		private String pad(String value, int minLength)
		{
			if (value.length() > minLength)
			{
				return value.substring(0, minLength - 1) + " ";
			}
			else
			{
				StringBuilder padding = new StringBuilder();
				for (int i = value.length(); i < minLength; i++)
				{
					padding = padding.append(" ");
				}
				return value + padding.toString();
			}
		}
	}

}
