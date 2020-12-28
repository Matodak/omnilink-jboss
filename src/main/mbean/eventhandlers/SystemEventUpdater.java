package mbean.eventhandlers;

import net.homeip.mleclerc.omnilink.enumeration.SecurityModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemEventTypeEnum;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport.SecurityArmingEventInfo;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport.SystemEventInfo;

public class SystemEventUpdater extends SystemEventHandler {

	private boolean alarmOn = false;
	private boolean arming = false;
	
	@Override
	public boolean canHandleEvent(SystemEventInfo event) throws Exception {
		if (event == null) {
    		return false;
    	}
		
		SystemEventTypeEnum eventType = event.getType();
		
		if (eventType.equals(SystemEventTypeEnum.ALARM_ACTIVATION)) {
			// Alarm activated
			alarmOn = true;
			return true;
		} else if (eventType.equals(SystemEventTypeEnum.SECURITY_ARMING)) {
			SecurityArmingEventInfo saEventInfo = (SecurityArmingEventInfo) event;
			SecurityModeEnum securityMode = saEventInfo.getSecurityMode();
			if (securityMode.equals(SecurityModeEnum.OFF)) {
				// Arming off - alarm disabled
				alarmOn = false;
				arming = false;
				return true;
			} else {
				// Arming system
				if (saEventInfo.isExitDelay()) {
					// Start arming delay
					arming = true;
					return true;					
				} else {
					// End arming delay
					arming = false;
					return true;
				}
			}
		}
		
		return !arming && !alarmOn;
	}

	@Override
	public void handleEvent(SystemEventInfo event) throws Exception {
		setAttribute("system", -1, "SystemEvent", event);
	}
}
