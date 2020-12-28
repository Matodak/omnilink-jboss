package mbean;

import org.jboss.system.ServiceMBean;


public interface ControllerMBean extends ServiceMBean
{
    // Login code: w/o
    public void setLoginCode(String loginCode);

    // Update period: r/w
    public int getUpdatePeriod();
    public void setUpdatePeriod(int updatePeriod);

    // Login/logout operations: action
    public void login();
    public void logout();
    public boolean isLoggedIn();

    // First house code: r/w
    public String getFirstHouseCode();
    public void setFirstHouseCode(String houseCode);
}