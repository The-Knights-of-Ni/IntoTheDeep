package org.firstinspires.ftc.teamcode.Subsystems.Drive.PoseEstimation;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.MotorGeneric;
import org.firstinspires.ftc.teamcode.Util.Pose;

import java.util.concurrent.TimeUnit;

public class MotorEncoders implements PoseEstimationMethod {
    // TODO: Calibrate both of these
    public static final double MILLIMETERS_PER_TICK = 0.00393700787;
    public static final double rb = 50 * MILLIMETERS_PER_TICK;
    MotorGeneric<Integer> initialMotorLocations = new MotorGeneric<>(0, 0, 0, 0);
    MotorGeneric<DcMotorEx> motors;
    Pose pose = new Pose(0, 0, 0);
    ElapsedTime timer = new ElapsedTime();
    double lastTime = 0;

    public MotorEncoders(MotorGeneric<DcMotorEx> motors) {
        this.motors = motors;
    }

    @Override
    public void start() {
        timer.reset();
        lastTime = timer.milliseconds();
        pose = new Pose(0, 0, 0);
        initialMotorLocations = new MotorGeneric<>(motors.frontLeft.getCurrentPosition(), motors.frontRight.getCurrentPosition(), motors.rearLeft.getCurrentPosition(), motors.rearRight.getCurrentPosition());
    }

    @Override
    public void update() {
        var forwardSpeed = (motors.frontLeft.getVelocity() + motors.frontRight.getVelocity() + motors.rearLeft.getVelocity() + motors.rearRight.getVelocity()) / 4;
        var strafeSpeed = (-motors.frontLeft.getVelocity() + motors.frontRight.getVelocity() + motors.rearLeft.getVelocity() - motors.rearRight.getVelocity()) / 4;
        var turnSpeed = (motors.rearRight.getVelocity() + motors.frontRight.getVelocity() - motors.frontLeft.getVelocity() - motors.rearLeft.getVelocity()) / (4 * 2 * rb);
        var now = timer.milliseconds();
        var dt = now - lastTime;
        var forwardDistance = forwardSpeed * dt;
        var strafeDistance = strafeSpeed * dt;
        var turnDistance = turnSpeed * dt;
        pose = new Pose(pose.x + forwardDistance, pose.y + strafeDistance, pose.heading + turnDistance);
        lastTime = now;
    }

    @Override
    public void stop() {
    }

    @Override
    public Pose getPose() {
        return pose;
    }
}
