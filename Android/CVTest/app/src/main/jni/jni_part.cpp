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
    /*
    Mat &mGr = *(Mat *) addrGray;
    Mat &mRgb = *(Mat *) addrRgba;

    Mat mbw;
    threshold(mGr, mbw, 60,255,THRESH_BINARY);


    vector<vector<Point> > Contours;
    findContours(mbw,Contours,RETR_EXTERNAL,CHAIN_APPROX_SIMPLE);

    vector<vector<Point> > mContours = Contours;
    for(size_t k = 0; k < Contours.size(); k++)
    {
        approxPolyDP(Mat(Contours[k]), mContours[k], 3, true);
    }

    //return mContours.size();

    */
    float fCrash = 0;
    try {

        Mat &mGr = *(Mat *) addrGray;
        Mat &img = *(Mat *) addrRgba;

        fCrash = 1;
        //converting the original image into grayscale
        //Mat* imgGrayScale = cvCreateImage(cvGetSize(img), 8, 1);//[RIV]
        //FUNZIONA?! Niente errori quindi sì!!//[RIV]
        Mat *imgGrayScale = (Mat *) cvCreateMat(img.rows, img.cols,
                                                CV_8U);//VUOI UNA SCALA DI GRIGI CON O SENZA SEGNO?!//[RIV]
        //Mat* imgCanny = cvCreateImage(cvGetSize(img), 8, 1);
        Mat *imgCanny = (Mat *) cvCreateMat(img.rows, img.cols,
                                            CV_8U);//VUOI UNA SCALA DI GRIGI CON O SENZA SEGNO?!//[RIV]
        //thresholding the grayscale image to get better results

        fCrash = 2;
        threshold(mGr, mGr, 128, 255, CV_THRESH_BINARY);//scopami poi ti spiego
        //cvThreshold(&mGr, &mGr, 128, 255, CV_THRESH_BINARY);

        CvSeq *contours;  //hold the pointer to a contour in the memory block
        CvSeq *result;   //hold sequence of points of a contour
        CvMemStorage *storage = cvCreateMemStorage(0); //storage area for all contours

        fCrash = 3;
        //finding all contours in the image
        cvFindContours(&mGr, storage, &contours, sizeof(CvContour), CV_RETR_LIST,
                       CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
        //findContours(mGr, contours, CV_RETR_LIST,
        //               CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

        fCrash = 4;
        int count = 0;
        //iterating through each contour
        while (contours) {
            //obtain a sequence of points of contour, pointed by the variable 'contour'
            result = cvApproxPoly(contours, sizeof(CvContour), storage, CV_POLY_APPROX_DP,
                                  cvContourPerimeter(contours) * 0.02, 0);

            //result = approxPoly(contours, result,
            //                      cvContourPerimeter(contours) * 0.02, true);

            fCrash = 10 + 100 * count;
            //if there are 3  vertices  in the contour(It should be a triangle)
            if (result->total == 3) {
                //iterating through each point
                CvPoint *pt[3];
                for (int i = 0; i < 3; i++) {
                    pt[i] = (CvPoint *) cvGetSeqElem(result, i);
                }

                //drawing lines around the triangle
                cvLine(&img, *pt[0], *pt[1], cvScalar(255, 0, 0), 4);
                cvLine(&img, *pt[1], *pt[2], cvScalar(255, 0, 0), 4);
                cvLine(&img, *pt[2], *pt[0], cvScalar(255, 0, 0), 4);

                fCrash = 11 + 100 * count;

            }

                //if there are 4 vertices in the contour(It should be a quadrilateral)
            else if (result->total == 4) {
                //iterating through each point
                CvPoint *pt[4];
                for (int i = 0; i < 4; i++) {
                    pt[i] = (CvPoint *) cvGetSeqElem(result, i);
                }

                //drawing lines around the quadrilateral
                cvLine(&img, *pt[0], *pt[1], cvScalar(0, 255, 0), 4);
                cvLine(&img, *pt[1], *pt[2], cvScalar(0, 255, 0), 4);
                cvLine(&img, *pt[2], *pt[3], cvScalar(0, 255, 0), 4);
                cvLine(&img, *pt[3], *pt[0], cvScalar(0, 255, 0), 4);

                fCrash = 12 + 100 * count;
            }

                //if there are 7  vertices  in the contour(It should be a heptagon)
            else if (result->total == 7) {
                //iterating through each point
                CvPoint *pt[7];
                for (int i = 0; i < 7; i++) {
                    pt[i] = (CvPoint *) cvGetSeqElem(result, i);
                }

                //drawing lines around the heptagon
                cvLine(&img, *pt[0], *pt[1], cvScalar(0, 0, 255), 4);
                cvLine(&img, *pt[1], *pt[2], cvScalar(0, 0, 255), 4);
                cvLine(&img, *pt[2], *pt[3], cvScalar(0, 0, 255), 4);
                cvLine(&img, *pt[3], *pt[4], cvScalar(0, 0, 255), 4);
                cvLine(&img, *pt[4], *pt[5], cvScalar(0, 0, 255), 4);
                cvLine(&img, *pt[5], *pt[6], cvScalar(0, 0, 255), 4);
                cvLine(&img, *pt[6], *pt[0], cvScalar(0, 0, 255), 4);

                fCrash = 13 + 100 * count;
            }

            //obtain the next contour
            contours = contours->h_next;
            count++;
        }


        fCrash = 20;
        // apply hough circles to find circles and draw a cirlce around it
        cvCanny(&mGr, imgCanny, 0, 0, 3);
        CvSeq *mycircles = cvHoughCircles(imgCanny,
                                        storage,
                                        CV_HOUGH_GRADIENT,
                                        2,
                                        imgCanny->rows / 4,//height/4,//WUT?!//[RIV]
                                        200,
                                        100);

        fCrash = 21;
        for (int i = 0; i < mycircles->total; i++)
        {
            float *p = (float *) cvGetSeqElem(mycircles, i);

            cvCircle(&img, cvPoint(cvRound(p[0]), cvRound(p[1])),
                     3, CV_RGB(0, 255, 0), -1, 8, 0);

            cvCircle(&img, cvPoint(cvRound(p[0]), cvRound(p[1])),
                     cvRound(p[2]), CV_RGB(0, 255, 255), 3, 8, 0);

            fCrash = 22 + 1000 * i;

        }

        //cleaning up
        cvReleaseMemStorage(&storage);
        //cvReleaseImage(&imgGrayScale);//WUT?!
        //addrGray->release();

        //return count;

        return 0;

        /*
        // apply hough circles to find circles and draw a cirlce around it
        cvCanny(&mGr, imgCanny, 0, 0, 3);
        CvSeq *circles = cvHoughCircles(imgCanny,
                                        storage,
                                        CV_HOUGH_GRADIENT,
                                        2,
                                        imgCanny->rows / 4,//height/4,//WUT?!//[RIV]
                                        200,
                                        100);

        for (int i = 0; i < circles->total; i++) {
            float *p = (float *) cvGetSeqElem(circles, i);
            cvCircle(&img, cvPoint(cvRound(p[0]), cvRound(p[1])),
                     3, CV_RGB(0, 255, 0), -1, 8, 0);
            cvCircle(&img, cvPoint(cvRound(p[0]), cvRound(p[1])),
                     cvRound(p[2]), CV_RGB(0, 255, 255), 3, 8, 0);
        }

        //cleaning up
        cvReleaseMemStorage(&storage);
        //cvReleaseImage(&imgGrayScale);//WUT?!
        //addrGray->release();

        return count;
         */
    }
    catch(...)
    {
        return fCrash;
    }
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
