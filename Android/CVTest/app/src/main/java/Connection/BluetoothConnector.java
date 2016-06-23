package Connection;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.lego.minddroid.MainActivity;
import com.lego.minddroid.R;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by becca on 21/06/2016.
 */
public class BluetoothConnector implements IConnector, Runnable {

    //singleton design pattern
    private static BluetoothConnector mInstance= null;
    protected BluetoothConnector(){}

    public static synchronized BluetoothConnector getInstance(){
        if(null == mInstance){
            mInstance = new BluetoothConnector();
        }
        return mInstance;
    }
    //the context of the caller activity
    private Context caller;
    //the tag for the log
    private String TAG = "BLUETOOTH_CONNECTOR";
    //the mac address of the lejos robot
    private final String macAddress = "00:16:53:4E:BE:0A";

    private BluetoothAdapter BTAdapter;
    private BluetoothSocket BTsocket;
    //boolean to know if the connection is active or not
    public boolean connected = false;

    private boolean sendMessage = true;
    private int code = 0;
    public int getCode(){ return code; }

    @Override
    public void StartConnection(String macAddress)
    {
        connected = false;
        createBTCommunicator();
        BluetoothDevice robot = BTAdapter.getRemoteDevice(macAddress);
        try {
            BTsocket = robot.createRfcommSocketToServiceRecord(UUID
                    .fromString("00001101-0000-1000-8000-00805F9B34FB"));
            BTsocket.connect();
            connected = true;

        } catch (IOException e) {
            Log.d("Bluetooth", "Err: Device not found or cannot connect");
            connected =false;
        }
    }

    @Override
    public void SendMessage(int command) {

        if (BTAdapter == null){
            StartConnection(macAddress);
        }

        if(connected == true) {
            try {
                OutputStream outputStream = BTsocket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeInt(command);
                dataOutputStream.flush();

                Thread.sleep(1000);
            }
            catch(IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d(TAG, "no connection found, impossible to send actions to robot");
        }
    }

    @Override
    public int ReceiveMessage() {
        int n = 0;
        // Swith nxt socket
        if (BTsocket!=null) {
            try {
                InputStreamReader in = new InputStreamReader(BTsocket.getInputStream());
                n = in.read();
            } catch (IOException e) {
                e.printStackTrace();
                return  0;
            }
        }
        return n;
    }

    //function to create the bluetooth adapter
    private void createBTCommunicator() {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        //these functions were used to read the values of available mac address
        //Set<BluetoothDevice> device = BTAdapter.getBondedDevices();
        //device.toArray();
        if (!BTAdapter.isEnabled()) {
            BTAdapter.enable();
            while(!(BTAdapter.isEnabled()));
        }
        /*00:16:53:10:75:86 is the value of the lejos robot WALL-E to which we connect*/

    }

    public void setParameters(boolean sendMessage, int code)
    {
        this.sendMessage = sendMessage;
        this.code = code;
    }


    @Override
    public void run()
    {
        if(sendMessage)
        {
            SendMessage(code);
        }
        else
        {
            code = ReceiveMessage();
        }
    }



    //public function to update the caller
    public void onStart(Context context) {
        caller = context;
    }
    //the motor are stopped and if the caller is main class thebluetooth connection is closed
    public void onDestroy() {
        int stopMotor = 0;
        SendMessage(stopMotor);
        if(caller.getClass() == MainActivity.class){
            try{
                BTsocket.close();
            }
            catch(Exception e){e.printStackTrace();}
        }
    }

    public void onPause() {

    }

    public void onResume(Context context) {
        caller = context;
    }

    public void onStop() {
        try {
            SendMessage(0);

        }catch(Exception e){e.printStackTrace();}
    }


}
