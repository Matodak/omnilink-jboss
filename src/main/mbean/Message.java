package mbean;

import net.homeip.mleclerc.omnilink.message.ClearMessageCommand;
import net.homeip.mleclerc.omnilink.message.LogMessageCommand;
import net.homeip.mleclerc.omnilink.message.MessageStatusReport;
import net.homeip.mleclerc.omnilink.message.MessageStatusRequest;
import net.homeip.mleclerc.omnilink.message.PhoneCommand;
import net.homeip.mleclerc.omnilink.message.ShowMessageCommand;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

public class Message extends NumberedComponentMBeanSupport implements MessageMBean
{
    private boolean displayed;

    public Message(Integer number, String name)
    {
        super(number, name);
    }

    public boolean isDisplayed()
    {
        return displayed;
    }

    private void setDisplayed(boolean displayed)
    {
        if (displayed != this.displayed)
        {
            sendAttrChangeNotif("Displayed", Boolean.class.getName(), this.displayed, displayed);
            this.displayed = displayed;
        }
    }

	public void show() throws Exception {
		// Issue request
		RequestMessage request = new ShowMessageCommand(getNumber());
		Utils.sendMessage(server, request);
	}

	public void play(int dialPhoneNumber) throws Exception {
		// Issue request
		RequestMessage request = new PhoneCommand(dialPhoneNumber, getNumber());
		Utils.sendMessage(server, request);
	}
	
	public void log() throws Exception {
		// Issue request
		RequestMessage request = new LogMessageCommand(getNumber());
		Utils.sendMessage(server, request);
	}

	public void clear() throws Exception {
		// Issue request
		RequestMessage request = new ClearMessageCommand(getNumber());
		Utils.sendMessage(server, request);
	}

    public void updateStatus()
    {
        try
        {
            MessageStatusReport messageStatusReply = (MessageStatusReport)
                    sendMessage(new MessageStatusRequest());
            MessageStatusReport.MessageStatusInfo status = (MessageStatusReport.MessageStatusInfo) messageStatusReply.getInfo(getNumber());
            setDisplayed(status.isDisplayed());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
