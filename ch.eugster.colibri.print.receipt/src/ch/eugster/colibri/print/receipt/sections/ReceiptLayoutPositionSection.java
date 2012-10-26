package ch.eugster.colibri.print.receipt.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class ReceiptLayoutPositionSection extends AbstractLayoutSection
{
	private static NumberFormat quantityFormatter = DecimalFormat.getIntegerInstance();

	private static NumberFormat amountFormatter = DecimalFormat.getNumberInstance();

	private static NumberFormat percentFormatter = DecimalFormat.getPercentInstance();

	public ReceiptLayoutPositionSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("CCCCCCCCCCCCCCCCCCCC EEEEEEE MM BBBBBBBB T\n");
		builder = builder.append("              BBBBBB PPPPPPP    RRRRRRRR\n");
		builder = builder.append("   AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("Total       SSSSSSSS RRRRRRR MM TTTTTTTT\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public Collection<String> prepareAreaDetail(final IPrintable printable)
	{
		final Collection<String> area = new ArrayList<String>();

		if (printable instanceof Receipt)
		{
			final Receipt receipt = (Receipt) printable;
			final Collection<Position> positions = receipt.getPositions();
			for (final Position position : positions)
			{
				area.addAll(super.prepareAreaDetail(position));
			}
		}
		return this.correctLineSizes(area);
	}

	@Override
	protected Collection<String> adaptPatternDetail(Collection<String> lines, final IPrintable printable)
	{
		if (printable instanceof Position)
		{
			Collection<String> newPatternLines = new ArrayList<String>();
			final Position position = (Position) printable;
			if (position.getProduct() == null)
			{
				for (final String line : lines)
				{
					if (!this.isDetailProductLine(line))
					{
						newPatternLines.add(line);
					}
				}
				lines = newPatternLines;
			}

			newPatternLines = new ArrayList<String>();
			if (Math.abs(position.getDiscount()) < 0.0000001)
			{
				for (final String line : lines)
				{
					if (!this.isDetailDiscountLine(line))
					{
						newPatternLines.add(line);
					}
				}
				lines = newPatternLines;
			}
		}
		return lines;
	}

	@Override
	protected Collection<String> adaptPatternTotal(Collection<String> lines, final IPrintable printable)
	{
		Collection<String> newPatternLines = new ArrayList<String>();
		for (final String line : lines)
		{
			if (!this.isTotalDiscountLine(line))
			{
				newPatternLines.add(line);
			}
		}
		lines = newPatternLines;
		newPatternLines = new ArrayList<String>();
		for (final String line : lines)
		{
			if (!this.isTotalSubtotalLine(line))
			{
				newPatternLines.add(line);
			}
		}
		lines = newPatternLines;
		return lines;
	}

	@Override
	protected PrintOption getDefaultPrintOptionDetail()
	{
		return PrintOption.OPTIONALLY;
	}

	@Override
	protected PrintOption getDefaultPrintOptionTotal()
	{
		return PrintOption.OPTIONALLY;
	}

	@Override
	protected IKey[] getKeysDetail()
	{
		return DetailKey.values();
	}

	@Override
	protected IKey[] getKeysTitle()
	{
		return TitleKey.values();
	}

	@Override
	protected IKey[] getKeysTotal()
	{
		return TotalKey.values();
	}

	@Override
	protected boolean hasDetailArea()
	{
		return true;
	}

	@Override
	protected boolean hasTitleArea()
	{
		return false;
	}

	@Override
	protected boolean hasTotalArea()
	{
		return true;
	}

	private boolean isDetailDiscountLine(final String line)
	{
		final Collection<String> markers = new ArrayList<String>();
		final String[] items = line.split(" ");
		for (final String item : items)
		{
			if ((item != null) && !item.isEmpty())
			{
				if (this.isKey(AreaType.DETAIL, item.charAt(0)))
				{
					final String marker = this.getMarker(item.charAt(0), item.length());
					if (marker.equals(item))
					{
						markers.add(marker);
					}
				}
			}
		}
		if (markers.isEmpty())
		{
			return false;
		}
		for (final String marker : markers)
		{
			if ((marker.charAt(0) != 'R') && (marker.charAt(0) != 'P') && (marker.charAt(0) != 'N')
					&& (marker.charAt(0) != 'B'))
			{
				return false;
			}
		}
		return true;
	}

	private boolean isDetailProductLine(final String line)
	{
		final Collection<String> markers = new ArrayList<String>();
		final String[] items = line.split(" ");
		for (final String item : items)
		{
			if ((item != null) && !item.isEmpty())
			{
				if (this.isKey(AreaType.DETAIL, item.charAt(0)))
				{
					final String marker = this.getMarker(item.charAt(0), item.length());
					if (marker.equals(item))
					{
						markers.add(marker);
					}
				}
			}
		}
		if (markers.isEmpty())
		{
			return false;
		}
		for (final String marker : markers)
		{
			if (marker.charAt(0) != 'A')
			{
				return false;
			}
		}
		return true;
	}

	// private Collection<String> prepareArea(final Position position)
	// {
	// final Collection<String> area = new ArrayList<String>();
	// final double amount =
	// position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
	// Position.AmountType.NETTO);
	// if ((amount != 0D) ||
	// this.getPrintOption(AreaType.DETAIL).equals(PrintOption.ALWAYS))
	// {
	// final Collection<String> lines = this.getAdaptedDetailPattern(position,
	// this.getPattern(AreaType.DETAIL));
	// final String[] markers = this.getMarkers(AreaType.DETAIL, lines);
	// for (String line : lines)
	// {
	// for (final String marker : markers)
	// {
	// line = this.replace(AreaType.DETAIL, position, marker, line);
	// }
	// area.add(line);
	// }
	// }
	// return area;
	// }

	private final boolean isTotalDiscountLine(final String line)
	{
		final Collection<String> markers = new ArrayList<String>();
		final String[] items = line.split(" ");
		for (final String item : items)
		{
			if ((item != null) && !item.isEmpty())
			{
				if (this.isKey(AreaType.TOTAL, item.charAt(0)))
				{
					final String marker = this.getMarker(item.charAt(0), item.length());
					if (marker.equals(item))
					{
						markers.add(marker);
					}
				}
			}
		}
		if (markers.isEmpty())
		{
			return false;
		}
		for (final String marker : markers)
		{
			if ((marker.charAt(0) != 'R') && (marker.charAt(0) != 'N'))
			{
				return false;
			}
		}
		return true;
	}

	private final boolean isTotalSubtotalLine(final String line)
	{
		final Collection<String> markers = new ArrayList<String>();
		final String[] items = line.split(" ");
		for (final String item : items)
		{
			if ((item != null) && !item.isEmpty())
			{
				if (this.isKey(AreaType.TOTAL, item.charAt(0)))
				{
					final String marker = this.getMarker(item.charAt(0), item.length());
					if (marker.equals(item))
					{
						markers.add(marker);
					}
				}
			}
		}
		if (markers.isEmpty())
		{
			return false;
		}
		for (final String marker : markers)
		{
			if (marker.charAt(0) != 'S')
			{
				return false;
			}
		}
		return true;
	}

	public enum DetailKey implements IKey
	{
		C, M, E, B, N, P, R, T, O, A;

		@Override
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
				default:
				{
					throw new RuntimeException("invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutSection, final IPrintable printable, final String marker)
		{
			if (printable instanceof Position)
			{
				final Position position = (Position) printable;

				switch (this)
				{
					case C:
					{
						if (position.getProduct() == null)
						{
							return layoutSection.replaceMarker(position.getProductGroup().getName(), marker, true);
						}
						else
						{
							return layoutSection.replaceMarker(position.getProduct().getCode(), marker, true);
						}
					}
					case M:
					{
						final String quantity = ReceiptLayoutPositionSection.quantityFormatter.format(position
								.getQuantity());
						return layoutSection.replaceMarker(quantity, marker, false);
					}
					case E:
					{
						ReceiptLayoutPositionSection.amountFormatter.setMinimumFractionDigits(position.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutPositionSection.amountFormatter.setMaximumFractionDigits(position.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						final String price = ReceiptLayoutPositionSection.amountFormatter.format(position.getPrice());
						return layoutSection.replaceMarker(price, marker, false);
					}
					case B:
					{
						ReceiptLayoutPositionSection.amountFormatter.setMinimumFractionDigits(position.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutPositionSection.amountFormatter.setMaximumFractionDigits(position.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						final String amountBrut = ReceiptLayoutPositionSection.amountFormatter.format(position
								.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.BRUTTO));
						return layoutSection.replaceMarker(amountBrut, marker, false);
					}
					case N:
					{
						ReceiptLayoutPositionSection.amountFormatter.setMinimumFractionDigits(position.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutPositionSection.amountFormatter.setMaximumFractionDigits(position.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						final String amountNet = ReceiptLayoutPositionSection.amountFormatter.format(position
								.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
						return layoutSection.replaceMarker(amountNet, marker, false);
					}
					case P:
					{
						ReceiptLayoutPositionSection.percentFormatter.setMaximumFractionDigits(1);
						final String percent = ReceiptLayoutPositionSection.percentFormatter.format(Math.abs(position
								.getDiscount()));
						return layoutSection.replaceMarker(percent, marker, false);
					}
					case R:
					{
						ReceiptLayoutPositionSection.amountFormatter.setMinimumFractionDigits(position.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutPositionSection.amountFormatter.setMaximumFractionDigits(position.getReceipt()
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						final String discount = ReceiptLayoutPositionSection.amountFormatter.format(position.getAmount(
								Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.DISCOUNT));
						return layoutSection.replaceMarker(discount, marker, false);
					}
					case T:
					{
						return layoutSection.replaceMarker(position.getCurrentTax().getTax().getTaxRate().getCode(),
								marker, false);
					}
					case O:
					{
						return layoutSection.replaceMarker(position.getOption().toCode(), marker, false);
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
									return layoutSection.replaceMarker(code + " "
											+ position.getProduct().getAuthorAndTitleShortForm(), marker, true);
								}
							}
						}
						return layoutSection.replaceMarker("", marker, true);
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

	public enum TitleKey implements IKey
	{
		;

		@Override
		public String label()
		{
			return null;
		}

		@Override
		public String replace(final ILayoutSection layoutSection, final IPrintable printable, final String marker)
		{
			return null;
		}

	}

	public enum TotalKey implements IKey
	{
		S, R, T, M, N;

		@Override
		public String label()
		{
			switch (this)
			{
				case S:
				{
					return "Subtotal";
				}
				case R:
				{
					return "Rabattbetrag";
				}
				case T:
				{
					return "Total";
				}
				case M:
				{
					return "Total Menge";
				}
				case N:
				{
					return "Anzahl Rabattierte";
				}
				default:
				{
					throw new RuntimeException("invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutSection, final IPrintable printable, final String marker)
		{
			if (printable instanceof Receipt)
			{
				final Receipt receipt = (Receipt) printable;

				switch (this)
				{
					case S:
					{
						String formattedAmount = "";
						double discount = receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
								Position.AmountType.DISCOUNT);
						if (Math.abs(discount) >= AbstractEntity.ROUND_FACTOR)
						{
							ReceiptLayoutPositionSection.amountFormatter.setMinimumFractionDigits(receipt
									.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
							ReceiptLayoutPositionSection.amountFormatter.setMaximumFractionDigits(receipt
									.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
							double amount = receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
									Position.AmountType.BRUTTO);
							formattedAmount = ReceiptLayoutPositionSection.amountFormatter.format(amount);
						}
						return layoutSection.replaceMarker(formattedAmount, marker, false);
					}
					case R:
					{
						String formattedAmount = "";
						double discount = receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
								Position.AmountType.DISCOUNT);
						if (Math.abs(discount) >= AbstractEntity.ROUND_FACTOR)
						{
							ReceiptLayoutPositionSection.amountFormatter.setMinimumFractionDigits(receipt
									.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
							ReceiptLayoutPositionSection.amountFormatter.setMaximumFractionDigits(receipt
									.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
							double amount = receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
									Position.AmountType.DISCOUNT);
							formattedAmount = ReceiptLayoutPositionSection.amountFormatter.format(amount);
						}
						return layoutSection.replaceMarker(formattedAmount, marker, false);
					}
					case T:
					{
						ReceiptLayoutPositionSection.amountFormatter.setMinimumFractionDigits(receipt
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						ReceiptLayoutPositionSection.amountFormatter.setMaximumFractionDigits(receipt
								.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
						final double amount = receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
								Position.AmountType.NETTO);
						return layoutSection.replaceMarker(ReceiptLayoutPositionSection.amountFormatter.format(amount),
								marker, false);
					}
					case M:
					{
						final String quantity = ReceiptLayoutPositionSection.quantityFormatter.format(receipt
								.getPositionQuantity(Option.values()));
						return layoutSection.replaceMarker(quantity, marker, false);
					}
					case N:
					{
						String formattedQuantity = "";
						int quantity = receipt.getPositionWithDiscountQuantity();
						if (quantity != 0)
						{
							formattedQuantity = ReceiptLayoutPositionSection.quantityFormatter.format(quantity);
						}
						return layoutSection.replaceMarker(formattedQuantity, marker, false);
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

	@Override
	protected boolean hasData(IPrintable printable)
	{
		if (printable instanceof Receipt)
		{
			return ((Receipt) printable).getPositions().size() > 0;
		}
		return true;
	}

}