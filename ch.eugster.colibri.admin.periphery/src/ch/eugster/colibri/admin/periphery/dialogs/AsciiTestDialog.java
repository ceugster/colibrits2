package ch.eugster.colibri.admin.periphery.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.admin.periphery.views.PeripheryView;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;

public class AsciiTestDialog extends TitleAreaDialog
{
	private ListViewer asciiViewer;
	
	private Label charLabel;

	public AsciiTestDialog(Shell parentShell) 
	{
		super(parentShell);
	}
	
	@Override
	protected Control createContents(Composite parent)
	{
		parent.setLayout(new GridLayout());
		
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("ASCII Code wählen");
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 240;
		
		List list = new List(composite, SWT.SINGLE | SWT.VERTICAL | SWT.BORDER);
		list.setLayoutData(gridData);
		
		asciiViewer = new ListViewer(list);
		asciiViewer.setContentProvider(new ArrayContentProvider());
		asciiViewer.setLabelProvider(new LabelProvider() 
		{
			@Override
			public String getText(Object element) 
			{
				return element.toString();
			}
		});
		asciiViewer.setInput(getAsciiCodes());
		
		label = new Label(composite, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Bildschirmzeichen");
		
		charLabel = new Label(composite, SWT.BORDER);
		charLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		asciiViewer.addSelectionChangedListener(new ISelectionChangedListener() 
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{
				IStructuredSelection ssel = (IStructuredSelection) event.getSelection();
				if (!ssel.isEmpty())
				{
					String value = (String)ssel.getFirstElement();
					try
					{
						int character = Integer.valueOf(value);
						charLabel.setText(String.valueOf((char) character));
						printAscii(getAscii());
					}
					catch (NumberFormatException e)
					{
						MessageDialog.openInformation(asciiViewer.getControl().getShell(), "Keinen ASCII Code gewählt", "Sie haben keinen ASCII Code ausgewählt.");
						return;
					}

				}
			}
		});
		
		return composite;
	}
	
	private String[] getAsciiCodes()
	{
		java.util.List<String> codes = new ArrayList<String>();
		for (int i = 32; i < 256; i++)
		{
			codes.add(String.valueOf(i));
		}
		return codes.toArray(new String[0]);
	}
	
	private int getAscii() throws NumberFormatException
	{
		IStructuredSelection ssel= (IStructuredSelection) asciiViewer.getSelection();
		return Integer.valueOf(ssel.getFirstElement().toString());
	}
	
	private void printAscii(int ascii)
	{
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (part instanceof PeripheryView)
		{
			PeripheryView view = (PeripheryView) part;
			if (view.getViewer().getSelection().isEmpty())
			{
				MessageDialog.openInformation(this.getShell(), "Kein Kundendisplay ausgewählt", "Bitte wählen Sie das Kundendisplay aus, für das die ASCII Codes getestet werden sollen.");
				return;
			}
			
			IStructuredSelection ssel = (IStructuredSelection) view.getViewer().getSelection();
			if (ssel.getFirstElement() instanceof ServiceReference)
			{
				final ServiceReference<?> ref = (ServiceReference<?>) ssel.getFirstElement();
				final Integer group = (Integer) ref.getProperty("custom.group");
				if (group instanceof Integer)
				{
					final int peripheryGroup = (group).intValue();
					if (peripheryGroup == 0)
					{
						MessageDialog.openInformation(this.getShell(), "Kein Kundendisplay ausgewählt", "Bitte wählen Sie das Kundendisplay aus, für das die ASCII Codes getestet werden sollen.");
						return;
					}
					else if (peripheryGroup == 1)
					{
						ServiceTracker<CustomerDisplayService, CustomerDisplayService> tracker = new ServiceTracker<CustomerDisplayService, CustomerDisplayService>(Activator.getDefault().getBundle().getBundleContext(), CustomerDisplayService.class, null);
						tracker.open();
						@SuppressWarnings("unchecked")
						CustomerDisplayService service = tracker.getService((ServiceReference<CustomerDisplayService>)ref);
						if (service == null)
						{
							MessageDialog.openInformation(this.getShell(), "Service nicht aktiv", "Der Service für das gewählte Kundendisplay ist nicht aktiviert.");
							return;
						}
						
						try 
						{
							service.testAscii(new byte[] { (byte) ascii });
						} 
						catch (Exception e) 
						{
							MessageDialog.openError(this.getShell(), "Kundendisplay", "Fehler: " + e.getLocalizedMessage());
						}
						finally
						{
							tracker.close();
						}
					}
				}
			}
		}
		else
		{
			MessageDialog.openInformation(this.getShell(), "Kein Kundendisplay ausgewählt", "Bitte wählen Sie ein Kundendisplay aus der Liste aus, für das die ASCII Codes getestet werden sollen.");
		}
	}
}
