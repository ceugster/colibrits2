/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Convert;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "us_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "us_version")),
		@AttributeOverride(name = "update", column = @Column(name = "us_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "us_deleted")) })
@Table(name = "colibri_user")
public class User extends AbstractEntity implements IReplicatable
{
	@Transient
	public static final String POS_LOGIN_PATTERN = "#######0";

	@Transient
	private static User loginUser;

	@Id
	@Column(name = "us_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "us_id")
	@TableGenerator(name = "us_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "us_ro_id", referencedColumnName = "ro_id")
	private Role role;

	@Basic
	@Column(name = "us_username")
	private String username;

	@Basic
	@Column(name = "us_password")
	private String password;

	@Basic
	@Column(name = "us_pos_login")
	private Integer posLogin;

	@Basic
	@Column(name = "us_default_user")
	@Convert("booleanConverter")
	private boolean defaultUser;

	protected User()
	{
		super();
	}

	protected User(Role role)
	{
		super();
		this.setRole(role);
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IUser#getPassword()
	 */
	public String getPassword()
	{
		return this.valueOf(this.password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IUser#getPosLogin()
	 */
	public Integer getPosLogin()
	{
		return this.posLogin;
	}

	public Role getRole()
	{
		return this.role;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IUser#getUsername()
	 */
	public String getUsername()
	{
		return this.valueOf(this.username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IUser#isDefaultUser()
	 */
	public boolean isDefaultUser()
	{
		return this.defaultUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IUser#setDefaultUser(boolean)
	 */
	public void setDefaultUser(final boolean defaultUser)
	{
		this.propertyChangeSupport.firePropertyChange("defaultUser", this.defaultUser, this.defaultUser = defaultUser);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IUser#setPassword(java.lang.String)
	 */
	public void setPassword(final String password)
	{
		this.propertyChangeSupport.firePropertyChange("password", this.password, this.password = password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IUser#setPosLogin(java.lang.Integer)
	 */
	public void setPosLogin(final Integer posLogin)
	{
		this.propertyChangeSupport.firePropertyChange("posLogin", this.posLogin, this.posLogin = posLogin);
	}

	public void setRole(final Role role)
	{
		this.propertyChangeSupport.firePropertyChange("role", this.role, this.role = role);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IUser#setUsername(java.lang.String)
	 */
	public void setUsername(final String username)
	{
		this.propertyChangeSupport.firePropertyChange("username", this.username, this.username = username);
	}

	public static User getLoginUser()
	{
		return User.loginUser;
	}

	public static User newInstance()
	{
		final User user = (User) AbstractEntity.newInstance(new User());
		return user;
	}

	public static User newInstance(Role role)
	{
		final User user = (User) AbstractEntity.newInstance(new User(role));
		return user;
	}

	public static void setLoginUser(final User user)
	{
		User.loginUser = user;
	}
}
