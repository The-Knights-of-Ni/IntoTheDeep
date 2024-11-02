package org.firstinspires.ftc.teamcode.Subsystems.Vision;
import static java.lang.Math.max;

import android.util.Log;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;
import org.opencv.core.*;
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
    private MarkerLocation markerLocation = MarkerLocation.NOT_FOUND;
    private int markerLeftDetected = 0;
    private int markerMiddleDetected = 0;
    private int markerRightDetected = 0;

    /**
     * Class instantiation
     *
     * @see Telemetry
     * @see AllianceColor
     */
    public BarDetectionPipeline(AllianceColor allianceColor) {
        this.allianceColor = allianceColor;
    }

    /**
     * This method detects where the marker is.
     *
     * <p>It does this by splitting the camera input into left, right, and middle rectangles, these
     * rectangles need to be calibrated. Combined, they do not have to encompass the whole camera
     * input, they probably will only check a small part of it. We then assume the alliance color is
     * either (255, 0, 0) or (0, 0, 255), we get the info when the object is instantiated ({@link
     * #allianceColor}), and that the marker color is (0, 255, 0), which is a bright green ({@link
     * Scalar}'s are used for colors). We compare the marker color with the alliance color on each of
     * the rectangles, if the marker color is on none or multiple of them, it is marked as {@link
     * MarkerLocation#NOT_FOUND}, if otherwise, the respective Location it is in is returned via a
     * {@link MarkerLocation} variable called {@link #markerLocation}
     *
     * @param input A Mask (the class is called {@link Mat})
     * @return The marker location
     * @see BarDetectionPipeline#allianceColor
     * @see Mat
     * @see Scalar
     * @see MarkerLocation
     */
    public Mat processFrame(Mat input) {
        Log.v("MarkerDetectionPipeline", "Processing frame of size " + input.width() + "x" + input.height());
        var oldMarkerLocation = markerLocation;
        if (input == null) {
            return null;
        }
        Mat mask = new Mat();
        Imgproc.cvtColor(input, mask, (allianceColor == AllianceColor.RED) ? Imgproc.COLOR_BGR2HSV : Imgproc.COLOR_RGB2HSV);

        Rect rectCrop = new Rect(0, 0, mask.width(), mask.height());
        Mat crop = new Mat(mask, rectCrop);
        mask.release();

        if (crop.empty()) {
            markerLocation = MarkerLocation.NOT_FOUND;
            return input;
        }
        Scalar lowHSV;
        Scalar highHSV;
        if (allianceColor == AllianceColor.RED) {
            lowHSV = new Scalar(0.0, 0.0, 0.0);
            highHSV = new Scalar(255.0, 255.0, 255.0);
        } else {
            // Default to blue
            lowHSV = new Scalar(0.0, 0.0, 0.0);
            highHSV = new Scalar(255.0, 255.0, 255.0);
        }
        Mat thresh = new Mat(); // Passed in by reference and is teh result just poorly named

        Core.inRange(crop, lowHSV, highHSV, thresh);

        Mat edges = new Mat();
        Imgproc.Canny(thresh, edges, 100, 300);
//        thresh.release();
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];

        for (int i = 0; i < contours.size(); i++) {
            if (contours.get(i).empty()) {
                Log.w("MarkerDetectionPipeline", "Empty contour");
            } else if (contours.get(i) == null) {
                Log.w("MarkerDetectionPipeline", "Null contour");
            } else {
                MatOfPoint2f tempContours = new MatOfPoint2f(contours.get(i).toArray());
                // IMPORTANT: MatOfPoint2f will prob leak memory, may want to fix
                contoursPoly[i] = new MatOfPoint2f();
                Imgproc.approxPolyDP(tempContours, contoursPoly[i], 3, true);
                MatOfPoint rectContours = new MatOfPoint(contoursPoly[i].toArray());
                boundRect[i] = Imgproc.boundingRect(rectContours);
//            Imgproc.contourArea(contoursPoly[i]); // TODO Maybe implement contour area check for next tourney
                tempContours.release();
                rectContours.release();
            }
        }


        System.out.println(Arrays.toString(boundRect));

        double left_x = 0.3 * crop.width();
        double right_x = 0.7 * crop.width();
        var largest_area = 0.0;
        double right_area = 0.0;
        double middle_area = 0.0;
        double left_area = 0.0;

        // Adult: Guess what this for loop is doing to the bounding box??
        for (int i = 0; i != boundRect.length; i++) {
            if (boundRect[i] != null) {
                double area = boundRect[i].area();
                int midpoint = boundRect[i].width + boundRect[i].x / 2;
                System.out.println(midpoint);
                if (midpoint < left_x) {
                    left_area += area;
                }
                else if (left_x <= midpoint && midpoint <= right_x) {
                    middle_area += area;
                }
                else if (right_x < midpoint) {
                    right_area += area;
                }
            }
        }
//        largest_area = max(largest_area,max(max(right_area,left_area),middle_area));
        if (right_area > largest_area) {
             largest_area = right_area;
            markerLocation = MarkerLocation.RIGHT;
        }
        if (middle_area > largest_area) {
             largest_area = middle_area;
            markerLocation = MarkerLocation.MIDDLE;
        }
        if (left_area > largest_area) {
             largest_area = left_area;
            markerLocation = MarkerLocation.LEFT;
        }

        switch (markerLocation) {
            case LEFT:
                markerLeftDetected++;
                break;
            case MIDDLE:
                markerMiddleDetected++;
                break;
            case RIGHT:
                markerRightDetected++;
                break;
            default:
                break;
        }

        // Adult: Clean up.
        for(int i = 0; i<contours.size(); i++){ contours.get(i).release(); contoursPoly[i].release(); }

        hierarchy.release();
        edges.release();
        crop.release();

        if (oldMarkerLocation != markerLocation) {
            Log.i("MarkerDetectionPipeline", "Marker Location: " + markerLocation.name());
        }

        return thresh;
    }

    /**
     * Gets the Marker Location, might be not found because of the Search Status.
     *
     * @return Where the marker is.
     * @see MarkerLocation
     *
     */
    public MarkerLocation getMarkerLocation() {
        MarkerLocation mostDetected = MarkerLocation.NOT_FOUND;
        int mostDetectedCount = 0;
        if (markerLeftDetected > mostDetectedCount) {
            mostDetectedCount = markerLeftDetected;
            mostDetected = MarkerLocation.LEFT;
        }
        if (markerMiddleDetected > mostDetectedCount) {
            mostDetectedCount = markerMiddleDetected;
            mostDetected = MarkerLocation.MIDDLE;
        }
        if (markerRightDetected > mostDetectedCount) {
            mostDetectedCount = markerRightDetected;
            mostDetected = MarkerLocation.RIGHT;
        }
        return mostDetected;
    }

    public enum MarkerLocation {
        LEFT, MIDDLE, RIGHT, NOT_FOUND
    }
}
