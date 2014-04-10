package ch.eugster.colibri.print.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.PrintoutArea;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.PrintoutQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.print.Activator;
import ch.eugster.colibri.print.section.ILayoutSection.AreaType;
import ch.eugster.colibri.print.section.ILayoutSectionType;
import ch.eugster.colibri.print.section.ILayoutType;

public abstract class AbstractPrintService implements PrintService, EventHandler
{
	private boolean ready = false;

	private LogService logService;

	private PersistenceService persistenceService;

	private EventAdmin eventAdmin;

	/**
	 * For each receipt printer service there exists one layoutType if a receipt
	 * printer service is removed, there must be removed the correspondent
	 * layoutType to;
	 */
	private Map<String, ILayoutType> layoutTypes;

	private Map<String, ReceiptPrinterService> receiptPrinterServices;

	private ComponentContext context;

	private Salespoint salespoint;

	private Printout printout;

	public void addReceiptPrinterService(final ReceiptPrinterService receiptPrinterService)
	{
		if (this.receiptPrinterServices == null)
		{
			this.receiptPrinterServices = new HashMap<String, ReceiptPrinterService>();
		}
		this.receiptPrinterServices.put(receiptPrinterService.getReceiptPrinterSettings().getComponentName(),
				receiptPrinterService);

		if (this.layoutTypes == null)
		{
			this.layoutTypes = new HashMap<String, ILayoutType>();
		}
		final ILayoutType layoutType = this.getLayoutType(receiptPrinterService);
		this.layoutTypes.put(receiptPrinterService.getReceiptPrinterSettings().getComponentName(), layoutType);
	}
	
	protected abstract boolean openDrawerAllowed();

	@Override
	public ComponentContext getContext()
	{
		return this.context;
	}

	@Override
	public ILayoutType getLayoutType()
	{
		if (this.salespoint == null)
		{
			return null;
		}
		else
		{
			if (this.salespoint.getReceiptPrinterSettings() == null)
			{
				return null;
			}
			else
			{
				return this.layoutTypes.get(this.salespoint.getReceiptPrinterSettings().getReceiptPrinterSettings()
						.getComponentName());
			}
		}
	}

	@Override
	public ReceiptPrinterService getReceiptPrinterService()
	{
		if (this.salespoint == null)
		{
			return null;
		}
		else
		{
			if (this.salespoint.getReceiptPrinterSettings() == null)
			{
				return null;
			}
			else
			{
				return this.receiptPrinterServices.get(this.salespoint.getReceiptPrinterSettings().getReceiptPrinterSettings()
						.getComponentName());
			}
		}
	}

	@Override
	public ILayoutType getLayoutType(final String componentName)
	{
		return componentName == null ? null : this.layoutTypes.get(componentName);
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (this.isReady())
		{
			if (!this.salespoint.getReceiptPrinterSettings().isDeleted())
			{
				Boolean force = (Boolean) event.getProperty("force") == null ? Boolean.FALSE : (Boolean) event.getProperty("force");
				if (this.printout.isAutomaticPrint() || force.equals(Boolean.TRUE))
				{
					log(LogService.LOG_INFO, IPrintable.class.getName() + " wird gedruckt auf " + this.context.getProperties().get("component.name"));
					final IPrintable printable = (IPrintable) event.getProperty(IPrintable.class.getName());
					if (printable != null)
					{
						final String eventTopic = event.getTopic();
						final Object properties = this.context.getProperties().get("event.topics");
						String[] topics = null;
						if (properties instanceof String)
						{
							topics = new String[] { (String) properties };
						}
						else if (properties instanceof String[])
						{
							topics = (String[]) properties;
						}
						if (topics != null)
						{
							for (final String topic : topics)
							{
								if (topic.equals(eventTopic))
								{
									Boolean property = (Boolean) event.getProperty("open.drawer");
									boolean openDrawer = property == null ? false : property.booleanValue();
									log(LogService.LOG_INFO, "Schublade öffnen: " + Boolean.valueOf(openDrawer).toString());
									if (openDrawer && this.openDrawerAllowed())
									{
										if (printable instanceof Receipt)
										{
											Receipt receipt = (Receipt) printable;
											Stock[] stocks = receipt.getSettlement().getSalespoint().getStocks().toArray(new Stock[0]);
											Currency[] open = new Currency[stocks.length];
											for (int i = 0; i < stocks.length; i++)
											{
												Collection<Payment> payments = receipt.getPayments();
												for (Payment payment : payments)
												{
													if (payment.getPaymentType().getCurrency().getId().equals(stocks[i].getPaymentType().getCurrency().getId()))
													{
														if (payment.getPaymentType().isOpenCashdrawer())
														{
															open[i] = payment.getPaymentType().getCurrency();
														}
													}
												}
											}
											for (Currency currency : open)
											{
												if (currency != null)
												{
													this.getReceiptPrinterService().openDrawer(currency);
													log(LogService.LOG_INFO, "Schublade " + currency.getCode() + " geöffnet.");
												}
											}

										}
									}
									Integer copies = (Integer) event.getProperty("copies");
									if (copies == null)
									{
										copies = Integer.valueOf(1);
									}
									for (int i = 0; i < copies.intValue(); i++)
									{
										log(LogService.LOG_INFO, "Drucken " + i);
										this.printDocument(printable);
									}
								}
							}
						}
					}
				}
			}
		}
		else
		{
			this.eventAdmin
					.sendEvent(this
							.getEvent(new Status(
									IStatus.ERROR,
									Activator.PLUGIN_ID,
									"Der Druckservice ist nicht bereit. Vergewissern Sie sich, dass die Einstellungen für den Belegdruck eingerichtet sind.")));
		}
	}

