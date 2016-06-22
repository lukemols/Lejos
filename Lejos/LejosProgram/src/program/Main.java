package program;

//import java.awt.Button;

import java.io.*;

import Connector.BluetoothConnector;
import Motor.MotorControl;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class Main {

	public static void main(String[] args) {
		
		LCD.drawString("Starting Color Sensor", 0,1 );
		EV3ColorSensor color = new EV3ColorSensor(SensorPort.S4);
		LCD.clear();
		
		MotorControl myMotor = new MotorControl();
		
		myMotor.initialize(100);

		while( !BluetoothConnector.getInstance().startConnection() )
		{
			LCD.drawString("waiting for BT", 0,1 );
			if(Button.ESCAPE.isDown())
				return;
		}

		LCD.clear();
		LCD.drawString("BT connected", 0,1 );
		
		SampleProvider sample =	color.getRGBMode();
		float[] actualRead = new float[sample.sampleSize()];
		float riskColor = 0.005f;

		while(BluetoothConnector.getInstance().ConnectionActive()) 
		{ 
			if(Button.ESCAPE.isDown())
				break;
			
			sample.fetchSample(actualRead, 0);

			int n = BluetoothConnector.getInstance().ReceiveMessage(); 

			LCD.clear(); 
			LCD.drawString("Operation: " + Integer.toString(n), 0, 1);
			LCD.drawString(Float.toString(actualRead[0]), 0, 2);

			if(actualRead[0] < riskColor)
			{
				myMotor.safeTurn();
				continue;
			}
			
			switch(n)
			{
			case 2:
				myMotor.goForward();
				break;
			case 3:
				myMotor.rotateSX();
				break;
			case 4:
				myMotor.rotateDX();
				break;
			case 0:
			case 1:
			default:
				myMotor.Stop();
				break;
			}
		}
		
		color.close();

	}

}
