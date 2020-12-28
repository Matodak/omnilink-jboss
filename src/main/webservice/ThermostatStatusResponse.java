package webservice;

public class ThermostatStatusResponse {

	private double currentTemperature;
    private double lowSetPoint;
    private double highSetPoint;
    private FanMode fanMode;
    private HoldMode holdMode;
    private SystemMode systemMode;
    
	public FanMode getFanMode() {
		return fanMode;
	}

	public void setFanMode(FanMode fanMode) {
		this.fanMode = fanMode;
	}

	public HoldMode getHoldMode() {
		return holdMode;
	}

	public void setHoldMode(HoldMode holdMode) {
		this.holdMode = holdMode;
	}

	public SystemMode getSystemMode() {
		return systemMode;
	}

	public void setSystemMode(SystemMode systemMode) {
		this.systemMode = systemMode;
	}

	public void setCurrentTemperature(double currentTemperature) {
		this.currentTemperature = currentTemperature;
	}
	
	public double getCurrentTemperature() {
		return currentTemperature;
	}
	
	public void setLowSetPoint(double lowSetPoint) {
		this.lowSetPoint = lowSetPoint;
	}
	
    public double getLowSetPoint() {
        return lowSetPoint;
    }

	public void setHighSetPoint(double highSetPoint) {
		this.highSetPoint = highSetPoint;
	}

    public double getHighSetPoint() {
        return highSetPoint;
    }
}
