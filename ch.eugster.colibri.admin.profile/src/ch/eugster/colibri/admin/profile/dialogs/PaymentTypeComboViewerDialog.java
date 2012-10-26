/*
 * Created on 30.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.dialogs;

import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class PaymentTypeComboViewerDialog extends IconAndMessageDialog
{
	private ComboViewer viewer;

	private PaymentType paymentType;

	private String labelText;

	private LabelProvider labelProvider;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ViewerFilter[] filters;

	public PaymentTypeComboViewerDialog(final Shell shell, final PaymentType paymentType, final LabelProvider labelProvider,
			final ViewerFilter[] viewerFilters, final String label)
	{
		super(shell);
		this.initialize(paymentType, labelProvider, viewerFilters, label);
	}

	@Override
	public Control createButtonBar(final Composite parent)
	{
		final Composite composite = (Composite) super.createButtonBar(parent);
		this.getButton(Window.OK).setText("OK");
		this.getButton(Window.CANCEL).setText("Abbrechen");
		this.setOkButtonState();
		return composite;
	}

	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	@Override
	public Image getImage()
	{
		return null;
	}

	public PaymentType getSelection()
	{
		return this.paymentType;
	}

	@Override
	protected Control createDialogArea(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label label = new Label(composite, SWT.NONE);
		label.setText(this.labelText == null ? "" : this.labelText);
		label.setLayoutData(new GridData());

		final Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.viewer = new ComboViewer(combo);
		this.viewer.setContentProvider(new ArrayContentProvider());
		this.viewer.setLabelProvider(this.labelProvider);
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.setSorter(new ViewerSorter()
		{
			@Override
			public int compare(final Viewer viewer, final Object element1, final Object element2)
			{
				if ((element1 instanceof PaymentType) && (element2 instanceof PaymentType))
				{
					final PaymentType pt1 = (PaymentType) element1;
					final PaymentType pt2 = (PaymentType) element2;

					return pt1.getName().compareTo(pt2.getName());
				}
				return 0;
			}
		});

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final PaymentTypeQuery queryService = (PaymentTypeQuery) persistenceService.getServerService().getQuery(PaymentType.class);
			if (queryService != null)
			{
				this.viewer.setInput(queryService.selectAll(true).toArray(new PaymentType[0]));
			}
		}
		this.viewer.setFilters(this.filters);
		this.viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final StructuredSelection ssel = (StructuredSelection) PaymentTypeComboViewerDialog.this.viewer.getSelection();
				if (!ssel.isEmpty())
				{
					PaymentTypeComboViewerDialog.this.paymentType = (PaymentType) ssel.getFirstElement();
				}

				PaymentTypeComboViewerDialog.this.setOkButtonState();
			}
		});

		if (this.paymentType != null)
		{
			this.viewer.setSelection(new StructuredSelection(this.paymentType));
		}

		return composite;
	}

	private void initialize(final PaymentType paymentType, final LabelProvider labelProvider, final ViewerFilter[] viewerFilters, final String label)
	{
		this.paymentType = paymentType;
		this.labelProvider = labelProvider;
		this.filters = viewerFilters;
		this.labelText = label;

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	private void setOkButtonState()
	{
		if (this.getButton(Window.OK) != null)
		{
			this.getButton(Window.OK).setEnabled(!this.viewer.getSelection().isEmpty());
		}
	}
}
