package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Servo.Direction;

@TeleOp
public class TeleOpPivot extends LinearOpMode {
    static final double INCREMENT = 0.01;     // amount to slew servo each CYCLE_MS cycle
    static final int CYCLE_MS = 50;     // period of each cycle
    static final double MAX_POS = 1.0;     // Maximum rotational position
    static final double MIN_POS = 0.0;     // Minimum rotational position

    // Define class members
    Servo al;
    Servo ar;
    Servo c;
    DcMotor sl;         // slider left
    DcMotor sr;         // slider right
    double motorTicks = 537.7;

    double position = (MAX_POS - MIN_POS) / 2; // Start at halfway position
    boolean rampUp = true;


    @Override
    public void runOpMode() {
        sl = hardwareMap.dcMotor.get("sl");
        sr = hardwareMap.dcMotor.get("sr");
        // Set motor's direction for left and right slider
        sl.setDirection(DcMotorSimple.Direction.REVERSE);
        sr.setDirection(DcMotorSimple.Direction.REVERSE);
        telemetry.addData(">", "Slider..." );
        telemetry.update();

        // Connect to servo (Assume Robot Left Hand)
        // Change the text in quotes to match any servo name on your robot.
        al = hardwareMap.get(Servo.class, "al");
        ar = hardwareMap.get(Servo.class, "ar");
        c = hardwareMap.get(Servo.class, "c");
        Double full = 355.0;
        Double zero = 0.0;
        Double one = 1.0;
        Double small = 0.2;  // of 355 degree
        Double medium = 0.4;
        Double large = 0.85;

        // Wait for the start button
        telemetry.addData(">", "Press Start.");
        telemetry.update();
        waitForStart();

        // Scan servo till stop pressed.
        while (opModeIsActive()) {
            sr.setPower(-gamepad1.left_stick_y * 0.7);
            sl.setPower(-gamepad1.left_stick_y * 0.7);
            sr.setPower(0);
            sl.setPower(0);


            if (gamepad1.a) {
                // al.setDirection(Servo.Direction.REVERSE);
                // ar.setDirection(Servo.Direction.REVERSE);
                printCurrentPositionAndDirection(al, ar);

                wait(3000);
                setPosition(al, ar, zero);
                printCurrentPositionAndDirection(al, ar);

                // wait(3000);
                // setPosition(al, ar, small);
                // printCurrentPositionAndDirection(al, ar);

                wait(3000);
                setPosition(al, ar, large);
                printCurrentPositionAndDirection(al, ar);
                wait(2000);

                wait(3000);
                setPosition(al, ar, small);
                printCurrentPositionAndDirection(al, ar);

                wait(3000);
                setPosition(al, ar, zero);
                printCurrentPositionAndDirection(al, ar);

                clawOpen(c);
                wait(2000);
                clawClose(c);
            }

            // al.setPosition(0.0 + gamepad1.left_stick_y * 0.5);
            // ar.setPosition(0.0 + gamepad1.left_stick_y * 0.5);
        }
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

    public void setPosition(Servo al, Servo ar, Double position){
        al.setPosition(position);
        ar.setPosition(position);
    }
}
