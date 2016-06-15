package com.lego.minddroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends Activity {

    private static final String    TAG = "OCVSample::MainActivity";
    private static final int    FOLLOW_REQUEST = 1;
    private StaticClass robotControl = StaticClass.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Instantiated new " + this.getClass());

        //start robot program
        robotControl.onStart(this);

    }
    @Override
    protected void onResume() {
        super.onResume();
        robotControl.onResume(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        robotControl.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        robotControl.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        robotControl.onPause();
    }

    //button interface to send starting intent to CameraActivity
    public void redBallButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivityForResult(intent, FOLLOW_REQUEST);
    }
}