import java.io.BufferedReader;
import java.io.FileReader;

import misc.Console;
import net.homeip.mleclerc.omnilink.NetworkCommunication;
import net.homeip.mleclerc.omnilink.enumeration.ProtocolTypeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemTypeEnum;

public class NetworkCommandReader
{
    public static void main(String[] args)
    {
        try
        {
            if (args.length == 6)
            {
                // Get the filename
                String filename = args[0];

            	// Host
                String host = args[1];

                // Port
                int port = Integer.parseInt(args[2]);

                // Private key
                String privateKey = args[3];
                
                // System type
                String systemTypeStr = args[4];
                int systemTypeVal = Integer.parseInt(systemTypeStr);
                SystemTypeEnum systemType = (SystemTypeEnum) SystemTypeEnum.metaInfo.getByValue(systemTypeVal);
                
                // Protocol type
                String protocolTypeStr = args[5];
                int protocolTypeVal = Integer.parseInt(protocolTypeStr);
                ProtocolTypeEnum protocolType = (ProtocolTypeEnum) ProtocolTypeEnum.metaInfo.getByValue(protocolTypeVal);
                
                // Get the remote service
            	NetworkCommunication comm = new NetworkCommunication(systemType, host, port, NetworkCommunication.DEFAULT_TIMEOUT, privateKey, protocolType);
            	
                // Open the file
                BufferedReader reader = new BufferedReader(new FileReader(filename));

                // Create the console
                Console console = new Console(comm);
                console.run(reader);
            }
            else
            {
            	System.err.println("Usage: NetworkCommandReader <input file> <host> <port> <private-key> <system-type> <protocol-type>");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
