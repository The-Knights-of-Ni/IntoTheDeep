package org.firstinspires.ftc.teamcode.Subsystems.Control;

/**
 * Units are in degrees
 */
public enum ClawPosition {
    OPEN(150),
    CLOSE(0);
    public final int pos;

    ClawPosition(int pos) {
        this.pos = pos;
    }
}
