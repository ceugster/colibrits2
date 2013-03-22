/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public class OptionAction extends ConfigurableAction
{
	public static final long serialVersionUID = 0l;

	protected Position.Option option;

	public OptionAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		this.option = Position.Option.values()[new Long(key.getParentId()).intValue()];
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		this.userPanel.getPositionWrapper().getPosition().setOption(this.option);
	}

	public Option getOption()
	{
		return this.option;
	}

	@Override
	protected boolean getState(final StateChangeEvent event)
	{
		boolean enabled = super.getState(event);
		if (enabled)
		{
			final ProductGroup productGroup = this.userPanel.getPositionWrapper().getPosition().getProductGroup();
			if (productGroup == null)
			{
				return true;
			}
			else
			{
				final ProductGroupType productGroupType = productGroup.getProductGroupType();
				if (productGroup.equals(productGroup.getCommonSettings().getPayedInvoice()))
				{
					enabled = false;
				}

				final Option[] options = productGroupType.getOptions();
				for (final Option option : options)
				{
					if (this.option.equals(option))
					{
						enabled = true;
					}
				}
			}
		}
		return enabled;
	}
}
