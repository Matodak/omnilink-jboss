package client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

public class ClientMain
{
    private final static String EVENT_FORWARDER = "Aegis:name=event.forwarder";

    private Map compObjTableModelMap = Collections.synchronizedMap(new HashMap());
    private RMIAdaptor server = null;
    private ClientListener listener;

    private ClientMain()
    {
    }

    public void run(String[] args) throws Exception
    {
        String host = args[0];
        String jndiPort = args[1];
        String rmiAdaptorJndi = args[2];

        // Set up JNDI properties
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        props.put(Context.PROVIDER_URL, "jnp://" + host + ":" + jndiPort);
        InitialContext ctx = new InitialContext(props);

        // Retrieve the RMI adaptor
        Object obj = ctx.lookup(rmiAdaptorJndi);
        ctx.close();

        // Get the RMI adaptor in its real type
        server = (RMIAdaptor) obj;

        // Listen for events coming from the event forwarder
        listener = new ClientListener(this);        
        server.addNotificationListener(new ObjectName(EVENT_FORWARDER), listener, null, null);

        // Get the table models
        TableModel zoneTableModel = createTableModel(server, "Aegis.zone");
        TableModel unitTableModel = createTableModel(server, "Aegis.unit");
        TableModel tempTableModel = createTableModel(server, "Aegis.thermostat");
        TableModel messageTableModel = createTableModel(server, "Aegis.message");
        TableModel systemTableModel = createTableModel(server, "Aegis.system");
        TableModel buttonTableModel = createTableModel(server, "Aegis.button");
        TableModel codeTableModel = createTableModel(server, "Aegis.code");
        TableModel eventLogTableModel = createTableModel(server, "Aegis.eventlog");
        
        // Create the client GUI
        ClientFrame frame = new ClientFrame(unitTableModel, zoneTableModel, tempTableModel, messageTableModel, systemTableModel, buttonTableModel, codeTableModel, eventLogTableModel);
        frame.setVisible(true);

        // Create the exit thread
        Runnable exec = new Runnable()
        {
            public void run()
            {
                if (server != null)
                {
                    try
                    {
                        // Unregistering the listener
                        server.removeNotificationListener(new ObjectName(EVENT_FORWARDER), listener);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };

        // Listen for exit
        Runtime.getRuntime().addShutdownHook(new Thread(exec));
    }

    private TableModel createTableModel(RMIAdaptor server, String componentName) throws Exception
    {
        TableModel ret = null;

        // Get the list of zone MBeans
        Set instanceList = server.queryNames(new ObjectName(componentName + ":*"), null);

        // Populate a table model with the zones retrieved
        if (instanceList != null)
        {
            int row = 0;
            for (Iterator iter = instanceList.iterator(); iter.hasNext(); row++)
            {
                // Get the component object name
                ObjectName objName = (ObjectName) iter.next();

                // Get the attributes defined for this object
                MBeanInfo zoneInfo = server.getMBeanInfo(objName);
                MBeanAttributeInfo attrsInfo[] = zoneInfo.getAttributes();
                if (ret == null)
                {
                    String[] columnNames = new String[attrsInfo.length];
                    for (int i = 0 ; i < attrsInfo.length; i++)
                        columnNames[i] = attrsInfo[i].getName();
                    ret = new DefaultTableModel(columnNames, instanceList.size());
                }

                // Keep the mapping between the object and its table
                compObjTableModelMap.put(objName, ret);

                for (int i = 0; i < attrsInfo.length; i++)
                {
                    MBeanAttributeInfo attrInfo = attrsInfo[i];
                    String attrName = attrInfo.getName();
                   
                    // Get the attribute value
                    Object attrValue = server.getAttribute(objName, attrName);
                    setTableValueAt(ret, attrValue, row, i);
                }
            }
        }

        return ret;
    }

    public static void main(String[] args)
    {
        try
        {
            ClientMain app = new ClientMain();
            app.run(args);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void handleNotification(Notification event, Object parm2)
    {
        // Check the event type
        if (event instanceof AttributeChangeNotification)
        {
            final AttributeChangeNotification attrNotif = (AttributeChangeNotification) event;

            // Get the source of the change
            final ObjectName source = (ObjectName) attrNotif.getUserData();

            // Get the table associated with the source
            final TableModel tableModel = (TableModel) compObjTableModelMap.get(source);
            if (tableModel == null)
                return;

            Runnable exec = new Runnable()
            {
                public void run()
                {
                    try
                    {
                        String attrName = attrNotif.getAttributeName();
                        Object attrValue = attrNotif.getNewValue();
                        int row = -1;
                        String numberStr = source.getKeyProperty("number");
                        if (numberStr != null)
                        {
                            Integer number = Integer.valueOf(numberStr);
                            row = getRow(tableModel, "Number", number);
                        }
                        else
                            row = 0;

                        // Get the column
                        int column = getColumn(tableModel, attrName);

                        // Change the value
                        setTableValueAt(tableModel, attrValue, row, column);
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            };

            SwingUtilities.invokeLater(exec);
        }
    }

    private int getRow(TableModel tableModel, String attrName, Object attrValue)
    {
        int column = getColumn(tableModel, attrName);
        for (int i = 0; i < tableModel.getRowCount(); i++)
        {
            Object value = tableModel.getValueAt(i, column);
            if (value.equals(attrValue))
                return i;
        }

        return -1;
    }

    private int getColumn(TableModel tableModel, String attrName)
    {
        for (int i = 0; i < tableModel.getColumnCount(); i++)
        {
            String colName = tableModel.getColumnName(i);
            if (colName.equals(attrName))
                return i;
        }

        return -1;
    }
    
    private void setTableValueAt(TableModel tableModel, Object value, int row, int column) {        
        // Handle array of string as a special case
        if (value.getClass().isArray()) {
        	Object[] attrValues = (Object[]) value;
        	String string = new String();
        	for (int j = 0; j < attrValues.length; j++) {
        		string += attrValues[j];
        		if (j < attrValues.length - 1) {
        			string += ", ";
        		}
        	}
        	value = string;
        }
        
        tableModel.setValueAt(value, row, column);
    }
}
