import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import misc.Console;
import net.homeip.mleclerc.omnilink.MessageManager;
import net.homeip.mleclerc.omnilink.MessageManagerRemote;

public class AegisCommandReader
{
    public static void main(String[] args)
    {
        try
        {
            if (args.length > 0)
            {
                // Get the filename
                String filename = args[0];

                // Host
                String host;
                if (args.length >= 2)
                    host = args[1];
                else
                    host = "localhost";

                // RMI registry port
                int rmiRegistryPort;
                if (args.length >= 3)
                    rmiRegistryPort = Integer.parseInt(args[2]);
                else
                    rmiRegistryPort = Registry.REGISTRY_PORT;

                // Get the remote service
                Registry reg = LocateRegistry.getRegistry(host, rmiRegistryPort);
                MessageManager service = (MessageManager) reg.lookup(MessageManagerRemote.SERVICE_NAME);
                if (service != null)
                {
                    // Open the file
                    BufferedReader reader = new BufferedReader(new FileReader(filename));

                    // Create the console
                    Console console = new Console(service);
                    console.run(reader);
                }
                else
                {
                    System.err.println("Service not available");
                }
            }
            else
            {
                System.err.println("Usage: AegisCommandReader <input file> [<host>] [<rmi port>]");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
