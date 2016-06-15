package com.lego.minddroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "CameraActivity";
    private static final int BACK_REQUEST = 1;

    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;

    private CameraBridgeViewBase mOpenCvCameraView;
    private Button sBt;

    private StaticClass robotControl = StaticClass.getInstance();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Camera Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.lego.minddroid/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    private enum STATE {STATE_RESET, STATE_GO, STATE_STOP}

    private STATE currentState;
    private float centers;

    private final int motorLeft = 1;
    private final int motorRight = 2;
    private final int goStraight = 3;
    private final int stopMotor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        robotControl.onStart(this);
        setContentView(R.layout.activity_camera);
        Log.i(TAG, "OnCreate CameraActivity");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_java_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        centers = 0f;
        currentState = STATE.STATE_RESET;


        sBt = (Button) findViewById(R.id.stop_button);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //load OpenCV library
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("mixed_sample");

                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        robotControl.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        currentState = STATE.STATE_RESET;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Camera Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.lego.minddroid/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        robotControl.onStop();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        currentState = STATE.STATE_RESET;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        robotControl.onResume(this);
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        currentState = STATE.STATE_RESET;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        robotControl.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        currentState = STATE.STATE_RESET;
    }

    // here starts the part to set the camera and retreive features----------------------------------


    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    //at each frame call the jni Function and followRedBall to update the position of the robot
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        centers = Function(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
        //followRedBall();
        String s = Float.toString(centers);
        Log.d("MSG", s);
        return mRgba;
    }

    //this function is triggered by the button STOP ME
    public void goBackToMain(View view) {
        Intent intent = new Intent(CameraActivity.this, MainActivity.class);
        startActivityForResult(intent, BACK_REQUEST);
    }

    //c++ function to retrieve features from frame

    public native float Function(long matAddrGr, long matAddrRgba);


    //here start the part to control the movement of the robot---------------------------------

    public void followRedBall() {
        switch (currentState) {
            case STATE_RESET:
                Log.d(TAG, "RESET STATE");
                if (centers != 0f) {
                    currentState = STATE.STATE_GO;
                } else {
                    currentState = STATE.STATE_STOP;
                }
                break;
            case STATE_GO:
                Log.d(TAG, "GO STATE");

                double min_width = 0.3f * (mRgba.size().width);
                double max_width = 0.7f * (mRgba.size().width);

                /*if(centers == 0){
                    robotControl.updateMotorControl(stopMotor);
                    currentState = STATE.STATE_STOP;
                }*/

                //if the position of the center of the circle is in the center, go straight
                if (centers > min_width && centers < max_width) {
                    robotControl.updateMotorControl(goStraight);
                    currentState = STATE.STATE_GO;
                } else {
                    //if the position of the center of the circle is on the left, go left
                    if (centers < min_width) {
                        robotControl.updateMotorControl(motorLeft);
                    } //if the position of the center of the circle is on the right, go right
                    else {
                        robotControl.updateMotorControl(motorRight);
                    }
                    currentState = STATE.STATE_GO;
                }
                break;
            //command motorStop is sent during this state if still no center is to be found
            case STATE_STOP:
                Log.d(TAG, "STOP STATE");
                if (centers == 0) {
                    robotControl.updateMotorControl(stopMotor);
                } else {
                    currentState = STATE.STATE_GO;
                    robotControl.updateMotorControl(goStraight);
                }
                break;
        }
    }

}