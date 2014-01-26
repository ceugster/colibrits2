package ch.eugster.colibri.provider.configuration;

import java.io.File;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.eugster.colibri.persistence.model.ProviderProperty;

public interface IProperty
{
	String defaultValue();
	
	Class<?> valueType();
	
	String key();
	
	String value();
	
	String[] filter();
	
	String control();
	
	String label();
	
	Properties controlProperties();
	
	Section section();
	
	String providerId();
	
	void setPersistedProperty(ProviderProperty persistedProperty);

	String label2();
	
	ProviderProperty getPersistedProperty();

	boolean isDefaultValue(String value);
	
	String value(IProperty property, org.eclipse.swt.widgets.Control control);
	
	void set(IProperty property, org.eclipse.swt.widgets.Control control, String value);
	
	/*
	 * Wichtig bei Buttons: wenn kein Wert angegeben ist, wird 0 und 1 als Range genommen (false und true) und es wird eine
	 * Checkbox gemacht.
	 * Bei mehr als 2 Werten werden Radiobuttons gemacht. Dann braucht label2 evtl für jede Radiobox einen Wert, alle getrennt durch |
	 */
	int[] validValues();
	
	org.eclipse.swt.widgets.Control createControl(Composite composite, FormToolkit formToolkit, IDirtyable dirtyable, int cols, int[] validValues);

	public interface Control
	{
		int columns(IProperty property);
		
		String controlName();
		
		boolean equal(Control control);
		
		String value(org.eclipse.swt.widgets.Control control);
		
		void value(org.eclipse.swt.widgets.Control control, String value);
		
		org.eclipse.swt.widgets.Control create(Composite composite, FormToolkit formToolkit, IProperty property, IDirtyable dirtyable, int cols, int[] validValues);
	}
	
	public enum AvailableControl implements Control
	{
		TEXT, FILE_DIALOG, BUTTON, SPINNER;
		
		public String controlName()
		{
			switch(this)
			{
			case TEXT:
			{
				return Text.class.getName();
			}
			case FILE_DIALOG:
			{
				return FileDialog.class.getName();
			}
			case BUTTON:
			{
				return Button.class.getName();
			}
			case SPINNER:
			{
				return Spinner.class.getName();
			}
			default:
			{
				throw new RuntimeException("Invalid control");
			}
			}
		}
		
		public boolean equal(Control control)
		{
			return control.getClass().getName().equals(controlName());
		}

		@Override
		public String value(org.eclipse.swt.widgets.Control control) 
		{
			switch(this)
			{
			case TEXT:
			{
				Text text = (Text) control;
				return text.getText();
			}
			case FILE_DIALOG:
			{
				Text text = (Text) control;
				return text.getText();
			}
			case BUTTON:
			{
				Composite composite = (Composite) control;
				return Integer.toString((Integer) composite.getData("value"));
			}
			case SPINNER:
			{
				Spinner spinner = (Spinner) control;
				return Integer.toString(spinner.getSelection());
			}
			default:
			{
				return null;
			}
			}
		}

		@Override
		public org.eclipse.swt.widgets.Control create(
				Composite parent, FormToolkit formToolkit, IProperty property, IDirtyable dirtyable, int cols, int[] validValues) 
		{
			switch(this)
			{
			case TEXT:
			{
				return createText(parent, formToolkit, property, dirtyable, cols);
			}
			case FILE_DIALOG:
			{
				return createFileDialog(parent, formToolkit, property, dirtyable, cols);
			}
			case BUTTON:
			{
				return createButton(parent, formToolkit, property, dirtyable, cols);
			}
			case SPINNER:
			{
				return createSpinner(parent, formToolkit, property, dirtyable, cols);
			}
			default:
			{
				return null;
			}
			}
		}

		private org.eclipse.swt.widgets.Control createText(Composite parent, FormToolkit formToolkit, IProperty property, final IDirtyable dirtyable, int cols)
		{
			Label label = formToolkit.createLabel(parent, property.label());
			label.setLayoutData(new GridData());

			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = cols - this.columns(property);
					
			final Text text = formToolkit.createText(parent, property.value());
			text.setData("property", property);
			text.setLayoutData(gridData);
			text.addModifyListener(new ModifyListener() 
			{
				@Override
				public void modifyText(ModifyEvent e) 
				{
					dirtyable.setDirty(true);
				}
			});
			text.addVerifyListener(new VerifyListener()
			{
				@Override
				public void verifyText(final VerifyEvent e)
				{
					if (e.getSource() instanceof Text)
					{
						final Text text = (Text) e.getSource();
						IProperty property = (IProperty) text.getData("property");
						if (property instanceof IProperty)
						{
							if (property.valueType().equals(Long.class) || property.valueType().equals(Integer.class))
							{
								if ("0123456789".indexOf(e.text) == -1)
								{
									e.doit = false;
								}
							}
						}
					}
				}
			});
			createLabel2(parent, formToolkit, property);
			return text;
		}
	
