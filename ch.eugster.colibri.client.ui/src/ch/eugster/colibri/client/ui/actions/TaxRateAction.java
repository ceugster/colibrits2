/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.PositionChangeMediator;
import ch.eugster.colibri.client.ui.panels.user.PositionWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.service.PersistenceService;

public final class TaxRateAction extends ConfigurableAction implements PropertyChangeListener, DisposeListener
{
	public static final long serialVersionUID = 0l;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	protected String[] positionPropertyNames = new String[] { PositionWrapper.KEY_PROPERTY_PRODUCT_GROUP };

	public TaxRateAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		new PositionChangeMediator(userPanel, this, this.positionPropertyNames);
		userPanel.addDisposeListener(this);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final Position position = this.userPanel.getPositionWrapper().getPosition();
		final ProductGroup productGroup = position.getProductGroup();
		CurrentTax currentTax = this.getCurrentTax(productGroup);
		if (currentTax == null)
		{
			currentTax = this.getCurrentTax(position.getCurrentTax());
		}

		this.userPanel.getPositionWrapper().getPosition().setCurrentTax(currentTax);
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	public TaxRate getTaxRate()
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			return (TaxRate) persistenceService.getCacheService().find(TaxRate.class, this.key.getParentId());
		}
		return null;
	}

	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource() instanceof PositionChangeMediator)
		{
			if (event.getPropertyName().equals(PositionWrapper.KEY_POSITION))
			{
				final Position position = (Position) event.getNewValue();
				if (position == null)
				{
					this.setEnabled(false);
				}
				else
				{
					this.setEnabled(position.getProductGroup() != null);
				}
			}
			else if (event.getPropertyName().equals("productGroup"))
			{
				this.setEnabled(event.getNewValue() != null);
			}
		}
		else if (event.getSource() instanceof Position)
		{
			if (event.getPropertyName().equals("position"))
			{
				final Position position = (Position) event.getNewValue();
				if (position == null)
				{
					this.setEnabled(false);
				}
				else
				{
					this.setEnabled(position.getProductGroup() != null);
				}
			}
			if (event.getPropertyName().equals("productGroup"))
			{
				this.setEnabled(event.getNewValue() != null);
			}
		}
	}

	private CurrentTax getCurrentTax(final CurrentTax currentTax)
	{
		if (currentTax != null)
		{
			final Tax defaultTax = currentTax.getTax();
			final TaxType taxType = defaultTax.getTaxType();
			final Collection<Tax> taxes = this.getTaxRate().getTaxes();
			for (final Tax tax : taxes)
			{
				if (tax.getTaxType().equals(taxType))
				{
					return tax.getCurrentTax();
				}
			}
		}
		return null;
	}

	private CurrentTax getCurrentTax(final ProductGroup productGroup)
	{
		if (productGroup != null)
		{
			final Tax defaultTax = productGroup.getDefaultTax();
			if (defaultTax != null)
			{
				final TaxType taxType = defaultTax.getTaxType();
				final Collection<Tax> taxes = this.getTaxRate().getTaxes();
				for (final Tax tax : taxes)
				{
					if (tax.getTaxType().equals(taxType))
					{
						return tax.getCurrentTax();
					}
				}
			}
		}
		return null;
	}
}
