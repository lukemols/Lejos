package program;

import java.awt.Button;
import java.io.*;
import lejos.ev3.*;
import lejos.hardware.Bluetooth;
import lejos.hardware.lcd.LCD;
import lejos.remote.nxt.NXTConnection;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.NXTComm;
import lejos.remote.nxt.NXTCommConnector;

public class Main {

	public static void main(String[] args) {
		NXTConnection connection = null;

		if( true ){
			LCD.drawString("waiting for BT", 0,1 );
			connection = Bluetooth.getNXTCommConnector().waitForConnection(10000, NXTConnection.RAW);
		}
		LCD.clear();
		LCD.drawString("BT connected", 0,1 );
		DataOutputStream dataOut = connection.openDataOutputStream();
		DataInputStream dataIn = connection.openDataInputStream();
		try {
			while(connection instanceof NXTConnection) { 
				int n = dataIn.readInt(); 

				LCD.clear(); 
				LCD.drawString("Operation: " + Integer.toString(n), 0, 1); 

			}
		} 
		catch (IOException e ) {
			System.out.println(" write error "+e); 
		}

	}

}
