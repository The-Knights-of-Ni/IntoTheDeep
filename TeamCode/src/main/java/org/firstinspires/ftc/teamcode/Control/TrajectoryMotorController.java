package org.firstinspires.ftc.teamcode.Control;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Control.MotionProfile.TrapezoidalMotionProfile1D;

public class TrajectoryMotorController {
    PIDVA controller;
    DcMotorEx motor;

    public TrajectoryMotorController(DcMotorEx motor, PIDCoefficients<Double> pidCoefficients, VACoefficients<Double> vaCoefficients) {
        this.motor = motor;
        this.motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        controller = new PIDVA(pidCoefficients, vaCoefficients);
    }

    public void move(int position) {
        var time = new ElapsedTime();
        var current = motor.getCurrentPosition();
        var trajectory = new TrapezoidalMotionProfile1D(1.0, 4.0, position - current);
        while (Math.abs(position - motor.getCurrentPosition()) > 25) {
            var dt = time.seconds();
            var output = trajectory.calculate(dt);
            var power = controller.calculate(output.position, motor.getCurrentPosition(), output.velocity, output.acceleration);
            motor.setPower(power);
        }
    }
}
