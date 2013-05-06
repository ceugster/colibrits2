/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.ui.Activator;
import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.service.PersistenceService;

@SuppressWarnings("unchecked")
public abstract class AbstractEntityEditor<T extends AbstractEntity> extends EditorPart implements IPropertyListener,
		EntityListener
{
	protected ColorManager colorManager;

	protected FormToolkit formToolkit;

	protected Color normal;

	protected Color error;

	protected ScrolledForm scrolledForm;

	private boolean dirty;

	protected ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public AbstractEntityEditor()
	{
		super();
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		this.formToolkit = new FormToolkit(parent.getDisplay());
		this.colorManager = new ColorManager();

		this.normal = this.colorManager.getColor(new RGB(255, 255, 255));
		this.error = this.colorManager.getColor(new RGB(247, 196, 145));

		final ColumnLayout columnLayout = new ColumnLayout();
		columnLayout.maxNumColumns = 1;
		columnLayout.minNumColumns = 1;

		this.scrolledForm = this.formToolkit.createScrolledForm(parent);
		this.scrolledForm.getBody().setLayout(columnLayout);
		this.scrolledForm.setText(this.getText());

		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		this.createSections(this.scrolledForm);

		this.loadValues();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	@Override
	public void doSave(final IProgressMonitor monitor)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService == null)
		{
			MessageDialog.openInformation(this.getSite().getShell(), "Datenbankverbindung",
					"Es besteht keine Verbindung zur Datenbank. Die Daten können nicht gespeichert werden.");
		}
		else
		{
			if (this.validate())
			{
				final AbstractEntityEditorInput<T> input = (AbstractEntityEditorInput<T>) this.getEditorInput();
				if (input.getEntity().getId() != null)
				{
					input.setEntity((T) persistenceService.getServerService().refresh(input.getEntity())); 
				}
//				input.setEntity((T) persistenceService.getServerService().refresh(input.getEntity()));
				this.saveValues();
				if (input.hasParent())
				{
					AbstractEntity parent = input.getParent();
					if (parent != null)
					{
						input.setParent(persistenceService.getServerService().merge(parent));
					}
					else
					{
						input.setEntity((T) persistenceService.getServerService().merge(input.getEntity()));
					}
				}
				else
				{
					final T entity = input.getEntity();
					input.setEntity((T) persistenceService.getServerService().merge(entity));
				}
				// if (input.hasParent())
				// {
				// serverService.refresh(input.getParent());
				// }
				this.setDirty(false);
				this.scrolledForm.setText(this.getText());
				this.updateControls();
			}
		}
	}

	@Override
	public void doSaveAs()
	{
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException
	{
		if (!this.validateType((AbstractEntityEditorInput<T>) input))
		{
			throw new PartInitException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Adaptable must be of Type T"));
		}

		this.setInput(input);
		this.setSite(site);
		this.setPartName(this.getName());

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public boolean isDirty()
	{
		return this.dirty;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public boolean isSaveOnCloseNeeded()
	{
		return false;
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void postLoad(final AbstractEntity entity)
	{
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
	}

	@Override
	public void postRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
	}

	@Override
	public void preDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void prePersist(final AbstractEntity entity)
	{
	}

	@Override
	public void preRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void preUpdate(final AbstractEntity entity)
	{
	}

	@Override
	public void propertyChanged(final Object source, final int propId)
	{
	}

	public void reset(final boolean ask)
	{
		if (this.isDirty())
		{
			boolean reset = true;
			if (ask)
			{
				reset = MessageDialog.openQuestion(this.getEditorSite().getShell(), "Verwerfen",
						"Sollen die Änderungen verworfen werden?");
			}
			if (reset)
			{
				this.scrolledForm.setText(this.getText());
				this.loadValues();
				this.setDirty(false);
			}
		}
	}

	public void setDirty(final boolean dirty)
	{
		this.dirty = dirty;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	public void setFocus(final Control control)
	{
		control.setFocus();
	}

	protected abstract void createSections(ScrolledForm parent);

	// protected abstract Message getMessage(PersistenceException.ErrorCode
	// errorCode);

	protected abstract String getName();

	protected abstract String getText();

	protected abstract void loadValues();

	protected abstract void saveValues();

	protected int showMessage(final String title, final Image image, final String message, final int dialogType,
			final String[] buttonLabels, final int defaultButton)
	{
		final MessageDialog dialog = new MessageDialog(this.getEditorSite().getShell(), title, image, message,
				dialogType, buttonLabels, defaultButton);
		return dialog.open();
	}

	protected int showWarningMessage(final Message msg)
	{
		final int result = this.showMessage(msg.getTitle(), Dialog.getImage(Dialog.DLG_IMG_MESSAGE_WARNING),
				msg.getMessage(), MessageDialog.WARNING, new String[] { "OK" }, 0);
		this.setFocus(msg.getControl());
		return result;
	}

	protected int showWarningMessage(final String title, final String message, final Control control)
	{
		final int result = this.showMessage(title, Dialog.getImage(Dialog.DLG_IMG_MESSAGE_WARNING), message,
				MessageDialog.WARNING, new String[] { "OK" }, 0);
		this.setFocus(control);
		return result;
	}

	protected void updateControls()
	{
		this.setPartName(this.getName());
	}

	protected abstract boolean validate();

	protected abstract boolean validateType(AbstractEntityEditorInput<T> input);
}