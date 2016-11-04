package ch.eugster.colibri.provider.configuration;

public interface IFailoverPerceptible 
{
	void setCurrentlyFailoverMode(boolean currentlyFailoverMode);
	
	boolean isCurrentlyFailoverMode();
}
