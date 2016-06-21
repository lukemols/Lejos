package com.lego.minddroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import Connection.BluetoothConnector;


public class MainActivity extends Activity {

    private static final String    TAG = "OCVSample::MainActivity";
    private static final int    FOLLOW_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Instantiated new " + this.getClass());

        //start robot program
        BluetoothConnector.getInstance().onStart(this);

    }
    @Override
    protected void onResume() {
        super.onResume();
        BluetoothConnector.getInstance().onResume(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        BluetoothConnector.getInstance().onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothConnector.getInstance().onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        BluetoothConnector.getInstance().onPause();
    }

    //button interface to send starting intent to CameraActivity
    public void redBallButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivityForResult(intent, FOLLOW_REQUEST);
    }
}