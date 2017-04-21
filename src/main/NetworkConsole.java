import misc.Console;
import net.homeip.mleclerc.omnilink.CommunicationException;
import net.homeip.mleclerc.omnilink.NetworkCommunication;
import net.homeip.mleclerc.omnilink.enumeration.ProtocolTypeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemTypeEnum;
import net.homeip.mleclerc.omnilink.messagebase.Message;

public class NetworkConsole
{
    public static void main(String[] args) throws CommunicationException
    {
    	if (args.length == 5)
        {
	    	// Host
	        String host = args[0];
	
	        // Port
	        int port = Integer.parseInt(args[1]);
	
	        // Private key
	        String privateKey = args[2];
	        
	        // System type
	        String systemTypeStr = args[3];
	        int systemTypeVal = Integer.parseInt(systemTypeStr);
	        SystemTypeEnum systemType = (SystemTypeEnum) SystemTypeEnum.metaInfo.getByValue(systemTypeVal);
	        
	        // Protocol type
	        String protocolTypeStr = args[4];
	        int protocolTypeVal = Integer.parseInt(protocolTypeStr);
	        ProtocolTypeEnum protocolType = (ProtocolTypeEnum) ProtocolTypeEnum.metaInfo.getByValue(protocolTypeVal);
	        
	        // Get the remote service
	    	NetworkCommunication comm = new NetworkCommunication(systemType, host, port, NetworkCommunication.DEFAULT_TIMEOUT, privateKey, protocolType);
	    	if (protocolType == ProtocolTypeEnum.HAI_OMNI_LINK_II) {
	    		// Register a listener to receive notifications from the controller
		        comm.addListener(new NetworkCommunication.NotificationListener() {
					public void notify(Message notification) {
						System.out.println("Notification: " + notification);
					}
				});
	    	}
	        Console cons = new Console(comm);
	        cons.run();
        }
    	else 
    	{
    		System.err.println("Usage: NetworkConsole <host> <port> <private-key> <system-type> <protocol-type>");
    	}
    }
}
