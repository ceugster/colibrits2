package ch.eugster.colibri.admin.profile.editors.tab;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

import ch.eugster.colibri.persistence.model.key.KeyType;

public class KeyTransfer extends ByteArrayTransfer
{
	public static final String PRODUCT_GROUP_TYPE_NAME = "JAVA_DATAFLAVOR:" + KeyTransferable.PRODUCT_GROUP_FLAVOR.getMimeType();

	public static final int PRODUCT_GROUP_TYPE_ID = Transfer.registerType(KeyTransfer.PRODUCT_GROUP_TYPE_NAME);

	public static final String PAYMENT_TYPE_TYPE_NAME = "JAVA_DATAFLAVOR:" + KeyTransferable.PAYMENT_TYPE_FLAVOR.getMimeType();

	public static final int PAYMENT_TYPE_TYPE_ID = Transfer.registerType(KeyTransfer.PAYMENT_TYPE_TYPE_NAME);

	public static final String TAX_RATE_TYPE_NAME = "JAVA_DATAFLAVOR:" + KeyTransferable.TAX_RATE_FLAVOR.getMimeType();

	public static final int TAX_RATE_TYPE_ID = Transfer.registerType(KeyTransfer.TAX_RATE_TYPE_NAME);

	public static final String OPTION_TYPE_NAME = "JAVA_DATAFLAVOR:" + KeyTransferable.OPTION_FLAVOR.getMimeType();

	public static final int OPTION_TYPE_ID = Transfer.registerType(KeyTransfer.OPTION_TYPE_NAME);

	public static final String FUNCTION_TYPE_NAME = "JAVA_DATAFLAVOR:" + KeyTransferable.FUNCTION_FLAVOR.getMimeType();

	public static final int FUNCTION_TYPE_ID = Transfer.registerType(KeyTransfer.FUNCTION_TYPE_NAME);

	private static KeyTransfer instance = new KeyTransfer();

	private Object source;

	private TabEditor tabEditor;

	public Object getSource()
	{
		return source;
	}

	public TabEditor getTabEditor()
	{
		return tabEditor;
	}

	@Override
	public void javaToNative(final Object object, final TransferData transferData)
	{
		source = object;
	}

	@Override
	public Object nativeToJava(final TransferData transferData)
	{
		return source;
	}

	public void setSource(final Object source)
	{
		this.source = source;
	}

	public void setTabEditor(final TabEditor tabEditor)
	{
		this.tabEditor = tabEditor;
	}

	/**
	 * Returns the type id used to identify this transfer.
	 * 
	 * @return the type id used to identify this transfer.
	 */
	@Override
	protected int[] getTypeIds()
	{
		return new int[] { KeyTransfer.PRODUCT_GROUP_TYPE_ID, KeyTransfer.PAYMENT_TYPE_TYPE_ID, KeyTransfer.FUNCTION_TYPE_ID };
	}

	/**
	 * Returns the type name used to identify this transfer.
	 * 
	 * @return the type name used to identify this transfer.
	 */
	@Override
	protected String[] getTypeNames()
	{
		return new String[] { KeyTransfer.PRODUCT_GROUP_TYPE_NAME, KeyTransfer.PAYMENT_TYPE_TYPE_NAME, KeyTransfer.FUNCTION_TYPE_NAME };
	}

	@Override
	protected boolean validate(final Object object)
	{
		return KeyType.validate(object);
	}

	public static KeyTransfer getInstance()
	{
		return KeyTransfer.instance;
	}

}
