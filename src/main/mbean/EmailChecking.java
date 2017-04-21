package mbean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.timer.Timer;
import javax.naming.InitialContext;

import misc.Console;
import net.homeip.mleclerc.omnilink.MessageManager;
import net.homeip.mleclerc.omnilink.message.PhoneCommand;

import org.jboss.system.ServiceMBeanSupport;

import util.Filter;

public class EmailChecking extends ServiceMBeanSupport implements EmailCheckingMBean, NotificationListener
{
    class EmailFilter extends Filter
    {
        public boolean getResult(Object arg)
        {
            try
            {
                if (arg == null)
                    return false;

                // Get the message wrapper
                javax.mail.Message msg = (javax.mail.Message) arg;

                // Get message from addresses
                Address[] msgFromAddresses = msg.getFrom();

                // Check message title
                String msgTitle = msg.getSubject();
                if (msgTitle != null && msgTitle.equalsIgnoreCase(controlTitle))
                {
                    // Send the email contents to the command line parser
                    InputStreamReader isr = new InputStreamReader(msg.getInputStream());
                    BufferedReader reader = new BufferedReader(isr);
                    console.run(reader);
                }

                if (fromAddresses == null)
                    return false;

                // Check message from address
                for (int i = 0; i < msgFromAddresses.length; i++)
                {
                    String msgFromAddressStr = msgFromAddresses[i].toString().toLowerCase();
                    for (int j = 0; j < fromAddresses.length; j++)
                    {
                        String fromAddressStr = fromAddresses[i];
                        if (msgFromAddressStr.indexOf(fromAddressStr) >= 0)
                            return true;
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return false;
        }
    };

    private final static String MBOX = "INBOX";
    private final static String TIMER_SERVICE = ":service=Timer";
    private final static String EVENT_TYPE = "emailCheckUpdate";

    private int updatePeriod = 0; // in seconds
    private String emailFrom = null;
    private String emailTo = null;
    private String[] fromAddresses = null;
    private String controlTitle = null;

    private Integer notifId = null;
    private Filter emailFilter = new EmailFilter();
    private Console console = null;

    private ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(10);
    
    public EmailChecking()
    {
    }

    public void setUpdatePeriod(int updatePeriod)
    {
        this.updatePeriod = updatePeriod;
    }

    public int getUpdatePeriod()
    {
        return updatePeriod;
    }

    public String getEmailFrom()
    {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom)
    {
        this.emailFrom = emailFrom;
    }

    public String getEmailTo()
    {
        return emailTo;
    }

    public void setEmailTo(String emailTo)
    {
        this.emailTo = emailTo;
    }

    public String getFromAddresses()
    {
        StringBuffer strbuf = new StringBuffer();

        if (fromAddresses != null)
        {
            for (int i = 0; i < fromAddresses.length; i++)
            {
                strbuf.append(fromAddresses[i]);
                if (i < fromAddresses.length - 1)
                    strbuf.append(' ');
            }
        }

        return strbuf.toString();
    }

    public void setFromAddresses(String fromAddresses)
    {
        StringTokenizer strtok = new StringTokenizer(fromAddresses);
        int tokenCount = strtok.countTokens();
        this.fromAddresses = new String[tokenCount];
        for (int i = 0; i < tokenCount; i++)
            this.fromAddresses[i] = strtok.nextToken().toLowerCase();
    }

    public String getControlTitle()
    {
        return controlTitle;
    }

    public void setControlTitle(String title)
    {
        this.controlTitle = title;
    }

    public void startService()
    {
        try
        {
            // Get the message manager from the communication MBean
            ObjectName name = new ObjectName(CommunicationMBean.NAME);
            MessageManager service = (MessageManager) server.getAttribute(name, "MessageManager");

            // Create the console
            console = new Console(service);

            // Register this MBean with the timer bean via the server
            NotificationFilterSupport myFilter = new NotificationFilterSupport();
            myFilter.enableType(EVENT_TYPE);
            server.addNotificationListener(new ObjectName(TIMER_SERVICE), this, myFilter, null);

            // Add a new notification to the timer bean
            if (updatePeriod > 0)
            {
            	notifId = (Integer) server.invoke(new ObjectName(TIMER_SERVICE), "addNotification", new Object[] {EVENT_TYPE, "message", null, Calendar.getInstance().getTime(), new Long(updatePeriod * Timer.ONE_SECOND)}, new String[] {"java.lang.String", "java.lang.String", "java.lang.Object", "java.util.Date", "long"});
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void stopService()
    {

        try
        {
            // Remove notification from timer bean
        	if (notifId != null)
        	{
        		server.invoke(new ObjectName(TIMER_SERVICE), "removeNotification", new Object[] {notifId}, new String[] {"java.lang.Integer"});
        	}

            // Remove notification listener
            server.removeNotificationListener(new ObjectName(TIMER_SERVICE), this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void handleNotification(Notification notification, Object handback)
    {
    	// Execute the following code in a separate runnable
    	Runnable exec = new Runnable()
    	{		
			public void run() 
			{
		        try
		        {
		            // Get the number of emails waiting to be read
		            int messageCount = getNewMessageCount(emailFilter);

		            // Debug
		            //java.lang.System.out.println("number of outstanding emails: " + messageCount);

		            // Check if there is new messages since the last notification
		            if (messageCount > 0)
		            {
		                // Debug
		                //java.lang.System.out.println("New email(s) available!");

		                // Send notification by phone
		                Utils.sendMessage(server, new PhoneCommand(1, 3));
		            }
		        }
		        catch(Exception ex)
		        {
		            ex.printStackTrace();
		        }
			}
		};
		threadPool.execute(exec);
    }

    public void sendEmail(String title, String msgText)
    {
        sendMessage(emailTo, emailFrom, title, msgText);
    }

    private void sendMessage(String to, String from, String title, String textMsg)
    {
        if (textMsg == null)
            textMsg = "";

        try
        {
            // Get the current session
            InitialContext ctx = new InitialContext();
            Object ref = ctx.lookup("java:/Mail");
            Session session = (Session) ref;

            // Create a message
            javax.mail.Message message = new MimeMessage(session);
            InternetAddress[] address = { new InternetAddress(to) };
            message.setRecipients(javax.mail.Message.RecipientType.TO, address);
            message.setFrom(new InternetAddress(from));
            message.setSubject(title);
            message.setSentDate(new Date());
            message.setText(textMsg);

            // Send the message
            Transport transport = session.getTransport();
            Transport.send(message);
            transport.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private Set retrievedMsgList = new HashSet();

    private int getNewMessageCount(Filter filter)
    {
        int ret = 0;

        try
        {
            // Get the current session
            InitialContext jndiContext = new InitialContext();
            Object ref = jndiContext.lookup("java:/Mail");
            Session session = (Session) ref;

            // Get a Store object
            Store store = session.getStore();

            // Connect
            store.connect();

            // Open the Folder
            Folder folder = store.getDefaultFolder();
            if (folder == null)
            {
                java.lang.System.out.println("No default folder");
                return -1;
            }

            folder = folder.getFolder(MBOX);
            if (folder == null)
            {
                java.lang.System.out.println("Invalid folder");
                return -1;
            }

            // try to open read/write and if that fails try read-only
            try
            {
                folder.open(Folder.READ_WRITE);
            }
            catch (MessagingException ex)
            {
                folder.open(Folder.READ_ONLY);
            }

            // Get the messaages
            javax.mail.Message[] messages = folder.getMessages();

            // Go through all new messages and check
            // if any matches any of the from addresses monitored
            Set newRetrievedMsgList = new HashSet();
            for (int i = 0; i < messages.length; i++)
            {
                // Get the next message
                javax.mail.Message msg = (javax.mail.Message) messages[i];

                // Get the unique ID of the message
                String msgUID = getMessageUID(folder, msg);

                // Check to see if we've already processed that message
                if (!retrievedMsgList.contains(msgUID))
                {
                    // Debug
                    //java.lang.System.out.print("New message: " + msgUID);

                    // Check to see the message passes the filter
                    if (filter.getResult(msg))
                        ret++;
                }
                else
                {
                    // Debug
                    //java.lang.System.out.print("Message already retrieved: " + msgUID);
                }

                // Add it to the list of processed msgs
                newRetrievedMsgList.add(msgUID);
            }

            // Keep the new list of retrieved messages
            retrievedMsgList = newRetrievedMsgList;

            // Close the folder
            folder.close(false);
            store.close();
        }
        catch (Exception ex)
        {        	
            //ex.printStackTrace();
        	//java.lang.System.err.println("Error retrieving email");
        }

        return ret;
    }

    private String getMessageUID(Folder folder, javax.mail.Message msg)
    {
        String uid = null;

        if (folder instanceof com.sun.mail.pop3.POP3Folder)
        {
            try
            {
                com.sun.mail.pop3.POP3Folder pf = (com.sun.mail.pop3.POP3Folder)folder;
                uid = pf.getUID(msg);
            }
            catch (MessagingException ex)
            {
                ex.printStackTrace();
            }
        }

        return uid;
    }
}
