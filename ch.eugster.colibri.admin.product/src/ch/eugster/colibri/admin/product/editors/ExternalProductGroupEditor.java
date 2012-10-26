/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderIdService;

public class ExternalProductGroupEditor extends AbstractEntityEditor<ExternalProductGroup>
{
	public static final String ID = "ch.eugster.colibri.admin.external.product.group.editor";

	private Text code;

	private Text name;

	private Text account;

	private ComboViewer providerViewer;

	private ServiceTracker<ProviderIdService, ProviderIdService> providerTracker;

	public ExternalProductGroupEditor()
	{
		EntityMediator.addListener(ExternalProductGroup.class, this);
	}

	@Override
	public void dispose()
	{
		this.providerTracker.close();

		EntityMediator.removeListener(ExternalProductGroup.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity.equals(this.getEditorInput().getAdapter(ExternalProductGroup.class)))
		{
			UIJob job = new UIJob("Der Editor für externe Warengruppen wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					dispose();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
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
		this.code.setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createSection(scrolledForm);
	}

	@Override
	protected String getName()
	{
		final ExternalProductGroup productGroup = (ExternalProductGroup) this.getEditorInput().getAdapter(ExternalProductGroup.class);
		if (productGroup.getId() == null)
		{
			return "Neu";
		}
		return productGroup.getCode();
	}

	@Override
	protected String getText()
	{
		final ExternalProductGroup productGroup = (ExternalProductGroup) this.getEditorInput().getAdapter(ExternalProductGroup.class);
		return productGroup.getText();
	}

	@Override
	protected void loadValues()
	{
		final ExternalProductGroup productGroup = (ExternalProductGroup) this.getEditorInput().getAdapter(ExternalProductGroup.class);

		this.code.setText(productGroup.getCode());
		this.name.setText(productGroup.getText());
		this.account.setText(productGroup.getAccount());
		if (productGroup.getProvider() == null || productGroup.getProvider().isEmpty())
		{
			if (providerViewer.getElementAt(0) != null)
			{
				this.providerViewer.setSelection(new StructuredSelection( new ProviderIdService[] { (ProviderIdService) providerViewer.getElementAt(0) }));
			}
		}
		else
		{
			@SuppressWarnings("unchecked")
			Collection<ProviderIdService> providers = (Collection<ProviderIdService>) providerViewer.getInput();
			for (ProviderIdService provider : providers)
			{
				if (provider.getProviderId().equals(productGroup.getProvider()))
				{
					this.providerViewer.setSelection(new StructuredSelection(new ProviderIdService[] { provider }));
				}
			}
		}
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final ExternalProductGroup productGroup = (ExternalProductGroup) this.getEditorInput().getAdapter(ExternalProductGroup.class);

		productGroup.setCode(this.code.getText());
		productGroup.setText(this.name.getText());
		productGroup.setAccount(this.account.getText());

		StructuredSelection ssel = (StructuredSelection) providerViewer.getSelection();
		if (ssel.isEmpty())
		{
			productGroup.setProvider(null);
		}
		else
		{
			if (ssel.getFirstElement() instanceof ProviderIdService)
			{
				ProviderIdService provider = (ProviderIdService) ssel.getFirstElement();
				productGroup.setProvider(provider.getProviderId());
			}
		}
	}

	@Override
	protected boolean validate()
	{
		Message msg = this.getEmptyCodeMessage();

		if (msg == null)
		{
			msg = this.getUniqueCodeMessage();
		}

		if (msg == null)
		{
			msg = this.getNoSelectedProviderMessage();
		}

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<ExternalProductGroup> input)
	{
		return input.getAdapter(ExternalProductGroup.class) instanceof ExternalProductGroup;
	}

	private Section createSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Beschreibung");
		section.setClient(this.fillSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ExternalProductGroupEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillSection(final Section parent)
	{
		final GridLayout layout = new GridLayout(2, false);

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		Label label = this.formToolkit.createLabel(composite, "Code", SWT.NONE);
		label.setLayoutData(new GridData());

		this.code = this.formToolkit.createText(composite, "");
		this.code.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.code.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ExternalProductGroupEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Bezeichnung", SWT.NONE);
		label.setLayoutData(new GridData());

		this.name = this.formToolkit.createText(composite, "");
		this.name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.name.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ExternalProductGroupEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Konto", SWT.NONE);
		label.setLayoutData(new GridData());

		this.account= this.formToolkit.createText(composite, "");
		this.account.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.account.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ExternalProductGroupEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Warenbewirtschaftung", SWT.NONE);
		label.setLayoutData(new GridData());

		CCombo combo = new CCombo(composite, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.setCursor(composite.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));

		providerViewer = new ComboViewer(combo);
		providerViewer.setContentProvider(new ArrayContentProvider());
		providerViewer.setLabelProvider(new LabelProvider() 
		{
			@Override
			public String getText(Object element) 
			{
				if (element instanceof ProviderIdService)
				{
					ProviderIdService provider = (ProviderIdService) element;
					return provider.getProviderLabel();
				}
				return element.toString();
			}
		});
		providerViewer.setInput(new ArrayList<ProviderIdService>());
		providerViewer.addSelectionChangedListener(new ISelectionChangedListener() 
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{
				setDirty(true);
			}
		});
		
		this.formToolkit.paintBordersFor(composite);

		this.providerTracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderIdService.class, null)
		{
			@Override
			public ProviderIdService addingService(ServiceReference<ProviderIdService> reference) 
			{
				ProviderIdService service = (ProviderIdService) super.addingService(reference);
				@SuppressWarnings("unchecked")
				Collection<ProviderIdService> providers = (Collection<ProviderIdService>) providerViewer.getInput();
				providers.add(service);
				providerViewer.setInput(providers);
				providerViewer.refresh();
				return service;
			}

			@Override
			public void removedService(ServiceReference<ProviderIdService> reference,
					ProviderIdService service) 
			{
				if (providerViewer != null && providerViewer.getInput() != null)
				{
					ProviderIdService provider = (ProviderIdService) super.addingService(reference);
					@SuppressWarnings("unchecked")
					Collection<ProviderIdService> providers = (Collection<ProviderIdService>) providerViewer.getInput();
					providers.remove(provider);
					providerViewer.setInput(providers);
					providerViewer.refresh();
				}
			}
		};
		this.providerTracker.open();

		return composite;
	}

	private Message getEmptyCodeMessage()
	{
		Message msg = null;

		if (this.code.getText().isEmpty())
		{
			msg = new Message(this.name, "Fehler");
			msg.setMessage("Der Code darf nicht leer sein.");
		}
		return msg;
	}

	private Message getNoSelectedProviderMessage()
	{
		Message msg = null;

		if (this.providerViewer.getSelection().isEmpty())
		{
			msg = new Message(this.providerViewer.getCCombo(), "Fehler");
			msg.setMessage("Sie müssen einen Provider auswählen.");
		}
		return msg;
	}

//	private Message getEmptyNameMessage()
//	{
//		Message msg = null;
//
//		if (this.name.getText().isEmpty())
//		{
//			msg = new Message(this.name, "Fehler");
//			msg.setMessage("Die Warengruppe muss eine Bezeichnung haben.");
//		}
//		return msg;
//	}

	private Message getUniqueCodeMessage()
	{
		Message msg = null;

		final ExternalProductGroup productGroup = (ExternalProductGroup) this.getEditorInput().getAdapter(ExternalProductGroup.class);

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ExternalProductGroupQuery queryService = (ExternalProductGroupQuery) persistenceService.getServerService().getQuery(
					ExternalProductGroup.class);
			if (!queryService.isCodeUnique(this.code.getText(), productGroup.getId()))
			{
				msg = new Message(this.name, "Fehler");
				msg.setMessage("Der gewählte Code wird bereits verwendet.");
				return msg;
			}
		}
		return msg;
	}

//	private Message getUniqueNameMessage()
//	{
//		Message msg = null;
//
//		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);
//
//		final String name = this.name.getText();
//		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
//		if (persistenceService != null)
//		{
//			final ProductGroupQuery queryService = (ProductGroupQuery) persistenceService.getServerService().getQuery(
//					ProductGroup.class);
//			if (queryService != null)
//			{
//				if (!queryService.isNameUnique(name, productGroup.getId()))
//				{
//					msg = new Message(this.name, "Fehler");
//					msg.setMessage("Die gewählte Bezeichnung wird bereits verwendet.");
//					return msg;
//				}
//			}
//		}
//		return msg;
//	}
}
