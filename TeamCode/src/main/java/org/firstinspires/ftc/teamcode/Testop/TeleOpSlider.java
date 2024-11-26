package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class TeleOpSlider extends LinearOpMode {
    DcMotor sl;         // slider left
    DcMotor sr;         // slider right
    double motorTicks = 537.7;    // Encoder ticks per rotation (use your specific motor specifications)
    double newTarget;

    @Override
    public void runOpMode() throws InterruptedException {
        // Get motors
        DcMotor sl = hardwareMap.dcMotor.get("sl");
        DcMotor sr = hardwareMap.dcMotor.get("sr");

        // Set motor's direction for left and right slider
        sl.setDirection(DcMotorSimple.Direction.REVERSE);
        sr.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData(">", "Starting slider..." );
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double slValue = -gamepad1.left_stick_y * 0.41; // this motor moves faster and needs to sync with sr
            double srValue = -gamepad1.left_stick_y * -0.5;

            sl.setPower(slValue);
            sl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            sr.setPower(srValue);
            sr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            telemetry.addData("Left Current Position: ", sl.getCurrentPosition());
            telemetry.addData("Right Current Position: ", sr.getCurrentPosition());
            telemetry.update();
        }
    }
}
