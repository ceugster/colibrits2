/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.periphery.editors;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;

public class ReceiptPrinterEditor extends AbstractEntityEditor<ReceiptPrinterSettings> implements
		PropertyChangeListener, EventHandler
{
	public static final String ID = "ch.eugster.colibri.admin.periphery.receipt.printer.editor";

	private Text port;

	private Text converter;

	private Spinner cols;

	private Spinner linesBeforeCut;

//	private Button printLogo;
//	
//	private ComboViewer logo;
//	
//	private ComboViewer printLogoMode;
	
	private IDialogSettings settings;

	private ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker;

	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;

	private ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> receiptPrinterServiceTracker;
	
	public ReceiptPrinterEditor()
	{
	}

	@Override
	public void dispose()
	{
		this.receiptPrinterServiceTracker.close();
		EntityMediator.removeListener(ReceiptPrinterSettings.class, this);
		this.eventAdminTracker.close();
		this.eventHandlerServiceRegistration.unregister();
		super.dispose();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter)
	{
		return null;
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals(ReceiptPrinterService.EVENT_ADMIN_TOPIC_ERROR))
		{
			final UIJob uiJob = new UIJob("send information")
			{
				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor)
				{
					Toolkit.getDefaultToolkit().beep();
					final MessageDialog dialog = new MessageDialog(ReceiptPrinterEditor.this.getSite().getShell(),
							"Drucker nicht bereit", null, "Der Drucker kann nicht angesprochen werden.",
							MessageDialog.INFORMATION, new String[] { "OK" }, 0);
					dialog.open();
					return Status.OK_STATUS;
				}
			};
			uiJob.schedule();
		}
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		this.settings = Activator.getDefault().getDialogSettings().getSection(ReceiptPrinterEditor.ID);
		if (this.settings == null)
		{
			this.settings = Activator.getDefault().getDialogSettings().addNewSection(ReceiptPrinterEditor.ID);
		}
		this.eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventAdminTracker.open();

		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, ReceiptPrinterService.EVENT_ADMIN_TOPIC_ERROR);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, this, properties);
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof ReceiptPrinterSettings)
		{
			final ReceiptPrinterSettings display = (ReceiptPrinterSettings) entity;
			if (display.equals(this.getEditorInput().getAdapter(ReceiptPrinterSettings.class)))
			{
				this.dispose();
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt)
	{
		this.setDirty(true);
	}

	@Override
	public void propertyChanged(final Object source, final int propId)
	{
		if (propId == IEditorPart.PROP_DIRTY)
		{
			this.setDirty(true);
		}
	}

	@Override
	public void setFocus()
	{
		// this.codePages.getCCombo().setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createPeripherySection(scrolledForm);
		this.createTestSection(scrolledForm);
		EntityMediator.addListener(Salespoint.class, this);

		this.receiptPrinterServiceTracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle().getBundleContext(),
				ReceiptPrinterService.class, null)
		{
			@Override
			public ReceiptPrinterService addingService(final ServiceReference<ReceiptPrinterService> reference)
			{
				String oldComponentName = null;
				ServiceReference<ReceiptPrinterService> oldReference = ((ReceiptPrinterEditorInput) getEditorInput()).getServiceReference();
				if (oldReference != null)
				{
					oldComponentName = (String) oldReference.getProperty("component.name");
				}
				String newComponentName = (String) reference.getProperty("component.name");
				if (oldComponentName == null || oldComponentName.equals(newComponentName)) 
				{
					((ReceiptPrinterEditorInput) getEditorInput()).setServiceReference(reference);
				}
				return super.addingService(reference);
			}

			@Override
			public void modifiedService(final ServiceReference<ReceiptPrinterService> reference, final ReceiptPrinterService service)
			{
				String oldComponentName = null;
				ServiceReference<ReceiptPrinterService> oldReference = ((ReceiptPrinterEditorInput) getEditorInput()).getServiceReference();
				if (oldReference != null)
				{
					oldComponentName = (String) oldReference.getProperty("component.name");
				}
				String newComponentName = (String) reference.getProperty("component.name");
				if (oldComponentName == null || oldComponentName.equals(newComponentName)) 
				{
					((ReceiptPrinterEditorInput) getEditorInput()).setServiceReference(reference);
				}
				super.modifiedService(reference, service);
			}
		};
		this.receiptPrinterServiceTracker.open();

	}

	@Override
	protected String getName()
	{
		final ReceiptPrinterEditorInput input = (ReceiptPrinterEditorInput) this.getEditorInput();
		final ServiceReference<ReceiptPrinterService> reference = input.getServiceReference();
		if (reference != null)
		{
			final String deviceName = (String) reference.getProperty("custom.device");
			if (deviceName != null)
			{
				return deviceName;
			}
		}
		return "Belegdrucker";
	}

	@Override
	protected String getText()
	{
		return "Belegdrucker";
	}

	@Override
	protected void loadValues()
	{
		final ReceiptPrinterSettings periphery = (ReceiptPrinterSettings) ((ReceiptPrinterEditorInput) this
				.getEditorInput()).getAdapter(ReceiptPrinterSettings.class);

		ServiceReference<ReceiptPrinterService> reference = receiptPrinterServiceTracker.getServiceReference();

		String portName = periphery.getPort();
		if ((portName == null) || portName.isEmpty())
		{
			final Object prop = reference.getProperty("custom.port");
			if (prop instanceof String)
			{
				portName = (String) prop;
			}
		}
		this.port.setText(portName);

		String conversions = periphery.getConverter();
		if ((conversions == null) || conversions.isEmpty())
		{
			final Object prop = reference.getProperty("custom.convert");
			if (prop instanceof String)
			{
				conversions = (String) prop;
			}
			else if (prop instanceof String[])
			{
				final StringBuilder result = new StringBuilder();
				final String[] lines = (String[]) prop;
				for (final String line : lines)
				{
					result.append(line + "\n");
				}
				conversions = result.toString();
			}
		}
		this.converter.setText(conversions);

		Integer cols = periphery.getCols();
		if ((cols == null) || cols.equals(Integer.valueOf(0)))
		{
			final Object prop = reference.getProperty("custom.cols");
			if (prop instanceof Integer)
			{
				cols = (Integer) prop;
			}
		}
		this.cols.setSelection(cols == null ? 0 : cols.intValue());

		this.linesBeforeCut.setSelection(periphery.getLinesBeforeCut());

		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final ReceiptPrinterEditorInput input = (ReceiptPrinterEditorInput) this.getEditorInput();
		final ReceiptPrinterSettings periphery = (ReceiptPrinterSettings) input
				.getAdapter(ReceiptPrinterSettings.class);
		periphery.setPort(this.port.getText());
		periphery.setConverter(this.converter.getText());
		periphery.setCols(Integer.valueOf(this.cols.getSelection()));
		periphery.setLinesBeforeCut(this.linesBeforeCut.getSelection());
		periphery.setComponentName((String) input.getServiceReference().getProperty("component.name"));
		periphery.setName((String) input.getServiceReference().getProperty("custom.device"));
	}

	@Override
	protected void updateControls()
	{
		super.updateControls();
		ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> tracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle().getBundleContext(), ReceiptPrinterService.class, null);
		tracker.open();
		try
		{
			ReceiptPrinterService service = tracker.getService(((ReceiptPrinterEditorInput)this.getEditorInput()).getServiceReference());
			if (service != null)
			{
				Bundle bundle = service.getContext().getBundleContext().getBundle();
				bundle.stop();
				bundle.start();
			}
		}
		catch (BundleException e) 
		{
		}
		finally
		{
			tracker.close();
		}
	}

	@Override
	protected boolean validate()
	{
		final Message msg = this.getEmptyNameMessage();

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<ReceiptPrinterSettings> input)
	{
		return input.getAdapter(ReceiptPrinterSettings.class) instanceof ReceiptPrinterSettings;
	}

	private Section createPeripherySection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Eigenschaften");
		section.setClient(this.fillPeripherySection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ReceiptPrinterEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createTestSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Prüfen");
		section.setClient(this.fillTestSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ReceiptPrinterEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillPeripherySection(final Section parent)
	{
		final TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(tableWrapData);
		composite.setLayout(new GridLayout(3, false));

		Label label = this.formToolkit.createLabel(composite, "Logischer Gerätename", SWT.NONE);
		label.setLayoutData(new GridData());

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		this.port = this.formToolkit.createText(composite, "");
		this.port.setLayoutData(gridData);
		this.port.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ReceiptPrinterEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Anzahl Spalten", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.widthHint = 32;

		this.cols = new Spinner(composite, SWT.RIGHT);
		this.cols.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		this.cols.setLayoutData(gridData);
		this.cols.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				ReceiptPrinterEditor.this.setDirty(true);
			}
		});
		this.cols.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ReceiptPrinterEditor.this.setDirty(true);
			}
		});
		this.cols.setIncrement(1);
		this.cols.setPageIncrement(10);
		this.formToolkit.adapt(this.cols);

		label = this.formToolkit.createLabel(composite, "Konverter");
		label.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.heightHint = 100;

		this.converter = this.formToolkit.createText(composite, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		this.converter.setLayoutData(gridData);
		this.converter.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ReceiptPrinterEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Zeilennachschübe");
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 32;

		this.linesBeforeCut = new Spinner(composite, SWT.RIGHT);
		this.linesBeforeCut.setLayoutData(gridData);
		this.linesBeforeCut.setDigits(0);
		this.linesBeforeCut.setIncrement(1);
		this.linesBeforeCut.setPageIncrement(10);
		this.linesBeforeCut.setMinimum(0);
		this.linesBeforeCut.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		this.linesBeforeCut.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				ReceiptPrinterEditor.this.setDirty(true);
			}
		});
		this.linesBeforeCut.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ReceiptPrinterEditor.this.setDirty(true);
			}
		});
		this.formToolkit.adapt(this.linesBeforeCut);

		label = this.formToolkit.createLabel(composite, "vor Belegschnitt");
		label.setLayoutData(new GridData());

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillTestSection(final Section parent)
	{
		final TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(tableWrapData);
		composite.setLayout(new GridLayout(2, false));

		final Text test = this.formToolkit.createText(composite, "Ä Ö Ü ä ö ü", SWT.MULTI | SWT.WRAP);
		test.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button button = this.formToolkit.createButton(composite, "Test", SWT.PUSH);
		button.setLayoutData(new GridData());
		button.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				ReceiptPrinterEditorInput input = (ReceiptPrinterEditorInput) ReceiptPrinterEditor.this
						.getEditorInput();
				final ServiceReference<ReceiptPrinterService> reference = input.getServiceReference();
				if (reference != null)
				{
					ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> tracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle().getBundleContext(), ReceiptPrinterService.class, null);
					tracker.open();
					try
					{
						ReceiptPrinterService printer = tracker.getService(reference);
						if (printer == null)
						{
							MessageDialog.openError(getSite().getShell(), "Belegdrucker", "Der Service für diesen Belegdrucker ist nicht aktiv.");
							return;
						}
						printer.testPrint(ReceiptPrinterEditor.this.port.getText(), ReceiptPrinterEditor.this.converter.getText(), test.getText(), linesBeforeCut.getSelection());
					}
					catch (Exception ex)
					{
						MessageDialog.openError(getSite().getShell(), "Belegdrucker", ex.getLocalizedMessage());
					}
					finally
					{
						tracker.close();
					}
				}
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Message getEmptyNameMessage()
	{
		final Message msg = null;
		return msg;
	}
}
