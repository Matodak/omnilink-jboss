package mbean;

import javax.management.AttributeChangeNotification;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.jboss.system.ServiceMBeanSupport;

public class EventForwarding extends ServiceMBeanSupport implements EventForwardingMBean, NotificationListener
{
    private final static String SERVER_DELEGATE = "JMImplementation:type=MBeanServerDelegate";

    public EventForwarding()
    {
    }

    public void startService()
    {
        try
        {
            server.addNotificationListener(new ObjectName(SERVER_DELEGATE), this, null, null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void stopService()
    {
        try
        {
            server.removeNotificationListener(new ObjectName(SERVER_DELEGATE), this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void handleNotification(Notification event, Object parm2)
    {
        try
        {
            if (event instanceof MBeanServerNotification)
            {
                // MBean created or removed from MBean server
                MBeanServerNotification notif = (MBeanServerNotification) event;

                // Get the name of the affected MBean
                ObjectName mBeanName = notif.getMBeanName();

                // Get the domain of the MBean
                String domain = mBeanName.getDomain();
                if (!domain.startsWith("Aegis."))
                    return;

                // Get the type of notification
                String type = notif.getType();
                if (type.equals(MBeanServerNotification.REGISTRATION_NOTIFICATION))
                {
                    // Bean added to MBean server

                    // Register for event notifcation coming from the MBean
                    server.addNotificationListener(mBeanName, this, null, null);
                }
                else if (type.equals(MBeanServerNotification.UNREGISTRATION_NOTIFICATION))
                {
                    // Bean removed from MBean server
                    if (server.isRegistered(mBeanName))
                    {
                        // Unregister for event notifcation coming from the MBean
                        server.removeNotificationListener(mBeanName, this);
                    }
                }
            }
            else if (event instanceof AttributeChangeNotification)
            {
                // Attribute change notification coming from one of the registered beans

                // Put the real source of the event in the user data section
                event.setUserData(event.getSource());

                // Forward event to listeners
                sendNotification(event);
            }
        }
        catch (InstanceNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (ListenerNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
}