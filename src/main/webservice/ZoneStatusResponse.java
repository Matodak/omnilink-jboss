package webservice;

public class ZoneStatusResponse {

	private int zoneNumber;
	private String zoneName;
	private ZoneCondition condition;
	private ArmingStatus armingStatus;
	private LatchedAlarmStatus latchedAlarmStatus;
	
	public ArmingStatus getArmingStatus() {
		return armingStatus;
	}

	public void setArmingStatus(ArmingStatus armingStatus) {
		this.armingStatus = armingStatus;
	}

	public ZoneCondition getCondition() {
		return condition;
	}

	public void setCondition(ZoneCondition condition) {
		this.condition = condition;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public int getZoneNumber() {
		return zoneNumber;
	}

	public void setZoneNumber(int zoneNumber) {
		this.zoneNumber = zoneNumber;
	}
	
	public LatchedAlarmStatus getLatchedAlarmStatus() {
		return latchedAlarmStatus;
	}

	public void setLatchedAlarmStatus(LatchedAlarmStatus latchAlarmStatus) {
		this.latchedAlarmStatus = latchAlarmStatus;
	}	
}
