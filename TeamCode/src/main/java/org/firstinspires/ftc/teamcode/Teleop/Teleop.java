package org.firstinspires.ftc.teamcode.Teleop;


import static java.lang.Math.abs;

import android.os.Build;


import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Control.*;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.MotorGeneric;
import org.firstinspires.ftc.teamcode.Subsystems.Vision.BarDetectionPipeline;
import org.firstinspires.ftc.teamcode.Subsystems.Vision.Vision;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;
import org.firstinspires.ftc.teamcode.Robot;
import org.opencv.core.Rect;

import java.util.HashMap;
import java.util.List;


@TeleOp(name = "TeleOp")
public class Teleop extends LinearOpMode {
    double deltaT;
    double timeCurrent;
    double timePre;
    ElapsedTime timer;
    private Robot robot;


    private void initOpMode() {
        // Initialize DC motor objects
        timer = new ElapsedTime();
        HashMap<String, Boolean> flags = new HashMap<>();
        flags.put("web", true);
        flags.put("vision", false);
        this.robot = new Robot(hardwareMap, telemetry, timer, AllianceColor.BLUE, gamepad1, gamepad2, flags);
        timeCurrent = timer.nanoseconds();
        timePre = timeCurrent;


        telemetry.addData("Waiting for start", "...");
        telemetry.update();
    }


    /**
     * Override of runOpMode()
     *
     * <p>Please do not swallow the InterruptedException, as it is used in cases where the op mode
     * needs to be terminated early.
     *
     * @see LinearOpMode
     */
    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode();

        // Activates bulk reading, a faster way of reading data
        // IMPORTANT: Caches have to be cleared every loop
        List<LynxModule> allHubs = robot.hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        ElapsedTime timer = new ElapsedTime();
        robot.control.initDevicesTeleop();
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        telemetry.log().add("Initialized, ready to start");
        telemetry.update();
        waitForStart();

        telemetry.clearAll();
        timeCurrent = timer.nanoseconds();
        timePre = timeCurrent;

        final double sensitivityHighPower = 1.0; // multiply inputs with this on high power mode
        final double sensitivityLowPower = 0.5; // multiply inputs with this on non-high power mode
        var twoGamepads = true;
        var clawOpen = false;

        while (opModeIsActive()) {
            // Clears cache to refresh data
            for (LynxModule hub : allHubs) {
                hub.clearBulkCache();
            }

            // update data from gamepads
            robot.updateGamepads();

            // Get current time and compute delta
            timeCurrent = timer.nanoseconds();
            deltaT = timeCurrent - timePre;
            timePre = timeCurrent;

            // gets the motor powers for drive from gamepad1
            // y button activates low speed mode
            // it gets the x and y positioning from the left stick and turns based on the right stick's x
            // calcMotorPowers creates a MotorGeneric
            MotorGeneric<Double> motorPowers;
            if (Robot.gamepad1.yButton.toggle) {
                motorPowers = robot.drive.calcMotorPowers(Robot.gamepad1.leftStickX * sensitivityHighPower, Robot.gamepad1.leftStickY * sensitivityHighPower, Robot.gamepad1.rightStickX * sensitivityHighPower);
            } else {
                motorPowers = robot.drive.calcMotorPowers(Robot.gamepad1.leftStickX * sensitivityLowPower, Robot.gamepad1.leftStickY * sensitivityLowPower, Robot.gamepad1.rightStickX * sensitivityLowPower);
            }
            robot.drive.setDrivePowers(motorPowers);
            // Switch to one gamepad
            if (Robot.gamepad1.xButton.isPressed() || Robot.gamepad2.xButton.isPressed()) {
                twoGamepads = !twoGamepads;
                telemetry.log().add("Switching to " + (twoGamepads ? "two" : "one") + " gamepad mode");
            }
            if (Robot.gamepad1.bButton.toggle) {
                Rect r = robot.vision.retrunBoundingBox();
                int xdiff = 1980/2-r.x;
                /*
                * When center of bounding box is to the left of center of screen move left, same for right
                * */
                if(xdiff < -50){
                    // Move left
                    motorPowers = robot.drive.calcMotorPowers(-0.2 * sensitivityHighPower, 0 * sensitivityHighPower, 0 * sensitivityHighPower);
                }
                else if(xdiff > 50){
                    // Move right
                    motorPowers = robot.drive.calcMotorPowers(0.2 * sensitivityHighPower, 0 * sensitivityHighPower, 0 * sensitivityHighPower);
                }
            }
            if (twoGamepads) {
                // Claw open/close - controlled by clicking b
                if (Robot.gamepad1.bButton.isPressed() || Robot.gamepad2.bButton.isPressed()) {
                    if (clawOpen) {
                        robot.control.moveClaw(ClawPosition.CLOSE);
                        telemetry.log().add("Closing claw");
                    } else {
                        robot.control.moveClaw(ClawPosition.OPEN);
                        telemetry.log().add("Opening claw");
                    }
                    clawOpen = !clawOpen;
                }

                // Scoring Position (4 pre-set positions)
                if (Robot.gamepad2.dPadUp.isPressed()) {
                    robot.control.moveArm(LinearSlidePosition.BASKETLOW, PivotPosition.BASKETLOW);
                    telemetry.log().add("Moving to basket low");
                }
                if (Robot.gamepad2.dPadDown.isPressed()) {
                    robot.control.moveArm(LinearSlidePosition.BASKETHIGH, PivotPosition.BASKETHIGH);
                    telemetry.log().add("Moving to basket high");
                }
                if (Robot.gamepad2.dPadRight.isPressed()) {
                    robot.control.moveArm(LinearSlidePosition.SUBMERSIBLELOW, PivotPosition.SUBMERSIBLELOW);
                    telemetry.log().add("Moving to submersible low");
                }
                if (Robot.gamepad2.dPadLeft.isPressed()) {
                    robot.control.moveArm(LinearSlidePosition.SUBMERSIBLEHIGH, PivotPosition.SUBMERSIBLEHIGH);
                    telemetry.log().add("Moving to submersible high");
                }
                if (Robot.gamepad2.bButton.isPressed()) {
                    robot.control.moveArm(LinearSlidePosition.DOWN, PivotPosition.DOWN);
                    telemetry.log().add("Moving to down (reset position)");
                }


                // Adjust height (manual linear slide override)
                if (Robot.gamepad1.triggerLeft > 0.05) {
                    robot.control.linearSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    robot.control.linearSlide.setPower(-Robot.gamepad1.triggerLeft);
                }
                // Adjust height (manual linear slide override)
                if (Robot.gamepad1.triggerRight > 0.05) {
                    robot.control.linearSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    robot.control.linearSlide.setPower(Robot.gamepad1.triggerRight);
                }
            } else {
                // TODO: single gamepad controls
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Thread.onSpinWait();
            } else {
                //noinspection BusyWait
                Thread.sleep(5); // Ten milli sleep so that the CPU doesn't die (this also means 5 ms baseline lag)
            }
            telemetry.update();
        }
    }
}