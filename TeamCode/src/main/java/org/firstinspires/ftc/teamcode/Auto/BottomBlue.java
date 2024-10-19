package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.Util.Pose;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;

@Autonomous(name = "Auto Blue Bottom", group = "Auto")
public class BottomBlue extends Auto {
    @SuppressWarnings("RedundantThrows")
    public void runOpMode() throws InterruptedException {
        //robot begins to function
        initAuto(AllianceColor.BLUE);
        waitForStart();
        timer.reset();

        robot.drive.move(new Pose(-4 * mmPerInch, 26 * mmPerInch, 90));
        //TODO: turn claw, open claw
        //robot.drive.move(90);
        robot.drive.move(new Pose(24 * mmPerInch, 40 * mmPerInch, 90));
        //robot.drive.move(90);
        robot.drive.move(new Pose(0, 34 * mmPerInch, 0));
        robot.drive.move(new Pose(0, -15 * mmPerInch, 0));
    }
}