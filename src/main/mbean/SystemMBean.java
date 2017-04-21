package mbean;

import java.util.Date;

import net.homeip.mleclerc.omnilink.enumeration.BasicUnitControlEnum;
import net.homeip.mleclerc.omnilink.enumeration.SecurityModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemTypeEnum;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport.SystemEventInfo;

public interface SystemMBean extends ComponentMBean
{
    // Version info: r/o
    String getVersion();

    // Phone number: r/o
    String getLocalPhoneNumber();

    // AC Power: r/o
    Boolean isACPowerOff();

    // Battery: r/o
    Boolean isBatteryLow();

    // Communication failure: r/o
    Boolean isCommunicationFailure();

    // Date, sunrise & sunset: r/o
    Date getDate();
    Date getSunrise();
    Date getSunset();

    // Security mode: r/w
    SecurityModeEnum getSecurityMode();
    void setSecurityMode(SecurityModeEnum securityMode) throws Exception;
    
    // Phone line status: r/w
    PhoneLineStatusEnum getPhoneLineStatus();
    void setPhoneLineStatus(PhoneLineStatusEnum phoneLineStatus);
    
    // System type: r/o
    SystemTypeEnum getSystemType();
    
    // Dial phone numbers: r/o
    String[] getDialPhoneNumbers();
    
    // System status
    SystemEventInfo getSystemEvent();
    void setSystemEvent(SystemEventInfo systemStatus);
    
    // Set all units on/off
    void controlAllUnits(BasicUnitControlEnum condition) throws Exception;
}
