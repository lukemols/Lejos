package Connector;

public interface IConnector {
	
	public boolean startConnection();
	
	public int ReceiveMessage();
	
	public void SendMessage(int code);
	
	public boolean ConnectionActive();
	
}
