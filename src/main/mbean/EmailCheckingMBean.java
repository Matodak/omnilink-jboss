package mbean;

import org.jboss.system.ServiceMBean;


public interface EmailCheckingMBean extends ServiceMBean
{
    // Update period: r/w
    public int getUpdatePeriod();
    public void setUpdatePeriod(int updatePeriod);

    // Email send/receive settings: r/w
    public String getEmailTo();
    public void setEmailTo(String emailTo);
    public String getEmailFrom();
    public void setEmailFrom(String from);

    // Filter criterias: r/w
    public String getFromAddresses();
    public void setFromAddresses(String fromAddresses);
    public String getControlTitle();
    public void setControlTitle(String title);

    // Send email: action
    public void sendEmail(String title, String msgText);
}