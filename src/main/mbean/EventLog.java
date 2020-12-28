package mbean;

import java.util.Collection;

import net.homeip.mleclerc.omnilink.message.UploadEventLogMessageReport;
import net.homeip.mleclerc.omnilink.message.UploadEventLogMessageRequest;

public class EventLog extends ComponentMBeanSupport implements EventLogMBean
{
    private UploadEventLogMessageReport.EventLogInfo[] entries = null;

    public EventLog()
    {
    }

    public UploadEventLogMessageReport.EventLogInfo[] getEntries()
    {
        return entries;
    }

    private void setEntries(UploadEventLogMessageReport.EventLogInfo[] entries)
    {
        if (entries != this.entries)
        {
            sendAttrChangeNotif("Entries", UploadEventLogMessageReport.EventLogInfo[].class.getName(), this.entries, entries);
            this.entries = entries;
        }
    }

	public void updateStatus()
    {
        try
        {
            UploadEventLogMessageReport reply = (UploadEventLogMessageReport)
                    sendMessage(new UploadEventLogMessageRequest());

            Collection<UploadEventLogMessageReport.EventLogInfo> infoList = reply.getInfoList();
            UploadEventLogMessageReport.EventLogInfo[] entries = new UploadEventLogMessageReport.EventLogInfo[infoList.size()];
            infoList.toArray(entries);
            setEntries(entries);
       }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
