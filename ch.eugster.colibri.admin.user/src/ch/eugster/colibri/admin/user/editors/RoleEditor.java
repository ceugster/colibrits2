/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.user.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
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

import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Role;
import ch.eugster.colibri.persistence.model.RoleProperty;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.queries.RoleQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class RoleEditor extends AbstractEntityEditor<Role>
{
	public static final String ID = "ch.eugster.colibri.admin.role.editor";

	private Text name;

	private final Map<String, Button> roleProperties = new HashMap<String, Button>();

	public RoleEditor()
	{
		EntityMediator.addListener(Role.class, this);
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(Role.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity.equals(this.getEditorInput().getAdapter(Role.class)))
		{
			this.dispose();
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
		this.name.setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createNameSection(scrolledForm);
		this.createLoginSection(scrolledForm);
		this.createFunctionSection(scrolledForm);
		this.createDataSection(scrolledForm);
	}

	@Override
	protected String getName()
	{
		final Role role = (Role) this.getEditorInput().getAdapter(Role.class);
		if (role.getId() == null)
		{
			return "Neu";
		}
		return role.getName();
	}

	@Override
	protected String getText()
	{
		return "Rolle";
	}

	@Override
	protected void loadValues()
	{
		final Role role = (Role) this.getEditorInput().getAdapter(Role.class);
		this.name.setText(role.getName());

		for (final RoleProperty roleProperty : role.getRoleProperties())
		{
			final Button button = this.roleProperties.get(roleProperty.getKey());
			if (button != null)
			{
				if (roleProperty.isDeleted())
				{
					button.setSelection(false);
				}
				else if (role.getId().equals(Long.valueOf(1l)))
				{
					button.setSelection(true);
					button.setEnabled(false);
				}
				else
				{
					button.setSelection(Boolean.parseBoolean(roleProperty.getValue()));
				}
			}
		}

		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final Role role = (Role) this.getEditorInput().getAdapter(Role.class);

		role.setName(this.name.getText());

		final Map<String, RoleProperty> props = new HashMap<String, RoleProperty>();
		for (final RoleProperty prop : role.getRoleProperties())
		{
			props.put(prop.getKey(), prop);
		}

		final Set<String> keys = this.roleProperties.keySet();
		for (final String key : keys)
		{
			final Button button = this.roleProperties.get(key);
			RoleProperty prop = props.get(key);
			if (prop == null)
			{
				prop = this.createRoleProperty(role);
			}
			prop = this.updateRoleProperty(prop, key, Boolean.toString(button.getSelection()));
			props.put(prop.getKey(), prop);
		}
		final Collection<RoleProperty> rprops = new ArrayList<RoleProperty>();
		for (final RoleProperty p : props.values())
		{
			rprops.add(p);
		}
		role.setRoleProperties(rprops);
	}

	@Override
	protected boolean validate()
	{
		Message msg = this.getEmptyRoleMessage();
		if (msg == null)
		{
			msg = this.getUniqueRoleMessage();
		}
		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Role> input)
	{
		return input.getAdapter(Role.class) instanceof Role;
	}

	private Section createNameSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Rollenbezeichnung");
		section.setClient(this.fillNameSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				RoleEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createLoginSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Berechtigungen");
		section.setClient(this.fillLoginSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				RoleEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createFunctionSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Berechtigungen");
		section.setClient(this.fillFunctionSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				RoleEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createDataSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Berechtigungen");
		section.setClient(this.fillKeySection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				RoleEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private RoleProperty createRoleProperty(final Role role)
	{
		return RoleProperty.newInstance(role);
	}

	private Control fillNameSection(final Section parent)
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
				RoleEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillLoginSection(final Section parent)
	{
		final String key = "login.";

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		layout = new TableWrapLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;

		final Composite comp = this.formToolkit.createComposite(composite);
		comp.setLayoutData(layoutData);
		comp.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		Button button = this.formToolkit.createButton(comp, "Alle", SWT.PUSH);
		button.setLayoutData(layoutData);
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
				Set<Entry<String, Button>> entries = RoleEditor.this.roleProperties.entrySet();
				for (Entry<String, Button> entry : entries)
				{
					if (entry.getKey().startsWith(key))
					{
						entry.getValue().setSelection(true);
					}
				}
				RoleEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		button = this.formToolkit.createButton(comp, "Keine", SWT.PUSH);
		button.setLayoutData(layoutData);
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
				Set<Entry<String, Button>> entries = RoleEditor.this.roleProperties.entrySet();
				for (Entry<String, Button> entry : entries)
				{
					if (entry.getKey().startsWith(key))
					{
						entry.getValue().setSelection(false);
					}
				}
				RoleEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		button = this.formToolkit.createButton(comp, "Auswahl umkehren", SWT.PUSH);
		button.setLayoutData(layoutData);
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
				Set<Entry<String, Button>> entries = RoleEditor.this.roleProperties.entrySet();
				for (Entry<String, Button> entry : entries)
				{
					if (entry.getKey().startsWith(key))
					{
						entry.getValue().setSelection(!entry.getValue().getSelection());
					}
				}
				RoleEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		button = this.formToolkit.createButton(composite, "Anmeldung Administrator", SWT.CHECK);
		button.setLayoutData(layoutData);
		button.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				RoleEditor.this.setDirty(true);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				RoleEditor.this.setDirty(true);
			}
		});
		this.roleProperties.put("login.admin", button);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		button = this.formToolkit.createButton(composite, "Anmeldung Auswertungen", SWT.CHECK);
		button.setLayoutData(layoutData);
		button.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				RoleEditor.this.setDirty(true);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				RoleEditor.this.setDirty(true);
			}
		});
		this.roleProperties.put("login.report", button);

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillFunctionSection(final Section parent)
	{
		final String key = "function.";

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		layout = new TableWrapLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;

		final Composite comp = this.formToolkit.createComposite(composite);
		comp.setLayoutData(layoutData);
		comp.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		Button button = this.formToolkit.createButton(comp, "Alle", SWT.PUSH);
		button.setLayoutData(layoutData);
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
				Set<Entry<String, Button>> entries = RoleEditor.this.roleProperties.entrySet();
				for (Entry<String, Button> entry : entries)
				{
					if (entry.getKey().startsWith(key))
					{
						entry.getValue().setSelection(true);
					}
				}
				RoleEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		button = this.formToolkit.createButton(comp, "Keine", SWT.PUSH);
		button.setLayoutData(layoutData);
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
				Set<Entry<String, Button>> entries = RoleEditor.this.roleProperties.entrySet();
				for (Entry<String, Button> entry : entries)
				{
					if (entry.getKey().startsWith(key))
					{
						entry.getValue().setSelection(false);
					}
				}
				RoleEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		button = this.formToolkit.createButton(comp, "Auswahl umkehren", SWT.PUSH);
		button.setLayoutData(layoutData);
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
				Set<Entry<String, Button>> entries = RoleEditor.this.roleProperties.entrySet();
				for (Entry<String, Button> entry : entries)
				{
					if (entry.getKey().startsWith(key))
					{
						entry.getValue().setSelection(!entry.getValue().getSelection());
					}
				}
				RoleEditor.this.setDirty(true);
			}
		});

		final FunctionType[] functionTypes = FunctionType.values();
		for (final FunctionType functionType : functionTypes)
		{
			layoutData = new TableWrapData();
			layoutData.align = TableWrapData.FILL;
			layoutData.grabHorizontal = true;

			button = this.formToolkit.createButton(composite, functionType.toString(), SWT.CHECK);
			button.setLayoutData(layoutData);
			button.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetDefaultSelected(final SelectionEvent e)
				{
					RoleEditor.this.setDirty(true);
				}

				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					RoleEditor.this.setDirty(true);
				}
			});
			this.roleProperties.put(functionType.key(), button);
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillKeySection(final Section parent)
	{
		final String key = "key.";

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		layout = new TableWrapLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;

		final Composite comp = this.formToolkit.createComposite(composite);
		comp.setLayoutData(layoutData);
		comp.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		Button button = this.formToolkit.createButton(comp, "Alle", SWT.PUSH);
		button.setLayoutData(layoutData);
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
				Set<Entry<String, Button>> entries = RoleEditor.this.roleProperties.entrySet();
				for (Entry<String, Button> entry : entries)
				{
					if (entry.getKey().startsWith(key))
					{
						entry.getValue().setSelection(true);
					}
				}
				RoleEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		button = this.formToolkit.createButton(comp, "Keine", SWT.PUSH);
		button.setLayoutData(layoutData);
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
				Set<Entry<String, Button>> entries = RoleEditor.this.roleProperties.entrySet();
				for (Entry<String, Button> entry : entries)
				{
					if (entry.getKey().startsWith(key))
					{
						entry.getValue().setSelection(false);
					}
				}
				RoleEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		button = this.formToolkit.createButton(comp, "Auswahl umkehren", SWT.PUSH);
		button.setLayoutData(layoutData);
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
				Set<Entry<String, Button>> entries = RoleEditor.this.roleProperties.entrySet();
				for (Entry<String, Button> entry : entries)
				{
					if (entry.getKey().startsWith(key))
					{
						entry.getValue().setSelection(!entry.getValue().getSelection());
					}
				}
				RoleEditor.this.setDirty(true);
			}
		});

		final KeyType[] keyTypes = KeyType.values();
		for (final KeyType keyType : keyTypes)
		{
			if (keyType.key().startsWith(key))
			{
				layoutData = new TableWrapData();
				layoutData.align = TableWrapData.FILL;
				layoutData.grabHorizontal = true;

				button = this.formToolkit.createButton(composite, keyType.toString(), SWT.CHECK);
				button.setLayoutData(layoutData);
				button.addSelectionListener(new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(final SelectionEvent e)
					{
						RoleEditor.this.setDirty(true);
					}

					@Override
					public void widgetSelected(final SelectionEvent e)
					{
						RoleEditor.this.setDirty(true);
					}
				});
				this.roleProperties.put(keyType.key(), button);
			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Message getEmptyRoleMessage()
	{
		Message msg = null;

		if (this.name.getText().isEmpty())
		{
			msg = new Message(this.name, "Rollenbezeichnung", "Die Rollenbezeichnung darf nicht leer sein.");
		}
		return msg;
	}

	private Message getUniqueRoleMessage()
	{
		Message msg = null;

		final Role role = (Role) this.getEditorInput().getAdapter(Role.class);

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final RoleQuery query = (RoleQuery) persistenceService.getServerService().getQuery(Role.class);
			if (!query.isNameUnique(this.name.getText(), role.getId()))
			{
				msg = new Message(this.name, "Fehler");
				msg.setMessage("Die gewählte Rollenbezeichnung wird bereits verwendet. Wählen Sie eine andere Rollenbezeichnung.");
			}
		}
		return msg;
	}

	private RoleProperty updateRoleProperty(final RoleProperty prop, final String key, final String value)
	{
		prop.setKey(key);
		prop.setValue(value);
		return prop;
	}
}
