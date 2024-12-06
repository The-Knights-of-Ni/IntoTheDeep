package org.firstinspires.ftc.teamcode.Control.MotionProfile;

public interface MotionProfile1D {
    MotionProfileOutput1D calculate(double time);

    boolean isFinished(double time);
}
