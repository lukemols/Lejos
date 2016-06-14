package program;

import java.awt.Button;
import java.io.*;
import lejos.ev3.*;
import lejos.hardware.Bluetooth;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.remote.nxt.NXTConnection;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.NXTComm;
import lejos.remote.nxt.NXTCommConnector;

public class Main {

	public static void main(String[] args) {
		
		Wheel wheel1 = WheeledChassis.modelWheel(Motor.B, 55.5).offset(-60);
		Wheel wheel2 = WheeledChassis.modelWheel(Motor.D, 55.5).offset(60);
		Chassis chassis = new WheeledChassis(new Wheel[]{wheel1, wheel2}, WheeledChassis.TYPE_DIFFERENTIAL); 
		
		MovePilot pilot = new MovePilot(chassis);
		
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
				
				float step = 0;
				switch(n)
				{
				case 1:
					step = 10;
					break;
				case 2:
					step = -10;
					break;
				case 3:
					pilot.rotate(- 5 * 5);
					break;
				case 4:
					pilot.rotate(5 * 5);
					break;
				case 0:
					default:
					break;
				}

				pilot.travel(step);

			}
		} 
		catch (IOException e ) {
			System.out.println(" write error "+e); 
		}

	}

}
