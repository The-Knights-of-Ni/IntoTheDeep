package org.firstinspires.ftc.teamcode.Control.MotionProfile;

public interface MotionProfile {
    MotionProfileOutput calculate(double time);

    boolean isFinished(double time);
}
