#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include <android/log.h>

#define LOGD(TAG,...) __android_log_print(ANDROID_LOG_DEBUG  , TAG,__VA_ARGS__)

//ho definito le mie forme
#define MY_NULL             0
#define MY_TRIANGLE         1
#define MY_SQUARE           2
#define MY_RECTANGLE        3
#define MY_PENTAGON         4
#define MY_CIRCLE           5
#define MY_FORMS            6


using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT jfloat JNICALL Java_com_lego_minddroid_CameraActivity_Function(JNIEnv*, jobject, jlong addrGray, jlong addrRgba);

JNIEXPORT jfloat JNICALL Java_com_lego_minddroid_CameraActivity_Function(JNIEnv*, jobject, jlong addrGray, jlong addrRgba) {

    //variabile per riconoscere il punto in cui il programma si schianta
    float fCrash = 0;
    //forma geometrica da ritornare
    float form = MY_NULL;
    //contatori delle forme osservate
    int forms[MY_FORMS] = {0};

    //blocco try per rilevare possibili eccezioni
    try
    {
        //matrici del frame da analizzare
        Mat &mGr = *(Mat *) addrGray;
        Mat &img = *(Mat *) addrRgba;

        /////////////////////
        fCrash = 1;
        /////////////////////

        //Filtro Gaussiano
        GaussianBlur(img,img,Size(5,5),1.4,1.4);//1.4
        //riconoscimento dei bordi
        Canny(img, mGr, 75, 175, 3, true);

        /////////////////////
        fCrash = 2;
        /////////////////////

        //soglia
        threshold(mGr, mGr, 128, 255, CV_THRESH_BINARY);

        //vettore di insiemi di punti (ogni insieme definisce una forma come triangolo, quadrato, ecc...)
        vector<vector<Point> > contours;
        //vettore di punti in cui salvare i punti che riassumono la figura geometrica (3 -> triangolo, 4 -> quadrilatero, ecc...)
        vector<Point> result;

        /////////////////////
        fCrash = 3;
        /////////////////////

        //si rilevano i contorni dell'immagine e li si salva in contours
        findContours(mGr, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

        /////////////////////
        fCrash = 4;
        /////////////////////

        //contatore per aiutare fCrash
        int count = 0;

        //si eseguono un numero di iterazioni pari al numero di contorni rilevati
        for(int idx = 0;idx < contours.size();idx++) {

            //si identificano i punti essenziali a definire la forma geometrica del contorno che si sta osservando
            approxPolyDP(Mat(contours[idx]), result, arcLength(Mat(contours[idx]), true) * 0.02, true);

            /////////////////////
            fCrash = 10 + 100 * count;
            /////////////////////

            //se i punti essenziali sono 3 abbiamo a che fare con un triangolo
            if (result.size() == 3)
            {

                //misuro i tre lati del triangolo (niente radice quadrata per evitare costi eccessivi)
                double dist1 = (result[0].x-result[1].x)*(result[0].x-result[1].x) + (result[0].y-result[1].y)*(result[0].y-result[1].y);
                double dist2 = (result[1].x-result[2].x)*(result[1].x-result[2].x) + (result[1].y-result[2].y)*(result[1].y-result[2].y);
                double dist3 = (result[2].x-result[0].x)*(result[2].x-result[0].x) + (result[2].y-result[0].y)*(result[2].y-result[0].y);

                //setto i valori che definiscono il range di ammissibilità dei triangoli
                double param1 = 0.49;//<--0.7^2
                double param2 = 1.69;//<--1.3^2
                double distMin = 200;//lunghezza minima del singolo lato
                double distSumMin = 1000;//lunghezza minima del perimetro

                //filtro i triangoli aventi anche un solo lato troppo stretto così evito il glitch a spillo
                if(
                    /**/
                    ((param1*dist2 < dist1 && dist1 < param2*dist2) || (param1*dist1 < dist2 && dist2 < param2*dist1)) &&
                    ((param1*dist3 < dist2 && dist2 < param2*dist3) || (param1*dist2 < dist3 && dist3 < param2*dist2)) &&
                    ((param1*dist1 < dist3 && dist3 < param2*dist1) || (param1*dist3 < dist1 && dist1 < param2*dist3)) &&
                    (dist1 + dist2 + dist3 > distSumMin) && (dist1 > distMin && dist2 > distMin && dist3 > distMin)
                    /**/
                  )
                {
                    //disegno i lati del triangolo a schermo
                    line(img, result[0], result[1], cvScalar(255, 0, 0), 4);
                    line(img, result[1], result[2], cvScalar(255, 0, 0), 4);
                    line(img, result[2], result[0], cvScalar(255, 0, 0), 4);

                    //è un triangolo
                    forms[MY_TRIANGLE]++;
                }

                /////////////////////
                fCrash = 11 + 100 * count;
                /////////////////////
            }

            //se i punti essenziali sono 4 abbiamo a che fare con un quadrilatero
            else if (result.size() == 4)
            {
                //misuro i lati (1-4) e le diagonali (5-6)
                double dist1 = (result[0].x-result[1].x)*(result[0].x-result[1].x) + (result[0].y-result[1].y)*(result[0].y-result[1].y);
                double dist2 = (result[1].x-result[2].x)*(result[1].x-result[2].x) + (result[1].y-result[2].y)*(result[1].y-result[2].y);
                double dist3 = (result[2].x-result[3].x)*(result[2].x-result[3].x) + (result[2].y-result[3].y)*(result[2].y-result[3].y);
                double dist4 = (result[3].x-result[0].x)*(result[3].x-result[0].x) + (result[3].y-result[0].y)*(result[3].y-result[0].y);
                double dist5 = (result[0].x-result[2].x)*(result[0].x-result[2].x) + (result[0].y-result[2].y)*(result[0].y-result[2].y);
                double dist6 = (result[1].x-result[3].x)*(result[1].x-result[3].x) + (result[1].y-result[3].y)*(result[1].y-result[3].y);

                //paramentri per definire i range di ammissibilità
                double param1 = 0.7;
                double param2 = 1.3;
                double distMin = 400;
                double distSumMin = 3000;

                //se le diagonali sono simili e le dimensioni sono accettabili
                if(
                    /**/
                    (param1*dist5 < dist6 && dist6 < param2*dist5 || param1*dist6 < dist5 && dist5 < param2*dist6) &&
                    (dist1 + dist2 + dist3 + dist4 > distSumMin) && (dist1 > distMin && dist2 > distMin && dist3 > distMin && dist4 > distMin)
                    /**/
                        )
                {
                    //disegno la figura, che sia un rettangolo o quadrato
                    line(img, result[0], result[1], cvScalar(255, 0, 0), 4);
                    line(img, result[1], result[2], cvScalar(255, 0, 0), 4);
                    line(img, result[2], result[3], cvScalar(255, 0, 0), 4);
                    line(img, result[3], result[0], cvScalar(255, 0, 0), 4);

                    //se la figura ha i lati uguali (simili)
                    if(
                        /**/
                        (param1*dist2 < dist1 && dist1 < param2*dist2 || param1*dist1 < dist2 && dist2 < param2*dist1) &&
                        (param1*dist3 < dist2 && dist2 < param2*dist3 || param1*dist2 < dist3 && dist3 < param2*dist2) &&
                        (param1*dist4 < dist3 && dist3 < param2*dist4 || param1*dist3 < dist4 && dist4 < param2*dist3) &&
                        (param1*dist1 < dist4 && dist4 < param2*dist1 || param1*dist4 < dist1 && dist1 < param2*dist4)
                        /**/
                            )
                    {
                        //è un quadrato
                        forms[MY_SQUARE]++;
                    }
                    else
                    {
                        //è un rettangolo
                        forms[MY_RECTANGLE]++;
                    }
                }

                /////////////////////
                fCrash = 12 + 100 * count;
                /////////////////////
            }

            //se i punti essenziali sono 5 abbiamo a che fare con un pentagono
            else if(result.size() == 5)
            {
                //misuro i lati (1-5)
                double dist1 = (result[0].x-result[1].x)*(result[0].x-result[1].x) + (result[0].y-result[1].y)*(result[0].y-result[1].y);
                double dist2 = (result[1].x-result[2].x)*(result[1].x-result[2].x) + (result[1].y-result[2].y)*(result[1].y-result[2].y);
                double dist3 = (result[2].x-result[3].x)*(result[2].x-result[3].x) + (result[2].y-result[3].y)*(result[2].y-result[3].y);
                double dist4 = (result[3].x-result[4].x)*(result[3].x-result[4].x) + (result[3].y-result[4].y)*(result[3].y-result[4].y);
                double dist5 = (result[4].x-result[0].x)*(result[4].x-result[0].x) + (result[4].y-result[0].y)*(result[4].y-result[0].y);

                //misuro le diagonali (6-8) [ne misuro 3 nella speranza che siano sufficienti]
                double dist6 = (result[0].x-result[3].x)*(result[0].x-result[3].x) + (result[0].y-result[3].y)*(result[0].y-result[3].y);
                double dist7 = (result[0].x-result[2].x)*(result[0].x-result[2].x) + (result[0].y-result[2].y)*(result[0].y-result[2].y);
                double dist8 = (result[4].x-result[1].x)*(result[4].x-result[1].x) + (result[4].y-result[1].y)*(result[4].y-result[1].y);

                //paramentri per definire i range di ammissibilità
                double param1 = 0.7;
                double param2 = 1.3;
                double distMin = 300;
                double distSumMin = 3500;

                //filtro i pentagoni per dimensioni in modo da evitare forme geometriche indesiderate
                if(
                    /**/
                    //lati
                    (param1*dist2 < dist1 && dist1 < param2*dist2 || param1*dist1 < dist2 && dist2 < param2*dist1) &&
                    (param1*dist3 < dist2 && dist2 < param2*dist3 || param1*dist2 < dist3 && dist3 < param2*dist2) &&
                    (param1*dist4 < dist3 && dist3 < param2*dist4 || param1*dist3 < dist4 && dist4 < param2*dist3) &&
                    (param1*dist5 < dist4 && dist4 < param2*dist5 || param1*dist4 < dist5 && dist5 < param2*dist4) &&
                    (param1*dist1 < dist5 && dist5 < param2*dist1 || param1*dist5 < dist1 && dist1 < param2*dist5) &&
                    //diagonali
                    (param1*dist6 < dist7 && dist7 < param2*dist6 || param1*dist7 < dist6 && dist6 < param2*dist7) &&
                    (param1*dist8 < dist6 && dist6 < param2*dist8 || param1*dist6 < dist8 && dist8 < param2*dist6) &&
                    (param1*dist7 < dist8 && dist8 < param2*dist7 || param1*dist8 < dist7 && dist7 < param2*dist8) &&
                    //perimetro
                    (dist1 + dist2 + dist3 + dist4 + dist5 > distSumMin) &&
                    //singoli lati
                    (dist1 > distMin && dist2 > distMin && dist3 > distMin && dist4 > distMin && dist5 > distMin)
                    /**/
                        )
                {
                    //disegno il pentagono
                    line(img, result[0], result[1], cvScalar(255, 0, 0), 4);
                    line(img, result[1], result[2], cvScalar(255, 0, 0), 4);
                    line(img, result[2], result[3], cvScalar(255, 0, 0), 4);
                    line(img, result[3], result[4], cvScalar(255, 0, 0), 4);
                    line(img, result[4], result[0], cvScalar(255, 0, 0), 4);

                    //è un pentagono
                    forms[MY_PENTAGON]++;
                }
            }

            //incrementiamo il contatore
            count++;
        }

        /*/
        //riconoscimento cerchi
        vector<Vec3f> circles;
        //
        HoughCircles(mGr, circles, CV_HOUGH_GRADIENT, 2, 50.0, 30, 150, 70, 140);
        //
        forms[MY_CIRCLE] += circles.size();

        /// Disegno i cerchi
        for( size_t i = 0; i < circles.size(); i++ )
        {
            //
            Point center(cvRound(circles[i][0]), cvRound(circles[i][1]));
            //
            int radius = cvRound(circles[i][2]);
            // circle outline
            circle( img, center, radius, Scalar(255, 0, 0, 255), 3, 8, 0 );
        }
        /**/

        /////////////////////
        fCrash = 20;
        /////////////////////

        //ritorno il valore associato alla forma geometrica più osservata
        int max = 0;

        for(int i=0;i<MY_FORMS;i++)
        {
            if(forms[i] > max)
            {
                max = forms[i];
                form = i;
            }
        }

        if(max == 0)
        {
            form = MY_NULL;
        }

        return form;
    }
    catch(...)
    {
        //per facilitare il riconoscimento del punto in cui il programma lancia un'eccezione
        fCrash *= -1;//il -1 serve a riconoscere che è associato ad un' eccezione e non ad una forma geometrica
        return fCrash;
    }
}//FINE definizione funzione

}//FINE extern "C"
