package mbean.eventhandlers;

import java.util.Set;

import javax.management.Attribute;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;

import mbean.ComponentMBean;
import mbean.Utils;
import net.homeip.mleclerc.omnilink.message.SystemEventsReport;
import net.homeip.mleclerc.omnilink.messagebase.ReplyMessage;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

public abstract class SystemEventHandler 
{	
	private MBeanServer server;
	
	protected MBeanServer getServer()
	{
		return server;
	}
	
	public void setServer(MBeanServer server)
	{
		this.server = server;
	}

	public abstract boolean canHandleEvent(SystemEventsReport.SystemEventInfo event) throws Exception;
	
	public abstract void handleEvent(SystemEventsReport.SystemEventInfo event) throws Exception;

	protected ReplyMessage sendCommand(RequestMessage command) throws Exception
	{
		return Utils.sendMessage(server, command);
	}

	protected ObjectName getObjectName(String compType, int compNo) throws OperationsException
	{
        String name = ComponentMBean.DOMAIN_BASE + "." + compType;
        if (compNo >= 1)
            name += ":number=" + compNo + ",*";
        else
            name += ":*";
        ObjectName compName = new ObjectName(name);
		Set compMBeanList = server.queryNames(compName, null);
		if (compMBeanList.size() == 1)
		{        	
			Object onlyItem = compMBeanList.iterator().next();
			return (ObjectName) onlyItem;
		}
		
		return null;
	}
	
	protected Object getAttribute(String compType, int compNo, String attrName) throws OperationsException, MBeanException, ReflectionException
	{
		ObjectName objName = getObjectName(compType, compNo);
		return getServer().getAttribute(objName, attrName);
	}
	
	protected void setAttribute(String compType, int compNo, String attrName, Object attrValue) throws OperationsException, MBeanException, ReflectionException
	{
		ObjectName objName = getObjectName(compType, compNo);
		getServer().setAttribute(objName, new Attribute(attrName, attrValue));
	}
}
