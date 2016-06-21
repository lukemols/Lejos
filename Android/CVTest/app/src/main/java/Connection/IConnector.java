package Connection;

/**
 * Created by becca on 21/06/2016.
 */
public interface IConnector {
    //init connection
    public void StartConnection(String macAddress);
    //send
    public void SendMessage(int command);
    //receive
    public int ReceiveMessage();
}
