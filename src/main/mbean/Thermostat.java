package mbean;

import net.homeip.mleclerc.omnilink.enumeration.FanModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.HoldModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemModeEnum;
import net.homeip.mleclerc.omnilink.message.ThermostatFanModeCommand;
import net.homeip.mleclerc.omnilink.message.ThermostatHoldModeCommand;
import net.homeip.mleclerc.omnilink.message.ThermostatMaxTemperatureCommand;
import net.homeip.mleclerc.omnilink.message.ThermostatMinTemperatureCommand;
import net.homeip.mleclerc.omnilink.message.ThermostatStatusReport;
import net.homeip.mleclerc.omnilink.message.ThermostatStatusRequest;
import net.homeip.mleclerc.omnilink.message.ThermostatSystemModeCommand;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

public class Thermostat extends NumberedComponentMBeanSupport implements ThermostatMBean
{
    private boolean commFailure;
    private boolean freezeAlarm;
    private double currTemp;
    private double lowSetPoint;
    private double highSetPoint;
    private SystemModeEnum systemMode = null;
    private FanModeEnum fanMode = null;
    private HoldModeEnum holdMode = null;

    public Thermostat(Integer number, String name)
    {
        super(number, name);
    }

    public boolean isCommunicationFailure()
    {
        return commFailure;
    }

    private void setCommunicationFailure(boolean commFailure)
    {
        if (commFailure != this.commFailure)
        {
            sendAttrChangeNotif("CommunicationFailure", Boolean.class.getName(), this.commFailure, commFailure);
            this.commFailure = commFailure;
        }
    }

    public boolean isFreezeAlarm()
    {
        return freezeAlarm;
    }

    private void setFreezeAlarm(boolean freezeAlarm)
    {
        if (freezeAlarm != this.freezeAlarm)
        {
            sendAttrChangeNotif("FreezeAlarm", Boolean.class.getName(), this.freezeAlarm, freezeAlarm);
            this.freezeAlarm = freezeAlarm;
        }
    }

    public double getCurrentTemperature()
    {
        return currTemp;
    }

    private void setCurrentTemperature(double currTemp)
    {
        if (currTemp != this.currTemp)
        {
            sendAttrChangeNotif("CurrentTemperature", Double.class.getName(), this.currTemp, currTemp);
            this.currTemp = currTemp;
        }
    }

    public double getLowSetPoint()
    {
        return lowSetPoint;
    }

    public void setLowSetPoint(double lowSetPoint) throws Exception {
		// Issue request
		RequestMessage request = new ThermostatMinTemperatureCommand(lowSetPoint);
		Utils.sendMessage(server, request);
		
		// Set attribute value
		setLowSetPointAttr(lowSetPoint);
    }
    
    private void setLowSetPointAttr(double lowSetPoint)
    {
        if (lowSetPoint != this.lowSetPoint)
        {
            sendAttrChangeNotif("LowSetPoint", Double.class.getName(), this.lowSetPoint, lowSetPoint);
            this.lowSetPoint = lowSetPoint;
        }
    }

    public double getHighSetPoint()
    {
        return highSetPoint;
    }
    
    public void setHighSetPoint(double highSetPoint) throws Exception {
		// Issue request
		RequestMessage request = new ThermostatMaxTemperatureCommand(highSetPoint);
		Utils.sendMessage(server, request);
		
		// Set attribute value
		setHighSetPointAttr(highSetPoint);
    }

    private void setHighSetPointAttr(double highSetPoint)
    {
        if (highSetPoint != this.highSetPoint)
        {
            sendAttrChangeNotif("HighSetPoint", Double.class.getName(), this.highSetPoint, highSetPoint);
            this.highSetPoint = highSetPoint;
        }
    }

    public SystemModeEnum getSystemMode()
    {
        return systemMode;
    }

    public void setSystemMode(SystemModeEnum systemMode) throws Exception {
		// Issue request
		RequestMessage request = new ThermostatSystemModeCommand(systemMode);
		Utils.sendMessage(server, request);
		
		// Set attribute value
		setSystemModeAttr(systemMode);    
    }
    
    private void setSystemModeAttr(SystemModeEnum systemMode)
    {
        if (!systemMode.equals(this.systemMode))
        {
            sendAttrChangeNotif("SystemMode", systemMode.getClass().getName(), this.systemMode, systemMode);
            this.systemMode = systemMode;
        }
    }

    public FanModeEnum getFanMode()
    {
        return fanMode;
    }

    public void setFanMode(FanModeEnum fanMode) throws Exception {
		// Issue request
		RequestMessage request = new ThermostatFanModeCommand(fanMode);
		Utils.sendMessage(server, request);
		
		// Set attribute value
		setFanModeAttr(fanMode);
    }
    
    private void setFanModeAttr(FanModeEnum fanMode)
    {
        if (!fanMode.equals(this.fanMode))
        {
            sendAttrChangeNotif("FanMode", fanMode.getClass().getName(), this.fanMode, fanMode);
            this.fanMode = fanMode;
        }
    }

    public HoldModeEnum getHoldMode()
    {
        return holdMode;
    }

    public void setHoldMode(HoldModeEnum holdMode) throws Exception {
		// Issue request
		RequestMessage request = new ThermostatHoldModeCommand(holdMode);
		Utils.sendMessage(server, request);
		
		// Set attribute value
		setHoldModeAttr(holdMode);    	
    }
    
    private void setHoldModeAttr(HoldModeEnum holdMode)
    {
        if (!holdMode.equals(this.holdMode))
        {
            sendAttrChangeNotif("HoldMode", holdMode.getClass().getName(), this.holdMode, holdMode);
            this.holdMode = holdMode;
        }
    }

    public void updateStatus()
    {
        try
        {
            ThermostatStatusReport reply = (ThermostatStatusReport) sendMessage(new ThermostatStatusRequest());
            ThermostatStatusReport.ThermostatStatusInfo status = (ThermostatStatusReport.ThermostatStatusInfo) reply.getInfo(getNumber());
            setCommunicationFailure(status.isCommunicationFailure());
            setFreezeAlarm(status.isFreezeAlarm());
            setCurrentTemperature(status.getCurrentTemperature());
            setLowSetPointAttr(status.getLowSetPoint());
            setHighSetPointAttr(status.getHighSetPoint());
            setFanModeAttr(status.getFanMode());
            setSystemModeAttr(status.getSystemMode());
            setHoldModeAttr(status.getHoldStatus());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
