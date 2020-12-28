package mbean;

import net.homeip.mleclerc.omnilink.enumeration.UnitControlEnum;

public interface UnitMBean extends NumberedComponentMBean
{
    // Remaining time: r/o
    int getRemainingTime();

    // Does it represent a unit?
    boolean isUnit();
    
    // Unit condition: r/w
    UnitControlEnum getCondition();
    void setCondition(UnitControlEnum condition) throws Exception;
    void overrideConditionAttr(UnitControlEnum condition) throws Exception;
    
    // Unit state: r/w
    void setUnitOn(boolean on);
    boolean getUnitOn();
}
