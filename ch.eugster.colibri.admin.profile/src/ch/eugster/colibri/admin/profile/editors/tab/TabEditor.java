/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JPanel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
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
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.admin.profile.menus.KeyPopupMenu;
import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TabEditor extends AbstractEntityEditor<Tab> implements PropertyChangeListener
{
	public static final String ID = "ch.eugster.colibri.admin.profile.tab.editor";

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;
	
	public static final String KEY_PANEL = "panel";

	private ScrolledForm scrolledForm;

	private IDialogSettings settings;
	
	private final Color lightGrey = new Color(PlatformUI.getWorkbench().getDisplay(), 230, 230, 230);

	/*
	 * name section
	 */
	private Text name;

	private Spinner tabPos;

	private Spinner tabRows;

	private Spinner tabCols;

	private TreeViewer viewer;

	private JPanel panel;

	private boolean failOverState;
	
	private Tree selectionTree;
	
	private Composite buttonField;

	private Frame frame;
	
	private final Map<Integer, Map<Integer, Key[]>> keyMap = new HashMap<Integer, Map<Integer, Key[]>>();

	public TabEditor()
	{
		settings = Activator.getDefault().getDialogSettings().getSection("tab.editor");
		if (settings == null)
		{
			settings = Activator.getDefault().getDialogSettings().addNewSection("tab.editor");
			settings.put("sash.weight.left", 1);
			settings.put("sash.weight.right", 1);
		}
		
	}

	public KeyPopupMenu createPopupMenu(final TabEditorButton button, final TabEditor editor)
	{
		KeyPopupMenu menu = null;
		try
		{
			Class<?> popupMenuClass = null;
			final KeyType keyType = button.getKeys()[1].getKeyType();
			final String popupMenuClassName = TabEditorButton.getPopupMenuClassName(keyType);
			if (popupMenuClassName == null)
			{
				popupMenuClass = TabEditorButton.getPopupMenuClass(button.getKeys()[1].getFunctionType());
			}
			else
			{
				popupMenuClass = Class.forName(popupMenuClassName);
			}

			final Constructor<?> constructor = popupMenuClass.getConstructor(TabEditorButton.class, TabEditor.class);
			final Object[] parameters = new Object[2];
			parameters[0] = button;
			parameters[1] = editor;
			menu = (KeyPopupMenu) constructor.newInstance(parameters);
			button.setInstalledMenu(menu);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return menu;
	}

	@Override
	public void dispose()
	{
		this.lightGrey.dispose();
		this.persistenceServiceTracker.close();
		EntityMediator.removeListener(Profile.class, this);
		EntityMediator.removeListener(Configurable.class, this);
		EntityMediator.removeListener(Tab.class, this);
		EntityMediator.removeListener(ProductGroup.class, this);
		EntityMediator.removeListener(PaymentType.class, this);
		EntityMediator.removeListener(TaxRate.class, this);
		EntityMediator.removeListener(Tax.class, this);
		super.dispose();
	}

	public boolean getFailOverState()
	{
		return failOverState;
	}

	private void setFailOverState(boolean state)
	{
		failOverState = state;
	}

	public Map<Integer, Map<Integer, Key[]>> getKeyMap()
	{
		return this.keyMap;
	}

	public TreeViewer getKeyViewer()
	{
		return this.viewer;
	}

	public Tab getTab()
	{
		return (Tab) ((TabEditorInput) this.getEditorInput()).getAdapter(Tab.class);
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		final TabEditorInput input = (TabEditorInput) this.getEditorInput();
		final Tab tab = (Tab) input.getAdapter(Tab.class);
		if (entity instanceof Profile)
		{
			if (entity.equals(tab.getConfigurable().getProfile()))
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(this, false);
			}
		}
		else if (entity instanceof Configurable)
		{
			if (entity.equals(tab.getConfigurable()))
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(this, false);
			}
		}
		else if (entity instanceof Tab)
		{
			if (entity.equals(tab))
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(this, false);
			}
		}
		else if (entity instanceof ProductGroup)
		{
			UIJob job = new UIJob("Updating viewer...") 
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					viewer.remove(entity);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof PaymentType)
		{
			UIJob job = new UIJob("Updating viewer...") 
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					viewer.remove(entity);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof TaxRate)
		{
			UIJob job = new UIJob("Updating viewer...") 
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					viewer.remove(entity);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof Tax)
		{
			UIJob job = new UIJob("Updating viewer...") 
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					viewer.remove(entity);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	private void updateButtons()
	{
		final Component[] components = TabEditor.this.panel.getComponents();
		for (final Component component : components)
		{
			if (component instanceof TabEditorButton)
			{
				final TabEditorButton button = (TabEditorButton) component;
				button.silentUpdate(TabEditor.this.getFailOverState());
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getPropertyName().equals("dirty"))
		{
			final UIJob uiJob = new UIJob("set dirty")
			{
				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor)
				{
					TabEditor.this.setDirty(true);
					return Status.OK_STATUS;
				}
			};
			uiJob.schedule();
		}
	}

	@Override
	public void propertyChanged(final Object source, final int propId)
	{
		if (propId == IEditorPart.PROP_DIRTY)
		{
			this.setDirty(true);
		}
		else if (propId == IEditorPart.PROP_INPUT)
		{
		}
	}

	@Override
	public void setFocus()
	{
		this.name.setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.scrolledForm = scrolledForm;
		this.createGeneralSection(scrolledForm);
		this.createSizeSection(scrolledForm);
		this.createButtonSection(scrolledForm);
		EntityMediator.addListener(Profile.class, this);
		EntityMediator.addListener(Configurable.class, this);
		EntityMediator.addListener(Tab.class, this);
		EntityMediator.addListener(ProductGroup.class, this);
		EntityMediator.addListener(PaymentType.class, this);
		EntityMediator.addListener(TaxRate.class, this);
		EntityMediator.addListener(Tax.class, this);
	}

	@Override
	protected String getName()
	{
		final Tab tab = (Tab) ((TabEditorInput) this.getEditorInput()).getAdapter(Tab.class);
		if (tab.getId() == null)
		{
			return "Neu";
		}
		return tab.getName();
	}

	// @Override
	// protected Message getMessage(final ErrorCode errorCode)
	// {
	// final Message msg = null;
	//
	// return msg;
	// }

	@Override
	protected String getText()
	{
		return "Tab";
	}

	@Override
	protected void loadValues()
	{
		final Tab tab = (Tab) ((TabEditorInput) this.getEditorInput()).getAdapter(Tab.class);
		this.name.setText(tab.getName());

		/*
		 * TabbedPane
		 */
		this.tabPos.setValues(tab.getPos(), 1, 20, 0, 1, 4);
		this.tabRows.setValues(tab.getRows() == 0 ? 1 : tab.getRows(), 1, 20, 0, 1, 4);
		this.tabCols.setValues(tab.getCols() == 0 ? 1 : tab.getCols(), 1, 20, 0, 1, 4);

		this.fillKeyMap(tab);

		this.tabRows.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent event)
			{
				TabEditor.this.updatePanel();
			}
		});

		this.tabCols.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent event)
			{
				TabEditor.this.updatePanel();
			}
		});

		this.updatePanel();

		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		/*
		 * Panels
		 */
		final Tab tab = (Tab) ((TabEditorInput) this.getEditorInput()).getAdapter(Tab.class);
		tab.setTimestamp(GregorianCalendar.getInstance(Locale.getDefault()));
		tab.setName(this.name.getText());
		/**
		 * Tabbed Pane
		 */
		tab.setPos(this.tabPos.getSelection());
		tab.setRows(this.tabRows.getSelection());
		tab.setCols(this.tabCols.getSelection());

		/*
		 * Zuerst bestehende Keys aktualisieren
		 */
		this.updateExistingKeys(tab);
		/*
		 * Anschliessend neue Keys in die Collection aufnehmen
		 */
		this.addNewKeys(tab);

