package mbean;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

import net.homeip.mleclerc.omnilink.CommunicationService;
import net.homeip.mleclerc.omnilink.MessageManager;
import net.homeip.mleclerc.omnilink.MessageManagerRemote;
import net.homeip.mleclerc.omnilink.NetworkCommunication;
import net.homeip.mleclerc.omnilink.SerialCommunication;
import net.homeip.mleclerc.omnilink.enumeration.SystemTypeEnum;
import net.homeip.mleclerc.omnilink.messagebase.ReplyMessage;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

import org.jboss.system.ServiceMBeanSupport;

public class Communication extends ServiceMBeanSupport implements CommunicationMBean
{
	private final static int SOCKET_TIMEOUT = 5;
	
    private SystemTypeEnum modelType = null;
    private int rmiRegistryPort = 0;
    private int baudRate = 0;
    private String commPort = null;
    private String localAccessCmd = null;
    private String initCmd = null;
    private int rmiServicePort = 0;
    private String rmiRegistryHost = null;
    private MessageManager comm = null;
    private Registry reg  = null;
    private String networkIpAddress;
    private int networkPort;
    private String encryptionKey;
    
    public Communication()
    {
    }

    protected void startService()
    {
        open();
    }

    protected void stopService()
    {
        close();
    }

    public int getSystemType()
    {
        return modelType.getValue();
    }

    public void setSystemType(int modelTypeVal)
    {
        this.modelType = (SystemTypeEnum) SystemTypeEnum.metaInfo.getByValue(modelTypeVal);
    }

    public int getRmiRegistryPort()
    {
        return rmiRegistryPort;
    }

    public void setRmiRegistryPort(int rmiRegistryPort)
    {
        this.rmiRegistryPort = rmiRegistryPort;
    }

    public int getRmiServicePort()
    {
        return rmiServicePort;
    }

    public void setRmiServicePort(int rmiServicePort)
    {
        this.rmiServicePort = rmiServicePort;
    }

    public String getCommPort()
    {
        return commPort;
    }

    public void setCommPort(String commPort)
    {
        this.commPort = commPort;
    }

    public String getInitCmd()
    {
        return initCmd;
    }

    public void setInitCmd(String initCmd)
    {
        this.initCmd = initCmd;
    }

    public int getBaudRate()
    {
        return baudRate;
    }

    public void setBaudRate(int baudRate)
    {
        this.baudRate = baudRate;
    }

    public String getLocalAccessCmd()
    {
        return localAccessCmd;
    }

    public void setLocalAccessCmd(String localAccessCmd)
    {
        this.localAccessCmd = localAccessCmd;
    }

    public void setRmiRegistryHost(String host)
    {
    	this.rmiRegistryHost = host;
    }
    
    public String getRmiRegistryHost()
    {
    	return rmiRegistryHost;
    }
    
    public String getNetworkIpAddress()
    {
    	return networkIpAddress;
    }

    public void setNetworkIpAddress(String networkIpAddress)
    {
    	this.networkIpAddress = networkIpAddress;
    }

    public int getNetworkPort()
    {
    	return networkPort;
    }

    public void setNetworkPort(int port)
    {
    	this.networkPort = port;
    }

    public String getEncryptionKey()
    {
    	return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey)
    {
    	this.encryptionKey = encryptionKey;
    }

    public ReplyMessage sendMessage(RequestMessage msg) throws Exception
    {
        if (comm != null)
            return comm.execute(msg);
        else
            throw new IllegalStateException("The communication MBean is not properly configured");
    }

    public void stopModule()
    {
        close();
    }

    public void open()
    {
        try
        {
            // Close the connection
            close();

            if (rmiRegistryHost != null && rmiRegistryPort > 0)
            {
            	// RMI connection
            	
            	// Get a reference to the communication client
            	Registry reg = LocateRegistry.getRegistry(rmiRegistryHost, rmiRegistryPort);
                comm = (MessageManager) reg.lookup(MessageManagerRemote.SERVICE_NAME);
            }
            else if (networkIpAddress != null && networkIpAddress.length() > 0 && networkPort > 0 && encryptionKey != null && encryptionKey.length() > 0)
            {
            	// Network connection
            	
	            if (rmiRegistryPort > 0)
	            {
	                // Creation of an RMI service
	
	                // Instantiate the registry object
	                try
	                {
	                    reg = LocateRegistry.createRegistry(rmiRegistryPort);
	                }
	                catch(ExportException ee)
	                {
	                    // The registry already exists
	                    reg = LocateRegistry.getRegistry(rmiRegistryPort);
	                }
	
	                // Instantiate the net.homeip.mleclerc.omnilink communication object
	                MessageManagerRemote msgmgr = new CommunicationService(new NetworkCommunication(modelType, networkIpAddress, networkPort, SOCKET_TIMEOUT, encryptionKey), rmiServicePort);
	
	                // Make the object available through the registry
	                reg.bind(CommunicationService.SERVICE_NAME, msgmgr);
	
	                // Keep a reference to the service
	                comm = msgmgr;
	            }
	            else
	            {
	                // Create and open a new connection
	                comm = new NetworkCommunication(modelType, networkIpAddress, networkPort, SOCKET_TIMEOUT, encryptionKey);
	            }
            }
            else if (commPort != null && commPort.length() > 0 && baudRate > 0)
            {
            	// Serial connection

	            if (rmiRegistryPort > 0)
	            {
	                // Creation of an RMI service
	
	                // Instantiate the registry object
	                try
	                {
	                    reg = LocateRegistry.createRegistry(rmiRegistryPort);
	                }
	                catch(ExportException ee)
	                {
	                    // The registry already exists
	                    reg = LocateRegistry.getRegistry(rmiRegistryPort);
	                }
	
	                // Instantiate the net.homeip.mleclerc.omnilink communication object
	                MessageManagerRemote msgmgr = new CommunicationService(new SerialCommunication(modelType, commPort, baudRate, initCmd, localAccessCmd), rmiServicePort);
	
	                // Make the object available through the registry
	                reg.bind(CommunicationService.SERVICE_NAME, msgmgr);
	
	                // Keep a reference to the service
	                comm = msgmgr;
	            }
	            else
	            {
	                // Create and open a new connection
	                comm = new SerialCommunication(modelType, commPort, baudRate, initCmd, localAccessCmd);
	            }
            }
            else
            {
            	// Error
            	throw new IllegalStateException("No communication defined in configuration");
            }

            // Open the connection
            if (comm != null)
            {
        		comm.open();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void close()
    {
        try
        {
            if (reg != null)
            {
                reg.unbind(CommunicationService.SERVICE_NAME);
                reg = null;
            }

            if (comm != null)
            {
        		comm.close();
            	comm = null;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isOpen()
    {
        try
        {
            if (comm != null)
                return comm.isOpen();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    public MessageManager getMessageManager()
    {
        return comm;
    }
}
