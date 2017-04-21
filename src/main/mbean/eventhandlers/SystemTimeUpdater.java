package mbean.eventhandlers;

import java.util.Calendar;

import net.homeip.mleclerc.omnilink.message.SetTimeCommand;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport.SystemEventInfo;

public class SystemTimeUpdater extends SystemEventHandler {
	
	private Calendar nextUpdate;
	private Calendar calendar;
	
	@Override
	public boolean canHandleEvent(SystemEventInfo event) throws Exception {
		// Get the current date & time
		Calendar calendar = Calendar.getInstance();
		
		// Trigger a system time update every midnight
		if (nextUpdate == null || calendar.after(nextUpdate)) {
			this.calendar = calendar;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void handleEvent(SystemEventInfo event) throws Exception {	
		// Update system time to the current time
		sendCommand(new SetTimeCommand(calendar));

		// Keep the date of the next update: at 2 AM the next day 
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		nextUpdate = calendar;
		
		// Debug
		//System.out.println("System time updated. Next update scheduled for: " + nextUpdate.getTime());
	}
}