		private org.eclipse.swt.widgets.Control createFileDialog(final Composite parent, FormToolkit formToolkit, IProperty property, final IDirtyable dirtyable, int cols)
		{
			Label label = formToolkit.createLabel(parent, property.label());
			label.setLayoutData(new GridData());
	
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = cols - this.columns(property) - 1;
					
			final Text text = formToolkit.createText(parent, property.label2());
			text.setData("property", property);
			text.setLayoutData(gridData);
			text.addModifyListener(new ModifyListener() 
			{
				@Override
				public void modifyText(ModifyEvent e) 
				{
					dirtyable.setDirty(true);
				}
			});

			final Button button = formToolkit.createButton(parent, "...", SWT.PUSH);
			button.setLayoutData(new GridData());
			button.setData("key", property.key());
			button.setData("filter", property.filter());
			button.setData("name", property.label());
			button.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetDefaultSelected(final SelectionEvent event)
				{
					this.widgetSelected(event);
				}
	
				@Override
				public void widgetSelected(final SelectionEvent event)
				{
					final FileDialog dialog = new FileDialog(parent.getShell());
					final Button button = (Button) event.getSource();
					String oldValue = text.getText();
					dialog.setFileName(oldValue);
					final String[] filter = (String[]) button.getData("filter");
					dialog.setFilterExtensions(filter);
					dialog.setFilterIndex(0);
					final String name = (String) button.getData("name");
					dialog.setText(name);
					dialog.setFilterPath(null);
					final String newValue = dialog.open();
					if (!newValue.equals(oldValue))
					{
						text.setData("value", newValue);
						text.setText(newValue);
						dirtyable.setDirty(true);
					}
				}
			});
			createLabel2(parent, formToolkit, property);
			return text;
		}

