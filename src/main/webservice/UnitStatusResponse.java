package webservice;

public class UnitStatusResponse {

	private int unitNumber;
	private String unitName;
	private UnitControl condition;
	
	public void setUnitNumber(int unitNumber) {
		this.unitNumber = unitNumber;
	}
	
	public int getUnitNumber() {
		return unitNumber;
	}
	
	public UnitControl getCondition() {
		return condition;
	}
	
	public void setCondition(UnitControl condition) {
		this.condition = condition;
	}
	
	public String getUnitName() {
		return unitName;
	}
	
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
}
