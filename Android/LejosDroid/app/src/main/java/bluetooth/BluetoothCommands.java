package bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.example.becca.lejosdroid.MainActivity;
import com.example.becca.lejosdroid.R;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by becca on 26/05/2016.
 */
public class BluetoothCommands {

    Socket socket;
    BufferedWriter pw;

    //boolean to know if the stop was already sent. We don't need to stop twice
    private boolean stopAlreadySent = false;
    //the context of the caller activity
    private Context caller;
    //the tag for the log
    private String TAG = "STATIC_CLASS";
    //the mac address of the lejos robot
    private final String macAddress = "00:16:53:4E:BE:0A";

    private BluetoothAdapter BTAdapter;
    private BluetoothSocket BTsocket;
    //the min value of the battery tollerated
    private static int minBattery = -2;
    //boolean to know if the connection is active or not
    public boolean connected = false;


    private static BluetoothCommands instance;

    public static BluetoothCommands GetInstance()
    {
        if(instance == null)
            instance = new BluetoothCommands();
        return instance;
    }

    private BluetoothCommands()
    {
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
    //function to create the bluetooth connection through the socket
    private void startBTCommunicator(String macAddress) {
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

    //public function called to update the values of the motors during Go state
    public void updateMotorControl(int code) {

        if (BTAdapter == null){
            startBTCommunicator(macAddress);
        }
        // don't send motor stop twice
        if (code == 0) {
            if (stopAlreadySent)
                return;
            else
                stopAlreadySent = true;
        }
        else
            stopAlreadySent = false;
        if(connected == true) {
            try {
                OutputStream outputStream = BTsocket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeInt(code);
                dataOutputStream.flush();

                Thread.sleep(1000);
                //readMessage();
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
    //public function to update the caller
    public void onStart(Context context) {
        caller = context;
    }
    //the motor are stopped and if the caller is main class thebluetooth connection is closed
    protected void onDestroy() {
        int stopMotor = 0;
        updateMotorControl(stopMotor);
        if(caller.getClass() == MainActivity.class){
            try{
                BTsocket.close();
            }
            catch(Exception e){e.printStackTrace();}
        }
    }

    public void onPause() {

    }

    protected void onResume(Context context) {
        caller = context;
    }

    public void onStop() {
        try {
            updateMotorControl(0);

        }catch(Exception e){e.printStackTrace();}
    }

    //function to read the value of the battery sent by the robot
    private void readMessage() {
        int n;
        // Swith nxt socket
        if (BTsocket!=null) {
            try {
                InputStreamReader in = new InputStreamReader(BTsocket.getInputStream());
                n = in.read();
                if(n < minBattery)
                    onBatteryEnded();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //alert dialog to control to advise that the battery is ending. It can be called during any exchange of messages from
    //an activity containing an instance of StaticClass
    private void onBatteryEnded(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(caller);

        alertDialogBuilder.setTitle(R.string.alert_title);
        alertDialogBuilder.
                setMessage("The battery is ending: please recharge me. Do you want to continue? ").
                setPositiveButton(R.string.Continue , new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
