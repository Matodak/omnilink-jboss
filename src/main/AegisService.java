import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import net.homeip.mleclerc.omnilink.CommunicationService;
import net.homeip.mleclerc.omnilink.SerialCommunication;
import net.homeip.mleclerc.omnilink.enumeration.SystemTypeEnum;

public class AegisService
{
    public static void main(String[] args)
    {
        // Determine the RMI registry port
        int rmiRegistryPort;
        if (args.length == 1)
            rmiRegistryPort = Integer.parseInt(args[0]);
        else
            rmiRegistryPort = Registry.REGISTRY_PORT;

        try
        {
            // Instantiate the registry object
            Registry reg = LocateRegistry.createRegistry(rmiRegistryPort);

            // Instantiate the net.homeip.mleclerc.omnilink communication object
            CommunicationService service = new CommunicationService(new SerialCommunication(SystemTypeEnum.AEGIS_2000, "COM1", 9600));

            // Make the object available through the registry
            reg.rebind(CommunicationService.SERVICE_NAME, service);

            System.out.println("Service started on port: " + rmiRegistryPort);
        }
        catch(RemoteException re)
        {
            re.printStackTrace();
        }
    }
}
