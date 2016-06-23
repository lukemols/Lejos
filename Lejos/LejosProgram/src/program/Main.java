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

		LCD.drawString("Starting", 0,1 );
		LCD.drawString("Color Sensor", 0,2 );
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
			LCD.drawString("Operation:", 0, 1);

			if(actualRead[0] < riskColor)
			{
				LCD.drawString("Safe Turn", 0, 2);
				myMotor.safeTurn();
				continue;
			}
			
			switch(n)
			{
			case 2:
				LCD.drawString("Go forward", 0, 2);
				myMotor.goForward();
				break;
			case 3:
				LCD.drawString("Turn Left", 0, 2);
				myMotor.rotateSX();
				break;
			case 4:
				LCD.drawString("Turn Right", 0, 2);
				myMotor.rotateDX();
				break;
			case 0:
			case 1:
			default:
				LCD.drawString("Stop", 0, 2);
				myMotor.Stop();
				break;
			}
		}
		
		color.close();

	}

}
