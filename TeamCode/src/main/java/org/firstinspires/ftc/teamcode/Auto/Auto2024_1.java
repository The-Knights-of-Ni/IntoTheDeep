package org.firstinspires.ftc.teamcode.Auto;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

@Autonomous(name = "Auto2024_1")
public class Auto2024_1 extends LinearOpMode {
    private static final double mmPerInch = 25.4;
    private static final double MOTOR_TICK_PER_REV_YELLOW_JACKET_312 = 537.6;
    private static final double DRIVE_GEAR_REDUCTION = 1.0; // This is < 1.0 if geared UP
    private static final double GOBUILDA_MECANUM_DIAMETER_MM = 96.0;
    private static final double COUNTS_PER_MM =
            (MOTOR_TICK_PER_REV_YELLOW_JACKET_312 * DRIVE_GEAR_REDUCTION)
                    / (GOBUILDA_MECANUM_DIAMETER_MM * Math.PI);
    private static final double COUNTS_CORRECTION_X = 1.37;
    private static final double COUNTS_CORRECTION_Y = 1.0;
    private static final double COUNTS_PER_DEGREE = (double) 1180 /90;
    private static final double DRIVE_SPEED = 0.60;

    // PID Constants
    private static final double motorKp = 0.0025;
    private static final double motorKi = 0.000175;
    private static final double motorKd = 0.0003;
    // PID Controllers
    public PID flControl;
    public PID frControl;
    public PID rlControl;
    public PID rrControl;


    public ElapsedTime timer;
    DcMotor frontLeft;
    DcMotor rearLeft;
    DcMotor frontRight;
    DcMotor rearRight;
    DcMotor sl;         // slider left
    DcMotor sr;         // slider right
    double motorTicks = 537.7;    // 312 RPM - SKU 5202-0002-0071.  145.1 for 1150 RPM
    Servo al;
    Servo ar;
    Servo c;
    private long startTime;

    public enum Bucket {
        BOTTOM(0.0, 0.0),
        LOW(2.41, -2.41),    //LOW(2.41, -2.5). ==> 26" to the top
        HIGH(5.31, -5.31);   //HIGH(5.31, -5.5) ==> 39" to the top

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
        START(0.11),    // Start position
        PICKUP(0.87),   // Position to pick up sample
        LARGE1(0.7),   // Intermediate position to slow down when putting to LARGE
        CARRY(0.55),     // Position for carrying to bucket
        SUBMERGE(0.72); // Position for Submerge


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
        timer = new ElapsedTime();

        // Motors for wheels
        frontLeft = hardwareMap.dcMotor.get("fl");
        rearLeft = hardwareMap.dcMotor.get("rl");
        frontRight = hardwareMap.dcMotor.get("fr");
        rearRight = hardwareMap.dcMotor.get("rr");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRight.setDirection(DcMotorSimple.Direction.REVERSE);

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

        telemetry.addData("Starting ", "Auto:");
        telemetry.addData("Servo C ", c.toString());
        telemetry.addData("CurrentPosition C:", c.getPosition());
        telemetry.update();

        waitForStart();
        // reset the timeout time and start motion.
        timer.reset();

        // ===== SCORE PRELOADED =====
        // move arm to scoring position
        clawClose(c);
        setPosition(al, ar, Pivot.START);
        move(new Vector2D(0, 13*mmPerInch), 0); // move forward
        move(new Vector2D(0,0),-65); // turn to bucket
        move(new Vector2D(0, 7*mmPerInch), 0); // adjust/scoot towards bucket
        move(new Vector2D(4*mmPerInch, 0*mmPerInch), 0); // strafe torwards bucket
        moveSlidersWithEncoder(sl, sr, Bucket.HIGH); // move linear slide up
        Thread.sleep(3000);
        setPosition(al, ar, Pivot.CARRY); // move arm up
        Thread.sleep(2000);
        clawOpen(c);
        Thread.sleep(1000);
        clawClose(c);
        Thread.sleep(1000);
        setPosition(al, ar, Pivot.START);
        Thread.sleep(500);
        resetMotor();
        setPosition(al, ar, Pivot.START);
        move(new Vector2D(0,0),73); // turn away from bucket
        move(new Vector2D(0*mmPerInch, 25*mmPerInch), 0);
        move(new Vector2D(0,0),40); // turn to bar
        move(new Vector2D(0*mmPerInch, 20*mmPerInch), 0);
        Thread.sleep(1000);
        setPosition(al, ar, Pivot.CARRY);
        Thread.sleep(5000);
        stop();
    }

