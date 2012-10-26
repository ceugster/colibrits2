package ch.eugster.colibri.admin.layout.printer.editors;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.print.service.PrintService;

public class PrintoutEditorInput extends AbstractEntityEditorInput<Printout>
{
	private final PrintService service;

	public PrintoutEditorInput(final PrintService service, final Printout printout)
	{
		super(printout);
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
		else if (PrintService.class.isAssignableFrom(adapter))
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
