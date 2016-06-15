#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include <android/log.h>

#define LOGD(TAG,...) __android_log_print(ANDROID_LOG_DEBUG  , TAG,__VA_ARGS__)

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT jfloat JNICALL Java_com_lego_minddroid_CameraActivity_Function(JNIEnv*, jobject, jlong addrGray, jlong addrRgba);

JNIEXPORT jfloat JNICALL Java_com_lego_minddroid_CameraActivity_Function(JNIEnv*, jobject, jlong addrGray, jlong addrRgba) {
    Mat &mGr = *(Mat *) addrGray;
    Mat &mRgb = *(Mat *) addrRgba;

    Mat mbw;
    threshold(mGr, mbw, 60,255,THRESH_BINARY);

    /**/
    vector<vector<Point> > Contours;
    findContours(mbw,Contours,RETR_EXTERNAL,CHAIN_APPROX_SIMPLE);

    vector<vector<Point> > mContours = Contours;
    for(size_t k = 0; k < Contours.size(); k++)
    {
        approxPolyDP(Mat(Contours[k]), mContours[k], 3, true);
    }

    return mContours.size();
    if(mContours.size() == 0)
        return 0;
    else
        return 1.0;

    /*
     *
     * vector<Vec3f> circles;
    GaussianBlur( mGr, mGr, Size(9, 9), 1, 1 );
    //HoughCircles(mGr, circles, CV_HOUGH_GRADIENT, 1, mGr.rows/8, 200, 100, 0, 0 ); //it does not take many circles
    //HoughCircles(mGr, circles, CV_HOUGH_GRADIENT, 2, 32.0, 30, 150, 550); //it takes no circle
    HoughCircles(mGr, circles, CV_HOUGH_GRADIENT, 2, 100.0, 30, 150, 100, 140);
    //HoughCircles(mGr, circles, CV_HOUGH_GRADIENT, 2, mGr.rows/4,200, 100); //this one is kind of imprecise
    vector<Point> mCen;

    /// Draw the circles detected
    for( size_t i = 0; i < circles.size(); i++ )
    {
        Point center(cvRound(circles[i][0]), cvRound(circles[i][1]));
        int radius = cvRound(circles[i][2]);
        // circle center
        circle( mRgb, center, 3, Scalar(255, 0, 0, 255), -1, 8, 0 );
        // circle outline
        circle( mRgb, center, radius, Scalar(255, 0, 0, 255), 3, 8, 0 );

        mCen.push_back(center);
    }
    if(mCen.size() != 0) {
        LOGD("JNI","found circle");
        return mCen[0].x;
    }
    else {
        LOGD("JNI","no circle");
        return 0;
    }*/


}

}
