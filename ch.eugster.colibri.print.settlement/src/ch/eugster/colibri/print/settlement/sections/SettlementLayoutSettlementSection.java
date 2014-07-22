package ch.eugster.colibri.print.settlement.sections;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementDetail;
import ch.eugster.colibri.persistence.model.SettlementDetail.Part;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.print.section.AbstractLayoutSection;
import ch.eugster.colibri.print.section.IKey;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSectionType;

public class SettlementLayoutSettlementSection extends AbstractLayoutSection
{
	private final double[] amounts = new double[2];

	private static NumberFormat doubleFormatter = DecimalFormat.getNumberInstance();

	public SettlementLayoutSettlementSection(final ILayoutSectionType layoutAreaType)
	{
		super(layoutAreaType);
	}

	@Override
	public String getDefaultPatternDetail()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("WWW TTTTTTTTTTTTTTTT DDDDDDDDDD CCCCCCCCCC");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTitle()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("WWW Kassastock             SOLL        IST\n");
		builder = builder.append("------------------------------------------");
		return builder.toString();
	}

	@Override
	public String getDefaultPatternTotal()
	{
		StringBuilder builder = new StringBuilder();
		builder = builder.append("------------------------------------------\n");
		builder = builder.append("WWW TTTTTTTTTTTTTTTT DDDDDDDDDD CCCCCCCCCC\n");
		builder = builder.append("==========================================");
		return builder.toString();
	}

	private void initializeAmounts()
	{
		for (int i = 0; i < amounts.length; i++)
		{
			amounts[i] = 0D;
		}
	}

	@Override
	public Collection<String> prepareSection(final IPrintable printable)
	{
		initializeAmounts();
		final Collection<String> sections = new ArrayList<String>();
		if (printable instanceof Settlement)
		{
			final Settlement settlement = (Settlement) printable;
			final Collection<SettlementDetail> allDetails = settlement.getDetails();
			Map<Long, Collection<SettlementDetail>> subSections = this.prepareSubSections(allDetails);
			final Long[] keys = subSections.keySet().toArray(new Long[0]);
			Arrays.sort(keys);
			for (final Long key : keys)
			{
				final Collection<SettlementDetail> currencyDetails = subSections.get(key);
				if ((currencyDetails != null) && !currencyDetails.isEmpty())
				{
					boolean hasEntries = false;
					for (SettlementDetail detail : currencyDetails)
					{
						if (detail.getCredit() != 0D || detail.getDebit() != 0)
						{
							hasEntries = true;
							break;
						}
					}
					SettlementDetail[] details = currencyDetails.toArray(new SettlementDetail[0]);
					if (hasEntries && details.length > 0)
					{
						if (this.hasTitleArea())
						{
							if (this.printIt(AreaType.TITLE, printable))
							{
								sections.addAll(this.prepareArea(AreaType.TITLE, details[0]));
							}
						}
						if (this.hasDetailArea())
						{
							if (this.printIt(AreaType.DETAIL, printable))
							{
								for (final SettlementDetail detail : details)
								{
									sections.addAll(this.prepareArea(AreaType.DETAIL, detail));
								}
							}
						}
						if (this.hasTotalArea())
						{
							if (this.printIt(AreaType.TOTAL, printable))
							{
								SettlementDetail totalDetail = SettlementDetail.newInstance(settlement,
										details[0].getStock());
								totalDetail.setPart(null);
								totalDetail.setPaymentType(details[0].getPaymentType());
								totalDetail.setVariableStock(details[0].isVariableStock());
								for (final SettlementDetail detail : details)
								{
									if (detail.getPart().equals(Part.END_STOCK))
									{
										if (detail.getDebit() == detail.getCredit())
										{
											totalDetail.setCredit(0D);
											totalDetail.setDebit(0D);
										}
										else if (detail.getDebit() > detail.getCredit())
										{
											totalDetail.setDebit(0);
											totalDetail.setCredit(detail.getDebit() - detail.getCredit());
											
										}
										else
										{
											totalDetail.setDebit(detail.getCredit() - detail.getDebit());
											totalDetail.setCredit(0);
										}
										totalDetail.setQuantity(totalDetail.getQuantity() + detail.getQuantity());
									}
								}
								sections.addAll(this.prepareArea(AreaType.TOTAL, totalDetail));
							}
						}
					}
				}
			}
		}
		return sections;
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
	protected boolean hasData(IPrintable printable)
	{
		return true;
	}

	@Override
	protected boolean hasDetailArea()
	{
		return true;
	}

	@Override
	protected boolean hasTitleArea()
	{
		return true;
	}

	@Override
	protected boolean hasTotalArea()
	{
		return true;
	}

	private Map<Long, Collection<SettlementDetail>> prepareSubSections(final Collection<SettlementDetail> details)
	{
		final Map<Long, Collection<SettlementDetail>> subSections = new HashMap<Long, Collection<SettlementDetail>>();
		for (final SettlementDetail detail : details)
		{
			final Currency currency = detail.getPaymentType().getCurrency();
			Collection<SettlementDetail> subSection = subSections.get(currency.getId());
			if (subSection == null)
			{
				subSection = new ArrayList<SettlementDetail>();
				subSections.put(currency.getId(), subSection);
			}
			subSection.add(detail);
		}
		return subSections;
	}

	public enum DetailKey implements IKey
	{
		W, T, D, C;

		@Override
		public String label()
		{
			switch (this)
			{
				case W:
				{
					return "Währungscode";
				}
				case T:
				{
					return "Text";
				}
				case D:
				{
					return "berechnet";
				}
				case C:
				{
					return "gezählt";
				}
				default:
				{
					throw new RuntimeException("invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutArea, final IPrintable printable, final String marker)
		{
			if (printable instanceof SettlementDetail)
			{
				final SettlementDetail detail = (SettlementDetail) printable;

				switch (this)
				{
					case W:
					{
						return layoutArea.replaceMarker(detail.getPaymentType().getCurrency().getCode(), marker, true);
					}
					case T:
					{
						String text = detail.getPart().label(detail);
						return layoutArea.replaceMarker(text, marker, true);
					}
					case D:
					{
						if (detail.getPart().equals(Part.DIFFERENCE) && detail.getDebit() == 0D)
						{
							return layoutArea.replaceMarker("", marker, true);
						}
						if (detail.getPart().equals(Part.BEGIN_STOCK))
						{
							return layoutArea.replaceMarker("", marker, true);
						}
						else
						{
							final java.util.Currency currency = detail.getPaymentType().getCurrency().getCurrency();
							SettlementLayoutSettlementSection.doubleFormatter.setGroupingUsed(false);
							SettlementLayoutSettlementSection.doubleFormatter.setMinimumFractionDigits(currency
									.getDefaultFractionDigits());
							SettlementLayoutSettlementSection.doubleFormatter.setMaximumFractionDigits(currency
									.getDefaultFractionDigits());
							return layoutArea.replaceMarker(
									SettlementLayoutSettlementSection.doubleFormatter.format(detail.getDebit()), marker,
									false);
						}

					}
					case C:
					{
						if (detail.getPart().equals(Part.DIFFERENCE) && detail.getCredit() == 0D)
						{
							return layoutArea.replaceMarker("", marker, true);
						}
						else
						{
							final java.util.Currency currency = detail.getPaymentType().getCurrency().getCurrency();
							SettlementLayoutSettlementSection.doubleFormatter.setGroupingUsed(false);
							SettlementLayoutSettlementSection.doubleFormatter.setMinimumFractionDigits(currency
									.getDefaultFractionDigits());
							SettlementLayoutSettlementSection.doubleFormatter.setMaximumFractionDigits(currency
									.getDefaultFractionDigits());
							return layoutArea.replaceMarker(
									SettlementLayoutSettlementSection.doubleFormatter.format(detail.getCredit()),
									marker, false);
						}
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
		W;

		@Override
		public String label()
		{
			switch (this)
			{
				case W:
				{
					return "Währungscode";
				}
				default:
				{
					throw new RuntimeException("Invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutArea, final IPrintable printable, final String marker)
		{
			if (printable instanceof SettlementDetail)
			{
				final SettlementDetail detail = (SettlementDetail) printable;

				switch (this)
				{
					case W:
					{
						return layoutArea.replaceMarker(detail.getPaymentType().getCurrency().getCode(), marker, true);
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

	public enum TotalKey implements IKey
	{
		W, T, D, C;

		@Override
		public String label()
		{
			switch (this)
			{
				case W:
				{
					return "Währungscode";
				}
				case T:
				{
					return "Text (automatisch gesetzt)";
				}
				case D:
				{
					return "Differenz (gerechnet+)";
				}
				case C:
				{
					return "Differenz (gezählt+)";
				}
				default:
				{
					throw new RuntimeException("invalid key");
				}
			}
		}

		@Override
		public String replace(final ILayoutSection layoutArea, final IPrintable printable, final String marker)
		{
			if (printable instanceof SettlementDetail)
			{
				final SettlementDetail detail = (SettlementDetail) printable;

				switch (this)
				{
					case W:
					{
						final String code = detail.getPaymentType().getCurrency().getCode();
						return layoutArea.replaceMarker(code, marker, true);
					}
					case T:
					{
						String text = null;
						if (detail.getDebit() == detail.getCredit())
						{
							text = "Bestand korrekt";
						}
						else if (detail.getDebit() == 0)
						{
							text = "Zuwenig in Kasse";
						}
						else
						{
							text = "Zuviel in Kasse";
						}
						return layoutArea.replaceMarker(text, marker, false);
					}
					case D:
					{
						if (detail.getDebit() == detail.getCredit())
						{
							return layoutArea.replaceMarker("", marker, false);
						}
						if (detail.getDebit() > detail.getCredit())
						{
							final java.util.Currency currency = detail.getPaymentType().getCurrency().getCurrency();
							SettlementLayoutSettlementSection.doubleFormatter.setMinimumFractionDigits(currency
									.getDefaultFractionDigits());
							SettlementLayoutSettlementSection.doubleFormatter.setMaximumFractionDigits(currency
									.getDefaultFractionDigits());
							final String amount = SettlementLayoutSettlementSection.doubleFormatter.format(detail
									.getDebit());
							return layoutArea.replaceMarker(amount, marker, false);
						}
						else
						{
							return layoutArea.replaceMarker("", marker, false);
						}
					}
					case C:
					{
						if (detail.getDebit() == detail.getCredit())
						{
							return layoutArea.replaceMarker("", marker, false);
						}
						if (detail.getDebit() > detail.getCredit())
						{
							return layoutArea.replaceMarker("", marker, false);
						}
						else
						{
							final java.util.Currency currency = detail.getPaymentType().getCurrency().getCurrency();
							SettlementLayoutSettlementSection.doubleFormatter.setMinimumFractionDigits(currency
									.getDefaultFractionDigits());
							SettlementLayoutSettlementSection.doubleFormatter.setMaximumFractionDigits(currency
									.getDefaultFractionDigits());
							final String amount = SettlementLayoutSettlementSection.doubleFormatter.format(detail
									.getCredit());
							return layoutArea.replaceMarker(amount, marker, false);
						}
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
