package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Servo.Direction;

@TeleOp
public class TeleOp2024 extends LinearOpMode {
    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    DcMotor sl;         // slider left
    DcMotor sr;         // slider right
    double motorTicks = 537.7;    // 312 RPM - SKU 5202-0002-0071.  145.1 for 1150 RPM
    Servo al;
    Servo ar;
    Servo c;

    public enum Bucket {
        BOTTOM(0.0, 0.0),
        LOW(2.41, -2.5),    //LOW(2.41, -2.5). ==> 26" to the top
        HIGH(5.31, -5.5);   //HIGH(5.31, -5.5) ==> 39" to the top

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
        ZERO(0.0),      // Initial position 0
        START(0.08),    // Start position
        PICKUP(0.87),   // Position to pick up sample
        LARGE1(0.7),   // Intermediate position to slow down when putting to LARGE
        CARRY(0.5),     // Position for carrying to bucket
        SUBMERGE(0.75); // Position for Submerge


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
        // Motors for wheels
        frontLeftMotor = hardwareMap.dcMotor.get("fl");
        backLeftMotor = hardwareMap.dcMotor.get("rl");
        frontRightMotor = hardwareMap.dcMotor.get("fr");
        backRightMotor = hardwareMap.dcMotor.get("rr");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Motots for sliders
        sl = hardwareMap.dcMotor.get("sl");
        sr = hardwareMap.dcMotor.get("sr");

        // Servos for arm/pivot
        al = hardwareMap.get(Servo.class, "al");
        ar = hardwareMap.get(Servo.class, "ar");
        c = hardwareMap.get(Servo.class, "c");

        // Set slider motor's direction for left and right slider
        sl.setDirection(DcMotorSimple.Direction.REVERSE);
        sr.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            telemetry.addData("Robot ", "start:");
            telemetry.update();

            moveRobot();

            // Manually move up and down for both sliders
            if (gamepad1.left_stick_y != 0) {
                sl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                sr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                double slValue = -gamepad1.left_stick_y * 0.41; // this motor moves faster and needs to sync with sr
                double srValue = -gamepad1.left_stick_y * -0.5; // 0.41,  -0.5// 0.574, -0.7

                sl.setPower(slValue);
                sl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

                sr.setPower(srValue);
                sr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }

            // Move up both sliders to HIGH position for top bucket
            if (gamepad1.left_bumper) {
                moveSlidersWithEncoder(sl, sr, Bucket.HIGH);
            }

            // Move up both sliders to LOW position for bottom bucket
            if (gamepad1.left_trigger != 0.0) {
                moveSlidersWithEncoder(sl, sr, Bucket.LOW);
            }

            // Put back arm/pivot to initial position
            if (gamepad1.x) {
                setPosition(al, ar, Pivot.START);
            }

            // Move arm from initial position to pickup position
            if (gamepad1.b) {
                setPosition(al, ar, Pivot.PICKUP);
            }

            // Position to pick up from submersible
            if (gamepad1.a) {
                setPosition(al, ar, Pivot.SUBMERGE);
            }

            // Move arm to scoring position for buckets
            if (gamepad1.y) {
                setPosition(al, ar, Pivot.CARRY);
            }

            // Open claw
            if (gamepad1.right_trigger != 0.0) {
                clawOpen(c);
            }

            // Close claw
            if (gamepad1.right_bumper) {
                clawClose(c);
            }
        }
    }

    public void moveRobot() {
        double y = -gamepad2.left_stick_y * 0.7; // Remember, Y stick value is reversed 0.7 to slow down
        double x = gamepad2.left_stick_x * 1.1 * 0.7; // Counteract imperfect strafing
        double rx = gamepad2.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        frontLeftMotor.setPower(frontLeftPower);
        backLeftMotor.setPower(backLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backRightMotor.setPower(backRightPower);
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

    public void clawOpen(Servo c){
        printClawPosition(c);
        // wait(1000);
        c.setPosition(0.3);     //0.3=160degree
        printClawPosition(c);
    }

    public void clawClose(Servo c){
        printClawPosition(c);
        // wait(1000);
        c.setPosition(0.09);     //0.09=.5" gap
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

    // incrementally move servo to target position
    public void setPosition(Servo al, Servo ar, Pivot p){
        double targetPosition = p.getSwing();
        double currentPosition = al.getPosition();
        // double currentPositionRight = ar.getPosition();
        double increment = 0.03;

        System.out.println("Current Position:" + currentPosition);
        telemetry.addData("Current Position:", currentPosition);
        telemetry.update();
        if (currentPosition < targetPosition) {
            currentPosition += increment;
            if (currentPosition > targetPosition) {
                currentPosition = targetPosition;
            }
        } else if (currentPosition > targetPosition) {
            currentPosition -= increment;
            if (currentPosition < targetPosition) {
                currentPosition = targetPosition;
            }
        }

        al.setPosition(currentPosition);
        ar.setPosition(currentPosition);
    }
}
