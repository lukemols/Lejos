package Connector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import lejos.hardware.Bluetooth;
import lejos.remote.nxt.NXTConnection;

public class BluetoothConnector implements IConnector {

	private NXTConnection connection;
	private static BluetoothConnector instance;
	
	public static BluetoothConnector getInstance() 
	{
		if(instance == null)
		{
			instance = new BluetoothConnector();
		}
		
		return instance;
	};
	
	private BluetoothConnector(){}
	
	@Override
	public boolean startConnection() {

		connection = Bluetooth.getNXTCommConnector().waitForConnection(10000, NXTConnection.RAW);
		if(connection == null)
			return false;
		else
			return true;
	}

	@Override
	public int ReceiveMessage() 
	{
		try
		{
			DataInputStream dataIn = connection.openDataInputStream();
			return dataIn.readInt();
		}
		catch(Exception e)
		{
			return -1;
		}
	}

	@Override
	public void SendMessage(int code)
	{
		try
		{
			DataOutputStream dataOut = connection.openDataOutputStream();
			
		}
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public boolean ConnectionActive()
	{
		return connection instanceof NXTConnection;
	}

}
