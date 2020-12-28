package mbean;

import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import net.homeip.mleclerc.omnilink.CommunicationException;
import net.homeip.mleclerc.omnilink.messagebase.ReplyMessage;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

public class Utils
{
    public static ReplyMessage sendMessage(MBeanServer server, RequestMessage request) throws Exception
    {
        ReplyMessage reply = null;

        ObjectName name = new ObjectName(CommunicationMBean.NAME);

        // Send the message only if the communication channel is open
        Boolean isOpen = (Boolean) server.getAttribute(name, "Open");
        if (isOpen.booleanValue())
        {
            Object[] params = { request };
            String[] sig = { RequestMessage.class.getName() };
            try
            {
            	reply = (ReplyMessage) server.invoke(name, "sendMessage", params, sig);
            }
            catch(MBeanException ex)
            {
            	Throwable nestedEx = ex.getCause();
            	while(nestedEx.getCause() != null) {
            		nestedEx = nestedEx.getCause();
            	}
            	java.lang.System.err.println("Nested exception class: " + nestedEx.getClass().getName());
            	if (nestedEx instanceof CommunicationException)
            	{
            		java.lang.System.err.println("Error sending " + request.getClass().getName() + ": " + nestedEx.getMessage());
            		throw (CommunicationException) nestedEx;
            	}
            	else {
            		throw ex;
            	}
            }
        }

        return reply;
    }
}