	@Override
	public void printDocument(final IPrintable printable)
	{
		if (this.isReady())
		{
			final ILayoutType layoutType = this.getLayoutType();
			if (layoutType != null)
			{
				layoutType.printDocument(printable);
			}
		}
	}

	public void removeReceiptPrinterService(final ReceiptPrinterService receiptPrinterService)
	{
		if (this.receiptPrinterServices != null)
		{
			this.receiptPrinterServices.remove(receiptPrinterService.getReceiptPrinterSettings().getComponentName());
		}
		if (this.layoutTypes != null)
		{
			this.layoutTypes.remove(receiptPrinterService.getReceiptPrinterSettings().getComponentName());
		}
	}

	public void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	public void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	@Override
	public void testDocument(final ILayoutType layoutType, final Printout printout)
	{
		layoutType.testDocument(printout);
	}

	protected void activate(final ComponentContext context)
	{
		this.context = context;

		this.setReady(this.setPrintingContext());

		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " aktiviert.");
		}
	}

	protected void deactivate(final ComponentContext context)
	{
		this.context = null;

		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " deaktiviert.");
		}
	}

	protected Event getEvent(final IStatus status)
	{
		final Dictionary<String, Object> eventProps = new Hashtable<String, Object>();
		eventProps.put(EventConstants.BUNDLE, Activator.getDefault().getBundleContext().getBundle());
		eventProps.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundleContext().getBundle().getBundleId()));
		eventProps.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
		if (this.getContext() != null)
		{
			if (this.getContext().getServiceReference() != null)
			{
				eventProps.put(EventConstants.SERVICE, this.getContext().getServiceReference());
			}
			if (this.getContext().getProperties().get("component.id") != null)
			{
				eventProps.put(EventConstants.SERVICE_ID, this.getContext().getProperties().get("component.id"));
			}
			if (this.getContext().getProperties().get("component.name") != null)
			{
				eventProps.put(EventConstants.SERVICE_PID, this.getContext().getProperties().get("component.name"));
			}
		}
		eventProps.put(EventConstants.SERVICE_OBJECTCLASS, this.getClass().getName());
		eventProps.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		eventProps.put("status", status);
		return new Event(Topic.PRINT_ERROR.topic(), eventProps);
	}

	protected EventAdmin getEventAdmin()
	{
		return this.eventAdmin;
	}

	protected LogService getLogService()
	{
		return this.logService;
	}

	protected PersistenceService getPersistenceService()
	{
		return this.persistenceService;
	}

	protected ReceiptPrinterService getReceiptPrinterService(final String componentName)
	{
		return componentName == null ? null : this.receiptPrinterServices.get(componentName);
	}

	protected Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	protected boolean isReady()
	{
		return this.ready;
	}

	protected void setEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	protected void setReady(final boolean ready)
	{
		this.ready = ready;
	}

	protected void unsetEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	protected void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	private Printout getPrintout(final ConnectionService connectionService)
	{
		Printout printout = null;
		if (connectionService != null)
		{
			if (this.salespoint != null)
			{
				if (this.salespoint.getReceiptPrinterSettings() != null)
				{
					final PrintoutQuery query = (PrintoutQuery) connectionService.getQuery(Printout.class);
					final ILayoutType layoutType = this.getLayoutType();
					if (layoutType != null)
					{
						printout = query.findByPrintoutTypeAndSalespoint(layoutType.getId(), this.salespoint);
					}
					if (printout == null)
					{
						printout = query.findTemplate(layoutType.getId(), this.salespoint.getReceiptPrinterSettings()
								.getReceiptPrinterSettings());
					}
					if (printout == null)
					{
						printout = Printout.newInstance(layoutType.getId(), this.salespoint);
						printout.setAutomaticPrint(this.getLayoutType().automaticPrint());
						printout.setReceiptPrinterSettings(this.salespoint.getReceiptPrinterSettings()
								.getReceiptPrinterSettings());
						printout.setSalespoint(this.salespoint);
					}

					final ILayoutSectionType[] layoutSectionTypes = layoutType.getLayoutSectionTypes();
					for (final ILayoutSectionType layoutSectionType : layoutSectionTypes)
					{
						layoutSectionType.setColumnCount(printout.getColumns());
						final Integer printoutAreaType = Integer.valueOf(layoutSectionType.ordinal());
						PrintoutArea printoutArea = printout.getPrintoutArea(printoutAreaType);
						if ((printoutArea == null) || printout.isDeleted())
						{
							if (printout.getParent() != null)
							{
								printoutArea = printout.getParent().getPrintoutArea(printoutAreaType);
							}
						}
						if ((printoutArea != null) && !printoutArea.isDeleted())
						{
							layoutSectionType.getLayoutSection().setPattern(AreaType.TITLE,
									printoutArea.getTitlePattern());
							layoutSectionType.getLayoutSection().setPattern(AreaType.DETAIL,
									printoutArea.getDetailPattern());
							layoutSectionType.getLayoutSection().setPattern(AreaType.TOTAL,
									printoutArea.getTotalPattern());
							layoutSectionType.getLayoutSection().setPrintOption(AreaType.TITLE,
									printoutArea.getTitlePrintOption());
							layoutSectionType.getLayoutSection().setPrintOption(AreaType.DETAIL,
									printoutArea.getDetailPrintOption());
							layoutSectionType.getLayoutSection().setPrintOption(AreaType.TOTAL,
									printoutArea.getTotalPrintOption());
						}
					}
				}
			}
		}
		return printout;
	}

	private Salespoint getSalespoint(final ConnectionService service)
	{
		if (service != null && service.isConnected())
		{
			final CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) service
					.getQuery(CommonSettings.class);
			final CommonSettings commonSettings = commonSettingsQuery.findDefault();

			if (commonSettings != null)
			{
				final SalespointQuery query = (SalespointQuery) service.getQuery(Salespoint.class);
				final Collection<Salespoint> salespoints = query.selectByHost(query.getHostname(commonSettings
						.getHostnameResolver()));
				if (!salespoints.isEmpty())
				{
					return salespoints.iterator().next();
				}
			}
		}
		return null;
	}

	private boolean setPrintingContext()
	{
		this.salespoint = this.getSalespoint(this.persistenceService.getCacheService());
		this.printout = this.getPrintout(this.persistenceService.getCacheService());

//		if (this.salespoint == null)
//		{
//			this.salespoint = this.getSalespoint(this.persistenceService.getServerService());
//		}
//		if (this.printout == null)
//		{
//			this.printout = this.getPrintout(this.persistenceService.getServerService());
//		}
		if ((this.salespoint == null) || (this.printout == null))
		{
			return false;
		}
		return true;
	}

	public static String padLeft(final String s, final int n)
	{
		return String.format("%1$" + n + "s", s);
	}

	public static String padRight(final String s, final int n)
	{
		return String.format("%1$-" + n + "s", s);
	}

	private void log(int level, String message)
	{
		if (logService != null)
		{
			logService.log(level, message);
		}
	}
}
