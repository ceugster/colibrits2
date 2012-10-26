/*
 * Created on 18.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.selection;

import ch.eugster.colibri.client.ui.panels.ConfigurablePanel;
import ch.eugster.colibri.client.ui.panels.IConfigurable;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Configurable;

public class PaymentTypePanel extends ConfigurablePanel implements IConfigurable
{
	public static final long serialVersionUID = 0l;

	private static final String ID = PaymentTypePanel.class.getName();

	public PaymentTypePanel(final UserPanel userPanel, final Configurable configurable)
	{
		super(userPanel, configurable);
	}

	public String getId()
	{
		return PaymentTypePanel.ID;
	}

	@Override
	public String getName()
	{
		return "Zahlungsarten";
	}
}
