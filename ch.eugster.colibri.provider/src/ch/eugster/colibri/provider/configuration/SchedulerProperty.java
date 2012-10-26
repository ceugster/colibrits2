package ch.eugster.colibri.provider.configuration;

import org.eclipse.swt.widgets.Text;

public enum SchedulerProperty implements IProperty
{
	SCHEDULER_DELAY, SCHEDULER_PERIOD, SCHEDULER_RECEIPT_COUNT;

	public String control()
	{
		if (this.equals(SCHEDULER_DELAY))
		{
			return Text.class.getName();
		}
		else if (this.equals(SCHEDULER_PERIOD))
		{
			return Text.class.getName();
		}
		else if (this.equals(SCHEDULER_RECEIPT_COUNT))
		{
			return Text.class.getName();
		}
		else
		{
			throw new RuntimeException("Invalid key");
		}
	}

	public boolean isPath()
	{
		return false;
	}
	
	public String[] filter()
	{
		if (this.equals(SCHEDULER_DELAY))
		{
			return null;
		}
		else if (this.equals(SCHEDULER_PERIOD))
		{
			return null;
		}
		else if (this.equals(SCHEDULER_RECEIPT_COUNT))
		{
			return null;
		}
		else
		{
			throw new RuntimeException("Invalid key");
		}
	}

	public String key()
	{
		if (this.equals(SCHEDULER_DELAY))
		{
			return "scheduler.delay";
		}
		else if (this.equals(SCHEDULER_PERIOD))
		{
			return "scheduler.period";
		}
		else if (this.equals(SCHEDULER_RECEIPT_COUNT))
		{
			return "scheduler.receipt.count";
		}
		else
		{
			throw new RuntimeException("Invalid key");
		}
	}

	public String label()
	{
		if (this.equals(SCHEDULER_DELAY))
		{
			return "Startverzögerung (in Millisekunden)";
		}
		else if (this.equals(SCHEDULER_PERIOD))
		{
			return "Periodische Wiederholung (in Millisekunden)";
		}
		else if (this.equals(SCHEDULER_RECEIPT_COUNT))
		{
			return "Zu übertragende Belege pro Lauf";
		}
		else
		{
			throw new RuntimeException("Invalid key");
		}
	}

	public String value()
	{
		if (this.equals(SCHEDULER_DELAY))
		{
			return "30000";
		}
		else if (this.equals(SCHEDULER_PERIOD))
		{
			return "15000";
		}
		else if (this.equals(SCHEDULER_RECEIPT_COUNT))
		{
			return "5";
		}
		else
		{
			throw new RuntimeException("Invalid key");
		}
	}
}
