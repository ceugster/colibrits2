package ch.eugster.colibri.report.settlement.views;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.widgets.Composite;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SettlementDetail;
import ch.eugster.colibri.persistence.model.SettlementInternal;
import ch.eugster.colibri.persistence.model.SettlementMoney;
import ch.eugster.colibri.persistence.model.SettlementPayedInvoice;
import ch.eugster.colibri.persistence.model.SettlementPayment;
import ch.eugster.colibri.persistence.model.SettlementPosition;
import ch.eugster.colibri.persistence.model.SettlementReceipt;
import ch.eugster.colibri.persistence.model.SettlementRestitutedPosition;
import ch.eugster.colibri.persistence.model.SettlementTax;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.report.engine.ReportService;
import ch.eugster.colibri.report.settlement.model.Section;
import ch.eugster.colibri.report.settlement.model.SettlementEntry;

public abstract class AbstractSettlementCompositeChild extends Composite implements ISettlementCompositeChild
{
	protected SettlementView parentView;

	private final ListenerList listeners = new ListenerList();

	/**
	 * @param parent
	 * @param style
	 */
	public AbstractSettlementCompositeChild(Composite parent, SettlementView parentView, int style)
	{
		super(parent, style);
		this.parentView = parentView;
		this.init();
	}
	
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		this.listeners.remove(listener);
	}

	protected Salespoint[] getSelectedSalespoints()
	{
		return this.parentView.getSelectedSalespoints();
	}
	
	protected Calendar[] getSelectedDateRange()
	{
		return this.parentView.getSelectedDateRange();
	}
	
	protected ReportService.Destination getSelectedDestination()
	{
		return this.parentView.getSelectedDestination();
	}
	
	protected abstract void init();

	protected Map<Long, SettlementEntry> createPositionSection(Map<Long, SettlementEntry> section,
			List<SettlementPosition> positions)
	{
		Collections.sort(positions, new Comparator<SettlementPosition>() 
		{
			@Override
			public int compare(SettlementPosition pos1, SettlementPosition pos2) 
			{
				String code1 = pos1.getCode() == null ? "" : pos1.getCode();
				String code2 = pos2.getCode() == null ? "" : pos2.getCode();
				int val = code1.compareTo(code2);
				if (val == 0)
				{
					String name1 = pos1.getName() == null ? "" : pos1.getName();
					String name2 = pos2.getName() == null ? "" : pos2.getName();
					val = name1.compareTo(name2);
				}
				return val;
			}
		});
		for (SettlementPosition position : positions)
		{
			SettlementEntry entry = section.get(position.getProductGroup().getId());
			if (entry == null)
			{
				Section sectionType = position.getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL) ? Section.INTERNAL : Section.POSITION;
				entry = new SettlementEntry(sectionType);
				entry.setGroup(position.getProductGroup().getProductGroupType().ordinal());
				entry.setCode(position.getCode());
				entry.setText(position.getName());
				section.put(position.getProductGroup().getId(), entry);
			}
			int quantity = (entry.getQuantity() == null ? 0 : entry.getQuantity().intValue()) + position.getQuantity();
			entry.setQuantity(quantity == 0 ? null : Integer.valueOf(quantity));

			double amount = entry.getAmount1() == null ? 0D : entry.getAmount1().doubleValue();
			amount += position.getDefaultCurrencyAmount();
//			entry.setAmount1(amount == 0D ? null : Double.valueOf(amount));
			entry.setAmount1(Double.valueOf(amount));

			amount = entry.getAmount2() == null ? 0D : entry.getAmount2().doubleValue();
			amount -= position.getTaxAmount();
//			entry.setAmount2(amount == 0D ? null : Double.valueOf(amount));
			entry.setAmount2(Double.valueOf(amount));
		}
		return section;
	}

	protected Map<Long, SettlementEntry> createPaymentSection(Map<Long, SettlementEntry> section,
			Collection<SettlementPayment> payments, Currency referenceCurrency)
	{
		for (SettlementPayment payment : payments)
		{
			long id = 0L;
			if (payment.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
			{
				id = payment.getDefaultCurrencyAmount() < 0D ? -payment.getPaymentType().getId().longValue() : payment.getPaymentType().getId().longValue();
			}
			else
			{
				id = payment.getPaymentType().getId().longValue();
			}
			SettlementEntry entry = section.get(Long.valueOf(id));
			if (entry == null)
			{
				entry = new SettlementEntry(Section.PAYMENT);
				section.put(id, entry);
				entry.setGroup(payment.getPaymentType().getCurrency().getId().equals(referenceCurrency.getId()) ? 0 : 1);
				entry.setCode(payment.getPaymentType().getCode());
				entry.setText((id < 0L ? "Rückgeld " : "") + payment.getPaymentType().getName());
			}
			int quantity = (entry.getQuantity() == null ? 0 : entry.getQuantity().intValue()) + payment.getQuantity();
			entry.setQuantity(quantity == 0 ? null : Integer.valueOf(quantity));

			double amount = entry.getAmount1() == null ? 0D : entry.getAmount1().doubleValue();
			amount += payment.getForeignCurrencyAmount();
			entry.setAmount1(amount == 0D ? null : Double.valueOf(amount));

			amount = entry.getAmount2() == null ? 0D : entry.getAmount2().doubleValue();
			amount += payment.getDefaultCurrencyAmount();
			entry.setAmount2(amount == 0D ? null : Double.valueOf(amount));
		}
		return section;
	}

	protected Map<Long, SettlementEntry> createRestitutedPositionSection(Map<Long, SettlementEntry> section,
			Collection<SettlementRestitutedPosition> restitutedPositions)
	{
		for (SettlementRestitutedPosition restitutedPosition : restitutedPositions)
		{
			SettlementEntry entry = section.get(restitutedPosition.getPosition().getId());
			if (entry == null)
			{
				entry = new SettlementEntry(Section.RESTITUTION);
				section.put(restitutedPosition.getPosition().getId(), entry);
				entry.setGroup(restitutedPosition.getPosition().getId().intValue());
				if (restitutedPosition.getPosition().getProduct() == null)
				{
					entry.setCode(restitutedPosition.getPosition().getProductGroup().getCode());
					entry.setText(restitutedPosition.getPosition().getProductGroup().getName());
				}
				else
				{
					entry.setCode(restitutedPosition.getPosition().getProduct().getCode());
					entry.setText(restitutedPosition.getPosition().getProduct().getAuthorAndTitleShortForm());
				}
			}
			int quantity = (entry.getQuantity() == null ? 0 : entry.getQuantity().intValue())
					+ restitutedPosition.getPosition().getQuantity();
			entry.setQuantity(quantity == 0 ? null : Integer.valueOf(quantity));

			double amount = entry.getAmount1() == null ? 0D : entry.getAmount1().doubleValue();
			amount += restitutedPosition.getPosition().getAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
					Position.AmountType.NETTO);
			entry.setAmount1(amount == 0D ? null : Double.valueOf(amount));

			amount = entry.getAmount2() == null ? 0D : entry.getAmount2().doubleValue();
			amount += -restitutedPosition.getPosition().getTaxAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
			entry.setAmount2(amount == 0D ? null : Double.valueOf(amount));
		}
		return section;
	}

	protected Map<Long, SettlementEntry> createPayedInvoiceSection(Map<Long, SettlementEntry> section,
			Collection<SettlementPayedInvoice> payedInvoices)
	{
		DateFormat df = SimpleDateFormat.getDateInstance();
		for (SettlementPayedInvoice payedInvoice : payedInvoices)
		{
			SettlementEntry entry = section.get(payedInvoice.getPositionId());
			if (entry == null)
			{
				entry = new SettlementEntry(Section.PAYED_INVOICES);
				section.put(payedInvoice.getPositionId(), entry);
				entry.setGroup(payedInvoice.getPositionId().intValue());
				if (payedInvoice.getDate() == null && payedInvoice.getNumber().isEmpty())
				{
					entry.setCode(payedInvoice.getProductGroup().getCode());
					entry.setText(payedInvoice.getProductGroup().getName());
				}
				else if (payedInvoice.getNumber().isEmpty())
				{
					entry.setCode(df.format(payedInvoice.getDate().getTime()));
					entry.setText(df.format(payedInvoice.getDate().getTime()));
				}
				else if (payedInvoice.getDate() == null)
				{
					entry.setCode(payedInvoice.getNumber());
					entry.setText(payedInvoice.getNumber());
				}
				else
				{
					entry.setCode(payedInvoice.getNumber());
					entry.setText(payedInvoice.getNumber() + " " + df.format(payedInvoice.getDate().getTime()));
				}
				entry.setAmount1(0D);
			}
			int quantity = (entry.getQuantity() == null ? 0 : entry.getQuantity().intValue())
					+ payedInvoice.getQuantity();
			entry.setQuantity(quantity == 0 ? null : Integer.valueOf(quantity));

			entry.setAmount1(null);

			double amount = entry.getAmount2() == null ? 0D : entry.getAmount2().doubleValue();
			amount += payedInvoice.getDefaultCurrencyAmount();
			entry.setAmount2(amount == 0D ? null : Double.valueOf(amount));
		}
		return section;
	}

	protected Map<Long, SettlementEntry> createInternalSection(Map<Long, SettlementEntry> section,
			Collection<SettlementInternal> internals, boolean internalDetails)
	{
		for (SettlementInternal internal : internals)
		{
			Long id = internalDetails ? internal.getPosition().getId() : internal.getPosition().getProductGroup().getId();
			int groupId = internal.getPosition().getProductGroup().getProductGroupType().ordinal();
			SettlementEntry entry = section.get(id);
			if (entry == null)
			{
				entry = new SettlementEntry(Section.INTERNAL);
				section.put(id, entry);
				entry.setGroup(groupId);
				entry.setCode(internal.getPosition().getProductGroup().getCode());
				entry.setText(internal.getPosition().getProductGroup().getName());
			}
			int quantity = (entry.getQuantity() == null ? 0 : entry.getQuantity().intValue())
					+ internal.getPosition().getQuantity();
			entry.setQuantity(quantity == 0 ? null : Integer.valueOf(quantity));

			if (internal.getPosition().getProductGroup().getProductGroupType().equals(ProductGroupType.ALLOCATION))
			{
				double amount = entry.getAmount1() == null ? 0D : entry.getAmount1().doubleValue();
				amount += Math.abs(internal.getPosition().getAmount(Receipt.QuotationType.FOREIGN_CURRENCY,
						Position.AmountType.NETTO));
				entry.setAmount1(amount == 0D ? null : Double.valueOf(amount));
			}
			else if (internal.getPosition().getProductGroup().getProductGroupType().equals(ProductGroupType.WITHDRAWAL))
			{
				double amount = entry.getAmount2() == null ? 0D : entry.getAmount2().doubleValue();
				amount += Math.abs(internal.getPosition().getAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
						Position.AmountType.NETTO));
				entry.setAmount2(amount == 0D ? null : Double.valueOf(amount));
			}
		}
		return section;
	}

	protected Map<Long, SettlementEntry> createTaxSection(Map<Long, SettlementEntry> section,
			Collection<SettlementTax> taxes)
	{
		for (SettlementTax tax : taxes)
		{
			SettlementEntry entry = section.get(tax.getCurrentTax().getId());
			if (entry == null)
			{
				entry = new SettlementEntry(Section.TAX);
				section.put(tax.getCurrentTax().getId(), entry);
				entry.setGroup(0);
				entry.setCode(tax.getCurrentTax().getTax().format());
				entry.setText(tax.getCurrentTax().getTax().format());
			}
			int quantity = (entry.getQuantity() == null ? 0 : entry.getQuantity().intValue()) + tax.getQuantity();
			entry.setQuantity(quantity == 0 ? null : Integer.valueOf(quantity));

			double amount = entry.getAmount1() == null ? 0D : entry.getAmount1().doubleValue();
			amount += tax.getBaseAmount();
//			entry.setAmount1(amount == 0D ? null : Double.valueOf(amount));
			entry.setAmount1(Double.valueOf(amount));

			amount = entry.getAmount2() == null ? 0D : entry.getAmount2().doubleValue();
			amount += -tax.getTaxAmount();
//			entry.setAmount2(amount == 0D ? null : Double.valueOf(amount));
			entry.setAmount2(Double.valueOf(amount));
		}
		return section;
	}

	protected Map<Long, SettlementEntry> createReceiptSection(Map<Long, SettlementEntry> section,
			Collection<SettlementReceipt> receipts)
	{
		DateFormat df = SimpleDateFormat.getInstance();
		for (SettlementReceipt receipt : receipts)
		{
			SettlementEntry entry = section.get(receipt.getReceipt().getId());
			if (entry == null)
			{
				NumberFormat nf = new DecimalFormat(receipt.getSettlement().getSalespoint().getCommonSettings()
						.getReceiptNumberFormat());
				entry = new SettlementEntry(Section.REVERSED_RECEIPT);
				section.put(receipt.getReceipt().getId(), entry);
				entry.setGroup(0);
				entry.setCode(nf.format(receipt.getNumber()));
				entry.setText(nf.format(receipt.getNumber()) + " " + df.format(receipt.getTime().getTime()));

				entry.setQuantity(1);

				double amount = entry.getAmount2() == null ? 0D : entry.getAmount2().doubleValue();
				amount += receipt.getAmount();
				entry.setAmount2(amount == 0D ? null : Double.valueOf(amount));
			}
		}
		return section;
	}

	protected Map<Long, SettlementEntry> createDetailSection(Map<Long, SettlementEntry> section,
			Collection<SettlementDetail> details)
	{
		for (SettlementDetail detail : details)
		{
			SettlementEntry entry = section.get(Long.valueOf(detail.getStock().getId()));
			if (entry == null)
			{
				entry = new SettlementEntry(Section.SETTLEMENT);
				section.put(
						Long.valueOf(detail.getStock().getId().toString()
								+ new Integer(detail.getPart().ordinal()).toString()), entry);
				entry.setGroup(detail.getStock().getId().intValue());
				entry.setCashtype(detail.getPart().ordinal());
				entry.setCode(detail.getStock().getPaymentType().getCurrency().getCode());
				entry.setText(detail.getStock().getPaymentType().getCurrency().getCode() + " "
						+ detail.getPart().label(detail));
			}
			int quantity = (entry.getQuantity() == null ? 0 : entry.getQuantity().intValue()) + detail.getQuantity();
			entry.setQuantity(quantity == 0 ? null : Integer.valueOf(quantity));

			double amount = entry.getAmount1() == null ? 0D : entry.getAmount1().doubleValue();
			amount += detail.getDebit();
			entry.setAmount1(Double.valueOf(amount));

			amount = entry.getAmount2() == null ? 0D : entry.getAmount2().doubleValue();
			amount += detail.getCredit();
			entry.setAmount2(Double.valueOf(amount));
		}
		return section;
	}

	protected Map<Long, SettlementEntry> createMoneySection(Map<Long, SettlementEntry> section,
			Collection<SettlementMoney> moneys)
	{
		for (SettlementMoney money : moneys)
		{
			SettlementEntry entry = null;
			entry = section.get(money.getMoney() == null ? money.getPaymentType().getId() : money.getMoney().getId());
			if (entry == null)
			{
				entry = new SettlementEntry(Section.CASH_CHECK);
				section.put(money.getMoney() == null ? money.getPaymentType().getId() : money.getMoney().getId(), entry);
				entry.setGroup(money.getPaymentType().getCurrency().getId().intValue());
				entry.setCode(money.getStock().getPaymentType().getCurrency().getCode());
				entry.setText(money.getStock().getPaymentType().getCurrency().getCode() + " " + money.getText());
			}
			int quantity = (entry.getQuantity() == null ? 0 : entry.getQuantity().intValue()) + money.getQuantity();
			entry.setQuantity(quantity == 0 ? null : Integer.valueOf(quantity));

			double amount = entry.getAmount2() == null ? 0D : entry.getAmount2().doubleValue();
			amount += money.getAmount();
			entry.setAmount2(amount == 0D ? null : Double.valueOf(amount));
		}
		return section;
	}
}
