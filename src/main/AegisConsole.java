import misc.Console;
import net.homeip.mleclerc.omnilink.SerialCommunication;
import net.homeip.mleclerc.omnilink.enumeration.SystemTypeEnum;

public class AegisConsole
{
    public static void main(String[] args)
    {
        //Communication comm = new Communication(SystemTypeEnum.AEGIS, "COM3", 9600, "AT&FX0S0=0S10=20&C1&D2", "ATS11=150DT###11710");
        SerialCommunication comm = new SerialCommunication(SystemTypeEnum.AEGIS_2000, "COM1", 9600);
        Console cons = new Console(comm);
        cons.run();
    }
}
