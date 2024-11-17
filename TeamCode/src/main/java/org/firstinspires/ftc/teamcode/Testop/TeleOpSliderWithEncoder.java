package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.*;
@TeleOp
public class TeleOpSliderWithEncoder extends LinearOpMode {
    DcMotor sl;         // slider left
    DcMotor sr;         // slider right
    double motorTicks = 537.7;    // 84 RPM - SKU 5202-0002-0071
    double newTarget;

    @Override
    public void runOpMode() throws InterruptedException {
        // Get motors
        sl = hardwareMap.dcMotor.get("sl");
//        sr = hardwareMap.dcMotor.get("sr");
        telemetry.addData(">", "Starting slider with encoder..." );
        telemetry.update();

        // Set motor's direction for left and right slider
        sl.setDirection(DcMotorSimple.Direction.REVERSE);
//        sr.setDirection(DcMotorSimple.Direction.REVERSE);

        // Use encoder
        sl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        sr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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
            telemetry.addData("start: ", "encoder test");
            telemetry.update();



//            List<DcMotor> motors = Arrays.asList(sl, sr);
//            motors.parallelStream().forEach(motor -> {
//                // Execute the command
//                telemetry.addData(">", "Starting thread..." );
//                telemetry.update();
//                motor.setPower(y);
//            });
                if (gamepad1.a && buttonb) {
                    runWithEncoder(2.5);          // 1 = 1 rotation, lower bucket = 2.5 turnage, go to 3 if need to go above bucket
                    buttona = false;
                    //returnfromlow = true;
//                runWithEncoder(0.5);
                }
                else if (gamepad1.a && buttona) {
                    runWithEncoder(-4);
                    buttona = true;
                }

                if (gamepad1.b && buttona) {
                    runWithEncoder(6.5);          // 1 = 1 rotation, higher bucket = 6.5 turnage, go to 7 if need to go above bucket
                    buttonb = false;
//                runWithEncoder(0.5);
                }
                else if (gamepad1.b && buttonb){
                    runWithEncoder(4);
                    buttonb = true;
                    //returnfromhigh = true;
                }
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

    public void runWithEncoder(double turnage){
        sl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        sr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        newTarget = motorTicks * turnage;

        sl.setTargetPosition((int)newTarget);
//        sr.setTargetPosition((int)newTarget);

        sl.setPower(1);
//        sr.setPower(0.6);

//        List<DcMotor> motors = Arrays.asList(sl, sr);
//        motors.parallelStream().forEach(motor -> {
//            // Execute the command
//            telemetry.addData(">", "Starting thread..." );
//            telemetry.update();
//            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        });
        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void resetMotor(){
        sl.setTargetPosition(0);
        //sr.setTargetPosition(0);

        sl.setPower(1);
        //sr.setPower(0.2);

        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}