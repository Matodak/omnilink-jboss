package mbean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.homeip.mleclerc.omnilink.enumeration.BasicUnitControlEnum;
import net.homeip.mleclerc.omnilink.enumeration.SecurityModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemEventTypeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemTypeEnum;
import net.homeip.mleclerc.omnilink.message.AllUnitsCommand;
import net.homeip.mleclerc.omnilink.message.MessageConstants;
import net.homeip.mleclerc.omnilink.message.SecurityCommand;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport;
import net.homeip.mleclerc.omnilink.message.SystemInformationReport;
import net.homeip.mleclerc.omnilink.message.SystemInformationRequest;
import net.homeip.mleclerc.omnilink.message.SystemStatusReport;
import net.homeip.mleclerc.omnilink.message.SystemStatusRequest;
import net.homeip.mleclerc.omnilink.message.UploadSetupMessageReport;
import net.homeip.mleclerc.omnilink.message.UploadSetupMessageRequest;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport.SystemEventInfo;
import net.homeip.mleclerc.omnilink.message.SystemStatusReport.ExpansionEnclosureInfo;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

public class System extends ComponentMBeanSupport implements SystemMBean
{
    private SecurityModeEnum securityMode = null;
    private PhoneLineStatusEnum phoneLineStatus = PhoneLineStatusEnum.UNKNOWN;
    private String version = null;
    private String phoneNumber = null;
    private Boolean acPowerOff = null;
    private Boolean batteryLow = null;
    private Boolean commFailure = null;
    private Date date = null;
    private Date sunrise = null;
    private Date sunset = null;
    private SystemTypeEnum systemType = null;
    private String[] dialPhoneNumbers = null;
    private SystemEventInfo systemEvent = (new SystemEventsReport()).new DefaultSystemEventInfo(0, SystemEventTypeEnum.USER_MACRO_BUTTON);
    
    public System()
    {
    }

    public String getVersion()
    {
        return version;
    }

    private void setVersion(String version)
    {
        if (!version.equals(this.version))
        {
            sendAttrChangeNotif("Version", version.getClass().getName(), this.version, version);
            this.version = version;
        }
    }

    public String getLocalPhoneNumber()
    {
        return phoneNumber;
    }

    private void setLocalPhoneNumber(String phoneNumber)
    {
        if (!phoneNumber.equals(this.phoneNumber))
        {
            sendAttrChangeNotif("LocalPhoneNumber", phoneNumber.getClass().getName(), this.phoneNumber, phoneNumber);
            this.phoneNumber = phoneNumber;
        }
    }

    public Boolean isACPowerOff()
    {
        return acPowerOff;
    }

    private void setACPowerOff(Boolean acPowerOff)
    {
        if (!acPowerOff.equals(this.acPowerOff))
        {
            sendAttrChangeNotif("ACPowerOff", acPowerOff.getClass().getName(), this.acPowerOff, acPowerOff);
            this.acPowerOff = acPowerOff;
        }
    }

    public Boolean isBatteryLow()
    {
        return batteryLow;
    }

    private void setBatteryLow(Boolean batteryLow)
    {
        if (!batteryLow.equals(this.batteryLow))
        {
            sendAttrChangeNotif("BatteryLow", batteryLow.getClass().getName(), this.batteryLow, batteryLow);
            this.batteryLow = batteryLow;
        }
    }

    public Boolean isCommunicationFailure()
    {
        return commFailure;
    }

    private void setCommunicationFailure(Boolean commFailure)
    {
        if (!commFailure.equals(this.commFailure))
        {
            sendAttrChangeNotif("CommunicationFailure", commFailure.getClass().getName(), this.commFailure, commFailure);
            this.commFailure = commFailure;
        }
    }

    public Date getDate()
    {
        return date;
    }

    private void setDate(Date date)
    {
        if (!date.equals(this.date))
        {
            sendAttrChangeNotif("Date", date.getClass().getName(), this.date, date);
            this.date = date;
        }
    }

    public Date getSunrise()
    {
        return sunrise;
    }

    private void setSunrise(Date sunrise)
    {
        if (!sunrise.equals(this.sunrise))
        {
            sendAttrChangeNotif("Sunrise", sunrise.getClass().getName(), this.sunrise, sunrise);
            this.sunrise = sunrise;
        }
    }

    public Date getSunset()
    {
        return sunset;
    }