//		private org.eclipse.swt.widgets.Control createButton(final Composite parent, FormToolkit formToolkit, IProperty property, final IDirtyable dirtyable, int cols)
//		{
//			Label label = formToolkit.createLabel(parent, property.label());
//			label.setLayoutData(new GridData());
//	
//			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//			gridData.horizontalSpan = cols - this.columns(property);
//			
//			final Button button = formToolkit.createButton(parent, property.label2(), SWT.CHECK);
//			button.setLayoutData(gridData);
//			button.setData("key", property.key());
//			button.addSelectionListener(new SelectionListener()
//			{
//				@Override
//				public void widgetDefaultSelected(final SelectionEvent event)
//				{
//					this.widgetSelected(event);
//				}
//
//				@Override
//				public void widgetSelected(final SelectionEvent event)
//				{
//					final Button button = (Button) event.getSource();
//					button.setData("value", Boolean.toString(button.getSelection()));
//					dirtyable.setDirty(true);
//				}
//			});
//			return button;
//		}

		private org.eclipse.swt.widgets.Control createButton(final Composite parent, FormToolkit formToolkit, IProperty property, final IDirtyable dirtyable, int cols)
		{
			Label label = formToolkit.createLabel(parent, property.label());
			label.setLayoutData(new GridData());
			
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = cols - this.columns(property);
			
			final Composite composite = formToolkit.createComposite(parent);
			composite.setLayout(new GridLayout());
			composite.setLayoutData(gridData);
			
			String[] labels = null;
			int[] values = property.validValues();
			if (values.length <= 2)
			{
				/*
				 * Checkbox
				 */
				labels = new String[] { property.label2() };
				if (values.length < 2)
				{
					values = new int[] { 0, 1 };
				}
				final Button button = formToolkit.createButton(composite, labels[0], SWT.CHECK);
				button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				button.setData("value", Integer.valueOf(values[0]));
				button.addSelectionListener(new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(final SelectionEvent event)
					{
						this.widgetSelected(event);
					}

					@Override
					public void widgetSelected(final SelectionEvent event)
					{
						final Button button = (Button) event.getSource();
						button.setData("value", Integer.valueOf(button.getSelection() ? 1 : 0));
						composite.setData("value", (Integer) button.getData("value"));
						dirtyable.setDirty(true);
					}
				});
			}
			else
			{
				/*
				 * Radiobuttons
				 */
				labels = property.label2().split("[|]");
				
				if (values.length == labels.length)
				{
					for (int i = 0; i < values.length; i++)
					{
						final Button button = formToolkit.createButton(composite, labels[i], SWT.RADIO);
						button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
						button.setData("value", Integer.valueOf(values[i]));
						button.addSelectionListener(new SelectionListener()
						{
							@Override
							public void widgetDefaultSelected(final SelectionEvent event)
							{
								this.widgetSelected(event);
							}

							@Override
							public void widgetSelected(final SelectionEvent event)
							{
								final Button button = (Button) event.getSource();
								composite.setData("value", (Integer) button.getData("value"));
								dirtyable.setDirty(true);
							}
						});
					}
				}
			}
			return composite;
		}

		private org.eclipse.swt.widgets.Control createSpinner(final Composite parent, FormToolkit formToolkit, IProperty property, final IDirtyable dirtyable, int cols)
		{
			Label label = formToolkit.createLabel(parent, property.label());
			label.setLayoutData(new GridData());
	
			GridData gridData = new GridData();
			gridData.horizontalSpan = cols - this.columns(property);
					
			String key = SpinnerPropertyKey.WIDTH.key();
			String value = property.controlProperties().getProperty(key);
			gridData.widthHint = Integer.valueOf(value);

			final Spinner spinner = new Spinner(parent, SWT.WRAP);
			spinner.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
			spinner.setLayoutData(gridData);
			spinner.setSelection(getInt(property.controlProperties().getProperty(SpinnerPropertyKey.SELECTION.key())));
			spinner.setMinimum(getInt(property.controlProperties().getProperty(SpinnerPropertyKey.MINIMUM.key())));
			spinner.setMaximum(getInt(property.controlProperties().getProperty(SpinnerPropertyKey.MAXIMUM.key())));
			spinner.setDigits(getInt(property.controlProperties().getProperty(SpinnerPropertyKey.DIGITS.key())));
			spinner.setIncrement(getInt(property.controlProperties().getProperty(SpinnerPropertyKey.INCREMENT.key())));
			spinner.setPageIncrement(getInt(property.controlProperties().getProperty(SpinnerPropertyKey.PAGE_INCREMENT.key())));
			spinner.setMinimum(getInt(property.controlProperties().getProperty(SpinnerPropertyKey.MINIMUM.key())));
			spinner.setData("key", property.key());
			formToolkit.adapt(spinner);
			spinner.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetDefaultSelected(final SelectionEvent event)
				{
					this.widgetSelected(event);
				}

				@Override
				public void widgetSelected(final SelectionEvent event)
				{
					final Spinner spinner = (Spinner) event.getSource();
					spinner.setData("value", Integer.toString(spinner.getSelection()));
					dirtyable.setDirty(true);
				}
			});
			createLabel2(parent, formToolkit, property);
			return spinner;
		}

		private void createLabel2(Composite composite, FormToolkit formToolkit, IProperty property)
		{
			if (!property.label2().isEmpty())
			{
				GridData gridData = new GridData();
				Label label = formToolkit.createLabel(composite, property.label2());
				label.setLayoutData(gridData);
			}
		}
		
		private int getInt(final String value)
		{
			try
			{
				return Integer.valueOf(value).intValue();
			}
			catch (final NumberFormatException e)
			{
				return 0;
			}
		}

		@Override
		public int columns(IProperty property) 
		{
			switch(this)
			{
			case TEXT:
			{
				return property.label2().isEmpty() ? 1 : 2;
			}
			case FILE_DIALOG:
			{
				return property.label2().isEmpty() ? 3 : 4;
			}
			case BUTTON:
			{
				return 1;
			}
			default:
			{
				return property.label2().isEmpty() ? 2 : 3;
			}
			}
		}

		@Override
		public void value(org.eclipse.swt.widgets.Control control, String value) 
		{
			switch(this)
			{
			case TEXT:
			{
				Text text = (Text) control;
				text.setText(value);
				break;
			}
			case FILE_DIALOG:
			{
				Text text = (Text) control;
				File path = new File(value);
				text.setText(path.getAbsolutePath());
				break;
			}
			case BUTTON:
			{
				int val = getInt(value);
				Composite composite = (Composite) control;
				org.eclipse.swt.widgets.Control[] children = composite.getChildren();
				for (org.eclipse.swt.widgets.Control child : children)
				{
					if (child instanceof Button)
					{
						Button button = (Button) child;
						Integer i = (Integer) button.getData("value");
						if (i.intValue() == val)
						{
							button.setSelection(true);
							composite.setData("value", i);
						}
					}
				}
				break;
			}
			case SPINNER:
			{
				Spinner spinner = (Spinner) control;
				spinner.setSelection(getInt(value));
				break;
			}
			}
		}

	}

	public interface IPropertyKey
	{
		String key();
	}

	public enum NoPropertyKey implements IPropertyKey
	{
		;
		public String key()
		{
			return "";
		}
	}

	public enum SpinnerPropertyKey implements IPropertyKey
	{
		SELECTION, MINIMUM, MAXIMUM, DIGITS, INCREMENT, PAGE_INCREMENT, WIDTH;
		
		public String key()
		{
			switch(this)
			{
			case SELECTION:
			{
				return "selection";
			}
			case MINIMUM:
			{
				return "minimum";
			}
			case MAXIMUM:
			{
				return "maximum";
			}
			case DIGITS:
			{
				return "digits";
			}
			case INCREMENT:
			{
				return "increment";
			}
			case PAGE_INCREMENT:
			{
				return "pageIncrement";
			}
			case WIDTH:
			{
				return "width";
			}
			default:
			{
				throw new RuntimeException("Invalid key");
			}
			}
		}
	}
	
	public interface Section
	{
		String title();
		
		int columns();
		
		IProperty[] properties();
	}
}
