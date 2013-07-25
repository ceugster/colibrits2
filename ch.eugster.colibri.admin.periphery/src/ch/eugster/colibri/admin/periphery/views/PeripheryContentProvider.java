/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.periphery.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.display.area.ILayoutType;
import ch.eugster.colibri.display.service.DisplayService;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.queries.DisplayQuery;
import ch.eugster.colibri.persistence.queries.PrintoutQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.print.service.PrintService;

public class PeripheryContentProvider implements ITreeContentProvider
{
	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> receiptPrinterServiceTracker;

	private ServiceTracker<PrintService, PrintService> printServiceTracker;

	public PeripheryContentProvider()
	{
		this.receiptPrinterServiceTracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle().getBundleContext(),
				ReceiptPrinterService.class, null);
		this.receiptPrinterServiceTracker.open();
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
		this.printServiceTracker = new ServiceTracker<PrintService, PrintService>(Activator.getDefault().getBundle().getBundleContext(), PrintService.class, null);
		this.printServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.printServiceTracker.close();
		this.persistenceServiceTracker.close();
		this.receiptPrinterServiceTracker.close();
	}

	@Override
	public Object[] getChildren(final Object parent)
	{
		if (parent instanceof PeripheryGroup)
		{
			final PeripheryGroup peripheryGroup = (PeripheryGroup) parent;
			try
			{
				final ServiceReference<?>[] references = Activator.getDefault().getBundle().getBundleContext()
						.getServiceReferences(peripheryGroup.getServiceName(), null);
				if (references instanceof ServiceReference[])
				{
					return references;
				}
			}
			catch (final InvalidSyntaxException e)
			{
				e.printStackTrace();
			}
			return new ServiceReference[0];
		}
		else if (parent instanceof ServiceReference)
		{
			ServiceReference<?> reference = (ServiceReference<?>) parent;
			Object service = Activator.getDefault().getBundle().getBundleContext().getService(reference);
			if (service instanceof CustomerDisplayService)
			{
				CustomerDisplayService customerDisplayService = (CustomerDisplayService) service;
				PersistenceService persistenceService = persistenceServiceTracker.getService();
				if (persistenceService != null)
				{
					List<Display> displays = new ArrayList<Display>();
					DisplayQuery displayQuery = (DisplayQuery) persistenceService.getServerService().getQuery(Display.class);
					CustomerDisplaySettings settings = customerDisplayService.getCustomerDisplaySettings();
					try
					{
						Collection<ServiceReference<DisplayService>> refs = Activator.getDefault().getBundle().getBundleContext().getServiceReferences(DisplayService.class, null);
						for (ServiceReference<DisplayService> ref : refs)
						{
							DisplayService displayService = Activator.getDefault().getBundle().getBundleContext().getService(ref);
							ILayoutType layoutType = displayService.getLayoutType(customerDisplayService.getCustomerDisplaySettings().getComponentName());
							if (layoutType.hasCustomerEditableAreaTypes())
							{
								Display display = displayQuery.findTemplate(displayService.getContext().getProperties().get("component.name").toString(), settings);
								if (display == null)
								{
									display = Display.newInstance(displayService.getContext().getProperties().get("component.name").toString(), settings);
								}
								displays.add(display);
							}
						}
					}
					catch (InvalidSyntaxException e)
					{
					}
					return displays.toArray(new Display[0]);
				}
			}
			else if (service instanceof ReceiptPrinterService)
			{
				ReceiptPrinterService receiptPrinterService = (ReceiptPrinterService) service;
				PersistenceService persistenceService = persistenceServiceTracker.getService();
				if (persistenceService != null)
				{
					List<Printout> printouts = new ArrayList<Printout>();
					PrintoutQuery printoutQuery = (PrintoutQuery) persistenceService.getServerService().getQuery(Printout.class);
					ReceiptPrinterSettings settings = receiptPrinterService.getReceiptPrinterSettings();
					try
					{
						Collection<ServiceReference<PrintService>> refs = Activator.getDefault().getBundle().getBundleContext().getServiceReferences(PrintService.class, null);
						for (ServiceReference<PrintService> ref : refs)
						{
							PrintService printService = Activator.getDefault().getBundle().getBundleContext().getService(ref);
							ch.eugster.colibri.print.section.ILayoutType layoutType = printService.getLayoutType(receiptPrinterService);
							if (layoutType.hasCustomerEditableAreaTypes())
							{
								Printout printout = printoutQuery.findTemplate(printService.getLayoutTypeId(), settings);
								if (printout == null)
								{
									printout = Printout.newInstance(printService.getLayoutTypeId(), settings);
								}
								printouts.add(printout);
							}
						}
					}
					catch (InvalidSyntaxException e)
					{
					}
					return printouts.toArray(new Printout[0]);
				}
			}
		}
		return PeripheryGroup.values();
	}

	@Override
	public Object[] getElements(final Object element)
	{
		return this.getChildren(element);
	}

	@Override
	public Object getParent(final Object element)
	{
		if (element instanceof ServiceReference)
		{
			final ServiceReference<?> reference = (ServiceReference<?>) element;
			final Integer group = (Integer) reference.getProperty("component.group");
			if (group instanceof Integer)
			{
				return PeripheryGroup.values()[group.intValue()];
			}
		}
		return null;
	}

	@Override
	public boolean hasChildren(final Object parent)
	{
		boolean hasChildren = false;
		if (parent instanceof PeripheryGroup)
		{
			final PeripheryGroup peripheryGroup = (PeripheryGroup) parent;
			try
			{
				final ServiceReference<?>[] references = Activator.getDefault().getBundle().getBundleContext()
						.getServiceReferences(peripheryGroup.getServiceName(), null);
				if (references instanceof ServiceReference[])
				{
					hasChildren = references.length > 0;
				}
			}
			catch (final InvalidSyntaxException e)
			{
				e.printStackTrace();
			}
		}
		else if (parent instanceof ServiceReference)
		{
			ServiceReference<?> reference = (ServiceReference<?>) parent;
			Object service = Activator.getDefault().getBundle().getBundleContext().getService(reference);
			if (service instanceof CustomerDisplayService)
			{
				CustomerDisplayService customerDisplayService = (CustomerDisplayService) service;
				try
				{
					Collection<ServiceReference<DisplayService>> displayServiceReferences = Activator.getDefault().getBundle().getBundleContext().getServiceReferences(DisplayService.class, null);
					for (ServiceReference<DisplayService> displayServiceReference : displayServiceReferences)
					{
						DisplayService displayService = Activator.getDefault().getBundle().getBundleContext().getService(displayServiceReference);
						ILayoutType layoutType = displayService.getLayoutType(customerDisplayService);
						if (layoutType != null && layoutType.hasCustomerEditableAreaTypes())
						{
							hasChildren = true;
						}
					}
				}
				catch(InvalidSyntaxException e)
				{
				}
			}
			else if (service instanceof ReceiptPrinterService)
			{
				ReceiptPrinterService receiptPrinterService = (ReceiptPrinterService) service;
				try
				{
					Collection<ServiceReference<PrintService>> printServiceReferences = Activator.getDefault().getBundle().getBundleContext().getServiceReferences(PrintService.class, null);
					for (ServiceReference<PrintService> printServiceReference : printServiceReferences)
					{
						PrintService printService = Activator.getDefault().getBundle().getBundleContext().getService(printServiceReference);
						ch.eugster.colibri.print.section.ILayoutType layoutType = printService.getLayoutType(receiptPrinterService);
						if (layoutType != null && layoutType.hasCustomerEditableAreaTypes())
						{
							hasChildren = true;
						}
					}
				}
				catch(InvalidSyntaxException e)
				{
				}
			}
		}
		return hasChildren;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}
}
