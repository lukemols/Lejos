package Motor;

import lejos.hardware.motor.*;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class MotorControl {
	
	public float speed;
	public float rL; //Left rotate scaling factor
	public float rR; //Right rotate scaling factor
	public float lambda; //Weight: how much the speed will change given the previous speed?
	public float speedL;
	public float speedR;
	public float lastSpeedL;
	public float lastSpeedR;

	//Default constructor
	public MotorControl(){}


	//Method to initialize motors and to stop them when the Android app closes the BT socket
	public void initialize(float initSpeed)
	{
		speed = initSpeed;
		lastSpeedL = 0;
		lastSpeedR = 0;
		speedL = 0;
		speedR = 0;
		rL = 0.85f;
		rR = 0.85f;
		lambda = 0.70f;
		Motor.B.stop();
		Motor.D.stop();
	}

	//Moves the robot forward
	public void goForward()
	{ 
		Motor.B.setSpeed(speed);
		Motor.D.setSpeed(speed);
		Motor.B.forward();
		Motor.D.forward(); 
		lastSpeedL = speed;
		lastSpeedR = speed;
	}
	
	public void safeTurn()
	{
		Wheel wheel1 = WheeledChassis.modelWheel(Motor.B, 55.5).offset(-60);
		Wheel wheel2 = WheeledChassis.modelWheel(Motor.D, 55.5).offset(60);
		Chassis chassis = new WheeledChassis(new Wheel[]{wheel1, wheel2}, WheeledChassis.TYPE_DIFFERENTIAL);
		
		MovePilot pilot = new MovePilot(chassis);
		
		pilot.setLinearSpeed(45);
		pilot.setAngularSpeed(50);
		pilot.travel(-50);
		pilot.rotate(100);
		
		/*
		speedL = - 2 * speed;
		speedR = + 2 * speed;

		//Regulate the speed in function of the previous one
		speedL = lastSpeedL*(1 - lambda) + lambda*speedL;
		speedR = lastSpeedR*(1 - lambda) + lambda*speedR;

		Motor.B.setSpeed(speedR);
		Motor.D.setSpeed(speedL);

		lastSpeedL = speedL;
		lastSpeedR = speedR;
		
		Motor.B.backward();
		Motor.D.backward();
		*/
	}

	//Makes the robot turn to the left
	public void rotateSX()
	{
		Move(+1);
	}

	public void rotateDX()
	{
		Move(-1);
	}
	
	public void Stop()
	{
		Motor.B.stop();
		Motor.D.stop();
		
		//lastSpeedL = 0;
		//lastSpeedR = 0;
	}
	
	private void Move(int rotateFactor)
	{
		//Start the curve
		speedL = speed - rL * speed * rotateFactor;
		speedR = speed + rR * speed * rotateFactor;

		//Regulate the speed in function of the previous one
		speedL = lastSpeedL*(1 - lambda) + lambda*speedL;
		speedR = lastSpeedR*(1 - lambda) + lambda*speedR;

		Motor.B.setSpeed(speedR);
		Motor.D.setSpeed(speedL);

		lastSpeedL = speedL;
		lastSpeedR = speedR;
		
		Motor.B.forward();
		Motor.D.forward(); 
		
	}

}
