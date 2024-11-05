package org.firstinspires.ftc.teamcode.Subsystems.Control;

/**
 * Units are in degrees
 */
public enum IntakePosition {
    EXTENDED(150),
    RETRACTED(0);
    public final int pos;

    IntakePosition(int pos) {
        this.pos = pos;
    }
}
