package ch.eugster.colibri.display.simple.area;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.display.area.AbstractLayoutArea;
import ch.eugster.colibri.display.area.IKey;
import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.display.area.ILayoutAreaType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;

public class PositionAddedMessageArea extends AbstractLayoutArea implements ILayoutArea
{
	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	private static NumberFormat percentFormatter = DecimalFormat.getPercentInstance();

	private static NumberFormat quantityFormatter = DecimalFormat.getIntegerInstance();

	public PositionAddedMessageArea(final ILayoutAreaType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPattern()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("CCCCCCCCCCCCC NNNNNN\n");
		builder = builder.append("Total LLLLLLLLLLLLLL");
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

		if (printable instanceof Position)
		{
			final Position position = (Position) printable;

			String pattern = this.getPattern();
			final String[] markers = this.getMarkers(pattern);
			for (final String marker : markers)
			{
				pattern = this.replace(position, marker, pattern);
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
		C, M, E, B, N, P, R, T, O, A, L, F;

		public String label()
		{
			switch (this)
			{
				case C:
				{
					return "Warengruppe/Barcode";
				}
				case M:
				{
					return "Menge";
				}
				case E:
				{
					return "Einzelpreis";
				}
				case B:
				{
					return "Bruttobetrag";
				}
				case N:
				{
					return "Nettobetrag";
				}
				case P:
				{
					return "Rabattprozent";
				}
				case R:
				{
					return "Rabattbetrag";
				}
				case T:
				{
					return "Mehrwertsteuercode";
				}
				case O:
				{
					return "Optionscode";
				}
				case A:
				{
					return "Artikelbezeichnung";
				}
				case L:
				{
					return "Gesamtbetrag LW";
				}
				case F:
				{
					return "Gesamtbetrag FW";
				}
				default:
				{
					throw new RuntimeException("invalid key");
				}
			}
		}

		public String replace(final ILayoutArea layoutArea, final IPrintable printable, final String marker)
		{
			if (printable instanceof Position)
			{
				final Position position = (Position) printable;

				amountFormatter.setMaximumFractionDigits(position.getForeignCurrency().getCurrency().getDefaultFractionDigits());
				amountFormatter.setMinimumFractionDigits(position.getForeignCurrency().getCurrency().getDefaultFractionDigits());

				switch (this)
				{
					case C:
					{
						if (position.getProduct() == null)
						{
							return layoutArea.replaceMarker(position.getProductGroup().getName(), marker, true);
						}
						else
						{
							return layoutArea.replaceMarker(position.getProduct().getCode(), marker, true);
						}
					}
					case M:
					{
						final String quantity = PositionAddedMessageArea.quantityFormatter.format(position.getQuantity());
						return layoutArea.replaceMarker(quantity, marker, false);
					}
					case E:
					{
						PositionAddedMessageArea.amountFormatter.setCurrency(position.getReceipt().getDefaultCurrency().getCurrency());
						final String price = PositionAddedMessageArea.amountFormatter.format(position.getPrice());
						return layoutArea.replaceMarker(price, marker, false);
					}
					case B:
					{
						PositionAddedMessageArea.amountFormatter.setCurrency(position.getReceipt().getDefaultCurrency().getCurrency());
						final String amountBrut = PositionAddedMessageArea.amountFormatter.format(position.getAmount(
								Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.BRUTTO));
						return layoutArea.replaceMarker(amountBrut, marker, false);
					}
					case N:
					{
						PositionAddedMessageArea.amountFormatter.setCurrency(position.getReceipt().getDefaultCurrency().getCurrency());
						final String amountNet = PositionAddedMessageArea.amountFormatter.format(position.getAmount(
								Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
						return layoutArea.replaceMarker(amountNet, marker, false);
					}
					case P:
					{
						PositionAddedMessageArea.percentFormatter.setMaximumFractionDigits(1);
						final String percent = PositionAddedMessageArea.percentFormatter.format(Math.abs(position.getDiscount()));
						return layoutArea.replaceMarker(percent, marker, false);
					}
					case R:
					{
						PositionAddedMessageArea.amountFormatter.setCurrency(position.getReceipt().getDefaultCurrency().getCurrency());
						final String discount = PositionAddedMessageArea.amountFormatter.format(position.getAmount(
								Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.DISCOUNT));
						return layoutArea.replaceMarker(discount, marker, false);
					}
					case T:
					{
						return layoutArea.replaceMarker(position.getCurrentTax().getTax().getTaxRate().getCode(), marker, false);
					}
					case O:
					{
						return layoutArea.replaceMarker(position.getOption().toCode(), marker, false);
					}
					case A:
					{
						if (position.getProduct() != null)
						{
							Collection<ProductGroupMapping> mappings = position.getProductGroup().getProductGroupMappings(position.getProvider());
							for (ProductGroupMapping mapping : mappings)
							{
								if (!mapping.isDeleted())
								{
									final String code = mapping.getExternalProductGroup().getCode();
									return layoutArea.replaceMarker(code + " " + position.getProduct().getAuthorAndTitleShortForm(), marker, true);
								}
							}
						}
						return layoutArea.replaceMarker("", marker, true);
					}
					case L:
					{
						PositionAddedMessageArea.amountFormatter.setCurrency(position.getReceipt().getDefaultCurrency().getCurrency());
						final String total = PositionAddedMessageArea.amountFormatter.format(position.getReceipt().getPositionAmount(
								Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
						return layoutArea.replaceMarker(total, marker, false);
					}
					case F:
					{
						PositionAddedMessageArea.amountFormatter.setCurrency(position.getReceipt().getDefaultCurrency().getCurrency());
						final String total = PositionAddedMessageArea.amountFormatter.format(position.getReceipt().getPositionAmount(
								Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY, Position.AmountType.NETTO));
						return layoutArea.replaceMarker(total, marker, false);
					}
					default:
					{
						throw new RuntimeException("invalid key");
					}
				}
			}
			return marker;
		}
	}
}
