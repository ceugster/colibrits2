package ch.eugster.colibri.persistence.rules;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class ServerDatabaseRule implements ISchedulingRule
{
	private static ServerDatabaseRule rule;
	
	private ServerDatabaseRule()
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

	public static ServerDatabaseRule getRule()
	{
		if (rule == null)
		{
			rule = new ServerDatabaseRule();
		}
		return rule;
	}
}
