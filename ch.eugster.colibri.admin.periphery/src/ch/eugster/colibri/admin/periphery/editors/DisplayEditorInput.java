package ch.eugster.colibri.admin.periphery.editors;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.display.service.DisplayService;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Display;

public class DisplayEditorInput extends AbstractEntityEditorInput<Display>
{
	private final DisplayService service;

	public DisplayEditorInput(final DisplayService service, final Display display)
	{
		super(display);
		this.service = service;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter)
	{
		if (adapter.equals(this.entity.getClass()))
		{
			return this.entity;
		}
		else if (DisplayService.class.isAssignableFrom(adapter))
		{
			return this.service;
		}
		return null;
	}

	@Override
	public String getName()
	{
		final Object name = this.service.getContext().getProperties().get("custom.label");
		if (name instanceof String)
		{
			return (String) name;
		}
		else
		{
			return "???";
		}
	}

	@Override
	public AbstractEntity getParent()
	{
		return this.entity.getParent();
	}

	@Override
	public String getToolTipText()
	{
		final Object name = this.service.getContext().getProperties().get("custom.label");
		if (name instanceof String)
		{
			return (String) name;
		}
		else
		{
			return "???";
		}
	}

	@Override
	public boolean hasParent()
	{
		return this.entity.hasParent();
	}

}
