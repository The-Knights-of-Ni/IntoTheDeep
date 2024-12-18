package org.firstinspires.ftc.teamcode.Subsystems.Vision;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Subsystems.Subsystem;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;
import org.firstinspires.ftc.teamcode.Util.Vector;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.List;

/**
 * The Vision Subsystem
 *
 * @see <a href="https://github.com/OpenFTC/EasyOpenCV">EasyOpenCV</a>
 */
public class Vision extends Subsystem {
    public static final int CAMERA_WIDTH = 1920; // width of wanted camera resolution
    public static final int CAMERA_HEIGHT = 1080; // height of wanted camera resolution
    public static final String WEBCAM_NAME =
            "Webcam 1"; // insert webcam name from configuration if using webcam
    // Since ImageTarget trackable use mm to specify their dimensions, we must use mm for all the
    // physical dimension.
    // Define constants
    private static final double mmPerInch = 25.4;
    // Define where camera is in relation to center of robot in inches
    final static double CAMERA_FORWARD_DISPLACEMENT = 6.0f * mmPerInch; // TODO: CALIBRATE WHEN ROBOT IS BUILT
    final static double CAMERA_VERTICAL_DISPLACEMENT = 6.5f * mmPerInch;
    final static double CAMERA_RIGHT_DISPLACEMENT = 0.0f * mmPerInch;
    private final HardwareMap hardwareMap;
    private final AllianceColor allianceColor;
    private BarDetectionPipeline pipeline;
    public AprilTagDetectionThread aprilTagDetectionThread;

    private OpenCvCamera camera;

    /**
     * Class instantiation
     *
     * @param telemetry     Telemetry
     * @param hardwareMap   the hardware map
     * @param allianceColor the alliance color
     */
    public Vision(
            Telemetry telemetry,
            HardwareMap hardwareMap,
            AllianceColor allianceColor) {
        super(telemetry, "vision");
        this.hardwareMap = hardwareMap;
        this.allianceColor = allianceColor;
//        this.aprilTagDetectionThread = new AprilTagDetectionThread(this.hardwareMap.get(WebcamName.class, WEBCAM_NAME));
//        this.aprilTagDetectionThread.start();
        // Telemetry
        logger.info("Vision init complete");
        initDetectionPipeline();
    }

    public void stopAprilTagDetection() throws InterruptedException {
        this.aprilTagDetectionThread.terminate = true;
        this.aprilTagDetectionThread.wait(2500);
    }

    public Vector getRobotPosition() {
        List<AprilTagDetection> tags = aprilTagDetectionThread.currentDetections;
        Vector position = null;
        double distance = 100000;
        for (AprilTagDetection tag : tags) {
            Vector robotPosition;
            switch (tag.id) {
                // Blue Alliance Center
                case 2:
                    robotPosition = new Vector(-36.6875 + tag.ftcPose.x - CAMERA_FORWARD_DISPLACEMENT, 61 - tag.ftcPose.y + CAMERA_RIGHT_DISPLACEMENT); // TODO: Calibrate y (use yaw etc. maybe?)
                    // Red Alliance Center
                    break;
                case 5:
                    robotPosition = new Vector(36.6875 + tag.ftcPose.x - CAMERA_FORWARD_DISPLACEMENT, 61 - tag.ftcPose.y + CAMERA_RIGHT_DISPLACEMENT);  // TODO: Calibrate y (use yaw etc. maybe?)
                    // Red pixel stack
                    break;
                case 8:
                    robotPosition = new Vector(11.5 + 24 - tag.ftcPose.x + CAMERA_FORWARD_DISPLACEMENT, -72.0 - tag.ftcPose.y - CAMERA_RIGHT_DISPLACEMENT);
                    // Blue pixel stack
                    break;
                case 9:
                    robotPosition = new Vector(-11.5 - 24 - tag.ftcPose.x + CAMERA_FORWARD_DISPLACEMENT, -72.0 - tag.ftcPose.y - CAMERA_RIGHT_DISPLACEMENT);
                    break;
                default:
                    robotPosition = null;
                    break;
            }
            double d = tag.ftcPose.range;
            if (d < distance && robotPosition != null) {
                position = robotPosition;
            }
        }
        return position;
    }

    private void initDetectionPipeline() {
        // Get the camera ID
        int cameraMonitorViewId =
                hardwareMap
                        .appContext
                        .getResources()
                        .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        // Obtain camera instance from ID
        camera =
                OpenCvCameraFactory.getInstance()
                        .createWebcam(hardwareMap.get(WebcamName.class, WEBCAM_NAME));

        // Create a detection pipeline for detecting the position
        // TODO: Create a detection pipeline for detecting the position
        pipeline = new BarDetectionPipeline(allianceColor);
        camera.setPipeline(pipeline);

        // Create listeners for the camera
        camera.openCameraDeviceAsync(
                new OpenCvCamera.AsyncCameraOpenListener() {
                    @Override
                    public void onOpened() { // Listener for when the camera first starts
                        logger.info("Streaming");
                        camera.startStreaming(CAMERA_WIDTH, CAMERA_HEIGHT, OpenCvCameraRotation.UPRIGHT);
                    }

                    @Override
                    public void onError(int errorCode) { // Listener to log if the camera stops abruptly
                        logger.error("Error Streaming, aborting with error code " + errorCode);
                    }
                });
    }

    public void stop() {
        // Stop streaming
        camera.stopStreaming();
        camera.closeCameraDevice();
    }
}
