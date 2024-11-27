package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Servo.Direction;

@TeleOp
public class TeleOpSliderWithEncoder extends LinearOpMode {
    DcMotor sl;         // slider left
    DcMotor sr;         // slider right
    double motorTicks = 537.7;    // 312 RPM - SKU 5202-0002-0071.  145.1 for 1150 RPM
    Servo al;
    Servo ar;
    Servo c;

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

    public enum Pivot {
        FULL(355.0),
        ZERO(0.0),
        ONE(1.0),
        SMALL(0.2),  // of 355 degree
        MEDIUM(0.4),
        LARGE(0.85);

        private double swing;

        // Constructor for the enum
        Pivot(Double swing) {
            this.swing = swing;
        }

        public double getSwing() {
            return swing;
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // Get motors
        sl = hardwareMap.dcMotor.get("sl");
        sr = hardwareMap.dcMotor.get("sr");

        // Connect to servos for arm/pivot
        al = hardwareMap.get(Servo.class, "al");
        ar = hardwareMap.get(Servo.class, "ar");
        c = hardwareMap.get(Servo.class, "c");

        // Set motor's direction for left and right slider
        sl.setDirection(DcMotorSimple.Direction.REVERSE);
        sr.setDirection(DcMotorSimple.Direction.REVERSE);

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
            if (gamepad1.left_stick_y != 0) {
                sl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                sr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                double slValue = -gamepad1.left_stick_y * 0.41; // this motor moves faster and needs to sync with sr
                double srValue = -gamepad1.left_stick_y * -0.5;

                sl.setPower(slValue);
                sl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

                sr.setPower(srValue);
                sr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }

            telemetry.addData("start: ", "encoder test");
            telemetry.update();

            if (gamepad1.y) {
                moveSlidersWithEncoder(sl, sr, Bucket.HIGH);
            }
            if (gamepad1.x) {
                moveSlidersWithEncoder(sl, sr, Bucket.LOW);
            }
            if (gamepad1.a) {
                resetMotor();
            }
            if (gamepad1.b) {   // pivot & claw
                // al.setDirection(Servo.Direction.REVERSE);
                // ar.setDirection(Servo.Direction.REVERSE);
                printCurrentPositionAndDirection(al, ar);

                wait(2000);
                setPosition(al, ar, Pivot.ZERO);
                printCurrentPositionAndDirection(al, ar);

                // wait(3000);
                // setPosition(al, ar, small);
                // printCurrentPositionAndDirection(al, ar);

                wait(2000);
                setPosition(al, ar, Pivot.LARGE);
                printCurrentPositionAndDirection(al, ar);
                wait(2000);

                wait(3000);
                setPosition(al, ar, Pivot.SMALL);
                printCurrentPositionAndDirection(al, ar);

                wait(3000);
                setPosition(al, ar, Pivot.ZERO);
                printCurrentPositionAndDirection(al, ar);

                clawOpen(c);
                wait(2000);
                clawClose(c);
            }

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
        // Use encoder
        sl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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

        sl.setPower(0.445);
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

    public void clawOpen(Servo c){
        printClawPosition(c);
        wait(1000);
        c.setPosition(0.0);
        printClawPosition(c);
    }

    public void clawClose(Servo c){
        printClawPosition(c);
        wait(1000);
        c.setPosition(0.19);
        printClawPosition(c);
    }

    public void wait(int milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e){
            System.err.println("Interrupted: " + e.getMessage());
        }
    }

    public void printClawPosition(Servo c){
        double currentPositionC;
        currentPositionC = c.getPosition();
        telemetry.addData("Current Position(C):", currentPositionC);
        telemetry.update();
    }

    public void printCurrentPositionAndDirection(Servo al, Servo ar){
        double currentPositionAl;
        double currentPositionAr;
        Direction currentDirectionAl;
        Direction currentDirectionAr;
        currentPositionAl = al.getPosition();
        currentPositionAr = ar.getPosition();
        telemetry.addData("Current Position(Al):", currentPositionAl);
        telemetry.addData("Current Position(Ar):", currentPositionAr);
        currentDirectionAl = al.getDirection();
        currentDirectionAr = ar.getDirection();
        telemetry.addData("Current Direction(Al):", currentDirectionAl.toString());
        telemetry.addData("Current Direction(Ar):", currentDirectionAr.toString());
        telemetry.update();
    }

    public void setDirection(Servo al, Servo ar, Direction d){
        al.setDirection(d);
        ar.setDirection(d);
    }

    public void setPosition(Servo al, Servo ar, Pivot p){
        al.setPosition(p.getSwing());
        ar.setPosition(p.getSwing());
    }
}
