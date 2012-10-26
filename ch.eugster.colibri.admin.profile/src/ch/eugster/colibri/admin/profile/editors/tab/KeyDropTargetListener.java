/*
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.HashMap;
import java.util.Map;

import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;

public class KeyDropTargetListener implements DropTargetListener
{
	private TabEditorButton target;

	public KeyDropTargetListener(final TabEditorButton target)
	{
		this.target = target;
	}

	@Override
	public void dragEnter(final DropTargetDragEvent dtde)
	{
		final int a = dtde.getDropAction();
		if ((a & DnDConstants.ACTION_COPY) != 0)
		{
			System.out.println();
		}

		if (!this.isDragAcceptable(dtde))
		{
			dtde.rejectDrag();
		}
	}

	@Override
	public void dragExit(final DropTargetEvent dte)
	{
	}

	@Override
	public void dragOver(final DropTargetDragEvent dtde)
	{
	}

	@Override
	public void drop(final DropTargetDropEvent dtde)
	{
		if (!this.isDropAcceptable(dtde))
		{
			dtde.rejectDrop();
			return;
		}

		dtde.acceptDrop(DnDConstants.ACTION_COPY);

		final Object source = KeyTransfer.getInstance().getSource();
		final TabEditor editor = KeyTransfer.getInstance().getTabEditor();
		Key[] keys = this.target.getKeys();
		if (keys == null)
		{
			keys = new Key[2];
		}

		if (keys[1] == null)
		{
			keys[1] = Key.newInstance(editor.getTab());
			keys[1].setTabRow(this.target.getPlace().x);
			keys[1].setTabCol(this.target.getPlace().y);
		}

		final Profile profile = keys[1].getTab().getConfigurable().getProfile();
		keys[1].setDeleted(false);
		keys[1].setFailOverBg(profile.getButtonFailOverBg());
		keys[1].setFailOverFg(profile.getButtonFailOverFg());
		keys[1].setFailOverFontSize(profile.getButtonFailOverFontSize());
		keys[1].setFailOverFontStyle(profile.getButtonFailOverFontStyle());
		keys[1].setFailOverHorizontalAlign(profile.getButtonFailOverHorizontalAlign());
		keys[1].setFailOverVerticalAlign(profile.getButtonFailOverVerticalAlign());
		keys[1].setNormalBg(profile.getButtonNormalBg());
		keys[1].setNormalFg(profile.getButtonNormalFg());
		keys[1].setNormalFontSize(profile.getButtonNormalFontSize());
		keys[1].setNormalFontStyle(profile.getButtonNormalFontStyle());
		keys[1].setNormalHorizontalAlign(profile.getButtonNormalHorizontalAlign());
		keys[1].setNormalVerticalAlign(profile.getButtonNormalVerticalAlign());

		if (source instanceof ProductGroup)
		{
			final ProductGroup productGroup = (ProductGroup) source;
			keys[1].setKeyType(KeyType.PRODUCT_GROUP);
			keys[1].setParentId(productGroup.getId());
			keys[1].setLabel(productGroup.getCode());
			this.target.setKeys(keys);
		}
		else if (source instanceof PaymentType)
		{
			final PaymentType paymentType = (PaymentType) source;
			keys[1].setKeyType(KeyType.PAYMENT_TYPE);
			keys[1].setParentId(paymentType.getId());
			keys[1].setLabel(paymentType.getCode());
			this.target.setKeys(keys);
		}
		else if (source instanceof TaxRate)
		{
			final TaxRate taxRate = (TaxRate) source;
			keys[1].setKeyType(KeyType.TAX_RATE);
			keys[1].setParentId(taxRate.getId());
			keys[1].setLabel(taxRate.getName());
			this.target.setKeys(keys);
		}
		else if (source instanceof Position.Option)
		{
			final Option option = (Option) source;
			keys[1].setKeyType(KeyType.OPTION);
			keys[1].setParentId(new Long(option.ordinal()));
			keys[1].setLabel(option.toString());
			this.target.setKeys(keys);
		}
		else if (source instanceof FunctionType)
		{
			final FunctionType functionType = (FunctionType) source;
			keys[1].setKeyType(KeyType.FUNCTION);
			keys[1].setFunctionType(functionType);
			if (functionType.equals(FunctionType.FUNCTION_STORE_RECEIPT) || functionType.equals(FunctionType.FUNCTION_STORE_RECEIPT_EXPRESS_ACTION)
					|| functionType.equals(FunctionType.FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION))
			{
				keys[1].setParentId(new Long(1l));
			}
			if (functionType.equals(FunctionType.FUNCTION_OPEN_DRAWER))
			{
				keys[1].setParentId(new Long(1l));
			}
			keys[1].setLabel(functionType.toCode());
			this.target.setKeys(keys);
		}

		if (this.target.getComponentPopupMenu() != null)
		{
			this.target.remove(this.target.getComponentPopupMenu());
		}

		this.target.add(editor.createPopupMenu(this.target, editor));
		this.target.update(editor.getFailOverState());

		final Map<Integer, HashMap<Integer, Key[]>> keyMap = editor.getKeyMap();
		HashMap<Integer, Key[]> cols = keyMap.get(new Integer(keys[1].getTabRow()));
		if (cols == null)
		{
			cols = new HashMap<Integer, Key[]>();
		}
		cols.put(new Integer(keys[1].getTabCol()), keys);
		keyMap.put(new Integer(keys[1].getTabRow()), cols);

		dtde.dropComplete(true);
	}

	@Override
	public void dropActionChanged(final DropTargetDragEvent dtde)
	{
		if (!this.isDragAcceptable(dtde))
		{
			dtde.rejectDrag();
		}
	}

	public boolean isDragAcceptable(final DropTargetDragEvent dtde)
	{
		return (dtde.getDropAction() & DnDConstants.ACTION_COPY) != 0;
	}

	public boolean isDropAcceptable(final DropTargetDropEvent dtde)
	{
		if ((dtde.getDropAction() & DnDConstants.ACTION_COPY) == 0)
		{
			return false;
		}
		return true;
	}

}
