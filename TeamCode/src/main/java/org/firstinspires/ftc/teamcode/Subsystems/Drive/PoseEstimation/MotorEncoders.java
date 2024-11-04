package org.firstinspires.ftc.teamcode.Subsystems.Drive.PoseEstimation;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.MotorGeneric;
import org.firstinspires.ftc.teamcode.Util.Pose;

import java.util.concurrent.TimeUnit;

public class MotorEncoders implements PoseEstimationMethod {
    // TODO: Calibrate both of these
    public static final double MILLIMETERS_PER_TICK = 0.00393700787;
    public static final double wheelDisplacePerEncoderCount = 0.1;
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
        var currentMotorLocations =  new MotorGeneric<>(motors.frontLeft.getCurrentPosition(), motors.frontRight.getCurrentPosition(), motors.rearLeft.getCurrentPosition(), motors.rearRight.getCurrentPosition());
        //Compute change in encoder positions
        var deltaMotorLocations = new MotorGeneric<>(currentMotorLocations.frontLeft - initialMotorLocations.frontLeft, currentMotorLocations.frontRight - initialMotorLocations.frontRight, currentMotorLocations.rearLeft - initialMotorLocations.rearLeft, currentMotorLocations.rearRight - initialMotorLocations.rearRight);
        double displ_m0 = ((double) deltaMotorLocations.frontLeft) * wheelDisplacePerEncoderCount;
        double displ_m1 = ((double) deltaMotorLocations.frontRight) * wheelDisplacePerEncoderCount;
        double displ_m2 = ((double) deltaMotorLocations.rearLeft) * wheelDisplacePerEncoderCount;
        double displ_m3 = ((double) deltaMotorLocations.rearRight) * wheelDisplacePerEncoderCount;

        //Compute the average displacement in order to untangle rotation
        //from displacment
        var forward_back = (displ_m0 + displ_m1 + displ_m2 + displ_m3) / 4.0;
        var strafe = (0 - displ_m1 - displ_m0 + displ_m2 + displ_m3) / 4.0;
        double delta_theta = (deltaMotorLocations.frontRight + deltaMotorLocations.rearRight - deltaMotorLocations.frontLeft - deltaMotorLocations.rearLeft) / (457.2);
        System.out.println(forward_back);
        System.out.println(strafe);
        System.out.println(delta_theta);

        //Move this holonomic displacement from robot to field frame of reference
        double robotTheta = delta_theta;  //Just make the accessor call once
        double delt_Xf = (forward_back * Math.cos(robotTheta) - strafe * Math.sin(robotTheta));
        double delt_Yf = (forward_back * Math.sin(robotTheta) + strafe * Math.cos(robotTheta));
        pose = new Pose(delt_Xf, delt_Yf, robotTheta);
    }

    @Override
    public void stop() {
    }

    @Override
    public Pose getPose() {
        return pose;
    }
}
