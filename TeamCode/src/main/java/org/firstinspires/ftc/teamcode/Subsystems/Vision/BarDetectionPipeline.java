package org.firstinspires.ftc.teamcode.Subsystems.Vision;

import android.util.Log;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Suggestions
1. Read Vision.java first about how to turn on the camera.
2. There is no 2 right now.
3. Go to suggestion 1.
*/

/*t your q
uestions here:
1. What's ImgProc?
2. What's Rect?
3. What's contour?
4. What's hierarchy?
5. Question that should have been asked earlier: How in the world do you get stuff from android studio to make
*/

/*
Stuff I gotta do:
3 Horizontal things

*/

/* Comments/answers from the adult

1. ImgProc is a library with a lot of fancy algorithms for analyzing images. You can just use it.
   https://docs.opencv.org/4.x/d7/da8/tutorial_table_of_content_imgproc.html
   Each algorithm includes example images and the "how to use" codes

2. OpenCV library likes to use shortening, like "Mat" is 2D/3D Matrix, and "Rect" is rectangle.

3. Easier to explain with the reference image at the bottom of
   https://docs.opencv.org/4.x/df/d0d/tutorial_find_contours.html

   Note the contours can only be found if the image already represents the "edge" of the original image
   (Again see the example at the bottom)
   https://docs.opencv.org/4.x/da/d5c/tutorial_canny_detector.html

4. One contour can "enclose" others, and you can build a graph based on which covers which.
   Example:
   https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjOTvAsCk8K9AZrtUn11Bgj81_dKcBGQlaFaMGo9g09EoX9QoaALhMnYZsHlrgVtEB1r4aMAKoBqlhKaIv3QTeWElvp0rMNE2Kyf1o-K3EuRSHEq26UnlT_XVqIkVaZPaF2jiKRMsJBzpc/s1600/markers.png


*/


public class BarDetectionPipeline extends OpenCvPipeline {
    private final AllianceColor allianceColor;
    private Rect boundingBox;

    /**
     * Class instantiation
     *
     * @see Telemetry
     * @see AllianceColor
     */
    public BarDetectionPipeline(AllianceColor allianceColor) {
        this.allianceColor = allianceColor;
        this.boundingBox = null;
    }

    public Mat processFrame(Mat input) {
        Log.v("MarkerDetectionPipeline", "Processing frame of size " + input.width() + "x" + input.height());
        var mask = new Mat();
        // TODO: ensure coloring schem matches with calibrator
        Imgproc.cvtColor(input, mask, (allianceColor == AllianceColor.RED) ? Imgproc.COLOR_BGR2HSV : Imgproc.COLOR_RGB2HSV);
        // TODO: decide on proper crop
        Rect rectCrop = new Rect(0, 0, mask.width(), mask.height());
        Mat crop = new Mat(mask, rectCrop);
        mask.release();

        if (crop.empty()) {
            return input;
        }
        // TODO: Calibrate these values
        Scalar lowHSV;
        Scalar highHSV;
        if (allianceColor == AllianceColor.RED) {
            // Range for red bar, semi-accurate but detects non-bar objects
            lowHSV = new Scalar(110.0, 140.0, 100.0);
            highHSV = new Scalar(160.0, 255.0, 255.0);
        } else {
            // Default to blue
            // Blue 3D printed thingy range bc the real bar is connected to something else
            lowHSV = new Scalar(99.0, 230.0, 210.0);
            highHSV = new Scalar(110.0, 255.0, 255.0);
        }
        Mat threshImage = new Mat();

        // thres(x,y) would be true if crop(x,y) value is between low and high
        Core.inRange(crop, lowHSV, highHSV, threshImage);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(threshImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        Point[] centers = new Point[contours.size()];
        float[][] radius = new float[contours.size()][1];

        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
            centers[i] = new Point();
            Imgproc.minEnclosingCircle(contoursPoly[i], centers[i], radius[i]);
        }

        // Find the largest bounding rectangle by area
        double maxArea = 0;
        Rect maxRect = null;
        for (Rect rect : boundRect) {
            double area = rect.area();
            if (area > maxArea) {
                maxArea = area;
                maxRect = rect;
            }
        }
        boundingBox = maxRect;
        return threshImage;
    }

    public Rect getMarkerLocation() {
        return boundingBox;
    }
}
