package mbean;

import net.homeip.mleclerc.omnilink.enumeration.ArmingStatusEnum;
import net.homeip.mleclerc.omnilink.enumeration.LatchedAlarmStatusEnum;
import net.homeip.mleclerc.omnilink.enumeration.ZoneConditionEnum;
import net.homeip.mleclerc.omnilink.message.BypassZoneCommand;
import net.homeip.mleclerc.omnilink.message.RestoreZoneCommand;
import net.homeip.mleclerc.omnilink.message.ZoneStatusReport;
import net.homeip.mleclerc.omnilink.message.ZoneStatusRequest;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

public class Zone extends NumberedComponentMBeanSupport implements ZoneMBean
{
    private ZoneConditionEnum condition = null;
    private ArmingStatusEnum armingStatus = null;
    private LatchedAlarmStatusEnum latchedAlarmStatus = null;
    private int analogLoopReading;

    public Zone(Integer number, String name)
    {
        super(number, name);
    }

    public ZoneConditionEnum getCondition()
    {
        return condition;
    }

    private void setCondition(ZoneConditionEnum condition)
    {
        if (!condition.equals(this.condition))
        {
            sendAttrChangeNotif("Condition", condition.getClass().getName(), this.condition, condition);
            this.condition = condition;
        }
    }

    public LatchedAlarmStatusEnum getLatchedAlarmStatus()
    {
        return latchedAlarmStatus;
    }

    private void setLatchedAlarmStatus(LatchedAlarmStatusEnum latchedAlarmStatus)
    {
        if (!latchedAlarmStatus.equals(this.latchedAlarmStatus))
        {
            sendAttrChangeNotif("LatchedAlarmStatus", latchedAlarmStatus.getClass().getName(), this.latchedAlarmStatus, latchedAlarmStatus);
            this.latchedAlarmStatus = latchedAlarmStatus;
        }
    }

    public ArmingStatusEnum getArmingStatus()
    {
        return armingStatus;
    }

    private void setArmingStatus(ArmingStatusEnum armingStatus)
    {
        if (!armingStatus.equals(this.armingStatus))
        {
            sendAttrChangeNotif("ArmingStatus", armingStatus.getClass().getName(), this.armingStatus, armingStatus);
            this.armingStatus = armingStatus;
        }
    }

    public int getAnalogLoopReading()
    {
        return analogLoopReading;
    }

    private void setAnalogLoopReading(int analogLoopReading)
    {
        if (analogLoopReading != this.analogLoopReading)
        {
            sendAttrChangeNotif("AnalogLoopReading", Integer.class.getName(), this.analogLoopReading, analogLoopReading);
            this.analogLoopReading = analogLoopReading;
        }
    }

    public void bypass(int code) throws Exception {
		// Issue request
		RequestMessage request = new BypassZoneCommand(getNumber(), code);
		Utils.sendMessage(server, request);    	
    }
    
    public void restore(int code) throws Exception {
		// Issue request
		RequestMessage request = new RestoreZoneCommand(getNumber(), code);
		Utils.sendMessage(server, request);
    }
    
    public void updateStatus()
    {
        try
        {
            // Get the zone status
            ZoneStatusReport reply = (ZoneStatusReport) sendMessage(new ZoneStatusRequest(getNumber()));
            ZoneStatusReport.ZoneStatusInfo status = (ZoneStatusReport.ZoneStatusInfo) reply.getInfo(getNumber());
            setCondition(status.getCondition());
            setArmingStatus(status.getArmingStatus());
            setLatchedAlarmStatus(status.getLatchedAlarmStatus());
            setAnalogLoopReading(status.getAnalogLoopReading());
            
            // Debug
            //java.lang.System.out.println("Zone " + getNumber() + " state changed: " + getCondition());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
