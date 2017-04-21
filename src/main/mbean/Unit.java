package mbean;

import net.homeip.mleclerc.omnilink.enumeration.UnitControlEnum;
import net.homeip.mleclerc.omnilink.message.UnitCommand;
import net.homeip.mleclerc.omnilink.message.UnitStatusReport;
import net.homeip.mleclerc.omnilink.message.UnitStatusRequest;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

public class Unit extends NumberedComponentMBeanSupport implements UnitMBean
{
    private int remainingTime;
    private UnitControlEnum condition;
    private boolean on;
    private boolean isUnit;

    public Unit(Integer number, String name)
    {
        super(number, name);
    }

    public int getRemainingTime()
    {
        return remainingTime;
    }

    private void setRemainingTime(int remainingTime)
    {
        if (remainingTime != this.remainingTime)
        {
            sendAttrChangeNotif("RemainingTime", Integer.class.getName(), this.remainingTime, remainingTime);
            this.remainingTime = remainingTime;
        }
    }

    public UnitControlEnum getCondition()
    {
        return condition;
    }

    public void setCondition(UnitControlEnum condition) throws Exception {
		// Issue request
		RequestMessage request = new UnitCommand(getNumber(), condition);
		Utils.sendMessage(server, request);
		
		// Set the attribute value
		setConditionAttr(condition);
    }
    
    @Override
	public void overrideConditionAttr(UnitControlEnum condition)
			throws Exception {
    	setConditionAttr(condition);
	}

	private void setConditionAttr(UnitControlEnum condition) {
        if (!condition.equals(this.condition)) {
            sendAttrChangeNotif("Condition", UnitControlEnum.class.getName(), this.condition, condition);
			this.condition = condition;
        }
    }
    
    public boolean getUnitOn()
    {
        return on;
    }

    public void setUnitOn(boolean on)
    {
        if (on != this.on)
        {
            sendAttrChangeNotif("UnitOn", Boolean.class.getName(), this.on, on);
            this.on = on;
        }
    }

    public boolean isUnit()
    {
        return isUnit;
    }

    private void setUnit(boolean isUnit)
    {
        if (isUnit != this.isUnit)
        {
            sendAttrChangeNotif("Unit", Boolean.class.getName(), this.isUnit, isUnit);
            this.isUnit = isUnit;
        }
    }

    public void updateStatus()
    {
        try
        {
            // Get the unit status
            UnitStatusReport unitStatusReply = (UnitStatusReport) sendMessage(new UnitStatusRequest(getNumber()));
            UnitStatusReport.UnitStatusInfo status = (UnitStatusReport.UnitStatusInfo) unitStatusReply.getInfo(getNumber());
            setConditionAttr(status.getCondition());
            setRemainingTime(status.getRemainingTime());
            setUnit(status.isUnit());

            // Determine, given the current condition, whether the unit is on or not
            boolean on = !status.getCondition().equals(UnitControlEnum.OFF);
            setUnitOn(on);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
