package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class TeleOpPivot extends LinearOpMode {
    static final double INCREMENT = 0.01;     // amount to slew servo each CYCLE_MS cycle
    static final int CYCLE_MS = 50;     // period of each cycle
    static final double MAX_POS = 1.0;     // Maximum rotational position
    static final double MIN_POS = 0.0;     // Minimum rotational position

    // Define class members
    Servo p;

    double position = (MAX_POS - MIN_POS) / 2; // Start at halfway position
    boolean rampUp = true;


    @Override
    public void runOpMode() {

        // Connect to servo (Assume Robot Left Hand)
        // Change the text in quotes to match any servo name on your robot.
        p = hardwareMap.get(Servo.class, "p");

        // Wait for the start button
        telemetry.addData(">", "Press Start to scan Servo.");
        telemetry.update();
        waitForStart();


        // Scan servo till stop pressed.
        while (opModeIsActive()) {
            if (gamepad1.a) {
                p.setPosition(MAX_POS);
            }

//            // slew the servo, according to the rampUp (direction) variable.
//            if (rampUp) {
//                // Keep stepping up until we hit the max value.
//                position += INCREMENT;
//                if (position >= MAX_POS) {
//                    position = MAX_POS;
//                    rampUp = !rampUp;   // Switch ramp direction
//                }
//            } else {
//                // Keep stepping down until we hit the min value.
//                position -= INCREMENT;
//                if (position <= MIN_POS) {
//                    position = MIN_POS;
//                    rampUp = !rampUp;  // Switch ramp direction
//                }
//            }
//
//            // Display the current value
//            telemetry.addData("Servo Position", "%5.2f", position);
//            telemetry.addData(">", "Press Stop to end test.");
//            telemetry.update();
//
//            // Set the servo to the new position and pause;
////            al.setPosition(position);
////            ar.setPosition(-position);
//            sleep(CYCLE_MS);
//            idle();
//        }
//
//        // Signal done;
//        telemetry.addData(">", "Done");
//        telemetry.update();
        }
    }
}


//    static final double INCREMENT   = 0.01;     // amount to slew servo each CYCLE_MS cycle
//    static final int    CYCLE_MS    =   2000;     // period of each cycle
//    static final double MAX_POS     =  1.0;     // Maximum rotational position
//    static final double MIN_POS     =  0.0;     // Minimum rotational position
//    public static final double ARM_SPEED  = 0.02 ;                 // sets rate to move servo
//    public static final double MID_SERVO   =  0.5 ;
//
//    // Define class members
//    Servo servoArmLeft;
//    Servo servoArmRight;
//    double  position = (MAX_POS - MIN_POS) / 2; // Start at halfway position
//    boolean rampUp = true;
//    double armOffset = 0;
//
//    @Override
//    public void runOpMode() throws InterruptedException {
////        // Declare our motors
////        // Make sure your ID's match your configuration
////        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("fl");
////        DcMotor backLeftMotor = hardwareMap.dcMotor.get("rl");
////        DcMotor frontRightMotor = hardwareMap.dcMotor.get("fr");
////        DcMotor backRightMotor = hardwareMap.dcMotor.get("rr");
//
//        // Connect to servo (Assume Robot Left Hand)
//        // Change the text in quotes to match any servo name on your robot.
//        servoArmLeft = hardwareMap.get(Servo.class, "al");
//        servoArmRight = hardwareMap.get(Servo.class, "ar");
//
//        // Wait for the start button
//        telemetry.addData(">", "Starting arm servos" );
//        telemetry.update();
//
//        // Reverse the right side motors. This may be wrong for your setup.
//        // If your robot moves backwards when commanded to go forwards,
//        // reverse the left side instead.
//        // See the note about this earlier on this page.
////        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
////        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        waitForStart();
//
//        if (isStopRequested()) return;
//
//        while (opModeIsActive()) {
//
//
////            // slew the servo, according to the rampUp (direction) variable.
////            if (rampUp) {
////                // Keep stepping up until we hit the max value.
////                position += INCREMENT ;
////                if (position >= MAX_POS ) {
////                    position = MAX_POS;
////                    rampUp = !rampUp;   // Switch ramp direction
////                }
////            }
////            else {
////                // Keep stepping down until we hit the min value.
////                position -= INCREMENT ;
////                if (position <= MIN_POS ) {
////                    position = MIN_POS;
////                    rampUp = !rampUp;  // Switch ramp direction
////                }
////            }
//
//            // Display the current value
//            telemetry.addData("Servo Position", "%5.2f", position);
//            telemetry.addData(">", "Press Stop to end test." );
//            telemetry.update();
//
//            // Use gamepad y & a to move up and down arm
//            armOffset = Range.clip(armOffset, -0.5, 0.5);
//            servoArmLeft.setPosition(MID_SERVO + armOffset);
//            servoArmRight.setPosition(MID_SERVO + armOffset);
//
//            if (gamepad1.y) {
//                servoArmLeft.setPosition(-0.1);
//                servoArmRight.setPosition(-0.1);
//            }
////                armOffset += ARM_SPEED;
//            if (gamepad1.a)
//                armOffset -= ARM_SPEED;
//
//            // Set the servo to the new position and pause;
////            servoArmLeft.setPosition(position + 0.3);
////            servoArmRight.setPosition(position + 0.3);
////            sleep(CYCLE_MS);
////            servoArmLeft.setPosition(position + 0.3);
////            servoArmRight.setPosition(position + 0.3);
////            idle();
//        }
//
//        // Signal done;
//        telemetry.addData(">", "Done");
//        telemetry.update();
//    }
//}

//
///*
//package org.firstinspires.ftc.robotcontroller.external.samples;
//
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.Servo;
//
///*
// * This OpMode scans a single servo back and forward until Stop is pressed.
// * The code is structured as a LinearOpMode
// * INCREMENT sets how much to increase/decrease the servo position each cycle
// * CYCLE_MS sets the update period.
// *
// * This code assumes a Servo configured with the name "left_hand" as is found on a Robot.
// *
// * NOTE: When any servo position is set, ALL attached servos are activated, so ensure that any other
// * connected servos are able to move freely before running this test.
// *
// * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
// * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
// */
