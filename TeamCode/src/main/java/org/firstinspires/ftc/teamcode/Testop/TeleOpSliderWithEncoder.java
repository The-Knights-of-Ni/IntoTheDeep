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
    double motorTicks = 1992.6;    // 84 RPM - SKU 5202-0002-0071
    double newTarget;

    @Override
    public void runOpMode() throws InterruptedException {
        // Get motors
        sl = hardwareMap.dcMotor.get("sl");
        sr = hardwareMap.dcMotor.get("sr");
        telemetry.addData(">", "Starting slider with encoder..." );
        telemetry.update();

        // Set motor's direction for left and right slider
        sl.setDirection(DcMotorSimple.Direction.REVERSE);
        sr.setDirection(DcMotorSimple.Direction.FORWARD);

        // Use encoder
        sl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            telemetry.addData("start: ", "encoder test");
            telemetry.update();

            if (gamepad1.a){
                runWithEncoder(0.5);          // 1 = 1 rotation
//                runWithEncoder(0.5);
            }

            if (gamepad1.b){
                resetMotor();
            }
        }
    }

    public void runWithEncoder(double turnage){
        sl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        newTarget = motorTicks * turnage;

        sl.setTargetPosition((int)newTarget);
        sr.setTargetPosition((int)newTarget);

        sl.setPower(0.6);
        sr.setPower(0.6);

        List<DcMotor> motors = Arrays.asList(sl, sr);
        motors.parallelStream().forEach(motor -> {
            // Execute the command
            telemetry.addData(">", "Starting thread..." );
            telemetry.update();
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        });
//        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void resetMotor(){
        sl.setTargetPosition(0);
        sr.setTargetPosition(0);

        sl.setPower(0.2);
        sr.setPower(0.2);

        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}