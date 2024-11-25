package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class TeleOpSliderPID extends LinearOpMode {
    DcMotor sl;         // slider left
    DcMotor sr;         // slider right

    double motorTicks = 537.7;    // 312 RPM - SKU 5202-0002-0071.  145.1 for 1150 RPM
    double diff = 0.267;  // Scaling factor, not needed for PID but kept for compatibility
    double newTarget;

    // PID constants: TUNE THIS
    double kP = 0.1;
    double kI = 0.01;
    double kD = 0.005;

    // Variables for PID control
    double integral = 0;
    double previousError = 0;
    double maxIntegral = 500;  // Limit integral to avoid windup
    double targetPosition = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        // Get motors
        sl = hardwareMap.dcMotor.get("sl");
        sr = hardwareMap.dcMotor.get("sr");
        telemetry.addData(">", "Starting slider with encoder...");
        telemetry.update();

        // Set motor's direction for left and right slider
        sl.setDirection(DcMotorSimple.Direction.REVERSE);
        sr.setDirection(DcMotorSimple.Direction.REVERSE);

        // Use encoder
        sl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Boolean checker variables to determine moving logic.
        boolean buttona = true;
        boolean buttonb = true;

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            if (gamepad1.left_stick_y != 0) {
                double y = -gamepad1.left_stick_y * 0.5;
                sl.setPower(y);
                sr.setPower(y);
            }

            telemetry.addData("start: ", "encoder test");
            telemetry.update();

            // Control logic for button presses
            if (gamepad1.a && buttonb) {
                runWithEncoder(2.5);  // Lower bucket target position
                buttona = false;
            }
            else if (gamepad1.a && buttona) {
                runWithEncoder(-4);  // Lower bucket target position
                buttona = true;
            }

            if (gamepad1.b && buttona) {
                runWithEncoder(6.5);  // Higher bucket target position
                buttonb = false;
            }
            else if (gamepad1.b && buttonb) {
                runWithEncoder(4);   // Higher bucket target position
                buttonb = true;
            }
        }
    }

    // Method to run with PID control
    public void runWithEncoder(double turnage) {
        sl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Calculate the target position in encoder ticks
        newTarget = motorTicks * turnage;
        setTargetPosition(newTarget);

        // Set the motors to the target position
        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Run the motors with PID control
        while (opModeIsActive() && Math.abs(sl.getCurrentPosition()) < Math.abs(newTarget)) {
            double slPosition = sl.getCurrentPosition();
            double srPosition = sr.getCurrentPosition();

            // Calculate PID output for each motor
            double slPower = calculatePID(slPosition);
            double srPower = calculatePID(srPosition);

            // Set the motor powers
            sl.setPower(slPower);
            sr.setPower(srPower);

            // Update telemetry
            telemetry.addData("Left Motor Position: ", sl.getCurrentPosition());
            telemetry.addData("Right Motor Position: ", sr.getCurrentPosition());
            telemetry.addData("PID Power (Left): ", slPower);
            telemetry.addData("PID Power (Right): ", srPower);
            telemetry.update();
        }

        // Stop motors once target is reached
        sl.setPower(0);
        sr.setPower(0);
    }

    // Set target position for the motors
    public void setTargetPosition(double position) {
        targetPosition = position;
        sl.setTargetPosition((int) position);
        sr.setTargetPosition((int) position);
    }

    // Calculate PID control output for a motor
    public double calculatePID(double currentPosition) {
        // Calculate error
        double error = targetPosition - currentPosition;

        // Calculate integral (accumulated error)
        integral += error;

        // Limit integral to avoid windup
        if (Math.abs(integral) > maxIntegral) {
            integral = Math.signum(integral) * maxIntegral;
        }

        // Calculate derivative (rate of change of error)
        double derivative = error - previousError;

        // Calculate PID output
        double output = kP * error + kI * integral + kD * derivative;

        // Save the current error for the next loop
        previousError = error;

        return output;
    }

    // Reset motor positions (for testing purposes)
    public void resetMotor() {
        sl.setTargetPosition(0);
        sl.setPower(1);
        sl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}
