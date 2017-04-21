package mbean;

import net.homeip.mleclerc.omnilink.MessageManager;
import net.homeip.mleclerc.omnilink.messagebase.ReplyMessage;
import net.homeip.mleclerc.omnilink.messagebase.RequestMessage;

import org.jboss.system.ServiceMBean;

public interface CommunicationMBean extends ServiceMBean
{
    String NAME = ComponentMBean.DOMAIN_BASE + ":name=communication";

    // Model type: r/w
    public int getSystemType();
    public void setSystemType(int systemType);

    // RMI registry port: r/w
    public int getRmiRegistryPort();
    public void setRmiRegistryPort(int port);

    // RMI service port (service to clients): r/w
    public int getRmiServicePort();
    public void setRmiServicePort(int port);

    // Communication Port: r/w
    public String getCommPort();
    public void setCommPort(String commPort);

    // Initialization command: r/w
    public String getInitCmd();
    public void setInitCmd(String initStr);

    // Local access command: r/w
    public String getLocalAccessCmd();
    public void setLocalAccessCmd(String localAccessCmd);

    // Baud rate: r/w
    public int getBaudRate();
    public void setBaudRate(int baudRate);

    // RMI registry host: r/w
    public void setRmiRegistryHost(String host);
    public String getRmiRegistryHost();
    
    // Network IP address: r/w
    public void setNetworkIpAddress(String networkIpAddress);
    public String getNetworkIpAddress();
    
    // Network port: r/w
    public void setNetworkPort(int port);
    public int getNetworkPort();
    
    // Encryption key: r/w
    public String getEncryptionKey();
    public void setEncryptionKey(String encryptionKey);
    
    // Send message to controller: action
    public ReplyMessage sendMessage(RequestMessage msg) throws Exception;

    // Open the connection: action
    public void open();
    public boolean isOpen();

    // Close the connection: action
    public void close();

    // Handle to the message manager: r/o
    public MessageManager getMessageManager();
}
