/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.user.editors;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
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
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.queries.RoleQuery;
import ch.eugster.colibri.persistence.queries.UserQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class UserEditor extends AbstractEntityEditor<User>
{
	public static final String ID = "ch.eugster.colibri.admin.user.editor";

	private Text username;

	private Text password;

	private FormattedText posLogin;

	private ComboViewer role;

	private Button defaultUser;

	public UserEditor()
	{
		EntityMediator.addListener(User.class, this);
		EntityMediator.addListener(Role.class, this);
	}

	// @Override
	// protected Message getMessage(ErrorCode errorCode)
	// {
	// Message msg = null;
	// // TODO
	// if (errorCode.equals(PersistenceException.ErrorCode.CONNECTION_FAILOR))
	// {
	// msg = this.getUniqueUsernameMessage();
	// if (msg == null)
	// {
	// msg = this.getUniquePosLoginMessage();
	// }
	// }
	// return msg;
	// }

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(User.class, this);
		EntityMediator.removeListener(Role.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity.equals(this.getEditorInput().getAdapter(User.class)))
		{
			this.dispose();
		}
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		if (entity instanceof Role)
		{
			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
			if (persistenceService != null)
			{
				final RoleQuery query = (RoleQuery) persistenceService.getServerService().getQuery(Role.class);
				final Role[] roles = query.selectAll(false).toArray(new Role[0]);
				this.role.setInput(roles);
			}
		}
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		if (entity instanceof Role)
		{
			final Role[] roles = (Role[]) this.role.getInput();
			for (Role role : roles)
			{
				if (role.getId().equals(entity.getId()))
				{
					role = (Role) entity;
				}
			}
			this.role.setInput(roles);
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
		this.username.setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createUserSection(scrolledForm);
		this.createLoginSection(scrolledForm);
		this.createStatusSection(scrolledForm);
	}

	@Override
	protected String getName()
	{
		final User user = (User) this.getEditorInput().getAdapter(User.class);
		if (user.getId() == null)
		{
			return "Neu";
		}
		return user.getUsername();
	}

	@Override
	protected String getText()
	{
		return "Benutzer";
	}

	@Override
	protected void loadValues()
	{
		final User user = (User) this.getEditorInput().getAdapter(User.class);
		this.username.setText(user.getUsername());
		this.password.setText(user.getPassword());
		this.posLogin.setValue(new Integer(user.getPosLogin() == null ? 0 : user.getPosLogin()));
		if (user.getRole() == null)
		{
			final Role[] roles = (Role[]) this.role.getInput();
			this.role.setSelection(new StructuredSelection(new Role[] { roles[roles.length - 1] }));
		}
		else
		{
			this.role.setSelection(new StructuredSelection(new Role[] { user.getRole() }));
		}
		this.defaultUser.setSelection(user.isDefaultUser());
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final User user = (User) this.getEditorInput().getAdapter(User.class);
		user.setUsername(this.username.getText().toLowerCase());
		user.setPassword(this.password.getText());
		user.setPosLogin(Integer.parseInt(this.posLogin.getValue().toString()));
		final StructuredSelection ssel = (StructuredSelection) this.role.getSelection();
		user.setRole((Role) ssel.getFirstElement());
		user.setDefaultUser(this.defaultUser.getSelection());
	}

	@Override
	protected boolean validate()
	{
		Message msg = this.getEmptyUsernameMessage();
		if (msg == null)
		{
			msg = this.getUniqueUsernameMessage();
		}

		if (msg == null)
		{
			msg = this.getEmptyPosLoginMessage();
			if (msg == null)
			{
				msg = this.getUniquePosLoginMessage();
			}
		}

		if (msg == null)
		{
			msg = this.getEmptyTypeSelectionMessage();
		}

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<User> input)
	{
		return input.getAdapter(User.class) instanceof User;
	}

	private Section createLoginSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Schlüssel");
		section.setClient(this.fillLoginSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				UserEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createStatusSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Status");
		section.setClient(this.fillStatusSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				UserEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createUserSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Benutzerangaben");
		section.setClient(this.fillUserSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				UserEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillLoginSection(final Section parent)
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

		Label label = this.formToolkit.createLabel(composite, "Passwort", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.password = this.formToolkit.createText(composite, "");
		this.password.setLayoutData(layoutData);
		this.password.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				UserEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Login", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		final Text login = this.formToolkit.createText(composite, "");
		login.setLayoutData(layoutData);
		login.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				UserEditor.this.setDirty(true);
			}
		});
		this.posLogin = new FormattedText(login);

		this.posLogin.setFormatter(new NumberFormatter(User.POS_LOGIN_PATTERN));

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillStatusSection(final Section parent)
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

		Label label = this.formToolkit.createLabel(composite, "Profil", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		final CCombo combo = new CCombo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.FLAT);
		combo.setLayoutData(layoutData);
		combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		combo.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				UserEditor.this.setDirty(true);
			}
		});

		this.role = new ComboViewer(combo);
		this.role.setContentProvider(new RoleViewerContentProvider());
		this.role.setLabelProvider(new RoleViewerLabelProvider());

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final RoleQuery query = (RoleQuery) persistenceService.getServerService().getQuery(Role.class);
			final Role[] roles = query.selectAll(false).toArray(new Role[0]);
			this.role.setInput(roles);
		}

		final User user = (User) this.getEditorInput().getAdapter(User.class);
		if ((user.getId() != null) && user.getId().equals(Long.valueOf(1l)))
		{
			this.role.getCCombo().setEnabled(false);
		}

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		this.defaultUser = this.formToolkit.createButton(composite, "Standardbenutzer", SWT.CHECK);
		this.defaultUser.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				UserEditor.this.setDirty(true);
			}

			public void widgetSelected(final SelectionEvent event)
			{
				UserEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillUserSection(final Section parent)
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

		final Label label = this.formToolkit.createLabel(composite, "Benutzername", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.username = this.formToolkit.createText(composite, "");
		this.username.setLayoutData(layoutData);
		this.username.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				UserEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Message getEmptyPosLoginMessage()
	{
		Message msg = null;

		if (new Integer(this.posLogin.getValue().toString()).intValue() == 0)
		{
			msg = new Message(this.posLogin.getControl(), "Login", "Das Login für die Kasse darf nicht leer sein.");
		}
		return msg;
	}

	private Message getEmptyTypeSelectionMessage()
	{
		Message msg = null;

		final StructuredSelection ssel = (StructuredSelection) this.role.getSelection();
		if (ssel.isEmpty())
		{
			msg = new Message(this.role.getControl(), "Benutzerprofil", "Der Benutzer muss einem Benutzerprofil zugeordnet werden.");
		}
		return msg;
	}

	private Message getEmptyUsernameMessage()
	{
		Message msg = null;

		if (this.username.getText().isEmpty())
		{
			msg = new Message(this.username, "Benutzername", "Der Benutzername darf nicht leer sein.");
		}
		return msg;
	}

	private Message getUniquePosLoginMessage()
	{
		Message msg = null;

		final User user = (User) this.getEditorInput().getAdapter(User.class);

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final UserQuery query = (UserQuery) persistenceService.getServerService().getQuery(User.class);
			if (!query.isPosLoginUnique(new Integer(this.posLogin.getValue().toString()), user.getId()))
			{
				msg = new Message(this.username, "Fehler");
				msg.setMessage("Das gewählte Login wird bereits verwendet.");
			}
		}
		return msg;
	}

	private Message getUniqueUsernameMessage()
	{
		Message msg = null;

		final User user = (User) this.getEditorInput().getAdapter(User.class);
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final UserQuery query = (UserQuery) persistenceService.getServerService().getQuery(User.class);
			if (!query.isUsernameUnique(this.username.getText().toLowerCase(), user.getId()))
			{
				msg = new Message(this.username, "Fehler");
				msg.setMessage("Der gewählte Benutzername wird bereits verwendet. Wählen Sie einen anderen Benutzernamen.");
			}
		}
		return msg;
	}
}
