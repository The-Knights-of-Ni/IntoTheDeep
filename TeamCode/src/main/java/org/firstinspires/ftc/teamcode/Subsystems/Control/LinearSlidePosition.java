package org.firstinspires.ftc.teamcode.Subsystems.Control;

/*TODO: Determine what values UP and DOWN should be, and/or replace UP and DOWN with any other positions we need*/
/**
 * An enum to keep track of the positions we need the slide to be in regularly.
 */
public enum LinearSlidePosition {
    UP(2222),
    DOWN(0);
    public final int pos;

    LinearSlidePosition(int pos) {
        this.pos = pos;
    }
}
