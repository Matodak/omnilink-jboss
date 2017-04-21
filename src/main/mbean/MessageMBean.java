package mbean;

public interface MessageMBean extends NumberedComponentMBean
{
    // Display status: r/o
    boolean isDisplayed();
    
    // Commands
	void show() throws Exception;
	void play(int dialPhoneNumber) throws Exception;
	void log() throws Exception;
	void clear() throws Exception;
}