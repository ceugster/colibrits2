/*
 * Created on 13.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.key.FunctionType;

public class KeyTransferable implements Transferable
{
	public static final DataFlavor PRODUCT_GROUP_FLAVOR = new DataFlavor(ProductGroup.class, "product-group");

	public static final DataFlavor PAYMENT_TYPE_FLAVOR = new DataFlavor(PaymentType.class, "payment-type");

	public static final DataFlavor TAX_RATE_FLAVOR = new DataFlavor(TaxRate.class, "tax-rate");

	public static final DataFlavor OPTION_FLAVOR = new DataFlavor(Position.Option.class, "option");

	public static final DataFlavor FUNCTION_FLAVOR = new DataFlavor(FunctionType.class, "function");

	private Object object;

	public KeyTransferable(final Object object)
	{
		this.object = object;
	}

	@Override
	public synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (flavor.equals(KeyTransferable.PRODUCT_GROUP_FLAVOR) || flavor.equals(KeyTransferable.PAYMENT_TYPE_FLAVOR)
				|| flavor.equals(KeyTransferable.FUNCTION_FLAVOR))
		{
			return object;
		}
		else
		{
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public synchronized DataFlavor[] getTransferDataFlavors()
	{
		return TabEditorButton.getSupportedFlavors();
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor flavor)
	{
		if (flavor.equals(KeyTransferable.PRODUCT_GROUP_FLAVOR) || flavor.equals(KeyTransferable.PAYMENT_TYPE_FLAVOR)
				|| flavor.equals(KeyTransferable.FUNCTION_FLAVOR))
		{
			return true;
		}

		return false;
	}

}
