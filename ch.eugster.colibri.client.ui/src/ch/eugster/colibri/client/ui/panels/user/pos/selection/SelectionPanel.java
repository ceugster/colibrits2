/*
 * Created on 08.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.selection;

import java.awt.CardLayout;
import java.util.Collection;

import javax.swing.JPanel;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Profile;

public class SelectionPanel extends JPanel implements StateChangeListener
{
	public static final long serialVersionUID = 0l;

	private static final String PRODUCT_GROUP_PANEL = "product.group.panel";

	private static final String PAYMENT_TYPE_PANEL = "payment.type.panel";

	private final UserPanel userPanel;

	private ProductGroupPanel productGroupPanel;

	private PaymentTypePanel paymentTypePanel;

	private final CardLayout cardLayout;

	public SelectionPanel(final UserPanel userPanel, final Profile profile)
	{
		this.userPanel = userPanel;

		cardLayout = new CardLayout();
		setLayout(cardLayout);

		final Collection<Configurable> configurables = profile.getConfigurables();
		for (final Configurable configurable : configurables)
		{
			if (configurable.getType().equals(Configurable.ConfigurableType.PRODUCT_GROUP))
			{
				productGroupPanel = new ProductGroupPanel(this.userPanel, configurable);
				this.userPanel.addStateChangeListener(productGroupPanel);
				this.add(SelectionPanel.PRODUCT_GROUP_PANEL, productGroupPanel);
			}
			else if (configurable.getType().equals(Configurable.ConfigurableType.PAYMENT_TYPE))
			{
				paymentTypePanel = new PaymentTypePanel(this.userPanel, configurable);
				this.userPanel.addStateChangeListener(paymentTypePanel);
				this.add(SelectionPanel.PAYMENT_TYPE_PANEL, paymentTypePanel);
			}
		}
	}

	public PaymentTypePanel getPaymentTypePanel()
	{
		return paymentTypePanel;
	}

	public ProductGroupPanel getProductGroupPanel()
	{
		return productGroupPanel;
	}

	public void stateChange(final StateChangeEvent event)
	{
		if (event.getNewState() != null)
		{
			if (event.getNewState().equals(UserPanel.State.POSITION_INPUT))
			{
				cardLayout.show(this, SelectionPanel.PRODUCT_GROUP_PANEL);
			}
			else if (event.getNewState().equals(UserPanel.State.PAYMENT_INPUT))
			{
				cardLayout.show(this, SelectionPanel.PAYMENT_TYPE_PANEL);
			}
		}
	}
}
