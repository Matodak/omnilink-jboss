package mbean;

/*
 @todo
   - Detail attributes and update notif for each component
*/

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.timer.Timer;

import mbean.eventhandlers.CriticalEventHandler;
import mbean.eventhandlers.EventLogUpdater;
import mbean.eventhandlers.SystemEventHandler;
import mbean.eventhandlers.SystemEventUpdater;
import mbean.eventhandlers.SystemTimeUpdater;
import net.homeip.mleclerc.omnilink.BadStartCharacterException;
import net.homeip.mleclerc.omnilink.enumeration.NameTypeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemEventTypeEnum;
import net.homeip.mleclerc.omnilink.enumeration.UnitControlEnum;
import net.homeip.mleclerc.omnilink.message.LoginControl;
import net.homeip.mleclerc.omnilink.message.LogoutControl;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport;
import net.homeip.mleclerc.omnilink.message.SystemEventsRequest;
import net.homeip.mleclerc.omnilink.message.UploadNameMessageReport;
import net.homeip.mleclerc.omnilink.message.UploadNameMessageRequest;
import net.homeip.mleclerc.omnilink.messagebase.ReplyMessage;

import org.jboss.system.ServiceMBeanSupport;

public class Controller extends ServiceMBeanSupport implements ControllerMBean, NotificationListener
{
    private final static String EVENT_TYPE = "systemEventsUpdate";
    private final static int MAX_LOGIN_ATTEMPTS = 5;
    
    private String loginCode = null;
    private int updatePeriod = 0; // in seconds
    private char firstHouseCode = 0;
    private boolean loggedIn = false;

    private Map unitNameMap = new HashMap();
    private Map tempNameMap = new HashMap();
    private Map zoneNameMap = new HashMap();
    private Map messageNameMap = new HashMap();
    private Map buttonNameMap = new HashMap();
    private Map codeNameMap = new HashMap();
    private ObjectName timerName;
    private Integer notifId;
    private Collection systemEventHandlers = new ArrayList();

    public Controller()
    {
        try
        {
        	// Create the object name for the timer
            timerName = new ObjectName(":service=Timer");
            
            // Add all the supported macros
            systemEventHandlers.add(new CriticalEventHandler());
            systemEventHandlers.add(new SystemTimeUpdater());
            systemEventHandlers.add(new SystemEventUpdater());
            systemEventHandlers.add(new EventLogUpdater());
        }
        catch (MalformedObjectNameException ex)
        {
            ex.printStackTrace();
        }
    }

    public void setLoginCode(String loginCode)
    {
        this.loginCode = loginCode;
    }

    public void setUpdatePeriod(int updatePeriod)
    {
        this.updatePeriod = updatePeriod;
    }

    public int getUpdatePeriod()
    {
        return updatePeriod;
    }

    public boolean isLoggedIn()
    {
        return loggedIn;
    }

    public String getLoginCode()
    {
        return loginCode;
    }

    public String getFirstHouseCode()
    {
        return String.valueOf(firstHouseCode);
    }

    public void setFirstHouseCode(String houseCode)
    {
        this.firstHouseCode = houseCode.charAt(0);;
    }

