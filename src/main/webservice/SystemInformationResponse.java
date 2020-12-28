package webservice;

public class SystemInformationResponse {

	private String version;
	private String localPhoneNumber;
	private SystemType systemType;
	private String[] dialPhoneNumbers;
	
	public SystemType getSystemType() {
		return systemType;
	}

	public void setSystemType(SystemType systemType) {
		this.systemType = systemType;
	}

	public String getLocalPhoneNumber() {
		return localPhoneNumber;
	}

	public void setLocalPhoneNumber(String localPhoneNumber) {
		this.localPhoneNumber = localPhoneNumber;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}

	public String[] getDialPhoneNumbers() {
		return dialPhoneNumbers;
	}

	public void setDialPhoneNumbers(String[] dialPhoneNumbers) {
		this.dialPhoneNumbers = dialPhoneNumbers;
	}
}
