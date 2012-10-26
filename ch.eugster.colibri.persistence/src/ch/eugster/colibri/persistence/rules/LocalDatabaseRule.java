package ch.eugster.colibri.persistence.rules;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class LocalDatabaseRule implements ISchedulingRule
{
	private static LocalDatabaseRule rule;
	
	private LocalDatabaseRule()
	{
		
	}
	
	@Override
	public boolean contains(ISchedulingRule rule)
	{
		return isConflicting(rule);
	}

	@Override
	public boolean isConflicting(ISchedulingRule rule)
	{
		return rule == this;
	}

	public static LocalDatabaseRule getRule()
	{
		if (rule == null)
		{
			rule = new LocalDatabaseRule();
		}
		return rule;
	}
}
