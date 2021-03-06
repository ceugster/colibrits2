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
import ch.eugster.colibri.persistence.model.Position.AmountType;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
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
					return "Offen/R�ckgeld Betrag LW";
				}
				case P:
				{
					return "Offen/R�ckgeld Betrag FW";
				}
				case R:
				{
					return "R�ckgeldw�hrung";
				}
				case W:
				{
					return "Zahlungsw�hrung";
				}
				case X:
				{
					return "Text Offen/R�ckgeld";
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
			NumberFormat defaultCurrencyFormat = NumberFormat.getNumberInstance();
			NumberFormat foreignCurrencyFormat = NumberFormat.getNumberInstance();

			if (printable instanceof Payment)
			{
				final Payment payment = (Payment) printable;

				Currency defaultCurrency = payment.getReceipt().getDefaultCurrency();
				Currency foreignCurrency = payment.getReceipt().getForeignCurrency();

				double defaultCurrencyAmount = payment.getReceipt().getPaymentDefaultCurrencyBackAmount();
				double foreignCurrencyAmount = payment.getReceipt().getPaymentDefaultForeignCurrencyBackAmount();

				String defaultCurrencyLabel = "";
				String defaultCurrencyAmountLabel = "";

				String foreignCurrencyLabel = "";
				String foreignCurrencyAmountLabel = "";

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
						String text = difference > 0D ? "Offen" : "R�ckgeld";
						return layoutArea.replaceMarker(text, marker, true);
					}
					case Y:
					{
						if (payment.getReceipt().getPositionAmount(Receipt.QuotationType.REFERENCE_CURRENCY, Position.AmountType.NETTO) != 0D)
						{
							if (payment.getReceipt().getPaymentDefaultCurrencyBackAmount() == payment.getReceipt().getPositionDefaultCurrencyAmount(AmountType.NETTO))
							{
								defaultCurrencyAmount = payment.getReceipt().getPaymentDefaultCurrencyBackAmount();
								foreignCurrencyAmount = payment.getReceipt().getPaymentDefaultForeignCurrencyBackAmount();
							}
							else
							{
								defaultCurrencyAmount = payment.getReceipt().getPaymentDefaultCurrencyAmount() - payment.getReceipt().getPaymentDefaultCurrencyBackAmount();
								foreignCurrencyAmount = payment.getReceipt().getPaymentDefaultForeignCurrencyAmount() - payment.getReceipt().getPaymentDefaultForeignCurrencyBackAmount();
							}
						}

						if (foreignCurrency == null || !defaultCurrency.getId().equals(foreignCurrency.getId()))
						{
							final java.util.Currency fc = java.util.Currency.getInstance(foreignCurrency.getCode());
							foreignCurrencyFormat.setMaximumFractionDigits(fc.getDefaultFractionDigits());
							foreignCurrencyFormat.setMinimumFractionDigits(fc.getDefaultFractionDigits());
							foreignCurrencyLabel = foreignCurrency.getCode();
							foreignCurrencyAmountLabel = foreignCurrencyFormat.format(foreignCurrencyAmount);
						}

						final java.util.Currency dc = java.util.Currency.getInstance(defaultCurrency.getCode());
						if (defaultCurrencyFormat.getMaximumFractionDigits() != dc.getDefaultFractionDigits())
						{
							defaultCurrencyFormat.setMaximumFractionDigits(dc.getDefaultFractionDigits());
						}
						if (defaultCurrencyFormat.getMinimumFractionDigits() != dc.getDefaultFractionDigits())
						{
							defaultCurrencyFormat.setMinimumFractionDigits(dc.getDefaultFractionDigits());
						}
						if (!defaultCurrencyLabel.equals(defaultCurrency.getCode()))
						{
							defaultCurrencyLabel = defaultCurrency.getCode();
						}
						defaultCurrencyAmountLabel = defaultCurrencyFormat.format(defaultCurrencyAmount);

						String amount = foreignCurrencyLabel.isEmpty() 
								? defaultCurrencyLabel + " " + defaultCurrencyFormat.format(defaultCurrencyAmount)
								: foreignCurrencyLabel + " " + foreignCurrencyFormat.format(foreignCurrencyAmount);
						int articleTextLen = marker.length() - amount.length();
						String received = pad("Erhalten", articleTextLen);
						return layoutArea.replaceMarker(received + amount, marker, false);
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
							// Offen in Zahlw�hrung
							difference = Currency.change(payment.getReceipt().getDefaultCurrency(), payment.getPaymentType().getCurrency(), difference);
						}
						String amountText = amountFormatter.format(Math.abs(difference));
						amountText = currency.getCode() + " " + amountText;
						int articleTextLen = marker.length() - amountText.length();
						String text = difference > 0D ? pad("Offen", articleTextLen) : pad("R�ckgeld", articleTextLen);
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
			String text = null;
			if (payment.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.CASH))
			{
				text = pad("Bargeld", minLength);
			}
			else
			{
				text = pad(payment.getPaymentType().getName(), minLength);
			}
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
