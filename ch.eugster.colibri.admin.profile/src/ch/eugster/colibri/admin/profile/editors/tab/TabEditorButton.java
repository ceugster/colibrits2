/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;

import javax.swing.ImageIcon;

import ch.eugster.colibri.admin.profile.menus.DefaultPopupMenu;
import ch.eugster.colibri.admin.profile.menus.DrawerPopupMenu;
import ch.eugster.colibri.admin.profile.menus.KeyPopupMenu;
import ch.eugster.colibri.admin.profile.menus.NameablePopupMenu;
import ch.eugster.colibri.admin.profile.menus.PaymentTypePopupMenu;
import ch.eugster.colibri.admin.profile.menus.ProductGroupPopupMenu;
import ch.eugster.colibri.admin.profile.menus.RestitutionPopupMenu;
import ch.eugster.colibri.admin.profile.menus.SelectCustomerPopupMenu;
import ch.eugster.colibri.admin.profile.menus.StoreReceiptPopupMenu;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.ui.buttons.HTMLButton;

public class TabEditorButton extends HTMLButton
{
	public static final long serialVersionUID = 0l;

	protected boolean failOver;

	protected Key[] keys;

	private Point place;

	public TabEditorButton(final Key[] key, final boolean failOver)
	{
		super();
		setMinimumSize(new Dimension(30, 20));
		setPreferredSize(new Dimension(60, 40));
		this.failOver = failOver;
		this.keys = key;
		this.keys[1] = this.keys[0].copy();
		if (this.keys[1].isDeleted())
		{
			this.updateDeleted();
		}
		else
		{
			this.update(true, failOver);
		}
	}

	public TabEditorButton(final Point place)
	{
		super();
		this.update(place);
	}

	/*
	 * keys[0] ist der original Key, keys[1] ist die Arbeitskopie
	 */
	public Key[] getKeys()
	{
		return this.keys;
	}

	public Point getPlace()
	{
		if ((this.keys == null) || (this.keys[1] == null))
		{
			return this.place;
		}
		else
		{
			return new Point(this.keys[1].getTabRow(), this.keys[1].getTabCol());
		}
	}

	public void reset()
	{
		final Profile profile = this.keys[1].getTab().getConfigurable().getProfile();
		this.keys[1].setFailOverBg(profile.getButtonFailOverBg());
		this.keys[1].setFailOverFg(profile.getButtonFailOverFg());
		this.keys[1].setFailOverFontSize(profile.getButtonFailOverFontSize());
		this.keys[1].setFailOverFontStyle(profile.getButtonFailOverFontStyle());
		this.keys[1].setFailOverHorizontalAlign(profile.getButtonFailOverHorizontalAlign());
		this.keys[1].setFailOverVerticalAlign(profile.getButtonFailOverVerticalAlign());
		this.keys[1].setNormalBg(profile.getButtonNormalBg());
		this.keys[1].setNormalFg(profile.getButtonNormalFg());
		this.keys[1].setNormalFontSize(profile.getButtonNormalFontSize());
		this.keys[1].setNormalFontStyle(profile.getButtonNormalFontStyle());
		this.keys[1].setNormalHorizontalAlign(profile.getButtonNormalHorizontalAlign());
		this.keys[1].setNormalVerticalAlign(profile.getButtonNormalVerticalAlign());
	}

	public void setKeys(final Key[] key)
	{
		this.keys = key;
	}

	public void silentUpdate(final boolean failOver)
	{
		if ((this.keys != null) && (this.keys[1] != null))
		{
			this.update();
			if (failOver)
			{
				this.updateFailOver();
			}
			else
			{
				this.updateNormal();
			}
		}
	}

	public void update()
	{
		if (this.keys[1] != null)
		{
			if ((this.keys[1].getImageId() != null) && (this.keys[1].getImageId().length() > 0))
			{
				this.setIcon(new ImageIcon(this.keys[1].getImageId()));
			}
			this.setHorizontalTextPosition(this.keys[1].getTextImageHorizontalPosition());
			this.setVerticalTextPosition(this.keys[1].getTextImageVerticalPosition());
			this.setEnabled(true);
		}
	}

