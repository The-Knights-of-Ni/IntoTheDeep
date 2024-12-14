package org.firstinspires.ftc.teamcode.Subsystems.Control;

/*TODO: Determine what values the positions should be should be*/
/**
 * An enum to keep track of the positions we need the slide to be in regularly.
 */
public enum LinearSlidePosition {
    DOWN(0),
    BASKETLOW(1),
    BASKETHIGH(2),
    SUBMERSIBLELOW(3),
    SUBMERSIBLEHIGH(4);
    public final int pos;

    LinearSlidePosition(int pos) {
        this.pos = pos;
    }
}
