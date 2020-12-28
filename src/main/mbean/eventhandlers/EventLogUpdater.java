package mbean.eventhandlers;

import java.util.Calendar;

import javax.management.ObjectName;

import mbean.EventLogMBean;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport.SystemEventInfo;

public class EventLogUpdater extends SystemEventHandler {
	
	private Calendar nextUpdate;
	private Calendar calendar;
	
	@Override
	public boolean canHandleEvent(SystemEventInfo event) throws Exception {
		// Get the current date & time
		Calendar calendar = Calendar.getInstance();
		
		if (nextUpdate == null) {
			// Don't update the event log now since it's been updated at startup
			// but set the next update
			this.calendar = calendar;
			setNextUpdate();
			return false;
		} else if (calendar.after(nextUpdate)) {
			this.calendar = calendar;
			return true;
		}
		
		return false;
	}

	@Override
	public void handleEvent(SystemEventInfo event) throws Exception {	
		// Update event log
		ObjectName eventLogObjName = new ObjectName(EventLogMBean.NAME);
		getServer().invoke(eventLogObjName, "updateStatus", new Object[0], new String[0]);

		// Set the next update
		setNextUpdate();
		
		// Debug
		//System.out.println("Event log updated. Next update scheduled for: " + nextUpdate.getTime());
	}
	
	private void setNextUpdate() {
		// Keep the date of the next update: every hour
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);		
		nextUpdate = calendar;		
	}
}