	@Override
	public void update(final boolean failOver)
	{
		this.silentUpdate(failOver);
		this.firePropertyChange("dirty", null, null);
	}

	public void update(final boolean silent, final boolean failOver)
	{
		if (silent)
		{
			this.silentUpdate(failOver);
		}
		else
		{
			this.update(failOver);
		}
	}

	public void update(final Point place)
	{
		this.place = place;
		this.updateDeleted();
		this.firePropertyChange("dirty", null, null);
	}

	private void updateDeleted()
	{
		this.setText("");
		this.setIcon(null);
		this.setBackground(Color.LIGHT_GRAY);
		this.setForeground(Color.LIGHT_GRAY);
		this.setEnabled(false);
		this.setVisible(true);
	}

	private void updateFailOver()
	{
		this.setText(this.createLabel(this.keys[1].getLabel(), true));
		this.keys[1].setLabel(this.getText());
		this.setFont(this.getFont().deriveFont(this.keys[1].getFailOverFontStyle(), this.keys[1].getFailOverFontSize()));
		this.setBackground(new Color(this.keys[1].getFailOverBg()));
		this.setForeground(new Color(this.keys[1].getFailOverFg()));
		this.setHorizontalAlignment(this.keys[1].getFailOverHorizontalAlign());
		this.setVerticalAlignment(this.keys[1].getFailOverVerticalAlign());
	}

	private void updateNormal()
	{
		this.setText(this.createLabel(this.keys[1].getLabel(), false));
		this.keys[1].setLabel(this.getText());
		this.setFont(this.getFont().deriveFont(this.keys[1].getNormalFontStyle(), this.keys[1].getNormalFontSize()));
		this.setBackground(new Color(this.keys[1].getNormalBg()));
		this.setForeground(new Color(this.keys[1].getNormalFg()));
		this.setHorizontalAlignment(this.keys[1].getNormalHorizontalAlign());
		this.setVerticalAlignment(this.keys[1].getNormalVerticalAlign());
	}

	public static DataFlavor getJavaDataFlavor(final KeyType keyType)
	{
		if (keyType.equals(KeyType.PRODUCT_GROUP))
		{
			return KeyTransferable.PRODUCT_GROUP_FLAVOR;
		}
		if (keyType.equals(KeyType.PAYMENT_TYPE))
		{
			return KeyTransferable.PAYMENT_TYPE_FLAVOR;
		}
		if (keyType.equals(KeyType.TAX_RATE))
		{
			return KeyTransferable.TAX_RATE_FLAVOR;
		}
		if (keyType.equals(KeyType.OPTION))
		{
			return KeyTransferable.OPTION_FLAVOR;
		}
		if (keyType.equals(KeyType.FUNCTION))
		{
			return KeyTransferable.FUNCTION_FLAVOR;
		}
		throw new RuntimeException("No such java data flavor");
	}

	public static int getKeyTransferTypeId(final KeyType keyType)
	{
		if (keyType.equals(KeyType.PRODUCT_GROUP))
		{
			return KeyTransfer.PRODUCT_GROUP_TYPE_ID;
		}
		if (keyType.equals(KeyType.PAYMENT_TYPE))
		{
			return KeyTransfer.PAYMENT_TYPE_TYPE_ID;
		}
		if (keyType.equals(KeyType.TAX_RATE))
		{
			return KeyTransfer.TAX_RATE_TYPE_ID;
		}
		if (keyType.equals(KeyType.OPTION))
		{
			return KeyTransfer.OPTION_TYPE_ID;
		}
		if (keyType.equals(KeyType.FUNCTION))
		{
			return KeyTransfer.FUNCTION_TYPE_ID;
		}
		throw new RuntimeException("No such key transfer type name");
	}

	public static int[] getKeyTransferTypeIds(final KeyType keyType)
	{
		final int[] transferTypeIds = new int[KeyType.values().length];
		final KeyType[] keyTypes = KeyType.values();
		for (int i = 0; i < keyTypes.length; i++)
		{
			transferTypeIds[i] = TabEditorButton.getKeyTransferTypeId(keyTypes[i]);
		}
		return transferTypeIds;
	}

