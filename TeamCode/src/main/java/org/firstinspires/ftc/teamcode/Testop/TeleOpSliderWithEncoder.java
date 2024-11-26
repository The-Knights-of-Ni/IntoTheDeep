package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class TeleOpSliderWithEncoder extends LinearOpMode {
    DcMotor sl;         // slider left
    DcMotor sr;         // slider right
    double motorTicks = 537.7;    // 312 RPM - SKU 5202-0002-0071.  145.1 for 1150 RPM
    public enum Bucket {
        BOTTOM(0.0, 0.0),
        LOW(2.5, -2.5),
        HIGH(4.5, -4.5);

        private double leftTurnage;
        private double rightTurnage;

        // Constructor for the enum
        Bucket(Double leftTurnage, Double rightTurnage) {
            this.leftTurnage = leftTurnage;
            this.rightTurnage = rightTurnage;
        }

        public double getLeftTurnage() {
            return leftTurnage;
        }

        public double getRightTurnage() {
            return rightTurnage;
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // Get motors
        sl = hardwareMap.dcMotor.get("sl");
        sr = hardwareMap.dcMotor.get("sr");
        telemetry.addData(">", "Starting slider with encoder..." );
        telemetry.update();

        // Set motor's direction for left and right slider
        sl.setDirection(DcMotorSimple.Direction.REVERSE);
        sr.setDirection(DcMotorSimple.Direction.REVERSE);

        // Use encoder
        sl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Boolean checker variables to determine moving logic.
        boolean buttona = true;
        boolean buttonb = true;
        /*
        Work In Progress
        boolean returnfromlow = false;
        boolean returnfromhigh = true;
        */

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            // if (gamepad1.left_stick_y != 0) {
            // double y = -gamepad1.left_stick_y * 0.5;
            // sl.setPower(y);
            // sr.setPower(y);
            // }

            telemetry.addData("start: ", "encoder test");
            telemetry.update();

            if (gamepad1.y) {
                moveSlidersWithEncoder(sl, sr, Bucket.HIGH);
            }
            if (gamepad1.a) {
                resetMotor();
            }
//            if (gamepad1.x) {
//                telemetry.addData("Left Current Position: ", sl.getTargetPosition());
//                telemetry.addData("Right Current Position: ", sr.getTargetPosition());
//                telemetry.update();
//            }

//             if (gamepad1.a && buttonb) {
//                 runWithEncoder(2.5);          // 1 = 1 rotation, lower bucket = 2.5 turnage, go to 3 if need to go above bucket
//                 buttona = false;
//                 //returnfromlow = true;
// //                runWithEncoder(0.5);
//             }
//             else if (gamepad1.a && buttona) {
//                 runWithEncoder(-4);
//                 buttona = true;
//             }

//             if (gamepad1.b && buttona) {
//                 runWithEncoder(6.5);          // 1 = 1 rotation, higher bucket = 6.5 turnage, go to 7 if need to go above bucket
//                 buttonb = false;
// //                runWithEncoder(0.5);
//             }
//             else if (gamepad1.b && buttonb){
//                 runWithEncoder(4);
//                 buttonb = true;
//                 //returnfromhigh = true;
//             }
                /*
                Work In Progress
                if (gamepad1.x && returnfromlow) {
                    runWithEncoder(-2.5);
                }
                else if (gamepad1.x && returnfromhigh){
                    runWithEncoder(-6.5);
                }
                else{
                    resetMotor();
                }
                */
        }
    }

    public void moveSlidersWithEncoder(DcMotor sl, DcMotor sr, Bucket b) {
        sl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        System.out.println("Left Current Position: " + sl.getCurrentPosition());
        System.out.println("Right Current Position: " + sr.getCurrentPosition());

        double newTargetLeft;
        double newTargetRight;
        System.out.println("Distance:"+ b.getLeftTurnage());
        System.out.println("Distance:"+ b.getRightTurnage());

        newTargetLeft = motorTicks * b.getLeftTurnage();
        newTargetRight = motorTicks * b.getRightTurnage();
        System.out.println("New Target Left: " + newTargetLeft);
        System.out.println("New Target Right: " + newTargetRight);

        sl.setTargetPosition((int)newTargetLeft);
        sr.setTargetPosition((int)newTargetRight);

        System.out.println("Left Current Position (before): " + sl.getCurrentPosition());
        System.out.println("Right Current Position (before): " + sr.getCurrentPosition());

        sl.setPower(0.5);
        sr.setPower(0.5);

        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        System.out.println("Left Current Position (after): " + sl.getCurrentPosition());
        System.out.println("Right Current Position (after): " + sr.getCurrentPosition());

        sl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void resetMotor(){
        sl.setTargetPosition(0);
        sr.setTargetPosition(0);

        sl.setPower(0.5);
        sr.setPower(0.5);

        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}
