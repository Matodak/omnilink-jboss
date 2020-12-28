package mbean;

import net.homeip.mleclerc.omnilink.enumeration.FanModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.HoldModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemModeEnum;

interface ThermostatMBean extends NumberedComponentMBean
{
    // Comm failure: r/o
    boolean isCommunicationFailure();

    // Freeze alarm: r/o
    boolean isFreezeAlarm();

    // Current temp: r/o
    double getCurrentTemperature();

    // Low setpoint: r/w
    double getLowSetPoint();
    void setLowSetPoint(double lowSetPoint) throws Exception;
    
    // High setpoint: r/w
    double getHighSetPoint();
    void setHighSetPoint(double highSetPoint) throws Exception;

    // System mode: r/w
    SystemModeEnum getSystemMode();
    void setSystemMode(SystemModeEnum systemMode) throws Exception;

    // Fan mode: r/w
    FanModeEnum getFanMode();
    void setFanMode(FanModeEnum fanMode) throws Exception;

    // Hold status: r/w
    HoldModeEnum getHoldMode();
    void setHoldMode(HoldModeEnum holdMode) throws Exception;
}
