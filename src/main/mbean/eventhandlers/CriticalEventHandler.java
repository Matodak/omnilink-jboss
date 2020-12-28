package mbean.eventhandlers;

import java.util.Set;

import javax.management.ObjectName;

import net.homeip.mleclerc.omnilink.enumeration.LatchedAlarmStatusEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemEventTypeEnum;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport.AlarmActivationEventInfo;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport.SystemEventInfo;

public class CriticalEventHandler extends SystemEventHandler {

    private final static String EMAIL_SERVICE = "Aegis:name=email.checker";

    public boolean canHandleEvent(SystemEventInfo event)
    {	
    	if (event == null) {
    		return false;
    	}
    	
		// Make sure we are dealing with an important system event
		SystemEventTypeEnum eventType = event.getType();
        return (eventType.equals(SystemEventTypeEnum.ALARM_ACTIVATION) ||
                eventType.equals(SystemEventTypeEnum.AC_POWER_OFF) ||
                eventType.equals(SystemEventTypeEnum.AC_POWER_RESTORED) ||
                eventType.equals(SystemEventTypeEnum.BATTERY_LOW) ||
                eventType.equals(SystemEventTypeEnum.BATTERY_OK) ||
                eventType.equals(SystemEventTypeEnum.DCM_TROUBLE) ||
                eventType.equals(SystemEventTypeEnum.DCM_OK));    	
    }
    
	public void handleEvent(SystemEventInfo event) throws Exception
	{	
		StringBuffer msgBody = new StringBuffer();
		SystemEventTypeEnum eventType = event.getType();
		if (eventType.equals(SystemEventTypeEnum.ALARM_ACTIVATION)) {
			AlarmActivationEventInfo aaei = (AlarmActivationEventInfo) event;
			msgBody.append("Area: " + aaei.getArea() + "\n");
			msgBody.append("Alarm type: " + aaei.getAlarmType() + "\n");
			msgBody.append(getAffectedZones());
		} else {
			msgBody.append(event.toString());
		}
		   
		// Send an email
		getServer().invoke(new ObjectName(EMAIL_SERVICE), "sendEmail",  new Object[] {event.getType().getUserLabel(), msgBody.toString()}, new String[] {"java.lang.String", "java.lang.String"});
	}
	
	private String getAffectedZones() throws Exception {
		StringBuffer strbuf = new StringBuffer();
		
		strbuf.append("Affected zones:\n");

		Set<ObjectName> instanceList = getServer().queryNames(new ObjectName("Aegis.zone:*"), null);
        if (instanceList != null) {
            for (ObjectName objName : instanceList) {
            	// Check to see if the alarm is tripped and add it to the list of affected zones if that's the case
                int zoneNumber = (Integer) getServer().getAttribute(objName, "Number");
                String zoneName = (String) getServer().getAttribute(objName, "Name");
				LatchedAlarmStatusEnum latchedAlarmStatus = (LatchedAlarmStatusEnum) getServer().getAttribute(objName, "LatchedAlarmStatus");
				if (latchedAlarmStatus.equals(LatchedAlarmStatusEnum.TRIPPED)) {
					strbuf.append("   Zone #" + zoneNumber + ": " + zoneName + "\n");
				}
            }
        }
        
        return strbuf.toString();
	}
}