	public static String getKeyTransferTypeName(final KeyType keyType)
	{
		if (keyType.equals(KeyType.PRODUCT_GROUP))
		{
			return KeyTransfer.PRODUCT_GROUP_TYPE_NAME;
		}
		if (keyType.equals(KeyType.PAYMENT_TYPE))
		{
			return KeyTransfer.PAYMENT_TYPE_TYPE_NAME;
		}
		if (keyType.equals(KeyType.TAX_RATE))
		{
			return KeyTransfer.TAX_RATE_TYPE_NAME;
		}
		if (keyType.equals(KeyType.OPTION))
		{
			return KeyTransfer.OPTION_TYPE_NAME;
		}
		if (keyType.equals(KeyType.FUNCTION))
		{
			return KeyTransfer.FUNCTION_TYPE_NAME;
		}
		throw new RuntimeException("No such key transfer type name");
	}

	public static String[] getKeyTransferTypeNames()
	{
		final String[] transferTypeNames = new String[KeyType.values().length];
		final KeyType[] keyTypes = KeyType.values();
		for (int i = 0; i < keyTypes.length; i++)
		{
			transferTypeNames[i] = TabEditorButton.getKeyTransferTypeName(keyTypes[i]);
		}
		return transferTypeNames;
	}

	public static Class<? extends KeyPopupMenu> getPopupMenuClass(final FunctionType functionType)
	{
		switch (functionType)
		{
			case FUNCTION_SELECT_CUSTOMER:
			{
				return SelectCustomerPopupMenu.class;
			}
//			case FUNCTION_STORE_RECEIPT:
//			{
//				return StoreReceiptPopupMenu.class;
//			}
			case FUNCTION_SHOW_CURRENT_RECEIPT_LIST:
			{
				return NameablePopupMenu.class;
			}
			case FUNCTION_SHOW_PARKED_RECEIPT_LIST:
			{
				return NameablePopupMenu.class;
			}
			case FUNCTION_SHOW_COIN_COUNTER_PANEL:
			{
				return NameablePopupMenu.class;
			}
			case FUNCTION_RESTITUTION:
			{
				return RestitutionPopupMenu.class;
			}
			case FUNCTION_TOTAL_SALES:
			{
				return NameablePopupMenu.class;
			}
			case FUNCTION_SALESPOINT_SALES:
			{
				return NameablePopupMenu.class;
			}
			case FUNCTION_USER_SALES:
			{
				return NameablePopupMenu.class;
			}
			case FUNCTION_OPEN_DRAWER:
			{
				return DrawerPopupMenu.class;
			}
			case FUNCTION_STORE_RECEIPT_EXPRESS_ACTION:
			{
				return StoreReceiptPopupMenu.class;
			}
			case FUNCTION_STORE_RECEIPT_SHORTHAND_ACTION:
			{
				return StoreReceiptPopupMenu.class;
			}
			default:
				return DefaultPopupMenu.class;
		}
	}

	public static String getPopupMenuClassName(final KeyType keyType)
	{
		switch (keyType)
		{
			case PRODUCT_GROUP:
			{
				return ProductGroupPopupMenu.class.getName();
			}
			case PAYMENT_TYPE:
			{
				return PaymentTypePopupMenu.class.getName();
			}
			case TAX_RATE:
			{
				return NameablePopupMenu.class.getName();
			}
			case OPTION:
			{
				return NameablePopupMenu.class.getName();
			}
			case FUNCTION:
			{
				return null;
			}
			default:
			{
				throw new RuntimeException(
						"No such menu class, try to get the menu class by using FunctionType.getPopupMenuClassName()");
			}
		}
	}

	public static DataFlavor[] getSupportedFlavors()
	{
		final DataFlavor[] dataFlavors = new DataFlavor[KeyType.values().length];
		final KeyType[] keyTypes = KeyType.values();
		for (int i = 0; i < keyTypes.length; i++)
		{
			dataFlavors[i] = TabEditorButton.getJavaDataFlavor(keyTypes[i]);
		}
		return dataFlavors;
	}
}
