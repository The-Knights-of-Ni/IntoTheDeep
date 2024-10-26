package org.firstinspires.ftc.teamcode.Teleop;


import android.os.Build;


import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.GamepadWrapper;
import org.firstinspires.ftc.teamcode.Subsystems.Control.*;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.MotorGeneric;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;
import org.firstinspires.ftc.teamcode.Robot;

import java.util.HashMap;
import java.util.List;
import java.lang.Math;


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
        List<LynxModule> allHubs = robot.hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }


        ElapsedTime timer = new ElapsedTime();
        robot.control.initDevicesTeleop();
        waitForStart();


        telemetry.clearAll();
        timeCurrent = timer.nanoseconds();
        timePre = timeCurrent;


        final double sensitivityHighPower = 1.0; // multiply inputs with this on high power mode
        final double sensitivityLowPower = 0.5; // multiply inputs with this on non-high power mode
        var twoGamepads = true;
        double slidePowerVel;
        double cranePowerVel;


        while (opModeIsActive()) {
            //clears cache to prevent overflow
            for (LynxModule hub : allHubs) {
                hub.clearBulkCache();
            }


            //get data from gamepads
            Robot.updateGamepads();


            //get current time
            timeCurrent = timer.nanoseconds();
            deltaT = timeCurrent - timePre;
            timePre = timeCurrent;


            //gets the motor powers for drive from gamepad1
            //y button activates low speed mode
            //it gets the x and y positioning from the left stick and turns based on the right stick's x
            //calcMotorPowers creates a MotorGeneric
            if (twoGamepads) {
                MotorGeneric<Double> motorPowers;
                if (!Robot.gamepad1.yButton.toggle) {
                    motorPowers = robot.drive.calcMotorPowers(sensitivityHighPower * Robot.gamepad1.leftStickX, sensitivityHighPower * robot.gamepad1.leftStickY, sensitivityHighPower * robot.gamepad1.rightStickX);
                } else {
                    motorPowers = robot.drive.calcMotorPowers(sensitivityLowPower * Robot.gamepad1.leftStickX, sensitivityLowPower * robot.gamepad1.leftStickY, sensitivityLowPower * robot.gamepad1.rightStickX);
                }


                //gets the robot to actually move from the newly created MotorGeneric
                robot.drive.setDrivePowers(motorPowers);


                // Switch to one gamepad
                if ((Robot.gamepad1.bButton.isPressed() && Robot.gamepad1.xButton.isPressed()) || (Robot.gamepad2.bButton.isPressed() && Robot.gamepad2.xButton.isPressed())) {
                    twoGamepads = false;
                }


                //Claw open/close - controlled by second driver
                if (Robot.gamepad2.aButton.isPressed()) {
                    robot.control.openClaw();
                }
                if (Robot.gamepad2.bButton.isPressed()) {
                    robot.control.closeClaw();
                }


                /*
                //linear slide power????
                //Way for driver to override and control linear slide (in case anything goes wrong)
                slidePowerVel = Robot.gamepad2.triggerLeft - Robot.gamepad2.triggerRight;
                if (Math.abs(slidePowerVel) >= 0.15) {
                    robot.control.setSlidePower(slidePowerVel);
                } else {
                    robot.control.setSlidePower(0);
                }*/


                //Scoring Position (4 pre-set positions)
                //TODO: correct heights
                if (Robot.gamepad2.dPadUp.isPressed() || Robot.gamepad1.dPadUp.isPressed()) {
                    robot.control.moveLinearSlide(LinearSlidePosition.BASKETLOW);
                    robot.control.movePivot(PivotPosition.UP);
                }
                if (Robot.gamepad2.dPadDown.isPressed() || Robot.gamepad1.dPadDown.isPressed()) {
                    robot.control.moveLinearSlide(LinearSlidePosition.BASKETHIGH);
                    robot.control.movePivot(PivotPosition.UP);
                }
                if (Robot.gamepad2.dPadRight.isPressed() || Robot.gamepad1.dPadRight.isPressed()) {
                    robot.control.moveLinearSlide(LinearSlidePosition.SUBMERSIBLELOW);
                    robot.control.movePivot(PivotPosition.UP);
                }
                if (Robot.gamepad2.dPadLeft.isPressed() || Robot.gamepad1.dPadLeft.isPressed()) {
                    robot.control.moveLinearSlide(LinearSlidePosition.SUBMERSIBLEHIGH);
                    robot.control.movePivot(PivotPosition.UP);
                }
                if (Robot.gamepad2.bButton.isPressed() || Robot.gamepad1.bButton.isPressed()) {
                    robot.control.moveLinearSlide(LinearSlidePosition.DOWN);
                    robot.control.movePivot(PivotPosition.DOWN);
                }


                //Adjust height (manual linear slide override)
                if (robot.gamepad2.bumperRight.isPressed() || robot.gamepad1.bumperRight.isPressed()) {
                    robot.control.setSlidePower(1);
                    robot.control.setSlidePosition(robot.control.getSlidePosition() + 150);
                }
                if (robot.gamepad2.bumperLeft.isPressed() || robot.gamepad1.bumperLeft.isPressed()) {
                    robot.control.setSlidePower(1);
                    robot.control.setSlidePosition(robot.control.getSlidePosition() - 150);
                }


                //Adjust the pivot (manual pivot override)
                if (robot.gamepad2.triggerRight > 0.05 || robot.gamepad1.xButton.isPressed()) {
                    robot.control.movePivot(PivotPosition.UP);
                }
                if (robot.gamepad2.triggerLeft > 0.05 || robot.gamepad1.xButton.isPressed()) {
                    robot.control.movePivot(PivotPosition.DOWN);
                }


            } else {
                // Must use gamepad 1 for one gamepad TODO: Find elegant fix
                MotorGeneric<Double> motorPowers;
                double triggerHit = GamepadWrapper.joystickDeadzoneCorrection(Math.max(Robot.gamepad1.triggerLeft, Robot.gamepad1.triggerRight));
                if (Robot.gamepad1.yButton.toggle) {
                    motorPowers = robot.drive.calcMotorPowers(Robot.gamepad1.leftStickX * sensitivityHighPower, Robot.gamepad1.leftStickY * sensitivityHighPower, triggerHit * sensitivityHighPower);
                } else {
                    motorPowers = robot.drive.calcMotorPowers(Robot.gamepad1.leftStickX * sensitivityLowPower, Robot.gamepad1.leftStickY * sensitivityLowPower, triggerHit * sensitivityLowPower);
                }
                robot.drive.setDrivePowers(motorPowers);


                if ((Robot.gamepad1.bButton.isPressed() && Robot.gamepad1.yButton.isPressed()) || (Robot.gamepad2.bButton.isPressed() && Robot.gamepad2.yButton.isPressed())) {
                    twoGamepads = true;
                }
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Thread.onSpinWait();
            } else {
                //noinspection BusyWait
                Thread.sleep(5); // Ten milli sleep so that the CPU doesn't die (this also means 5 ms baseline lag)
            }
        }
    }
}