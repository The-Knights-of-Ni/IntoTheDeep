package org.firstinspires.ftc.teamcode.Auto;

import org.firstinspires.ftc.teamcode.Util.Pose;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;


//@Autonomous(name = "Auto Red Top", group = "Auto")
public class TopRedNew extends Auto {
    @SuppressWarnings("RedundantThrows")
    public void runOpMode() throws InterruptedException {
        //robot begins to function
        initAuto(AllianceColor.RED);
        waitForStart();
        timer.reset();

        robot.drive.move(new Pose(-4 * mmPerInch, 26 * mmPerInch, 0));
        //TODO: turn claw, open claw
        robot.dive.moveAngle(90);
        robot.drive.move(new Pose(24 * mmPerInch, 40 * mmPerInch, 0));
        robot.dive.moveAngle(90);
        robot.drive.move(new Pose(0, 34 * mmPerInch, 0));
        robot.drive.move(new Pose(0, -15 * mmPerInch, 0));
    }
}