    public void move(Vector2D v, double turnAngle) {
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Vector2D newV = new Vector2D(v.getX() * COUNTS_PER_MM * COUNTS_CORRECTION_X, v.getY() * COUNTS_PER_MM * COUNTS_CORRECTION_Y);
        // Sqrt2 is introduced as a correction factor, since the pi/4 in the next line is required
        // for the strafer chassis to operate properly
        double distance = newV.distance(Vector2D.ZERO) * Math.sqrt(2);
        double angle = Math.atan2(newV.getY(), newV.getX()) - Math.PI / 4;

        int[] tickCount = new int[4]; // All tick counts need to be integers
        tickCount[0] = (int)((distance * Math.cos(angle)));
        tickCount[0] -= (int)(turnAngle * COUNTS_PER_DEGREE);
        tickCount[1] = (int)((distance * Math.sin(angle)));
        tickCount[1] += (int)(turnAngle * COUNTS_PER_DEGREE);
        tickCount[2] = (int)((distance * Math.sin(angle)));
        tickCount[2] -= (int)(turnAngle * COUNTS_PER_DEGREE);
        tickCount[3] = (int)((distance * Math.cos(angle)));
        tickCount[3] += (int)(turnAngle * COUNTS_PER_DEGREE);
        PID[] pids = {new PID(motorKp, motorKi, motorKd), new PID(motorKp, motorKi, motorKd), new PID(motorKp, motorKi, motorKd), new PID(motorKp, motorKi, motorKd)};
        allMotorControl(tickCount, pids);
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        rearLeft.setPower(0.0);
        rearRight.setPower(0.0);
    }

