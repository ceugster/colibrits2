package ch.eugster.colibri.export;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jdom.Attribute;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.export.service.ExportService;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ExportServiceComponent implements ExportService, EventHandler
{
	private static final String EXPORT_FOLDER = "export";

	private static final String WORK_FOLDER = "working";

	private static final String WORK_FILE = "export.work";

	private static final String TRANSFER_DTD = "transfer.dtd";

	private static final String EXPORT_FILE_EXTENSION = ".xml";

	private Document export;
	
	private Boolean doExport = null;
	
	private String exportPath;
	
	private LogService logService;
	
	private ComponentContext context;
	
	private PersistenceService persistenceService;
	
	private Collection<BarcodeVerifier> barcodeVerifiers = new ArrayList<BarcodeVerifier>();
	
	
	public ExportServiceComponent()
	{
	}

	public void setLogService(LogService logService)
	{
		this.logService = logService;
	}
	
	public void unsetLogService(LogService logService)
	{
		this.logService = null;
	}
	
	public void setPersistenceService(PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	public void unsetPersistenceService(PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}
	/*
	 * Only use as dynamic service
	 */
	protected void activate(ComponentContext context)
	{
		this.context = context;
		log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " registriert.");
	}
	
	public void addBarcodeVerifier(BarcodeVerifier barcodeVerifier)
	{
		barcodeVerifiers.add(barcodeVerifier);
	}
	
	public void removeBarcodeVerifier(BarcodeVerifier barcodeVerifier)
	{
		barcodeVerifiers.remove(barcodeVerifier);
	}
	
	private void log(int level, String message)
	{
		if (logService != null)
		{
			logService.log(context.getServiceReference(), level, message);
		}
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (doExport == null)
		{
			SalespointQuery query = (SalespointQuery) persistenceService.getCacheService().getQuery(Salespoint.class);
			Salespoint salespoint = query.getCurrentSalespoint();
			doExport = salespoint.isExport();
			exportPath = salespoint.getExportPath();
			load();
		}
		if (doExport.booleanValue())
		{
			if (event.getTopic().equals("ch/eugster/colibri/client/store/receipt"))
			{
				IPrintable printable = (IPrintable) event.getProperty("ch.eugster.colibri.persistence.model.print.IPrintable");
				if (printable instanceof Receipt)
				{
					Receipt receipt = (Receipt) printable;
					this.update(receipt);
					log(LogService.LOG_INFO, "Beleg " + receipt.getNumber() + " exportiert.");
				}
			}
			else if (event.getTopic().equals("ch/eugster/colibri/client/print/settlement"))
			{
				IPrintable printable = (IPrintable) event.getProperty(IPrintable.class.getName());
				if (printable instanceof Settlement)
				{
					Settlement settlement = (Settlement) printable;
					this.settle(settlement);
					String settle = null;
					if (settlement.getSettled() != null)
					{
						DateFormat format = SimpleDateFormat.getDateTimeInstance();
						settle = format.format(settlement.getSettled().getTime()) + " ";
					}
					log(LogService.LOG_INFO, "Abschluss " + settle == null ? "" : settle + "exportiert.");
				}
			}
		}
	}

	private void load()
	{
		File file = this.getFile(ExportServiceComponent.WORK_FILE);
		if (file.exists())
		{
			SAXBuilder builder = new SAXBuilder();
			builder.setValidation(true);
			try 
			{
				export = builder.build(file);
			} 
			catch (JDOMException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			export = initialize();
		}
		log(LogService.LOG_INFO, "Exportdokument geladen.");
	}

	private Document initialize()
	{
		Document document = new Document();
		document.setDocType(new DocType("transfer", TRANSFER_DTD));
		document.setRootElement(new Element("transfer"));
		document.getRootElement().setAttribute(new Attribute("salespoint", ""));
		document.getRootElement().setAttribute(new Attribute("date", ""));
		document.getRootElement().setAttribute(new Attribute("count", ""));
		return document;
	}
	
	private void save(Settlement settlement)
	{
		String filename = null;
		if (settlement == null)
		{
			filename = ExportServiceComponent.WORK_FILE;
		}
		else
		{
			export.getRootElement().setAttribute(new Attribute("salespoint", settlement.getSalespoint().getMapping() == null ? settlement.getSalespoint().getHost() : settlement.getSalespoint().getMapping()));
			export.getRootElement().setAttribute(new Attribute("date", settlement.valueOf(Long.valueOf(settlement.getSettled().getTimeInMillis()).toString())));
			export.getRootElement().setAttribute(new Attribute("count", Integer.valueOf(export.getRootElement().getChildren("receipt").size()).toString()));
			String salespoint = export.getRootElement().getAttributeValue("salespoint");
			String date = export.getRootElement().getAttributeValue("date");
			filename = salespoint + date + EXPORT_FILE_EXTENSION;
		}
		save(filename);
	}
	
	private void save(String filename)
	{
		boolean save = true;
		if (filename.equals(ExportServiceComponent.WORK_FILE))
		{
			if (export == null || export.getRootElement().getChildren().size() == 0)
			{
				save = false;
			}
		}
		if (save)
		{
			try 
			{
				final Format format = Format.getPrettyFormat();
				final XMLOutputter outputter = new XMLOutputter();
				outputter.setFormat(format);
				FileOutputStream out = new FileOutputStream(getFile(filename));
				outputter.output(export, out);
				out.close();
				log(LogService.LOG_INFO, "Export ausgeführt.");
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private File getExportFolder()
	{
		File exportFolder = new File(exportPath);
		if (!exportFolder.exists())
		{
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final File root = workspace.getRoot().getRawLocation().toFile();
			exportFolder = new File(root.getAbsolutePath() + File.separator
					+ EXPORT_FOLDER);
			if (!exportFolder.exists())
			{
				exportFolder.mkdir();
			}
		}
		return exportFolder;
	}
	
	private File getWorkFolder()
	{
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final File root = workspace.getRoot().getRawLocation().toFile();
		File workFolder = new File(root.getAbsolutePath() + File.separator + WORK_FOLDER);
		if (!workFolder.exists())
		{
			workFolder.mkdir();
		}
		if (workFolder.exists())
		{
			File dtdFile = new File(workFolder.getAbsolutePath() + File.separator
					+ TRANSFER_DTD);
			if (!dtdFile.exists())
			{
				createDTDFile(dtdFile);
			}
		}
		return workFolder;
	}
	
	private File getFile(String name)
	{
		if (name.equals(ExportServiceComponent.WORK_FILE))
		{
			return new File(this.getWorkFolder().getAbsolutePath() + File.separator + ExportServiceComponent.WORK_FILE);
		}
		else
		{
			return new File(this.getExportFolder().getAbsolutePath()+ File.separator + name);
		}
	}

	private void createDTDFile(File dtdFile)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dtdFile)));
			URL url = Activator.getDefault().getBundle().getEntry("/" + TRANSFER_DTD);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = reader.readLine();
			while (line != null)
			{
				writer.write(line + "\n");
				line = reader.readLine();
			}
			reader.close();
			writer.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void deactivate(ComponentContext context)
	{
		this.save(WORK_FILE);
		log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " registriert.");
		this.context = null;
	}

	@Override
	public void add(Receipt receipt) 
	{
		Element root = export.getRootElement();
		Element element = new Element("receipt");
		convertToJdomElement(receipt, element);
		root.addContent(element);
		save(WORK_FILE);
	}

	@Override
	public void update(Receipt receipt) 
	{
		Element root = export.getRootElement();
		@SuppressWarnings("unchecked")
		Iterator<Element> receipts = root.getDescendants(new Filter() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 748475863506349376L;

			@Override
			public boolean matches(Object object) 
			{
				if (object instanceof Element)
				{
					Element element = (Element) object;
					return element.getName().equals("receipt");
				}
				return false;
			}
		});
		boolean found = false;
		while (receipts.hasNext())
		{
			Element element = receipts.next();
			if (element.getAttributeValue("id").equals(receipt.getId().toString()))
			{
				found = true;
				convertToJdomElement(receipt, element);
				
			}
		}
		if (!found)
		{
			Element element = new Element("receipt");
			convertToJdomElement(receipt, element);
			root.addContent(element);
		}
		save(WORK_FILE);
	}

	@Override
	public void settle(Settlement settlement) 
	{
		save(settlement);
		export = initialize();
		save(WORK_FILE);
	}

	public Element convertToJdomElement(Receipt receipt, Element element)
	{
		element.setAttribute("id", receipt.getId().toString()); //$NON-NLS-2$
		element.setAttribute("timestamp", Long.valueOf(receipt.getTimestamp().getTime()).toString());
		element.setAttribute("number", receipt.getNumber().toString());
		element.setAttribute("transaction-id", "0");
		element.setAttribute("booking-id", "0");
		element.setAttribute("salespoint-id", receipt.getSettlement().getSalespoint().getMapping());
		element.setAttribute("user-id", receipt.getUser().getUsername());
		element.setAttribute("foreign-currency-id", receipt.getDefaultCurrency().getCode());
		element.setAttribute("status", receipt.getState().compatibleState());
		element.setAttribute("settlement", receipt.getSettlement().getId().toString());
		element.setAttribute("amount", Double.toString(receipt.getPaymentAmount(Receipt.QuotationType.REFERENCE_CURRENCY)));
		element.setAttribute("customer-id", receipt.getCustomerCode());
		element.setAttribute("transferred", Boolean.valueOf(receipt.isTransferred()).toString());

		updatePositionElements(receipt, element);
		updatePaymentElements(receipt, element);
		return element;
	}

	private void updatePositionElements(Receipt receipt, Element receiptElement)
	{
		Position[] positions = receipt.getPositions().toArray(new Position[0]);
		for (Position position : positions)
		{
			boolean found = false;
			@SuppressWarnings("unchecked")
			Collection<Element> positionElements = receiptElement.getChildren("position");
			for (Element positionElement : positionElements)
			{
				if (positionElement.getAttributeValue("id").equals(position.getId().toString()))
				{
					convertToJdomElement(position, positionElement);
					found = true;
				}
			}
			if (!found)
			{
				Element positionElement = new Element("position");
				convertToJdomElement(position, positionElement);
				receiptElement.addContent(positionElement);
			}
		}
		/**
		 * remove deleted positions
		 */
		@SuppressWarnings("unchecked")
		Collection<Element> positionElements = receiptElement.getChildren("position");
		for (Element positionElement : positionElements)
		{
			boolean found = false;
			positions = receipt.getPositions().toArray(new Position[0]);
			for (Position position : positions)
			{
				if (position.getId().toString().equals(positionElement.getAttributeValue("id")))
				{
					found = true;
				}
			}
			if (!found)
			{
				positionElement.detach();
			}
		}
	}

	private void updatePaymentElements(Receipt receipt, Element receiptElement)
	{
		Payment[] payments = receipt.getPayments().toArray(new Payment[0]);
		for (Payment payment : payments)
		{
			boolean found = false;
			@SuppressWarnings("unchecked")
			Collection<Element> paymentElements = receiptElement.getChildren("payment");
			for (Element paymentElement : paymentElements)
			{
				if (paymentElement.getAttributeValue("id").equals(payment.getId().toString()))
				{
					convertToJdomElement(payment, paymentElement);
					found = true;
				}
			}
			if (!found)
			{
				Element paymentElement = new Element("payment");
				convertToJdomElement(payment, paymentElement);
				receiptElement.addContent(paymentElement);
			}
		}
		/**
		 * remove deleted payments
		 */
		@SuppressWarnings("unchecked")
		Collection<Element> paymentElements = receiptElement.getChildren("payment");
		for (Element paymentElement : paymentElements)
		{
			boolean found = false;
			payments = receipt.getPayments().toArray(new Payment[0]);
			for (Payment payment : payments)
			{
				if (payment.getId().toString().equals(paymentElement.getAttributeValue("id")))
				{
					found = true;
				}
			}
			if (!found)
			{
				paymentElement.detach();
			}
		}
	}

	private void convertToJdomElement(Position position, Element element)
	{
		element.setAttribute("id", position.getId().toString());
		element.setAttribute("receipt-id", position.getReceipt().getId().toString());
		element.setAttribute("product-group-id", position.getProductGroup().getMappingId());
		element.setAttribute("tax-id", position.getCurrentTax().getTax().getCode().toString());
		element.setAttribute("current-tax-id", position.getCurrentTax().getValidFrom().toString());
		element.setAttribute("quantity", Integer.toString(position.getQuantity()));
		element.setAttribute("price", Double.toString(position.getPrice()));
		element.setAttribute("discount", Double.toString(position.getDiscount()));
		element.setAttribute("galileo-book", Boolean.toString(position.isBookProvider()));
		element.setAttribute("galileo-booked", Boolean.toString(position.isProviderBooked()));
		element.setAttribute("opt-code", position.getOption().toCode());
		element.setAttribute("ordered", Boolean.toString(position.isOrdered()));
		element.setAttribute("order-id", position.valueOf(position.getOrder()));
		element.setAttribute("stock", Boolean.toString(position.isFromStock()));
		Customer customer = position.getReceipt().getCustomer();
		boolean updateAccount = customer != null && customer.getHasAccount();
		element.setAttribute("update-customer-account", Boolean.toString(updateAccount));
		element.setAttribute("payed-invoice", Boolean.toString(position.getOption().equals(Option.PAYED_INVOICE)));
		String invoiceNumber = "";
		String invoiceDate = "";
		if (position.getProduct() == null)
		{
			element.setAttribute("author", "");
			element.setAttribute("title", "");
			element.setAttribute("publisher", "");
			element.setAttribute("isbn", "");
			element.setAttribute("bznr", "");
			element.setAttribute("product-number", "");
			element.setAttribute("product-id", "");
		}
		else
		{
			element.setAttribute("author", position.getProduct().getAuthor());
			element.setAttribute("title", position.getProduct().getTitle());
			element.setAttribute("publisher", position.getProduct().getPublisher());
			element.setAttribute("isbn", getIsbn(position.getProduct().getCode()));
			element.setAttribute("bznr", getBz(position.getProduct().getCode()));
			element.setAttribute("product-number", position.getProduct().getCode());
			element.setAttribute("product-id", position.getProduct().getCode());
			invoiceNumber = position.valueOf(position.getProduct().getInvoiceNumber());
			invoiceDate = position.getProduct().getInvoiceDate() == null ? "" : Long.valueOf(position.getProduct().getInvoiceDate().getTimeInMillis()).toString();
		}
		element.setAttribute("invoice", invoiceNumber);
		element.setAttribute("invoice-date", invoiceDate);
		element.setAttribute("tax", Double.toString(position.getTaxAmount(Receipt.QuotationType.REFERENCE_CURRENCY)));
		element.setAttribute("type", position.getProductGroup().compatibleState());
		element.setAttribute("amount-fc", Double.valueOf(position.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.NETTO)).toString());
		element.setAttribute("amount", Double.valueOf(position.getAmount(Receipt.QuotationType.REFERENCE_CURRENCY, Position.AmountType.NETTO)).toString());
	}

	public void convertToJdomElement(Payment payment, Element element)
	{
		element.setAttribute("id", payment.getId().toString());
		element.setAttribute("receipt-id", payment.getReceipt().getId().toString());
		element.setAttribute("payment-type-id", payment.getPaymentType().getMappingId());
		element.setAttribute("foreign-currency-id", payment.getPaymentType().getCurrency().getCode());
		element.setAttribute("quotation", Double.toString(payment.getForeignCurrencyQuotation()));
		element.setAttribute("amount", Double.toString(payment.getAmount()));
		element.setAttribute("amount-fc", Double.toString(payment.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY)));
		element.setAttribute("round-factor", Double.toString(payment.getForeignCurrencyRoundFactor()));
		element.setAttribute("round-factor-fc", Double.toString(payment.getPaymentType().getCurrency().getRoundFactor()));
		element.setAttribute("back", new Boolean(payment.isBack()).toString());
		String settlement = payment.getReceipt().getSettlement().getSettled() == null ? "" : Long.valueOf(payment.getReceipt().getSettlement().getSettled().getTimeInMillis()).toString();
		element.setAttribute("settlement", settlement);
		Salespoint salespoint = payment.getReceipt().getSettlement().getSalespoint();
		element.setAttribute("salespoint-id", salespoint.getMapping() == null ? salespoint.getHost() : salespoint.getMapping());
		boolean inputOrWithdraw = payment.getReceipt().getPositions().size() > 0 && payment.getReceipt().getPositions().iterator().next().getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL);
		element.setAttribute("is-input-or-withdraw", Boolean.toString(inputOrWithdraw));
	}
	
	private String getIsbn(String code)
	{
		for (BarcodeVerifier barcodeVerifier : barcodeVerifiers)
		{
			Barcode barcode = barcodeVerifier.verify(code);
			if (barcode.getName().toLowerCase().contains("isbn"))
			{
				return code;
			}
		}
		return "";
	}

	private String getBz(String code)
	{
		for (BarcodeVerifier barcodeVerifier : barcodeVerifiers)
		{
			Barcode barcode = barcodeVerifier.verify(code);
			if (barcode.getName().toLowerCase().contains("bz"))
			{
				return code;
			}
		}
		return "";
	}
}
