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

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Connection.BluetoothConnector;


public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "CameraActivity";
    private static final int BACK_REQUEST = 1;

    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;

    int lastS;
    int countS;

    private CameraBridgeViewBase mOpenCvCameraView;
    private Button sBt;

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

    private float form;

    private final int motorLeft = 1;
    private final int motorRight = 2;
    private final int goStraight = 3;
    private final int stopMotor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothConnector.getInstance().onStart(this);
        setContentView(R.layout.activity_camera);
        Log.i(TAG, "OnCreate CameraActivity");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_java_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        form = 0f;

        lastS = Calendar.getInstance().get(Calendar.SECOND);
        countS = 0;

        sBt = (Button) findViewById(R.id.stop_button);
        //sBt.setVisibility(View.INVISIBLE);
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
        BluetoothConnector.getInstance().onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
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
        BluetoothConnector.getInstance().onStop();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        BluetoothConnector.getInstance().onResume(this);
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BluetoothConnector.getInstance().onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
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
        form = DetectShape(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
        SendCommand();
        String s = Float.toString(form);
        Log.d("MSG", s);
        GetFrameRate();
        return mRgba;
    }

    //this function is triggered by the button STOP ME
    public void goBackToMain(View view) {
        finish();
    }

    //c++ function to retrieve features from frame

    public native float DetectShape(long matAddrGr, long matAddrRgba);//Function


    //here start the part to control the movement of the robot---------------------------------

    public void SendCommand()
    {
        ExecutorService TaskList = Executors.newFixedThreadPool(2);

        BluetoothConnector.getInstance().setParameters(true,(int)form);
        TaskList.execute(BluetoothConnector.getInstance());

        TaskList.shutdown();
    }

        private void GetFrameRate()
    {
        int s = Calendar.getInstance().get(Calendar.SECOND);
        if(s != lastS)
        {
            lastS = s;
            Log.d("FrameRate: ", Integer.toString(countS));
            countS = 0;
        }
        else
        {
            countS++;
        }
    }


}
