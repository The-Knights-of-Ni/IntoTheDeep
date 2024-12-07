package org.firstinspires.ftc.teamcode.Subsystems.Claw;

/**
 * Units are in degrees
 */
public enum ClawPosition {
    OPEN(0.0),
    CLOSE(0.20);
    public final double pos;

    ClawPosition(double pos) {
        this.pos = pos;
    }
}
