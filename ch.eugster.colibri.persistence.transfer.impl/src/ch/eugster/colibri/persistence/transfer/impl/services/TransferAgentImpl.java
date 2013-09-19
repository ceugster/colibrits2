package ch.eugster.colibri.persistence.transfer.impl.services;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.service.CacheService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.ServerService;
import ch.eugster.colibri.persistence.transfer.impl.Activator;
import ch.eugster.colibri.persistence.transfer.services.TransferAgent;

public class TransferAgentImpl implements TransferAgent
{
	private LogService logService;

	private PersistenceService persistenceService;

	@Override
	public IStatus transfer(final int maxResult)
	{
		IStatus status = Status.OK_STATUS;

		final CacheService cacheService = this.persistenceService.getCacheService();
		final ServerService serverService = this.persistenceService.getServerService();

		if (!serverService.isLocal() && serverService.isConnected())
		{
			final ReceiptQuery queryService = (ReceiptQuery) cacheService.getQuery(Receipt.class);
			final Receipt[] receipts = queryService.selectTransferables(maxResult).toArray(new Receipt[0]);
			for (final Receipt receipt : receipts)
			{
				status = this.transfer(receipt);
			}
		}
		return status;
	}

	public void log(String message)
	{
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, message);
		}
	}

	@Override
	public IStatus transfer(Receipt receipt)
	{
		IStatus status = Status.OK_STATUS;

		final ServerService serverService = this.persistenceService.getServerService();
		if (!serverService.isLocal())
		{
			if (!serverService.isConnected())
			{
				serverService.connect();
			}
			if (serverService.isConnected())
			{
				log("Transfer " + receipt.getSettlement().getSalespoint().getHost() + " " + receipt.getNumber() + "...");
				final ReceiptQuery query = (ReceiptQuery) persistenceService.getServerService().getQuery(Receipt.class);

				if (receipt.getOtherId() == null)
				{
					status = insert(persistenceService, receipt);
				}
				else
				{
					Receipt target = query.findByOtherId(receipt.getOtherId());
					if (target != null && target.getId().equals(receipt.getOtherId()))
					{
						status = this.update(receipt, target);
					}
				}
			}
		}
		return status;
	}

	private IStatus insert(PersistenceService service, Receipt receipt)
	{
		IStatus status = Status.OK_STATUS;
		Receipt target = this.clone(receipt);
		try
		{
			target = (Receipt) service.getServerService().merge(target, false);
			if (target.getId() != null)
			{
				receipt = updateSource(target, receipt);
				receipt = (Receipt) service.getCacheService().merge(receipt, false);
				log("Transferred " + receipt.getSettlement().getSalespoint().getHost() + " " + receipt.getNumber() + "...");
			}
		}
		catch (Exception exception)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Speichern des Belegs fehlgeschlagen", exception);
		}
		return status;
	}

	private Receipt updateSource(Receipt serverReceipt, Receipt localReceipt)
	{
		localReceipt.setTransferred(true);
		localReceipt.setOtherId(serverReceipt.getId());
		for (Position localPosition : localReceipt.getAllPositions())
		{
			if (localPosition.getId() != null)
			{
				for (Position serverPosition : serverReceipt.getAllPositions())
				{
					if (serverPosition.getOtherId() != null)
					{
						if (localPosition.getId().equals(serverPosition.getOtherId()))
						{
							localPosition.setOtherId(serverPosition.getId());
						}
					}
				}
			}
		}
		return localReceipt;
	}

	protected void activate(final ComponentContext componentContext)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name")
					+ " aktiviert.");
		}
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name")
					+ " deaktiviert.");
		}
	}

	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	protected void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	protected void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	private Product clone(final Position position, final Product source)
	{
		if (source == null)
		{
			return null;
		}

		final ServerService serverService = this.persistenceService.getServerService();

		ExternalProductGroup externalProductGroup = null;
		if (source.getExternalProductGroup() != null)
		{
			externalProductGroup = (ExternalProductGroup) serverService.find(ExternalProductGroup.class, source
					.getExternalProductGroup().getId());
		}

		final Product target = Product.newInstance(position);
		target.setExternalProductGroup(externalProductGroup);
		target.setAuthor(source.getAuthor());
		target.setCode(source.getCode());
		target.setDeleted(source.isDeleted());
		target.setInvoiceDate(source.getInvoiceDate());
		target.setInvoiceNumber(source.getInvoiceNumber());
		target.setPublisher(source.getPublisher());
		target.setTimestamp(source.getTimestamp());
		target.setTitle(source.getTitle());
		target.setUpdate(source.getUpdate());
		target.setVersion(0);
		return target;
	}

	private Salespoint getSalespoint(ServerService service, Receipt receipt)
	{
		Salespoint salespoint = (Salespoint) service.find(Salespoint.class, receipt.getSettlement().getSalespoint()
				.getId());
		boolean saveSalespoint = false;
		if (salespoint.getSettlement() == null)
		{
			saveSalespoint = true;
			salespoint.setSettlement(Settlement.newInstance(salespoint));
		}
		if (salespoint.getCurrentReceiptNumber().longValue() <= receipt.getNumber().longValue())
		{
			saveSalespoint = true;
			salespoint.setCurrentReceiptNumber(Long.valueOf(receipt.getNumber().longValue() + 1L));
		}
		if (saveSalespoint)
		{
			salespoint = (Salespoint) service.merge(salespoint);
		}
		return salespoint;
	}

	private Receipt clone(final Receipt source)
	{
		Receipt target = null;
		final ServerService serverService = this.persistenceService.getServerService();

		try
		{
			final User user = (User) serverService.find(User.class, source.getUser().getId());
			final Salespoint salespoint = getSalespoint(serverService, source);

			final Currency referenceCurrency = (Currency) serverService.find(Currency.class, source.getReferenceCurrency()
					.getId());
			final Currency defaultCurrency = (Currency) serverService.find(Currency.class, source.getDefaultCurrency()
					.getId());
			final Currency foreignCurrency = (Currency) serverService.find(Currency.class, source.getForeignCurrency()
					.getId());

			target = Receipt.newInstance(salespoint.getSettlement(), source.getUser());
			target.setOtherId(source.getId());
			target.setBookkeepingTransaction(source.getBookkeepingTransaction());
			target.setCustomerCode(source.getCustomerCode());
			target.setDefaultCurrency(defaultCurrency);
			target.setDeleted(source.isDeleted());
			target.setForeignCurrency(foreignCurrency);
			target.setNumber(source.getNumber());
			target.setProviderUpdated(source.isProviderUpdated());
			target.setReferenceCurrency(referenceCurrency);
			target.setState(source.getState());
			target.setTimestamp(source.getTimestamp());
			target.setTransaction(source.getTransaction());
			target.setTransferred(true);
			target.setUpdate(source.getUpdate());
			target.setUser(user);
			target.setVersion(0);

			final Position[] positions = source.getAllPositions().toArray(new Position[0]);
			for (final Position position : positions)
			{
				target.addPosition(this.clone(target, position));
			}

			final Payment[] payments = source.getAllPayments().toArray(new Payment[0]);
			for (final Payment payment : payments)
			{
				target.addPayment(this.clone(target, payment));
			}
		}
		catch (Exception e)
		{
			serverService.resetEntityManager(e);
		}
		return target;
	}

	private Payment clone(final Receipt receipt, final Payment source)
	{
		final PaymentType paymentType = (PaymentType) this.persistenceService.getServerService().find(
				PaymentType.class, source.getPaymentType().getId());

		final Payment target = Payment.newInstance(receipt);
		target.setBookProvider(source.isBookProvider());
		target.setProviderBooked(source.isProviderBooked());
		target.setProviderId(source.getProviderId());
		target.setProviderState(source.getProviderState());
		target.setAmount(source.getAmount());
		target.setBack(source.isBack());
		target.setDeleted(source.isDeleted());
		target.setForeignCurrencyQuotation(source.getForeignCurrencyQuotation());
		target.setForeignCurrencyRoundFactor(source.getForeignCurrencyRoundFactor());
		target.setPaymentType(paymentType);
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getUpdate());
		target.setVersion(0);
		return target;
	}

	private Position clone(final Receipt receipt, final Position source)
	{
		final ServerService serverService = this.persistenceService.getServerService();
		final ProductGroup productGroup = (ProductGroup) serverService.find(ProductGroup.class, source
				.getProductGroup().getId());
		final CurrentTax currentTax = (CurrentTax) serverService.find(CurrentTax.class, source.getCurrentTax().getId());

		Currency foreignCurrency = null;
		if (source.getForeignCurrency() != null)
		{
			foreignCurrency = (Currency) serverService.find(Currency.class, source.getForeignCurrency().getId());
		}

		final Position target = Position.newInstance(receipt);
		target.setOtherId(source.getId());
		target.setProductGroup(productGroup);
		target.setCurrentTax(currentTax);
		target.setDeleted(source.isDeleted());
		target.setDiscount(source.getDiscount());
		target.setForeignCurrency(foreignCurrency);
		target.setOption(source.getOption());
		target.setOrder(source.getOrder());
		target.setOrdered(source.isOrdered());
		target.setPrice(source.getPrice());
		target.setQuantity(source.getQuantity());
		target.setSearchValue(source.getSearchValue());
		target.setTaxPercents(source.getTaxPercents());
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getUpdate());
		target.setVersion(0);
		target.setProduct(this.clone(target, source.getProduct()));
		target.setBookProvider(source.isBookProvider());
		target.setOrder(source.getOrder());
		target.setFromStock(source.isFromStock());
		target.setProvider(source.getProvider());
		target.setProviderBooked(source.isProviderBooked());
		target.setProviderState(source.getProviderState());
		return target;
	}

	/**
	 * This method is used to update already transferred receipts. There are
	 * several opportunities when updates has to be performed:
	 * 
	 * 1. A receipt with <code>State.REVERSED</code> has been updated to
	 * <code>State.SAVED</code>. In this case the state in the existing receipt
	 * in the central database has to be set to <code>State.SAVED</code>.
	 * 
	 * 2. A receipt with <code>State.SAVED</code> has been updated to
	 * <code>State.REVERSED</code>. In this case the state of the existing
	 * receipt in the central database has to be set to
	 * <code>State.REVERSED</code>.
	 * 
	 * 3. A receipt with <code>State.SAVED</code> has been updated to
	 * <code>State.CLOSED</code>. This means, that the receipt is no longer
	 * editable (say can no longer be toggled between
	 * <code>State.REVERSED</code> and <code>State.SAVED</code>.
	 * 
	 * @param target
	 *            The receipt of the target database
	 * @param source
	 *            The receipt of the source (local) database
	 */
	private IStatus update(Receipt source, Receipt target)
	{
		IStatus status = Status.OK_STATUS;
		try
		{
			target.setState(source.getState());
			target = (Receipt) this.persistenceService.getServerService().merge(target, false);
			source.setTransferred(true);
			source = (Receipt) this.persistenceService.getCacheService().merge(source, false);
		}
		catch (RuntimeException e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Speichern des Belegs fehlgeschlagen");
		}
		return status;
	}
}
