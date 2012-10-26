package ch.eugster.colibri.report.list.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

public class ListView extends ViewPart implements IViewPart, ISelectionListener, ISelectionChangedListener
{
	public static final String ID = "ch.eugster.colibri.report.list.views.list";

	private ComboViewer reportViewer;

	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new GridLayout());

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(gridData);
		group.setText("Auswahl Liste");

		Combo combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		reportViewer = new ComboViewer(combo);
		reportViewer.setContentProvider(new ArrayContentProvider());
		reportViewer.setLabelProvider(new ReportLabelProvider());
		reportViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				if (printOnReceiptPrinter != null)
				{
					StructuredSelection ssel = (StructuredSelection) event.getSelection();
					printOnReceiptPrinter.setEnabled(!ssel.isEmpty());
				}
			}
		});

		// String[] reports = new String[] { "Rabattliste", Mehrwertsteuerliste
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

}
