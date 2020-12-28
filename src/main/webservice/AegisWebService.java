package webservice;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import mbean.ComponentMBean;
import mbean.EventLogMBean;
import mbean.PhoneLineStatusEnum;
import net.homeip.mleclerc.omnilink.enumeration.ArmingStatusEnum;
import net.homeip.mleclerc.omnilink.enumeration.BasicUnitControlEnum;
import net.homeip.mleclerc.omnilink.enumeration.FanModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.HoldModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.LatchedAlarmStatusEnum;
import net.homeip.mleclerc.omnilink.enumeration.SecurityModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemEventTypeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemModeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemTypeEnum;
import net.homeip.mleclerc.omnilink.enumeration.UnitControlEnum;
import net.homeip.mleclerc.omnilink.enumeration.ZoneConditionEnum;
import net.homeip.mleclerc.omnilink.message.UploadEventLogMessageReport;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport.SystemEventInfo;

import org.jboss.mx.util.MBeanServerLocator;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public class AegisWebService {

	private MBeanServer server;
	
	public AegisWebService() {
		server = MBeanServerLocator.locateJBoss();		
	}
	
	@WebMethod
	public SystemStatusResponse getSystemStatus() {
		try {
			ObjectName systemObjName = new ObjectName("Aegis.system:name=system");
			Date date = (Date) server.getAttribute(systemObjName, "Date");
			Date sunset = (Date) server.getAttribute(systemObjName, "Sunset");
			Date sunrise = (Date) server.getAttribute(systemObjName, "Sunrise");
			PhoneLineStatusEnum phoneLineStatus = (PhoneLineStatusEnum) server.getAttribute(systemObjName, "PhoneLineStatus");
			SecurityModeEnum securityMode = (SecurityModeEnum) server.getAttribute(systemObjName, "SecurityMode");
			SystemEventInfo systemEvent = (SystemEventInfo) server.getAttribute(systemObjName, "SystemEvent");
			
			// Create response
			SystemStatusResponse response = new SystemStatusResponse();
			response.setSecurityMode(convert(SecurityMode.class, securityMode));
			response.setDate(date.getTime());
			response.setSunrise(sunrise.getTime());
			response.setSunset(sunset.getTime());
			response.setPhoneLineStatus(convert(PhoneLineStatus.class, phoneLineStatus));
			response.setSystemStatus(getSystemStatus(systemEvent));
			
			// Send response
			return response;
		}
		catch(Exception ex) {
			// Error
			return null;
		}
	}
	
	@WebMethod
	public boolean setSecurity(SecurityMode securityMode) {
		try {
			ObjectName systemObjName = new ObjectName("Aegis.system:name=system");
			Attribute securityModeAttr = new Attribute("SecurityMode", convert(SecurityModeEnum.class, securityMode));
			server.setAttribute(systemObjName, securityModeAttr);
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}
	}
	
	@WebMethod
	public SystemInformationResponse getSystemInformation() {
		try {
			ObjectName systemObjName = new ObjectName("Aegis.system:name=system");
			String version = (String) server.getAttribute(systemObjName, "Version");
			String localPhoneNumber = (String) server.getAttribute(systemObjName, "LocalPhoneNumber");
			SystemTypeEnum systemType = (SystemTypeEnum) server.getAttribute(systemObjName, "SystemType");
			String[] dialPhoneNumbers = (String[]) server.getAttribute(systemObjName, "DialPhoneNumbers");
			
			// Create response
			SystemInformationResponse response = new SystemInformationResponse();
			response.setVersion(version);
			response.setLocalPhoneNumber(localPhoneNumber);
			response.setSystemType(convert(SystemType.class, systemType));
			response.setDialPhoneNumbers(dialPhoneNumbers);
			
			// Send response
			return response;
		} catch(Exception ex) {
			return null;
		}		
	}

	@WebMethod
	public ThermostatStatusResponse getThermostatsStatus() {
		try {
			ObjectName thermostatObjName = new ObjectName("Aegis.thermostat:name=Thermostat,number=1");
			double currentTemperature = (Double) server.getAttribute(thermostatObjName, "CurrentTemperature");
			double lowSetPoint = (Double) server.getAttribute(thermostatObjName, "LowSetPoint");
			double highSetPoint = (Double) server.getAttribute(thermostatObjName, "HighSetPoint");
			SystemModeEnum systemMode = (SystemModeEnum) server.getAttribute(thermostatObjName, "SystemMode");
			FanModeEnum fanMode = (FanModeEnum) server.getAttribute(thermostatObjName, "FanMode");
			HoldModeEnum holdStatus = (HoldModeEnum) server.getAttribute(thermostatObjName, "HoldMode");
			
			// Create response
			ThermostatStatusResponse response = new ThermostatStatusResponse();
			response.setCurrentTemperature(currentTemperature);
			response.setLowSetPoint(lowSetPoint);
			response.setHighSetPoint(highSetPoint);
			response.setFanMode(convert(FanMode.class, fanMode));
			response.setHoldMode(convert(HoldMode.class, holdStatus));
			response.setSystemMode(convert(SystemMode.class, systemMode));
			
			// Send response
			return response;
		} catch(Exception ex) {
			return null;
		}
	}
	
	@WebMethod
	public boolean setThermostatLowSetPoint(double lowSetPoint) {
		try {
			ObjectName thermostatObjName = new ObjectName("Aegis.thermostat:name=Thermostat,number=1");
			Attribute lowSetPointAttr = new Attribute("LowSetPoint", new Double(lowSetPoint));
			server.setAttribute(thermostatObjName, lowSetPointAttr);
			return true;
		} catch(Exception ex) {
			return false;
		}
	}
	
	@WebMethod
	public boolean setThermostatHighSetPoint(double highSetPoint) {
		try {
			ObjectName thermostatObjName = new ObjectName("Aegis.thermostat:name=Thermostat,number=1");
			Attribute highSetPointAttr = new Attribute("HighSetPoint", new Double(highSetPoint));
			server.setAttribute(thermostatObjName, highSetPointAttr);
			return true;
		} catch(Exception ex) {
			return false;
		}
	}

	@WebMethod
	public boolean setThermostatFanMode(FanMode fanMode) {
		try {
			ObjectName thermostatObjName = new ObjectName("Aegis.thermostat:name=Thermostat,number=1");
			Attribute fanModeAttr = new Attribute("FanMode", convert(FanModeEnum.class, fanMode));
			server.setAttribute(thermostatObjName, fanModeAttr);
			return true;
		} catch(Exception ex) {
			return false;
		}
	}

	@WebMethod
	public boolean setThermostatHoldMode(HoldMode holdMode) {
		try {
			ObjectName thermostatObjName = new ObjectName("Aegis.thermostat:name=Thermostat,number=1");
			Attribute holdModeAttr = new Attribute("HoldMode", convert(HoldModeEnum.class, holdMode));
			server.setAttribute(thermostatObjName, holdModeAttr);
			return true;
		} catch(Exception ex) {
			return false;
		}
	}

	@WebMethod
	public boolean setThermostatSystemMode(SystemMode systemMode) {
		try {
			ObjectName thermostatObjName = new ObjectName("Aegis.thermostat:name=Thermostat,number=1");
			Attribute systemModeAttr = new Attribute("SystemMode", convert(SystemModeEnum.class, systemMode));
			server.setAttribute(thermostatObjName, systemModeAttr);
			return true;
		} catch(Exception ex) {
			return false;
		}
	}

	@WebMethod
	public UnitStatusResponse[] getUnitStatuses() {
		try {
			Comparator<UnitStatusResponse> comparator = new Comparator<UnitStatusResponse>() {
				@Override
				public int compare(UnitStatusResponse o1, UnitStatusResponse o2) {
					return (o1.getUnitNumber() < o2.getUnitNumber()) ? -1 : 1;
				}				
			};
			Collection<UnitStatusResponse> unitStatusList = new TreeSet<UnitStatusResponse>(comparator);
			
	        // Get all the registered units
			// Use information in Unit MBeans as it should be up-to-date
			Set<ObjectName> instanceList = server.queryNames(new ObjectName("Aegis.unit:*"), null);
	        if (instanceList != null)
	        {
	            for (ObjectName objName : instanceList)
	            {
	                // Get the unit name, number and condition
	                int unitNumber = (Integer) server.getAttribute(objName, "Number");
	                
	                // Is unit?
	                boolean isUnit = (Boolean) server.getAttribute(objName, "Unit");	                
                	if (isUnit) {
		                String unitName = (String) server.getAttribute(objName, "Name");
		                UnitControlEnum condition = (UnitControlEnum) server.getAttribute(objName, "Condition");
						
						// Create response for the current unit
						UnitStatusResponse response = new UnitStatusResponse();
						response.setUnitName(unitName);
						response.setUnitNumber(unitNumber);
						response.setCondition(convert(UnitControl.class, condition));
						
						// Add the unit response to the return list
						unitStatusList.add(response);
                	}
				}	            				
			}
	        
			// Send response
			UnitStatusResponse[] ret = new UnitStatusResponse[unitStatusList.size()];
			unitStatusList.toArray(ret);
			return ret;
		} catch(Exception ex) {
			// Error
			return null;
		}
	}
	
	@WebMethod
	public boolean setUnitControl(int unitNumber, UnitControl condition) {
		try {
			ObjectName unitObjName = getObjectName("unit", unitNumber);
			Attribute conditionAttr = new Attribute("Condition", convert(UnitControlEnum.class, condition));
			server.setAttribute(unitObjName, conditionAttr);
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}
	}
	
	@WebMethod
	public boolean setAllUnitsControl(BasicUnitControl control) {
		try {
			ObjectName systemObjName = new ObjectName("Aegis.system:name=system");
			server.invoke(systemObjName, "controlAllUnits", new Object[] {convert(BasicUnitControlEnum.class, control)}, new String[] {BasicUnitControlEnum.class.getName()});
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}		
	}
	
	@WebMethod
	public ZoneStatusResponse[] getZoneStatuses() {
		try {
			Comparator<ZoneStatusResponse> comparator = new Comparator<ZoneStatusResponse>() {
				@Override
				public int compare(ZoneStatusResponse o1, ZoneStatusResponse o2) {
					return (o1.getZoneNumber() < o2.getZoneNumber()) ? -1 : 1;
				}				
			};
			Collection<ZoneStatusResponse> zoneStatusList = new TreeSet<ZoneStatusResponse>(comparator);
			
	        // Get all the registered zones
			// Use information in Zone MBeans as it should be up-to-date
			Set<ObjectName> instanceList = server.queryNames(new ObjectName("Aegis.zone:*"), null);
	        if (instanceList != null)
	        {
	            for (ObjectName objName : instanceList)
	            {
	                // Get the zone name, number and condition
	                int zoneNumber = (Integer) server.getAttribute(objName, "Number");
	                String zoneName = (String) server.getAttribute(objName, "Name");
	                ZoneConditionEnum condition = (ZoneConditionEnum) server.getAttribute(objName, "Condition");
	                ArmingStatusEnum armingStatus = (ArmingStatusEnum) server.getAttribute(objName, "ArmingStatus");
					LatchedAlarmStatusEnum latchedAlarmStatus = (LatchedAlarmStatusEnum) server.getAttribute(objName, "LatchedAlarmStatus");
					
					// Create response for the current zone
	                ZoneStatusResponse response = new ZoneStatusResponse();
					response.setZoneName(zoneName);
					response.setZoneNumber(zoneNumber);
					response.setCondition(convert(ZoneCondition.class, condition));
					response.setArmingStatus(convert(ArmingStatus.class, armingStatus));
					response.setLatchedAlarmStatus(convert(LatchedAlarmStatus.class, latchedAlarmStatus));
					
					// Add the zone response to the return list
					zoneStatusList.add(response);
				}	            				
			}
	        
			// Send response
	        ZoneStatusResponse[] ret = new ZoneStatusResponse[zoneStatusList.size()];
			zoneStatusList.toArray(ret);
			return ret;
		} catch(Exception ex) {
			// Error
			return null;
		}
	}

	@WebMethod
	public boolean bypassZone(int zoneNumber) {
		try {
			ObjectName zoneObjName = getObjectName("zone", zoneNumber);
			server.invoke(zoneObjName, "bypass", new Object[] {1}, new String[] {"int"});
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}
	}
	
	@WebMethod
	public boolean restoreZone(int zoneNumber) {
		try {
			ObjectName zoneObjName = getObjectName("zone", zoneNumber);
			server.invoke(zoneObjName, "restore", new Object[] {1}, new String[] {"int"});
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}
	}

	@WebMethod
	public ButtonResponse[] getButtons() {
		try {
			Comparator<ButtonResponse> comparator = new Comparator<ButtonResponse>() {
				@Override
				public int compare(ButtonResponse o1, ButtonResponse o2) {
					return (o1.getButtonNumber() < o2.getButtonNumber()) ? -1 : 1;
				}				
			};
			Collection<ButtonResponse> buttonList = new TreeSet<ButtonResponse>(comparator);
	
			Set<ObjectName> instanceList = server.queryNames(new ObjectName("Aegis.button:*"), null);
	        if (instanceList != null)
	        {
		        // Get all the buttons
				// Use information in Unit MBeans as it should be up-to-date
	            for (ObjectName objName : instanceList)
	            {
	                // Get the button name, number and condition
	                int buttonNumber = (Integer) server.getAttribute(objName, "Number");
	                String buttonName = (String) server.getAttribute(objName, "Name");
	                
					// Create response for the current button
	                ButtonResponse response = new ButtonResponse();
					response.setButtonName(buttonName);
					response.setButtonNumber(buttonNumber);
					
					// Add the button response to the return list
					buttonList.add(response);
				}	            				
			}
	        
			// Send response
			ButtonResponse[] ret = new ButtonResponse[buttonList.size()];
			buttonList.toArray(ret);
			return ret;
		} catch(Exception ex) {
			// Error
			return null;
		}
	}
	
	@WebMethod
	public boolean executeButton(int buttonNumber) {
		try {
			ObjectName buttonObjName = getObjectName("button", buttonNumber);
			server.invoke(buttonObjName, "execute", null, null);
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}
	}
	
	@WebMethod
	public MessageStatusResponse[] getMessageStatuses() {
		try {
			Comparator<MessageStatusResponse> comparator = new Comparator<MessageStatusResponse>() {
				@Override
				public int compare(MessageStatusResponse o1, MessageStatusResponse o2) {
					return (o1.getMessageNumber() < o2.getMessageNumber()) ? -1 : 1;
				}				
			};
			Collection<MessageStatusResponse> messageStatusList = new TreeSet<MessageStatusResponse>(comparator);
			
	        // Get all the registered messages
			// Use information in Message MBeans as it should be up-to-date
			Set<ObjectName> instanceList = server.queryNames(new ObjectName("Aegis.message:*"), null);
	        if (instanceList != null)
	        {
	            for (ObjectName objName : instanceList)
	            {
	                // Get the message name, number and state
	                int messageNumber = (Integer) server.getAttribute(objName, "Number");
	                String messageName = (String) server.getAttribute(objName, "Name");
	                boolean displayed = (Boolean) server.getAttribute(objName, "Displayed");
	                
					// Create response for the current message
	                MessageStatusResponse response = new MessageStatusResponse();
					response.setMessageName(messageName);
					response.setMessageNumber(messageNumber);
					response.setDisplayed(displayed);
					
					// Add the message response to the return list
					messageStatusList.add(response);
				}	            				
			}
	        
			// Send response
	        MessageStatusResponse[] ret = new MessageStatusResponse[messageStatusList.size()];
	        messageStatusList.toArray(ret);
			return ret;
		} catch(Exception ex) {
			// Error
			return null;
		}
	}

	@WebMethod
	public boolean showMessage(int messageNumber) {
		try {
			ObjectName messageObjName = getObjectName("message", messageNumber);
			server.invoke(messageObjName, "show", null, null);
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}
	}

	@WebMethod
	public boolean playMessage(int messageNumber, int dialPhoneNumber) {
		try {
			ObjectName messageObjName = getObjectName("message", messageNumber);
			server.invoke(messageObjName, "play", new Object[] {dialPhoneNumber}, new String[] {"int"});
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}
	}
	
	@WebMethod
	public boolean logMessage(int messageNumber) {
		try {
			ObjectName messageObjName = getObjectName("message", messageNumber);
			server.invoke(messageObjName, "log", null, null);
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}
	}

	@WebMethod
	public boolean clearMessage(int messageNumber) {
		try {
			ObjectName messageObjName = getObjectName("message", messageNumber);
			server.invoke(messageObjName, "clear", null, null);
			return true;
		} catch (Exception ex) {
			// Error
			return false;
		}
	}

	@WebMethod
	public EventLogResponse[] getEventLog(boolean update) {
		try {
			// Update event log then get new entries
			ObjectName eventLogObjName = new ObjectName(EventLogMBean.NAME);
			if (update) {
				server.invoke(eventLogObjName, "updateStatus", new Object[0], new String[0]);
			}
			UploadEventLogMessageReport.EventLogInfo[] eventLogInfoList = (UploadEventLogMessageReport.EventLogInfo[]) server.getAttribute(eventLogObjName, "Entries");

			// Create the response: a list of event log entries
			Collection<EventLogResponse> eventLogEntries = new LinkedHashSet<EventLogResponse>();
			for (UploadEventLogMessageReport.EventLogInfo eventLogInfo : eventLogInfoList) {
				EventLogResponse eventLogEntry = new EventLogResponse();
				Date date = eventLogInfo.getDate();
				long time = (date != null) ? date.getTime() : 0;
				eventLogEntry.setDate(time);
				eventLogEntry.setEventType(convert(EventType.class, eventLogInfo.getType()));
				eventLogEntry.setP1(eventLogInfo.getP1());
				eventLogEntry.setP2(eventLogInfo.getP2());
				eventLogEntries.add(eventLogEntry);
			}
			
			// Send response
			EventLogResponse[] ret = new EventLogResponse[eventLogEntries.size()];
			eventLogEntries.toArray(ret);			
			return ret;
		} catch (Exception ex) {
			// Error
			return null;
		}	
	}
	
	private static <E extends java.lang.Enum<E>> E convert(Class<E> enumType, net.homeip.mleclerc.omnilink.enumeration.Enum selectedEnum) throws IllegalArgumentException, IllegalAccessException {
		Field[] enumMembers = selectedEnum.getClass().getFields();
		for (int i = 0; i < enumMembers.length; i++) {
			if (enumMembers[i].get(selectedEnum) == selectedEnum) {
				return java.lang.Enum.valueOf(enumType, enumMembers[i].getName());
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static  <E extends net.homeip.mleclerc.omnilink.enumeration.Enum> E convert(Class<E> enumType, java.lang.Enum<?> selectedEnum) throws IllegalArgumentException, IllegalAccessException {
		Field[] enumMembers = enumType.getFields();
		for (int i = 0; i < enumMembers.length; i++) {
			if (enumMembers[i].getName().equals(selectedEnum.toString())) {
				return (E) enumMembers[i].get(enumType);
			}
		}
		return null;
	}
	
	private ObjectName getObjectName(String componentType, int number) throws MalformedObjectNameException {
        ObjectName compObjPartialName = new ObjectName(ComponentMBean.DOMAIN_BASE + "." + componentType + ":number=" + number + ",*");
		Set<ObjectName> compObjList = server.queryNames(compObjPartialName, null);
		if (compObjList.size() == 1) {
			return compObjList.iterator().next();
		} else {
			throw new IllegalStateException();
		}
	}
	
	private SystemStatus getSystemStatus(SystemEventInfo systemEvent) throws Exception {
		SystemEventTypeEnum systemEventType = systemEvent.getType();
		if (systemEventType.equals(SystemEventTypeEnum.ALARM_ACTIVATION) || 
			systemEventType.equals(SystemEventTypeEnum.SECURITY_ARMING) ||
			systemEventType.equals(SystemEventTypeEnum.PHONE_LINE_DEAD) ||
			systemEventType.equals(SystemEventTypeEnum.AC_POWER_OFF) ||
			systemEventType.equals(SystemEventTypeEnum.BATTERY_LOW) ||
			systemEventType.equals(SystemEventTypeEnum.DCM_TROUBLE)) {
			return convert(SystemStatus.class, systemEventType);			
		} else {
			return SystemStatus.SYSTEM_OK;
		}
	}
}
