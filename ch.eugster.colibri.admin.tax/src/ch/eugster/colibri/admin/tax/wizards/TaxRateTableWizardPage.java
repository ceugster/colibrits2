package ch.eugster.colibri.admin.tax.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.graphics.Image;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.tax.TaxActivator;
import ch.eugster.colibri.admin.ui.wizards.TableWizardPage;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TaxRateTableWizardPage extends TableWizardPage<TaxRate>
{
	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public TaxRateTableWizardPage(final String name, final String title, final ImageDescriptor image)
	{
		super(name, title, image);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(TaxActivator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public boolean canDelete()
	{
		return false;
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		super.dispose();
	}

	@Override
	public int[] getColumnAlignments()
	{
		return new int[] { SWT.LEFT, SWT.LEFT };
	}

	@Override
	public String[] getColumnNames()
	{
		return new String[] { "Code", "Bezeichnung" };
	}

	@Override
	public TaxRate[] getInput()
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final TaxRateQuery query = (TaxRateQuery) persistenceService.getServerService().getQuery(TaxRate.class);
			return query.selectAll(false).toArray(new TaxRate[0]);
		}
		else
		{
			return new TaxRate[0];
		}
	}

	@Override
	public ILabelProvider getLabelProvider()
	{
		return new TaxRateTableLabelProvider();
	}

	@Override
	public TaxRate getNewEntity()
	{
		return TaxRate.newInstance();
	}

	@Override
	public String getPageMessage()
	{
		return "Wählen Sie eine Mehrwertsteuersatzart aus, die Sie bearbeiten oder entfernen wollen oder\nklicken Sie auf 'Neu', um eine neue Mehrwertsteuersatzart zu erstellen.";
	}

	@Override
	public ViewerSorter getSorter()
	{
		return new TaxRateTableSorter();
	}

	@Override
	protected int getTableStyle()
	{
		return SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
	}

	@Override
	protected DragSourceListener getViewerDragListener(final TableViewer viewer)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ViewerDropAdapter getViewerDropAdapter(final TableViewer viewer)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public class TaxRateTableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		@Override
		public Image getColumnImage(final Object element, final int index)
		{
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int index)
		{
			final TaxRate taxRate = (TaxRate) element;
			switch (index)
			{
				case 0:
					return taxRate.getCode();
				case 1:
					return taxRate.getName();
				default:
					return "";
			}
		}
	}

	private class TaxRateTableSorter extends ViewerSorter
	{
		@Override
		public int compare(final Viewer viewer, final Object object1, final Object object2)
		{
			if (object1 instanceof TaxRate)
			{
				final TaxRate taxRate1 = (TaxRate) object1;
				final TaxRate taxRate2 = (TaxRate) object2;
				return taxRate1.getCode().compareTo(taxRate2.getCode());
			}
			else
			{
				return 0;
			}
		}
	}
}
