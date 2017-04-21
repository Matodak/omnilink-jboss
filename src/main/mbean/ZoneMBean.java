package mbean;

import net.homeip.mleclerc.omnilink.enumeration.ArmingStatusEnum;
import net.homeip.mleclerc.omnilink.enumeration.LatchedAlarmStatusEnum;
import net.homeip.mleclerc.omnilink.enumeration.ZoneConditionEnum;

public interface ZoneMBean extends NumberedComponentMBean
{
    // Zone condition: r/o
    ZoneConditionEnum getCondition();

    // Alarm status: r/o
    LatchedAlarmStatusEnum getLatchedAlarmStatus();

    // Arming status: r/o
    ArmingStatusEnum getArmingStatus();

    // Analog loop reading: r/o
    int getAnalogLoopReading();
    
    // Bypass and restore
    void bypass(int code) throws Exception;
    void restore(int code) throws Exception;
}