    public void allMotorControl(int[] tickCount, PID[] pids) {
        // Refresh motors
        stop();
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        boolean initialized = false;

        // Initialize PID controllers
        this.flControl = pids[0];
        this.frControl = pids[1];
        this.rlControl = pids[2];
        this.rrControl = pids[3];

        // Current motor encoder values
        int currentCountFL;
        int currentCountFR;
        int currentCountRL;
        int currentCountRR;

        // Previous motor encoder values
        int prevCountFL = 0;
        int prevCountFR = 0;
        int prevCountRL = 0;
        int prevCountRR = 0;

        // Conditionals to control PID loop
        boolean isMotorFLDone = false;
        boolean isMotorFRDone = false;
        boolean isMotorRLDone = false;
        boolean isMotorRRDone = false;

        // Conditionals to control timeout
        boolean isMotorFLNotMoving = false;
        boolean isMotorFRNotMoving = false;
        boolean isMotorRLNotMoving = false;
        boolean isMotorRRNotMoving = false;

        // Timeout control (stop loop if motor stalls)
        boolean isTimeOutStarted = false;
        boolean isTimeOutExceeded = false;
        double timeOutPeriod = 0.1;
        double timeOutStartedTime = 0.0;
        int timeOutThreshold = 3; // If the encoder does not change by 2 ticks, motor is "stuck"
        double currentTime = 0.0;

        while(((!isMotorFLDone) || (!isMotorFRDone) || (!isMotorRLDone) || (!isMotorRRDone)) && (!isTimeOutExceeded)) {
            // Update current variables
            currentTime = ((double) timer.nanoseconds()) * 1.0e-9 - startTime;
            currentCountFL = frontLeft.getCurrentPosition();
            currentCountFR = (int) (frontRight.getCurrentPosition() / 0.7); // FR is always off, not sure why
            currentCountRL = rearLeft.getCurrentPosition();
            currentCountRR = rearRight.getCurrentPosition();

            // PID control
            double powerFL = flControl.calculate(tickCount[0], currentCountFL);
            double powerFR = frControl.calculate(tickCount[1], currentCountFR);
            double powerRL = rlControl.calculate(tickCount[2], currentCountRL);
            double powerRR = rrControl.calculate(tickCount[3], currentCountRR);
            frontLeft.setPower(DRIVE_SPEED * powerFL);
            frontRight.setPower(DRIVE_SPEED * powerFR);
            rearLeft.setPower(DRIVE_SPEED * powerRL);
            rearRight.setPower(DRIVE_SPEED * powerRR);
            // Check for target hit
            int directionSign;
            directionSign = tickCount[0] / Math.abs(tickCount[0]);
            if (tickCount[0] == 0 || currentCountFL * directionSign >= Math.abs(tickCount[0])) {
                isMotorFLDone = true;
                isMotorFLNotMoving = true;
                frontLeft.setPower(0.0);
            }
            directionSign = tickCount[1] / Math.abs(tickCount[1]);
            if (tickCount[1] == 0 || currentCountFR * directionSign >= Math.abs(tickCount[1])) {
                isMotorFRDone = true;
                isMotorFRNotMoving = true;
                frontRight.setPower(0.0);
            }
            directionSign = tickCount[2] / Math.abs(tickCount[2]);
            if (tickCount[2] == 0 || currentCountRL * directionSign >= Math.abs(tickCount[2])) {
                isMotorRLDone = true;
                isMotorRLNotMoving = true;
                rearLeft.setPower(0.0);
            }
            directionSign = tickCount[3] / Math.abs(tickCount[3]);
            if (tickCount[3] == 0 || currentCountRR * directionSign >= Math.abs(tickCount[3])) {
                isMotorRRDone = true;
                isMotorRRNotMoving = true;
                rearRight.setPower(0.0);
            }

            // Check for timeout
            if (initialized) { // check if the motor is rotating
                isMotorFLNotMoving = Math.abs(currentCountFL - prevCountFL) < timeOutThreshold;
                isMotorFRNotMoving = Math.abs(currentCountFR - prevCountFR) < timeOutThreshold;
                isMotorRLNotMoving = Math.abs(currentCountRL - prevCountRL) < timeOutThreshold;
                isMotorRRNotMoving = Math.abs(currentCountRR - prevCountRR) < timeOutThreshold;
            }
            if (isMotorFLNotMoving && isMotorFRNotMoving && isMotorRLNotMoving && isMotorRRNotMoving) {
                if (isTimeOutStarted) {
                    if (currentTime - timeOutStartedTime > timeOutPeriod) {
                        isTimeOutExceeded = true;
                    }
                } else { // time out was not started yet
                    isTimeOutStarted = true;
                    timeOutStartedTime = currentTime;
                }
            } else {
                isTimeOutStarted = false;
                isTimeOutExceeded = false;
            }

            prevCountFL = currentCountFL;
            prevCountFR = currentCountFR;
            prevCountRL = currentCountRL;
            prevCountRR = currentCountRR;

            initialized = true;
            Log.d("Target tick", tickCount[0] + " " + tickCount[1] + " " + tickCount[2] + " " + tickCount[3]);
            Log.d("Current tick", currentCountFL + " " + currentCountFR + " " + currentCountRL + " " + currentCountRR);
            Log.d("Current power", powerFL + " " + powerFR + " " + powerRL + " " + powerRR);
        }
    }

    public void setRunMode(DcMotor.RunMode mode) {
        this.frontLeft.setMode(mode);
        this.frontRight.setMode(mode);
        this.rearLeft.setMode(mode);
        this.rearRight.setMode(mode);
    }

    public void moveSlidersWithEncoder(DcMotor sl, DcMotor sr, Bucket b) {
        // Use encoder
        sl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        double newTargetLeft;
        double newTargetRight;

        newTargetLeft = motorTicks * b.getLeftTurnage();
        newTargetRight = motorTicks * b.getRightTurnage();

        sl.setTargetPosition((int)newTargetLeft);
        sr.setTargetPosition((int)newTargetRight);

        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        sl.setPower(0.75);
        sr.setPower(0.75);

        sl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void resetMotor() {
        sl.setTargetPosition(0);
        sr.setTargetPosition(0);

        sl.setPower(0.5);
        sr.setPower(0.5);

        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void clawOpen(Servo c){
        c.setPosition(0.3);     //0.3=160degree
    }

    public void clawClose(Servo c){
        c.setPosition(0.05);     //0.09=.5" gap
    }

    public void setPosition(Servo al, Servo ar, Pivot p){
        double targetPosition = p.getSwing();
        telemetry.addData("Target Position:", targetPosition);
        telemetry.update();

        al.setPosition(targetPosition);
        ar.setPosition(targetPosition);
    }

}

