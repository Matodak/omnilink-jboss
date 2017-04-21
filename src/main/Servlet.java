import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import misc.Console;
import net.homeip.mleclerc.omnilink.CommunicationException;
import net.homeip.mleclerc.omnilink.MessageManager;
import net.homeip.mleclerc.omnilink.MessageManagerRemote;
import net.homeip.mleclerc.omnilink.NetworkCommunication;
import net.homeip.mleclerc.omnilink.enumeration.ProtocolTypeEnum;
import net.homeip.mleclerc.omnilink.enumeration.SystemTypeEnum;

public class Servlet extends HttpServlet
{
    private final static String TITLE = "Aegis Web Console";
    private final static String LN_SEPARATOR = System.getProperty("line.separator");
    private Console console;

    public void init() throws ServletException
    {
        try
        {
            try
            {
            	// Get the network host, port, etc.
                String host = getServletConfig().getInitParameter("host");
                String portStr = getServletConfig().getInitParameter("port");
                String privateKey = getServletConfig().getInitParameter("private-key");
                String systemTypeStr = getServletConfig().getInitParameter("system-type");
                String protocolTypeStr = getServletConfig().getInitParameter("protocol-type");
                if (host != null && portStr != null && privateKey != null && systemTypeStr != null && protocolTypeStr != null)
                {
                    int port = Integer.parseInt(portStr);
                    int systemTypeVal = Integer.parseInt(systemTypeStr);
                    SystemTypeEnum systemType = (SystemTypeEnum) SystemTypeEnum.metaInfo.getByValue(systemTypeVal);
                    int protocolTypeVal = Integer.parseInt(protocolTypeStr);
                    ProtocolTypeEnum protocolType = (ProtocolTypeEnum) ProtocolTypeEnum.metaInfo.getByValue(protocolTypeVal);
                	NetworkCommunication comm = new NetworkCommunication(systemType, host, port, NetworkCommunication.DEFAULT_TIMEOUT, privateKey, protocolType);

                    // Instantiate the console
                    console = new Console(comm);
                }
            	
                // Get the RMI registry host name and port
                String rmiHost = getServletConfig().getInitParameter("rmiHost");
                String rmiPortStr = getServletConfig().getInitParameter("rmiPort");

                if (rmiHost != null && rmiPortStr != null)
                {
                    int rmiPort = Integer.parseInt(rmiPortStr);

                    // Get a reference to the service
                    Registry reg = LocateRegistry.getRegistry(rmiHost, rmiPort);
                    MessageManager msgmgr = (MessageManager) reg.lookup(MessageManagerRemote.SERVICE_NAME);

                    // Instantiate the console
                    console = new Console(msgmgr);
                }
            }
            catch (CommunicationException ex)
            {
                ex.printStackTrace();
            }
            catch (NotBoundException ex)
            {
                ex.printStackTrace();
            }
        }
        catch(RemoteException re)
        {
            re.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter out;

        // set content type
        response.setContentType("text/html");

        // retrieve the response stream writer
        out = response.getWriter();

        String output = (String) request.getAttribute("Servlet.output");
        if (output == null)
            output = "";

        // Write html content to the response stream
        out.println("<HTML><HEAD><TITLE>");
        out.println(TITLE);
        out.println("</TITLE></HEAD><BODY ONLOAD=\"document.form.command.focus()\">");
        out.println("<H1>" + TITLE + "</H1>");
        out.println("<FORM ACTION=Servlet NAME=form METHOD=POST>");
        out.println(
                "<P>Please enter commands into the command input box:<BR/> " +
                LN_SEPARATOR + "<TABLE BORDER=0>" + LN_SEPARATOR +
                "  <TR><TD>Command:</TD> <TD><INPUT TYPE=text NAME=command SIZE=80 />" +
                "&nbsp;<INPUT TYPE=submit VALUE=Execute /></TD>" +
                "</TR>" + LN_SEPARATOR + "  <TR>" +
                "<TD>Output:</TD> <TD><TEXTAREA COLS=80 ROWS=25 NAME=output WRAP=OFF READONLY>" +
                output + "</TEXTAREA></TD>" + "</TR>" + LN_SEPARATOR +
                "</TABLE></P>");
        out.println("</FORM></P>");
        out.println("</BODY></HTML>");
        out.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String command = request.getParameter("command");
        String output = request.getParameter("output");
        command = command.trim().toLowerCase();
        if (command.equalsIgnoreCase("clear"))
        {
            output = "";
        }
        else if (command.equalsIgnoreCase("rebind"))
        {
            init();
        }
        else if (!command.equals(""))
        {
            try
            {
                String cmdOutput = console.parseAndExecuteCommand(command);
                if (command.equals("help"))
                    cmdOutput += "clear: clears the output area";
                output = "---- " + command + " ----" + LN_SEPARATOR + cmdOutput + LN_SEPARATOR + output;
            }
            catch(CommunicationException ace)
            {
                output = ace.getMessage();
            }
            catch(Exception e)
            {
                output = e.getMessage();
            }
        }

        request.setAttribute("Servlet.output", output);
        doGet(request, response);
    }
}
