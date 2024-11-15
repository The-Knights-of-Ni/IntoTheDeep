package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.List;

@TeleOp
public class TeleOpSlider extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Get motors
        DcMotor sl = hardwareMap.dcMotor.get("sl");
        DcMotor sr = hardwareMap.dcMotor.get("sr");
        telemetry.addData(">", "Starting slider..." );
        telemetry.update();

        // set left slider motor and right slider motor, they need to be opposite
        sl.setDirection(DcMotorSimple.Direction.REVERSE);
        sr.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y;

            List<DcMotor> motors = Arrays.asList(sl, sr);
            motors.parallelStream().forEach(motor -> {
                // Execute the command
                telemetry.addData(">", "Starting thread..." );
                telemetry.update();
                motor.setPower(y);
            });

//            sl.setPower(y);
//            sr.setPower(y);
        }
    }
}

