package org.firstinspires.ftc.teamcode.Subsystems.Control;

//Not sure if this will be needed for the pivot, but I've left the implementation in case it is
/*TODO: Determine what values UP and DOWN should be, and/or replace UP and DOWN with any other positions we need*/
/**
 * An enum to keep track of the positions we need the slide to be in regularly.
 */
public enum PivotPosition {
    UP(1),
    DOWN(0);
    public final int pos;

    PivotPosition(int pos) {
        this.pos = pos;
    }
}
