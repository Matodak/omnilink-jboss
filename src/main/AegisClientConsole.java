import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import misc.Console;
import net.homeip.mleclerc.omnilink.MessageManager;
import net.homeip.mleclerc.omnilink.MessageManagerRemote;

public class AegisClientConsole
{
    public static void main(String[] args)
    {
        // Host
        String host;
        if (args.length >= 1)
            host = args[0];
        else
            host = "localhost";

        // RMI registry port
        int rmiRegistryPort;
        if (args.length >= 2)
            rmiRegistryPort = Integer.parseInt(args[1]);
        else
            rmiRegistryPort = Registry.REGISTRY_PORT;

        try
        {
            // Get the remote service
            Registry reg = LocateRegistry.getRegistry(host, rmiRegistryPort);
            MessageManager service = (MessageManager) reg.lookup(MessageManagerRemote.SERVICE_NAME);
            if (service != null)
            {
                // Create the console
                Console console = new Console(service);
                console.run();
            }
            else
            {
                System.err.println("Service not available");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
