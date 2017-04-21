package webservice;

public class SystemStatusResponse {
	
	private SecurityMode securityMode;
	private long date;
	private long sunrise;
	private long sunset;
	private PhoneLineStatus phoneLineStatus;
	private SystemStatus systemStatus;
	
	public void setDate(long date) {
		this.date = date;
	}
	
	public long getDate() {
		return date;
	}

	public long getSunrise() {
		return sunrise;
	}
	
	public void setSunrise(long sunrise) {
		this.sunrise = sunrise;
	}
	
	public long getSunset() {
		return sunset;
	}
	
	public void setSunset(long sunset) {
		this.sunset = sunset;
	}
	
	public PhoneLineStatus getPhoneLineStatus() {
		return phoneLineStatus;
	}
	
	public void setPhoneLineStatus(PhoneLineStatus phoneLineStatus) {
		this.phoneLineStatus = phoneLineStatus;
	}
	
	public void setSecurityMode(SecurityMode securityMode) {
		this.securityMode = securityMode;
	}
	
	public SecurityMode getSecurityMode() {
		return securityMode;
	}

	public SystemStatus getSystemStatus() {
		return systemStatus;
	}

	public void setSystemStatus(SystemStatus systemStatus) {
		this.systemStatus = systemStatus;
	}
}
