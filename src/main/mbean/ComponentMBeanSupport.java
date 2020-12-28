package mbean;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import net.homeip.mleclerc.omnilink.messagebase.ReplyMessage;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

public abstract class ComponentMBeanSupport extends NotificationBroadcasterSupport implements ComponentMBean, MBeanRegistration
{
    protected MBeanServer server = null;

    public abstract void updateStatus();

    public ObjectName preRegister(MBeanServer server, ObjectName name)
    {
        this.server = server;
        return name;
    }

    public void postRegister(Boolean registrationDone)
    {
        updateStatus();
    }

    public void preDeregister()
    {
    }

    public void postDeregister()
    {
    }

    protected ReplyMessage sendMessage(RequestMessage request) throws Exception
    {
        return Utils.sendMessage(server, request);
    }

    protected void sendAttrChangeNotif(String attrName, String attrType, Object oldValue, Object newValue)
    {
        //java.lang.System.err.println("Attr value changed: " + attrName + " from: " + oldValue + " to: " + newValue);
        AttributeChangeNotification notif = new AttributeChangeNotification(this, 0, java.lang.System.currentTimeMillis(), "Attribute Change", attrName, attrType, oldValue, newValue);
        sendNotification(notif);
    }
}