//		if (tab.getId() == null)
//		{
//			tab.getConfigurable().addTab(tab);
//		}
	}

	@Override
	protected void updateControls()
	{
		this.fillKeyMap((Tab) ((TabEditorInput) this.getEditorInput()).getAdapter(Tab.class));
		this.updatePanel();
		super.updateControls();
	}

	@Override
	protected boolean validate()
	{
		Message msg = this.getEmptyNameMessage();

		if (msg == null)
		{
			msg = this.getMissingKeyInformationMessage();
		}

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Tab> input)
	{
		return input.getAdapter(Tab.class) instanceof Tab;
	}

	private void addNewKeys(final Tab tab)
	{
		final Collection<Key> existingKeys = tab.getKeys();
		final Collection<Map<Integer, Key[]>> rows = this.keyMap.values();
		for (final Map<Integer, Key[]> row : rows)
		{
			final Collection<Key[]> keys = row.values();
			for (final Key[] key : keys)
			{
				if ((key != null) && (key[1] != null) && (key[1].getId() == null) && !key[1].isDeleted())
				{
					key[0] = key[1].update(Key.newInstance(key[1].getTab()));
					existingKeys.add(key[0]);
				}
			}
		}
	}

	private boolean checkParent(final Key key)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();

		final Long parentId = key.getParentId();
		if (parentId != null)
		{
			if (key.getKeyType().equals(KeyType.FUNCTION))
			{
				if (key.getFunctionType().equals(FunctionType.FUNCTION_OPEN_DRAWER))
				{
				}
				else if (key.getFunctionType().equals(FunctionType.FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION))
				{
				}
				else if (key.getFunctionType().equals(FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION))
				{
				}
//				else if (key.getFunctionType().equals(FunctionType.FUNCTION_STORE_RECEIPT))
//				{
//				}
			}
			else if (key.getKeyType().equals(KeyType.OPTION))
			{

			}
			else if (key.getKeyType().equals(KeyType.PAYMENT_TYPE))
			{
				return persistenceService == null ? false : persistenceService.getServerService().find(
						PaymentType.class, parentId) != null;
			}
			else if (key.getKeyType().equals(KeyType.PRODUCT_GROUP))
			{
				return persistenceService == null ? false : persistenceService.getServerService().find(
						ProductGroup.class, parentId) != null;
			}
			else if (key.getKeyType().equals(KeyType.TAX_RATE))
			{
				return persistenceService == null ? false : persistenceService.getServerService().find(TaxRate.class,
						parentId) != null;
			}
		}
		return true;
	}

	private TabEditorButton createButton(final Key[] keys, final boolean state)
	{
		final TabEditorButton button = new TabEditorButton(keys, state);
		if (!keys[1].isDeleted())
		{
			button.add(this.createPopupMenu(button, this));
		}
		button.setDropTarget(new DropTarget(button, new KeyDropTargetListener(button)));
		button.addPropertyChangeListener(this);
		return button;
	}

	private TabEditorButton createButton(final Point place)
	{
		final TabEditorButton button = new TabEditorButton(place);
		button.setDropTarget(new DropTarget(button, new KeyDropTargetListener(button)));
		button.addPropertyChangeListener(this);
		return button;
	}

	private Section createButtonSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		layoutData.heightHint = 360;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 2;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Tastenanordnung");
		section.setClient(this.fillButtonSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				TabEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createGeneralSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Allgemein");
		section.setClient(this.fillGeneralSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				TabEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createSizeSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Tastenanordnung");
		section.setClient(this.fillSizeSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				TabEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillButtonSection(final Section parent)
	{
		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;
		layoutData.grabVertical = true;
//		layoutData.heightHint = 720;

//		GridLayout layout = new GridLayout(2, false);
//		layout.horizontalSpacing = 10;
//		layout.marginHeight = 4;
//		layout.marginWidth = 4;
//		layout.verticalSpacing = 0;

		final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setLayoutData(layoutData);
		sashForm.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		this.formToolkit.adapt(sashForm);
//		final Composite composite = this.formToolkit.createComposite(parent);
//		composite.setLayoutData(layoutData);
//		composite.setLayout(layout);

//		GridData gridData = new GridData(GridData.FILL_BOTH);
//		gridData.widthHint = 180;
//		gridData.minimumWidth = 100;
//		gridData.heightHint = 240;
//		gridData.minimumHeight = 180;

		selectionTree = this.formToolkit.createTree(sashForm, SWT.BORDER);
		selectionTree.addControlListener(new ControlAdapter() 
		{
			@Override
			public void controlResized(ControlEvent e) 
			{
				settings.put("sash.weight.left", selectionTree.getSize().x);
				settings.put("sash.weight.right", buttonField.getSize().x);
			}
		});
		selectionTree.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		this.formToolkit.adapt(selectionTree);

		this.viewer = new TreeViewer(selectionTree);
		this.viewer.setContentProvider(new KeyTypeContentProvider());
		this.viewer.setLabelProvider(new KeyTypeLabelProvider());

		final int ops = DND.DROP_COPY;
		final Transfer[] transfers = new Transfer[] { KeyTransfer.getInstance() };
		this.viewer.addDragSupport(ops, transfers, new KeyDragSourceListener(this));

//		gridData = new GridData();
//		gridData.widthHint = 240;
//		gridData.minimumWidth = 160;
//		gridData.heightHint = 160;
//		gridData.minimumHeight = 120;

		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;

		buttonField = this.formToolkit.createComposite(sashForm, SWT.EMBEDDED);
		buttonField.addControlListener(new ControlAdapter() 
		{
			@Override
			public void controlResized(ControlEvent e) 
			{
				settings.put("sash.weight.left", selectionTree.getSize().x);
				settings.put("sash.weight.right", buttonField.getSize().x);
			}
		});
		int[] weights = new int[2];
		weights[0] = Integer.valueOf(settings.get("sash.weight.left")).intValue();
		weights[1] = Integer.valueOf(settings.get("sash.weight.right")).intValue();
		sashForm.setWeights(weights);

		//		frameComposite.setLayoutData(gridData);
		buttonField.setLayout(layout);

		frame = SWT_AWT.new_Frame(buttonField);
		frame.setLayout(new java.awt.BorderLayout());

		this.panel = new JPanel();
		this.panel.setLayout(new java.awt.GridLayout());
		frame.add(this.panel, BorderLayout.CENTER);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		final Button selectState = this.formToolkit.createButton(parent, "Im Failover-Modus darstellen", SWT.CHECK);
		selectState.setLayoutData(gridData);
		selectState.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				this.widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event)
			{
				TabEditor.this.setFailOverState(selectState.getSelection());
				updateButtons();
			}
		});

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(ServiceReference<PersistenceService> reference) 
			{
				PersistenceService service = (PersistenceService) super.addingService(reference);
				viewer.setInput(service);
				return service;
			}

			@Override
			public void removedService(ServiceReference<PersistenceService> reference, final PersistenceService service) 
			{
				if (viewer != null && !viewer.getControl().isDisposed())
				{
					viewer.setInput(null);
				}
			}
		};
		this.persistenceServiceTracker.open();

		this.formToolkit.paintBordersFor(sashForm);

		return sashForm;
	}

	public Frame getFrame()
	{
		return frame;
	}
	
	private Control fillGeneralSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		final Label label = this.formToolkit.createLabel(composite, "Bezeichnung", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.name = this.formToolkit.createText(composite, "");
		this.name.setLayoutData(layoutData);
		this.name.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				TabEditor.this.propertyChanged(TabEditor.this.name, IEditorPart.PROP_DIRTY);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private void fillKeyMap(final Tab tab)
	{
		this.keyMap.clear();
		final Key[] keys = tab.getKeys().toArray(new Key[0]);
		for (final Key key : keys)
		{
			Map<Integer, Key[]> cols = this.keyMap.get(new Integer(key.getTabRow()));
			if (cols == null)
			{
				cols = new HashMap<Integer, Key[]>();
				this.keyMap.put(new Integer(key.getTabRow()), cols);
			}
			cols.put(new Integer(key.getTabCol()), new Key[] { key, null });
		}
	}

	private Control fillSizeSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		Label label = this.formToolkit.createLabel(composite, "Position", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		this.tabPos = new Spinner(composite, SWT.NONE);
		this.tabPos.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		this.tabPos.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				this.widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event)
			{
				TabEditor.this.propertyChanged(TabEditor.this.tabPos, IEditorPart.PROP_DIRTY);
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Anzahl Zeilen", SWT.NONE);
		label.setLayoutData(layoutData);
		label.setText("Anzahl Zeilen");

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		this.tabRows = new Spinner(composite, SWT.NONE);
		this.tabRows.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		this.tabRows.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				this.widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event)
			{
				TabEditor.this.propertyChanged(TabEditor.this.tabRows, IEditorPart.PROP_DIRTY);
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Anzahl Spalten", SWT.NONE);
		label.setLayoutData(layoutData);
		label.setText("Anzahl Spalten");

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		this.tabCols = new Spinner(composite, SWT.NONE);
		this.tabCols.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		this.tabCols.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				this.widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event)
			{
				TabEditor.this.propertyChanged(TabEditor.this.tabCols, IEditorPart.PROP_DIRTY);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Message getEmptyNameMessage()
	{
		Message msg = null;

		if (this.name.getText().isEmpty())
		{
			msg = new Message(this.name, "Fehler");
			msg.setMessage("Der Tab muss eine Bezeichnung haben.");
		}
		return msg;
	}

	private Message getMissingKeyInformationMessage()
	{
		Message msg = null;

		final Collection<Map<Integer, Key[]>> cols = this.keyMap.values();
		for (final Map<Integer, Key[]> col : cols)
		{
			for (final Key[] keys : col.values())
			{
				if (keys[1] != null)
				{
					if (!keys[1].isDeleted())
					{
						if (keys[1].getKeyType().equals(KeyType.FUNCTION))
						{
							if (keys[1].getFunctionType().equals(FunctionType.FUNCTION_OPEN_DRAWER))
							{
								if (keys[1].getParentId() == null)
								{
									msg = new Message(this.name, "Fehler");
									msg.setMessage("Der Taste " + FunctionType.FUNCTION_OPEN_DRAWER.toCode()
											+ " muss ein Schublade zugeordnet werden.");
									return msg;
								}
							}
							else if (keys[1].getFunctionType().equals(
									FunctionType.FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION))
							{
								if (keys[1].getParentId() == null)
								{
									msg = new Message(this.name, "Fehler");
									msg.setMessage("Für die Taste "
											+ FunctionType.FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION.toCode()
											+ " muss die Wechselgeldwährung festgelegt werden.");
									return msg;
								}
							}
							else if (keys[1].getFunctionType().equals(
									FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION))
							{
								if (keys[1].getParentId() == null)
								{
									msg = new Message(this.name, "Fehler");
									msg.setMessage("Für die Taste "
											+ FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION.toCode()
											+ " muss die Wechselgeldwährung festgelegt werden.");
									return msg;
								}
							}
//							else if (keys[1].getFunctionType().equals(FunctionType.FUNCTION_STORE_RECEIPT))
//							{
//								if (keys[1].getParentId() == null)
//								{
//									msg = new Message(this.name, "Fehler");
//									msg.setMessage("Für die Taste " + FunctionType.FUNCTION_STORE_RECEIPT.toCode()
//											+ " muss die Wechselgeldwährung festgelegt werden.");
//									return msg;
//								}
//							}
							else if (keys[1].getFunctionType().equals(FunctionType.FUNCTION_SELECT_CUSTOMER))
							{
								if (keys[1].getParentId() == null)
								{
									msg = new Message(this.name, "Fehler");
									msg.setMessage("Für die Taste " + FunctionType.FUNCTION_SELECT_CUSTOMER.toCode()
											+ " muss die Warengruppe für bezahlte Rechnungen festgelegt werden.");
									return msg;
								}
							}
						}
						else if (keys[1].getKeyType().equals(KeyType.OPTION))
						{

						}
						else if (keys[1].getKeyType().equals(KeyType.PAYMENT_TYPE))
						{
							if (keys[1].getParentId() == null)
							{
								msg = new Message(this.name, "Fehler");
								msg.setMessage("Für die Taste " + KeyType.PAYMENT_TYPE.toString()
										+ " muss die Zahlungsart festgelegt werden.");
								return msg;
							}
						}
						else if (keys[1].getKeyType().equals(KeyType.PRODUCT_GROUP))
						{
							if (keys[1].getParentId() == null)
							{
								msg = new Message(this.name, "Fehler");
								msg.setMessage("Für die Taste " + KeyType.PRODUCT_GROUP.toString()
										+ " muss die Warengruppe festgelegt werden.");
								return msg;
							}
						}
						else if (keys[1].getKeyType().equals(KeyType.TAX_RATE))
						{
							if (keys[1].getParentId() == null)
							{
								msg = new Message(this.name, "Fehler");
								msg.setMessage("Für die Taste " + KeyType.TAX_RATE.toString()
										+ " muss die Mehrwertsteuer festgelegt werden.");
								return msg;
							}
						}
					}
				}
			}
		}
		return msg;
	}

	private void updateExistingKeys(final Tab tab)
	{
		final Collection<Key> keys = tab.getKeys();
		for (Key key : keys)
		{
			final Map<Integer, Key[]> row = this.keyMap.get(Integer.valueOf(key.getTabRow()));
			final Key[] mappedKeys = row.get(Integer.valueOf(key.getTabCol()));
			if ((mappedKeys != null) && (mappedKeys[1] != null))
			{
				key = mappedKeys[1].update(key);
				key.setDeleted(mappedKeys[1].isDeleted());
			}
		}
	}

	private void updatePanel()
	{
		final int rows = this.tabRows.getSelection();
		final int cols = this.tabCols.getSelection();

		this.panel.removeAll();
		this.panel.setLayout(new java.awt.GridLayout(rows, cols));

		for (int i = 0; i < rows; i++)
		{
			final Map<Integer, Key[]> row = this.keyMap.get(new Integer(i));
			if (row == null)
			{
				for (int j = 0; j < cols; j++)
				{
					this.panel.add(this.createButton(new Point(i, j)));
				}
			}
			else
			{
				for (int j = 0; j < cols; j++)
				{
					final Key[] keys = row.get(new Integer(j));
					if ((keys == null) || (keys[0] == null) || keys[0].isDeleted())
					{
						this.panel.add(this.createButton(new Point(i, j)));
					}
					else
					{
						if (this.checkParent(keys[0]))
						{
							this.panel.add(this.createButton(keys, this.getFailOverState()));
						}
						else
						{
							this.panel.add(this.createButton(new Point(i, j)));
						}
					}
				}
			}
		}
		this.panel.validate();
	}
}
