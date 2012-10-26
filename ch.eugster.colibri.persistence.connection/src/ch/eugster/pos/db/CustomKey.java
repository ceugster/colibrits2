/*
 * Created on 20.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CustomKey extends Key
{

	public Double value = new Double(Table.DOUBLE_DEFAULT_ZERO);

	public String parentClassName = ""; //$NON-NLS-1$

	public Long parentId = null;

	public boolean setDefaultTab = false;

	private Long tabId = null;

	private Tab tab;

	// 10226
	public Long paymentTypeId;

	private PaymentType paymentType;

	// 10226
	/**
	 * 
	 */
	public CustomKey()
	{
		super();
	}

	public void copyId(final CustomKey key)
	{
		this.setId(key.getId());
	}

	public Tab getTab()
	{
		return this.tab;
	}

	public Long getTabId()
	{
		return this.tabId;
	}

	public void setPaymentType(final PaymentType paymentType)
	{
		this.paymentType = paymentType;
		if (paymentType != null)
		{
			this.paymentTypeId = this.paymentType.getId();
		}
	}

	// protected Action getPosAction(UserPanel context)
	// {
	// Action action = null;
	// Table table = null;
	//
	//		if (this.parentClassName == null || this.parentClassName.equals("")) //$NON-NLS-1$
	// {
	// action = this.createAction(context);
	//			action.putValue(Action.POS_KEY_CLASS_NAME, ""); //$NON-NLS-1$
	// }
	// else
	// {
	// table = this.getParentObject();
	// if (table == null)
	// {
	// return null;
	// }
	//
	// action = this.createAction(context);
	// if (action != null)
	// {
	// action.putParent(Action.POS_KEY_CLASS_NAME, this.parentClassName, table);
	// }
	// }
	// return action;
	// }

	public void setTab(final Tab tab)
	{
		this.tab = tab;
		this.tabId = tab.getId();
	}

}