    private void setSunset(Date sunset)
    {
        if (!sunset.equals(this.sunset))
        {
            sendAttrChangeNotif("Sunset", sunset.getClass().getName(), this.sunset, sunset);
            this.sunset = sunset;
        }
    }

    public SecurityModeEnum getSecurityMode()
    {
        return securityMode;
    }
	
	public void setSecurityMode(SecurityModeEnum securityMode) throws Exception {
		// Issue request
		RequestMessage request = new SecurityCommand(securityMode);
		Utils.sendMessage(server, request);

		// Set attribute value
		setSecurityModeAttr(securityMode);
	}

    private void setSecurityModeAttr(SecurityModeEnum securityMode)
    {
        if (!securityMode.equals(this.securityMode))
        {
            sendAttrChangeNotif("SecurityMode", securityMode.getClass().getName(), this.securityMode, securityMode);
            this.securityMode = securityMode;
        }
    }
    public PhoneLineStatusEnum getPhoneLineStatus()
    {
        return phoneLineStatus;
    }

    public void setPhoneLineStatus(PhoneLineStatusEnum phoneLineStatus)
    {
        if (!this.phoneLineStatus.equals(phoneLineStatus)) {
            sendAttrChangeNotif("PhoneLineStatus", phoneLineStatus.getClass().getName(), this.phoneLineStatus, phoneLineStatus);
            this.phoneLineStatus = phoneLineStatus;
        }
    }

	public SystemTypeEnum getSystemType() {
		return systemType;
	}

	private void setSystemType(SystemTypeEnum systemType) {
		this.systemType = systemType;
	}

	public String[] getDialPhoneNumbers() {
		return dialPhoneNumbers;
	}

	private void setDialPhoneNumbers(String[] dialPhoneNumbers) {
		List<String> validDialPhoneNumbers = new ArrayList<String>();		
		for (int i = 0; i < dialPhoneNumbers.length; i++) {
			String dialPhoneNumber = dialPhoneNumbers[i];
			if (dialPhoneNumber.equals("-")) {
				break;
			}
			validDialPhoneNumbers.add(dialPhoneNumber);
		}		
		this.dialPhoneNumbers = validDialPhoneNumbers.toArray(new String[0]);
	}

	public SystemEventInfo getSystemEvent() {
		return systemEvent;
	}

	public void setSystemEvent(SystemEventInfo systemEvent) {
        if (this.systemEvent != systemEvent) {
            sendAttrChangeNotif("SystemEvent", SystemEventInfo.class.getName(), this.systemEvent, systemEvent);
            this.systemEvent = systemEvent;
        }
	}

	public void controlAllUnits(BasicUnitControlEnum condition) throws Exception {
		// Issue request
		RequestMessage request = new AllUnitsCommand(condition);
		Utils.sendMessage(server, request);
	}
	
	public void updateStatus()
    {
        try
        {
            // Get the system status
            SystemStatusReport status = (SystemStatusReport) sendMessage(new SystemStatusRequest());
            setDate(status.getDate());
            setSunrise(status.getSunrise());
            setSunset(status.getSunset());
            setSecurityModeAttr(status.getSecurityMode(MessageConstants.DEFAULT_AREA));
            ExpansionEnclosureInfo expansionEnclosure = status.getExpansionEnclosure(MessageConstants.DEFAULT_AREA);
            if (expansionEnclosure != null) {
	            setCommunicationFailure(new Boolean(status.getExpansionEnclosure(MessageConstants.DEFAULT_AREA).isCommunicationFailure()));
	            setBatteryLow(new Boolean(status.getExpansionEnclosure(MessageConstants.DEFAULT_AREA).isBatteryLow()));
	            setACPowerOff(new Boolean(status.getExpansionEnclosure(MessageConstants.DEFAULT_AREA).isACPowerOff()));
            }

            // Get the system info
            SystemInformationReport info = (SystemInformationReport) sendMessage(new SystemInformationRequest());
            setVersion(info.getVersion());
            setLocalPhoneNumber(info.getLocalPhoneNumber());
            setSystemType(info.getModel());
            
            // Get setup info only once
            if (getDialPhoneNumbers() == null) {
	            UploadSetupMessageReport setup = (UploadSetupMessageReport) sendMessage(new UploadSetupMessageRequest());
	            setDialPhoneNumbers(setup.getDialPhoneNumbers());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
