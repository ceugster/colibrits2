/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.admin.profile.dialogs.PaymentTypeComboViewerDialog;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class SelectPaymentTypeMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	private LabelProvider labelProvider;

	private ViewerFilter[] filters;

	private String dialogLabelText;

	public SelectPaymentTypeMenuItem(final TabEditor editor, final TabEditorButton button, final String labelText, final String actionCommand,
			final LabelProvider labelProvider, final ViewerFilter[] viewerFilters, final String dialogLabelText)
	{
		super();
		this.setLabel(labelText);
		this.setActionCommand(actionCommand);
		this.editor = editor;
		this.button = button;
		this.addActionListener(this);
		this.labelProvider = labelProvider;
		this.filters = viewerFilters;
		this.dialogLabelText = dialogLabelText;
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final UIJob uiJob = new UIJob("menu action")
		{
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor)
			{
				final ServiceTracker<PersistenceService, PersistenceService> serviceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
						PersistenceService.class, null);
				serviceTracker.open();
				try
				{
					final PersistenceService persistenceService = (PersistenceService) serviceTracker.getService();
					if (persistenceService != null)
					{
						PaymentType paymentType = null;
						final Long id = SelectPaymentTypeMenuItem.this.button.getKeys()[1].getParentId();
						if (id != null)
						{
							paymentType = (PaymentType) persistenceService.getServerService().find(PaymentType.class, id);
						}
	
						final PaymentTypeComboViewerDialog dialog = new PaymentTypeComboViewerDialog(Display.getCurrent().getActiveShell(),
								paymentType, SelectPaymentTypeMenuItem.this.labelProvider, SelectPaymentTypeMenuItem.this.filters,
								SelectPaymentTypeMenuItem.this.dialogLabelText);
						if (dialog.open() == Window.OK)
						{
							if (dialog.getSelection() != null)
							{
								paymentType = dialog.getSelection();
								SelectPaymentTypeMenuItem.this.button.getKeys()[1].setParentId(paymentType.getId());
								SelectPaymentTypeMenuItem.this.button.getKeys()[1].setDeleted(false);
								SelectPaymentTypeMenuItem.this.button.getKeys()[1].setLabel(FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION.toCode() + "<br>"
										+ paymentType.getCode() + " " + paymentType.getCurrency().getCode());
								SelectPaymentTypeMenuItem.this.button.update(SelectPaymentTypeMenuItem.this.editor.getFailOverState());
							}
						}
					}
				}
				finally
				{
					serviceTracker.close();
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}
}
