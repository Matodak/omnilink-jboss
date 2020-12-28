package mbean;

import net.homeip.mleclerc.omnilink.message.UploadEventLogMessageReport;

public interface EventLogMBean extends ComponentMBean
{
	String NAME =  ComponentMBean.DOMAIN_BASE + ".eventlog:name=eventlog";
	
    // Log entries: r/o
    public UploadEventLogMessageReport.EventLogInfo[] getEntries();
}