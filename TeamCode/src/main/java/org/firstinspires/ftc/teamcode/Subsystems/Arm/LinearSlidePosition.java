package org.firstinspires.ftc.teamcode.Subsystems.Arm;

/*TODO: Determine what values the positions should be should be*/

/**
 * An enum to keep track of the positions we need the slide to be in regularly.
 */
public enum LinearSlidePosition {
    DOWN(0),
    BASKETLOW(0),
    BASKETHIGH(0),
    SUBMERSIBLELOW(0),
    SUBMERSIBLEHIGH(0);
    public final int pos;

    LinearSlidePosition(int pos) {
        this.pos = pos;
    }
}
