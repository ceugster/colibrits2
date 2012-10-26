/*
 * Created on 30.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.dialogs;

import java.util.Collection;

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
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class ProductGroupComboViewerDialog extends IconAndMessageDialog
{
	private ComboViewer viewer;

	private ProductGroup productGroup;

	private String labelText;

	private LabelProvider labelProvider;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ViewerFilter[] filters;

	public ProductGroupComboViewerDialog(final Shell shell, final ProductGroup productGroup,
			final LabelProvider labelProvider, final ViewerFilter[] viewerFilters, final String label)
	{
		super(shell);
		this.initialize(productGroup, labelProvider, viewerFilters, label);
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

	public ProductGroup getSelection()
	{
		return this.productGroup;
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
				if ((element1 instanceof ProductGroup) && (element2 instanceof ProductGroup))
				{
					final ProductGroup pg1 = (ProductGroup) element1;
					final ProductGroup pg2 = (ProductGroup) element2;

					return pg1.getName().compareTo(pg2.getName());
				}
				return 0;
			}
		});

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ProductGroupQuery query = (ProductGroupQuery) persistenceService.getServerService().getQuery(
					ProductGroup.class);
			if (query != null)
			{
				Collection<ProductGroup> productGroups = query.selectAll(true);
				this.viewer.setInput(productGroups.toArray(new ProductGroup[0]));
			}
		}
		this.viewer.setFilters(this.filters);
		this.viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final StructuredSelection ssel = (StructuredSelection) ProductGroupComboViewerDialog.this.viewer
						.getSelection();
				if (!ssel.isEmpty())
				{
					ProductGroupComboViewerDialog.this.productGroup = (ProductGroup) ssel.getFirstElement();
				}

				ProductGroupComboViewerDialog.this.setOkButtonState();
			}
		});

		if (this.productGroup != null)
		{
			this.viewer.setSelection(new StructuredSelection(this.productGroup));
		}

		return composite;
	}

	private void initialize(final ProductGroup productGroup, final LabelProvider labelProvider,
			final ViewerFilter[] viewerFilters, final String label)
	{
		this.productGroup = productGroup;
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
