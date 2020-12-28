package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.management.Notification;

import org.jboss.jmx.adaptor.rmi.RMINotificationListener;

public class ClientListener implements RMINotificationListener 
{
	private ClientMain client;

	public ClientListener(ClientMain client) throws RemoteException 
	{
		this.client = client;
		
		UnicastRemoteObject.exportObject(this);
	}

	public void handleNotification(Notification event, Object handback)
	{
	   	client.handleNotification(event, handback);
	}
}