    public void login()
    {
        try
        {
            if (loginCode != null && loginCode.length() > 0 && !loggedIn)
            {
            	// Logout/login user and retry a number of times if needed
            	int loginCount = 1;
            	while(true) {
	                java.lang.System.out.println("User login, attempt #" + loginCount);
	                try
	                {
	                    // Send a logout message
	                    Utils.sendMessage(server, new LogoutControl());
	                }
	                catch(Exception e)
	                {
	                    java.lang.System.out.println("No user logged in");
	                }
	
	                try {
	                	// Send a login message
	                	ReplyMessage loginResponse = Utils.sendMessage(server, new LoginControl(loginCode));
	                	if (loginResponse != null) {
	                		// Login successful, stop retrying
	                		java.lang.System.out.println("User login successful");
	                		break;
	                	} else {
	                		throw new IllegalStateException();
	                	}
	                } catch(BadStartCharacterException ex) {
	                	// Error parsing the login response
	                	if (loginCount >= MAX_LOGIN_ATTEMPTS) {
	                		// Login failed after number of attempts
	                		throw ex;
	                	}
	                	else {
	                		// Debug
	                		java.lang.System.out.println("Error processing login response");
	                		// Pause before attempting another user login
	                		Thread.sleep(1000);
	                	}
	                }
	                loginCount++;
            	}
            }
                
            // Create the component mbeans
            createMBeans();

            // Register this MBean with the timer bean via the server
            NotificationFilterSupport myFilter = new NotificationFilterSupport();
            myFilter.enableType(EVENT_TYPE);
            server.addNotificationListener(timerName, this, myFilter, null);

            // Add a new notification to the timer bean
            if (updatePeriod > 0)
            {
            	notifId = (Integer) server.invoke(timerName, "addNotification", new Object[] {EVENT_TYPE, "message", null, Calendar.getInstance().getTime(), new Long(updatePeriod * Timer.ONE_SECOND)}, new String[] {"java.lang.String", "java.lang.String", "java.lang.Object", "java.util.Date", "long"});
            }

            // Logged in
            loggedIn = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void logout()
    {
        if (loggedIn)
        {
            try
            {
                // Remove notification from timer bean
            	if (notifId != null)
            	{
            		server.invoke(timerName, "removeNotification", new Object[] {notifId}, new String[] {"java.lang.Integer"});
            	}

                // Remove notification listener
                server.removeNotificationListener(timerName, this);

                if (loginCode != null && loginCode.length() > 0) {
	                // Send a logout message
	                Utils.sendMessage(server, new LogoutControl());
                }

                // Remove the component mbeans
                removeMBeans();

                // Logged out
                loggedIn = false;

                // Reset the user code
                loginCode = null;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void startService()
    {
        login();
    }

    public void stopService()
    {
        logout();
    }

    private void createMBeans()
    {
        try
        {
            // Create the system MBean
            ObjectName systemMBeanName = new ObjectName(ComponentMBean.DOMAIN_BASE + ".system:name=system");
            createMBean(System.class, systemMBeanName, new Object[0], new String[0]);

            // Create the event log MBean
            ObjectName eventLogMBeanName = new ObjectName(EventLogMBean.NAME);
            createMBean(EventLog.class, eventLogMBeanName, new Object[0], new String[0]);

            // Get all the (unit, zone, thermostat) names from
            // the communication MBean
            UploadNameMessageReport reply = (UploadNameMessageReport)
                    Utils.sendMessage(server, new UploadNameMessageRequest());

            // Put the names in various maps
            for (Iterator iter = reply.getInfoList().iterator(); iter.hasNext(); )
            {
                // Get the current name info
                UploadNameMessageReport.NameInfo nameInfo =
                                 (UploadNameMessageReport.NameInfo) iter.next();

                // Get the component number and name
                int number = nameInfo.getItem();
                String name = nameInfo.getText();

                // Get the type of component the name belongs to
                NameTypeEnum type = nameInfo.getType();
                if (type.equals(NameTypeEnum.UNIT))
            		unitNameMap.put(new Integer(number), name);
                else if (type.equals(NameTypeEnum.THERMOSTAT))
                    tempNameMap.put(new Integer(number), name);
                else if (type.equals(NameTypeEnum.ZONE))
                    zoneNameMap.put(new Integer(number), name);
                else if (type.equals(NameTypeEnum.MESSAGE))
                    messageNameMap.put(new Integer(number), name);
                else if (type.equals(NameTypeEnum.BUTTON))
                    buttonNameMap.put(new Integer(number), name);
                else if (type.equals(NameTypeEnum.CODE))
                    codeNameMap.put(new Integer(number), name);
            }

            // Create unit MBeans
            createMBeans(Unit.class, "unit", unitNameMap);

            // Create thermostat MBeans
            createMBeans(Thermostat.class, "thermostat", tempNameMap);

            // Create zone MBeans
            createMBeans(Zone.class, "zone", zoneNameMap);

            // Create message MBeans
            createMBeans(Message.class, "message", messageNameMap);

            // Create button MBeans
            createMBeans(Button.class, "button", buttonNameMap);

            // Create code MBeans
            createMBeans(Code.class, "code", codeNameMap);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void createMBeans(Class compClass, String compType, Map compNameMap) throws Exception
    {
        // Get all the components that have names assigned to them
        // and for each component create a corresponding MBean
        for (Iterator iter = compNameMap.keySet().iterator(); iter.hasNext(); )
        {
            // Get the current unit number
            Integer compNoInt = (Integer) iter.next();

            // Get the name of the unit
            String compName = (String) compNameMap.get(compNoInt);

            // Create a MBean representing this unit
            ObjectName compMBeanName = new ObjectName(ComponentMBean.DOMAIN_BASE + "." + compType + ":number=" + compNoInt + ",name=" + compName);
            Object[] params = { compNoInt, compName };
            String[] sig = { Integer.class.getName(), String.class.getName() };
            createMBean(compClass, compMBeanName, params, sig);
        }
    }

    private void createMBean(Class mbeanClass, ObjectName mbeanName, Object[] params, String[] sig) throws Exception
    {
        if (server.isRegistered(mbeanName))
        {
            java.lang.System.out.println("MBean already registered: " + mbeanName);
            server.unregisterMBean(mbeanName);
        }

        server.createMBean(mbeanClass.getName(), mbeanName, params, sig);
    }

    private void removeMBeans()
    {
        try
        {
            Set nameList = server.queryNames(new ObjectName(ComponentMBean.DOMAIN_BASE + ".*:*"), null);
            for (Iterator iter = nameList.iterator(); iter.hasNext(); )
            {
                ObjectName name = (ObjectName) iter.next();
                server.unregisterMBean(name);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void handleNotification(Notification notification, Object handback)
    {
        try
        {
            // Get the new system events
            SystemEventsReport reply = (SystemEventsReport) Utils.sendMessage(server, new SystemEventsRequest());
            if (reply == null) {
            	return;
            }
            
            // Get the list of events
            Collection eventList = reply.getInfoList();
            if (eventList.isEmpty()) {
                // No system event received
            	// Notify all the event handlers indicating that no system event was received at this time
                for (Iterator handlerIter = systemEventHandlers.iterator(); handlerIter.hasNext(); )
                {
                	SystemEventHandler eventHandler = (SystemEventHandler) handlerIter.next();
                	eventHandler.setServer(server);                    	
                	if (eventHandler.canHandleEvent(null))
                	{
                		eventHandler.handleEvent(null);
                	}
                }
            }
            else {
            	// Process the system events received
                for (Iterator iter = eventList.iterator(); iter.hasNext(); )
                {
                    // Get the current event
                    SystemEventsReport.SystemEventInfo systemEvent = (SystemEventsReport.SystemEventInfo) iter.next();

                    // Update component based on the event type
                    SystemEventTypeEnum eventType = systemEvent.getType();
                    if (eventType.equals(SystemEventTypeEnum.ZONE_STATE_CHANGE))
                    {
                        // Zone state change
                        SystemEventsReport.ZoneStateChangeEventInfo info = (SystemEventsReport.ZoneStateChangeEventInfo) systemEvent;

                        // Get the zone number
                        int zoneNo = info.getZone();

                        // Refresh the component
                        updateComponentStatus("zone", zoneNo);
                    }
                    else if (eventType.equals(SystemEventTypeEnum.UNIT_STATE_CHANGE))
                    {
                        // Unit state change
                        SystemEventsReport.UnitStateChangeEventInfo info = (SystemEventsReport.UnitStateChangeEventInfo) systemEvent;

                    	// Debug
                        java.lang.System.out.println("Unit state change: " + info);
                    	
                        // Get the unit number
                        int unitNo = info.getUnit();

                        // Refresh the component
                        updateComponentStatus("unit", unitNo);
                    }
                    else if (eventType.equals(SystemEventTypeEnum.SECURITY_ARMING))
                    {
                        // Security arming change
                    }
                    else if (eventType.equals(SystemEventTypeEnum.PHONE_LINE_DEAD) ||
                             eventType.equals(SystemEventTypeEnum.PHONE_LINE_OFF_HOOK) ||
                             eventType.equals(SystemEventTypeEnum.PHONE_LINE_ON_HOOK) ||
                             eventType.equals(SystemEventTypeEnum.PHONE_LINE_RING) )
                    {
                        // Change in phone state

                        // Map the phone state to an enum
                        PhoneLineStatusEnum phoneStatus = (PhoneLineStatusEnum) PhoneLineStatusEnum.metaInfo.getByValue(eventType.getValue());

                        // Set the system's phone status
                        setComponentAttr("system", 0, "PhoneLineStatus", phoneStatus);
                    }
                    else if (eventType.equals(SystemEventTypeEnum.X10_CODE_RECEIVED))
                    {
                        // X-10 code received
                        SystemEventsReport.X10CodeReceivedEventInfo info = (SystemEventsReport.X10CodeReceivedEventInfo) systemEvent;

                        // Debug
                        java.lang.System.out.println("X10 code received: " + info);
                        
                        // Make sure only one unit is affected
                        if (info.isOneUnit())
                        {
                            // Get the X10 unit number
                            char x10HouseCode = info.getHouseCode();
                            int x10UnitNumber = info.getUnit();
                            boolean isOn = info.isOn();
                            UnitControlEnum condition = info.isOn() ? UnitControlEnum.ON : UnitControlEnum.OFF;
                            
                            // Determine the unit component number
                            int unitNumber = (x10HouseCode - firstHouseCode) * 16 + x10UnitNumber;
                            
                            // Set unit current status
                            setComponentAttr("unit", unitNumber, "UnitOn", new Boolean(isOn));
                            setComponentAttr("unit", unitNumber, "Condition", condition);
                        }
                    } 
                    else if (eventType.equals(SystemEventTypeEnum.ALL_ON_OFF)) 
                    {
                    	// All on/off command received
                    	SystemEventsReport.AllOnOffEventInfo info = (SystemEventsReport.AllOnOffEventInfo) systemEvent;  
		            	UnitControlEnum condition = info.isOn() ? UnitControlEnum.ON : UnitControlEnum.OFF;
            			Set<ObjectName> instanceList = server.queryNames(new ObjectName("Aegis.unit:*"), null);
    	            	// Set all units to on/off
            	        if (instanceList != null)
            	        {
            	            for (ObjectName objName : instanceList)
            	            {	
            	            	boolean isUnit = (Boolean) server.getAttribute(objName, "Unit");
            	            	if (isUnit) {
            	            		server.invoke(objName, "overrideConditionAttr", new Object[] {condition}, new String[] { UnitControlEnum.class.getName() });
            	            	}
            	            }
            	        }
                    }

                    // Notify all the event handlers about the current system event
                    for (Iterator handlerIter = systemEventHandlers.iterator(); handlerIter.hasNext(); )
                    {
                    	SystemEventHandler eventHandler = (SystemEventHandler) handlerIter.next();
                    	eventHandler.setServer(server);                    	
                    	if (eventHandler.canHandleEvent(systemEvent))
                    	{
                    		eventHandler.handleEvent(systemEvent);
                    	}
                    }
                }
            }
                
            // Always update the following components
            updateComponentStatus("system", 0);
            updateComponentStatus("thermostat", -1);
            updateComponentStatus("message", -1);
            updateComponentStatus("zone", -1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void updateComponentStatus(String compType, int compNo)
    {
    	if (server == null) {
    		return;
    	}
    	
        try
        {
            String name = ComponentMBean.DOMAIN_BASE + "." + compType;
            if (compNo >= 1)
                name += ":number=" + compNo + ",*";
            else
                name += ":*";
            ObjectName compTypeName = new ObjectName(name);
            Set compMBeanList = server.queryNames(compTypeName, null);
            if (compNo == -1 || compMBeanList.size() == 1)
            {
                for (Iterator iter = compMBeanList.iterator(); iter.hasNext(); )
                {
                    ObjectName compMBeanName = (ObjectName) iter.next();
                  	server.invoke(compMBeanName, "updateStatus", null, null);
                }
            }
            else
                throw new IllegalStateException("Component not found in MBean server: " + compTypeName);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void setComponentAttr(String compType, int compNo, String attrName, Object attrValue)
    {
        try
        {
            String name = ComponentMBean.DOMAIN_BASE + "." + compType;
            if (compNo >= 1)
                name += ":number=" + compNo + ",*";
            else
                name += ":*";
            ObjectName compTypeName = new ObjectName(name);
            Set compMBeanList = server.queryNames(compTypeName, null);
            if (compNo == -1 || compMBeanList.size() == 1)
            {
                for (Iterator iter = compMBeanList.iterator(); iter.hasNext(); )
                {
                    ObjectName compMBeanName = (ObjectName) iter.next();
                	server.setAttribute(compMBeanName, new Attribute(attrName, attrValue));
                }
            }
            else
                throw new IllegalStateException("Component not found in MBean server: " + compTypeName);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
