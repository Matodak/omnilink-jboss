package mbean;

import net.homeip.mleclerc.omnilink.message.MacroButtonCommand;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

public class Button extends NumberedComponentMBeanSupport implements ButtonMBean
{
    public Button(Integer number, String name)
    {
        super(number, name);
    }

    @Override
	public void execute() throws Exception {
		// Issue request
		RequestMessage request = new MacroButtonCommand(getNumber());
		Utils.sendMessage(server, request);
	}

	public void updateStatus()
    {
        // Nothing to do for now
    }
}