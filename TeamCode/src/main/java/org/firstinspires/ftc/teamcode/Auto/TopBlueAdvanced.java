package org.firstinspires.ftc.teamcode.Auto;

import org.firstinspires.ftc.teamcode.Util.Pose;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;

//@Autonomous(name = "Auto Blue Top Advanced", group = "Auto")
public class TopBlueAdvanced extends Auto {
    @SuppressWarnings("RedundantThrows")
    public void runOpMode() throws InterruptedException {
        //robot begins to function
        initAuto(AllianceColor.BLUE);
        waitForStart();
        timer.reset();

        robot.drive.move(new Pose(28 * mmPerInch , 26 * mmPerInch, 0));
        //TODO: turn claw, open claw
        robot.drive.move(-90);
        robot.drive.move(new Pose(24 * mmPerInch, 40 * mmPerInch, 0));
        robot.drive.move(-90);
        robot.drive.move(new Pose(0, 36 * mmPerInch, 0));
        robot.drive.move(new Pose(14 * mmPerInch, 14 * mmPerInch, 45));
    }
}