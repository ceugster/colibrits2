package ch.eugster.colibri.client.ui.panels;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;

public class ColibriFocusTraversalPolicy extends FocusTraversalPolicy
{

	@Override
	public Component getComponentAfter(Container container, Component component)
	{
		Component next = null;
		if (container instanceof UserPanel)
		{
			UserPanel panel = (UserPanel) container;
			next = panel.getNumericPadPanel().getEnterButton();
		}
		return next;
	}

	@Override
	public Component getComponentBefore(Container container, Component component)
	{
		Component next = null;
		if (container instanceof UserPanel)
		{
			UserPanel panel = (UserPanel) container;
			next = panel.getNumericPadPanel().getEnterButton();
		}
		return next;
	}

	@Override
	public Component getDefaultComponent(Container container)
	{
		Component next = null;
		if (container instanceof UserPanel)
		{
			UserPanel panel = (UserPanel) container;
			next = panel.getNumericPadPanel().getEnterButton();
		}
		return next;
	}

	@Override
	public Component getFirstComponent(Container container)
	{
		Component next = null;
		if (container instanceof UserPanel)
		{
			UserPanel panel = (UserPanel) container;
			next = panel.getNumericPadPanel().getEnterButton();
		}
		return next;
	}

	@Override
	public Component getLastComponent(Container container)
	{
		Component next = null;
		if (container instanceof UserPanel)
		{
			UserPanel panel = (UserPanel) container;
			next = panel.getNumericPadPanel().getEnterButton();
		}
		return next;
	}

}
