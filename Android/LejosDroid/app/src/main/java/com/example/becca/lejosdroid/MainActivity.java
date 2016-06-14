package com.example.becca.lejosdroid;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.becca.lejosdroid.R;

import bluetooth.BluetoothCommands;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button ForwardBT;
    Button BackwardBT;
    Button LeftBT;
    Button RightBT;
    Button StopBT;
    Button ConnectBT;

    final int Forward = 1;
    final int Backward = 2;
    final int Left = 3;
    final int Right = 4;
    final int Stop = 0;
    final int Connect = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Crea la classe BluetoothCommands
        BluetoothCommands.GetInstance();

        ForwardBT = (Button)findViewById(R.id.ForwardBT);
        BackwardBT = (Button)findViewById(R.id.BackwardBT);
        LeftBT = (Button)findViewById(R.id.LeftBT);
        RightBT = (Button)findViewById(R.id.RightBT);
        StopBT = (Button)findViewById(R.id.StopBT);
        ConnectBT = (Button)findViewById(R.id.ConnectBT);

        ForwardBT.setOnClickListener(this);
        BackwardBT.setOnClickListener(this);
        LeftBT.setOnClickListener(this);
        RightBT.setOnClickListener(this);
        StopBT.setOnClickListener(this);
        ConnectBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int command = -1;
        switch (v.getId())
        {
            case R.id.ForwardBT:
                command = Forward;
                break;
            case R.id.BackwardBT:
                command = Backward;
                break;
            case R.id.LeftBT:
                command = Left;
                break;
            case R.id.RightBT:
                command = Right;
                break;
            case R.id.StopBT:
                command = Stop;
                break;
            case R.id.ConnectBT:
                command = Connect;
                break;
        }

        BluetoothCommands.GetInstance().updateMotorControl(command);

    }
}
